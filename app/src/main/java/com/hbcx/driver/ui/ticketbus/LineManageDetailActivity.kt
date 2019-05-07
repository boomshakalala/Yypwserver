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
import com.hbcx.driver.network.beans.Line
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_line_manage_detail.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class LineManageDetailActivity : TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_line_manage_detail

    private val tipDialog by lazy {
        TipDialog()
    }

    override fun initClick() {
        tv_set_price.onClick {
            startActivity<SetLinePriceActivity>("id" to id, "editable" to false)
        }
        btn_action.onClick {
            it as Button
            tipDialog.arguments = bundleOf("msg" to (if (it.text == "上架") "你确定要上架这条线路吗？" else "你确定要下架这条线路吗？"), "ok" to "确定", "cancel" to "取消")
            tipDialog.setDialogListener { p, s ->
                showDialog()
                HttpManager.upDownLine(id).request(this@LineManageDetailActivity) { _, _ ->
                    toast(if (it.text == "上架") "上架成功" else "下架成功")
                    setResult(Activity.RESULT_OK)
                    getData()
                }
            }
            tipDialog.show(supportFragmentManager, "line")
        }
    }

    private val name by lazy {
        intent.getStringExtra("title")
    }
    private val id by lazy {
        intent.getIntExtra("id", 0)
    }

    private var data: Line? = null
    override fun initView() {
        title = name
        titleBar.addRightButton("编辑", onClickListener = View.OnClickListener { _ ->
            if (data != null)
                startActivityForResult<EditLineDetailActivity>(1, "data" to data)
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
        HttpManager.lineDetail(id).request(this) { _, data ->
            data?.let {
                this.data = it
                tv_time.text = it.createTime.toTime("yyyy-MM-dd HH:mm")
                tv_name.text = it.lineName
                tv_type.text = it.typeName
                tv_start.text = it.startStationName
                tv_end.text = it.endStationName
                tv_points.text = it.stationName
                tv_up_points.text = it.stationUpName
                tv_count.text = it.num.toString()
                tv_money.text = String.format("￥%.2f", it.salesMoney)
                tv_status.text = it.getStatusStr()
                when (it.status) {
                    3 -> {
                        btn_action.visible()
                        btn_action.text = "下架"
                    }
                    4 -> {
                        btn_action.visible()
                        btn_action.text = "上架"
                    }
                    else ->
                        btn_action.gone()
                }
                if (it.lineType == -1)
                    ll_price.visible()
                else
                    ll_price.gone()
            }
        }
    }
}