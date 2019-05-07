package com.hbcx.driver.adapter

import android.view.View
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.utils.toTime
import com.hbcx.driver.R

class HistoryAdapter(data:ArrayList<com.hbcx.driver.network.beans.TicketBus>):HFRecyclerAdapter<com.hbcx.driver.network.beans.TicketBus>(data, R.layout.item_history) {
    override fun onBind(holder: ViewHolder, position: Int, data: com.hbcx.driver.network.beans.TicketBus) {
        holder.setText(R.id.tv_title,data.lineName)
        holder.setText(R.id.tv_start,data.startStationName)
        holder.setText(R.id.tv_end,data.endStationName)
        holder.setText(R.id.tv_time,data.rideDate.toTime("yyyy-MM-dd HH:mm"))
        holder.setText(R.id.tv_person_count,String.format("乘坐：%d人",data.peoNum))
        holder.setText(R.id.tv_station_num,String.format("%d站",data.stationNum))
        holder.bind<View>(R.id.tv_action).setOnClickListener {
            callback?.onDelete(data.id)
        }
    }


    interface OnDeleteCallback{
        fun onDelete(id:Int)
    }

    private var callback:OnDeleteCallback? = null

    fun setOnDelete(l:(id:Int)->Unit){
        this.callback = object :OnDeleteCallback{
            override fun onDelete(id: Int) {
                l(id)
            }
        }
    }
}