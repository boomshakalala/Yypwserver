package com.hbcx.driver.ui.account

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.md5
import com.hbcx.driver.R
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_change_pwd.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class ChangePwdActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView(): Int = R.layout.activity_change_pwd

    override fun initClick() {
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
        btn_action.onClick {
            val s = et_pwd.text.toString().trim()
            if(s.length<6){
                toast("密码不能少于六位")
                return@onClick
            }
            com.hbcx.driver.network.HttpManager.updatePwd(s.md5(),SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)).request(this@ChangePwdActivity){ _, _->
                toast("密码修改成功")
                finish()
            }
        }
    }

    override fun initView() {
        title = "设置登录密码"
    }
}