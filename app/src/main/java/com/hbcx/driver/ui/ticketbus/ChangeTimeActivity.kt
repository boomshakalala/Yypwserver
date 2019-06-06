package com.hbcx.driver.ui.ticketbus

import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.picker.TimePicker
import cn.qqtheme.framework.util.ConvertUtils
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.hbcx.driver.R
import com.hbcx.driver.adapter.ChageTimeBusStationAdapter
import com.hbcx.driver.dialogs.CommitLocationDialog
import com.hbcx.driver.dialogs.TipDialog
import com.hbcx.driver.interfaces.OnDialogListener
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.BusStation
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_change_time.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList

class ChangeTimeActivity : com.hbcx.driver.ui.TranslateStatusBarActivity(), ChageTimeBusStationAdapter.Listener, AMapLocationListener {
    override fun onLocationChanged(p0: AMapLocation?) {
        p0.let {
            if (it!=null){
                longitude = String.format("%s",it.longitude)
                latitude = String.format("%s",it.latitude)
            }
        }
    }


    var comId = 0
    var driverName = ""
    var toponymy = ""
    var longitude = ""
    var latitude = ""
    override fun onChangeTime(view:View,data: BusStation) {
        showDateTimePicker(view,data)
    }

    override fun onChangeLocation(data: BusStation) {
        toponymy = data.name
        commitLocationDialog.show(supportFragmentManager,"commitLocation")
    }


    private val commitLocationDialog by lazy {
        val dialog = CommitLocationDialog()
        dialog.setDialogListener{p, s ->
            if (s!=null)
            commitLocation(longitude,latitude,s,comId,toponymy,driverName)

        }
        dialog
    }

    private val app by lazy {
        application as com.hbcx.driver.YypwApplication
    }


    /**
     * 时间选择
     */
    private fun showDateTimePicker(view: View,data:BusStation) {
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
            val endTime = "$hour:$minute"
            val id = data.id
            commitTime(id,endTime)

        }
        picker.show()
    }


    private val isLocation by lazy {
        intent.getBooleanExtra("isLocation",false);
    }
    private val data = arrayListOf<BusStation>()
    private val adapter by lazy {
        ChageTimeBusStationAdapter(data,isLocation,this)
    }

    private val id by lazy {
        intent.getIntExtra("id", 0)
    }



    private val recyclerLayout by lazy {
        findViewById<RecyclerView>(R.id.recyclerView)
    }

    override fun setContentView() = R.layout.activity_change_time

    override fun initClick() {

    }

    override fun initView() {
        title = "客运班线"
        recyclerLayout.backgroundResource = R.color.bg_grey
        recyclerLayout.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerLayout.adapter = adapter
        if(isLocation){
            tv_tip.text = "点击站点输入该站点经纬度信息"
            app.addLocationListener(this)
            app.startLocation()
        }
    }

    fun getData() = if (isLocation){

        HttpManager.getLocationPage(id).request(this,true, success = { _, data ->
            data?.let {
                var stations = ArrayList<BusStation>()
                comId = it.companyId
                driverName = it.drivername
                if (it.stations.isEmpty())
                    return@let
                for (station in it.stations) {
                    var busStation = BusStation(0,0,station,0,0,"",0,false,"")
                    stations.add(busStation)
                }
                if (stations.isNotEmpty()){
                    this.data.clear()
                    this.data.addAll(stations)
                    adapter.notifyDataSetChanged()
                }

            }

        }, error = {_, _ ->

        })
    }else{

        HttpManager.getTimePage(id).request(this,true, success = { _, data ->
            data?.let {
                this.data.clear()
                if (it.isNotEmpty()) {
                    this.data.addAll(it)
                }
                adapter.notifyDataSetChanged()
            }

        }, error = {_, _ ->

        })
    }

    fun commitTime(id:Int,endTime:String){
        HttpManager.commitTime(id,endTime).request(this,true,success = {_,_ ->
            toast("修改成功")
            getData()
        },error = {_,_ ->

        })
    }


    fun commitLocation(Longitude:String,latitude:String,message:String,companyId:Int,toponymy:String,drivername:String){
        HttpManager.commitLocation(Longitude,latitude,message,companyId,toponymy,drivername).request(this,true,success = {_,_ ->
            toast("提交成功")
            getData()
        },error = {_,_ ->

        })
    }

    override fun onResume() {
        super.onResume()
        getData()
    }
}