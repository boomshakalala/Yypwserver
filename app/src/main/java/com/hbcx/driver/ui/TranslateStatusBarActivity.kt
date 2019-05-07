package com.hbcx.driver.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Environment
import cn.sinata.xldutils.activity.TitleActivity
import com.hbcx.driver.R
import com.hbcx.driver.utils.StatusBarUtil
import org.jetbrains.anko.backgroundColorResource
import java.io.File

abstract class TranslateStatusBarActivity :TitleActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(setContentView())
        titleBar.setTitleColor(R.color.textColor)
        titleBar.backgroundColorResource = R.color.white
        titleBar.leftView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_arrow,0,0,0)
        StatusBarUtil.initStatus(window)
        initView()
        initClick()
    }

    abstract fun setContentView():Int
    abstract fun initClick()
    abstract fun initView()

    protected fun getPath(): String {
        val path = Environment.getExternalStorageDirectory().toString() + "/Luban/image/"
        val file = File(path)
        return if (file.mkdirs()) {
            path
        } else path
    }
}