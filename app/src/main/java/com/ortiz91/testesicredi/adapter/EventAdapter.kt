package com.ortiz91.testesicredi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ortiz91.testesicredi.model.Event
import com.ortiz91.testesicredi.R
import com.ortiz91.testesicredi.utils.FormatUtils
import kotlinx.android.synthetic.main.it_event.view.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*


class EventAdapter(var items: ArrayList<Event>, var onEventClickedListener: OnEventClickedListener?) :
	RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

	lateinit var context: Context

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
		context = parent.context
		val v = LayoutInflater.from(parent.context).inflate(R.layout.it_event, parent, false)
		return EventViewHolder(v)
	}

	override fun getItemCount(): Int {
		return items.size
	}

	override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
		val pos = holder.adapterPosition
		val item = items[pos]
		holder.title.text = item.title
		holder.date.text = FormatUtils.formatDate(item.date)
		holder.price.text = FormatUtils.formatCurrency(item.price)
		Glide.with(context).applyDefaultRequestOptions(RequestOptions().centerCrop())
			.load(item.image).placeholder(R.drawable.ic_image_placeholder).into(holder.image)
		holder.itemView.setOnClickListener{
			onEventClickedListener?.onEventClicked(item)
		}
	}

	class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var title: AppCompatTextView = itemView.tv_it_event_title
		var date: AppCompatTextView = itemView.tv_it_event_date
		var price: AppCompatTextView = itemView.tv_it_event_price
		var image: AppCompatImageView = itemView.iv_it_event_image
	}

	interface OnEventClickedListener {
		fun onEventClicked(event: Event)
	}

}