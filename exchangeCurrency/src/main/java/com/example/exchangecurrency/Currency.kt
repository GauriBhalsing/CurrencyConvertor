package com.example.exchangecurrency

import java.math.BigDecimal
import java.math.MathContext


/**
 * This class is responsible for holding currency data and doing conversion
 */
data class Currency(
    var currencyCode: CurrencyCode,
    var name: String,
    var rate: BigDecimal = BigDecimal.ONE
) {

    constructor(code: String, name: String) : this(CurrencyCode(code), name)
    constructor(code: String, name: String, rate: Double) : this(
        CurrencyCode(code),
        name,
        BigDecimal(rate)
    )

    operator fun compareTo(another: Currency): Int {
        return this.name.compareTo(another.name)
    }


    /**
     * Calculated the conversion rate from this currency into a target currency.
     */
    fun conversionTo(targetCurrency: Currency): BigDecimal {
        return BigDecimal.ONE
            .divide(rate, MathContext.DECIMAL128)
            .multiply(targetCurrency.rate)
    }

    companion object {
        val NONE = Currency(code = "NONE", name = "NONE")
    }

    override fun toString(): String {
        currencyCode.getSymbol().let { symbol ->
            return if (symbol != currencyCode.code) {
                "$currencyCode: $name ($symbol)"
            } else {
                "$currencyCode: $name"
            }
        }
    }

}
