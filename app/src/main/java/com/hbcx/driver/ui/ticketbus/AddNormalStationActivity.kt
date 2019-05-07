package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.amap.api.services.help.Tip
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.HalfStation
import com.hbcx.driver.ui.TranslateStatusBarActivity
import kotlinx.android.synthetic.main.activity_add_normal_station.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class AddNormalStationActivity:TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_add_normal_station

    override fun initClick() {
        tv_city.onClick {
            startActivityForResult<StringListActivity>(1,"type" to StringListActivity.HALF_PROVINCE)
        }
        tv_address.onClick {
            if (cityName.isEmpty()){
                toast("请先选择城市")
                return@onClick
            }
            startActivityForResult<SearchPlaceActivity>(2,"region" to regionName,"city" to cityName)
        }
        btn_action.onClick {
            if (cityCode.isEmpty()){
                toast("请选择站点所在城市")
                return@onClick
            }
            val name = et_name.text.toString().trim()
            if (name.isEmpty()){
                toast("请填写站点名称")
                return@onClick
            }
            if (stationName.isEmpty()){
                toast("请选择站点地址")
                return@onClick
            }
            val sort = et_sort.text.toString().trim()
            if (sort.isEmpty()){
                toast("请输入站点序号")
                return@onClick
            }
            val sorti = sort.toInt()
            val station = HalfStation(name, stationName,0, lon, lat, sorti, 2, 0, cityCode,if (isUp) 1 else 3)
            setResult(Activity.RESULT_OK,intent.putExtra("station",station))
            Log.e("site",station.toString())
            finish()
        }
    }

    private val isUp by lazy {
        intent.getBooleanExtra("isUp",false)
    }

    override fun initView() {
        title = if (isUp) "上车点普通站点" else "沿途地普通站点"
    }

    private var cityCode = ""
    private var cityName = ""
    private var regionName = ""
    private var stationName = ""
    private var lat = 0.0
    private var lon = 0.0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK&&data!=null){
            if (requestCode == 1){
                cityName = data.getStringExtra("city")
                regionName = data.getStringExtra("name")
                tv_city.text = regionName
                cityCode = data.getStringExtra("code")
            }
            if (requestCode == 2){
                val tip = data.getParcelableExtra<Tip>("data")
                tv_address.text = tip.name
                stationName = tip.name
                lat = tip.point.latitude
                lon = tip.point.longitude
            }
        }
    }
}