package com.ortiz91

import android.app.Application
import com.ortiz91.testesicredi.utils.FormatUtils

class SicrediTestApplication : Application() {

	override fun onCreate() {
		super.onCreate()
		FormatUtils.context = applicationContext
	}

}