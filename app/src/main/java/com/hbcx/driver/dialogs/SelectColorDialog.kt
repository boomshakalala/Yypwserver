package com.hbcx.driver.dialogs

import android.app.DialogFragment
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hbcx.driver.R
import kotlinx.android.synthetic.main.dialog_select_sex_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

class SelectColorDialog : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, wrapContent)
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.dialog_select_sex_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = arguments.getStringArrayList("data")
        if (list == null)
            wv_sex.setItems(arrayListOf("灰色", "白色", "黄色", "红色", "银色", "蓝色", "金色", "紫色", "黑色", "棕色", "绿色"))
        else
            wv_sex.setItems(list)
        wv_sex.setSeletion(0)
        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        tv_sure.setOnClickListener {
            dialogListener?.onClick(wv_sex.seletedIndex, wv_sex.seletedItem)
            dismissAllowingStateLoss()
        }
    }

    private var dialogListener: com.hbcx.driver.interfaces.OnDialogListener? = null

    fun setDialogListener(l: (p: Int, s: String?) -> Unit) {
        dialogListener = object : com.hbcx.driver.interfaces.OnDialogListener {
            override fun onClick(position: Int, data: String?) {
                l(position, data)
            }
        }
    }
}