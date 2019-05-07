package com.hbcx.driver.network.beans

import com.hbcx.driver.R
import java.io.Serializable

data class TicketBus(val id: Int, val start_time: String, val rideDate: Long, val stationNum: Int, val endStationName: String, val startCityName: String
                     , val lineName: String, val peoNum: Int, val peoNum1: Int, val status: Int, val startStationName: String, val endCityName: String
                     , val endLat: Double, val startLon: Double, val startLat: Double, val endLon: Double, val lineList: ArrayList<BusStation>) {
    fun getStatusStr() = if (status == 1) "发车中" else "待发车"
    fun getActionStr() = if (status == 1) "安全到达" else "立即发车"
}

data class Passenger(val id: Int, val createTime: Long, val pointDownName: String, val nickName: String, val phone: String, val num: Int
                     , val status: Int, val pointUpName: String) : Serializable

data class Driver(val id: Int, val imgUrl: String, val nickName: String, val sex: Int, val auditStatus: Int, val isCar: Int, val licensePlate: String
                  , val carColor: String, val modelName: String, val brandName: String, val idCards: String, val drivingAge: Int,
                  val driverLicensePhotograph: String,val phone:String,var isChecked: Boolean = false) {
    fun getStatusStr() = when (auditStatus) {
        1 -> "待审核"
        2 -> "已通过"
        3 -> "已拒绝"
        4 -> "已删除"
        else -> ""
    }

    fun getStatusColorRes() = if (auditStatus == 1) R.color.colorOrange else R.color.grey
}

data class Car(val id: Int, val pedestal: Int, val status: Int, val licensePlate: String, val carColor: String, val nickName: String, val modelName: String
               , val brandName: String, val annualTrial: String, val bodyIllumination: String, val drivingLicense: String, val strongInsurance: String,
               val commercialInsurance:String){
    fun getStatusStr() = when(status){ //1=待审核,2=未关联司机，3=已关联司机，4=公司停用，5=平台停用，6=删除，7=已拒绝
        1->"待审核"
        2,3->"已通过"
        7->"未通过"  //拒绝
        else->""
    }
    fun getActionStr() = when(status){
        1->"空闲"
        2->"关联司机"
        3->"更换司机"
        else->""
    }

    fun getStatusColorRes() = if (status == 1) R.color.colorOrange else R.color.grey

}

data class BusStation(val id: Int, val peoNum1: Int, val name: String, val type: Int, val peoNum: Int, val times: String, val isUpOrDown: Int,
                      var isChecked: Boolean = false)

data class TicketDetail(val id: Int, val createTime: Long, val phone: String, val times: String, val nickName: String, val imgUrl: String, val canUp: Boolean
                        , val pointDownName: String, val pointUpName: String, val passengerList: ArrayList<Passenger>) : Serializable

/**售票班线详情**/
data class TicketLineDetail(val id: Int, val pedestal: String, val km1: Double, val start_time: String, val km2: Double, val startLat: Double, val endLon: Double
                            , val endLat: Double, val money: Double, val startLon: Double, val endName: String, val startName: String, val stationList: ArrayList<BusStation>)

/**线路管理**/
data class Line(val startName:String,val endName:String, val createTime:Long,val endStationName:String,val stationUpName:String
                ,val num:Int,val typeName:String, val lineName:String,val stationName:String,val id:Int,val status:Int,
                val salesMoney:Double,val startStationName:String,val lineType:Int,val startStationId:Int,val endStationId:Int):Serializable{
    fun getStatusStr() = when(status){ //-1=带设置价格，1=审核中，2=已被拒，3=有效中，4=已下架，5=平台下架,6=删除
        -1->"待设置价格"
        1->"审核中"
        2->"已被拒"
        3->"有效中"
        4->"已下架"
        5->"平台下架"
        6->"已删除"
        else->""
    }
}

/**线路类型**/
data class LineType(val id: Int, val name: String):Serializable

/**中途站点**/
data class HalfStation(val name:String,val address:String,val id: Int
                       ,val lon:Double,val lat:Double,val sort:Int,val type:Int,
                       val stationId:Int,val cityCode:String,val stationType:Int = 3):Serializable

/**
 * 设置价格列表数据
 */
data class LinePrice(val id	:Int,val startStationId:Int,val endStationId:Int,val lineId:Int,
                     val startStationName:String,val endStationName:String,var salesMoney:Double)

/**
 * 设置价格列表数据
 */
data class UpdataLinePrice(val id	:Int,val startStationId:Int,val endStationId:Int,val lineId:Int,
                     var salesMoney:Double)

/**
 * 班次
 */
data class ClassModel(val weeks:String,val num:Int,val createTime:Long,val line_id:Int,val carId:Int,val totalNum:Int,
                      val percentage:Int,val startTime:String,val salesMoney:Double,val stattionList:ArrayList<Station>,
                      val id:Int,val name:String,val lineName:String,val status:Int,val licensePlate:String):Serializable{
    fun getStatusStr() = when(status){ //1=有效，2=平台下架，3=公司下架,4=线路下架
        1->"有效"
        2->"平台下架"
        3->"公司下架"
        4->"线路下架"
        else->""
    }
}

/**
 * 可关联的线路
 */
data class EnableLine(val id:Int,val lineName:String)

/**
 * 可关联的车
 */
data class EnableCar(val id:Int,val licensePlate:String)

/**
 * 班次站点
 */
data class Station(val id:Int,val name: String,val type: Int,var times: String = ""):Serializable

/**
 * 设置时间后的站点
 */
data class StationTime(val pid:Int,val times: String)
