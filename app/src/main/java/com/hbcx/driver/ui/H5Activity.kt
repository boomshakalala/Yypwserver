package com.hbcx.driver.ui

import android.os.Bundle
import cn.sinata.xldutils.activity.WebViewActivity
import com.hbcx.driver.R
import com.hbcx.driver.utils.StatusBarUtil

import org.jetbrains.anko.backgroundColorResource

class H5Activity :WebViewActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.initStatus(window)
        titleBar.setTitleColor(R.color.textColor)
        titleBar.backgroundColorResource = R.color.white
        titleBar.leftView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_arrow,0,0,0)
    }
}