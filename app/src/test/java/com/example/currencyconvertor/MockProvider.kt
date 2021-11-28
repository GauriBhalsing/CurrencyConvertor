package com.example.currencyconvertor

import org.json.JSONObject

object MockProvider {

    fun getJsonObject(): JSONObject
    {
        val mJSONObject = JSONObject()
        mJSONObject.put("country", "USA")
        mJSONObject.put("sellTT", "0.6944")
        mJSONObject.put("currencyCode", "USD")
        return mJSONObject
    }
}