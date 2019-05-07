package com.hbcx.driver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import cn.sinata.xldutils.utils.timeDay
import com.hbcx.driver.R

class MainCarOrderAdapter(private val context: Context, private var data: MutableList<com.hbcx.driver.network.beans.OrderList>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_main, parent, false)
            holder = ViewHolder()
            holder.tvname = convertView.findViewById<TextView>(R.id.tv_name)
            holder.tvtime = convertView.findViewById<TextView>(R.id.tv_time)
            holder.tvstart = convertView.findViewById<TextView>(R.id.tv_start)
            holder.tvend = convertView.findViewById<TextView>(R.id.tv_end)
            holder.tvstatus = convertView.findViewById<TextView>(R.id.tv_state)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.tvname?.text = when(data[position].type){
            1-> "快车"
            2-> "专车经济"
            3-> "专车舒适"
            4-> "专车商务"
            else->""
        }
        holder.tvtime?.text = data[position].createTime.timeDay()
        holder.tvstart?.text = data[position].startAddress
        holder.tvend?.text = data[position].endAddress
        holder.tvstatus?.text = when(data[position].status){
            1->"待应答"
            2->"待接驾"
            3->"待上车"
            4->"服务中"
            5->"待支付"
            6->"取消待支付"
            7->"待评价"
            8->"已完成"
            9->"已取消"
            else->""
        }
        return convertView!!
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    private inner class ViewHolder {
        internal var tvname: TextView? = null
        internal var tvtime: TextView? = null
        internal var tvstart: TextView? = null
        internal var tvend: TextView? = null
        internal var tvstatus: TextView? = null
    }
}