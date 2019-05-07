package com.hbcx.driver.ui.account

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.SpanBuilder
import cn.sinata.xldutils.utils.optBoolean
import cn.sinata.xldutils.utils.optDouble
import com.hbcx.driver.R
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_wallet.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class WalletActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_wallet

    private var isBindCard = false
    private var balance = 0.0 //余额
    private val id by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }
    private val type by lazy {  //1:票务司机 0:快专车司机
        intent.getIntExtra("type",0)
    }
    override fun initClick() {
        tv_withdraw_deposit.onClick {
            if (!isBindCard){
                val tipDialog = com.hbcx.driver.dialogs.TipDialog()
                tipDialog.arguments = bundleOf("msg" to "你还未绑定银行卡， 请先绑定银行卡后再申请提现！", "ok" to "前往绑定", "cancel" to "取消")
                tipDialog.setDialogListener { p, s ->
                    startActivityForResult<com.hbcx.driver.ui.account.BindCardActivity>(1, "name" to intent.getStringExtra("name"))
                }
                tipDialog.show(supportFragmentManager, "bind")
            }else
                startActivityForResult<WithdrawDepositActivity>(2, "name" to intent.getStringExtra("name"),
                        "phone" to intent.getStringExtra("phone"),"balance" to balance)
        }

        tv_income.onClick {
            startActivity<IncomeDetailActivity>()
        }
        tv_withdraw_history.onClick {
            startActivity<WithdrawHistoryActivity>()
        }
        tv_withhold.onClick {
            startActivity<WithholdHistoryActivity>()
        }
    }

    override fun initView() {
        title = "我的钱包"
        if (type == 1){
            line.gone()
            tv_withhold.gone()
        }
        getData()
    }

    private fun getData(){
        showDialog(canCancel = false)
        com.hbcx.driver.network.HttpManager.myWallet(id).request(this){ _, data->
            data?.let {
                isBindCard = it.optBoolean("isBindingBank")
                balance = it.optDouble("balance",0.0)
                tv_money.text = balance.toString()
                val income = it.optDouble("totalIncome").toString()
                val withhold = it.optDouble("totalBuckled").toString()
                if (type == 1){
                    val s = "近30天收益￥$income"
                    tv_content.text = SpanBuilder(s)
                            .style(6,s.length,Typeface.BOLD).build()
                }else{
                    val s = "近30天收益￥$income，因改派等原因扣款￥$withhold"
                    tv_content.text = SpanBuilder(s)
                            .style(6,income.length+7,Typeface.BOLD)
                            .style(s.length-withhold.length-1,s.length,Typeface.BOLD).build()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            getData()
    }
}