package com.hbcx.driver.ui.cardriver


import cn.sinata.xldutils.utils.SpanBuilder
import cn.sinata.xldutils.utils.timeDay
import com.hbcx.driver.R
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_order_detail.*

class OrderDetailActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView(): Int = R.layout.activity_order_detail

    override fun initClick() {
    }

    override fun initView() {
        if (intent.getSerializableExtra("data") != null)
            order = intent.getSerializableExtra("data") as com.hbcx.driver.network.beans.Order
        title = "完成服务"
        if (order != null) {
            setUI()
        } else
            getData()

    }

    private fun setUI() {
        tv_content1.text = order!!.departureTime!!.timeDay()
        tv_content2.text = order!!.startAddress
        tv_content3.text = order!!.endAddress
        val p = String.format("%.2f元", order!!.orderMoney)
        tv_station_count.text = SpanBuilder(p)
                .size(p.length - 1, p.length, 14)
                .color(this, p.length - 1, p.length, R.color.black_text)
                .build()
        tv_mileage_price.text = String.format("里程费（%.2f公里）", order!!.mileage)
        tv_time_price.text = String.format("时长费（%d分钟）", order!!.duration)
        tv_long_price.text = String.format("远途费（%.2f公里）", order!!.longMileage)
        tv_price_1.text = "${order!!.mileageMoney}元"
        tv_price_2.text = "${order!!.durationMoney}元"
        tv_price_3.text = "${order!!.longDurationMoney}元"
        tv_price_4.text = "${order!!.nightMoney}元"
        tv_price_5.text = "${order!!.serverMoney}元"
    }

    private fun getData() {
        com.hbcx.driver.network.HttpManager.getOrderDetail(orderId).request(this) { _, data ->
            data?.let {
                this.order = it
                setUI()
            }
        }
    }

    private var order: com.hbcx.driver.network.beans.Order? = null

    private val orderId by lazy {
        intent.getIntExtra("orderId", 0)
    }

}
