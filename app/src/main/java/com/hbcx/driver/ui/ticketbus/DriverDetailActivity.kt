package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.view.View
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_driver_detail.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.textColorResource
import org.jetbrains.anko.toast

class DriverDetailActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_driver_detail

    private val id by lazy {
        intent.getIntExtra("id",0)
    }
    override fun initClick() {
    }

    override fun initView() {
        title = "司机详情"
        titleBar.addRightButton("删除司机",onClickListener = View.OnClickListener {
            val tipDialog = com.hbcx.driver.dialogs.TipDialog()
            tipDialog.arguments = bundleOf("msg" to "确定删除该司机?","ok" to "确定","cancel" to "取消")
            tipDialog.setDialogListener { p, s ->
                HttpManager.delDriver(id).request(this){_,_->
                    toast("删除成功")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            tipDialog.show(supportFragmentManager,"delete")
        })
        getData()
    }

    private fun getData(){
        HttpManager.getDriverDetail(id).request(this){_,data->
            data?.let {
                tv_status.text = data.getStatusStr()
                tv_status.textColorResource = data.getStatusColorRes()
                iv_head.setImageURI(data.imgUrl)
                tv_name.text = it.nickName
                tv_sex.text = if (it.sex == 1) "男" else "女"
                tv_id_card.text = it.idCards
                tv_drive_year.text = String.format("%d年",it.drivingAge)
                tv_car_info.text = if (data.isCar == 1) "待关联车辆" else "${data.licensePlate} ${data.brandName}${data.modelName} ${data.carColor}"
                iv_license.setImageURI(it.driverLicensePhotograph)
            }
        }
    }
}