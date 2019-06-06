package com.hbcx.driver.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.sinata.xldutils.utils.screenWidth
import com.hbcx.driver.R
import kotlinx.android.synthetic.main.dialog_commit_location.*
import kotlinx.android.synthetic.main.dialog_tip_layout.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.wrapContent

/**
 * 提示弹窗
 */
class CommitLocationDialog : DialogFragment() {

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
            inflater.inflate(R.layout.dialog_commit_location, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        btn_close.setOnClickListener { dismissAllowingStateLoss() }

        btn_cancel.setOnClickListener {
            dialogCancelListener?.onClick(0,"")
            dismissAllowingStateLoss()
        }
        btn_commit.setOnClickListener {
            if (et_message.text.isEmpty()){
                toast("请输入提示信息")
                return@setOnClickListener
            }
            dialogListener?.onClick(1,et_message.text.toString())
            dismissAllowingStateLoss()
        }

    }


    private var dialogListener: com.hbcx.driver.interfaces.OnDialogListener? = null
    private var dialogCancelListener: com.hbcx.driver.interfaces.OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : com.hbcx.driver.interfaces.OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
    fun setCancelDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogCancelListener = object : com.hbcx.driver.interfaces.OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
}