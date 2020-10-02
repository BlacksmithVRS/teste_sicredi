package com.ortiz91.testesicredi.fragment

import android.content.DialogInterface
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.ortiz91.testesicredi.R
import com.ortiz91.testesicredi.adapter.EventAdapter
import com.ortiz91.testesicredi.adapter.PersonAdapter
import com.ortiz91.testesicredi.model.Event
import com.ortiz91.testesicredi.service.CheckinRequest
import com.ortiz91.testesicredi.service.CheckinResponse
import com.ortiz91.testesicredi.service.RetrofitInitializer
import com.ortiz91.testesicredi.utils.FormatUtils
import kotlinx.android.synthetic.main.dialog_checkin.view.*
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_details.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DetailsFragment : Fragment(), OnMapReadyCallback {
	private lateinit var eventId: String
	private lateinit var call: Call<Event>
	private var event: Event? = null
	private var isLoaderShowing = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		arguments?.let {
			eventId = it.getString("eventId")!!
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val v = inflater.inflate(R.layout.fragment_details, container, false)
		v.rv_event_details_people.adapter = EventAdapter(arrayListOf(), null)
		val dividerItemDecoration = DividerItemDecoration(
			context,
			(v.rv_event_details_people.layoutManager as LinearLayoutManager).orientation
		)
		v.rv_event_details_people.addItemDecoration(dividerItemDecoration)
		return v
	}

	override fun onResume() {
		(activity as AppCompatActivity).supportActionBar!!.title = getString(R.string.event_details)
		getEvents()
		super.onResume()
	}

	private fun getEvents() {
		if (event == null) {
			setPlaceholder(true)
			call = RetrofitInitializer().sicrediTestService().getEventDetails(eventId)
			call.enqueue(object : Callback<Event> {
				override fun onFailure(call: Call<Event>, t: Throwable) {
					setPlaceholderError()
				}

				override fun onResponse(
					call: Call<Event>,
					response: Response<Event>
				) {
					if (response.isSuccessful && response.body() != null) {
						event = response.body()
						populatePage()
						setPlaceholder(false)
					} else {
						setPlaceholderError()
					}
				}
			})
		}
	}

	private fun populatePage() {
		if (event != null) {
			val ev = event!!
			tv_event_details_title.text = ev.title
			tv_event_details_date.text = FormatUtils.formatDate(ev.date)
			tv_event_details_price.text = FormatUtils.formatCurrency(ev.price)
			if (activity != null) {
				Glide.with(activity!!).applyDefaultRequestOptions(RequestOptions().centerCrop())
					.load(ev.image).placeholder(R.drawable.ic_image_placeholder)
					.into(tv_event_details_image)
			}
			tv_event_details_description.text = ev.description
			view?.rv_event_details_people?.adapter = PersonAdapter(
				ev.people
			)
			tv_event_details_people_list_title.text =
				activity!!.getString(if (rv_event_details_people.adapter?.itemCount ?: 0 == 0) R.string.no_people else R.string.people_checked_in)
			val mapFragment = SupportMapFragment.newInstance()
			childFragmentManager.beginTransaction().replace(R.id.fl_event_details_map, mapFragment)
				.commit()
			mapFragment.getMapAsync(this)
		} else {
			setPlaceholder(true)
		}
	}

	override fun onMapReady(p0: GoogleMap?) {
		if (activity != null) {
			val ev = event!!
			p0?.let {
				it.moveCamera(
					CameraUpdateFactory.newLatLngZoom(
						LatLng(
							ev.latitude.toDouble(),
							ev.longitude.toDouble()
						), 14f
					)
				)
				it.uiSettings.setAllGesturesEnabled(false)
				it.setOnMapClickListener {
					val gmmIntentUri: Uri =
						Uri.parse(
							String.format(
								"geo:0,0?q=%s,%s(%s)",
								ev.latitude,
								ev.longitude,
								ev.title
							)
						)
					val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
					val url = "waze://?ll=" + ev.latitude + ", " + ev.longitude + "&navigate=yes"
					val intentWaze =
						Intent(Intent.ACTION_VIEW, Uri.parse(url)).setPackage("com.waze")
					val title: String = activity!!.getString(R.string.app_name)
					val chooserIntent = Intent.createChooser(mapIntent, title)
					val arr = arrayOfNulls<Intent>(1)
					arr[0] = intentWaze
					chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr)
					activity!!.startActivity(chooserIntent)
				}
			}
		}
	}

	private fun setPlaceholder(isShowing: Boolean) {
		view?.let {
			it.cl_event_details_data.visibility =
				if (isShowing && event == null) View.GONE else View.VISIBLE
			it.tv_event_details_placeholder.visibility =
				if (isShowing) View.VISIBLE else View.GONE
			it.rv_event_details_people.visibility =
				if (!isShowing && it.rv_event_details_people.adapter?.itemCount ?: 0 > 0) View.VISIBLE else View.GONE
			val hasPeople = it.rv_event_details_people.adapter?.itemCount ?: 0 == 0
			it.tv_event_details_placeholder.text =
				activity!!.getString(if (!isShowing && hasPeople) R.string.no_data else R.string.loading_data)
			val padding = it.tv_event_details_people_list_title.paddingLeft
			it.tv_event_details_people_list_title.setPadding(
				padding,
				padding,
				padding,
				if (!hasPeople) 0 else padding
			)
		}
	}

	private fun setPlaceholderError() {
		tv_event_details_placeholder.text = getString(R.string.error_loading_data)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.event_details_menu, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (isLoaderShowing) return false
		event?.let {
			when (item.itemId) {
				R.id.menu_event_details_share -> {
					val sendIntent: Intent = Intent().apply {
						action = Intent.ACTION_SEND
						val sb = StringBuilder()
						sb.append(
							String.format(
								getString(R.string.string_format_check_event),
								it.title
							)
						)
						sb.append(
							String.format(
								getString(R.string.date),
								FormatUtils.formatDate(it.date)
							)
						)
						sb.append(
							String.format(
								getString(R.string.price),
								FormatUtils.formatCurrency(it.price)
							)
						)
						val gc = Geocoder(
							activity,
							Locale.getDefault()
						).getFromLocation(it.latitude.toDouble(), it.longitude.toDouble(), 1)
						if (gc.size > 0) {
							sb.append("\n").append(
								String.format(
									getString(R.string.address),
									gc[0].getAddressLine(0)
								)
							)
						}
						/* 	Here I was not sure about whether to include the description.
							"Sharing" in this exercise could mean different things, such
							as just sharing the above generated text, or actually sharing
							a link to the event inside this app, which people would be able
							to open by downloading the app (link provided in the message) and
							clicking the event link, which would be intercepted by a broadcast
							receiver. */
						putExtra(Intent.EXTRA_TEXT, sb.toString())
						type = "text/plain"
					}
					val shareIntent = Intent.createChooser(sendIntent, null)
					startActivity(shareIntent)
					return true
				}
				R.id.menu_event_details_checkin -> {
					buildCheckinDialog()
					return true
				}
				else -> return false
			}
		}
		return false
	}

	private fun buildCheckinDialog() {
		val b = AlertDialog.Builder(activity!!)
		b.setTitle(R.string.checkin)
		val v = activity!!.layoutInflater.inflate(R.layout.dialog_checkin, null, false)
		b.setView(v)
		b.setNegativeButton(
			R.string.cancel
		) { dialog, _ ->
			dialog.dismiss()
		}
		val listener = DialogInterface.OnClickListener { dialog, _ ->
			val name = v.et_checkin_dialog_name.text.toString().trim()
			val email = v.et_checkin_dialog_email.text.toString().trim()
			if (name.isEmpty() || email.isEmpty()) {
				val b2 = AlertDialog.Builder(activity!!)
				b2.setMessage(R.string.dialog_checkin_empty_fields)
				b2.setPositiveButton(R.string.ok) { dialog2, _ ->
					buildCheckinDialog()
					dialog2.dismiss()
				}
				b2.create().show()
			} else {
				setLoader(true)
				RetrofitInitializer().sicrediTestService()
					.checkin(CheckinRequest(event!!.id, name, email))
					.enqueue(object : Callback<CheckinResponse> {
						override fun onFailure(call: Call<CheckinResponse>, t: Throwable) {
							val b2 = AlertDialog.Builder(activity!!)
							b2.setMessage(R.string.dialog_checkin_error)
							b2.setPositiveButton(R.string.ok) { dialog2, _ ->
								dialog2.dismiss()
							}
							b2.create().show()
							setLoader(false)
						}

						override fun onResponse(
							call: Call<CheckinResponse>,
							response: Response<CheckinResponse>
						) {
							val b2 = AlertDialog.Builder(activity!!)
							b2.setMessage(R.string.dialog_checkin_success)
							b2.setPositiveButton(R.string.ok) { dialog2, _ ->
								dialog2.dismiss()
							}
							b2.create().show()
							setLoader(false)
						}
					})
			}
			dialog.dismiss()
		}
		b.setPositiveButton(R.string.confirm, listener)
		val d = b.create()
		v.et_checkin_dialog_email.setOnEditorActionListener(object : TextView.OnEditorActionListener{
			override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
				if (actionId == EditorInfo.IME_ACTION_GO) {
					listener.onClick(d, 1)
				}
				return false
			}
		})
		d.show()
	}

	private fun setLoader(isShowing: Boolean) {
		isLoaderShowing = isShowing
		view?.let {
			ll_loader.visibility = if (isShowing) View.VISIBLE else View.GONE
		}
	}

	companion object {
		fun newInstance(eventId: String) =
			DetailsFragment().apply {
				arguments = Bundle().apply {
					putString("eventId", eventId)
				}
			}
	}
}