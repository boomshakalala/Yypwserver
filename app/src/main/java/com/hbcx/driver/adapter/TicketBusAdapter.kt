package com.hbcx.driver.adapter

import android.widget.TextView
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.visible
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.TicketBus
import org.jetbrains.anko.sdk25.coroutines.onClick

class TicketBusAdapter(data:ArrayList<TicketBus>, private val canStart:Boolean, private val listener:OnItemClickListener):HFRecyclerAdapter<TicketBus>(data, R.layout.item_ticket_bus) {
    override fun onBind(holder: ViewHolder, position: Int, data: TicketBus) {
        holder.setText(R.id.tv_title,data.lineName)
        holder.setText(R.id.tv_status,data.getStatusStr())
        holder.setText(R.id.tv_start,data.startStationName)
        holder.setText(R.id.tv_end,data.endStationName)
        holder.setText(R.id.tv_time_and_count,String.format("%s 购票：%d人",data.start_time,data.peoNum1))
        holder.setText(R.id.tv_station_count,String.format("%d站",data.stationNum))
        holder.setText(R.id.tv_person_count,String.format("已坐：%d人",data.peoNum))
        holder.setText(R.id.tv_action,data.getActionStr())

        holder.bind<TextView>(R.id.tv_passengers).onClick {
            listener.onPassengerList(data.id)
        }
        holder.bind<TextView>(R.id.tv_sale_ticket).onClick {
            listener.onSaleTicket(data)
        }
        if (canStart){
            holder.bind<TextView>(R.id.tv_action).visible()
            holder.bind<TextView>(R.id.tv_sale_ticket).visible()
            holder.bind<TextView>(R.id.tv_action).onClick {
                it as TextView
                if (it.text == "安全到达")
                    listener.onArrived(data.id)
                else
                    listener.onStarted(data.id)
            }
        }
        holder.itemView.onClick {
            listener.onItemClick(data.id)
        }
    }

    interface OnItemClickListener{
        fun onPassengerList(id:Int)
        fun onSaleTicket(data: com.hbcx.driver.network.beans.TicketBus)
        fun onArrived(id:Int)
        fun onStarted(id:Int)
        fun onItemClick(id:Int)
    }
}