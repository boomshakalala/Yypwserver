package com.hbcx.driver.adapter

import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.Line

class LineManageAdapter(data:ArrayList<Line>) :HFRecyclerAdapter<Line>(data, R.layout.item_line_manage){
    override fun onBind(holder: ViewHolder, position: Int, data: Line) {
        holder.setText(R.id.tv_title,String.format("线路名称:%s",data.lineName))
        holder.setText(R.id.tv_start_end, String.format("%s-%s",data.startName,data.endName))
        holder.setText(R.id.tv_status,data.getStatusStr())
    }
}