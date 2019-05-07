package com.hbcx.driver.adapter

import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.ClassModel
import com.hbcx.driver.network.beans.Line

class ClassManageAdapter(data:ArrayList<ClassModel>) :HFRecyclerAdapter<ClassModel>(data, R.layout.item_class_manage){
    override fun onBind(holder: ViewHolder, position: Int, data: ClassModel) {
        holder.setText(R.id.tv_title,String.format("班次名称:%s",data.name))
        holder.setText(R.id.tv_line, String.format("关联线路:%s",data.lineName))
        holder.setText(R.id.tv_car,String.format("关联车辆:%s",data.licensePlate))
        holder.setText(R.id.tv_status,data.getStatusStr())
    }
}