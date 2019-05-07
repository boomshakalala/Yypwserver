package com.hbcx.driver.ui.account

import android.os.CountDownTimer
import cn.sinata.xldutils.utils.hidePhone
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_verify_phone_for_pwd.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class VerifyPhoneForUpdatePwdActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_verify_phone_for_pwd

    private val countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
        override fun onFinish() {
            tv_get_code.text = "重新获取"
            tv_get_code.isEnabled = true
        }

        override fun onTick(millisUntilFinished: Long) {
            tv_get_code.text = String.format("%ss", millisUntilFinished / 1000)
        }
    }
    private val phone by lazy {
        intent.getStringExtra("phone")
    }
    override fun initClick() {
        tv_get_code.onClick {
            sendSms()
        }
        btn_action.onClick {
            val s = et_code.text.toString()
            if (s.length!=6){
                toast("请输入六位验证码")
            }else{
                HttpManager.checkCode(phone, s, 6).request(this@VerifyPhoneForUpdatePwdActivity) { _, _ ->
                    toast("验证成功")
                    startActivity<com.hbcx.driver.ui.account.ChangePwdActivity>()
                    finish()
                }

            }
        }
    }

    override fun initView() {
        title = "安全验证"
        tv_phone.text = phone.hidePhone()
    }

    private fun sendSms() {
        showDialog()
        com.hbcx.driver.network.HttpManager.sendSms(phone, 6).request(this) { msg, _ ->
            toast(msg.toString())
            tv_get_code.isEnabled = false
            countDownTimer.start()
        }
    }
}