package com.hbcx.driver.network

import cn.sinata.xldutils.data.ResultData
import com.google.gson.JsonObject
import com.hbcx.driver.network.beans.*
import io.reactivex.Flowable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 *
 */
interface ApiService {

    @POST("app/server")
    fun stringRequest(@Query("key") key: String): Flowable<ResultData<String>>

    @POST("app/server")
    fun intRequest(@Query("key") key: String): Flowable<ResultData<Int>>

    @POST("app/server")
    fun simpleRequest(@Query("key") key: String): Flowable<ResultData<JsonObject>>

    @POST("app/server")
    fun getMessages(@Query("key") key: String): Flowable<ResultData<ArrayList<Message>>>

    @POST("app/server")
    fun getCarTypeList(@Query("key") key: String): Flowable<ResultData<List<com.hbcx.driver.network.beans.CarType>>>

    @POST("app/server")
    fun getDriverMain(@Query("key") key: String): Flowable<ResultData<com.hbcx.driver.network.beans.DriverMain>>

    @POST("app/server")
    fun orderRequest(@Query("key") key: String): Flowable<ResultData<com.hbcx.driver.network.beans.Order>>

    @POST("app/server")
    fun carOrderList(@Query("key") key: String): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.OrderList>>>

    @POST("app/server")
    fun getOpenCity(@Query("key") key: String): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.OpenCity>>>

    @POST("app/server")
    fun getTicketList(@Query("key") key: String): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.TicketBus>>>

    @POST("app/server")
    fun getTicketDetail(@Query("key") key: String): Flowable<ResultData<com.hbcx.driver.network.beans.TicketDetail>>

    @POST("app/server")
    fun getTicketLineDetail(@Query("key") key: String): Flowable<ResultData<com.hbcx.driver.network.beans.TicketBus>>

    @POST("app/server")
    fun getLineDetail(@Query("key") key: String): Flowable<ResultData<com.hbcx.driver.network.beans.TicketLineDetail>>

    @POST("app/server")
    fun getPassengerList(@Query("key") key: String): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.Passenger>>>

    @POST("app/server")
    fun getDriverList(@Query("key") key: String): Flowable<ResultData<ArrayList<Driver>>>

    @POST("app/server")
    fun getCarList(@Query("key") key: String): Flowable<ResultData<ArrayList<Car>>>

    @POST("app/server")
    fun getDriverDetail(@Query("key") key: String): Flowable<ResultData<Driver>>

    @POST("app/server")
    fun getEvaluateData(@Query("key") key: String): Flowable<ResultData<Evaluate>>

    @POST("app/server")
    fun getCarDetail(@Query("key") key: String): Flowable<ResultData<Car>>

    @POST("app/server")
    fun getWithdrawHistory(@Query("key") key: String): Flowable<ResultData<ArrayList<com.hbcx.driver.network.beans.WithdrawHistory>>>

    @POST("app/server")
    fun getIncomeData(@Query("key") key: String): Flowable<ResultData<com.hbcx.driver.network.beans.IncomeData>>

    @Multipart
    @POST("app/public/uplaodImg")
    fun uploadFile(@Part() filePart: MultipartBody.Part): Flowable<ResultData<JsonObject>>

    @POST("app/server")
    fun lineTypeList(@Query("key") key: String): Flowable<ResultData<ArrayList<LineType>>>

    @POST("app/server")
    fun regionList(@Query("key") key: String): Flowable<ResultData<ArrayList<Region>>>

    @POST("app/server")
    fun manageLineList(@Query("key") key: String): Flowable<ResultData<ArrayList<Line>>>

    @POST("app/server")
    fun enableLineList(@Query("key") key: String): Flowable<ResultData<ArrayList<EnableLine>>>

    @POST("app/server")
    fun enableCarList(@Query("key") key: String): Flowable<ResultData<ArrayList<EnableCar>>>

    @POST("app/server")
    fun classList(@Query("key") key: String): Flowable<ResultData<ArrayList<ClassModel>>>

    @POST("app/server")
    fun stationSiteList(@Query("key") key: String): Flowable<ResultData<ArrayList<HalfStation>>>

    @POST("app/server")
    fun lineDetail(@Query("key") key: String): Flowable<ResultData<Line>>

    @POST("app/server")
    fun classDetail(@Query("key") key: String): Flowable<ResultData<ClassModel>>

    @POST("app/server")
    fun linePriceList(@Query("key") key: String): Flowable<ResultData<ArrayList<LinePrice>>>

    @POST("app/server")
    fun getStations(@Query("key") key: String): Flowable<ResultData<ArrayList<Station>>>

    @POST
    fun getBankInfo(@Url url:String ,@Query("key") key: String, @Query("bankcard") bankcard: String) :Flowable<JsonObject>

    @POST("app/server")
    fun getTimePage(@Query("key") key: String): Flowable<ResultData<List<BusStation>>>

    @POST("app/server")
    fun getLocationPage(@Query("key") key: String): Flowable<ResultData<StationLocation>>

    @POST("app/server")
    fun commitTime(@Query("key") key: String): Flowable<ResultData<JsonObject>>

    @POST("app/server")
    fun commitLocation(@Query("key") key: String): Flowable<ResultData<JsonObject>>

    @POST("app/server")
    fun editTicketCount(@Query("key") key: String): Flowable<ResultData<JsonObject>>

}