package com.hbcx.driver.ui.account

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import cn.sinata.xldutils.utils.SPUtils
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_feedback.*
import org.jetbrains.anko.toast

class FeedbackActivity: com.hbcx.driver.ui.TranslateStatusBarActivity(), TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        if (s!=null){
            tv_count.text = "还可以输入${200 - s.length}字"
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun setContentView() = R.layout.activity_feedback

    override fun initClick() {
    }

    override fun initView() {
        title = "意见反馈"
        titleBar.addRightButton("提交",onClickListener = View.OnClickListener {
            val content = et_content.text.toString()
            if (content.isEmpty()){
                toast("请输入内容")
                return@OnClickListener
            }
            titleBar.getRightButton(0)?.isEnabled = false
            HttpManager.feedback(SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID),content,2)
                    .request(this, success = { _, _->
                        toast("提交成功")
                        finish()
                    }, error = { _, _->
                        titleBar.getRightButton(0)?.isEnabled = true
                    })
        })
        et_content.addTextChangedListener(this)
    }
}