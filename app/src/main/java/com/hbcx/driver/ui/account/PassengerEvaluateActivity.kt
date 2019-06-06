package com.hbcx.driver.ui.account

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.Appraise
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.layout_bus_evaluate_head.view.*
import org.jetbrains.anko.backgroundResource

class PassengerEvaluateActivity: com.hbcx.driver.ui.TranslateStatusBarActivity(),SwipeRefreshRecyclerLayout.OnRefreshListener {
    override fun onRefresh() {
        page = 1
        getData()
    }

    override fun onLoadMore() {
        page ++
        getData()
    }

    override fun setContentView() = R.layout.base_recyclerview_layout

    override fun initClick() {
    }

    private val evaluates = arrayListOf<Appraise>()
    private val adapter by lazy {
        com.hbcx.driver.adapter.BusAppraiseAdapter(evaluates)
    }

    private val headView by lazy {
        LayoutInflater.from(this).inflate(R.layout.layout_bus_evaluate_head,mRecyclerLayout.mRecyclerView,false)
    }
    private val mRecyclerLayout by lazy {
        findViewById<SwipeRefreshRecyclerLayout>(R.id.swipeRefreshLayout)
    }
    override fun initView() {
        title = "乘客评价"
        mRecyclerLayout.setLayoutManager(LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false))
        mRecyclerLayout.setAdapter(adapter)
        mRecyclerLayout.setOnRefreshListener(this)
        mRecyclerLayout.backgroundResource = R.color.bg_grey
        adapter.setHeaderView(headView)
        getData()
    }

    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }
    private var page = 1
    private fun getData(){
        HttpManager.getEvaluateData(userId,page).request(this, success = { _, data->
            mRecyclerLayout.isRefreshing = false
            data?.let {
                if (page == 1){
                    evaluates.clear()
                    headView.tv_score.text = it.totalScore.toString()
                    headView.rb_sanitation.rating = it.hygiene.toFloat()
                    headView.tv_sanitation.text = it.hygiene.toString()
                    headView.rb_facility.rating = it.facilities.toFloat()
                    headView.tv_facility.text = it.facilities.toString()
                    headView.rb_on_time_rate.rating = it.punctuality.toFloat()
                    headView.tv_on_time_rate.text = it.punctuality.toString()
                    headView.rb_service.rating = it.serviceScore.toFloat()
                    headView.tv_service.text = it.serviceScore.toString()
                    headView.rb_attitude.rating = it.attitudeScore.toFloat()
                    headView.tv_attitude.text = it.attitudeScore.toString()
                }
                evaluates.addAll(it.evaluateList)
                if (evaluates.isEmpty())
                    mRecyclerLayout.setLoadMoreText("暂无数据")
                if (it.evaluateList.isEmpty()&&page != 1)
                    mRecyclerLayout.setLoadMoreText("没有更多")
            }
        }, error = { _, _->
            mRecyclerLayout.isRefreshing = false
        })
    }
}