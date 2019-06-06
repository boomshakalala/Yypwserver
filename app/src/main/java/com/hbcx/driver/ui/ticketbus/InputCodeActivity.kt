package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import cn.sinata.xldutils.utils.SPUtils
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_input_code.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class InputCodeActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_input_code

    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }
    private val tipDialog by lazy {
        val dialog = com.hbcx.driver.dialogs.TipDialog()
        dialog.setCancelDialogListener { p, s ->
            setResult(Activity.RESULT_OK)
            finish()
        }
        dialog
    }

        override fun initClick() {
        btn_commit.onClick {
           val s = et_code.text.toString()
            if (s.isEmpty()){
                toast("请输入验票码")
                return@onClick
            }
            HttpManager.getTicketDetail(s, userId, null).request(this@InputCodeActivity,false, success = { _, data ->
                if (data != null){
                    startActivity<com.hbcx.driver.ui.ticketbus.TicketDetailActivity>("data" to data)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }, error = { _, msg ->
                tipDialog.arguments = bundleOf("msg" to msg,"ok" to "再次验票", "cancel" to "取消验票")
                tipDialog.show(supportFragmentManager, "ticket")
            })
        }
    }

    override fun initView() {
        title = "扫码验票"
    }
}