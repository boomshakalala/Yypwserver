package com.hbcx.driver.ui.login

import android.os.Bundle
import android.util.Log
import cn.jpush.android.api.JPushInterface
import cn.jpush.android.api.TagAliasCallback
import cn.sinata.xldutils.activity.TitleActivity
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.isValidPhone
import cn.sinata.xldutils.utils.md5
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.ui.cardriver.DriverMainActivity
import com.hbcx.driver.ui.ticketbus.BusMainActivity
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.StatusBarUtil
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity :TitleActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "伙伴出行服务"
        StatusBarUtil.initStatus2(window)
        if (SPUtils.instance().getInt(Const.User.USER_ID)!=-1){
            when(SPUtils.instance().getInt(Const.User.USER_TYPE)){
                1->{ //快专车
                    startActivity<DriverMainActivity>()
                    finish()
                }
                2,3->{ //票务：个人2  公司3
                    startActivity<BusMainActivity>()
                    finish()
                }
            }
            finish()
        }
        rootLayout.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        titleBar.showLeft(false)
        titleBar.setTitleColor(R.color.white)
        titleBar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        initClick()
    }

    private fun initClick(){
        tv_forget.setOnClickListener {
            startActivity<ForgetActivity>()
        }

        tv_join.setOnClickListener {
            startActivity<ChooseRoleActivity>()
        }

        btn_login.setOnClickListener {
            val phone = et_phone.text.toString().trim()
            if (!phone.isValidPhone()){
                toast("请输入正确的手机号")
                return@setOnClickListener
            }
            val pwd = et_pwd.text.toString().trim()
            if (pwd.length<6){
                toast("请输入六位以上密码")
                return@setOnClickListener
            }
            HttpManager.login(phone,pwd.md5()).request(this){ _, data->
                SPUtils.instance().put(Const.User.USER_ID,data?.get("id")!!.asInt).apply()
                SPUtils.instance().put(Const.User.USER_TYPE, data.get("type")!!.asInt).apply()
                toast("登录成功")
                JPushInterface.setAlias(this@LoginActivity,0,phone)
                when(data.get("type").asInt){
                    1->{ //快专车
                        startActivity<DriverMainActivity>()
                        finish()
                    }
                    2,3->{ //票务
                        startActivity<BusMainActivity>()
                        finish()
                    }
                }
                finish()
            }
        }
    }
}