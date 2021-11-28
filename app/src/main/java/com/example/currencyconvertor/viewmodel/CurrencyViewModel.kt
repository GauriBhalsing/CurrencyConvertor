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
import com.example.currencyconvertor.model.CurrencyInfo
import com.example.currencyconvertor.repository.MainRepository
import com.example.exchangecurrency.Currency
import com.example.exchangecurrency.Money
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject


/**
 * This view model will take data from repository and will send it to UI.
 * It is responsible to make any formatting,parsing of data.
 */
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

    /** Converting JsonObject to CurrencyInfo object
     * This will take API response as body and returns
     * @param ResponseBody
     * @return arraylist of CurrencyInfo
     * */
    private fun createCurrencyList(rateResponse: Resource.Success<ResponseBody>): ArrayList<CurrencyInfo> {
        val jsonObject = getJsonObject(rateResponse)
        val currencyList = ArrayList<CurrencyInfo>()
        jsonObject.let { jsonObject ->
            val keys: Iterator<String> = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val ratesJObject: JSONObject = jsonObject.getJSONObject(key)
                val ratesInnerKeys = ratesJObject.keys()
                while (ratesInnerKeys.hasNext()) {
                    val innerKey = ratesInnerKeys.next()
                    try {
                        val innermostJObject = ratesJObject.getJSONObject(innerKey)
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

    /**
     * This method will do currency conversion.
     * @param fromCountry Country name from currency will be converted
     * @param toCountry country name of target currency
     * @param fromCountryCode country code from currency will be converted
     * @param toCountryCode country code of expected currency
     * @param amount amount which need to be converted
     * @param rate selling rate
     * @return converted currency amount
     */
    fun convertCurrency(
        fromCountry: String, toCountry: String, fromCountryCode:
        String, toCountryCode: String, amount: Double, rate: Double
    ): String {
        val fromCurrency = Currency(fromCountry, fromCountryCode, rate)
        val toCurrency = Currency(toCountry, toCountryCode)

        // create a money object and use it for calculations.
        val currentMoney = Money(amount, fromCurrency)
        val convertedMoney = currentMoney.convertInto(toCurrency)

        return convertedMoney.toString()
    }

}