package com.hbcx.driver.ui.ticketbus

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.util.ConvertUtils
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_history.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import java.util.*

class HistoryActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
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

    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }
    private val history = arrayListOf<com.hbcx.driver.network.beans.TicketBus>()

    private val adapter by lazy {
        val l = com.hbcx.driver.adapter.HistoryAdapter(history)
        l.setOnDelete {
            com.hbcx.driver.network.HttpManager.delTicketHistory(it).request(this){ _, _->
                toast("删除成功")
                showDialog()
                page = 1
                getData()
            }
        }
        l
    }

    override fun initView() {
        title = "历史服务"
        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        mSwipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.Both)
        mSwipeRefreshLayout.setAdapter(adapter)
        mSwipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshRecyclerLayout.OnRefreshListener {
            override fun onRefresh() {
                page = 1
                getData()
            }

            override fun onLoadMore() {
                page++
                getData()
            }
        })
        getData()
    }

    override fun setContentView() = R.layout.activity_history

    private var page = 1
    private var startTime = ""
    private var endTime = ""
    private fun getData() {
        com.hbcx.driver.network.HttpManager.getTicketHistory(userId, page, startTime, endTime).request(this, success = { _, data ->
            mSwipeRefreshLayout.isRefreshing = false
            data?.let {
                if (page == 1)
                    history.clear()
                history.addAll(it)
                if (it.isEmpty())
                    if (page == 1)
                        mSwipeRefreshLayout.setLoadMoreText("暂无数据")
                    else
                        mSwipeRefreshLayout.setLoadMoreText("没有更多记录")
                adapter.notifyDataSetChanged()
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
        picker.setRangeStart(2018, 10, 10)
        picker.setSelectedItem(2018, 10, 10)
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