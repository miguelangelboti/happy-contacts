package com.miguelangelboti.happycontacts.data.model

import java.util.Date

data class Contact(
    val name: String,
    val birthday: Date,
    val imageUrl: String?
)
