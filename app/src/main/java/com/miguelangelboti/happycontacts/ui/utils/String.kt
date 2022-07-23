package com.miguelangelboti.happycontacts.ui.utils

import java.text.Normalizer

fun String.initial(): String {
    val initial = firstOrNull()?.toString() ?: ""
    return Normalizer.normalize(initial, Normalizer.Form.NFD).replace("\\p{Mn}+".toRegex(), "")
}

fun String.initials(number: Int? = null): String {
    return split(" ").map { it.filter(Char::isLetter) }
        .filter(String::isNotEmpty)
        .map(String::first).joinToString("")
        .uppercase()
        .also { if (number != null) it.truncate(number) }
}

fun String.firstWord(): String {
    return split(" ").firstOrNull() ?: ""
}

fun String.truncate(number: Int): String {
    return if (length > number) substring(0, number) else this
}
