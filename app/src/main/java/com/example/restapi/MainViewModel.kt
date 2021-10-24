package com.example.restapi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restapi.data.entities.currency.Rate
import com.example.restapi.repository.currency.CurrencyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel : ViewModel() {

    private val currencyRepository = CurrencyRepository()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val _rates = MutableLiveData<List<Rate>>()
    val rates: LiveData<List<Rate>> = _rates
    private val _modifiedRates = MutableLiveData<List<Rate>>()
    private val _ratesTomorrow = MutableLiveData<List<Rate>>()
    val ratesTomorrow: LiveData<List<Rate>> = _ratesTomorrow
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _errorBus = MutableLiveData<String>()
    val errorBus: LiveData<String> = _errorBus


    fun loadRates(periodicity: Int) {
        _isLoading.value = true
        ioScope.launch {
            try {
                _rates.postValue(currencyRepository.loadCurrency(periodicity))
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _errorBus.postValue(e.message)
            }
        }
    }

    fun loadRatesForTomorrow(periodicity: Int, date: String) {
        _isLoading.value = true
        ioScope.launch {
            try {
                _ratesTomorrow.postValue(currencyRepository.loadCurrencyTomorrow(periodicity, date))
                _isLoading.postValue(false)
            } catch (e: Exception) {
                _errorBus.postValue(e.message)
            }
        }
    }

    fun modifyList(list: List<Rate>) {
        _modifiedRates.value = list
    }
}