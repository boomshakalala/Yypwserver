package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.adapter.ClassManageAdapter
import com.hbcx.driver.adapter.LineManageAdapter
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.ClassModel
import com.hbcx.driver.network.beans.Line
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import org.jetbrains.anko.*

class ClassManageActivity : TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.base_recyclerview_layout

    override fun initClick() {
        adapter.setOnItemClickListener { view, position ->
            if (lines[position].status == -1)
                startActivityForResult<SetLinePriceActivity>(1,"id" to lines[position].id,"editable" to true)
            else
                startActivityForResult<ClassManageDetailActivity>(1, "id" to lines[position].id)
        }
    }

    private val lines = arrayListOf<ClassModel>()
    private val adapter by lazy {
        ClassManageAdapter(lines)
    }
    private lateinit var rootView: View
    private lateinit var mSwipeRefreshRecyclerLayout: SwipeRefreshRecyclerLayout
    override fun initView() {
        title = "班次管理"
        titleBar.addRightButton("添加班次", onClickListener = View.OnClickListener {
            startActivityForResult<AddClassActivity>(1)
        })
        rootView = find(R.id.rootFL)
        mSwipeRefreshRecyclerLayout = find(R.id.swipeRefreshLayout)

        rootView.backgroundResource = R.color.bg_grey
        rootView.setPadding(0, dip(10), 0, 0)
        mSwipeRefreshRecyclerLayout.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        mSwipeRefreshRecyclerLayout.setAdapter(adapter)
        mSwipeRefreshRecyclerLayout.setOnRefreshListener(object : SwipeRefreshRecyclerLayout.OnRefreshListener {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            page = 1
            getData()
        }
    }

    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }
    private var page = 1
    private fun getData() {
        HttpManager.getClassList(userId, page).request(this, success = { _, data ->
            mSwipeRefreshRecyclerLayout.isRefreshing = false
            data?.let {
                if (page == 1)
                    lines.clear()
                lines.addAll(it)
                if (lines.isEmpty())
                    mSwipeRefreshRecyclerLayout.setLoadMoreText("暂无线路数据")
                if (it.isEmpty() && page != 1)
                    mSwipeRefreshRecyclerLayout.setLoadMoreText("没有更多线路数据")
                adapter.notifyDataSetChanged()
            }
        }, error = { _, _ ->
            mSwipeRefreshRecyclerLayout.isRefreshing = false
        })
    }
}