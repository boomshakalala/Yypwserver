package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.view.View
import com.hbcx.driver.R
import com.hbcx.driver.ui.TranslateStatusBarActivity
import kotlinx.android.synthetic.main.activity_select_recycle.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class SelectRecycleActivity:TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_select_recycle

    override fun initClick() {
        fl_1.onClick { 
            iv_1.visibility = if (iv_1.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        fl_2.onClick {
            iv_2.visibility = if (iv_2.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        fl_3.onClick {
            iv_3.visibility = if (iv_3.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        fl_4.onClick {
            iv_4.visibility = if (iv_4.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        fl_5.onClick {
            iv_5.visibility = if (iv_5.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        fl_6.onClick {
            iv_6.visibility = if (iv_6.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        fl_7.onClick {
            iv_7.visibility = if (iv_7.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    override fun initView() {
        title = "选择周期"
        titleBar.addRightButton("确定",onClickListener = View.OnClickListener {
            val sb = StringBuilder()
            if (iv_1.visibility == View.VISIBLE)
                sb.append(1).append(",")
            if (iv_2.visibility == View.VISIBLE)
                sb.append(2).append(",")
            if (iv_3.visibility == View.VISIBLE)
                sb.append(3).append(",")
            if (iv_4.visibility == View.VISIBLE)
                sb.append(4).append(",")
            if (iv_5.visibility == View.VISIBLE)
                sb.append(5).append(",")
            if (iv_6.visibility == View.VISIBLE)
                sb.append(6).append(",")
            if (iv_7.visibility == View.VISIBLE)
                sb.append(7).append(",")
            if (sb.isNotEmpty())
                sb.deleteCharAt(sb.lastIndex)
            setResult(Activity.RESULT_OK,intent.putExtra("data",sb.toString()))
            finish()
        })
    }
}