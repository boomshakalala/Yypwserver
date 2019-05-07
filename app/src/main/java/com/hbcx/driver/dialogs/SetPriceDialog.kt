package com.hbcx.driver.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.sinata.xldutils.utils.SPUtils

import cn.sinata.xldutils.utils.screenWidth
import com.hbcx.driver.R
import com.hbcx.driver.interfaces.OnDialogListener
import com.hbcx.driver.utils.Const
import kotlinx.android.synthetic.main.dialog_set_price.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 提示弹窗
 */
class SetPriceDialog : DialogFragment() {

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
            inflater.inflate(R.layout.dialog_set_price, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_close.setOnClickListener {
            dismissAllowingStateLoss()
        }
        tv_ok.onClick {
            val s = et_name.text.trim().toString()
            if (s.isEmpty()){
                toast("请输入价格")
                return@onClick
            }
            try {
                s.toDouble()
                dialogListener?.onClick(0, s)
                dismissAllowingStateLoss()
            }catch (e:Exception){
                toast("请输入正确的价格")
                return@onClick
            }
        }
    }

    override fun onResume() {
        super.onResume()
        et_name.setText("")
    }
    private var dialogListener: OnDialogListener? = null
    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
}