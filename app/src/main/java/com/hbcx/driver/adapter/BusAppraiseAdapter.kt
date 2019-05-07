package com.hbcx.driver.adapter

import android.widget.RatingBar
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.utils.toTime
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.Appraise

/**
 * 我的评价adapter
 */
class BusAppraiseAdapter(mData:ArrayList<Appraise>):HFRecyclerAdapter<Appraise>(mData, R.layout.item_bus_appraise_layout) {
    override fun onBind(holder: ViewHolder, position: Int, data: Appraise) {
        holder.setText(R.id.tv_time, data.createTime.toTime("yyyy-MM-dd"))
        holder.setText(R.id.tv_content,data.content)
        holder.setText(R.id.tv_name,data.nickName)
        holder.setText(R.id.tv_car_info,"${data.licensePlate} ${data.brandName}${data.modelName} ${data.carColor}")
        val ratingBar = holder.bind<RatingBar>(R.id.rb_score)
        ratingBar.rating = data.evaluateScore.toFloat()
    }
}