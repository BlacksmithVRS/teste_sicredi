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
import com.ortiz91.testesicredi.R
import com.ortiz91.testesicredi.model.Person
import kotlinx.android.synthetic.main.it_person.view.*
import java.util.*


class PersonAdapter(var people: ArrayList<Person>) :
	RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

	lateinit var context: Context

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
		context = parent.context
		val v = LayoutInflater.from(parent.context).inflate(R.layout.it_person, parent, false)
		return PersonViewHolder(v)
	}

	override fun getItemCount(): Int {
		return people.size
	}

	override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
		val pos = holder.adapterPosition
		val person = people[pos]
		Glide.with(context).applyDefaultRequestOptions(RequestOptions().circleCrop())
			.load(person.picture).placeholder(R.drawable.ic_image_placeholder).into(holder.image)
		holder.name.text = person.name
	}

	class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var image: AppCompatImageView = itemView.iv_it_event_details_person_image
		var name: AppCompatTextView = itemView.tv_it_event_details_person_name
	}

}