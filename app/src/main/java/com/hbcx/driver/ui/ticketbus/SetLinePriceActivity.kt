package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import cn.sinata.xldutils.visible
import com.google.gson.Gson
import com.hbcx.driver.R
import com.hbcx.driver.adapter.SetLinePriceAdapter
import com.hbcx.driver.dialogs.SetPriceDialog
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.LinePrice
import com.hbcx.driver.network.beans.UpdataLinePrice
import com.hbcx.driver.ui.TranslateStatusBarActivity
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_verify_phone_for_card.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class SetLinePriceActivity:TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_line_price_list

    private var editable:Boolean = false //是否为编辑状态

    override fun initClick() {
        btn_action.onClick {
            val list = arrayListOf<UpdataLinePrice>()
            list.addAll(lines.map {
                if (it.salesMoney <= 0.0){
                    toast("价格不能小于0")
                    return@onClick
                }
                UpdataLinePrice(it.id,it.startStationId,it.endStationId,it.lineId,it.salesMoney)
            })
            HttpManager.setLinePriceList(Gson().toJson(list)).request(this@SetLinePriceActivity){_,_->
                toast("设置价格成功")
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private lateinit var mSwipeRefreshRecyclerLayout: SwipeRefreshRecyclerLayout

    private val priceDialog by lazy {
        SetPriceDialog()
    }
    private val lines = arrayListOf<LinePrice>()
    private val adapter by lazy {
        val a = SetLinePriceAdapter(lines)
        a.setOnPriceClicker {
            if (!editable)
                return@setOnPriceClicker
            priceDialog.setDialogListener { p, s ->
                lines[it].salesMoney = s!!.toDouble()
                a.notifyDataSetChanged()
            }
            priceDialog.show(supportFragmentManager,"price")
        }
        a
    }
    override fun initView() {
        title = "设置票价"
        editable = intent.getBooleanExtra("editable",false)
        if (!editable){ //不可编辑
            titleBar.addRightButton("编辑",onClickListener = View.OnClickListener {
                editable = true
                titleBar.hideAllRightButton()
                btn_action.visible()
            })
        } else{
            titleBar.hideAllRightButton()
            btn_action.visible()
        }
        mSwipeRefreshRecyclerLayout = find(R.id.swipeRefreshLayout)
        mSwipeRefreshRecyclerLayout.setLayoutManager(LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false))
        mSwipeRefreshRecyclerLayout.setMode(SwipeRefreshRecyclerLayout.Mode.None)
        mSwipeRefreshRecyclerLayout.setAdapter(adapter)
        getData()
    }

    private val id by lazy {
        intent.getIntExtra("id",0)
    }
    private fun getData(){
        HttpManager.getLinePriceList(id).request(this){_,data->
            lines.addAll(data!!)
            adapter.notifyDataSetChanged()
        }
    }
}