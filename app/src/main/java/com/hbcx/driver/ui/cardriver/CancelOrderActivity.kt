package com.hbcx.driver.ui.cardriver

import android.app.Activity
import android.view.View
import android.widget.RadioButton


import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_cancel_order.*
import com.hbcx.driver.R
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * 取消订单
 */
class CancelOrderActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView(): Int = R.layout.activity_cancel_order

    override fun initClick() {
        rg.setOnCheckedChangeListener { group, checkedId ->
            reason = find<RadioButton>(checkedId).text.toString()
            if (checkedId == R.id.rb_8) {//其他理由
                reason = "其他"
            }
        }

        tv_action.setOnClickListener {
            if (reason.isEmpty()) {
                toast("请选择取消原因")
                return@setOnClickListener
            }
            if (rg.checkedRadioButtonId == R.id.rb_8) {
                val content = et_content.text.toString()
                if (content.isEmpty()) {
                    toast("请输入详细描述")
                    return@setOnClickListener
                }
            }
            submit()
        }
    }

    override fun initView() {
        title = "申请改派"
        titleBar.addRightButton("改派说明",onClickListener = View.OnClickListener{
            startActivity<com.hbcx.driver.ui.H5Activity>("title" to "改派说明", "url" to com.hbcx.driver.network.Api.CALCEL_RULE)
        })
    }

    private val id by lazy {
        intent.getIntExtra("orderId",0)
    }

    private var reason = ""


    private fun submit() {
        showDialog()
        val content = et_content.text.toString()
        com.hbcx.driver.network.HttpManager.cancelOrder(id,reason,content).request(this){ _, _->
            toast("订单已改派！")
            setResult(Activity.RESULT_OK)
            finish()
        }
    }


}
