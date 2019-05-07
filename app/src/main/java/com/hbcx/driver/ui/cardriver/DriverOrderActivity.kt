package com.hbcx.driver.ui.cardriver

import android.graphics.Color
import android.os.Bundle
import cn.sinata.xldutils.activity.RecyclerActivity
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.utils.StatusBarUtil
import com.hbcx.driver.utils.request
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.startActivity

class DriverOrderActivity : RecyclerActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {
    override fun onRefresh() {
        page = 1
        getData()
    }

    override fun onLoadMore() {
        page++
        getData()
    }

    private val orders = ArrayList<com.hbcx.driver.network.beans.OrderList>()
    private val adapter = com.hbcx.driver.adapter.DriverOrderAdapter(orders)
    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID, 0)
    }

    override fun adapter() = adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootFl.setBackgroundColor(Color.parseColor("#2A303E"))
        titleBar.setTitleColor(R.color.textColor)
        titleBar.backgroundColorResource = R.color.white
        titleBar.leftView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back_arrow, 0, 0, 0)
        StatusBarUtil.initStatus(window)
        title = "我的订单"

        mSwipeRefreshLayout.mRecyclerView.setPadding(24,0,24,0)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        adapter.setOnItemClickListener { view, position ->
            if (orders[position].status == 9)
                return@setOnItemClickListener
            if (orders[position].status in 2..4){
                startActivity<com.hbcx.driver.ui.cardriver.TripActivity>("orderId" to orders[position].id)
            }else
                startActivity<com.hbcx.driver.ui.cardriver.OrderDetailActivity>("orderId" to orders[position].id)
        }
    }

    override fun onResume() {
        super.onResume()
        page=1
        showDialog()
        getData()
    }

    private var page = 1
    private fun getData() {
        com.hbcx.driver.network.HttpManager.carOrderList(userId, page).request(this) { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            if (page == 1) {
                orders.clear()
            }
            if (data == null) {
                if (page == 1) {
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                } else {
                    mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                    page--
                }
            }
            data?.let {
                if (it.isEmpty()) {
                    if (page == 1) {
                        mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                    } else {
                        mSwipeRefreshLayout.setLoadMoreText("没有更多了")
                        page--
                    }
                } else {
                    orders.addAll(it)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
}