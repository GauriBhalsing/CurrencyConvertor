package com.example.currencyconvertor


import com.example.currencyconvertor.MockProvider.getJsonObject
import com.example.currencyconvertor.api.CurrencyApi
import com.example.currencyconvertor.repository.MainRepository
import com.example.currencyconvertor.viewmodel.CurrencyViewModel
import io.mockk.mockk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain

import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CurrencyViewModelTest : BaseTest() {
    private lateinit var mCurrencyViewModel: CurrencyViewModel
    private lateinit var mMainRepository: MainRepository
    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var currencyApi: CurrencyApi
    private val testScope = TestCoroutineScope(testDispatcher)

    @get:Rule
    val testCoroutineRule = MainCoroutineRule()
    @Before
    fun start() {
        //Used for initiation of Mockk
        currencyApi= mockk()
        mMainRepository = mockk()
        mCurrencyViewModel = CurrencyViewModel(mMainRepository)
    }

    @Test
    fun testConvertJsonToModel() {
        var mJsonObject: JSONObject = getJsonObject()
        val currencyInfo=mCurrencyViewModel.convertJsonToModel(mJsonObject.toString())
        assert(currencyInfo !=null)
    }

    @Test
    fun testGetConversionAmount()
    {
        val price=mCurrencyViewModel.convertCurrency("USA","Australia",
            "USA","AUS", 1000.0,0.5)
        assert(price=="1")
    }

    @After
    fun after() {
        Dispatchers.resetMain()
        testScope.cleanupTestCoroutines()
    }
}