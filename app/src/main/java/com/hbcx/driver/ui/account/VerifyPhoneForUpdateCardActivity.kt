package com.hbcx.driver.ui.account

import android.os.CountDownTimer
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_verify_phone_for_card.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class VerifyPhoneForUpdateCardActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_verify_phone_for_card

    override fun initClick() {
        tv_get_code.onClick {
            sendSms()
        }
        btn_action.onClick {
            val code = et_code.text.toString().trim()
            if (code.length!=6) {
                toast("请输入六位验证码")
                return@onClick
            }
            HttpManager.checkCode(phone, code, 6).request(this@VerifyPhoneForUpdateCardActivity) { _, _ ->
                toast("验证成功")
                startActivity<BindCardActivity>("name" to intent.getStringExtra("name"))
                finish()
            }
        }
    }

    private val phone by lazy {
        intent.getStringExtra("phone")
    }
    override fun initView() {
        title = "更换银行卡"
        et_phone.text = phone
    }

    private val countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
        override fun onFinish() {
            tv_get_code.text = "重新获取"
            tv_get_code.isEnabled = true
        }

        override fun onTick(millisUntilFinished: Long) {
            tv_get_code.text = String.format("%s", millisUntilFinished / 1000)
        }
    }

    private fun sendSms() {
        tv_get_code.isEnabled = false
        HttpManager.sendSms(phone, 6).request(this, success = { msg, _ ->
            if (isDestroy) {
                return@request
            }
            toast(msg.toString())
            countDownTimer.start()
        }, error = { _, _->
            tv_get_code.isEnabled = true
        })
    }
}