package com.hbcx.driver.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.hbcx.driver.R

class RoleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    private var iconRes: Int = 0
    private var title: String? = null
    private var describe: String? = null

    init {
        initView(context, attrs, defStyleAttr)
    }

    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.view_role, this, true)
        val ivIcon = view.findViewById<ImageView>(R.id.icon)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val tvDescribe = view.findViewById<TextView>(R.id.tv_describe)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoleView)
        iconRes = typedArray.getResourceId(R.styleable.RoleView_icon, 0)
        title = typedArray.getString(R.styleable.RoleView_title)
        describe = typedArray.getString(R.styleable.RoleView_describe)
        typedArray.recycle()

        ivIcon.setImageResource(iconRes)
        tvTitle.text = title
        tvDescribe.text = describe
    }
}
