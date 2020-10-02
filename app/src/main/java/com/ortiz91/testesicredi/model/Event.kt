package com.ortiz91.testesicredi.model

class Event(
	var title: String = "",
	var date: Long,
	var description: String,
	var image: String,
	var latitude: String,
	var longitude: String,
	var price: Double,
	var id: String,
	var people: ArrayList<Person>
)