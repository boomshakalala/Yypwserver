package com.hbcx.driver.ui.account

import android.app.Activity
import cn.sinata.xldutils.utils.hidePhone
import com.hbcx.driver.R
import com.hbcx.driver.dialogs.UpdatePhoneDialog
import kotlinx.android.synthetic.main.activity_change_phone.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class ChangePhoneActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView(): Int = R.layout.activity_change_phone

    private val phone by lazy {
        intent.getStringExtra("phone")
    }
    override fun initClick() {
        tv_change_phone.onClick {
            val updatePwdDialog = UpdatePhoneDialog()
            updatePwdDialog.show(supportFragmentManager,"phone")
        }
    }

    override fun initView() {
        title = "修改手机号"
        tv_phone.text = phone.hidePhone()
    }

    fun updatePhone(phone:String){
        tv_phone.text = phone.hidePhone()
        setResult(Activity.RESULT_OK,intent.putExtra("phone",phone))
    }
}