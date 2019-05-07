package com.hbcx.driver.ui.account

import android.app.Activity
import android.content.Intent
import android.view.View
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.optDouble
import cn.sinata.xldutils.utils.optInt
import cn.sinata.xldutils.utils.optString
import com.hbcx.driver.R
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_withdraw.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class WithdrawDepositActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_withdraw
    override fun initClick() {
        btn_action.onClick {
            if (et_money.text.toString().trim().isEmpty()){
                toast("请输入提现金额")
                return@onClick
            }
            val d = et_money.text.toString().trim().toDouble()
            if (d <= 0.0){
                toast("提现金额不能小于0")
                return@onClick
            }
            if (d > enableMoney){
                toast("可提现余额不足")
                return@onClick
            }
            showDialog(canCancel = false)
            com.hbcx.driver.network.HttpManager.withdraw(userId,d).request(this@WithdrawDepositActivity){ _, _->
                toast("提现申请已提交")
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }
    private var enableMoney = 0.0 //可提现金额

    override fun initView() {
        title = "申请提现"
        titleBar.addRightButton("更换绑定",onClickListener = View.OnClickListener {
            startActivityForResult<VerifyPhoneForUpdateCardActivity>(1,"name" to intent.getStringExtra("name"),"phone" to intent.getStringExtra("phone"))
        })
        showDialog()
        getData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            getData()
        }
    }

    private fun getData(){
        showDialog()
        com.hbcx.driver.network.HttpManager.getWithdrawData(userId).request(this){ _, data->
            data?.let {
                enableMoney = it.optDouble("balance")
                tv_money_enable.text = String.format("可提现金额：￥%.2f",enableMoney)
                val num = it.optString("bankNumber")
                tv_tip.text = String.format("每周%s可申请提现上周及以前的余额，提现将于2-7个工作日内至您绑定的尾号为%s的%s！",when(it.optInt("weekDay")){
                    1->"一"
                    2->"二"
                    3->"三"
                    4->"四"
                    5->"五"
                    6->"六"
                    7->"日"
                    else -> ""
                },num.substring(num.length-4),it.optString("bankName"))
            }
        }
    }
}