package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.adapter.StringListAdapter
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.EnableCar
import com.hbcx.driver.network.beans.EnableLine
import com.hbcx.driver.network.beans.LineType
import com.hbcx.driver.network.beans.Region
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivityForResult

class StringListActivity : TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.base_recyclerview_layout

    override fun initClick() {
        adapter.setOnItemClickListener { view, position ->
            when (dataType) {
                TYPE -> {
                    setResult(Activity.RESULT_OK, intent.putExtra("position", position))
                    finish()
                }
                START_PROVINCE ->
                    startActivityForResult<StringListActivity>(1, "id" to regions[position].id, "type" to START_CITY)
                START_CITY ->
                    startActivityForResult<StringListActivity>(1, "id" to regions[position].id, "type" to START_DISTRICT)
                START_DISTRICT ->
                    startActivityForResult<StringListActivity>(1, "code" to regions[position].code, "type" to START_STATION)
                END_PROVINCE ->
                    startActivityForResult<StringListActivity>(1, "id" to regions[position].id, "type" to END_CITY,"lineTypeId" to typeId, "isNormalBus" to isNormalBus)
                END_CITY ->
                    startActivityForResult<StringListActivity>(1, "id" to regions[position].id, "type" to END_DISTRICT,"lineTypeId" to typeId, "isNormalBus" to isNormalBus)
                END_DISTRICT ->
                    startActivityForResult<StringListActivity>(1, "code" to regions[position].code, "type" to END_STATION,"lineTypeId" to typeId, "isNormalBus" to isNormalBus)
                START_STATION, END_STATION -> {
                    setResult(Activity.RESULT_OK, intent.putExtra("name", regions[position].name).putExtra("id", regions[position].id))
                    finish()
                }
                HALF_STATION-> {
                    setResult(Activity.RESULT_OK, intent.putExtra("name", regions[position].name).putExtra("address", regions[position].address)
                            .putExtra("lon",regions[position].lon).putExtra("lat",regions[position].lat).putExtra("id",regions[position].id))
                    finish()
                }
                HALF_PROVINCE ->
                    startActivityForResult<StringListActivity>(1, "id" to regions[position].id, "type" to HALF_CITY)
                HALF_CITY ->
                    startActivityForResult<StringListActivity>(1, "id" to regions[position].id, "type" to HALF_DISTRICT,"city" to regions[position].name)
                HALF_DISTRICT -> {
                    setResult(Activity.RESULT_OK, intent.putExtra("name", regions[position].name).putExtra("code", regions[position].code))
                    finish()
                }
                LINE->{
                    setResult(Activity.RESULT_OK, intent.putExtra("id",lines[position].id).putExtra("name",lines[position].lineName))
                    finish()
                }
                CAR->{
                    setResult(Activity.RESULT_OK, intent.putExtra("id",cars[position].id).putExtra("name",cars[position].licensePlate))
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    private val strings = arrayListOf<String>()
    private val adapter by lazy {
        StringListAdapter(strings)
    }

    private val mSwipeRefreshRecyclerLayout by lazy {
        find<SwipeRefreshRecyclerLayout>(R.id.swipeRefreshLayout)
    }
    private val rootView by lazy {
        find<View>(R.id.rootFL)
    }

    override fun initView() {
        title = when (dataType) {
            TYPE -> "选择线路类型"
            START_PROVINCE -> "选择出发地所在省份"
            START_CITY -> "选择出发地所在市"
            START_DISTRICT -> "选择出发地所在区"
            START_STATION -> "选择出发地车站"
            END_PROVINCE -> "选择到达地所在省份"
            END_CITY -> "选择到达地所在市"
            END_DISTRICT -> "选择到达地所在区"
            END_STATION -> "选择到达地车站"
            HALF_PROVINCE -> "选择站点所在省份"
            HALF_CITY -> "选择站点所在市"
            HALF_DISTRICT -> "选择站点所在区"
            HALF_STATION -> "选择沿途站点"
            LINE -> "关联线路"
            CAR -> "关联车辆"
            else -> ""
        }
        rootView.backgroundResource = R.color.bg_grey
        rootView.setPadding(0, dip(10), 0, 0)
        mSwipeRefreshRecyclerLayout.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        mSwipeRefreshRecyclerLayout.setMode(SwipeRefreshRecyclerLayout.Mode.None)
        mSwipeRefreshRecyclerLayout.setAdapter(adapter)
        getData()
    }

    private val regions = arrayListOf<Region>()    //区域集和
    private val lines = arrayListOf<EnableLine>()    //线路集和
    private val cars = arrayListOf<EnableCar>()    //车集和
    private val id by lazy {
        //上级id
        intent.getIntExtra("id", 0)
    }
    private val typeId by lazy { //线路类型id 获取专线结束站点需要
        //上级id
        intent.getIntExtra("lineTypeId", 0)
    }
    private val code by lazy {
        //第三级区域code
        intent.getStringExtra("code")
    }
    private val isNormalBus by lazy {
        //是否为普通客运线路
        intent.getBooleanExtra("isNormalBus", false)
    }
    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }

    private fun getData() {
        when (dataType) {
            TYPE -> {
                val list = intent.getSerializableExtra("data") as ArrayList<LineType>
                strings.addAll(list.map {
                    it.name
                })
                adapter.notifyDataSetChanged()
            }
            START_STATION, END_STATION, HALF_STATION -> {
                HttpManager.getStations(if (dataType == END_STATION && !isNormalBus) 2 else 1, code,typeId).request(this) { _, data ->
                    data?.let {
                        regions.addAll(it)
                        strings.addAll(it.map {
                            it.name
                        })
                        if (it.isEmpty())
                            mSwipeRefreshRecyclerLayout.setLoadMoreText("暂无可用站点")
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            LINE->{
                HttpManager.getEnableLineList(userId).request(this) { _, data ->
                    data?.let {
                        lines.addAll(it)
                        strings.addAll(it.map {
                            it.lineName
                        })
                        if (it.isEmpty())
                            mSwipeRefreshRecyclerLayout.setLoadMoreText("暂无可关联线路")
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            CAR->
                HttpManager.getEnableCarList(userId).request(this) { _, data ->
                    data?.let {
                        cars.addAll(it)
                        strings.addAll(it.map {
                            it.licensePlate
                        })
                        if (it.isEmpty())
                            mSwipeRefreshRecyclerLayout.setLoadMoreText("暂无可关联车辆")
                        adapter.notifyDataSetChanged()
                    }
                }
            else -> {
                HttpManager.getRegins(id).request(this) { _, data ->
                    data?.let {
                        regions.addAll(it)
                        strings.addAll(it.map {
                            it.name
                        })
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private val dataType by lazy {
        //数据类型
        intent.getIntExtra("type", 0)
    }

    companion object {
        const val TYPE = 0 //线路类型
        const val START_PROVINCE = 1 //出发省份
        const val START_CITY = 2 //出发市
        const val START_DISTRICT = 3 //出发区
        const val START_STATION = 4 //出发站点
        const val END_PROVINCE = 5 //到达省份
        const val END_CITY = 6 //到达市
        const val END_DISTRICT = 7 //到达区
        const val END_STATION = 8 //到达站点
        const val HALF_PROVINCE = 9 //沿途省份
        const val HALF_CITY = 10 //沿途市
        const val HALF_DISTRICT = 11 //沿途区
        const val HALF_STATION = 12 //沿途站点
        const val LINE = 13 //线路
        const val CAR = 14 //车辆
    }
}