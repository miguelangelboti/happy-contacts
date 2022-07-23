package com.miguelangelboti.happycontacts.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.miguelangelboti.happycontacts.data.ContactsDataSource
import com.miguelangelboti.happycontacts.data.ContactsRepository
import kotlinx.coroutines.Dispatchers

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            MainViewModel::class.java -> {
                MainViewModel(
                    contactsRepository = ContactsRepository(
                        dataSource = ContactsDataSource(application.contentResolver),
                        dispatcher = Dispatchers.IO
                    )
                )
            }
            else -> throw IllegalArgumentException("Unknown class $modelClass")
        } as T
    }
}
