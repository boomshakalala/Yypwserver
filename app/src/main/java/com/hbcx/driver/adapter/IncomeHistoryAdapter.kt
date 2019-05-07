package com.hbcx.driver.adapter

import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.utils.toTime
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.IncomeDetail

class IncomeHistoryAdapter(data:ArrayList<IncomeDetail>,val isIncome:Boolean):HFRecyclerAdapter<com.hbcx.driver.network.beans.IncomeDetail>(data, R.layout.item_common_history) {
    override fun onBind(holder: ViewHolder, position: Int, data: IncomeDetail) {
        holder.setText(R.id.tv_title,String.format(if (isIncome) "￥%.2f" else "-￥%.2f",data.money))
        holder.setText(R.id.tv_time,data.createTime.toTime("yyyy-MM-dd HH:mm"))
        holder.setText(R.id.tv_name,data.remark)
    }
}