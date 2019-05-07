package com.hbcx.driver.ui.cardriver

import android.Manifest
import android.graphics.Typeface
import android.os.Bundle
import android.widget.AbsListView
import cn.sinata.rxnetty.NettyClient
import cn.sinata.xldutils.activity.BaseActivity
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.utils.*
import cn.sinata.xldutils.visible
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.ui.account.MessageActivity
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.StatusBarUtil
import com.hbcx.driver.utils.request
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_driver_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject

class DriverMainActivity : BaseActivity(), AMapLocationListener, com.hbcx.driver.interfaces.TripMessageListener, AbsListView.OnScrollListener {
    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        val view1 = view!!.getChildAt(firstVisibleItem)
        refresh_layout.isEnabled = firstVisibleItem == 0&&(view1 == null || view1.top == 0)
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
    }

    override fun onRefuseSuccess(obj: JSONObject) {
        toast("您的订单已成功改派")
        showDialog()
        getData()
    }

    override fun onReceiveOrder(obj: JSONObject) {
        showDialog()
        getData()
    }

    private var working = false
    private var orders = arrayListOf<com.hbcx.driver.network.beans.OrderList>()
    private val adapter = com.hbcx.driver.adapter.MainCarOrderAdapter(this, orders)
    private val app by lazy {
        application as com.hbcx.driver.YypwApplication
    }
    private var state = 1  //1=离线，2=空闲，3=服务中，4=已停用,5=已删除
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)
        StatusBarUtil.initStatus(window)
        lv_now_order.adapter = adapter
        lv_now_order.emptyView = tv_empty
        RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE).subscribe { }
        tv_time.text = "${System.currentTimeMillis().toMDTime()} ${System.currentTimeMillis().toWeek("星期")}"
        initClick()
        NettyClient.getInstance().startService()
        //开启定位。
        app.addLocationListener(this)
        app.startLocation()
        getData()
        refresh_layout.setOnRefreshListener {
            getData()
        }
        app.addTripMessageListener(this)
        lv_now_order.setOnScrollListener(this)
    }

    private fun initClick() {
        tv_work.setOnClickListener {
            com.hbcx.driver.network.HttpManager.changeWorkState(SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)).request(this) { _, _ ->
                working = !working
                tv_work_state.isEnabled = working
                tv_work_state.text = if (working) {
                    "接单中"
                } else {
                    "待接单"
                }
                tv_work.text = if (working) {
                    "下班"
                } else {
                    "上班"
                }
                state = if (state == 1){
                    2
                }else
                    1
            }
        }

        lv_now_order.setOnItemClickListener { _, _, position, _ ->
            if (orders[position].status == 9)
                return@setOnItemClickListener
            if (orders[position].status in 1..4){
                startActivity<com.hbcx.driver.ui.cardriver.TripActivity>("orderId" to orders[position].id)
            }else
                startActivity<com.hbcx.driver.ui.cardriver.OrderDetailActivity>("orderId" to orders[position].id)
        }

        tv_order.setOnClickListener {
            startActivity<com.hbcx.driver.ui.cardriver.DriverOrderActivity>()
        }

        iv_menu.setOnClickListener {
            startActivity<com.hbcx.driver.ui.cardriver.DriverMenuActivity>()
        }
        iv_msg.onClick {
            startActivity<MessageActivity>()
        }
    }

    private fun getData() {
        com.hbcx.driver.network.HttpManager.getDriverMain(SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)).request(this, true, { _, data ->
            refresh_layout.isRefreshing = false
            data.let {
                state = it?.status!!
                when(state){
                    1,4,5-> {
                        working = false
                        tv_work.text = "上班"
                        tv_work_state.text = "待接单"
                    }
                    2,3->{
                        working = true
                        tv_work.text = "下班"
                        tv_work_state.text = "接单中"
                    }
                }
                tv_work_state.isEnabled = working
                tv_car_num.text = it.licensePlate
                tv_car_type.text = "${it.brandName}${it.modelName} ${it.carColor}"
                val s1 = "${it.praise}%\n好评率"
                tv_good_percent.text = SpanBuilder(s1).style(0, s1.length - 4, Typeface.BOLD)
                        .size(0, s1.length - 4, 16)
                        .build()
                val s2 = "${it.driverOrderNums}\n今日接单"
                tv_order_count.text = SpanBuilder(s2).style(0, s2.length - 4, Typeface.BOLD)
                        .size(0, s2.length - 4, 16)
                        .build()
                val s3 = "${it.money}\n今日收入"
                tv_income.text = SpanBuilder(s3).style(0, s3.length - 4, Typeface.BOLD)
                        .size(0, s3.length - 4, 16)
                        .build()
                orders.clear()
                orders.addAll(it.orderList)
                adapter.notifyDataSetChanged()
            }
        }, { _, msg ->
            toast(msg)
            refresh_layout.isRefreshing = false
        }
        )
    }
    override fun onLocationChanged(location: AMapLocation?) {
        if (location != null) {
            val lat = location.latitude
            val lng = location.longitude
            com.hbcx.driver.YypwApplication.lat = lat
            com.hbcx.driver.YypwApplication.lng = lng
            if (state > 1) {
                uploadLocation(lat, lng)
            }
        }
    }
    /**
     * 上传位置
     */
    private fun uploadLocation(lat: Double, lng: Double) {
        val map = HashMap<String, Any>()
        val userId = SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
        map["id"] = userId
//        map["citycode"] = cityCode
        map["lon"] = lng
        map["lat"] = lat
        map["orderId"] = 0
        val json = JSONObject(map)
        NettyClient.getInstance().sendMessage("{\"con\":$json,\"method\":\"LOCATION\",\"code\":\"0\",\"msg\":\"SUCCESS\"}")
    }

    private fun hasNewMsg() {
        val id = SPUtils.instance().getInt(Const.User.USER_ID)
        if (id != -1) {
            HttpManager.hasNewMsg(id).request(this) { _, data ->
                if (data?.optBoolean("isMess") == true) {
                    iv_unread.visible()
                }else
                    iv_unread.gone()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        getData()
        hasNewMsg()
    }

    override fun onDestroy() {
        super.onDestroy()
        app.removeLocationListener(this)
        app.removeTripMessageListener(this)
    }
}