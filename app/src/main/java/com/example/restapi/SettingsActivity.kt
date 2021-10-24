package com.example.restapi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.restapi.reorder.ReorderHelperCallback


class SettingsActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private var mItemTouchHelper: ItemTouchHelper? = null
    lateinit var adapter : SettingsAdapter

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val actionBar = (this).supportActionBar
        actionBar?.title = "настройки"
        val recycler = findViewById<RecyclerView>(R.id.recyclerview_settings)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = SettingsAdapter(viewModel,applicationContext){
        }

        viewModel.rates.observe(this){
            recycler.adapter = adapter
           adapter.initialize(it)
            }

        viewModel.loadRates(0)
//сделать чтобы приходиди вчерашние результапты
        viewModel.errorBus.observe(this) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.error))
                .setMessage(it)
                .show()
        }
        val callback: ItemTouchHelper.Callback = ReorderHelperCallback(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(recycler)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            return true
        startActivity(Intent(this,MainActivity::class.java))
        return super.onOptionsItemSelected(item)
    }
}