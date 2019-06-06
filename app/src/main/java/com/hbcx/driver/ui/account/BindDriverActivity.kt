package com.hbcx.driver.ui.account

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.adapter.BindDriverAdapter
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.Driver
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

class BindDriverActivity : TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.base_recyclerview_layout

    override fun initClick() {
        adapter.setOnItemClickListener { view, position ->
            if (checkIndex != -1)
                drivers[checkIndex].isChecked = false
            drivers[position].isChecked = true
            checkIndex = position
            adapter.notifyDataSetChanged()
        }
    }

    private val drivers = arrayListOf<Driver>()
    private val adapter by lazy {
        BindDriverAdapter(drivers)
    }
    private var checkIndex = -1
    private val recyclerLayout by lazy {
        find<SwipeRefreshRecyclerLayout>(R.id.swipeRefreshLayout)
    }

    override fun initView() {
        title = "关联司机"
        titleBar.addRightButton("确定", onClickListener = View.OnClickListener {
            if (checkIndex == -1){
                toast("请选择要关联的司机")
                return@OnClickListener
            }
            HttpManager.bindDriver(id,drivers[checkIndex].id).request(this){_,_->
                toast("关联成功")
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
        recyclerLayout.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
        recyclerLayout.setOnRefreshListener(object : SwipeRefreshRecyclerLayout.OnRefreshListener {
            override fun onRefresh() {
                page = 1
                getData()
            }

            override fun onLoadMore() {
                page++
                getData()
            }
        })
        recyclerLayout.setAdapter(adapter)
        getData()
    }

    private val id by lazy {
        intent.getIntExtra("id", 0)
    }
    private var page = 1
    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }
    private fun getData() {
        HttpManager.getIdelDriverList(userId,page).request(this, success = { _, data->
            recyclerLayout.isRefreshing = false
            if (page == 1)
                drivers.clear()
            data?.let {
                drivers.addAll(it)
                if (drivers.isEmpty())
                    recyclerLayout.setLoadMoreText("暂无数据")
                if (it.isEmpty()&&page!=1)
                    recyclerLayout.setLoadMoreText("没有更多")
            }
        }, error = { _, _->
            recyclerLayout.isRefreshing = false
        })
    }
}