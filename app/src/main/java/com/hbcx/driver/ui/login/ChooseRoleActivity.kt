package com.hbcx.driver.ui.login

import android.os.Bundle
import cn.sinata.xldutils.activity.TitleActivity
import com.hbcx.driver.R
import com.hbcx.driver.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_choose_role.*
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.startActivity

class ChooseRoleActivity: TitleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_role)
        StatusBarUtil.initStatus(window)
        title = "选择角色"
        titleBar.leftView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_arrow,0,0,0)
        titleBar.backgroundColorResource = R.color.white
        titleBar.setTitleColor(R.color.textColor)
        initClick()
    }

    private fun initClick() {
        role_company.setOnClickListener {
            startActivity<com.hbcx.driver.ui.login.CompanyJoinActivity>()
        }
        role_person.setOnClickListener {
            startActivity<com.hbcx.driver.ui.login.PersonJoinActivity>()
        }
        role_driver.setOnClickListener {
            startActivity<com.hbcx.driver.ui.login.DriverJoinActivity>()
        }
    }
}