package com.miguelangelboti.happycontacts.data

import android.content.ContentResolver
import android.provider.ContactsContract.CommonDataKinds.Event
import android.provider.ContactsContract.CommonDataKinds.Event.TYPE
import android.provider.ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.Data.MIMETYPE
import com.miguelangelboti.happycontacts.data.model.Contact
import java.text.SimpleDateFormat
import java.util.Date

class ContactsDataSource(private val contentResolver: ContentResolver) {

    private val format = SimpleDateFormat("--MM-dd")

    fun fetchContacts(): List<Contact> {
        return mutableListOf<Contact>().apply {
            contentResolver.query(
                Data.CONTENT_URI,
                arrayOf(
                    Phone.DISPLAY_NAME,
                    Event.START_DATE,
                    Phone.PHOTO_URI
                ),
                "$MIMETYPE = ? AND $TYPE = $TYPE_BIRTHDAY",
                arrayOf(Event.CONTENT_ITEM_TYPE),
                Phone.DISPLAY_NAME
            )?.run {
                moveToFirst()
                while (!isAfterLast) {
                    val birthday = parseBirthday(getString(1))
                    if (birthday != null) {
                        add(
                            Contact(
                                name = getString(0),
                                birthday = birthday,
                                imageUrl = getString(2)
                            )
                        )
                    }
                    moveToNext()
                }
                close()
            }
        }
    }

    private fun parseBirthday(string: String): Date? {
        return try {
            format.parse(string)
        } catch (ignored: Exception) {
            null
        }
    }
}

//fun String.toContactImageUri() = Uri.withAppendedPath(
//    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, toLong()),
//    ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
//).toString()
