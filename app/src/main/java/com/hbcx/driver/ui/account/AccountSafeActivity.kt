package com.hbcx.driver.ui.account

import android.app.Activity
import android.content.Intent
import cn.sinata.xldutils.utils.hidePhone
import com.hbcx.driver.R
import kotlinx.android.synthetic.main.activity_account_safe.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class AccountSafeActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView(): Int = R.layout.activity_account_safe

    private var phone = ""
    override fun initClick() {
        ll_change_phone.setOnClickListener {
            startActivityForResult<ChangePhoneActivity>(1,"phone" to phone)
        }

        tv_pwd.setOnClickListener {
            startActivity<VerifyPhoneForUpdatePwdActivity>("phone" to phone)
        }
    }

    override fun initView() {
        title = "账户安全"
        phone = intent.getStringExtra("phone")
        tv_phone.text = phone.hidePhone()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode== Activity.RESULT_OK&&data!=null){
            if (requestCode == 1){
                phone = data.getStringExtra("phone")
                setResult(Activity.RESULT_OK,data)
            }
        }
    }
}