package com.hbcx.driver.ui.login

import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import android.text.TextUtils
import cn.sinata.xldutils.activity.SelectPhotoDialog
import cn.sinata.xldutils.utils.isValidPhone
import cn.sinata.xldutils.utils.optString
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_company_join.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

class CompanyJoinActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    private var licenseUrl = "" //营业执照
    private var operationUrl = "" //运营资质
    private val countDownTimer = object : CountDownTimer(60 * 1000, 1000) {
        override fun onFinish() {
            tv_get_code.text = "重新获取"
            tv_get_code.isEnabled = true
        }

        override fun onTick(millisUntilFinished: Long) {
            tv_get_code.text = String.format("%s", millisUntilFinished / 1000)
        }
    }
    private val cities = arrayListOf<com.hbcx.driver.network.beans.OpenCity>()
    private var cityCode = ""
    private val types = arrayListOf("票务","租车","包车")
    private var type = 0

    override fun initView() {
        title = "填写资料"
        getOpenCity()
    }

    override fun setContentView(): Int {
        return R.layout.activity_company_join
    }

    override fun initClick() {
        tv_get_code.setOnClickListener {
            sendSms()
        }
        iv_business_license.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 0)
        }
        iv_qualification.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 1)
        }
        tv_company_area.onClick {
            val dialog = com.hbcx.driver.dialogs.SelectColorDialog()
            dialog.arguments = bundleOf("data" to cities.map { it.cityName })
            dialog.setDialogListener { p, s ->
                tv_company_area.text = s
                cityCode = cities[p].cityCode
            }
            dialog.show(fragmentManager,"area")
        }
        tv_server_type.onClick {
            val dialog = com.hbcx.driver.dialogs.SelectColorDialog()
            dialog.arguments = bundleOf("data" to types)
            dialog.setDialogListener { p, s ->
                tv_server_type.text = s
                type = p+1
            }
            dialog.show(fragmentManager,"type")
        }
        btn_commit.setOnClickListener {
            val company = et_company_name.text.toString()
            if (company.isEmpty()) {
                toast("请输入公司名字")
                return@setOnClickListener
            }
            val driverNum = et_driver_num.text.toString()
            if (driverNum.isEmpty()) {
                toast("请输入司机数量")
                return@setOnClickListener
            }
            val carNum = et_car_num.text.toString()
            if (carNum.isEmpty()) {
                toast("请输入车辆数量")
                return@setOnClickListener
            }
            if (type == 0){
                toast("请选择服务类型")
                return@setOnClickListener
            }
            val area = tv_company_area.text.toString()
            if (area.isEmpty()) {
                toast("请选择公司所在地区")
                return@setOnClickListener
            }
            val address = et_address.text.toString()
            if (address.isEmpty()) {
                toast("请输入您的详细地址")
                return@setOnClickListener
            }
            val name = et_name.text.toString()
            if (name.isEmpty()) {
                toast("请输入您的姓名")
                return@setOnClickListener
            }
            val phone = et_phone.text.toString().trim()
            if (!phone.isValidPhone()) {
                toast("请输入有效手机号码")
                return@setOnClickListener
            }
            val code = et_code.text.toString().trim()
            if (code.length != 6) {
                toast("请输入六位短信验证码")
                return@setOnClickListener
            }
            if (licenseUrl.isEmpty()) {
                toast("请上传营业执照")
                return@setOnClickListener
            }
            if (operationUrl.isEmpty()) {
                toast("请上传驾营运资质")
                return@setOnClickListener
            }
            com.hbcx.driver.network.HttpManager.companyRegister(phone, cityCode, type, driverNum, company, code, address, operationUrl, licenseUrl, carNum, 0.0,0.0,name).request(this) { msg, _ ->
                toast(msg!!)
                startActivity<com.hbcx.driver.ui.login.LoginActivity>()
            }
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

    private fun getOpenCity() {
        com.hbcx.driver.network.HttpManager.getOpenCity().request(this) { _, data ->
            if (data != null)
                cities.addAll(data)
        }
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
                            HttpManager.uploadFile(file).request(this@CompanyJoinActivity) { _, data ->
                                data?.let {
                                    val s = it.optString("imgUrl")
                                    when (requestCode) {
                                        0 -> {
                                            iv_business_license.setImageURI(s)
                                            licenseUrl = s
                                        }
                                        1 -> {
                                            iv_qualification.setImageURI(s)
                                            operationUrl = s
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
}
