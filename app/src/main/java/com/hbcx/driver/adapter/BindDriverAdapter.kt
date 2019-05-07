package com.hbcx.driver.adapter

import android.widget.CheckedTextView
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.Driver

class BindDriverAdapter(data:ArrayList<Driver>):HFRecyclerAdapter<Driver>(data, R.layout.item_driver_select) {
    override fun onBind(holder: ViewHolder, position: Int, data: Driver) {
        holder.bind<CheckedTextView>(R.id.iv_check).isChecked = data.isChecked
        holder.setText(R.id.tv_name,data.nickName)
        holder.setText(R.id.tv_phone,data.phone)
    }
}