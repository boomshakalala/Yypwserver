package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import cn.sinata.xldutils.defaultScheduler
import cn.sinata.xldutils.ioScheduler
import cn.sinata.xldutils.rxutils.ResultException
import cn.sinata.xldutils.view.SwipeRefreshRecyclerLayout
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.hbcx.driver.R
import com.hbcx.driver.adapter.TipAdapter
import com.hbcx.driver.ui.TranslateStatusBarActivity
import io.reactivex.Flowable
import io.reactivex.subscribers.DisposableSubscriber
import kotlinx.android.synthetic.main.activity_search_address.*
import org.jetbrains.anko.toast


class SearchPlaceActivity : TranslateStatusBarActivity() {
    private val region by lazy {//城市
        intent.getStringExtra("region")
    }
    private val city by lazy {//城市
        intent.getStringExtra("city")
    }

    override fun setContentView() = R.layout.activity_search_address

    override fun initClick() {//清空历记录
//        clearHistoryView.setOnClickListener {
//            HistoryDBManager().clearHistory(this, DBHelper.HISTORY_RENT_TABLE_NAME)
//            clearHistoryView.gone()
//            mTips.clear()
//            tipAdapter.notifyDataSetChanged()
//        }

        et_content.addTextChangedListener(textWatcher)

        tipAdapter.setOnItemClickListener { _, position ->
            val tip = mTips[position]
//            HistoryDBManager().saveHistory(this, tip, DBHelper.HISTORY_RENT_TABLE_NAME)
            val intent = Intent()
            intent.putExtra("data", tip)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun initView() {
        title = "沿途站点地址"
        mSwipeRefreshLayout.setMode(SwipeRefreshRecyclerLayout.Mode.None)
        mSwipeRefreshLayout.setLayoutManager(LinearLayoutManager(this))
        mSwipeRefreshLayout.setAdapter(tipAdapter)
//        tipAdapter.setClearView(clearHistoryView)
//        clearHistoryView.gone()
//        val history = HistoryDBManager().getAddressList(this, 10, DBHelper.HISTORY_RENT_TABLE_NAME)
//        if (history.isNotEmpty()) {
//            mTips.clear()
//            mTips.addAll(history)
//            //显示清空按钮
//            clearHistoryView.visible()
//            tipAdapter.notifyDataSetChanged()
//        }
        search(region)
    }

    private val mTips = ArrayList<Tip>()

    private val tipAdapter by lazy {
        TipAdapter(mTips)
    }

//    private val clearHistoryView by lazy {
//        layoutInflater.inflate(R.layout.view_clear_history, mSwipeRefreshLayout.mRecyclerView, false)
//    }

    private var disposable: DisposableSubscriber<List<Tip>>? = null

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

            if (s == null || s.isEmpty()) {
                search(region)
            } else {
                search(s.toString())
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    private fun search(keyword: String) {
        //取消以前的订阅
        if (disposable != null && !disposable!!.isDisposed) {
            disposable?.dispose()
        }
        val query = InputtipsQuery(keyword, city)
        query.cityLimit = true
        val inputtips = Inputtips(this, query)

        disposable = object : DisposableSubscriber<List<Tip>>() {

            override fun onComplete() {

            }

            override fun onNext(t: List<Tip>?) {
//                clearHistoryView.gone()
                mTips.clear()
                if (t == null) {

                } else {
                    mTips.addAll(t.filter { it.point != null && (it.point.latitude != 0.0 || it.point.longitude != 0.0) })
                }
                tipAdapter.notifyDataSetChanged()
            }

            override fun onError(t: Throwable?) {
//                clearHistoryView.gone()
                if (t is ResultException) {
                    toast(t.message.toString())
                } else {
                    toast("搜索出错啦！")
                }
            }
        }
        Flowable.just(inputtips).ioScheduler().flatMap {
            val list = try {
                it.requestInputtips()
            } catch (e: Exception) {
                null
            }
            if (list == null) {
                Flowable.error(ResultException("没有搜索到相关数据"))
            } else {
                Flowable.just(list)
            }
        }.defaultScheduler().subscribe(disposable)
    }

    override fun onDestroy() {
        try {
            et_content.removeTextChangedListener(textWatcher)
        } catch (e: Exception) {

        }
        super.onDestroy()
    }
}
