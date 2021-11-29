package com.example.exchangecurrency


import org.junit.Assert.assertEquals

import org.junit.Before
import org.junit.Test


class CurrencyTest {
    private lateinit var mCurrency: Currency
    private lateinit var mCurrencyCode: CurrencyCode
    private lateinit var mMoney: Money

    @Before
    fun start() {

        mCurrency = Currency("USD", "USA", 0.5)
        mCurrencyCode = CurrencyCode("GBP")
        mMoney = Money(100.0, mCurrency)
    }

    @Test
    fun testCompareTo() {
        val targetCurrency = Currency("AUD", "Australia", 0.5)
        val convertedCurrency = mCurrency.conversionTo(targetCurrency)
        assertEquals(convertedCurrency.toDouble(), 1.0, 0.01)
    }

    @Test
    fun testGetSymbol() {
        val symbol = mCurrencyCode.getSymbol()
        assertEquals("GBP", symbol)
    }

    @Test
    fun testConvertInto() {
        var convertedMoney: Money = mMoney.convertInto(mCurrency)
        assertEquals("100", convertedMoney.toString())
    }
}


