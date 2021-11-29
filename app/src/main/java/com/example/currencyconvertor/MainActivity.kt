package com.example.currencyconvertor

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.currencyconvertor.api.CurrencyEvent
import com.example.currencyconvertor.databinding.ActivityMainBinding
import com.example.currencyconvertor.helper.Constants
import com.example.currencyconvertor.helper.Utility.getAllCountries
import com.example.currencyconvertor.helper.Utility.isNetworkAvailable
import com.example.currencyconvertor.helper.Utility.makeStatusBarTransparent
import com.example.currencyconvertor.helper.hideKeyboard
import com.example.currencyconvertor.model.CurrencyInfo
import com.example.currencyconvertor.viewmodel.CurrencyViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    //viewBinding to get the reference of the views
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private var countries = ArrayList<String>()
    private var selectedItem1: String? = "AFN"
    private var selectedItem2: String? = "AFN"
    private lateinit var currencyInfo: List<CurrencyInfo>
    private var countrySpinnerPosition = 0

    //ViewModel
    private val mainViewModel: CurrencyViewModel by viewModels()

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme_NoActionBar)

        super.onCreate(savedInstanceState)

        //Make status bar transparent
        makeStatusBarTransparent(this)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        observeUI()
        getCurrencyData()
        //Listen to click events
        setUpClickListener()
    }

    private fun observeUI() {
        /** Handled API response*/
        lifecycleScope.launchWhenStarted {
            mainViewModel.conversion.collect { event ->
                when (event) {
                    is CurrencyEvent.Success -> {
                        currencyInfo = event.currencyInfo
                        initSpinner(currencyInfo)
                    }
                    is CurrencyEvent.Failure -> {
                        displaySnackBar(event.errorText)

                    }

                    else -> Unit
                }
            }
        }
    }

    /**
     * This method does everything required for handling spinner (Dropdown list) -
     * showing list of countries, handling click events on items selected.
     * @param List if CurrencyInfo objects
     */

    private fun initSpinner(currencyInfo: List<CurrencyInfo>) {
        countries = getAllCountries(currencyInfo)
        //get first spinner country reference in view
        val spinnerFromCurrency = binding.spnFromCurrency

        //set items in the spinner i.e a list of all countries
        spinnerFromCurrency.setItems(countries)
        spinnerFromCurrency.setOnClickListener {
            hideKeyboard(this)
        }

        //Handle selected item, by getting the item and storing the value in a  variable - selectedItem1
        spinnerFromCurrency.setOnItemSelectedListener { view, position, id, item ->
            selectedItem1 = item.toString()
            countrySpinnerPosition = position
            binding.txtFirstCurrencyName.text = currencyInfo[position].currencyCode
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
            selectedItem2 = item.toString()
            binding.txtSecondCurrencyName.text = "AUD"
        }
    }


    /**
     * A method for handling click events in the UI
     */

    private fun setUpClickListener() {

        //Convert button clicked - check for empty string and internet then do the conersion
        binding.btnConvert.setOnClickListener {

            //check if the input is empty
            val numberToConvert = binding.etFirstCurrency.text.toString()

            if (numberToConvert.isEmpty() || numberToConvert == "0") {

                displaySnackBar(getString(R.string.blank_input))

            }

            //check if internet is available
            else if (!isNetworkAvailable(this)) {
                displaySnackBar(getString(R.string.internet_not_available))
            }

            //carry on and convert the value
            else {
                doConversion()
            }
        }
    }

    /**
     * A method that does the conversion by communicating with the API - fixer.io based on the data inputed
     * Uses viewModel and flows
     */

    private fun doConversion() {

        //hide keyboard
        hideKeyboard(this)

        //Get the data inputed

        val from = selectedItem1.toString()
        val to = selectedItem2.toString()
        val amount = binding.etFirstCurrency.text.toString().toDouble()
        val firstcountry = currencyInfo[countrySpinnerPosition].currencyName
        val rate = currencyInfo[countrySpinnerPosition].sellTT

        if (rate == Constants.ON_APP || rate == Constants.NOT_APPLICABLE) {
            binding.etSecondCurrency.setText("")

            displaySnackBar(getString(R.string.not_applicable))

        } else
            binding.etSecondCurrency.setText(
                mainViewModel.convertCurrency(
                    from,
                    to,
                    firstcountry,
                    "AUD",
                    amount,
                    rate.toDouble()
                )
            )
    }


    /**
     * Method for changing the background color of snackBars
     */

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }

    private fun getCurrencyData() {
        if (isNetworkAvailable(this))
            mainViewModel.getCurrencyApi()
        else
            displaySnackBar(getString(R.string.internet_not_available))
    }

    private fun displaySnackBar(charSequence: CharSequence) {
        Snackbar.make(
            binding.mainLayout,
            charSequence,
            Snackbar.LENGTH_LONG
        )
            .withColor(ContextCompat.getColor(this, R.color.dark_red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

}

