package com.example.currencyconvertor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertor.api.CurrencyEvent
import com.example.currencyconvertor.api.Resource
import com.example.currencyconvertor.helper.Constants.BRANDS
import com.example.currencyconvertor.helper.Constants.DATA
import com.example.currencyconvertor.helper.Constants.FX
import com.example.currencyconvertor.helper.Constants.PORTFOLIOS
import com.example.currencyconvertor.helper.Constants.PRODUCTS
import com.example.currencyconvertor.helper.Constants.WBC
import com.example.currencyconvertor.models.CurrencyInfo
import com.example.currencyconvertor.repository.MainRepository

import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import java.text.DecimalFormat
import javax.inject.Inject


@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {
    private val _conversion = MutableStateFlow<CurrencyEvent>(CurrencyEvent.Empty)
    val conversion: StateFlow<CurrencyEvent> = _conversion

    /** Actual API call*/
    fun getCurrencyApi() {
        viewModelScope.launch(Dispatchers.IO) {

            when (val rateResponse = repository.getRates()) {
                is Resource.Error -> _conversion.value =
                    CurrencyEvent.Failure(rateResponse.message!!)
                is Resource.Success -> {
                    _conversion.value = CurrencyEvent.Success(createCurrencyList(rateResponse))
                }
            }
        }
    }

    /** Converting JsonObject to CurrencyInfo object*/
    private fun createCurrencyList(rateResponse: Resource.Success<ResponseBody>): ArrayList<CurrencyInfo> {
        var jsonObject = getJsonObject(rateResponse)
        var currencyList = ArrayList<CurrencyInfo>()
        jsonObject.let { jsonObject ->
            val keys: Iterator<String> = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val ratesJObject: JSONObject = jsonObject.getJSONObject(key)
                val ratesInnerKeys = ratesJObject.keys()
                while (ratesInnerKeys.hasNext()) {
                    val innerKey = ratesInnerKeys.next()
                    try {
                        var innermostJObject = ratesJObject.getJSONObject(innerKey)
                        currencyList.add(
                            convertJsonToModel(
                                innermostJObject.getJSONObject(key).toString()
                            )
                        )
                    } catch (e: Exception) {
                        // ignore for String type
                    }
                }
            }
        }
        return currencyList
    }

    fun convertJsonToModel(jsonObject: String): CurrencyInfo {
        return Gson().fromJson(jsonObject, CurrencyInfo::class.java)
    }

    private fun getJsonObject(rateResponse: Resource.Success<ResponseBody>): JSONObject {
        return JSONObject(rateResponse.data?.string())
            .getJSONObject(DATA)
            .getJSONObject(BRANDS)
            .getJSONObject(WBC)
            .getJSONObject(PORTFOLIOS)
            .getJSONObject(FX)
            .getJSONObject(PRODUCTS)
    }

    /*fun getConversionAmount(amount: String, sellingPrice: String):String {
        return CurrencyConversion.convertCurrency(sellingPrice, amount)
    }*/
}