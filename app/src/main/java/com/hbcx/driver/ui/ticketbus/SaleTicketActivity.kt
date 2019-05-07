package com.hbcx.driver.ui.ticketbus

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import cn.sinata.amaplib.overlay.DrivingRouteOverlay
import cn.sinata.xldutils.utils.toTime
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.hbcx.driver.R
import com.hbcx.driver.utils.request
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_sale_ticket.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dip
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class SaleTicketActivity: com.hbcx.driver.ui.TranslateStatusBarActivity(), SlidingUpPanelLayout.PanelSlideListener, RouteSearch.OnRouteSearchListener {
    private val aMap by lazy {
        mMapView.map
    }

    private val stations = arrayListOf<com.hbcx.driver.network.beans.BusStation>()

    private val adapter by lazy {
        com.hbcx.driver.adapter.BusStationAdapter(stations, true)
    }

    private val start by lazy {
        intent.getStringExtra("start")
    }

    private val end by lazy {
        intent.getStringExtra("end")
    }

    private val time by lazy {
        intent.getLongExtra("date",0)
    }

    private val id by lazy {
        intent.getIntExtra("id",0)
    }

    private var startMarker: Marker? = null //起点标记
    private var endMarker: Marker? = null //终点标记
    private var route:DrivingRouteOverlay? = null //路径

    //上下站id 第一次为-1
    private var startPointId = -1
    private var endPointId = -1

    //购票页面需要的数据
    private var money = 0.0 //单价
    private var ticketNum = 0 //余票数
    private var startIndex = 0
    private var endIndex = 0

    override fun setContentView() = R.layout.activity_sale_ticket

    private val changeStationDialog by lazy {
        com.hbcx.driver.dialogs.ChangeStationDialog()
    }

    override fun initClick() {
        iv_location.onClick {
            aMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)))
        }
        tv_action.onClick {
            if (ticketNum == 0){
                toast("该班次已售完")
                return@onClick
            }
            startActivity<ReserveTicketActivity>("start" to start, "end" to end,"startPoint" to stations[startIndex].name
                    ,"endPoint" to stations[endIndex].name,"date" to time,"startTime" to stations[startIndex].times,"id" to id,
                    "endTime" to stations[endIndex].times,"money" to money,"ticketNum" to if (ticketNum>3) 3 else ticketNum,
                    "startPointId" to stations[startIndex].id,"endPointId" to stations[endIndex].id)
        }
        tv_change_start.onClick {
            val list = stations.subList(0, endIndex).filter {
                it.type in arrayOf(1,3,4)
            }
            changeStationDialog.arguments = bundleOf("isStart" to true,"list" to list)
            changeStationDialog.setCallback{position ->
                val busStation = list[position]
                startPointId = busStation.id
                startIndex = stations.indexOf(busStation)
                getData()
            }
            changeStationDialog.show(supportFragmentManager,"start")
        }
        tv_change_end.onClick {
            val list = stations.subList(startIndex+1, stations.size).filter {
                it.type in arrayOf(2,3,5)
            }
            changeStationDialog.arguments = bundleOf("isStart" to false,"list" to list)
            changeStationDialog.setCallback{position ->
                val busStation = list[position]
                endPointId = busStation.id
                endIndex = stations.indexOf(busStation)
                getData()
            }
            changeStationDialog.show(supportFragmentManager,"end")
        }
    }

    override fun initView() {
        title = String.format("%s—%s",start,end)
        mSlidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        mSlidingUpPanelLayout.addPanelSlideListener(this)
        mSlidingUpPanelLayout.isOverlayed = true
        aMap.uiSettings.isRotateGesturesEnabled=false
        aMap.uiSettings.isZoomControlsEnabled = false
        rv_bus_station.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_bus_station.adapter = adapter
        getData()
    }

    private fun getData() {
        com.hbcx.driver.network.HttpManager.getTicketLineDeatail( time.toTime("yyyy-MM-dd"), id, startPointId, endPointId).request(this) { _, data ->
            data?.let {
                money = it.money
                ticketNum = it.pedestal.toInt()
                tv_start_time.text = String.format("%s上车", it.start_time)
                tv_count.text = String.format("余票%s张", it.pedestal)
                tv_money.text = String.format("￥%.2f", it.money)
                tv_start.text = it.startName
                tv_end.text = it.endName
                tv_length.text = String.format("约%.2fkm", it.km2 / 1000)
                if (stations.isEmpty()&&it.stationList.size>5){ //第一次才设置高度
                    rv_bus_station.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dip(250))
                }
                if (it.stationList.isNotEmpty()) {
                    stations.clear()
                    endIndex = it.stationList.size-1
                    stations.addAll(it.stationList)
                    adapter.notifyDataSetChanged()
                }
                setStartMarker(it.startLat,it.startLon)
                if (it.endLat==0.0&&it.endLon == 0.0)
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.startLat,it.startLon)))
                else{
                    setEndMarker(it.endLat,it.endLon)
                    calculateRoute(LatLonPoint(it.startLat,it.startLon), LatLonPoint(it.endLat,it.endLon))
                }
            }
        }

    }

    private fun calculateRoute(startPoint: LatLonPoint, endPoint: LatLonPoint) {
        val fromAndTo = RouteSearch.FromAndTo(startPoint,endPoint)
        val driveRouteQuery = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "")
        val routeSearch = RouteSearch(this)
        routeSearch.calculateDriveRouteAsyn(driveRouteQuery)
        routeSearch.setRouteSearchListener(this)
    }
    override fun onDriveRouteSearched(result: DriveRouteResult?, p1: Int) {
        if (p1 == 1000) {
            route?.removeFromMap()
            if (result?.paths != null) {
                if (result.paths.size > 0) {
                    val drivePath = result.paths[0]
                    route = DrivingRouteOverlay(
                            this, aMap, drivePath,
                            result.startPos,
                            result.targetPos)
                    route!!.setNodeIconVisibility(false)
                    route!!.addToMap()
                    route!!.zoomToSpan(50, 50, 50, 50)
                }
            }
        }
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
    }

    private fun setStartMarker(lat:Double,lng:Double){
        if (startMarker==null){
            startMarker = aMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_start))
                    .position(LatLng(lat,lng)))
        }else{
            startMarker?.position = LatLng(lat,lng)
        }
    }
    private fun setEndMarker(lat:Double,lng:Double){
        if (endMarker==null){
            endMarker = aMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_start))
                    .position(LatLng(lat,lng)))
        }else{
            endMarker?.position = LatLng(lat,lng)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMapView.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {
    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
        if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
            tv_panel.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_panel_down,0)
        }else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED){
            tv_panel.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_panel,0)
        }
    }
}