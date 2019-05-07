package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.picker.TimePicker
import cn.qqtheme.framework.util.ConvertUtils
import cn.sinata.xldutils.utils.SPUtils
import com.google.gson.Gson
import com.hbcx.driver.R
import com.hbcx.driver.adapter.SetStationTimeAdapter
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.HalfStation
import com.hbcx.driver.network.beans.LineType
import com.hbcx.driver.network.beans.Station
import com.hbcx.driver.network.beans.StationTime
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_add_class.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*

class AddClassActivity : TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_add_class

    private val adapter by lazy {
        SetStationTimeAdapter(stationList)
    }

    override fun initClick() {
        tv_line.onClick {
            startActivityForResult<StringListActivity>(2, "type" to StringListActivity.LINE)
        }
        tv_car.onClick {
            startActivityForResult<StringListActivity>(3, "type" to StringListActivity.CAR)
        }
        tv_recycle.onClick {
            startActivityForResult<SelectRecycleActivity>(4)
        }

        titleBar.addRightButton("保存",onClickListener = View.OnClickListener {
            val name = et_name.text.toString().trim()
            if (name.isEmpty()) {
                toast("请输入班次名称")
                return@OnClickListener
            }
            if (lineId == 0) {
                toast("请选择关联线路")
                return@OnClickListener
            }
            if (carId == 0) {
                toast("请选择关联车辆")
                return@OnClickListener
            }
            if (weeks.isEmpty()){
                toast("请选择班次周期")
                return@OnClickListener
            }

            val times = arrayListOf<StationTime>() //沿途站时间
            var startTime = ""
            var endTime = ""
            stationList.forEach {
                if (it.times == null||it.times.isEmpty()) {
                    toast("请设置站点时间")
                    return@OnClickListener
                }
                if (it.type == 1)
                    startTime = it.times
                if (it.type==3)
                    endTime = it.times
            }
            times.addAll(stationList.filter {
                it.type == 2
            }.map {
                StationTime(it.id,it.times)
            })

            val s = et_money.text.toString().trim()
            var rate = 0 //价格浮动比率
            if (s.isNotEmpty()) {
                rate = s.toInt()
            }
            titleBar.getRightButton(0)?.isEnabled = false
            HttpManager.addClass(name,lineId,carId,weeks,Gson().toJson(times),rate,startTime,endTime)
                    .request(this@AddClassActivity,success = { _, _ ->
                        toast("添加成功")
                        setResult(Activity.RESULT_OK)
                        finish()
                    },error = {_,_->
                        titleBar.getRightButton(0)?.isEnabled = true
                    })
        })
    }

    override fun initView() {
        title = "添加班次"
        lv_time.adapter = adapter
        lv_time.layoutManager = object : LinearLayoutManager(this) {
            //解决RecyclerView嵌套RecyclerView滑动卡顿的问题
            //如果你的RecyclerView是水平滑动的话可以重写canScrollHorizontally方法
            override fun canScrollVertically() = false
        }
        adapter.setOnTimeClicker {
            showDateTimePicker(it)
        }
    }

    private val stationList = arrayListOf<Station>()
    private var lineId = 0 //关联线路
    private var carId = 0 //关联车
    private var weeks = "" //周期

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                2 -> {
                    tv_line.text = data!!.getStringExtra("name")
                    lineId = data.getIntExtra("id", 0)
                    HttpManager.getStationList(lineId).request(this,success = {_,data->
                        data?.let {
                            stationList.clear()
                            stationList.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    },error = {_,_->
                        stationList.clear()
                    })
                }
                3 -> {
                    tv_car.text = data!!.getStringExtra("name")
                    carId = data.getIntExtra("id", 0)
                }
                4 -> {
                    if (data != null){
                        weeks = data.getStringExtra("data")
                        val sb = StringBuilder()
                        weeks.split(",").forEach {
                            sb.append(when (it) {
                                "1" -> "周一,"
                                "2" -> "周二,"
                                "3" -> "周三,"
                                "4" -> "周四,"
                                "5" -> "周五,"
                                "6" -> "周六,"
                                "7" -> "周日,"
                                else -> ""
                            })
                        }
                        if (sb.isNotEmpty())
                            sb.deleteCharAt(sb.lastIndex)
                        tv_recycle.text = sb.toString()
                    }
                }
            }
        }
    }
    /**
     * 时间选择
     */
    private fun showDateTimePicker(position:Int) {
        val picker = TimePicker(this)
        picker.setCanceledOnTouchOutside(true)
        picker.setUseWeight(true)
        picker.setTopPadding(ConvertUtils.toPx(this, 10f))
        picker.setRangeStart(0, 0)
        picker.setSubmitTextColor(resources.getColor(R.color.colorPrimary))
        picker.setLabel(":","")
        picker.setSelectedItem(0,0)
        picker.setResetWhileWheel(false)
        picker.setDividerVisible(false)
        picker.setCancelTextColor(Color.parseColor("#999999"))
        picker.setTopLineColor(Color.parseColor("#999999"))
        picker.setOnTimePickListener { hour, minute ->
            stationList[position].times = "$hour:$minute"
            adapter.notifyDataSetChanged()
        }
        picker.show()
    }
}