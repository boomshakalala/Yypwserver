package com.hbcx.driver.interfaces

import org.json.JSONObject

/**
 * 行程中消息接口
 */
interface TripMessageListener {
    //改派成功
    fun onRefuseSuccess(obj: JSONObject)
    //收到指派
    fun onReceiveOrder(obj: JSONObject)
}