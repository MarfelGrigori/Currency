package com.example.restapi


import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.restapi.data.entities.currency.Rate
import com.example.restapi.databinding.ItemSettingsBinding
import android.content.SharedPreferences
import android.view.MotionEvent
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MotionEventCompat
import com.example.restapi.utils.ItemTouchHelperAdapter
import com.example.restapi.utils.ItemTouchHelperViewHolder
import com.example.restapi.utils.OnStartDragListener
import java.util.*

import kotlin.collections.ArrayList


class SettingsAdapter(
    val viewModel: MainViewModel,
    val context: Context,
    private val reordered: () -> Unit
) :
    RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>(), ItemTouchHelperAdapter {

    private var currencyList = ArrayList<Rate>()

    @SuppressLint("NotifyDataSetChanged")
    fun initialize(list: List<Rate>) {
        currencyList = list.toMutableList() as ArrayList<Rate>
        notifyDataSetChanged()
    }

    inner class SettingsViewHolder(
        private val binding: ItemSettingsBinding, val reordered: () -> Unit,
        private val dragStartListener: OnStartDragListener? = null
    ) :
        RecyclerView.ViewHolder(binding.root), ItemTouchHelperViewHolder {
        @SuppressLint(
            "SetTextI18n",
            "RestrictedApi",
            "WrongConstant",
            "ClickableViewAccessibility",
            "NotifyDataSetChanged"
        )
        fun bind(person: Rate) {
            binding.currencyAbbreveation.text = person.curAbbreviation
            binding.name.text = person.curScale.toString().plus(person.curName)
            binding.check.isChecked = person.isFavourite
            val index = currencyList.indexOf(person)
//           shared prefs for checked rates
            val contextWrapper = ContextWrapper(context)
            val sh =
                contextWrapper.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_APPEND)
            val editor: SharedPreferences.Editor = sh.edit()
            val str = sh.all
//            пертаскивания
            val sharedOrder = contextWrapper.getSharedPreferences(
                "MySharedPrefOrdered",
                AppCompatActivity.MODE_APPEND
            )

//            порядок валют

            currencyList.forEach { it1 ->
                sharedOrder.all.forEach { (s, any) ->
                    if (it1.curName == any)
                        it1.prefIndex = s.toInt()
                    viewModel.modifyList(currencyList)
                }
            }
            currencyList.sortBy { it.prefIndex }
            binding.drag.setOnTouchListener { _, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    dragStartListener?.onStartDrag(this)
                }
                false
            }
//            checkbox
            str.forEach {
                val currency = it.key
                if (currency == person.curAbbreviation) {
                    currencyList[index].isFavourite = true
                    viewModel.modifyList(currencyList)
                    binding.check.isChecked = true
                }
            }
            binding.check.setOnClickListener {
                if (binding.check.isChecked) {
                    currencyList[index].isFavourite = (it as Switch).isChecked
                    viewModel.modifyList(currencyList)
                    editor.apply {
                        putString(person.curAbbreviation, person.curAbbreviation)
                    }.apply()
                } else {
                    editor.apply {
                        binding.check.isChecked = false
                        remove(currencyList[index].curAbbreviation)
                        viewModel.modifyList(currencyList)
                    }.apply()
                }
            }


        }

        override fun onItemSelected() {

        }

        override fun onItemClear() {
            reordered()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val binding = DataBindingUtil.inflate<ItemSettingsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_settings,
            parent,
            false
        )
        return SettingsViewHolder(binding, reordered)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(currencyList[position])
    }

    override fun getItemCount(): Int {
        return currencyList.size
    }

    @SuppressLint("WrongConstant")
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(currencyList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        val contextWrapper = ContextWrapper(context)
        val sh = contextWrapper.getSharedPreferences(
            "MySharedPrefOrdered",
            AppCompatActivity.MODE_APPEND
        )
        sh.edit().clear().apply()
        val editor: SharedPreferences.Editor = sh.edit()
        currencyList.forEach {
            editor.apply {
                val index = currencyList.indexOf(it)
                putString(index.toString(), it.curName)
            }.apply()
        }

        return true
    }

    override fun onItemDismiss(position: Int) {

    }
}