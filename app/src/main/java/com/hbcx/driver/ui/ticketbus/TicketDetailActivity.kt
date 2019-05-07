package com.hbcx.driver.ui.ticketbus

import android.view.View
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.toTime
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_ticket_detail.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class TicketDetailActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {

    override fun setContentView() = R.layout.activity_ticket_detail

    private val data by lazy {
        intent.getSerializableExtra("data") as com.hbcx.driver.network.beans.TicketDetail
    }

    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }

    override fun initClick() {
        btn_action.onClick {
            val tipDialog = com.hbcx.driver.dialogs.TipDialog()
            tipDialog.arguments = bundleOf("msg" to "是否确认乘客已上车？","ok" to "确认","cancel" to "取消")
            tipDialog.setDialogListener { p, s ->
                HttpManager.confirmRide(data.id,userId).request(this@TicketDetailActivity){ _, data->
                    toast("确认成功")
                    startActivity<BusMainActivity>()
                }
            }
            tipDialog.show(supportFragmentManager,"ok")
        }
    }

    override fun initView() {
        title = "车票详情"
        tv_start.text = data.pointUpName
        tv_end.text = data.pointDownName
        tv_time.text = String.format("%s上车",data.times)
        tv_name.text = data.nickName
        tv_buy_time.text = data.createTime.toTime("yyyy-MM-dd HH:mm")
        tv_passenger_count.text = String.format("%d人",data.passengerList.size)
        tv_phone.text = data.phone
        iv_head.setImageURI(data.imgUrl)
        btn_action.visibility = if (data.canUp) View.VISIBLE else View.GONE
        tv_tip.visibility = if (data.canUp) View.GONE else View.VISIBLE
    }
}