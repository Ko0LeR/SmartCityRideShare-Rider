package com.tlabs.smartcity.rideshare.ridesharerider.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.tlabs.smartcity.rideshare.ridesharerider.data.*
import kotlinx.coroutines.Deferred
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SovrApi {
    @POST("/api/login")
    fun login(@Body body: LoginReq): Deferred<LoginResp>

    @POST("/api/connectionoffer")
    fun createConnOffer(@Header("Authorization")header: String,
                        @Body body: CreateConnectionOfferRequest): Deferred<CreateConnOfferResp>

    @POST("/api/connectionrequest")
    fun acceptConnOffer(@Header("Authorization")header: String,
                        @Body body: CreateConnOfferRespMsg
    ): Deferred<ResponseBody>

    @GET("/api/wallet/default")
    fun pairwiseConnections(@Header("Authorization")header: String
    ): Deferred<PairwiseConnResp>

    @POST("/api/proofrequest")
    fun createProofReq(@Header("Authorization")header: String,
                       @Body body: CreateProofReq
    ): Deferred<GetProofReqResp>

    @GET("/api/proofrequest")
    fun getProofReq(@Header("Authorization")header: String
    ): Deferred<List<GetProofReqResp>>

    @POST("/api/proof")
    fun acceptProofReqAndSendProof(@Header("Authorization")header: String,
                                   @Body body: AccAndCreProof
    ): Deferred<List<ResponseBody>>

    @GET("/api/proof")
    fun getAllProof(@Header("Authorization")header: String
    ): Deferred<List<RequestedPredicates>>

    @GET("/api/proof/{id}")
    fun getProofReq(@Header("Authorization")header: String,
                    @Path("id")id:String
    ): Deferred<RequestedPredicates>

    companion object {
        val instance = Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .baseUrl("http://10.177.1.142:8000")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SovrApi::class.java)
    }
}