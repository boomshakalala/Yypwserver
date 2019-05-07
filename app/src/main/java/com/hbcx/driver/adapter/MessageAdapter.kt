package com.hbcx.driver.adapter

import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.utils.interval
import cn.sinata.xldutils.visible
import com.facebook.drawee.view.SimpleDraweeView
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.Message

class MessageAdapter(data:ArrayList<Message>):HFRecyclerAdapter<Message>(data, R.layout.item_message) {
    override fun onBind(holder: ViewHolder, position: Int, data: Message) {
        holder.setText(R.id.tv_title,if (data.type == 1) "平台公告" else "系统消息")
        holder.setText(R.id.tv_time,data.createTime.interval())
        holder.setText(R.id.tv_content,if (data.type == 1) data.title else data.content)
        val draweeView = holder.bind<SimpleDraweeView>(R.id.iv_img)
        if (data.type == 1){
            draweeView.visible()
            draweeView.setImageURI(data.imgUrl)
        }else
            draweeView.gone()
    }
}