package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Button
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.utils.toTime
import cn.sinata.xldutils.visible
import com.hbcx.driver.R
import com.hbcx.driver.dialogs.TipDialog
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.ClassModel
import com.hbcx.driver.network.beans.Line
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_class_manage_detail.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class ClassManageDetailActivity : TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_class_manage_detail

    private val tipDialog by lazy {
        TipDialog()
    }

    override fun initClick() {
        btn_action.onClick {
            it as Button
            tipDialog.arguments = bundleOf("msg" to (if (it.text == "上架") "你确定要上架这条班次吗？" else "你确定要下架这条班次吗？"), "ok" to "确定", "cancel" to "取消")
            tipDialog.setDialogListener { p, s ->
                showDialog()
                HttpManager.upDownClass(id).request(this@ClassManageDetailActivity) { _, _ ->
                    toast(if (it.text == "上架") "上架成功" else "下架成功")
                    getData()
                }
            }
            tipDialog.show(supportFragmentManager, "line")
        }
    }

    private val id by lazy {
        intent.getIntExtra("id", 0)
    }

    private var data: ClassModel? = null
    override fun initView() {
        title = "线路班次管理"
        titleBar.addRightButton("编辑", onClickListener = View.OnClickListener { _ ->
            if (data != null)
                startActivityForResult<EditClassDetailActivity>(1, "data" to data)
        })
        getData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK)
            getData()
        }
    }

    private fun getData() {
        HttpManager.classDetail(id).request(this) { _, data ->
            data?.let {
                this.data = it
                tv_add_time.text = it.createTime.toTime("yyyy-MM-dd HH:mm")
                tv_name.text = it.name
                tv_line.text = it.lineName
                tv_car.text = it.licensePlate
                tv_time.text = it.startTime
                val sb = StringBuilder()
                it.weeks.split(",").forEach {
                    sb.append(when (it) {
                        "1" -> "周一,"
                        "2" -> "周二,"
                        "3" -> "周三,"
                        "4" -> "周四,"
                        "5" -> "周五,"
                        "6" -> "周六,"
                        "7" -> "周日,"
                        else -> ""
                    })
                }
                if (sb.isNotEmpty())
                    sb.deleteCharAt(sb.lastIndex)
                tv_recycle.text = sb.toString()
                tv_count.text = it.num.toString()
                tv_all_count.text = it.totalNum.toString()
                tv_money.text = String.format("￥%.2f", it.salesMoney)
                tv_status.text = it.getStatusStr()
                when (it.status) {
                    1 -> {
                        btn_action.visible()
                        btn_action.text = "下架"
                    }
                    3 -> {
                        btn_action.visible()
                        btn_action.text = "上架"
                    }
                    else ->
                        btn_action.gone()
                }
            }
        }
    }
}