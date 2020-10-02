package com.ortiz91.testesicredi.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ortiz91.testesicredi.R
import com.ortiz91.testesicredi.adapter.EventAdapter
import com.ortiz91.testesicredi.model.Event
import com.ortiz91.testesicredi.service.RetrofitInitializer
import kotlinx.android.synthetic.main.fragment_home_list.*
import kotlinx.android.synthetic.main.fragment_home_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeListFragment : Fragment() {

	private lateinit var call: Call<ArrayList<Event>>
	private var items: ArrayList<Event>? = null
	private var isRefreshing = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val v = inflater.inflate(R.layout.fragment_home_list, container, false)
		v.rv_home_events.adapter = EventAdapter(arrayListOf(), null)
		val dividerItemDecoration = DividerItemDecoration(
			context,
			(v.rv_home_events.layoutManager as LinearLayoutManager).orientation
		)
		v.rv_home_events.addItemDecoration(dividerItemDecoration)
		return v
	}

	override fun onResume() {
		(activity as AppCompatActivity).supportActionBar!!.title = getString(R.string.event_list)
		if (items == null || items!!.size == 0) {
			getEvents()
		} else {
			populateList()
		}
		super.onResume()
	}

	private fun getEvents() {
		setPlaceholder(true)
		isRefreshing = true
		call = RetrofitInitializer().sicrediTestService().getEvents()
		call.enqueue(object : Callback<ArrayList<Event>> {
			override fun onFailure(call: Call<ArrayList<Event>>, t: Throwable) {
				isRefreshing = false
				setPlaceholderError()
			}

			override fun onResponse(
				call: Call<ArrayList<Event>>,
				response: Response<ArrayList<Event>>
			) {
				if (response.isSuccessful && response.body() != null) {
					items = response.body()
					populateList()
					setPlaceholder(false)
				} else {
					setPlaceholderError()
				}
				isRefreshing = false
			}
		})
	}

	private fun populateList() {
		view?.rv_home_events?.adapter = EventAdapter(
			items ?: arrayListOf(),
			object : EventAdapter.OnEventClickedListener {
				override fun onEventClicked(event: Event) {
					activity!!.supportFragmentManager.beginTransaction()
						.setCustomAnimations(
							R.anim.slide_in_right,
							android.R.anim.fade_out,
							android.R.anim.fade_in,
							android.R.anim.slide_out_right
						).replace(R.id.content, DetailsFragment.newInstance(event.id))
						.addToBackStack("details").commit()
				}
			})
	}

	private fun setPlaceholder(isShowing: Boolean) {
		view?.let {
			it.rv_home_events.visibility =
				if (isShowing && it.rv_home_events.adapter?.itemCount ?: 0 > 0) View.GONE else View.VISIBLE
			it.tv_home_placeholder.visibility =
				if (isShowing || it.rv_home_events.adapter?.itemCount ?: 0 == 0) View.VISIBLE else View.GONE
			it.tv_home_placeholder.text =
				activity!!.getString(if (!isShowing && it.rv_home_events.adapter?.itemCount ?: 0 == 0) R.string.no_data else R.string.loading_data)
		}
	}

	private fun setPlaceholderError() {
		tv_home_placeholder.text = getString(R.string.error_loading_data)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.home_menu, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == R.id.menu_home_refresh && !isRefreshing) {
			getEvents()
			return true
		}
		return false
	}

	companion object {
		@JvmStatic
		fun newInstance() =
			HomeListFragment().apply {
				arguments = Bundle().apply {
				}
			}
	}
}
