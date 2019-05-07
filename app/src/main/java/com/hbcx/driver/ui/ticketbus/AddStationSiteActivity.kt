package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.hbcx.driver.R
import com.hbcx.driver.network.beans.HalfStation
import com.hbcx.driver.ui.TranslateStatusBarActivity
import kotlinx.android.synthetic.main.activity_add_station_site.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

/**
 * 车站站点添加
 */
class AddStationSiteActivity:TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_add_station_site

    override fun initClick() {
        tv_city.onClick {
            startActivityForResult<StringListActivity>(1,"type" to StringListActivity.HALF_PROVINCE)
        }
        tv_name.onClick {
            if (cityCode.isEmpty()){
                toast("请先选择站点城市")
                return@onClick
            }
            startActivityForResult<StringListActivity>(2,"type" to StringListActivity.HALF_STATION,"code" to cityCode)
        }
        btn_action.onClick {
            if (cityCode.isEmpty()){
                toast("请选择站点所在城市")
                return@onClick
            }
            if (stationName.isEmpty()){
                toast("请选择车站")
                return@onClick
            }
            val sort = et_sort.text.toString().trim()
            if (sort.isEmpty()){
                toast("请输入站点序号")
                return@onClick
            }
            val sorti = sort.toInt()
            val station = HalfStation(stationName, address,0 ,lon, lat, sorti, 1, stationId, cityCode,if (isUp) 1 else 3)
            Log.e("site",station.toString())
            setResult(Activity.RESULT_OK,intent.putExtra("station",station))
            finish()
        }
    }
    private val isUp by lazy {
        intent.getBooleanExtra("isUp",false)
    }
    override fun initView() {
        title = if (isUp) "上车点车站站点" else "沿途地车站站点"
    }

    private var cityCode = ""
    private var regionName = ""
    private var stationName = ""
    private var stationId = 0
    private var address = ""
    private var lat = 0.0
    private var lon = 0.0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK&&data!=null){
            if (requestCode == 1){
                regionName = data.getStringExtra("name")
                tv_city.text = regionName
                cityCode = data.getStringExtra("code")
            }
            if (requestCode == 2){
                stationName = data.getStringExtra("name")
                stationId = data.getIntExtra("id",0)
                address = data.getStringExtra("address")
                tv_name.text = stationName
                lat = data.getDoubleExtra("lat",0.0)
                lon = data.getDoubleExtra("lon",0.0)
            }
        }
    }
}