package com.hbcx.driver.dialogs

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.sinata.xldutils.activity.BaseActivity
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.isValidPhone
import cn.sinata.xldutils.utils.screenWidth
import com.hbcx.driver.R
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.dialog_update_pwd.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

class UpdatePhoneDialog : DialogFragment() {
    private val timer by lazy {
        object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                tv_get_code.isEnabled = true
                tv_get_code.text = "重新获取"
            }

            override fun onTick(millisUntilFinished: Long) {
                tv_get_code.text = "${millisUntilFinished/1000}s"
            }
        }
    }

    private val driverId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FadeDialog)
        dialog.window.setLayout((screenWidth() * 0.85).toInt(), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.dialog_update_pwd, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_get_code.onClick {
            sendSms()
        }
        iv_close.onClick {
            dialog.dismiss()
        }
        tv_action.onClick {
            val phone = et_phone.text.toString().trim()
            val code = et_code.text.toString().trim()
            if (phone.isEmpty()) {
                toast("手机号不能为空")
                return@onClick
            }
            if (!phone.isValidPhone()) {
                toast("请输入正确手机号")
                return@onClick
            }
            if (code.length!=6){
                toast("请输入6位验证码")
                return@onClick
            }
            val activity = activity as com.hbcx.driver.ui.account.ChangePhoneActivity
            com.hbcx.driver.network.HttpManager.updatePhone(phone,code,driverId).request(activity){ _, _->
                toast("修改成功")
                activity.updatePhone(phone)
                dialog.dismiss()
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
        com.hbcx.driver.network.HttpManager.sendSms(phone, 5).request(activity as BaseActivity) { msg, _ ->
            toast(msg.toString())
            tv_get_code.isEnabled = false
            timer.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}