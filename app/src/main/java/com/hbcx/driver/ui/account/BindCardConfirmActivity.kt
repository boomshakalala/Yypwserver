package com.hbcx.driver.ui.account

import android.app.Activity
import cn.sinata.xldutils.utils.SPUtils
import com.hbcx.driver.R
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_bind_card_confirm.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick

class BindCardConfirmActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_bind_card_confirm

    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }
    private val name by lazy {
        intent.getStringExtra("name")
    }
    private val num by lazy {
        intent.getStringExtra("carNum")
    }
    private val type by lazy {
        intent.getStringExtra("bankName")
    }
    override fun initClick() {
        btn_action.onClick {
            showDialog(canCancel = false)
            com.hbcx.driver.network.HttpManager.bindBankCard(userId,name,num,type).request(this@BindCardConfirmActivity){ _, _->
                val tipDialog = com.hbcx.driver.dialogs.TipDialog()
                tipDialog.arguments = bundleOf("msg" to "您的银行卡已绑定成功！","notice" to true)
                tipDialog.setDialogListener { p, s ->
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                tipDialog.show(supportFragmentManager,"bind")
            }
        }
    }

    override fun initView() {
        title = "绑定银行卡"
        tv_name.text = name
        tv_car_num.text = num
        tv_id_card.text = intent.getStringExtra("idCard")
        tv_type.text = type
    }
}