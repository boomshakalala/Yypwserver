package com.hbcx.driver.adapter

import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import com.hbcx.driver.R

class StringListAdapter(data:ArrayList<String>):HFRecyclerAdapter<String>(data, R.layout.item_string_list) {
    override fun onBind(holder: ViewHolder, position: Int, data: String) {
        holder.setText(R.id.tv_name,data)
    }
}