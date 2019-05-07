package com.hbcx.driver.ui.account

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import cn.sinata.xldutils.rxutils.request
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.optFloat
import cn.sinata.xldutils.utils.optJsonArray
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hbcx.driver.R
import com.hbcx.driver.adapter.AppraiseAdapter
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.Appraise
import com.hbcx.driver.utils.Const
import kotlinx.android.synthetic.main.layout_evaluate_head.view.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip

class MyEvaluateActivity: com.hbcx.driver.ui.TranslateStatusBarActivity(),SwipeRefreshRecyclerLayout.OnRefreshListener {
    override fun onRefresh() {

    }

    override fun onLoadMore() {
    }

    override fun setContentView() = R.layout.base_recyclerview_layout

    override fun initClick() {
    }

    private val histories = arrayListOf<Appraise>()
    private val adapter by lazy {
        AppraiseAdapter(histories)
    }

    private val headView by lazy {
        LayoutInflater.from(this).inflate(R.layout.layout_evaluate_head,mRecyclerLayout.mRecyclerView,false)
    }
    private val mRecyclerLayout by lazy {
        findViewById<SwipeRefreshRecyclerLayout>(R.id.swipeRefreshLayout)
    }
    override fun initView() {
        title = "我的评价"
        mRecyclerLayout.setLayoutManager(LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false))
        mRecyclerLayout.setAdapter(adapter)
        mRecyclerLayout.setOnRefreshListener(this)
        mRecyclerLayout.backgroundResource = R.color.bg_grey
        mRecyclerLayout.setPadding(0,dip(10),0,0)
        adapter.setHeaderView(headView)
        getData()
    }

    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }
    private var page = 1
    private fun getData(){
        HttpManager.getDriverEvaluate(userId,page).request(this,success = { _, data->
            mRecyclerLayout.isRefreshing = false
            if (page == 1) {
                histories.clear()
            }
            data?.let {
                if (page == 1) {
                    val score = it.optFloat("score")
                    headView.tv_score.text = String.format("%.1f分",score)
                }
                val list = it.optJsonArray("evaluateList")
                val temp = Gson().fromJson<ArrayList<Appraise>>(list,object : TypeToken<ArrayList<Appraise>>(){}.type)
                if (temp.isEmpty()) {
                    if (page == 1) {
                        mRecyclerLayout.setLoadMoreText("暂无数据")
                    } else {
                        mRecyclerLayout.setLoadMoreText("没有更多了")
                        page--
                    }
                } else {
                    histories.addAll(temp)
                }
            }
            adapter.notifyDataSetChanged()
        }){_,_ ->
            mRecyclerLayout.isRefreshing = false
        }
    }
}