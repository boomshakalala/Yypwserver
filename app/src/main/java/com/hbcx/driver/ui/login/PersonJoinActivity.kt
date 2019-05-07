package com.hbcx.driver.ui.login

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.text.TextUtils
import android.widget.TextView
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.util.ConvertUtils
import cn.sinata.xldutils.activity.SelectPhotoDialog
import cn.sinata.xldutils.utils.isValidIdCard
import cn.sinata.xldutils.utils.isValidPhone
import cn.sinata.xldutils.utils.optString
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.AuthenTicationUtil
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_person_join.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*

class PersonJoinActivity : com.hbcx.driver.ui.TranslateStatusBarActivity() {
    private var headUrl = "" //头像
    private var licenseUrl = "" //运营资质
    private var idCardUrl = "" //身份证
    private var driveCardUrl = "" //驾驶证证
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
    }

    override fun setContentView(): Int {
        return R.layout.activity_person_join
    }

    override fun initClick() {
        tv_sex.setOnClickListener {
            val selectSexDialog = com.hbcx.driver.dialogs.SelectSexDialog()
            selectSexDialog.setDialogListener { _, s ->
                tv_sex.text = s
            }
            selectSexDialog.show(fragmentManager, "sex")
        }
        tv_get_code.setOnClickListener {
            sendSms()
        }
        iv_head.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 0)
        }
        iv_id_card.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 1)
        }
        iv_qualification.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 2)
        }
        iv_driver_card.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 3)
        }
        tv_drive_year.setOnClickListener {
            showDateTimePicker(tv_drive_year)
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
            val driveYear = tv_drive_year.text.toString().trim()
            if (driveYear.isEmpty()) {
                toast("请选择驾龄")
                return@setOnClickListener
            }
            val idCard = et_id_card.text.toString().trim()
            if (!idCard.isValidIdCard()) {
                toast("请输入正确身份证号")
                return@setOnClickListener
            }
            val lineNum = tv_line_count.text.toString().trim()
            if (lineNum.isEmpty()){
                toast("请输入运营线路数量")
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
            if (headUrl.isEmpty()) {
                toast("请上传头像")
                return@setOnClickListener
            }
            if (idCardUrl.isEmpty()) {
                toast("请上传身份证")
                return@setOnClickListener
            }
            if (driveCardUrl.isEmpty()) {
                toast("请上传驾驶证")
                return@setOnClickListener
            }
            if (licenseUrl.isEmpty()) {
                toast("请上传营运资质")
                return@setOnClickListener
            }
            com.hbcx.driver.network.HttpManager.personRegister(phone,idCard,driveYear,sex,name,driveCardUrl,code,headUrl,idCardUrl,licenseUrl,lineNum,num).request(this){ msg, _->
                toast(msg!!)
                startActivity<com.hbcx.driver.ui.login.LoginActivity>()
            }
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
                            HttpManager.uploadFile(file).request(this@PersonJoinActivity) { _, data ->
                                data?.let {
                                    val s = it.optString("imgUrl")
                                    when (requestCode) {
                                        0 -> {
                                            iv_head.setImageURI(s)
                                            headUrl = s
                                        }
                                        1 -> {
                                            iv_id_card.setImageURI(s)
                                            idCardUrl = s
                                        }
                                        2 -> {
                                            iv_qualification.setImageURI(s)
                                            licenseUrl = s
                                        }
                                        3 -> {
                                            iv_driver_card.setImageURI(s)
                                            driveCardUrl = s
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
}