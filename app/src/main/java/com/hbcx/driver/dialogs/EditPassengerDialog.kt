package com.hbcx.driver.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.sinata.xldutils.utils.isValidIdCard

import cn.sinata.xldutils.utils.screenWidth
import com.hbcx.driver.R
import com.hbcx.driver.interfaces.OnDialogListener
import kotlinx.android.synthetic.main.dialog_edit_passenger.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 提示弹窗
 */
class EditPassengerDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, R.style.FadeDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout((screenWidth()*0.75).toInt(), wrapContent)
        dialog.window.setGravity(Gravity.CENTER)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.dialog_edit_passenger, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }
        tv_ok.onClick {
            val s = et_name.text.trim().toString()
            if (s.isEmpty()){
                toast("请输入姓名")
                return@onClick
            }
            val num = et_num.text.toString()
            if (!num.isValidIdCard()){
                toast("请输入正确的身份证号")
                return@onClick
            }
            dialogListener?.onClick(s,num)
            dismissAllowingStateLoss()
        }
    }

    override fun onResume() {
        super.onResume()
        et_name.setText("")
        et_num.setText("")
    }
    interface OnDialogCallback{
        fun onClick(name: String, data: String)
    }
    private var dialogListener: OnDialogCallback? = null
    fun setDialogListener(l: (p: String, s: String) -> Unit) {
        dialogListener = object : OnDialogCallback {
            override fun onClick(name: String, data: String) {
                l(name, data)
            }
        }
    }
}