package com.hbcx.driver.ui.login

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.text.TextUtils
import android.widget.TextView
import cn.qqtheme.framework.entity.CarNumberCity
import cn.qqtheme.framework.entity.CarNumberProvince
import cn.qqtheme.framework.picker.CarNumberPicker
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.util.ConvertUtils
import cn.sinata.xldutils.activity.SelectPhotoDialog
import cn.sinata.xldutils.utils.isValidIdCard
import cn.sinata.xldutils.utils.isValidPhone
import cn.sinata.xldutils.utils.optString
import com.amap.api.col.sln3.it
import com.hbcx.driver.R
import com.hbcx.driver.R.id.*
import com.hbcx.driver.dialogs.SelectCarTypeDialog
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.network.beans.CarType
import com.hbcx.driver.utils.AuthenTicationUtil
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_driver_join.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*

class DriverJoinActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    private var carTypeList = mutableListOf<CarType>()
    private var driveCardUrl = ""
    private var driveLicenseUrl = ""
    private var insuranceUrl = ""
    private var carPersonUrl = ""
    private var carType = 0
    private val countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
        override fun onFinish() {
            tv_get_code.text = "重新获取"
            tv_get_code.isEnabled = true
        }

        override fun onTick(millisUntilFinished: Long) {
            tv_get_code.text = String.format("%s", millisUntilFinished / 1000)
        }
    }

    override fun initView() {
        title = "填写资料"
        getCarTypeList()
    }

    override fun setContentView(): Int {
        return R.layout.activity_driver_join
    }

    override fun initClick() {
        tv_sex.setOnClickListener {
            val selectSexDialog = com.hbcx.driver.dialogs.SelectSexDialog()
            selectSexDialog.setDialogListener { _, s ->
                tv_sex.text = s
            }
            selectSexDialog.show(fragmentManager, "sex")
        }
        tv_drive_year.setOnClickListener {
            showDateTimePicker(tv_drive_year)
        }
        tv_get_code.setOnClickListener {
            sendSms()
        }
        iv_driver_card.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 0)
        }

        tv_car_type.setOnClickListener {
            val carTypeDialog = SelectCarTypeDialog()
            carTypeDialog.arguments = bundleOf("data" to carTypeList)
            carTypeDialog.setDialogListener { p, s, p1, s1 ->
                carType = carTypeList[p].list[p1].id
                tv_car_type.text = s1
            }
            carTypeDialog.show(fragmentManager, "carType")
        }

        tv_car_color.setOnClickListener {
            val carColorDialog = com.hbcx.driver.dialogs.SelectColorDialog()
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

        tv_check_time.setOnClickListener {
            showDateTimePicker(tv_check_time)
        }

        iv_driving_license.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 1)
        }
        iv_insurance.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 2)
        }
        iv_car_person.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 3)
        }

        btn_commit.setOnClickListener {
            val name = et_name.text.toString()
            if (name.isEmpty()) {
                toast("姓名不能为空")
                return@setOnClickListener
            }
            val num = et_num.text.toString()
            if (num.isEmpty()){
                toast("档案编号不能为空")
                return@setOnClickListener
            }
            if (AuthenTicationUtil.IDCardValidate(num)!="YES"){
                toast("请输入正确的档案编号")
                return@setOnClickListener
            }
            val sex = when (tv_sex.text.toString()) {
                "男" -> 1
                "女" -> 2
                else -> {
                    toast("请选择性别")
                    return@setOnClickListener
                }
            }
            val idCard = et_id_card.text.toString().trim()
            if (!idCard.isValidIdCard()) {
                toast("请输入正确身份证号")
                return@setOnClickListener
            }
            val driveYear = tv_drive_year.text.toString().trim()
            if (driveYear.isEmpty()) {
                toast("请选择驾龄")
                return@setOnClickListener
            }
            val phone = et_phone.text.toString().trim()
            if (!phone.isValidPhone()) {
                toast("请输入正确手机号")
                return@setOnClickListener
            }
            val code = et_code.text.toString().trim()
            if (code.length != 6) {
                toast("请输入六位短信验证码")
                return@setOnClickListener
            }
            if (driveCardUrl.isEmpty()) {
                toast("请上传驾驶证")
                return@setOnClickListener
            }
            if (carType == 0) {
                toast("请选择车型")
                return@setOnClickListener
            }
            val color = tv_car_color.text.toString().trim()
            if (color.isEmpty()) {
                toast("请选择车辆颜色")
                return@setOnClickListener
            }
            var carNum = et_car_num.text.toString().trim()
            if (carNum.length != 5) {
                toast("请输入正确的车牌号")
                return@setOnClickListener
            } else
                carNum = tv_car_num.text.toString() + carNum
            val checkTime = tv_check_time.text.toString().trim()
            if (checkTime.isEmpty()) {
                toast("请选择年审时间")
                return@setOnClickListener
            }
            if (driveLicenseUrl.isEmpty()) {
                toast("请上传行驶证")
                return@setOnClickListener
            }
            if (insuranceUrl.isEmpty()) {
                toast("请上传保险合同")
                return@setOnClickListener
            }
            if (carPersonUrl.isEmpty()) {
                toast("请上传人车合影")
                return@setOnClickListener
            }
            driverRegister(phone, idCard, driveYear, sex, name, driveCardUrl, code, carType, color, carNum, checkTime, driveLicenseUrl, carPersonUrl, insuranceUrl,num)
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
//            val ossUtil = OSSUtil(this)
//            ossUtil.uploadSingle(data.getStringExtra("path"),object :OSSUtil.OSSUploadCallBack(){
//                override fun onFinish(url: String?) {
//                    when(requestCode){
//                        0->{
//                            iv_driver_card.setImageURI(url)
//                            driveCardUrl = url!!
//                        }
//                        1->{
//                            iv_driving_license.setImageURI(url)
//                            driveLicenseUrl = url!!
//                        }
//                        2->{
//                            iv_insurance.setImageURI(url)
//                            insuranceUrl = url!!
//                        }
//                        3->{
//                            iv_car_person.setImageURI(url)
//                            carPersonUrl = url!!
//                        }
//                    }
//                    dismissDialog()
//                }
//
//                override fun onFial(message: String?) {
//                    super.onFial(message)
//                    dismissDialog()
//                }
//            })
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
                            HttpManager.uploadFile(file).request(this@DriverJoinActivity){ _, data->
                                data?.let {
                                    val s = it.optString("imgUrl")
                                    when(requestCode){
                                        0->{
                                            iv_driver_card.setImageURI(s)
                                            driveCardUrl = s
                                        }
                                        1->{
                                            iv_driving_license.setImageURI(s)
                                            driveLicenseUrl = s
                                        }
                                        2->{
                                            iv_insurance.setImageURI(s)
                                            insuranceUrl = s
                                        }
                                        3->{
                                            iv_car_person.setImageURI(s)
                                            carPersonUrl = s
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

    private fun sendSms() {
        val phone = et_phone.text.toString().trim()
        if (phone.isEmpty()) {
            toast("手机号不能为空")
            return
        }
        if (!phone.isValidPhone()) {
            toast("请输入正确手机号")
            return
        }
        showDialog()
        com.hbcx.driver.network.HttpManager.sendSms(phone, 4).request(this) { msg, _ ->
            if (isDestroy) {
                return@request
            }
            toast(msg.toString())
            tv_get_code.isEnabled = false
            countDownTimer.start()
        }
    }

    private fun getCarTypeList() {
        HttpManager.getCarTypeList().request(this) { _, data ->
            carTypeList = data as MutableList<com.hbcx.driver.network.beans.CarType>
        }
    }

    private fun driverRegister(phone: String, idCards: String, drivingAge: String, sex: Int, nickName: String
                               , driverLicensePhotograph: String, code: String, carModelId: Int, carColor: String
                               , licensePlate: String, annualTrial: String, drivingLicense: String
                               , bodyIllumination: String, strongInsurance: String,DriversNumbers:String) {
        btn_commit.isClickable = false
        HttpManager.driverRegister(phone, idCards, drivingAge, sex, nickName
                , driverLicensePhotograph, code, carModelId, carColor
                , licensePlate, annualTrial, drivingLicense
                , bodyIllumination, strongInsurance,DriversNumbers).request(this, true, { msg, _ ->
            toast(msg!!)
            startActivity<com.hbcx.driver.ui.login.LoginActivity>()
        }, { _, _ ->
            btn_commit.isClickable = true
        })
    }
}