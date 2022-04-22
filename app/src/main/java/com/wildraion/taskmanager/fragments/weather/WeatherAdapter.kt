package com.wildraion.taskmanager.fragments.weather

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wildraion.taskmanager.R
import com.wildraion.taskmanager.fragments.list.ListAdapter
import com.wildraion.taskmanager.model.Weather
import com.wildraion.taskmanager.util.Utils

class WeatherAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var weatherList: List<Weather> = emptyList()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.MyViewHolder {
        return ListAdapter.MyViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_layout, parent, false))
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ListAdapter.MyViewHolder, position: Int) {
        val currentItem = weatherList[position]

        holder.itemView.findViewById<TextView>(R.id.w_time_tv).text = currentItem.time

        val icon = holder.itemView.findViewById<ImageView>(R.id.w_icon_iv)
        icon.setImageResource(Utils.getImageId(holder.itemView.context, currentItem.icon))

        holder.itemView.findViewById<TextView>(R.id.w_temp_tv).text = currentItem.temp
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(weatherList: List<Weather>, ){
        this.weatherList = weatherList
        notifyDataSetChanged()
    }
}