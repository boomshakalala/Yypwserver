package com.hbcx.driver.adapter

import android.widget.TextView
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.Station

class SetStationTimeAdapter(data:ArrayList<Station>):HFRecyclerAdapter<Station>(data, R.layout.item_set_line_price) {
    override fun onBind(holder: ViewHolder, position: Int, data: Station) {
        holder.setText(R.id.tv_name, data.name)
        holder.bind<TextView>(R.id.et_price).hint = "选择时间"
        holder.setText(R.id.et_price,data.times)
        holder.bind<TextView>(R.id.et_price).setOnClickListener {
            callback?.onTimeClick(position)
        }
    }
    private var callback:OnTimeClicker? = null
    fun setOnTimeClicker(l:(position:Int)->Unit){
        callback = object :OnTimeClicker{
            override fun onTimeClick(position: Int) {
                l(position)
            }
        }
    }
    interface OnTimeClicker{
        fun onTimeClick(position: Int)
    }
}