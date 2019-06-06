package com.hbcx.driver.adapter

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import cn.sinata.amaplib.util.IFlyTTS
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.visible
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.BusStation
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.textColorResource

class ChageTimeBusStationAdapter(data: ArrayList<BusStation>, private val isLocation:Boolean = false, private val listener:Listener) : HFRecyclerAdapter<BusStation>(data, R.layout.item_location) {


    override fun onBind(holder: ViewHolder, position: Int, data: BusStation) {
        holder.setText(R.id.tv_station,data.name)
        if (isLocation){
            holder.setText(R.id.tv_time,"")
            holder.bind<TextView>(R.id.tv_time).gone()
        }
        else{
            holder.setText(R.id.tv_time,data.time)
            holder.bind<TextView>(R.id.tv_time).visible()
        }
        val top = holder.bind<View>(R.id.top_line)
        top.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        val bottom = holder.bind<View>(R.id.bottom_line)
        bottom.visibility = if (position == itemCount - 1) View.INVISIBLE else View.VISIBLE
        val divider = holder.bind<View>(R.id.v_divider)
        divider.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE
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

        holder.itemView.onClick {
            if (isLocation){
                listener.onChangeLocation(data)
            }else{
                listener.onChangeTime(holder.itemView,data)
            }

        }



    }

    interface Listener{
        fun onChangeTime(view :View,data: BusStation)
        fun onChangeLocation(data:BusStation)
    }
}