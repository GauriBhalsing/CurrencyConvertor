package com.example.currencyconvertor.repository


import com.example.currencyconvertor.api.CurrencyApi
import com.example.currencyconvertor.api.Resource
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * This repository class will be responsible to make API call and fetch data
 */
class DefaultMainRepository @Inject constructor(
    private val api: CurrencyApi
) : MainRepository {
    override suspend fun getRates(): Resource<ResponseBody> {
        return try {
            val response = api.getCurrencyRate()
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (error: Exception) {
            Resource.Error(error.message ?: "An error occurred")
        }
    }
}