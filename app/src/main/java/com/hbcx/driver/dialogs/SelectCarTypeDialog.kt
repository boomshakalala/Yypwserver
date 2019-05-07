package com.hbcx.driver.dialogs

import android.app.DialogFragment
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hbcx.driver.R
import kotlinx.android.synthetic.main.dialog_select_car_type_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

class SelectCarTypeDialog : DialogFragment() {
    private lateinit var data:MutableList<com.hbcx.driver.network.beans.CarType>
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
            inflater?.inflate(R.layout.dialog_select_car_type_layout, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data  = arguments.getSerializable("data") as MutableList<com.hbcx.driver.network.beans.CarType>
        wv_brand.setItems(data.map {
            it.name!!
        })
        wv_brand.setSeletion(0)
        wv_mode.setItems(data[0].list.map {
            it.name
        })
        wv_mode.setSeletion(0)
        tv_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        tv_sure.setOnClickListener {
            dialogListener?.onClick(wv_brand.seletedIndex,wv_brand.seletedItem,wv_mode.seletedIndex,wv_mode.seletedItem)
            dismissAllowingStateLoss()
        }

        wv_brand.setOnWheelViewListener { selectedIndex, item ->
            wv_mode.setItems(data[selectedIndex].list.map {
                it.name
            })
        }
    }
    private var dialogListener: com.hbcx.driver.interfaces.OnDialogListener2? = null

    fun setDialogListener(l: (p: Int, s: String?,p1: Int, s1: String?) -> Unit) {
        dialogListener = object : com.hbcx.driver.interfaces.OnDialogListener2 {
            override fun onClick(position: Int, data: String?,position1: Int, data1: String?) {
                l(position, data,position1,data1)
            }
        }
    }
}