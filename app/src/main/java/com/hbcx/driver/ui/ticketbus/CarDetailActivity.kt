package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.view.View
import cn.sinata.xldutils.gone
import cn.sinata.xldutils.visible
import com.hbcx.driver.R
import com.hbcx.driver.dialogs.TipDialog
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.ui.account.BindDriverActivity
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_car_detail.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.textColorResource
import org.jetbrains.anko.toast

class CarDetailActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_car_detail

    private val tipDialog by lazy {
        val dialog = TipDialog()
        dialog.arguments = bundleOf("msg" to "是否取消司机关联","ok" to "确定","cancel" to "取消")
        dialog
    }

    override fun initClick() {
        tv_cancel.onClick {
            tipDialog.setDialogListener { p, s ->
                HttpManager.bindDriver(id,null).request(this@CarDetailActivity){_,_->
                    toast("取消关联成功")
                    setResult(Activity.RESULT_OK)
                    getData()
                }
            }
            tipDialog.show(supportFragmentManager,"cancel")
        }
        tv_action.onClick {
            startActivityForResult<BindDriverActivity>(1,"id" to id)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK)
            getData()
        }
    }

    private val id by lazy {
        intent.getIntExtra("id",0)
    }

    override fun initView() {
        title = "车辆详情"
        getData()
    }

    private fun getData(){
        HttpManager.getCarDetail(id).request(this){_,data->
            data?.let {
                tv_status.text = it.getStatusStr()
                tv_status.textColorResource = it.getStatusColorRes()
                tv_car_type.text = "${it.brandName}${it.modelName}"
                tv_color.text = it.carColor
                tv_license.text = it.licensePlate
                tv_check_time.text = it.annualTrial
                tv_seat_count.text = String.format("%d座",it.pedestal)
                iv_license.setImageURI(it.bodyIllumination)
                iv_car_license.setImageURI(it.drivingLicense)
                iv_safe.setImageURI(it.strongInsurance)
                iv_business_safe.setImageURI(it.commercialInsurance)
                if (it.status == 7)
                    titleBar.addRightButton("删除车辆",onClickListener = View.OnClickListener {
                        val tipDialog = com.hbcx.driver.dialogs.TipDialog()
                        tipDialog.arguments = bundleOf("msg" to "确定删除该车辆？","ok" to "确定","cancel" to "取消")
                        tipDialog.setDialogListener { p, s ->
                            HttpManager.delCar(id).request(this){_,_->
                                toast("删除成功")
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                        tipDialog.show(supportFragmentManager,"delete")
                    })
                when(it.status){
                    2-> {
                        ll_bottom.visible()
                        tv_cancel.gone()
                    }
                    3->{
                        ll_bottom.visible()
                        tv_cancel.visible()
                    }
                    else-> ll_bottom.gone()

                }
            }
        }
    }
}