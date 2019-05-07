package com.hbcx.driver.adapter

import android.widget.TextView
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.Driver
import org.jetbrains.anko.textColorResource

class DriverManageAdapter(data:ArrayList<Driver>):HFRecyclerAdapter<Driver>(data, R.layout.item_driver_manage) {
    override fun onBind(holder: ViewHolder, position: Int, data: Driver) {
        holder.bind<SimpleDraweeView>(R.id.iv_head).setImageURI(data.imgUrl)
        holder.setText(R.id.tv_name,data.nickName)
        holder.bind<TextView>(R.id.tv_name).setCompoundDrawablesWithIntrinsicBounds(0,0,if (data.sex == 1) R.mipmap.ic_man else R.mipmap.ic_woman,0)
        holder.setText(R.id.tv_car_info,if (data.isCar == 1) "待关联车辆" else "${data.licensePlate} ${data.brandName}${data.modelName} ${data.carColor}")
        holder.setText(R.id.tv_status,data.getStatusStr())
        holder.bind<TextView>(R.id.tv_status).textColorResource = data.getStatusColorRes()
    }
}