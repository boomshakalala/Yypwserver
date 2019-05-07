package com.hbcx.driver.ui.ticketbus

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import cn.sinata.rxnetty.NettyClient
import cn.sinata.xldutils.activity.BaseActivity
import cn.sinata.xldutils.callPhone
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.utils.*
import cn.sinata.xldutils.visible
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.ui.account.MessageActivity
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.StatusBarUtil
import com.hbcx.driver.utils.request
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_bus_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class BusMainActivity : BaseActivity() {
    private val fragments = arrayListOf<Fragment>()
    private val type by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_TYPE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_main)
        StatusBarUtil.initStatus(window)
        RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE).subscribe { }
        initView()
        initClick()
    }

    private fun initClick() {
        tv_history.onClick {
            startActivity<HistoryActivity>()
        }
        tv_scan.onClick {
            RxPermissions(this@BusMainActivity).request(Manifest.permission.CAMERA).subscribe {
                if (!it){
                    toast("请开启相机权限")
                }else
                    startActivity<ScanQrCodeActivity>()
            }
        }
        iv_menu.onClick {
            if (type == 2)
                startActivity<BusDriverMenuActivity>()
            else
                startActivity<TicketMenuActivity>()
        }
        iv_msg.onClick {
            startActivity<MessageActivity>()
        }
        HttpManager.getServicePhone().request(this){_,data->
            data?.let {
                val phone = it.optString("phone")
                tv_police.setOnClickListener {
                    callPhone(phone)
                }
            }
        }
    }

    private fun initView() {
        NettyClient.getInstance().startService()
        HttpManager.getEnableDayCount().request(this) { _, data ->
            val enableDays = data?.optInt("days", 0) ?: 0
            val times = arrayOfNulls<String>(enableDays)
            (0 until enableDays).forEach {
                val l = System.currentTimeMillis() + 24 * 60 * 60 * 1000 * it
                times[it] = l.toTime("MM-dd") + "\n" + l.toWeek("星期")
                fragments.add(TicketBusFragment.newInstance(l))
            }
            mTabLayout.setViewPager(mViewPager, times, this, fragments)
            Log.e("mmp",times.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        hasNewMsg()
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
}