package com.example.restapi

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.restapi.data.entities.currency.Rate

class CurrencyAdapter(val viewModel: MainViewModel) :
    RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {
    private var currencyList = ArrayList<Rate>()
    var currencyListTomorrow = ArrayList<Rate>()

    //    инициализируем наши листы
    @SuppressLint("NotifyDataSetChanged")
    fun initialize(list1: List<Rate>, list2: List<Rate>) {
        currencyList = list1.toMutableList() as ArrayList<Rate>
        currencyListTomorrow = list2.toMutableList() as ArrayList<Rate>
        notifyDataSetChanged()
    }

    inner class CurrencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("WrongConstant")
        fun setData(itemView: View, position: Int) {
            val currency = currencyList[position]
            val currencyTomorrow = currencyListTomorrow[position]
            val rateTomorrow = itemView.findViewById<TextView>(R.id.tomorrow_rate)
            rateTomorrow.text = currencyTomorrow.curOfficialRate.toString()
            val abbreviation = itemView.findViewById<TextView>(R.id.currency_abbreveation)
            abbreviation.text = currency.curAbbreviation
            val name = itemView.findViewById<TextView>(R.id.name)
            name.text = currency.curScale.toString().plus(currency.curName)
            val rate = itemView.findViewById<TextView>(R.id.today_rate)
            val layout = itemView.findViewById<ConstraintLayout>(R.id.currency_layout)
            rate.text = currency.curOfficialRate.toString()
            val contextWrapper = ContextWrapper(itemView.context)
            val sh =
                contextWrapper.getSharedPreferences("MySharedPref", AppCompatActivity.MODE_APPEND)
            val str = sh.all
//            при первом запуске добавляем USD RUB EUR
            if (str.isEmpty()) {
                val editor: SharedPreferences.Editor = sh.edit()
                editor.apply {
                    putString("USD", "USD")
                    putString("EUR", "EUR")
                    putString("RUB", "RUB")
                }.apply()
            }

            val sharedOrder = contextWrapper.getSharedPreferences(
                "MySharedPrefOrdered",
                AppCompatActivity.MODE_APPEND
            )
            compareList(currencyList,sharedOrder.all)
            currencyList.sortBy { it.prefIndex }
            compareList(currencyListTomorrow,sharedOrder.all)
            currencyListTomorrow.sortBy { it.prefIndex }


            if (str.toString().contains(currency.curAbbreviation)) {
                layout.visibility = View.VISIBLE
            } else {
                layout.visibility = View.GONE
            }
        }
        private fun compareList (currencyList: List<Rate>, sharedOrder: MutableMap<String, *>){
            currencyList.forEach { it1 ->
                sharedOrder.forEach { (s, any) ->
                    if (it1.curName == any)
                        it1.prefIndex = s.toInt()
                    viewModel.modifyList(currencyList)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.setData(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return currencyList.size
    }
}