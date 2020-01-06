package com.example.ama_tracking_app.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ama_tracking_app.R
import com.example.geofence.model.GeoLog
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

class RecyclerViewAdapter(private var dataset: List<GeoLog>) :
    RecyclerView.Adapter<RecyclerViewAdapter.GeoLogViewHolder>() {
    class GeoLogViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    val datePattern = "yyy.MM.dd hh:mm"
    val simpleDateFormat = SimpleDateFormat(datePattern)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeoLogViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(
            R.layout.geolog_view,
            parent,
            false
        ) as TextView
        return GeoLogViewHolder(textView)
    }

    override fun onBindViewHolder(holder: GeoLogViewHolder, position: Int) {
        val dateString = simpleDateFormat.format(dataset[position].date)
        holder.textView.text = "${dateString} ${dataset[position].content}"
    }

    fun setData(newData: List<GeoLog>) {
        this.dataset = newData
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataset.size
}