package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.view.View
import cn.sinata.xldutils.utils.SPUtils
import com.google.gson.Gson
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.HalfStation
import com.hbcx.driver.network.beans.Line
import com.hbcx.driver.network.beans.LineType
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_line_edit_detail.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class EditLineDetailActivity : TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_line_edit_detail
    private val gson = Gson()
    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }
    private val data by lazy {
        intent.getSerializableExtra("data") as Line
    }

    override fun initClick() {
        tv_start.onClick {
            startActivityForResult<StringListActivity>(2, "type" to StringListActivity.START_PROVINCE)
        }
        tv_end.onClick {
            if (typeId == 0) {
                toast("请选择线路类型")
                return@onClick
            }
            startActivityForResult<StringListActivity>(3, "type" to StringListActivity.END_PROVINCE, "isNormalBus" to (typeId == -1))
        }
        tv_points.onClick {
            startActivityForResult<WayStationActivity>(4,"id" to id)
        }
        tv_up_points.onClick {
            startActivityForResult<WayStationActivity>(5,"id" to id,"isUp" to true)
        }
        btn_action.onClick {
            val name = et_name.text.toString().trim()
            if (name.isEmpty()) {
                toast("请输入线路名称")
                return@onClick
            }
            if (typeId == 0) {
                toast("请选择线路类型")
                return@onClick
            }
            if (startId == 0) {
                toast("请选择出发站点")
                return@onClick
            }
            if (endId == 0) {
                toast("请选择到达站点")
                return@onClick
            }
            val s = et_money.text.toString().trim()
            if (s.isEmpty()) {
                toast("请填写票价")
                return@onClick
            }
            val money: Double
            try {
                money = s.toDouble()
                if (money == 0.0) {
                    toast("票价必须大于0元")
                    return@onClick
                }
            } catch (e: Exception) {
                toast("请输入正确的票价")
                return@onClick
            }
            var stationLine = ""
            if (upsites.isNotEmpty()||sites.isNotEmpty()) { //上车点不为空或者沿途点不为空，需要中途站点
                upsites.addAll(sites)
                val gson = Gson()
                stationLine = gson.toJson(upsites).toString()
            }
            HttpManager.addLine(name, typeId, money, startId, endId, userId, stationLine, id).request(this@EditLineDetailActivity) { _, _ ->
                toast("修改成功")
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun initView() {
        title = "编辑"
        et_name.setText(data.lineName)
        tv_type.text = data.typeName
        tv_start.text = data.startStationName
        tv_end.text = data.endStationName
        tv_points.text = data.stationName
        tv_up_points.text = data.stationUpName
        et_money.setText(String.format("%.2f", data.salesMoney))
        getTypeList()

        //截取数据
        typeId = data.lineType
        ll_points.visibility = if (typeId == -1) View.VISIBLE else View.GONE
        startId = data.startStationId
        endId = data.endStationId
        id = data.id
    }

    private val typeList = arrayListOf<LineType>()
    private var typeId = 0 //线路类型
    private var startId = 0 //出发站点
    private var endId = 0 //到达站点
    private var id = 0
    private val sites = arrayListOf<HalfStation>() //沿途站点
    private val upsites = arrayListOf<HalfStation>() //上车站点

    private fun getTypeList() {
        HttpManager.getLineTypes().request(this) { _, data ->
            data?.let {
                it.add(0, LineType(-1, "客运班线"))
                typeList.addAll(it.filter {
                    it.id != 0
                })
                tv_type.setOnClickListener {
                    startActivityForResult<StringListActivity>(1, "type" to StringListActivity.TYPE, "data" to typeList)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                1 -> {
                    tv_type.text = typeList[data!!.getIntExtra("position", 0)].name
                    typeId = typeList[data.getIntExtra("position", 0)].id
                    ll_points.visibility = if (typeId == -1) View.VISIBLE else View.GONE
                }
                2 -> {
                    tv_start.text = data!!.getStringExtra("name")
                    startId = data.getIntExtra("id", 0)
                }
                3 -> {
                    tv_end.text = data!!.getStringExtra("name")
                    endId = data.getIntExtra("id", 0)
                }
                4 -> {
                    if (data != null) {
                        sites.clear()
                        sites.addAll(data.getSerializableExtra("data") as ArrayList<HalfStation>)
                        val sb = StringBuilder()
                        sites.forEach {
                            sb.append(it.name).append("，")
                        }
                        if (sb.isNotEmpty())
                            sb.deleteCharAt(sb.lastIndex)
                        tv_points.text = sb.toString()
                    }
                }
                5 -> {
                    if (data != null){
                        upsites.clear()
                        upsites.addAll(data.getSerializableExtra("data") as ArrayList<HalfStation>)
                        val sb = StringBuilder()
                        upsites.forEach {
                            sb.append(it.name).append("，")
                        }
                        if (sb.isNotEmpty())
                            sb.deleteCharAt(sb.lastIndex)
                        tv_up_points.text = sb.toString()
                    }
                }
            }
        }
    }

}