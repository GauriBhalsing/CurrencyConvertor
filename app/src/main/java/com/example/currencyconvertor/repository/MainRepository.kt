package com.example.currencyconvertor.repository

import com.example.currencyconvertor.api.Resource
import okhttp3.ResponseBody

interface MainRepository {
    suspend fun getRates(): Resource<ResponseBody>
}