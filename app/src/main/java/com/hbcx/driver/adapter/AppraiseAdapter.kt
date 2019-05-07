package com.hbcx.driver.adapter

import android.widget.RatingBar
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import cn.sinata.xldutils.utils.toTime
import com.hbcx.driver.R

/**
 * 我的评价adapter
 */
class AppraiseAdapter(mData:ArrayList<com.hbcx.driver.network.beans.Appraise>):HFRecyclerAdapter<com.hbcx.driver.network.beans.Appraise>(mData, R.layout.item_appraise_layout) {
    override fun onBind(holder: ViewHolder, position: Int, data: com.hbcx.driver.network.beans.Appraise) {
        holder.setText(R.id.tv_time, data.createTime.toTime("yyyy-MM-dd"))
        holder.setText(R.id.tv_content,data.remark)
        val ratingBar = holder.bind<RatingBar>(R.id.rb_score)
        ratingBar.rating = data.score.toFloat()
    }
}