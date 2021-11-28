package com.example.exchangecurrency

/*
*This class holds a currency code and is able to fetch the symbol.
* */
data class CurrencyCode(val code: String) {

    fun getSymbol(): String {
        var symbol: String
        try {
            symbol = java.util.Currency.getInstance(code).symbol
        } catch (ia: IllegalArgumentException) {
            // Log.w("Currency Code", "Could not find symbol for $code")
            symbol = code
        }
        return symbol
    }

}
