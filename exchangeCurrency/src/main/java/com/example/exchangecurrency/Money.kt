package com.example.exchangecurrency

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat



data class Money(val amount: BigDecimal = BigDecimal.ZERO, val currency: Currency) : Comparable<Money> {

    constructor(amount: Double, currency: Currency) : this(BigDecimal(amount), currency)

    override operator fun compareTo(other: Money): Int {
        val thisBaseCurrencyAmount = amount.divide(currency.rate, MathContext.DECIMAL32)
        val otherBaseCurrencyAmount = other.amount.divide(other.currency.rate, MathContext.DECIMAL32)
        return thisBaseCurrencyAmount.compareTo(otherBaseCurrencyAmount)
    }

    override fun toString(): String {
        return if (currency == Currency.NONE) {
            val formatter = DecimalFormat("#.## (no currency)")
            formatter.format(amount)
        } else {
            val decimalFormat = DecimalFormat("#.##")
             decimalFormat.format(amount)
        }
    }

    fun convertInto(targetCurrency: Currency): Money {
        val rate = currency.conversionTo(targetCurrency)
        return Money(amount.multiply(rate), targetCurrency)
    }


    override fun equals(other: Any?): Boolean {
        return if (other is Money) {
            val codeEquals = currency.currencyCode == other.currency.currencyCode
            val thisAmount = amount.setScale(MoneyConfig.scale, RoundingMode.HALF_DOWN)
            val otherAmount = other.amount.setScale(MoneyConfig.scale, RoundingMode.HALF_DOWN)
            val amountEquals = thisAmount == otherAmount
            codeEquals && amountEquals
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = amount.hashCode()
        result = 31 * result + currency.hashCode()
        return result
    }

    companion object {
        val ZERO = Money(BigDecimal.ZERO, Currency.NONE)
    }
}
