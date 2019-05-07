package com.hbcx.driver.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hbcx.driver.R
import kotlinx.android.synthetic.main.dialog_select_station_type_layout.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.wrapContent

class SelectStationTypeDialog:DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, R.style.Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_select_station_type_layout,null)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setLayout(matchParent, wrapContent)
        dialog.window.setGravity(Gravity.BOTTOM)
        dialog.setCanceledOnTouchOutside(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_cancel.onClick {
            dismiss()
        }
        tv_option_1.onClick {
            callback?.onClick(0)
            dismiss()
        }
        tv_option_2.onClick {
            callback?.onClick(1)
            dismiss()
        }
    }

    private var callback:OnClickCallback? = null

    fun setOnClickCallback(l:(position:Int)->Unit){
        callback = object :OnClickCallback{
            override fun onClick(position: Int) {
                l(position)
            }
        }
    }

    interface OnClickCallback{
        fun onClick(position:Int)
    }
}