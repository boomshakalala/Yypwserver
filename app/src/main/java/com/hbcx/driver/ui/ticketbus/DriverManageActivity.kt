package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.adapter.DriverManageAdapter
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.Driver
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_driver_manage.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult

class DriverManageActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_driver_manage

    override fun initClick() {
        btn_add.onClick {
            startActivityForResult<com.hbcx.driver.ui.ticketbus.AddDriverActivity>(1)
        }
    }

    private val drivers = arrayListOf<Driver>()
    private val adapter by lazy {
        DriverManageAdapter(drivers)
    }

    override fun initView() {
        title = "司机管理"
        swipeRefreshLayout.setLayoutManager(LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false))
        swipeRefreshLayout.setAdapter(adapter)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshRecyclerLayout.OnRefreshListener{
            override fun onRefresh() {
                page = 1
                getData()
            }

            override fun onLoadMore() {
                page++
                getData()
            }
        })
        adapter.setOnItemClickListener { view, position ->
            startActivityForResult<DriverDetailActivity>(1,"id" to drivers[position].id)
        }
        getData()
    }

    private var page = 1
    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }
    private fun getData(){
        HttpManager.getDriverList(userId,page).request(this, success = { _, data->
            swipeRefreshLayout.isRefreshing = false
            data?.let {
                if (page == 1)
                    drivers.clear()
                drivers.addAll(it)
                if (drivers.isEmpty())
                    swipeRefreshLayout.setLoadMoreText("暂无数据")
                if (it.isEmpty()&&page!=1)
                    swipeRefreshLayout.setLoadMoreText("没有更多")
                adapter.notifyDataSetChanged()
            }
        }, error = { _, _->
            swipeRefreshLayout.isRefreshing = false
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            getData()
        }
    }
}