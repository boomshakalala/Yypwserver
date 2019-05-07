package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import cn.qqtheme.framework.picker.TimePicker
import cn.qqtheme.framework.util.ConvertUtils
import com.google.gson.Gson
import com.hbcx.driver.R
import com.hbcx.driver.adapter.SetStationTimeAdapter
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.ClassModel
import com.hbcx.driver.network.beans.Station
import com.hbcx.driver.network.beans.StationTime
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_edit_class.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class EditClassDetailActivity:TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_edit_class

    override fun initClick() {
        tv_recycle.onClick {
            startActivityForResult<SelectRecycleActivity>(4)
        }
        btn_action.onClick {
            val name = et_name.text.toString().trim()
            if (name.isEmpty()) {
                toast("请输入班次名称")
                return@onClick
            }
            if (weeks.isEmpty()){
                toast("请选择班次周期")
                return@onClick
            }
            val times = arrayListOf<StationTime>() //沿途站时间
            var startTime = ""
            var endTime = ""
            stationList.forEach {
                if (it.times.isEmpty()) {
                    toast("请设置站点时间")
                    return@onClick
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
            HttpManager.addClass(name,data.line_id,data.carId,weeks, Gson().toJson(times),rate,startTime,endTime,data.id).request(this@EditClassDetailActivity) { _, _ ->
                toast("编辑成功")
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private val stationList = arrayListOf<Station>()
    private val adapter by lazy {
        SetStationTimeAdapter(stationList)
    }

    private val data by lazy {
        intent.getSerializableExtra("data") as ClassModel
    }
    private var weeks =""
    override fun initView() {
        title = "编辑班次"
        et_name.setText(data.name)
        val sb = StringBuilder()
        weeks = data.weeks
        data.weeks.split(",").forEach {
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
        stationList.addAll(data.stattionList)
        et_money.setText(data.percentage.toString())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
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