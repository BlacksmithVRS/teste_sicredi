package com.ortiz91.testesicredi.utils

import android.content.Context
import com.ortiz91.testesicredi.R
import java.lang.Error
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class FormatUtils {

	companion object {

		lateinit var context: Context

		fun formatDate(long: Long): String {
			try {
				return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
					Date(long)
				)
			} catch (e: Error) {
				return context.getString(R.string.data_unavailable)
			}
		}

		fun formatCurrency(value: Double): String {
			/*	As no currency info was provided with the test, BRL was assumed.
			The number format is still based on the default locale, but to force it to
			BR viewers, Locale("pt", "BR") could be used instead of Locale.getDefault()*/
			try {
				return DecimalFormat(
					"R$#,###.##",
					DecimalFormatSymbols.getInstance(Locale.getDefault())
				).format(value)
			} catch (e: Error) {
				return context.getString(R.string.data_unavailable)
			}
		}

	}

}