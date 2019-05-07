package com.hbcx.driver.ui.ticketbus

import com.hbcx.driver.R
import com.hbcx.driver.ui.TranslateStatusBarActivity
import kotlinx.android.synthetic.main.activity_line_manage.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

class BusLineManageActivity:TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_line_manage

    override fun initClick() {
        tv_line_manage.onClick {
            startActivity<LineManageActivity>()
        }
        tv_class_manage.onClick {
            startActivity<ClassManageActivity>()
        }
    }

    override fun initView() {
        title = "线路班次管理"
    }
}