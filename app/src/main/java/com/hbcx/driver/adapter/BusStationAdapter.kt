package com.hbcx.driver.adapter

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.visible
import com.hbcx.driver.R
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.textColorResource

class BusStationAdapter(data: ArrayList<com.hbcx.driver.network.beans.BusStation>, val isSale:Boolean = false) : HFRecyclerAdapter<com.hbcx.driver.network.beans.BusStation>(data, R.layout.item_bus_station) {
    override fun onBind(holder: ViewHolder, position: Int, data: com.hbcx.driver.network.beans.BusStation) {
        holder.setText(R.id.tv_station,data.name)
        val top = holder.bind<View>(R.id.top_line)
        top.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        val bottom = holder.bind<View>(R.id.bottom_line)
        bottom.visibility = if (position == itemCount - 1) View.INVISIBLE else View.VISIBLE
        val stamp = holder.bind<TextView>(R.id.tv_stamp)
        when (position) {
            0 -> {
                stamp.visible()
                stamp.text = "起"
                stamp.backgroundResource = R.drawable.bg_blue_circle
            }
            itemCount-1->{
                stamp.visible()
                stamp.text = "终"
                stamp.backgroundResource = R.drawable.bg_orange_circle
            }
            else->stamp.gone()
        }
        if (isSale){ //售票页面
            when(data.isUpOrDown){
                1->{ //上车点
                    stamp.visible()
                    stamp.text = "上"
                    stamp.backgroundResource = R.drawable.bg_blue_circle
                }
                2->{//下车点
                    stamp.visible()
                    stamp.text = "下"
                    stamp.backgroundResource = R.drawable.bg_orange_circle
                }
            }
            if (position == 0)
                holder.setText(R.id.tv_time, data.times)
            else
                holder.setText(R.id.tv_time, if (data.times == null) "" else String.format("预计%s", data.times))
            holder.bind<TextView>(R.id.tv_station).typeface = if (data.isUpOrDown in 1..2) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            holder.bind<TextView>(R.id.tv_time).typeface = if (data.isUpOrDown in 1..2) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            holder.bind<TextView>(R.id.tv_station).textColorResource = if (data.isUpOrDown in 1..2) R.color.black_text else R.color.textColor66
            holder.bind<TextView>(R.id.tv_time).textColorResource = if (data.isUpOrDown in 1..2) R.color.black_text else R.color.textColor66
        }else //详情页面
            holder.setText(R.id.tv_time,if (data.type==2||data.type == 5) "" else String.format("购：%d 上：%d",data.peoNum1,data.peoNum))
    }
}