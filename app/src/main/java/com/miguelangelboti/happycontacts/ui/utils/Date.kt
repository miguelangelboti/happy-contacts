package com.miguelangelboti.happycontacts.ui.utils

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.monthAsString(capitalize: Boolean = false): String {
    return DateFormat.format("MMMM", this).toString().run {
        if (capitalize) replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } else this
    }
}

fun Date.dayAsInt(): Int {
    return Calendar.getInstance().apply { time = this@dayAsInt }.get(Calendar.DAY_OF_MONTH)
}
