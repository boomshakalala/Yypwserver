package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.hbcx.driver.R
import com.hbcx.driver.adapter.CarManageAdapter
import com.hbcx.driver.dialogs.TipDialog
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.Car
import com.hbcx.driver.ui.account.BindDriverActivity
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_car_manage.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class CarManageActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_car_manage

    override fun initClick() {
        btn_add.onClick {
            startActivityForResult<AddCarActivity>(1)
        }
    }

    private val tipDialog by lazy {
        val dialog = TipDialog()
        dialog.arguments = bundleOf("msg" to "是否取消司机关联","ok" to "确定","cancel" to "取消")
        dialog
    }

    private val cars = arrayListOf<Car>()
    private val adapter by lazy {
        CarManageAdapter(cars,object :CarManageAdapter.OnClickCallback{
            override fun bindDriver(id: Int) {
                startActivityForResult<BindDriverActivity>(1,"id" to id)
            }

            override fun unbindDriver(id: Int) {
                tipDialog.setDialogListener { p, s ->
                    HttpManager.bindDriver(id,null).request(this@CarManageActivity){_,_->
                        toast("取消关联成功")
                        page = 1
                        getData()
                    }
                }
                tipDialog.show(supportFragmentManager,"cancel")
            }
        })
    }
    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }

    override fun initView() {
        title = "车辆管理"
        swipeRefreshLayout.setLayoutManager(LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false))
        swipeRefreshLayout.setAdapter(adapter)
        swipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshRecyclerLayout.OnRefreshListener{
            override fun onRefresh() {
                page = 1
                getData()
            }

            override fun onLoadMore() {
                page++
                getData()
            }

        })
        adapter.setOnItemClickListener { view, position ->
            startActivityForResult<CarDetailActivity>(1,"id" to cars[position].id)
        }
        getData()
    }

    private var page = 1
    private fun getData(){
        HttpManager.getCarList(userId,page).request(this,success = {_,data->
            swipeRefreshLayout.isRefreshing = false
            data?.let {
                if (page == 1)
                    cars.clear()
                cars.addAll(it)
                if (cars.isEmpty())
                    swipeRefreshLayout.setLoadMoreText("暂无数据")
                if (it.isEmpty()&&page!=1)
                    swipeRefreshLayout.setLoadMoreText("没有更多")
                adapter.notifyDataSetChanged()
            }
        },error = {_,_->
            swipeRefreshLayout.isRefreshing = false
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            page = 1
            getData()
        }
    }
}