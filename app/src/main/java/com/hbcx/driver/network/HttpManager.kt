package com.hbcx.driver.network

import android.text.TextUtils
import android.util.Log
import cn.sinata.util.DES
import cn.sinata.xldutils.data.ResultData
import cn.sinata.xldutils.defaultScheduler
import com.google.gson.JsonObject
import com.hbcx.driver.network.beans.*
import com.hbcx.driver.utils.Const
import io.reactivex.Flowable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * 网络请求处理
 */
object HttpManager {

    const val PAGE_SIZE = 20
    const val encodeDES = true

    private class ParamsBuilder private constructor() {
        private val sb: StringBuilder = StringBuilder()

        fun build(): String {
            return sb.toString()
        }

        fun build(des: Boolean): String {
            return if (des) {
                Log.d("server:", sb.toString())
                DES.encryptDES(sb.toString())
            } else {
                sb.toString()
            }
        }

        fun append(key: String, value: String): ParamsBuilder {
            _append(key, value)
            return this
        }

        fun append(key: String, value: Int): ParamsBuilder {
            _append(key, value)
            return this
        }

        fun append(key: String, value: Double): ParamsBuilder {
            _append(key, value)
            return this
        }

        fun append(key: String, value: Float): ParamsBuilder {
            _append(key, value)
            return this
        }

        fun append(key: String, value: Long): ParamsBuilder {
            _append(key, value)
            return this
        }

        private fun _append(key: String, value: Any) {
            var value = value
            if (value is String) {

                if ("null" == value || TextUtils.isEmpty(value.toString())) {
                    value = ""
                }
            }
            if (sb.isEmpty()) {
                sb.append(key)
                sb.append(SPLIT)
                sb.append(value)
            } else {
                if (sb.contains(BEGIN)) {
                    sb.append(AND)
                    sb.append(key)
                    sb.append(SPLIT)
                    sb.append(value)
                } else {
                    sb.append(BEGIN)
                    sb.append(key)
                    sb.append(SPLIT)
                    sb.append(value)
                }
            }
        }

        companion object {
            const val SPLIT = "="
            const val AND = "&"
            const val BEGIN = "?"

            fun create(): ParamsBuilder {
                return ParamsBuilder()
            }
        }

    }


    /**
     * 发起请求方法
     */
    private fun request() =
            RRetrofit.instance().create(ApiService::class.java)


    /**
     * 上传图片
     */
    fun uploadFile(file: File): Flowable<ResultData<JsonObject>> {
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val part = MultipartBody.Part.createFormData("myfile", file.name, requestFile)
        return request().uploadFile(part).defaultScheduler()
    }

    /**
     * 登录
     */
    fun login(phone: String, passWord: String): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.LOGIN).append("phone", phone).append("passWord", passWord)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 重置密码并登录
     */
    fun forgetPwd(phone: String, passWord: String, code: String): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.FORGET).append("phone", phone).append("passWord", passWord).append("code", code)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 发送验证码
     * @param type 类型【1=用户登录，2=用户更换手机，3=用户忘记密码，4=司机注册，5=司机更换手机，6=司机忘记密码】
     */
    fun sendSms(phone: String, type: Int): Flowable<ResultData<String>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.SEND_MSM).append("phone", phone).append("type", type)
        return request().stringRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 验证验证码
     * @param type 类型【1=用户登录，2=用户更换手机，3=用户忘记密码，4=司机注册，5=司机更换手机，6=司机忘记密码】
     */
    fun checkCode(phone: String, code: String, type: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.CHECK_CODE).append("phone", phone).append("code", code).append("type", type)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 改密码
     * @param type 类型【1=用户登录，2=用户更换手机，3=用户忘记密码，4=司机注册，5=司机更换手机，6=司机忘记密码】
     */
    fun updatePwd(passWord: String, id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.UPDATE_PWD).append("passWord", passWord).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 改手机
     */
    fun updatePhone(phone: String, code: String, id: Int): Flowable<ResultData<Int>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.UPDATE_PHONE).append("phone", phone).append("id", id).append("code", code)
        return request().intRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 广告页
     */
    fun getAd(): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.GET_AD)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 获取开通城市
     */
    fun getOpenCity(): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.OpenCity>>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.GET_OPEN_CITY)
        return request().getOpenCity(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 可预约天数
     */
    fun getEnableDayCount(): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.ENABLE_DAY)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 车型
     */
    fun getCarTypeList(): Flowable<ResultData<List<com.hbcx.driver.network.beans.CarType>>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.CAR_TYPE_LIST)
        return request().getCarTypeList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 司机入驻
     */
    fun driverRegister(phone: String, idCards: String, drivingAge: String, sex: Int, nickName: String
                       , driverLicensePhotograph: String, code: String, carModelId: Int, carColor: String
                       , licensePlate: String, annualTrial: String, drivingLicense: String
                       , bodyIllumination: String, strongInsurance: String,DriversNumbers: String): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.DRIVER_REGISTER)
                .append("phone", phone).append("idCards", idCards).append("drivingAge", drivingAge)
                .append("sex", sex).append("nickName", nickName).append("driverLicensePhotograph", driverLicensePhotograph)
                .append("code", code).append("carModelId", carModelId).append("carColor", carColor)
                .append("licensePlate", licensePlate).append("annualTrial", annualTrial).append("drivingLicense", drivingLicense)
                .append("bodyIllumination", bodyIllumination).append("strongInsurance", strongInsurance).append("DriversNumbers",DriversNumbers)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 个人入驻
     */
    fun personRegister(phone: String, idCards: String, drivingAge: String, sex: Int, nickName: String
                       , driverLicensePhotograph: String, code: String, imgUrl: String, idCardsImg: String
                       , operationCertificate: String, lineNum: String,DriversNumbers:String): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.PERSON_REGISTER)
                .append("phone", phone).append("idCards", idCards).append("drivingAge", drivingAge)
                .append("sex", sex).append("nickName", nickName).append("driverLicensePhotograph", driverLicensePhotograph)
                .append("code", code).append("imgUrl", imgUrl).append("idCardsImg", idCardsImg)
                .append("operationCertificate", operationCertificate).append("lineNum", lineNum).append("DriversNumbers",DriversNumbers)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 公司入驻
     */
    fun companyRegister(phone: String, cityCode: String, serverType: Int, driverNum: String, name: String
                        , code: String, address: String, operationQualificationPhotos: String
                        , photoBusinessLicense: String, carNum: String, lon: Double, lat: Double, contacts: String): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.COMPANY_REGISTER)
                .append("phone", phone).append("cityCode", cityCode).append("serverType", serverType)
                .append("driverNum", driverNum).append("name", name).append("address", address)
                .append("code", code).append("operationQualificationPhotos", operationQualificationPhotos)
                .append("photoBusinessLicense", photoBusinessLicense).append("contacts", contacts)
                .append("carNum", carNum).append("lon", lon).append("lat", lat)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 司机首页
     */
    fun getDriverMain(id: Int): Flowable<ResultData<com.hbcx.driver.network.beans.DriverMain>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.DRIVER_MAIN).append("id", id)
        return request().getDriverMain(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 司机评价
     */
    fun getDriverEvaluate(id: Int, page: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.DRIVER_EVALUATE)
                .append("id", id).append("page", page).append("rows", PAGE_SIZE)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 上下班
     */
    fun changeWorkState(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.CHANGE_WORK_STATE).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 抢单
     */
    fun robOrder(id: Int, orderId: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.ROB_ORDER).append("id", id).append("orderId", orderId)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 前往预约地点
     */
    fun goStartAddress(orderId: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.GO_START_ADDRESS).append("orderId", orderId)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 到达预约地点
     */
    fun arriveStartAddress(orderId: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.ARRIVE_START_ADDRESS).append("orderId", orderId)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 开始行程
     */
    fun startTrip(orderId: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.START_TRIP).append("orderId", orderId)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 结束行程
     */
    fun endTrip(orderId: Int, lon: Double, lat: Double): Flowable<ResultData<com.hbcx.driver.network.beans.Order>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.END_TRIP).append("orderId", orderId).append("lon", lon).append("lat", lat)
        return request().orderRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 订单详情
     */
    fun getOrderDetail(orderId: Int): Flowable<ResultData<com.hbcx.driver.network.beans.Order>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.ORDER_DETAIL).append("orderId", orderId)
        return request().orderRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 申请改派金额
     */
    fun getCancelMoney(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.CANLEL_MONEY).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 申请改派
     */
    fun cancelOrder(orderId: Int, cause: String, remark: String?): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.CANLEL_ORDER).append("orderId", orderId).append("cause", cause)
        if (!remark.isNullOrEmpty())
            request.append("remark", remark!!)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 快专车订单列表
     */
    fun carOrderList(id: Int, page: Int): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.OrderList>>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.CAR_ORDER_LIST).append("id", id).append("page", page).append("rows", PAGE_SIZE)
        return request().carOrderList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 票务首页服务列表
     */
    fun getTicketList(id: Int, days: String): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.TicketBus>>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.TICKET_LIST).append("id", id).append("days", days)
        return request().getTicketList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 车票详情
     */
    fun getTicketDetail(id: String?, driverId: Int, orderNum: String?): Flowable<ResultData<com.hbcx.driver.network.beans.TicketDetail>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.TICKET_DETAIL).append("driverId", driverId)
        if (id != null)
            request.append("id", id)
        if (orderNum != null)
            request.append("orderNum", orderNum)
        return request().getTicketDetail(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 票务历史服务列表
     */
    fun getTicketHistory(id: Int, page: Int, startTime: String, endTime: String): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.TicketBus>>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.TICKET_HISTORY_LIST)
                .append("id", id).append("page", page).append("startTime", startTime).append("endTime", endTime).append("rows", PAGE_SIZE)
        return request().getTicketList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 班次乘客列表
     */
    fun getPassengerList(id: Int, days: String, pointUpId: Int? = null): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.Passenger>>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.BUS_PASSENGERS)
                .append("id", id).append("days", days)
        if (pointUpId != null)
            request.append("pointUpId", pointUpId)
        return request().getPassengerList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 删除票务历史服务
     */
    fun delTicketHistory(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.DELETE_TICKET_HISTORY).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 班次详情
     */
    fun getLineDetail(id: Int, shiftId: Int, days: String): Flowable<ResultData<com.hbcx.driver.network.beans.TicketBus>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.TICKET_LINE_DETAIL).append("id", id)
                .append("shiftId", shiftId).append("days", days)
        return request().getTicketLineDetail(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 票务班线详情
     */
    fun getTicketLineDeatail(days: String, id: Int, startPointId: Int, endPointId: Int): Flowable<ResultData<com.hbcx.driver.network.beans.TicketLineDetail>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.TICKETLINE_DETAIL)
                .append("id", id).append("startCityCode", "0").append("endCityCode", "0")
                .append("lon", 0.0).append("lat", 0.0).append("days", days).append("startPointId", startPointId).append("endPointId", endPointId)
        return request().getLineDetail(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 发车
     */
    fun busStart(id: Int, shiftId: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.BUS_START).append("id", id).append("shiftId", shiftId)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 安全到达
     */
    fun busArrived(id: Int, shiftId: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.BUS_ARRIVE).append("id", id).append("shiftId", shiftId)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 确认乘坐
     */
    fun confirmRide(id: Int, driverId: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.CONFIRM_RIDE).append("id", id).append("driverId", driverId)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 司机个人中心首页
     */
    fun driverHome(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.DRIVER_HOME).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 票务个人中心首页
     */
    fun ticketDriverHome(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.TICKET_DRIVER_HOME).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 我的钱包
     */
    fun myWallet(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.WALLET).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 添加司机
     */
    fun addDriver(phone: String, idCards: String, drivingAge: String, sex: Int, affiliatedCompany: Int
                  , nickName: String, driverLicensePhotograph: String, imgUrl: String): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.ADD_DRIVER).append("phone", phone)
                .append("idCards", idCards).append("drivingAge", drivingAge).append("sex", sex).append("affiliatedCompany", affiliatedCompany)
                .append("nickName", nickName).append("driverLicensePhotograph", driverLicensePhotograph).append("imgUrl", imgUrl)

        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 添加车
     */
    fun addCar(carModelId: Int, carColor: String, licensePlate: String, pedestal: Int, affiliatedCompany: Int
               , annualTrial: String, bodyIllumination: String, operationCertificate: String, drivingLicense: String
               , strongInsurance: String, commercialInsurance: String): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.ADD_CAR).append("carModelId", carModelId)
                .append("carColor", carColor).append("licensePlate", licensePlate).append("pedestal", pedestal)
                .append("affiliatedCompany", affiliatedCompany).append("annualTrial", annualTrial).append("drivingLicense", drivingLicense)
                .append("bodyIllumination", bodyIllumination).append("operationCertificate", operationCertificate)
                .append("strongInsurance", strongInsurance).append("commercialInsurance", commercialInsurance)

        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 关联/取关司机
     */
    fun bindDriver(id: Int, driverId: Int?): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.BIND_DRIVER).append("id", id)
        if (driverId != null)
            request.append("driverId", driverId)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 司机列表
     */
    fun getDriverList(id: Int, page: Int): Flowable<ResultData<ArrayList<Driver>>> {
        val request = ParamsBuilder.create().append("server", Api.GET_DRIVER_LIST).append("id", id)
                .append("page", page).append("rows", PAGE_SIZE)
        return request().getDriverList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 司机列表
     */
    fun getIdelDriverList(id: Int, page: Int): Flowable<ResultData<ArrayList<Driver>>> {
        val request = ParamsBuilder.create().append("server", Api.GET_IDEL_DRIVER_LIST).append("id", id)
                .append("page", page).append("rows", PAGE_SIZE)
        return request().getDriverList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 评价管理
     */
    fun getEvaluateData(id: Int, page: Int): Flowable<ResultData<Evaluate>> {
        val request = ParamsBuilder.create().append("server", Api.GET_EVALUATE_LIST).append("id", id)
                .append("page", page).append("rows", PAGE_SIZE)
        return request().getEvaluateData(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 车列表
     */
    fun getCarList(id: Int, page: Int): Flowable<ResultData<ArrayList<Car>>> {
        val request = ParamsBuilder.create().append("server", Api.CAR_LIST).append("id", id)
                .append("page", page).append("rows", PAGE_SIZE)
        return request().getCarList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 司机详情
     */
    fun getDriverDetail(id: Int): Flowable<ResultData<Driver>> {
        val request = ParamsBuilder.create().append("server", Api.GET_DRIVER_DETAIL).append("id", id)
        return request().getDriverDetail(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 车详情
     */
    fun getCarDetail(id: Int): Flowable<ResultData<Car>> {
        val request = ParamsBuilder.create().append("server", Api.GET_CAR_DETAIL).append("id", id)
        return request().getCarDetail(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 删除司机
     */
    fun delDriver(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.DEL_DRIVER).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 删除车
     */
    fun delCar(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.DEL_CAR).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 绑银行卡
     */
    fun bindBankCard(id: Int, cardholder: String, bankNumber: String, bankName: String): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.BIND_CARD).append("id", id)
                .append("cardholder", cardholder).append("bankNumber", bankNumber).append("bankName", bankName)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 提现界面数据
     */
    fun getWithdrawData(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.WITHDRAW_DATA).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 提现
     */
    fun withdraw(id: Int, money: Double): Flowable<ResultData<String>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.WITHDRAW).append("id", id).append("money", money)
        return request().stringRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 提现记录
     */
    fun getWithdrawHistroy(id: Int, page: Int): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.WithdrawHistory>>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.WITHDRAW_HISTORY)
                .append("id", id).append("page", page).append("rows", PAGE_SIZE)
        return request().getWithdrawHistory(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 收入明细
     */
    fun getIncomeData(id: Int, status: Int, page: Int, startTime: String, endTime: String): Flowable<ResultData<com.hbcx.driver.network.beans.IncomeData>> {
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.INCOME_HISTORY)
                .append("id", id).append("page", page).append("rows", PAGE_SIZE)
                .append("status", status).append("startTime", startTime).append("endTime", endTime)
        return request().getIncomeData(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 反馈
     */
    fun feedback(userId: Int, content: String, type: Int): Flowable<ResultData<JsonObject>> {
        val replace = content.replace("%", "%25").replace("&", "%26")
                .replace("+", "%2B").replace("=", "2D")
                .replace("#", "%23")
        val request = ParamsBuilder.create().append("server", com.hbcx.driver.network.Api.FEEDBACK)
                .append("type", type).append("userId", userId).append("content", replace)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 线路类型
     */
    fun getLineTypes(): Flowable<ResultData<ArrayList<LineType>>> {
        val request = ParamsBuilder.create().append("server", Api.LINE_TYPE_LIST)
        return request().lineTypeList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 省市区三级列表
     */
    fun getRegins(id: Int): Flowable<ResultData<ArrayList<Region>>> {
        val request = ParamsBuilder.create().append("server", Api.REGION_LIST).append("id", id)
        return request().regionList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 站点列表
     */
    fun getStations(type: Int, code: String,lineTypeId:Int): Flowable<ResultData<ArrayList<Region>>> {
        val request = ParamsBuilder.create().append("server", Api.STATION_LIST)
                .append("type", type).append("code", code).append("lineTypeId",lineTypeId)
        return request().regionList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 沿途站点列表
     */
    fun getHalfStations(id: Int,type: Int): Flowable<ResultData<ArrayList<HalfStation>>> {
        val request = ParamsBuilder.create().append("server", Api.HALF_STATIONS)
                .append("id", id).append("type", type)
        return request().stationSiteList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 线路详情
     */
    fun lineDetail(id: Int): Flowable<ResultData<Line>> {
        val request = ParamsBuilder.create().append("server", Api.LINE_MANAGE_DETAIL)
                .append("id", id)
        return request().lineDetail(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 线路详情
     */
    fun classDetail(id: Int): Flowable<ResultData<ClassModel>> {
        val request = ParamsBuilder.create().append("server", Api.CLASS_DETAIL)
                .append("id", id)
        return request().classDetail(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 上下架线路
     */
    fun upDownLine(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.UP_DOWN_LINE)
                .append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 上下架班次
     */
    fun upDownClass(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.UP_DOWN_CLASS)
                .append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 价格管理列表
     */
    fun getLinePriceList(id: Int): Flowable<ResultData<ArrayList<LinePrice>>> {
        val request = ParamsBuilder.create().append("server", Api.PRICE_LIST)
                .append("id", id)
        return request().linePriceList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 设置价格列表
     */
    fun setLinePriceList(stationLineFare: String): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.SET_PRICE_LIST)
                .append("stationLineFare", stationLineFare)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 线路管理列表
     */
    fun getManageLineList(id: Int, page: Int): Flowable<ResultData<ArrayList<Line>>> {
        val request = ParamsBuilder.create().append("server", Api.LINE_MANAGE_LIST)
                .append("id", id).append("page", page).append("rows", PAGE_SIZE)
        return request().manageLineList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 班次管理列表
     */
    fun getClassList(id: Int, page: Int): Flowable<ResultData<ArrayList<ClassModel>>> {
        val request = ParamsBuilder.create().append("server", Api.CLASS_LIST)
                .append("id", id).append("page", page).append("rows", PAGE_SIZE)
        return request().classList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 可关联线路列表
     */
    fun getEnableLineList(id: Int): Flowable<ResultData<ArrayList<EnableLine>>> {
        val request = ParamsBuilder.create().append("server", Api.LINE_LIST)
                .append("id", id)
        return request().enableLineList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 可关联车列表
     */
    fun getEnableCarList(id: Int): Flowable<ResultData<ArrayList<EnableCar>>> {
        val request = ParamsBuilder.create().append("server", Api.ENABLE_CAR_LIST)
                .append("id", id)
        return request().enableCarList(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 班次站点列表
     */
    fun getStationList(id: Int): Flowable<ResultData<ArrayList<Station>>> {
        val request = ParamsBuilder.create().append("server", Api.STATION_TIME_LIST)
                .append("id", id)
        return request().getStations(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 线路管理列表
     */
    fun addLine(lineName: String, lineType: Int, salesMoney: Double, startStationId: Int, endStationId: Int
                , companyId: Int, stationLine: String, id: Int = 0): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.ADD_LINE)
                .append("lineName", lineName).append("lineType", lineType).append("salesMoney", salesMoney)
                .append("startStationId", startStationId).append("endStationId", endStationId).append("companyId", companyId)
                .append("stationLine", stationLine).append("id", id)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 添加班次
     */
    fun addClass(name: String, lineId: Int, carId: Int, weeks: String, times: String
                 , percentage: Int, startTime: String, endTime: String, id: Int = 0): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.ADD_CLASS)
                .append("name", name).append("lineId", lineId).append("carId", carId)
                .append("weeks", weeks).append("times", times).append("percentage", percentage)
                .append("startTime", startTime).append("id", id).append("endTime", endTime)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 获取银行卡信息
     */
    fun getBankInfo(bankNumber: String): Flowable<JsonObject> {
        return request().getBankInfo(Api.JUHE_URL, Const.JUHE_KEY, bankNumber).defaultScheduler()
    }

    /**
     * 是否有新消息
     */
    fun hasNewMsg(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.HAS_NEW_MSG)
                .append("id", id).append("peoType", 2)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 清空消息
     */
    fun clearMsg(id: Int): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.CLEAR_MSG)
                .append("id", id).append("peoType", 2)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 获取消息
     */
    fun getMsg(id: Int, page: Int): Flowable<ResultData<ArrayList<Message>>> {
        val request = ParamsBuilder.create().append("server", Api.GET_MSG_LIST)
                .append("id", id).append("peoType", 2).append("page", page).append("rows", PAGE_SIZE)
        return request().getMessages(request.build(encodeDES)).defaultScheduler()
    }

    /**
     * 获取服务热线
     */
    fun getServicePhone(): Flowable<ResultData<JsonObject>> {
        val request = ParamsBuilder.create().append("server", Api.GET_SERVICE)
        return request().simpleRequest(request.build(encodeDES)).defaultScheduler()
    }
//    /**
//     * 所有服务城市
//     */
//    fun getCities() =
//            request().getCities().defaultScheduler()
//
//    /**
//     * 评价司机
//     */
//    fun evaDriver(orderId:String?,score:Int,content:String) =
//            request().evaDriver(orderId,score,content).defaultScheduler()
//
//    fun cashPay(orderId:String?) =
//            request().cashPay(orderId).defaultScheduler()
//
//    /**
//     * 计算价格
//     */
//    fun calculatePrice(userId:String,deplon:Double,deplat:Double,bourlon:Double,bourlat:Double,cityId:String,sermodel:String) =
//            request().calculatePrice(userId,deplon,deplat,bourlon,bourlat,cityId,sermodel).defaultScheduler()
//
//    fun calculateDayPrice(userId:String,cityId:String,sermodel:String,days:Float) =
//            request().calculateDayPrice(userId,cityId,sermodel,days).defaultScheduler()
//
//    /**
//     * 下单
//     */
//    fun callOrder(userId:String,time:String,startAddress:String,deplon:Double,deplat:Double,endAddress:String,bourlon:Double,bourlat:Double
//                  ,cityId:String,sermodel:String,money:Double?,coupon:Double?,couponId:String?) =
//            request().callOrder(userId,time,startAddress,deplon,deplat,endAddress,bourlon,bourlat,cityId,sermodel,money,coupon,couponId).defaultScheduler()
//
//    fun callDayOrder(userId:String,time:String,startAddress:String,deplon:Double,deplat:Double
//                  ,cityId:String,sermodel:String,day:Float,endTime:String,money:Double?,coupon:Double?,couponId:String?) =
//            request().callDayOrder(userId,time,startAddress,deplon,deplat,cityId,sermodel,day,endTime,money,coupon,couponId).defaultScheduler()
//
//    /**
//     * 查看司机主页
//     */
//    fun getDriverInfo(orderId:String,page:Int) =
//            request().getDriverInfo(orderId,page).defaultScheduler()
//
//    /**
//     * 订单列表
//     */
//    fun getOrderList(userId:String,state:Int?,page: Int) =
//            request().getOrderList(userId,state,page).defaultScheduler()
//
//    fun getOrderDetail(orderId:String) =
//            request().getOrderDetail(orderId).defaultScheduler()
//
//    /**
//     * 下小时计费订单
//     */
//    fun callHourOrder(userId:String,time:String,startAddress:String,deplon:Double,deplat:Double
//                  ,cityId:String,sermodel:String) =
//            request().callHourOrder(userId,time,startAddress,deplon,deplat,cityId,sermodel).defaultScheduler()
//
//    /**
//     * 取消订单信息
//     */
//    fun getCancelOrderInfo(orderId:String) =
//            request().getCancelOrderInfo(orderId).defaultScheduler()
//
//    /**
//     * 取消订单
//     */
//    fun cancelOrder(orderId:String,money:Double,content:String?="",reason:String?="") =
//            request().cancelOrder(orderId,money,content,reason).defaultScheduler()
//
//    /**
//     * 优惠券列表
//     */
//    fun getCouponsList(userId:String,type:Int,page:Int) =
//            request().getCouponsList(userId,type,page).defaultScheduler()
//
//    /**
//     * 删除订单
//     */
//    fun deleteOrder(orderId:String) =
//            request().deleteOrder(orderId).defaultScheduler()
//
//    fun complaints(orderId:String?,reason: String?,content:String?) =
//            request().complaints(orderId,reason,content).defaultScheduler()
//

//
//    /**
//     * 获取支付信息
//     */
//    fun getPayInfo(type: Int,orderId: String?) =
//            request().getPayInfo(type,orderId).defaultScheduler()
//
//    fun getInviteList(userId: String,page: Int) =
//            request().getInviteList(userId,page).defaultScheduler()
//
//    fun getInviteInfo(userId: String) =
//            request().getInviteInfo(userId).defaultScheduler()
//
//    fun getBanner() =
//            request().getBanner().defaultScheduler()

}

