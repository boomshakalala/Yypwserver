package com.hbcx.driver.adapter

import android.view.View
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.HalfStation
import org.jetbrains.anko.toast

class WayStationAdapter(data:ArrayList<HalfStation>):HFRecyclerAdapter<HalfStation>(data, R.layout.item_string_list_delete) {
    override fun onBind(holder: ViewHolder, position: Int, data: HalfStation) {
        holder.setText(R.id.tv_name,data.name)
        holder.bind<View>(R.id.tv_del).setOnClickListener {
            if (data.id!=0){
                context.toast("无法删除线路站点")
                return@setOnClickListener
            }
            mData.removeAt(position)
            notifyDataSetChanged()
        }
    }
}