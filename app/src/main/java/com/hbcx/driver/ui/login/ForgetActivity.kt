package com.hbcx.driver.ui.login

import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import cn.sinata.xldutils.activity.TitleActivity
import cn.sinata.xldutils.utils.isValidPhone
import cn.sinata.xldutils.utils.md5
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.StatusBarUtil
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_forget.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.toast

class ForgetActivity : TitleActivity() {
    private val countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
        override fun onFinish() {
            tv_get_code.text = "重新获取"
            tv_get_code.isEnabled = true
        }

        override fun onTick(millisUntilFinished: Long) {
            tv_get_code.text = String.format("%s", millisUntilFinished / 1000)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)
        title = "忘记密码"
        titleBar.setTitleColor(R.color.textColor)
        titleBar.leftView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_arrow, 0, 0, 0)
        titleBar.backgroundColor = resources.getColor(R.color.white)
        StatusBarUtil.initStatus(window)
        initClick()
    }

    private fun initClick() {
        tv_hide.setOnClickListener {
            if (et_pwd.tag == "1") { //show
                et_pwd.tag = "0"
                tv_hide.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_hide, 0)
                et_pwd.transformationMethod = PasswordTransformationMethod.getInstance()
                et_pwd.setSelection(et_pwd.text.length)
            } else if (et_pwd.tag == "0") { //hide
                et_pwd.tag = "1"
                tv_hide.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_show, 0)
                et_pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
                et_pwd.setSelection(et_pwd.text.length)
            }
        }

        tv_get_code.setOnClickListener {
            sendSms()
        }
        btn_reset.setOnClickListener {
            val phone = et_phone.text.toString().trim()
            if (!phone.isValidPhone()) {
                toast("请输入正确手机号")
                return@setOnClickListener
            }
            val code = et_code.text.toString().trim()
            if (code.length!=6) {
                toast("请输入六位验证码")
                return@setOnClickListener
            }
            val pwd = et_pwd.text.toString().trim()
            if (pwd.length<6){
                toast("请输入六位以上密码")
                return@setOnClickListener
            }
            com.hbcx.driver.network.HttpManager.forgetPwd(phone,pwd.md5(),code).request(this){ _, _->
                toast("密码重置成功")
                finish()
            }
        }
    }

    private fun sendSms() {
        val phone = et_phone.text.toString().trim()
        if (phone.isEmpty()) {
            toast("手机号不能为空")
            return
        }
        if (!phone.isValidPhone()) {
            toast("请输入正确手机号")
            return
        }
        tv_get_code.isEnabled = false
        HttpManager.sendSms(phone, 6).request(this,success = { msg, _ ->
            if (isDestroy) {
                return@request
            }
            toast(msg.toString())
            countDownTimer.start()
        },error = {_,_->
            tv_get_code.isEnabled = true
        })
    }
}