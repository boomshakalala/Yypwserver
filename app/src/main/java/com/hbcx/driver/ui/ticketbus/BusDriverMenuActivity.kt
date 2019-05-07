package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import cn.sinata.rxnetty.NettyClient
import cn.sinata.xldutils.callPhone
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.SpanBuilder
import cn.sinata.xldutils.utils.optString
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.ui.account.FeedbackActivity
import com.hbcx.driver.ui.account.IncomeDetailActivity
import com.hbcx.driver.utils.request
import com.umeng.socialize.UMShareAPI
import kotlinx.android.synthetic.main.activity_bus_driver_menu.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class BusDriverMenuActivity: TranslateStatusBarActivity() {
    private val type by lazy { //3=个人，4=公司
        intent.getIntExtra("type",3)
    }
    override fun setContentView(): Int = R.layout.activity_bus_driver_menu

    private val id by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }

    private val app by lazy {
        application as com.hbcx.driver.YypwApplication
    }

    private var phone = ""

    override fun initClick() {
        tv_account_safe.setOnClickListener {
            if (phone.isEmpty())
                return@setOnClickListener
            startActivityForResult<com.hbcx.driver.ui.account.AccountSafeActivity>(1,"phone" to phone)
        }

        tv_logout.setOnClickListener {
            val tipDialog = com.hbcx.driver.dialogs.TipDialog()
            tipDialog.arguments = bundleOf("msg" to "是否确定退出当前账户?","cancel" to "取消")
            tipDialog.setDialogListener { _, _ ->
                app.stopLocation()
                NettyClient.getInstance().stopService()
                SPUtils.instance().put(com.hbcx.driver.utils.Const.User.USER_ID, -1).apply()
                app.exitToLogin()
            }
            tipDialog.show(supportFragmentManager,"logout")
        }
        tv_deal_detail.onClick {
            startActivity<IncomeDetailActivity>()
        }
        tv_feedback.onClick{
            startActivity<FeedbackActivity>()
        }
        tv_share_friends.onClick {
            val inviteDialog = com.hbcx.driver.dialogs.InviteDialog()
            inviteDialog.show(supportFragmentManager,"share")
        }
        tv_about_us.onClick {
            startActivity<com.hbcx.driver.ui.H5Activity>("title" to "关于我们","url" to com.hbcx.driver.network.Api.ABOUT)
        }
    }

    override fun initView() {
        title = "我的主页"
        getData()
    }

    private fun getData(){
        HttpManager.ticketDriverHome(id).request(this){ _, data->
            data?.let {
                headImg.setImageURI(it.get("imgUrl").asString)
                tv_name.text = it.get("nickName").asString
                tv_name.isSelected = it.get("sex").asInt != 1
                tv_car_info.text = "${it.optString("licensePlate")}  ${it.optString("brandName")}${it.optString("modelName")}"
                val s = it.get("shiftNum").asString
                tv_order_count.text = SpanBuilder("$s\n服务班次").size(0,s.length,16).style(0,s.length,Typeface.BOLD).build()
                val s1 = it.get("favorableRate").asString
                tv_praise.text = SpanBuilder("$s1%\n好评率").size(0,s1.length,16).style(0,s1.length,Typeface.BOLD).build()
                val s2 = it.optString("peoNum","0")
                tv_money.text = SpanBuilder("$s2\n载客").size(0,s2.length,16).style(0,s2.length,Typeface.BOLD).build()
                phone = it.get("phone").asString
            }
        }
        HttpManager.getServicePhone().request(this){_,data->
            data?.let {
                val phone = it.optString("phone")
                tv_service_phone.text = phone
                fl_services.setOnClickListener {
                    callPhone(phone)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode== Activity.RESULT_OK&&data!=null){
            if (requestCode == 1){
                phone = data.getStringExtra("phone")
            }
        }
    }
}