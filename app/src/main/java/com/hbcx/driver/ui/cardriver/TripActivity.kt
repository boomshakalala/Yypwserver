package com.hbcx.driver.ui.cardriver

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView

import cn.sinata.amaplib.GPSNaviActivity
import cn.sinata.amaplib.overlay.DrivingRouteOverlay
import cn.sinata.rxnetty.NettyClient
import cn.sinata.xldutils.callPhone

import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.optDouble
import cn.sinata.xldutils.utils.timeDay

import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
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
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import kotlinx.android.synthetic.main.activity_trip.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivityForResult

/**
 * 行程中
 */
class TripActivity : com.hbcx.driver.ui.TranslateStatusBarActivity(), AMapLocationListener, Handler.Callback, com.hbcx.driver.interfaces.OnTripStateListener, RouteSearch.OnRouteSearchListener, SlidingUpPanelLayout.PanelSlideListener, com.hbcx.driver.interfaces.TripMessageListener {
    override fun onReceiveOrder(obj: JSONObject) {

    }

    override fun onRefuseSuccess(obj: JSONObject) {
        val id = obj.optInt("id")
        if (id == order!!.id) {
            val tipDialog = com.hbcx.driver.dialogs.TipDialog()
            tipDialog.arguments = bundleOf("msg" to "此订单已改派成功", "notice" to true)
            tipDialog.setDialogListener { p, s ->
                finish()
            }
            tipDialog.show(supportFragmentManager, "cancel")
        }
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

    override fun setContentView() = R.layout.activity_trip

    private val orderId by lazy {
        intent.getIntExtra("orderId", 0)
    }

    override fun initClick() {
        tv_location.setOnClickListener {
            aMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)))
        }

        tv_guide.setOnClickListener {
            val startLatLng = LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)
            val endLatLng = if (state < 5) {//去用户起点
                LatLng(order!!.startLat!!.toDouble(), order!!.startLon!!.toDouble())
            } else {//去终点
                LatLng(order!!.endLat!!.toDouble(), order!!.endLon!!.toDouble())
            }
            startActivity<GPSNaviActivity>("start" to startLatLng, "end" to endLatLng)
        }
        //按钮滑动触发
        btn_slide_action.onSwipeListener = {
            when (order!!.status) {
                2 -> {
                    if (order!!.setOutIsNot!!)
                        arriveStartAddress()
                    else
                        goStartAddress()
                }
                3 -> {
                    startTrip()
                }
                4 -> {
                    endTrip()
                }
            }
        }

        tv_call_phone.setOnClickListener {
            order?.let {
                callPhone(it.phone)
            }
        }

        mSlidingUpPanelLayout.addPanelSlideListener(this)
    }

    override fun initView() {
        if (intent.getSerializableExtra("data") != null)
            order = intent.getSerializableExtra("data") as com.hbcx.driver.network.beans.Order
        title = "准备出发"
        mSlidingUpPanelLayout.isClipPanel = false
        mSlidingUpPanelLayout.isOverlayed = true


        titleBar.addRightButton("申请改派", onClickListener = View.OnClickListener {
            com.hbcx.driver.network.HttpManager.getCancelMoney(order!!.id!!).request(this) { _, data ->
                data?.let {
                    val money = it.optDouble("money")
                    val tipDialog = com.hbcx.driver.dialogs.TipDialog()
                    if (money == 0.0) //可直接取消
                        tipDialog.arguments = bundleOf("msg" to "您是否进行改派？", "cancel" to "取消")
                    else {
                        tipDialog.arguments = bundleOf("msg" to "当前改派将收取￥$money 元作为服务费，你是否要进行改派？", "cancel" to "取消")
                    }
                    tipDialog.setDialogListener { p, s ->
                        startActivityForResult<com.hbcx.driver.ui.cardriver.CancelOrderActivity>(8, "orderId" to order!!.id)
                    }
                    tipDialog.show(supportFragmentManager, "cancel")
                }
            }
        })
        app.addLocationListener(this)
        app.addTripStateListener(this)
        app.addTripMessageListener(this)
        mSlidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        if (order == null) {
            getData()
        } else {
            setStateUI()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 8 && resultCode == Activity.RESULT_OK)
            finish()
    }

    private val aMap by lazy {
        mapView.map
    }

    private val app by lazy {
        application as com.hbcx.driver.YypwApplication
    }

    private var state = 2 //1=待应答，2=待接驾，3=待上车，4=服务中，5=待支付，6=取消待支付，7=待评价，8=已完成，9=已取消


    private var order: com.hbcx.driver.network.beans.Order? = null

    private var driverMarker: Marker? = null
    private var endMarker: Marker? = null

    private val handler by lazy {
        Handler(this)
    }


    private var startMarker: Marker? = null //预约地点

    private var drivingRouteOverlay: DrivingRouteOverlay? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView.onCreate(savedInstanceState)
        aMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)))
        aMap.uiSettings.isZoomControlsEnabled = false
        aMap.uiSettings.isRotateGesturesEnabled = false
        aMap.uiSettings.setZoomInByScreenCenter(true)
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        app.removeLocationListener(this)
        app.removeTripStateListener(this)
        app.removeTripMessageListener(this)
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    private fun setStateUI() {
        headView.setImageURI(order!!.imgUrl)
        tv_name.text = order!!.nickName
        tv_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (order!!.sex == 1) R.mipmap.ic_man else R.mipmap.ic_woman, 0)
        tv_count.text = String.format("%d次乘车", order!!.num)

        val time = order!!.departureTime?.timeDay()
        tv_content1.text = time
        tv_content2.text = order!!.startAddress
        tv_content3.text = order!!.endAddress

        val latLon = LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)
        when (order!!.status) {
            2 -> {
                if (order!!.setOutIsNot!!) {
                    title = "去接客户"
                    btn_slide_action.changeButtonText("到达预约地点")
                } else {
                    title = "准备出发"
                    btn_slide_action.changeButtonText("出发前往预约地点")
                }
                setMarker(latLon, String.format("距预约点%.2f公里", order!!.estimateDistance ?: 0))
                setStartMarker()
                setRoute(LatLonPoint(latLon.latitude, latLon.longitude), LatLonPoint(order!!.startLat!!.toDouble(), order!!.startLon!!.toDouble()))
            }
            3 -> {
                title = "等待客户"
                startMarker?.remove()
                btn_slide_action.changeButtonText("开始行程")
                setMarker(latLon, "您已等待00:00")
                handler.sendEmptyMessageDelayed(0, 1000)
            }
            4 -> {
                handler.removeMessages(0)
                titleBar.hideAllRightButton()
                title = "服务中"
                btn_slide_action.changeButtonText("送达该乘客")
                setMarker(latLon, String.format("剩余%.2f公里\n预计还需%d分钟", order!!.estimateDistance
                        ?: 0, order!!.estimateTime ?: 0))
                setStartMarker()
                setEndMarker()
                setRoute(LatLonPoint(latLon.latitude, latLon.longitude), LatLonPoint(order!!.endLat!!.toDouble(), order!!.endLon!!.toDouble()))
            }
        }
    }

    private fun getData() {
        com.hbcx.driver.network.HttpManager.getOrderDetail(orderId).request(this) { _, data ->
            data.let {
                this.order = it
                setStateUI()
            }
        }
    }

    //前往预约地点
    private fun goStartAddress() {
        showDialog()
        com.hbcx.driver.network.HttpManager.goStartAddress(order!!.id!!).request(this) { _, _ ->
            order!!.setOutIsNot = true
            setStateUI()
        }
    }

    //到达预约地点
    private fun arriveStartAddress() {
        showDialog()
        com.hbcx.driver.network.HttpManager.arriveStartAddress(order!!.id!!).request(this) { _, _ ->
            order!!.status = 3
            order!!.arrivalTime = System.currentTimeMillis()
            setStateUI()
        }
    }

    //开始行程
    private fun startTrip() {
        showDialog()
        com.hbcx.driver.network.HttpManager.startTrip(order!!.id!!).request(this) { _, _ ->
            order!!.status = 4
            setStateUI()
        }
    }

    //送达乘客
    private fun endTrip() {
        showDialog()
        com.hbcx.driver.network.HttpManager.endTrip(order!!.id!!, com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng).request(this) { _, data ->
            data?.let {
                startActivity<com.hbcx.driver.ui.cardriver.OrderDetailActivity>("data" to data)
            }
            finish()
        }
    }

    private fun setMarker(latLon: LatLng, title: CharSequence = "") {
        //文本改变，或marker未初始化时初始化图标信息。
        val b = if (title.isNotEmpty() || driverMarker == null) {
            val view = layoutInflater.inflate(R.layout.marker_start_location, null, false)
            val contentView = view.findViewById<TextView>(R.id.tv_content)
            if (title.isEmpty()) {
                contentView.text = ""
            } else {
                contentView.text = title
            }
            BitmapDescriptorFactory.fromView(view)
        } else {
            null
        }
        if (driverMarker == null) {
            val options = MarkerOptions()
            options.position(latLon)
            options.icon(b)
            driverMarker = aMap.addMarker(options)
        } else {
            driverMarker?.position = latLon
            //当有改变才重新赋值
            if (b != null) {
                driverMarker?.setIcon(b)
            }
        }
    }

    private fun setStartMarker() {
        //文本改变，或marker未初始化时初始化图标信息。
        val latLng = LatLng(order!!.startLat!!.toDouble(), order!!.startLon!!.toDouble())
        val b = BitmapDescriptorFactory.fromResource(if (order!!.status == 2) R.mipmap.ic_start_point else R.mipmap.ic_marker_start)
        if (startMarker == null) {
            val options = MarkerOptions()
            options.position(latLng)
            options.icon(b)
            startMarker = aMap.addMarker(options)
        } else {
            startMarker?.position = latLng
            startMarker?.setIcon(b)
        }
    }

    private fun setEndMarker() {
        //文本改变，或marker未初始化时初始化图标信息。
        val latLng = LatLng(order!!.endLat!!.toDouble(), order!!.endLon!!.toDouble())
        if (endMarker == null) {
            val b = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_end)
            val options = MarkerOptions()
            options.position(latLng)
            options.icon(b)
            endMarker = aMap.addMarker(options)
        } else {
            endMarker?.position = latLng
        }
    }

    private fun setRoute(start: LatLonPoint, end: LatLonPoint) {
        val fromAndTo = RouteSearch.FromAndTo(start, end)
        val driveRouteQuery = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "")
        val routeSearch = RouteSearch(this)
        routeSearch.calculateDriveRouteAsyn(driveRouteQuery)
        routeSearch.setRouteSearchListener(this)
    }

//    private fun setStartMarker(latLon: LatLng, title: CharSequence = "") {
//
//        //文本改变，或marker未初始化时初始化图标信息。
//        val b = if (title.isNotEmpty() || startMarker == null) {
//
//            BitmapDescriptorFactory.fromResource(R.mipmap.)
//        } else {
//            null
//        }
//        if (startMarker == null) {
//            val options = MarkerOptions()
//            options.position(latLon)
//            options.icon(b)
//            startMarker = aMap.addMarker(options)
//        } else {
//            startMarker?.position = latLon
//            //当有改变才重新赋值
//            if (b != null) {
//                startMarker?.setIcon(b)
//            }
//        }
//    }

    override fun onLocationChanged(location: AMapLocation?) {
        if (location == null) {
            return
        }
        val latLon = LatLng(location.latitude, location.longitude)

        setMarker(latLon)

        if (order!=null&&(order!!.status == 2||order!!.status == 4)) {//如果开始行程和前往接乘客就会实时更新位置
            sendLocation(location.latitude, location.longitude)
        }
    }

    /**
     * 上传位置
     */
    private fun sendLocation(lat: Double, lng: Double) {
        val map = HashMap<String, Any>()
        val userId = SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
        map["id"] = userId
//        map["citycode"] = cityCode
        map["lon"] = lng
        map["lat"] = lat
        map["orderId"] = if (order != null) order!!.id!! else 0
        val json = JSONObject(map)
        NettyClient.getInstance().sendMessage("{\"con\":$json,\"method\":\"LOCATION\",\"code\":\"0\",\"msg\":\"SUCCESS\"}")
    }


    override fun handleMessage(msg: Message?): Boolean {
        when (msg?.what) {
            0 -> {
                val latLon = LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)
                val waittime = order!!.arrivalTime ?: 0
                val s = System.currentTimeMillis()
                val i = s - waittime
                setMarker(latLon, String.format("您已等待%02d:%02d", i / 1000 / 60, i / 1000 % 60))
                handler.sendEmptyMessageDelayed(0, 1000)
            }
        }
        return true
    }

    override fun onTripping(obj: JSONObject) {
        if (order!!.status==2){
            val latLon = LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)
            val yjm = obj.optDouble("estimateDistance")
            setMarker(latLon, String.format("距预约点%.2f公里", yjm))
            setStartMarker()
            setRoute(LatLonPoint(latLon.latitude, latLon.longitude), LatLonPoint(order!!.startLat!!.toDouble(), order!!.startLon!!.toDouble()))
        }
        if (order!!.status==4){
            val latLon = LatLng(com.hbcx.driver.YypwApplication.lat, com.hbcx.driver.YypwApplication.lng)
            val yjm = obj.optDouble("estimateDistance")
            val yjfz = obj.optInt("estimateTime")
            setMarker(latLon, String.format("剩余%.2f公里\n预计还需%d分钟", yjm, yjfz))
        }
    }

    override fun onCancel(obj: JSONObject,type:Int) {
        val id = obj.optInt("id")
        if (id == order!!.id) {
            val tipDialog = com.hbcx.driver.dialogs.TipDialog()
            tipDialog.arguments = bundleOf("msg" to if (type==0) "客户已取消订单" else "平台已取消订单", "notice" to true)
            tipDialog.setDialogListener { p, s ->
                finish()
            }
            tipDialog.show(supportFragmentManager, "cancel")
        }
    }

    override fun onDriveRouteSearched(result: DriveRouteResult?, errorCode: Int) {
        if (errorCode == 1000) {
            drivingRouteOverlay?.removeFromMap()
            if (result?.paths != null) {
                if (result.paths.size > 0) {
                    val drivePath = result.paths[0]
                    drivingRouteOverlay = DrivingRouteOverlay(
                            this, aMap, drivePath,
                            result.startPos,
                            result.targetPos)
                    drivingRouteOverlay!!.setNodeIconVisibility(false)
                    drivingRouteOverlay!!.addToMap()
                    drivingRouteOverlay!!.zoomToSpan(50, 500, 50, 200)
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
}
