package com.hbcx.driver.network

import com.hbcx.driver.utils.JsonValidator
import com.hbcx.driver.utils.log.LogUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络请求retrofit初始化。
 */
class RRetrofit private constructor() {

    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .addConverterFactory(JsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient.Builder()
                    .connectTimeout(30,TimeUnit.SECONDS)
                    .addInterceptor(getHttpLoggingInterceptor())
                    .build())
            .build()
    companion object {
        fun instance(): RRetrofit = RRetrofit()
    }

    fun <T> create(clazz: Class<T>): T = retrofit.create(clazz)

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            val jsonValidator = JsonValidator()
            if (!jsonValidator.validate(message)) {
                LogUtil.d("HttpLog", message)
            } else {
                LogUtil.json("HttpLog", message)
            }
        })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }
}