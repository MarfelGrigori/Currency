package com.example.restapi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restapi.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private lateinit var curAdapter: CurrencyAdapter
    private var errorMessage: String? = "error"
    private lateinit var binding : ActivityMainBinding
    @SuppressLint("SimpleDateFormat", "WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        action bar
        val actionBar = (this).supportActionBar
        actionBar?.title = "курсы валют"

//        progress
        val progressBar = binding.progressBar
        viewModel.isLoading.observe(this) {
            if (it) {
                progressBar.visibility = View.VISIBLE
                actionBar?.hide()
            } else {
                progressBar.visibility = View.GONE
                actionBar?.show()
            }

        }
//        date
        val todayDate = binding.todayDate
        val sdf = SimpleDateFormat("dd-M-yyyy")
        val formatForQuery = SimpleDateFormat("yyyy-M-d")
        val currentDate = sdf.format(Date())
        todayDate.text = currentDate
        val tomorrowDay = binding.tomorrowDate
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time
        val tomorrowAsString: String = sdf.format(tomorrow)
        tomorrowDay.text = tomorrowAsString

//      recycler
        val recycler = binding.recyclerview

        recycler.layoutManager = LinearLayoutManager(this)
        viewModel.ratesTomorrow.observe(this) { it1 ->
            viewModel.rates.observe(this) {
                curAdapter = CurrencyAdapter(viewModel)
                curAdapter.initialize(it, it1)
                recycler.adapter = curAdapter
                Log.e("TAG", it.toString())

            }
            if (it1.isEmpty()){
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = calendar.time
                val yesterdayAsString: String = sdf.format(yesterday)
                tomorrowDay.text = yesterdayAsString
                val dateYesterday = formatForQuery.format(yesterday)
                viewModel.loadRatesForTomorrow(0, dateYesterday)
            }
        }
        val dateTomorrow = formatForQuery.format(tomorrow)
        viewModel.loadRatesForTomorrow(0, dateTomorrow)
        viewModel.loadRates(0)
//сделать чтобы прихлдиди вчерашние результапты

        viewModel.errorBus.observe(this) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.error))
                .setMessage(it)
                .show()
            errorMessage = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            return true
        startActivity(Intent(this, SettingsActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

}