package com.hbcx.driver.ui.account

import android.support.v7.widget.LinearLayoutManager
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.adapter.IncomeHistoryAdapter
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.IncomeDetail
import com.hbcx.driver.utils.request
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip

class WithholdHistoryActivity : com.hbcx.driver.ui.TranslateStatusBarActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {
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

    private val histories = arrayListOf<IncomeDetail>()
    private val adapter by lazy {
        IncomeHistoryAdapter(histories, false)
    }
    private lateinit var mRecyclerLayout: SwipeRefreshRecyclerLayout
    override fun initView() {
        title = "扣款明细"
        mRecyclerLayout = findViewById<SwipeRefreshRecyclerLayout>(R.id.swipeRefreshLayout)
        mRecyclerLayout.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        mRecyclerLayout.setAdapter(adapter)
        mRecyclerLayout.setOnRefreshListener(this)
        mRecyclerLayout.backgroundResource = R.color.bg_grey
        mRecyclerLayout.setPadding(0, dip(10), 0, 0)
        getData()
    }

    private var page = 1
    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }

    private fun getData() {
        HttpManager.getIncomeData(userId, 2, page, "", "").request(this, success = { _, data ->
            mRecyclerLayout.isRefreshing = false
            data?.let {
                if (page == 1)
                    histories.clear()
                histories.addAll(it.incomeDetailsList)
                if (histories.isEmpty())
                    mRecyclerLayout.setLoadMoreText("暂无数据")
                if (it.incomeDetailsList.isEmpty() && page != 1)
                    mRecyclerLayout.setLoadMoreText("没有更多")
            }
        }, error = { _, _ ->
            mRecyclerLayout.isRefreshing = false
        })
    }
}