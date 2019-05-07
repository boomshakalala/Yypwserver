package com.hbcx.driver

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.support.multidex.MultiDex
import android.util.Log
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.TagAliasCallback
import cn.sinata.rxnetty.NettyClient
import cn.sinata.xldutils.application.BaseApplication
import cn.sinata.xldutils.getUUID
import cn.sinata.xldutils.ioScheduler
import cn.sinata.xldutils.sysErr
import cn.sinata.xldutils.utils.SPUtils
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hbcx.driver.dialogs.NewOrderDialog
import com.hbcx.driver.network.beans.Order
import com.hbcx.driver.ui.cardriver.TripActivity
import com.hbcx.driver.ui.login.LoginActivity
import com.hbcx.driver.utils.Const
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import io.reactivex.Flowable
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*
import java.util.concurrent.TimeUnit

class YypwApplication : BaseApplication(), Application.ActivityLifecycleCallbacks, AMapLocationListener {
    private val activities = ArrayList<Activity?>()
    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activities.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        activities.add(activity)
    }

    private val aMapLocationClient by lazy {
        AMapLocationClient(this)
    }
    private val locationListeners = ArrayList<AMapLocationListener>()
    private val tripStateListeners = ArrayList<com.hbcx.driver.interfaces.OnTripStateListener>()
    private val msglisteners = ArrayList<com.hbcx.driver.interfaces.TripMessageListener>()

    override fun onLocationChanged(p0: AMapLocation?) {
        locationListeners.forEach {
            it.onLocationChanged(p0)
        }
    }

    companion object {
        var lat = 0.0
        var lng = 0.0
        var isOrderShow = false //当前是否显示新订单Dialog
    }

    override fun getSPName(): String {
        return "yypwservice"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    var nextOrder :Order? = null
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)
        ZXingLibrary.initDisplayOpinion(this)
        JPushInterface.init(this)
        initLocationOption()
        UMConfigure.init(this, Const.UMENG_KEY, "", UMConfigure.DEVICE_TYPE_PHONE, "")
        PlatformConfig.setWeixin(Const.WX_APP_ID, Const.WX_SECRET)
        PlatformConfig.setQQZone(Const.QQ_APP_ID, Const.QQ_SECRET)
        PlatformConfig.setSinaWeibo(Const.SINA_APP_ID, Const.SINA_SECRET, "")
        NettyClient.getInstance().init(this, com.hbcx.driver.network.Api.SOCKET_SERVER, com.hbcx.driver.network.Api.SOCKET_PORT, false)
        NettyClient.getInstance().addOnMessageListener { message ->
            Log.e("socket", message)
            writeToFile(message)
            try {
                val json = JSONObject(message)
                val method = json.optString("method")
                val code = json.optInt("code", -1)
                if (code == 0) {
                    when (method) {
                        Const.Method.PING_RECIEVE -> { //心跳
                            if (SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID) == -1) {
                                return@addOnMessageListener
                            }
                            //延时发送心跳
                            Flowable.just("").delay(5000, TimeUnit.MILLISECONDS).subscribe {
                                sendHeart()
                            }
                        }
                        Const.Method.ORDER_CANCLE -> {
                            tripStateListeners.forEach {
                                val con = json.optJSONObject("con")
                                runOnUiThread { it.onCancel(con) }
                            }
                        }
                        Const.Method.USER_LOGIN -> {//其他设备登录
                            NettyClient.getInstance().stopService()
                            SPUtils.instance().put(com.hbcx.driver.utils.Const.User.USER_ID, -1).apply()
                            Looper.prepare()
                            activities[0]?.toast(json.optJSONObject("con").optString("msg"))
                            exitToLogin()
                            Looper.loop()
                        }
                        Const.Method.NEW_ORDER -> {
                            //收到订单
                            val con = json.optJSONObject("con")
                            val order = Gson().fromJson<Order>(con.toString(), object : TypeToken<Order>() {}.type)
                            if (order.status == 1) {
                                if (isOrderShow)
                                    nextOrder = order
                                else{
                                    if (activities.isNotEmpty()) {
                                        //最顶部页面
                                        val act = activities[activities.size - 1]
                                        if (act != null && act is TripActivity) {
                                            act.startActivity<NewOrderDialog>("type" to 1, "data" to order)
                                        } else {
                                            act?.startActivity<NewOrderDialog>("data" to order)
                                        }
                                    }
                                }
                            } else {
                                tripStateListeners.forEach {
                                    val con = json.optJSONObject("con")
                                    runOnUiThread { it.onTripping(con) }
                                }
                            }
                        }
                        com.hbcx.driver.utils.Const.Method.ORDER_CANCLE_PLAT ->{
                            tripStateListeners.forEach {
                                val con = json.optJSONObject("con")
                                runOnUiThread { it.onCancel(con,1) }
                            }
                        }
                        com.hbcx.driver.utils.Const.Method.REFUSE_ORDER->{
                            msglisteners.forEach {
                                val con = json.optJSONObject("con")
                                runOnUiThread { it.onRefuseSuccess(con) }
                            }
                        }
                        com.hbcx.driver.utils.Const.Method.RECEIVE_ORDER->{
                            msglisteners.forEach {
                                val con = json.optJSONObject("con")
                                runOnUiThread {
                                    toast("您收到新的指派订单")
                                    it.onReceiveOrder(con) }
                            }
                        }
                    }
                }
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
        NettyClient.getInstance().setOnConnectListener {
            //连接成功，发送一次心跳
            sendHeart()
        }
    }

    fun exitToLogin(){
        Log.e("mmp","exit1")
        if (activities.isNotEmpty()) {
            //最顶部页面
            val act = activities[activities.size - 1]
            activities.forEach {
                it?.finish()
            }
            JPushInterface.deleteAlias(this,0)
            act?.startActivity<LoginActivity>()
        }
        Log.e("mmp","exit2")
    }

    /**
     * 测试方法
     */
    private fun writeToFile(content: String) {
        var bufferedWriter: BufferedWriter? = null
        var outputStream: FileOutputStream? = null
        Flowable.just(content).ioScheduler()
                .map {
                    try {
                        sysErr("----writeToFile---")
                        val file = File(Environment.getExternalStorageDirectory().absolutePath + "/YYDriver")
                        if (!file.exists()) {
                            file.mkdirs()
                        }
                        val f = File(file.absolutePath + "/log.txt")
                        outputStream = FileOutputStream(f, true)
                        bufferedWriter = BufferedWriter(OutputStreamWriter(outputStream))
                        bufferedWriter?.write(Date().toString() + "--->" + content + "\n\n")
                        sysErr("----writeToFile--->$f")
                        ""
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    } finally {
                        try {
                            bufferedWriter?.close()
                            outputStream?.close()
                        } catch (e: Exception) {
                        }
                    }
                }.subscribe { }
    }

    /**
     * 高德定位设置
     */
    private fun initLocationOption() {
        val option = AMapLocationClientOption()
        option.interval = 5 * 1000
        option.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        option.isMockEnable = false
        aMapLocationClient.setLocationOption(option)
        aMapLocationClient.setLocationListener(this)
    }

    /**
     * 设置定位监听回调
     */
    fun addLocationListener(listener: AMapLocationListener) {
        locationListeners.add(listener)
    }

    /**
     * 移除监听
     */
    fun removeLocationListener(listener: AMapLocationListener) {
        locationListeners.remove(listener)
    }

    /**
     *
     */
    fun addTripStateListener(listener: com.hbcx.driver.interfaces.OnTripStateListener) {
        tripStateListeners.add(listener)

    }

    fun addTripMessageListener(changedListener: com.hbcx.driver.interfaces.TripMessageListener) {
        msglisteners.add(changedListener)
    }

    fun removeTripMessageListener(changedListener: com.hbcx.driver.interfaces.TripMessageListener) {
        msglisteners.remove(changedListener)
    }

    /**
     *
     */
    fun removeTripStateListener(listener: com.hbcx.driver.interfaces.OnTripStateListener) {
        tripStateListeners.remove(listener)
    }

    /**
     * 开始定位
     */
    fun startLocation() {
        if (aMapLocationClient.isStarted) {
            aMapLocationClient.stopLocation()
        }
        aMapLocationClient.startLocation()
    }

    /**
     * 停止定位
     */
    fun stopLocation() {
        if (aMapLocationClient.isStarted) {
            aMapLocationClient.stopLocation()
        }
    }

    private fun sendHeart() {
        val userId = SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
        NettyClient.getInstance().sendMessage("{\"con\":{\"userId\":$userId,\"type\":2,\"token\":\"${this.getUUID()}\"},\"method\":\"OK\",\"code\":\"0\",\"msg\":\"SUCCESS\"}")
    }

    //司机点击新订单关闭按钮调用，展示下一个订单
    fun showNextOrder() {
        if (nextOrder==null)
            return
        val act = activities[activities.size - 1]
        if (act != null && act is TripActivity) {
            act.startActivity<NewOrderDialog>("type" to 1, "data" to nextOrder)
        } else {
            act?.startActivity<NewOrderDialog>("data" to nextOrder)
        }
        nextOrder = null
    }
}