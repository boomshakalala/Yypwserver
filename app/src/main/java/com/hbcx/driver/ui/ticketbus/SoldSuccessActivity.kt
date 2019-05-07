package com.hbcx.driver.ui.ticketbus

import com.hbcx.driver.R
import kotlinx.android.synthetic.main.activity_sold_success.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity

class SoldSuccessActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_sold_success

    override fun initClick() {
        btn_back.onClick {
            startActivity<com.hbcx.driver.ui.ticketbus.BusMainActivity>()
        }
    }

    override fun initView() {
        title = "成功售票"
        titleBar.showLeft(false)
    }

    override fun onBackPressed() {
        startActivity<com.hbcx.driver.ui.ticketbus.BusMainActivity>()
    }
}