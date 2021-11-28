package com.example.currencyconvertor.api

import com.example.currencyconvertor.model.CurrencyInfo

/**
 * This class will be used to handle API event.
 * Success event : returns value if API call is successful
 * Failure event : return error message if API call fails
 */
sealed class CurrencyEvent {
    class Success(val currencyInfo: List<CurrencyInfo>) : CurrencyEvent()
    class Failure(val errorText: String) : CurrencyEvent()
    object Empty : CurrencyEvent()
}