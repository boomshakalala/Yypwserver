package com.hbcx.driver.interfaces

import org.json.JSONObject

/**
 * Created on 2018/5/10.
 */
interface OnTripStateListener {
    fun onTripping(obj:JSONObject)
    fun onCancel(obj:JSONObject,type:Int = 0)
}