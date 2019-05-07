package com.hbcx.driver.ui.account

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.util.ConvertUtils
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.SpanBuilder
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.adapter.IncomeHistoryAdapter
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.IncomeDetail
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_income_detail.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*

class IncomeDetailActivity : com.hbcx.driver.ui.TranslateStatusBarActivity(), SwipeRefreshRecyclerLayout.OnRefreshListener {
    override fun onRefresh() {
        page = 1
        getData()
    }

    override fun onLoadMore() {
        page++
        getData()
    }

    private val histories = arrayListOf<IncomeDetail>()

    private val adapter by lazy {
        IncomeHistoryAdapter(histories,true)
    }

    override fun setContentView() = R.layout.activity_income_detail

    override fun initClick() {
        tv_start.onClick {
            showDateTimePicker(tv_start)
        }
        tv_end.onClick {
            showDateTimePicker(tv_end)
        }
        tv_action.onClick {
            page = 1
            getData()
        }
    }

    override fun initView() {
        title = "收入明细"
        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setAdapter(adapter)
        getData()
    }

    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }
    private var page = 1
    private var startTime = ""
    private var endTime = ""
    private fun getData() {
        HttpManager.getIncomeData(userId, 1, page, startTime, endTime).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            data?.let {
                if (page == 1){
                    tv_money.text = SpanBuilder("￥${it.totalMoney}").size(0, 1, 14).style(0, 1, Typeface.NORMAL).build()
                    histories.clear()
                }
                histories.addAll(it.incomeDetailsList)
                if (histories.isEmpty())
                    mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                if (it.incomeDetailsList.isEmpty()&&page != 1)
                    mSwipeRefreshLayout.setLoadMoreText("没有更多")
            }
        }, error = { _, _ ->
            mSwipeRefreshLayout.isRefreshing = false
        })
    }

    /**
     * 时间选择
     */
    private fun showDateTimePicker(view: TextView) {
        val picker = DatePicker(this)
        picker.setCanceledOnTouchOutside(true)
        picker.setUseWeight(true)
        picker.setTopPadding(ConvertUtils.toPx(this, 10f))
        picker.setRangeEnd(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        picker.setRangeStart(1900, 1, 1)
        picker.setSelectedItem(1990, 1, 1)
        picker.setResetWhileWheel(false)
        picker.setDividerVisible(false)
        picker.setCancelTextColor(Color.parseColor("#999999"))
        picker.setTopLineColor(Color.parseColor("#999999"))
        picker.setOnDatePickListener(DatePicker.OnYearMonthDayPickListener { year, month, day ->
            view.text = "$year-$month-$day"
            if (view.id == R.id.tv_start)
                startTime = "$year-$month-$day"
            else
                endTime = "$year-$month-$day"
        })
        picker.show()
    }
}