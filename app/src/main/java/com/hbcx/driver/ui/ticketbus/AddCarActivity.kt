package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.widget.TextView
import cn.qqtheme.framework.entity.CarNumberCity
import cn.qqtheme.framework.entity.CarNumberProvince
import cn.qqtheme.framework.picker.CarNumberPicker
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.util.ConvertUtils
import cn.sinata.xldutils.activity.SelectPhotoDialog
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.optString
import com.hbcx.driver.R
import com.hbcx.driver.dialogs.SelectCarTypeDialog
import com.hbcx.driver.dialogs.SelectColorDialog
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.CarType
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_add_car.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*

class AddCarActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    private var carTypeList = mutableListOf<CarType>()
    private var carType = 0
    private var carBodyImg = "" //车身
    private var carLisenceImg = "" //行驶证
    private var lisenceImg = "" //营运证
    private var strongInsurance = "" //交强险
    private var commercialInsurance = "" //商业险
    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }

    override fun setContentView() = R.layout.activity_add_car

    override fun initClick() {
        tv_car_type.setOnClickListener {
            val carTypeDialog = SelectCarTypeDialog()
            carTypeDialog.arguments = bundleOf("data" to carTypeList)
            carTypeDialog.setDialogListener { p, s, p1, s1 ->
                carType = carTypeList[p].list[p1].id
                tv_car_type.text = s+s1
            }
            carTypeDialog.show(fragmentManager, "carType")
        }
        tv_car_color.setOnClickListener {
            val carColorDialog = SelectColorDialog()
            carColorDialog.arguments = bundleOf()
            carColorDialog.setDialogListener { _, s ->
                tv_car_color.text = s
            }
            carColorDialog.show(fragmentManager, "color")
        }
        tv_car_num.setOnClickListener {
            val carNumberPicker = CarNumberPicker(this)
            carNumberPicker.setOnPickListener { carNumberProvince: CarNumberProvince, carNumberCity: CarNumberCity, _ ->
                tv_car_num.text = "${carNumberProvince.name}${carNumberCity.name}"
            }
            carNumberPicker.setDividerVisible(false)
            carNumberPicker.setCancelTextColor(Color.parseColor("#999999"))
            carNumberPicker.setTopLineColor(Color.parseColor("#999999"))
            carNumberPicker.show()
        }
        tv_check_time.onClick {
            showDateTimePicker(tv_check_time)
        }
        iv_car_body.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 1)
        }
        iv_car_license.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 2)
        }
        iv_operation_certificate.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 3)
        }
        iv_strong_insurance.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 4)
        }
        iv_commercial_insurance.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 5)
        }
        btn_action.onClick {
            if (carType == 0){
                toast("请选择车型")
                return@onClick
            }
            val color = tv_car_color.text.toString()
            if (color.isEmpty()){
                toast("请选择车辆颜色")
                return@onClick
            }
            val carNum = et_car_num.text.toString().trim()
            if (carNum.length!=5){
                toast("请输入正确的车牌号")
                return@onClick
            }
            val checkTime = tv_check_time.text.toString()
            if (checkTime.isEmpty()){
                toast("请选择年审时间")
                return@onClick
            }
            val seatCount = et_seat_count.text.toString().trim()
            if (seatCount.isEmpty()||seatCount=="0"){
                toast("请输入座位数")
                return@onClick
            }
            if (carBodyImg.isEmpty()){
                toast("请上传车身照片")
                return@onClick
            }
            if (carLisenceImg.isEmpty()){
                toast("请上传行驶证")
                return@onClick
            }
            if (lisenceImg.isEmpty()){
                toast("请上传营运证")
                return@onClick
            }
            if (strongInsurance.isEmpty()){
                toast("请上传交强险图片")
                return@onClick
            }
            if (commercialInsurance.isEmpty()){
                toast("请上传商业险图片")
                return@onClick
            }
            btn_action.isEnabled = false
            HttpManager.addCar(carType,color,tv_car_num.text.toString()+carNum,seatCount.toInt()
                    ,userId,checkTime,carBodyImg,lisenceImg,carLisenceImg,strongInsurance,commercialInsurance).request(this@AddCarActivity, success = { msg, _->
                toast(msg!!)
                setResult(Activity.RESULT_OK)
                finish()
            }, error = { _, _->
                btn_action.isEnabled = true
            })
        }
    }

    /**
     * 时间选择
     */
    private fun showDateTimePicker(view: TextView) {
        val picker = DatePicker(this)
        picker.setCanceledOnTouchOutside(true)
        picker.setUseWeight(true)
        picker.setTopPadding(ConvertUtils.toPx(this, 10f))
        picker.setRangeEnd(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        picker.setRangeStart(1900, 1, 1)
        picker.setSelectedItem(1990, 1, 1)
        picker.setResetWhileWheel(false)
        picker.setDividerVisible(false)
        picker.setCancelTextColor(Color.parseColor("#999999"))
        picker.setTopLineColor(Color.parseColor("#999999"))
        picker.setOnDatePickListener(DatePicker.OnYearMonthDayPickListener { year, month, day ->
            view.text = "$year-$month-$day"
        })
        picker.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Luban.with(this)
                    .load(File(data.getStringExtra("path")))
                    .ignoreBy(100)
                    .setTargetDir(getPath())
                    .filter { path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")) }
                    .setCompressListener(object : OnCompressListener {
                        override fun onStart() {
                            showDialog(canCancel = false)
                        }
                        override fun onSuccess(file: File) {
                            HttpManager.uploadFile(file).request(this@AddCarActivity){ _, data->
                                data?.let {
                                    val s = it.optString("imgUrl")
                                    when(requestCode){
                                        1->{
                                            iv_car_body.setImageURI(s)
                                            carBodyImg = s
                                        }
                                        2->{
                                            iv_car_license.setImageURI(s)
                                            carLisenceImg = s
                                        }
                                        3->{
                                            iv_operation_certificate.setImageURI(s)
                                            lisenceImg = s
                                        }
                                        4->{
                                            iv_strong_insurance.setImageURI(s)
                                            strongInsurance = s
                                        }
                                        5->{
                                            iv_commercial_insurance.setImageURI(s)
                                            commercialInsurance = s
                                        }
                                    }
                                    dismissDialog()
                                }
                            }
                        }
                        override fun onError(e: Throwable) {
                            dismissDialog()
                        }
                    }).launch()
        }
    }

    override fun initView() {
        title = "添加车辆"
        getCarTypeList()
    }

    private fun getCarTypeList() {
        HttpManager.getCarTypeList().request(this) { _, data ->
            carTypeList = data as MutableList<com.hbcx.driver.network.beans.CarType>
        }
    }
}