package com.hbcx.driver.dialogs


import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import cn.sinata.amaplib.overlay.DrivingRouteOverlay
import cn.sinata.amaplib.util.TTSController
import cn.sinata.xldutils.activity.DialogActivity
import cn.sinata.xldutils.utils.*
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.hbcx.driver.R
import com.hbcx.driver.YypwApplication
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.fragment_new_order.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.wrapContent

/**
 *新订单
 */
class NewOrderDialog : DialogActivity(), RouteSearch.OnRouteSearchListener {

    private val order by lazy {
        intent.getSerializableExtra("data") as com.hbcx.driver.network.beans.Order
    }
    private val amap by lazy {
        mapView.map
    }

    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }

    private val type by lazy {
        intent.getIntExtra("type", 0)
    }
    private lateinit var ttsController:TTSController
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_new_order)
        YypwApplication.isOrderShow = true
        window.setGravity(Gravity.BOTTOM)
        window.setLayout((screenWidth() * 0.8).toInt(), wrapContent)

        mapView.onCreate(savedInstanceState)

        setFinishOnTouchOutside(false)

        mapView.layoutParams.height = (screenWidth() * 0.8 / 2.68).toInt()
        mapView.requestLayout()

        amap.uiSettings.isZoomControlsEnabled = false
        amap.uiSettings.isRotateGesturesEnabled = false
        amap.uiSettings.isScaleControlsEnabled = false
        amap.uiSettings.isTiltGesturesEnabled = false
        amap.uiSettings.isZoomGesturesEnabled = false
        amap.uiSettings.isCompassEnabled = false
        amap.uiSettings.isScrollGesturesEnabled = false



        tv_close.setOnClickListener {
            YypwApplication.isOrderShow = false
            (application as YypwApplication).showNextOrder()
            ttsController.stopSpeaking()
            finish()
        }
        tv_action.setOnClickListener {
            takeOrder()
        }
        ttsController = TTSController.getInstance(applicationContext)
        ttsController.init()
        ttsController.setTTSType(TTSController.TTSType.IFLYTTS)
        setUI()
    }

    private fun setTime(c: String) {
        tv_action.text = SpanBuilder(c)
                .size(0, c.length - 2, 24)
                .style(0, c.length - 2, Typeface.BOLD)//粗体
                .build()
    }

    private fun setUI() {
        val startLatLng = LatLng(order.startLat!!.toDouble(), order.startLon!!.toDouble())
        val endLatLng = LatLng(order.endLat!!.toDouble(), order.endLon!!.toDouble())
        tv_distance.text = if (order.estimateDistance!! < 1000.0) String.format("距您%d米", order.distance!!.toInt() ) else
            String.format("距您%.1f公里", order.distance!! / 1000)
        tv_order_type.text = String.format("%s订单", when (order.type) {
            1 -> "快车"
            2 -> "专车经济"
            3 -> "专车舒适"
            4 -> "专车商务"
            else -> ""
        })
        val startTime = order.departureTime
        val time = startTime!!.timeDay()
        tv_content1.text = time
        tv_content2.text = order.startAddress
        tv_content3.text = order.endAddress
        drawMarker(startLatLng, endLatLng)
        tv_all_distance.text = String.format("全程约%.1f公里", order.mileage)
        var voiceText = ""
        voiceText = if (order.distance!! < 1000.0) {String.format("您收到新的%s订单,距您约%d米,从%s出发," +
//                "出发时间%s," +
                "全程约%.1f公里"
                ,if (order.type == 1) "快车" else "专车",order.distance!!.toInt() ,order.startAddress,order.mileage)}
        else{
            String.format("您收到新的%s订单,距您约%.1f公里,从%s出发," +
//                "出发时间%s," +
                    "全程约%.1f公里"
                    ,if (order.type == 1) "快车" else "专车",order.distance!! / 1000,order.startAddress,order.mileage)
        }
        ttsController.playText(voiceText)
        setTime("60S\n抢单")
        countDownTimer =
                object : CountDownTimer(60 * 1000, 1000) {
                    override fun onFinish() {
                        YypwApplication.isOrderShow = false
                        (application as YypwApplication).showNextOrder()
                        finish()
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        setTime(String.format("%sS\n抢单", millisUntilFinished / 1000))
                    }
                }
        countDownTimer?.start()
    }

    private fun drawMarker(startLatLng: LatLng, endLatLng: LatLng) {
        amap.clear()
        val startMarkerOptions = MarkerOptions()
        startMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_start))
        startMarkerOptions.position(startLatLng)
        amap.addMarker(startMarkerOptions)

        val endMarkerOptions = MarkerOptions()
        endMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_end))
        endMarkerOptions.position(endLatLng)
        amap.addMarker(endMarkerOptions)

        val fromAndTo = RouteSearch.FromAndTo(LatLonPoint(startLatLng.latitude, startLatLng.longitude)
                , LatLonPoint(endLatLng.latitude, endLatLng.longitude))
        val driveRouteQuery = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "")
        val routeSearch = RouteSearch(this)
        routeSearch.calculateDriveRouteAsyn(driveRouteQuery)
        routeSearch.setRouteSearchListener(this)
    }

    private fun takeOrder() {
        showDialog()
        com.hbcx.driver.network.HttpManager.robOrder(userId, order.id!!).request(this,success = { _, _ ->
            toast("抢单成功！")
            YypwApplication.isOrderShow = false
            (application as YypwApplication).nextOrder = null
            order.let {
                if (type != 1) {
                    //                    data.type = order.type
                    startActivity<com.hbcx.driver.ui.cardriver.TripActivity>("orderId" to order.id)
                }
                finish()
            }
        },error = {_,_->
            YypwApplication.isOrderShow = false
            finish()
        })
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
        try {
            mapView.onDestroy()
            countDownTimer?.cancel()
            countDownTimer = null
        } catch (e: Exception) {

        }
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    //屏蔽物理键
    override fun onBackPressed() {

    }

    override fun onDriveRouteSearched(result: DriveRouteResult?, errorCode: Int) {
        if (errorCode == 1000) {
            if (result?.paths != null) {
                if (result.paths.size > 0) {
                    val drivePath = result.paths[0]
                    val drivingRouteOverlay = DrivingRouteOverlay(
                            this, amap, drivePath,
                            result.startPos,
                            result.targetPos)
                    drivingRouteOverlay.removeFromMap()
                    drivingRouteOverlay.setNodeIconVisibility(false)
                    drivingRouteOverlay.addToMap()
                    drivingRouteOverlay.zoomToSpan(50,50,50,50)
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