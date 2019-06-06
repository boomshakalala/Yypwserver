package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import cn.sinata.xldutils.utils.SPUtils
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.request
import com.uuzuche.lib_zxing.activity.CaptureFragment
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_scan_qrcode.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class ScanQrCodeActivity : com.hbcx.driver.ui.TranslateStatusBarActivity(), CodeUtils.AnalyzeCallback {
    private val tipDialog by lazy {
        val dialog = com.hbcx.driver.dialogs.TipDialog()
        dialog.setCancelDialogListener { p, s ->
            finish()
        }
        dialog.setDialogListener{_,_->
            initView()
        }
        dialog
    }
    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }

    override fun onAnalyzeSuccess(mBitmap: Bitmap?, result: String?) {
        if (result == null || !result.startsWith("YunYou:")) { //解析结果为非法二维码
            tipDialog.arguments = bundleOf("msg" to "暂未查询出车票信息， 请核验验票码是否正确！","ok" to "再次验票", "cancel" to "取消验票")
            tipDialog.show(supportFragmentManager, "ticket")
        }else {
            val id = result.substring(7)
            HttpManager.getTicketDetail(id, userId, null).request(this, false, success = { _, data ->
                if (data != null){
                    startActivity<TicketDetailActivity>("data" to data,"id" to id)
                    finish()
                }
            }, error = { _, msg ->
                tipDialog.arguments = bundleOf("msg" to msg,"ok" to "再次验票", "cancel" to "取消验票")
                tipDialog.show(supportFragmentManager, "ticket")
            })
        }

    }

    override fun onAnalyzeFailed() {
        toast("获取车票信息失败，请尝试手动输入验票码")
    }

    override fun setContentView() = R.layout.activity_scan_qrcode

    override fun initClick() {
        tv_input_code.onClick {
            startActivityForResult<com.hbcx.driver.ui.ticketbus.InputCodeActivity>(1)
        }
        cb_flashlight.setOnCheckedChangeListener { _, isChecked ->
            CodeUtils.isLightEnable(isChecked)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) //验票成功或者取消验票
            finish()
    }
    override fun initView() {
        title = "扫码购票"
        val captureFragment= CaptureFragment()
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera)
        captureFragment.analyzeCallback = this
        supportFragmentManager.beginTransaction().replace(R.id.fl_my_container, captureFragment).commit()
    }
}