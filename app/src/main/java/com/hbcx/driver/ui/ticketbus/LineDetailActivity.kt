package com.hbcx.driver.ui.ticketbus

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.sinata.amaplib.overlay.DrivingRouteOverlay
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.visible
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
import kotlinx.android.synthetic.main.activity_line_detail.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick


class LineDetailActivity : com.hbcx.driver.ui.TranslateStatusBarActivity(), SlidingUpPanelLayout.PanelSlideListener, RouteSearch.OnRouteSearchListener {
    private val aMap by lazy {
        mMapView.map
    }

    private val data = arrayListOf<com.hbcx.driver.network.beans.BusStation>()

    private val adapter by lazy {
        com.hbcx.driver.adapter.BusStationAdapter(data)
    }

    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }

    private val isCurrentDay by lazy {
        intent.getBooleanExtra("isCurrentDay",false)
    }

    private val id by lazy {
        intent.getIntExtra("id", 0)
    }

    private val time by lazy {
        intent.getStringExtra("time")
    }

    private var startMarker: Marker? = null //起点标记
    private var endMarker: Marker? = null //终点标记
    private var route: DrivingRouteOverlay? = null //路径

    override fun setContentView() = R.layout.activity_line_detail

    override fun initClick() {
        iv_location.onClick {
            aMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)))
        }
        tv_action.onClick {
            it as TextView
            if (it.text == "安全到达") {
                val tipDialog = com.hbcx.driver.dialogs.TipDialog()
                tipDialog.arguments = bundleOf("msg" to "是否确认到达？", "cancel" to "取消", "ok" to "确认")
                tipDialog.setDialogListener { p, s ->
                    showDialog()
                    com.hbcx.driver.network.HttpManager.busArrived(userId, id).request(this@LineDetailActivity) { _, _ ->
                        toast("辛苦了")
                        finish()
                    }
                }
                tipDialog.show(supportFragmentManager, "start")
            } else {
                val tipDialog = com.hbcx.driver.dialogs.TipDialog()
                tipDialog.arguments = bundleOf("msg" to "是否确认发车？", "cancel" to "取消", "ok" to "确认")
                tipDialog.setDialogListener { p, s ->
                    showDialog()
                    com.hbcx.driver.network.HttpManager.busStart(userId, id).request(this@LineDetailActivity) { _, _ ->
                        toast("请注意安全，规范驾驶")
                        tv_action.text = "安全到达"
                    }
                }
                tipDialog.show(supportFragmentManager, "start")
            }
        }
    }

    override fun initView() {
        title = "线路详情"
        mSlidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        mSlidingUpPanelLayout.addPanelSlideListener(this)
        mSlidingUpPanelLayout.isOverlayed = true
        aMap.uiSettings.isRotateGesturesEnabled = false
        aMap.uiSettings.isZoomControlsEnabled = false
        aMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location)).position(LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)))
        rv_bus_station.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_bus_station.adapter = adapter
        getData()
        if (!isCurrentDay){
            tv_action.isEnabled = false
            tv_action.backgroundResource = R.color.light_gry
        }
    }

    private fun getData() {
        com.hbcx.driver.network.HttpManager.getLineDetail(userId, id, time).request(this) { _, data ->
            data?.let {
                tv_start.text = it.startStationName
                tv_end.text = it.endStationName
                tv_station_count.text = String.format("%d站", it.stationNum)
                tv_time_and_count.text = String.format("%s 购票：%d人", it.start_time, it.peoNum1)
                tv_person_count.text = String.format("已坐：%d人", it.peoNum)
                tv_action.visible()
                tv_action.text = it.getActionStr()
                if (it.lineList.size > 5)
                    rv_bus_station.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip(250))
                this.data.addAll(it.lineList)
                adapter.notifyDataSetChanged()
                setStartMarker(it.startLat, it.startLon)
                if (it.endLat == 0.0 && it.endLon == 0.0)
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.startLat, it.startLon)))
                else {
                    setEndMarker(it.endLat, it.endLon)
                    calculateRoute(LatLonPoint(it.startLat, it.startLon), LatLonPoint(it.endLat, it.endLon))
                }
                adapter.setOnItemClickListener { view, position ->
                    startActivity<com.hbcx.driver.ui.ticketbus.PassengersActivity>("id" to id, "time" to time, "upId" to it.lineList[position].id)
                }
            }
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
        if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            tv_panel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_panel_down, 0)
        } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            tv_panel.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_panel, 0)
        }
    }

    private fun calculateRoute(startPoint: LatLonPoint, endPoint: LatLonPoint) {
        val fromAndTo = RouteSearch.FromAndTo(startPoint, endPoint)
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

    private fun setStartMarker(lat: Double, lng: Double) {
        if (startMarker == null) {
            startMarker = aMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_start))
                    .position(LatLng(lat, lng)))
        } else {
            startMarker?.position = LatLng(lat, lng)
        }
    }

    private fun setEndMarker(lat: Double, lng: Double) {
        if (endMarker == null) {
            endMarker = aMap.addMarker(MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_start))
                    .position(LatLng(lat, lng)))
        } else {
            endMarker?.position = LatLng(lat, lng)
        }
    }
}