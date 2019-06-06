package com.hbcx.driver.ui.ticketbus

import android.support.v7.widget.LinearLayoutManager
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.utils.request

class PassengersActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.base_recyclerview_layout

    override fun initClick() {
    }

    private val passengers = arrayListOf<com.hbcx.driver.network.beans.Passenger>()

    private val adapter by lazy {
        com.hbcx.driver.adapter.PassengerAdapter(passengers, this)
    }
    private val id by lazy {
        intent.getIntExtra("id",0)
    }
    private val upId by lazy { //上车点ID
        intent.getIntExtra("upId",0)
    }
    private val time by lazy {
        intent.getStringExtra("time")
    }

    private var recyclerLayout:SwipeRefreshRecyclerLayout? = null
    override fun initView() {
        title = "乘客列表"
        recyclerLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerLayout?.setLayoutManager(LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false))
        recyclerLayout?.setMode(SwipeRefreshRecyclerLayout.Mode.Top)
        recyclerLayout?.setOnRefreshListener(object :SwipeRefreshRecyclerLayout.OnRefreshListener{
            override fun onRefresh() {
                getData()
            }

            override fun onLoadMore() {
            }
        })
        recyclerLayout?.setAdapter(adapter)
        getData()
    }

    private fun getData(){
        com.hbcx.driver.network.HttpManager.getPassengerList(id,time,if (upId == 0) null else upId).request(this, success = { _, data->
            recyclerLayout?.isRefreshing = false
            data?.let {
                passengers.clear()
                passengers.addAll(it)
                if (passengers.isEmpty())
                    recyclerLayout?.setLoadMoreText("没有数据")
                else
                    recyclerLayout?.setLoadMoreText("")
                adapter.notifyDataSetChanged()
            }
        }, error = { _, _->
            recyclerLayout?.isRefreshing = false
        })
    }
}