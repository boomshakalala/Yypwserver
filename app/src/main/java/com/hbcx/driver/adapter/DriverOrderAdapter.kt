package com.hbcx.driver.adapter

import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.utils.timeDay
import com.hbcx.driver.R

class DriverOrderAdapter(data: ArrayList<com.hbcx.driver.network.beans.OrderList>): HFRecyclerAdapter<com.hbcx.driver.network.beans.OrderList>(data, R.layout.item_order_main) {
    override fun onBind(holder: ViewHolder, position: Int, data: com.hbcx.driver.network.beans.OrderList) {
        holder.setText(R.id.tv_name,when(data.type){
            1-> "快车"
            2-> "专车经济"
            3-> "专车舒适"
            4-> "专车商务"
            else->""
        })
        holder.setText(R.id.tv_state,when(data.status){
            2->"待接驾"
            3->"待乘客上车"
            4->"服务中"
            5->"待支付"
            6->"取消待支付"
            7->"待评价"
            8->"已完成"
            9->"已取消"
            else->""
        })
        holder.setText(R.id.tv_time,data.createTime.timeDay())
        holder.setText(R.id.tv_start,data.startAddress)
        holder.setText(R.id.tv_end,data.endAddress)
    }
}