package com.hbcx.driver.adapter

import android.view.View
import android.widget.TextView
import cn.sinata.xldutils.adapter.HFRecyclerAdapter
import cn.sinata.xldutils.adapter.util.ViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.Car

class CarManageAdapter(data:ArrayList<Car>,private val callback:OnClickCallback):HFRecyclerAdapter<Car>(data, R.layout.item_car_manage) {
    override fun onBind(holder: ViewHolder, position: Int, data: Car) {
        holder.bind<SimpleDraweeView>(R.id.iv_car).setImageURI(data.bodyIllumination)
        holder.setText(R.id.tv_status,data.getStatusStr())
        holder.bind<TextView>(R.id.tv_status).isEnabled = data.status == 1
        holder.setText(R.id.tv_title,"${data.licensePlate} ${data.brandName}${data.modelName} ${data.carColor}")
        holder.setText(R.id.tv_driver,if (data.status == 3) data.nickName else "暂未关联司机")
        holder.setText(R.id.tv_action,data.getActionStr())
        val tvAction = holder.bind<TextView>(R.id.tv_action)
        tvAction.isEnabled = data.status in (2..3)
        tvAction.setOnClickListener {
            callback.bindDriver(data.id)
        }
        val tvCancel = holder.bind<TextView>(R.id.tv_cancel)
        tvCancel.visibility = if (data.status == 3) View.VISIBLE else View.GONE
        tvCancel.setOnClickListener {
            callback.unbindDriver(data.id)
        }
    }

    interface OnClickCallback{
        fun bindDriver(id:Int)
        fun unbindDriver(id:Int)
    }
}