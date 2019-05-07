package com.hbcx.driver.ui.ticketbus

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.widget.TextView
import cn.qqtheme.framework.picker.DatePicker
import cn.qqtheme.framework.util.ConvertUtils
import cn.sinata.xldutils.activity.SelectPhotoDialog
import cn.sinata.xldutils.utils.SPUtils
import cn.sinata.xldutils.utils.isValidIdCard
import cn.sinata.xldutils.utils.isValidPhone
import cn.sinata.xldutils.utils.optString
import com.hbcx.driver.R
import com.hbcx.driver.network.HttpManager
import com.hbcx.driver.utils.Const
import com.hbcx.driver.utils.request
import kotlinx.android.synthetic.main.activity_add_driver.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*

class AddDriverActivity: com.hbcx.driver.ui.TranslateStatusBarActivity() {
    override fun setContentView() = R.layout.activity_add_driver

    private val userId by lazy {
        SPUtils.instance().getInt(Const.User.USER_ID)
    }
    private var headImg = "" //头像路径
    private var lisenceImg = "" //驾照路径
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
        iv_head.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 1)
        }
        iv_license.setOnClickListener {
            startActivityForResult(Intent(this, SelectPhotoDialog::class.java), 2)
        }
        btn_action.onClick {
            val name = et_name.text.toString().trim()
            if (name.isEmpty()) {
                toast("请输入姓名")
                return@onClick
            }
            val sex = when (tv_sex.text.toString()) {
                "男" -> 1
                "女" -> 2
                else -> {
                    toast("请选择性别")
                    return@onClick
                }
            }
            val phone = et_phone.text.toString().trim()
            if (!phone.isValidPhone()){
                toast("请输入正确的联系电话")
                return@onClick
            }
            val idCard = et_id_card.text.toString().trim()
            if (!idCard.isValidIdCard()){
                toast("请输入正确的身份证号码")
                return@onClick
            }
            val driveYear = tv_drive_year.text.toString().trim()
            if (driveYear.isEmpty()) {
                toast("请选择驾龄")
                return@onClick
            }
            if (headImg.isEmpty()){
                toast("请上传头像")
                return@onClick
            }
            if (lisenceImg.isEmpty()){
                toast("请上传驾驶证")
                return@onClick
            }
            showDialog(canCancel = false)
            HttpManager.addDriver(phone,idCard,driveYear,sex,userId,name,lisenceImg,headImg).request(this@AddDriverActivity){msg,_->
                toast(msg!!)
                setResult(Activity.RESULT_OK)
                finish()
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
                            HttpManager.uploadFile(file).request(this@AddDriverActivity){ _, data->
                                data?.let {
                                    val s = it.optString("imgUrl")
                                    when(requestCode){
                                        1->{
                                            iv_head.setImageURI(s)
                                            headImg = s
                                        }
                                        2->{
                                            iv_license.setImageURI(s)
                                            lisenceImg = s
                                        }
                                    }
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
        title = "添加司机"
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