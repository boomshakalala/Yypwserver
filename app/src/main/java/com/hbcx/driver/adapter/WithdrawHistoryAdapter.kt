package com.hbcx.driver.adapter

import android.widget.TextView
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.utils.toTime
import com.hbcx.driver.R
import org.jetbrains.anko.textColorResource

class WithdrawHistoryAdapter(data:ArrayList<com.hbcx.driver.network.beans.WithdrawHistory>):HFRecyclerAdapter<com.hbcx.driver.network.beans.WithdrawHistory>(data, R.layout.item_common_history) {
    override fun onBind(holder: ViewHolder, position: Int, data: com.hbcx.driver.network.beans.WithdrawHistory) {
        holder.setText(R.id.tv_title,String.format("提现：￥%.2f",data.money))
        holder.setText(R.id.tv_time,data.createTime.toTime("yyyy-MM-dd HH:mm"))
        holder.setText(R.id.tv_name,data.getStatusStr())
        holder.bind<TextView>(R.id.tv_name).textColorResource = data.getStatusColorRes()
    }
}