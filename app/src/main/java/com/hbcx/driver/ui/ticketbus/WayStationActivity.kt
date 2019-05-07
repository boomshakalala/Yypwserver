package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.adapter.WayStationAdapter
import com.hbcx.driver.dialogs.SelectStationTypeDialog
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.HalfStation
import com.hbcx.driver.network.beans.Region
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_way_station.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult

/**
 * 沿途地
 */
class WayStationActivity :TranslateStatusBarActivity(){
    override fun setContentView() = R.layout.activity_way_station

    private val isUp by lazy {
        intent.getBooleanExtra("isUp",false)
    }
    private val dialog by lazy {
        val typeDialog = SelectStationTypeDialog()
        typeDialog.setOnClickCallback {
            if (it == 0){
                startActivityForResult<AddNormalStationActivity>(1,"isUp" to isUp)
            }else{
                startActivityForResult<AddStationSiteActivity>(1,"isUp" to isUp)
            }
        }
        typeDialog
    }
    override fun initClick() {
        btn_action.onClick {
            dialog.show(supportFragmentManager,"type")
        }
    }

    private val stations = arrayListOf<HalfStation>()
    private val adapter by lazy {
        WayStationAdapter(stations)
    }
    private val id by lazy {
        intent.getIntExtra("id",0)
    }
    override fun initView() {
        title = if (isUp) "上车点" else "沿途地"
        titleBar.addRightButton("确认",onClickListener = View.OnClickListener {
            setResult(Activity.RESULT_OK,intent.putExtra("data",stations))
            finish()
        })
        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false))
        mSwipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.None)
        mSwipeRefreshLayout.setAdapter(adapter)
        if (id!=0)
            getData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK&&data!=null){
            stations.add(data.getSerializableExtra("station") as HalfStation)
            adapter.notifyDataSetChanged()
        }
    }

    private fun getData(){
        HttpManager.getHalfStations(id,if (isUp) 1 else 0).request(this){_,data->
            data?.let {
                stations.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }
    }
}