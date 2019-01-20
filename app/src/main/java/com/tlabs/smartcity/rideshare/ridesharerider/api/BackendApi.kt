package com.tlabs.smartcity.rideshare.ridesharerider.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tlabs.smartcity.rideshare.ridesharerider.data.BalanceResponse
import com.tlabs.smartcity.rideshare.ridesharerider.data.PayDto
import com.tlabs.smartcity.rideshare.ridesharerider.data.RequestRide
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BackendApi {
    @POST("/availableRides")
    fun requestRide(@Body requestRide: RequestRide): Deferred<ResponseBody>

    @POST("/verifyRide")
    fun pay(@Body dto: PayDto): Deferred<ResponseBody>

    @GET("/balance/{wallet}")
    fun getBalance(@Path("wallet") wallet: String = "0x322DDB258B6A596C332A8E50eB18B6Cc3C975AC7"): Deferred<BalanceResponse>

    companion object {
        val instance = Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .baseUrl("http://10.177.1.130:8080")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(BackendApi::class.java)
    }
}
