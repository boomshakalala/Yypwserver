package com.hbcx.driver.network.beans

import java.io.Serializable

data class CarType (var id:Int=0,var name:String? = null,var list:ArrayList<CarMode>) : Serializable

data class CarMode(val id: Int,val name:String): Serializable

data class OpenCity(val id:Int,val cityCode:String,val cityName:String)
/**
 * 首页订单列表
 */
data class OrderList(var id: Int,var departureTime:Long,var type:Int,var status: Int,var startAddress:String,var endAddress:String,var createTime:Long)

data class DriverMain(var modelName:String ,var carColor:String ,var brandName:String ,var licensePlate:String,
var driverOrderNums:Int,var money:Double,var praise:String,var status :Int,var orderList:ArrayList<OrderList>)

/**
 * 省市区三级列表数据
 * code :6位
 * citycode： 3位
 **/
data class Region(val id:Int,val name:String,val code:String,val citycode:String,val address:String,val lon:Double,val lat:Double):Serializable

data class Message(val id:Int,val title:String,val content:String,val imgUrl:String,val createTime:Long,val type:Int)