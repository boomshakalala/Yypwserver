package com.hbcx.driver.ui.account

import android.support.v7.widget.LinearLayoutManager
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.adapter.WithdrawHistoryAdapter
import com.hbcx.driver.network.beans.WithdrawHistory
import com.hbcx.driver.utils.request
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip

class WithdrawHistoryActivity: com.hbcx.driver.ui.TranslateStatusBarActivity(),SwipeRefreshRecyclerLayout.OnRefreshListener {
    override fun onRefresh() {
        page = 1
        getData()
    }

    override fun onLoadMore() {
        page++
        getData()
    }

    override fun setContentView() = R.layout.base_recyclerview_layout

    override fun initClick() {
    }

    private var page = 1
    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }
    private val histories = arrayListOf<WithdrawHistory>()
    private val adapter by lazy {
        WithdrawHistoryAdapter(histories)
    }
    private val mRecyclerLayout by lazy {
        findViewById<SwipeRefreshRecyclerLayout>(R.id.swipeRefreshLayout)
    }
    override fun initView() {
        title = "提现记录"
        mRecyclerLayout.setLayoutManager(LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false))
        mRecyclerLayout.setAdapter(adapter)
        mRecyclerLayout.setOnRefreshListener(this)
        mRecyclerLayout.backgroundResource = R.color.bg_grey
        mRecyclerLayout.setPadding(0,dip(10),0,0)
        getData()
    }

    private fun getData(){
        com.hbcx.driver.network.HttpManager.getWithdrawHistroy(userId,page).request(this, success = { _, data->
            mRecyclerLayout.isRefreshing = false
            data?.let {
                if (page == 1)
                    histories.clear()
                histories.addAll(it)
                if (histories.isEmpty())
                    mRecyclerLayout.setLoadMoreText("暂无数据")
                if (it.isEmpty()&&page!=1)
                    mRecyclerLayout.setLoadMoreText("没有更多数据")
            }
        }, error = { _, _->
            mRecyclerLayout.isRefreshing = false
        })
    }
}