package com.hbcx.driver.ui.ticketbus

import android.view.LayoutInflater
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.utils.toTime
import cn.sinata.xldutils.utils.toWeek
import com.hbcx.driver.R
import com.hbcx.driver.dialogs.EditPassengerDialog
import kotlinx.android.synthetic.main.activity_reserve_ticket.*
import kotlinx.android.synthetic.main.item_passenger_id_card.*
import kotlinx.android.synthetic.main.item_passenger_id_card.view.*
import kotlinx.android.synthetic.main.layout_pay_check.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class ReserveTicketActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    private var personCount = 1
    private var payWay: Int = 0 //0农行 1:支付宝 2:wx 3:cash

    override fun setContentView() = R.layout.activity_reserve_ticket

    private val inputDialog by lazy {
        EditPassengerDialog()
    }
    override fun initClick() {
        iv_edit.setOnClickListener {
            inputDialog.setDialogListener { p, s ->
                et_id_card.text = "$p $s"
            }
            inputDialog.show(supportFragmentManager,"edit")
        }
        tv_add.onClick {
            if (personCount != ticketNum) {
                tv_count.text = "${++personCount}人"
                val view = LayoutInflater.from(this@ReserveTicketActivity).inflate(R.layout.item_passenger_id_card, null)
                view.iv_edit.setOnClickListener {
                    inputDialog.setDialogListener { p, s ->
                        view.et_id_card.text = "$p $s"
                    }
                    inputDialog.show(supportFragmentManager,"edit")
                }
                ll_passenger.addView(view)
            }
        }
        tv_subtract.onClick {
            if (personCount != 1) {
                tv_count.text = "${--personCount}人"
                ll_passenger.removeViewAt(ll_passenger.childCount - 1)
            }
        }
        rl_bank.setOnClickListener {
            payWay = 0
            iv_check.isSelected = true
            iv_check_wx.isSelected = false
            iv_check_ali.isSelected = false
            iv_check_cash.isSelected = false
        }
        rl_wx.setOnClickListener {
            payWay = 2
            iv_check.isSelected = false
            iv_check_wx.isSelected = true
            iv_check_ali.isSelected = false
            iv_check_cash.isSelected = false
        }

        rl_ali.setOnClickListener {
            payWay = 1
            iv_check.isSelected = false
            iv_check_wx.isSelected = false
            iv_check_ali.isSelected = true
            iv_check_cash.isSelected = false
        }
        rl_cash.onClick {
            payWay = 3
            iv_check.isSelected = false
            iv_check_wx.isSelected = false
            iv_check_ali.isSelected = false
            iv_check_cash.isSelected = true
        }
        switch_btn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                //todo calculate
            } else {
                //todo calculate
            }
        }
        tv_action.onClick {
            toast("暂未开通站外售票")
            return@onClick
//            startActivity<com.hbcx.driver.ui.ticketbus.SoldSuccessActivity>()
        }
    }

    private val startCity by lazy {
        intent.getStringExtra("start")
    }
    private val endCity by lazy {
        intent.getStringExtra("end")
    }
    private val startPoint by lazy {
        intent.getStringExtra("startPoint")
    }
    private val endPoint by lazy {
        intent.getStringExtra("endPoint")
    }
    private val startTime by lazy {
        intent.getStringExtra("startTime")
    }
    private val endTime by lazy {
        intent.getStringExtra("endTime")
    }
    private val time by lazy {
        intent.getLongExtra("date", 0)
    }
    private val ticketNum by lazy {
        intent.getIntExtra("ticketNum", 3)
    }

    override fun initView() {
        title = "预订车票"
        iv_check.isSelected = true
        tv_start_city.text = startCity
        tv_end_city.text = endCity
        tv_start_address.text = startPoint
        tv_end_address.text = endPoint
        tv_time.text = String.format("%s %s %s上车", time.toTime("MM月dd日"), time.toWeek(), startTime)
        tv_arrive_time.text = if (endTime == null) "" else String.format("预计 %s到达", endTime)
        rl_bank.gone()
        rl_cash.gone()
    }
}