package com.example.currencyconvertor

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels

import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.currencyconvertor.api.CurrencyEvent
import com.example.currencyconvertor.databinding.ActivityMainBinding
import com.example.currencyconvertor.helper.Utility
import com.example.currencyconvertor.helper.hideKeyboard
import com.example.currencyconvertor.models.CurrencyInfo
import com.example.currencyconvertor.viewmodel.CurrencyViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    //viewBinding to get the reference of the views
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var countries = ArrayList<String>()

    //ViewModel
    private val mainViewModel: CurrencyViewModel by viewModels()

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState)

        //Make status bar transparent
        Utility.makeStatusBarTransparent(this)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        /** Handled API response*/
        lifecycleScope.launchWhenStarted {
            mainViewModel.conversion.collect { event ->
                when (event) {
                    is CurrencyEvent.Success -> {
                        initSpinner(event.currencyInfo)
                    }
                    is CurrencyEvent.Failure -> {
                        //currencyFragmentBinding.progressBar.isVisible = false
                    }

                    else -> Unit
                }
            }
        }


        getCurrencyData()
        //Listen to click events
        setUpClickListener()
    }


    /**
     * This method does everything required for handling spinner (Dropdown list) - showing list of countries, handling click events on items selected.*
     */

    private fun initSpinner(currencyInfo: List<CurrencyInfo>) {
        countries = Utility.getAllCountries(currencyInfo)
        //get first spinner country reference in view
        val spinnerFromCurrency = binding.spnFromCurrency

        //set items in the spinner i.e a list of all countries
        spinnerFromCurrency.setItems(countries)
       spinnerFromCurrency.setOnClickListener {
            hideKeyboard(this)
        }

        //Handle selected item, by getting the item and storing the value in a  variable - selectedItem1
        spinnerFromCurrency.setOnItemSelectedListener { view, position, id, item ->

        }
        //get second spinner country reference in view
        val spinnerToCurrency = binding.spnToCurrency

        //hide key board when spinner shows
        spinnerFromCurrency.setOnClickListener {
            hideKeyboard(this)
        }

        //"Australia" is not available in API response so setting it separately.
        // In other cases,"countries" list can be used here as well
        spinnerToCurrency.setItems("Australia")


        //Handle selected item, by getting the item and storing the value in a  variable - selectedItem2,
        spinnerToCurrency.setOnItemSelectedListener { view, position, id, item ->
        }
    }

    /**
     * A method for handling click events in the UI
     */

    private fun setUpClickListener(){

        //Convert button clicked - check for empty string and internet then do the conersion
        binding.btnConvert.setOnClickListener {

            //check if the input is empty
            val numberToConvert = binding.etFirstCurrency.text.toString()

            if(numberToConvert.isEmpty() || numberToConvert == "0"){
                Snackbar.make(binding.mainLayout,getString(R.string.blank_input), Snackbar.LENGTH_LONG)
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            }

            //check if internet is available
            else if (!Utility.isNetworkAvailable(this)){
                Snackbar.make(binding.mainLayout,getString(R.string.internet_not_available), Snackbar.LENGTH_LONG)
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            }

            //carry on and convert the value
            else{
                doConversion()
            }
        }




    }

    /**
     * A method that does the conversion by communicating with the API - fixer.io based on the data inputed
     * Uses viewModel and flows
     */

    private fun doConversion(){
/*
        //hide keyboard
        Utility.hideKeyboard(this)

        //make progress bar visible
        binding.prgLoading.visibility = View.VISIBLE

        //make button invisible
        binding.btnConvert.visibility = View.GONE

        //Get the data inputed

        val from = selectedItem1.toString()
        val to = selectedItem2.toString()
        val amount = binding.etFirstCurrency.text.toString().toDouble()

        //do the conversion
        Log.e("currencyList", "Activity called")


        //observe for changes in UI
        observeUi()*/

    }

    /**
     * Using coroutines flow, changes are observed and responses gotten from the API
     *
     */

    @SuppressLint("SetTextI18n")
    private fun observeUi() {

/*
        mainViewModel.data.observe(this, androidx.lifecycle.Observer {result ->

            when(result.status){
                *//*Resource.Status.SUCCESS -> {

                        val map: Map<String, Rates>

                        map = result.data.

                        map.keys.forEach {

                            val rateForAmount = map[it]?.rate_for_amount

                            mainViewModel.convertedRate.value = rateForAmount

                            //format the result obtained e.g 1000 = 1,000
                            val formattedString = String.format("%,.2f", mainViewModel.convertedRate.value)

                            //set the value in the second edit text field
                            binding.etSecondCurrency.setText(formattedString)

                        }


                        //stop progress bar
                        binding.prgLoading.visibility = View.GONE
                        //show button
                        binding.btnConvert.visibility = View.VISIBLE


                }*//*
                Resource.Status.ERROR -> {

                    val layout = binding.mainLayout
                    Snackbar.make(layout,  "Oopps! Something went wrong, Try again", Snackbar.LENGTH_LONG)
                        .withColor(ContextCompat.getColor(this, R.color.dark_red))
                        .setTextColor(ContextCompat.getColor(this, R.color.white))
                        .show()
                    //stop progress bar
                    binding.prgLoading.visibility = View.GONE
                    //show button
                    binding.btnConvert.visibility = View.VISIBLE
                }

                Resource.Status.LOADING -> {
                    //stop progress bar
                    binding.prgLoading.visibility = View.VISIBLE
                    //show button
                    binding.btnConvert.visibility = View.GONE
                }
            }
        })*/
    }

    /**
     * Method for changing the background color of snackBars
     */

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }
    private fun getCurrencyData() {
        if (Utility.isNetworkAvailable(this) == true)
            mainViewModel.getCurrencyApi()
        else
            Snackbar.make(binding.mainLayout,getString(R.string.internet_not_available), Snackbar.LENGTH_LONG)
                .withColor(ContextCompat.getColor(this, R.color.dark_red))
                .setTextColor(ContextCompat.getColor(this, R.color.white))
                .show()
    }






}

