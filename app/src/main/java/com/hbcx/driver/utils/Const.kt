package com.hbcx.driver.utils

/**
 * 一些常量等。
 * Created on 2018/3/20.
 */
object Const {
    //各类第三方key
    const val UMENG_KEY = "5c173d17f1f55613b6000279"
    const val WX_APP_ID = "wx8eafc65b513a120e"
    const val WX_SECRET = "d48482ec5316d17e7119090f181e36df"
    const val QQ_APP_ID = "1107981295"
    const val QQ_SECRET = "WS7LJigCXgPsk4YS"
    const val SINA_APP_ID = ""
    const val SINA_SECRET = ""
    const val JUHE_KEY = "f0d2dd497ef2fd34124f829f852cfaae"
    const val RULE = "http://www.baidu.com"
    const val PLATFORM_PROTOCOL = "http://www.baidu.com"
    const val ABOUT_US = "http://www.baidu.com"
    const val PAY_ACTION = "cn.dznev.wx.pay"

    object User {
        const val IS_LOGIN = "is_Login"
        const val USER_ID = "userId"
        const val USER_TYPE = "userType"
        const val USER_PHONE = "userPhone"
    }

    object Code {
        const val LOG_OUT = 2
    }

    object Method {
        const val PING_SEND = "OK"//心跳
        const val PING_RECIEVE = "PING"//心跳
        const val LOTICO = "LOCATION"//司机上传经纬度
        const val USER_LOGIN = "DRIVER_LOGIN"//用户其他设备登录
        const val ORDER_CANCLE = "ORDER_CANCLE"//用户取消
        const val NEW_ORDER = "ORDER_DRIVER"//司机接到新订单
        const val REFUSE_ORDER = "ORDER_NOANSWER"//申请改派司机成功通知
        const val RECEIVE_ORDER = "ORDER_APPLY_NOANSWER"//被指派司机
        const val ORDER_CANCLE_PLAT = "ORDER_CANCLE_PLAT"//平台取消订单
        const val USER_ORDER_GRAB = "USER_ORDER_GRAB"//实时计费模式下,司机接单 推给用户的消息
        const val USER_ORDER_SETOff = "USER_ORDER_SETOff"//实时计费模式下,司机出发前往预约地点 推给用户的消息
        const val USER_ORDER_DAODA = "USER_ORDER_DAODA"//实时计费模式下,司机到达预约地点 推给用户的消息
        const val USER_ORDER_BEGIN = "USER_ORDER_BEGIN"//实时计费模式下,司机开始服务 推给用户的消息
        const val USER_ORDER_OVER = "USER_ORDER_OVER"//实时计费模式下,司机送达乘客 推给用户的消息
        const val USER_ORDER_NOYINGDA = "USER_ORDER_NOYINGDA"//用户呼叫订单,三次推单后无司机应答 订单自动取消

        const val USER_ORDER_GRAB_HOURS = "USER_ORDER_GRAB_HOURS"//按小时计费模式下,司机接单 推给用户的消息
        const val USER_ORDER_SETOFF_HOURS = "USER_ORDER_SETOFF_HOURS"//按小时计费模式下,司机出发前往预约地点 推给用户的消息
        const val USER_ORDER_DAODA_HOURS = "USER_ORDER_DAODA_HOURS"//按小时计费模式下,司机到达预约地点 推给用户的消息
        const val USER_ORDER_BEGIN_HOURS = "USER_ORDER_BEGIN_HOURS"//按小时计费模式下,司机开始服务 推给用户的消息
        const val USER_ORDER_OVER_HOURS = "USER_ORDER_OVER_HOURS"//按小时计费模式下,司机开始服务 推给用户的消息

        const val USER_ORDER_GRAB_TODAY = "USER_ORDER_GRAB_TODAY"    //按天计费模式下,司机接单 推给用户的消息
        const val USER_ORDER_SETOFF_TODAY = "USER_ORDER_SETOFF_TODAY"    //按天计费模式下,司机出发前往预约地点 推给用户的消息
        const val USER_ORDER_DAODA_TODAY = "USER_ORDER_DAODA_TODAY"    //按天计费模式下,司机到达预约地点 推给用户的消息
        const val USER_ORDER_BEGIN_TODAY = "USER_ORDER_BEGIN_TODAY"    //按天计费模式下,司机开始服务 推给用户的消息
        const val USER_ORDER_OVER_TODAY = "USER_ORDER_OVER_TODAY"    //按天计费模式下,司机送达乘客 推给用户的消息

        const val REALTIME_REAL_USER = "REALTIME_REAL_USER"//按里程计费模式下,司机开始服务 推给用户的消息
        const val HOURS_REAL_USER = "HOURS_REAL_USER"//按小时计费模式下,司机开始服务 推给用户的消息
        const val TODAY_REAL_USER = "TODAY_REAL_USER"//按天计费模式下,司机开始服务 推给用户的消息
    }
}