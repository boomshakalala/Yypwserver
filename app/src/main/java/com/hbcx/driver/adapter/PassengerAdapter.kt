package com.hbcx.driver.adapter

import android.view.View
import android.widget.TextView
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.callPhone
import cn.sinata.xldutils.utils.toTime
import com.hbcx.driver.R
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.textColorResource

class PassengerAdapter(data:ArrayList<com.hbcx.driver.network.beans.Passenger>, val activity: com.hbcx.driver.ui.ticketbus.PassengersActivity):HFRecyclerAdapter<com.hbcx.driver.network.beans.Passenger>(data, R.layout.item_passenger) {
    override fun onBind(holder: ViewHolder, position: Int, data: com.hbcx.driver.network.beans.Passenger) {
        holder.setText(R.id.tv_time,data.createTime.toTime("yyyy-MM-dd HH:mm"))
        holder.setText(R.id.tv_status,if (data.status == 2) "待上车" else "已上车")
        holder.setText(R.id.tv_start,data.pointUpName)
        holder.setText(R.id.tv_end,data.pointDownName)
        holder.setText(R.id.tv_name,data.nickName)
        holder.setText(R.id.tv_person_count,String.format("%d人乘车",data.num))
        holder.bind<TextView>(R.id.tv_status).textColorResource = if (data.status == 2) R.color.colorOrange else R.color.grey
        holder.bind<View>(R.id.tv_action).setOnClickListener {
            val tipDialog = com.hbcx.driver.dialogs.TipDialog()
            tipDialog.arguments = bundleOf("msg" to "是否确认联系该乘客?","ok" to "确认","cancel" to "取消")
            tipDialog.setDialogListener { p, s ->
                activity.callPhone(data.phone)
            }
            tipDialog.show(activity.supportFragmentManager,"call")
        }
    }
}