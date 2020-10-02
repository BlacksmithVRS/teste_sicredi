package com.ortiz91.testesicredi.service

import com.ortiz91.testesicredi.model.Event
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SicrediTestService {

	@GET("events")
	fun getEvents(): Call<ArrayList<Event>>

	@GET("events/{id}")
	fun getEventDetails(@Path("id") id: String): Call<Event>

	@POST("checkin")
	fun checkin(@Body request: CheckinRequest): Call<CheckinResponse>

}