package com.example.currencyconvertor.api
import com.example.currencyconvertor.models.CurrencyInfo

sealed class CurrencyEvent {
    class Success(val currencyInfo: List<CurrencyInfo>):CurrencyEvent()
    class Failure(val errorText: String):CurrencyEvent()
    object Empty:CurrencyEvent()
}