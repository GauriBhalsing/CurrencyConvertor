package com.example.currencyconvertor.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
/*
This will be used to call API
*/
interface CurrencyApi {
    @GET("/bin/getJsonRates.wbc.fx.json")
    suspend fun getCurrencyRate(): Response<ResponseBody>
}