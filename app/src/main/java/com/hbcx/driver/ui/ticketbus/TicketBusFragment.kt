package com.hbcx.driver.ui.ticketbus

import android.widget.TextView
import cn.sinata.xldutils.fragment.RecyclerFragment
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.toTime
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import cn.sinata.xldutils.visible
import com.hbcx.driver.R
import com.hbcx.driver.adapter.TicketBusAdapter
import com.hbcx.driver.dialogs.TipDialog
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.TicketBus
import com.hbcx.driver.utils.requestByF
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class TicketBusFragment:RecyclerFragment() {
    companion object {
        fun newInstance(time:Long):TicketBusFragment{
            val fragment = TicketBusFragment()
            fragment.arguments = bundleOf("time" to time)
            return fragment
        }
    }

    private val userId by lazy {
        SPUtils.instance().getInt(com.hbcx.driver.utils.Const.User.USER_ID)
    }
    private val time by lazy {
        (arguments?.getLong("time")?:0)
    }
    private val lines = arrayListOf<com.hbcx.driver.network.beans.TicketBus>()
    private val adapter by lazy {
        TicketBusAdapter(lines, time.toTime("yyyy-MM-dd") == System.currentTimeMillis().toTime("yyyy-MM-dd"), object : TicketBusAdapter.OnItemClickListener {
            override fun onItemClick(id: Int) {
                startActivity<LineDetailActivity>("id" to id, "time" to time.toTime("yyyy-MM-dd"),"isCurrentDay" to (time.toTime("yyyy-MM-dd") == System.currentTimeMillis().toTime("yyyy-MM-dd")))
            }

            override fun onPassengerList(id: Int) {
                startActivity<PassengersActivity>("id" to id, "time" to time.toTime("yyyy-MM-dd"))
            }

            override fun onSaleTicket(data: TicketBus) {
                startActivity<SaleTicketActivity>("start" to data.startCityName, "end" to data.endCityName,
                        "date" to time, "id" to data.id)
            }

            override fun onArrived(id: Int) {
                val tipDialog = TipDialog()
                tipDialog.arguments = bundleOf("msg" to "是否确认到达？", "cancel" to "取消", "ok" to "确认")
                tipDialog.setDialogListener { p, s ->
                    HttpManager.busArrived(userId, id).requestByF(this@TicketBusFragment) { _, _ ->
                        toast("辛苦了")
                        showDialog()
                        getData()
                    }
                }
                tipDialog.show(fragmentManager, "start")
            }

            override fun onStarted(id: Int) {
                val tipDialog = TipDialog()
                tipDialog.arguments = bundleOf("msg" to "是否确认发车？", "cancel" to "取消", "ok" to "确认")
                tipDialog.setDialogListener { p, s ->
                    HttpManager.busStart(userId, id).requestByF(this@TicketBusFragment) { _, _ ->
                        toast("请注意安全，规范驾驶")
                        showDialog()
                        getData()
                    }
                }
                tipDialog.show(fragmentManager, "start")
            }
        })
    }
    override fun setAdapter() = adapter

    override fun getMode(): SwipeRefreshRecyclerLayout.Mode {
        return SwipeRefreshRecyclerLayout.Mode.Top
    }

    override fun pullDownRefresh() {
        getData()
    }

    override fun onFirstVisibleToUser() {
        emptyView = find(R.id.tv_empty)
        emptyView.setCompoundDrawablesWithIntrinsicBounds(0,R.mipmap.no_bus,0,0)
        getData()
    }

    override fun onVisibleToUser() {
        mSwipeRefreshLayout.isRefreshing = true
        getData()
    }

    private lateinit var emptyView: TextView
    private fun getData(){
        HttpManager.getTicketList(userId,time.toTime("yyyy-MM-dd")).requestByF(this,success = { _, data->
            mSwipeRefreshLayout.isRefreshing = false
            data?.let {
                lines.clear()
                lines.addAll(it)
                if (lines.isEmpty()){
                    emptyView.visible()
                }else
                    emptyView.gone()
                adapter.notifyDataSetChanged()
            }
        },error = {_,_->
            mSwipeRefreshLayout.isRefreshing = false
        })
    }
}