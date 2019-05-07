package com.hbcx.driver.adapter

import android.widget.TextView
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.LinePrice

class SetLinePriceAdapter(data:ArrayList<LinePrice>):HFRecyclerAdapter<LinePrice>(data, R.layout.item_set_line_price) {
    override fun onBind(holder: ViewHolder, position: Int, data: LinePrice) {
        holder.setText(R.id.tv_name, String.format("%s-%s",data.startStationName,data.endStationName))
        holder.bind<TextView>(R.id.et_price).hint = String.format("%.2f元",data.salesMoney)
        holder.bind<TextView>(R.id.et_price).setOnClickListener {
            callback?.onPriceClick(position)
        }
    }
    private var callback:OnPriceClicker? = null
    fun setOnPriceClicker(l:(position:Int)->Unit){
        callback = object :OnPriceClicker{
            override fun onPriceClick(position: Int) {
                l(position)
            }
        }
    }
    interface OnPriceClicker{
        fun onPriceClick(position: Int)
    }
}