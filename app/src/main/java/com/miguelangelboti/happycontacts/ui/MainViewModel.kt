package com.miguelangelboti.happycontacts.ui

import androidx.lifecycle.ViewModel
import com.miguelangelboti.happycontacts.data.ContactsRepository
import com.miguelangelboti.happycontacts.data.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainViewModel(private val contactsRepository: ContactsRepository) : ViewModel() {

    var contacts: Flow<List<Contact>> = flow {
        emit(contactsRepository.fetchContacts())
    }

    suspend fun getMessageFor(contact: Contact): String? {
        return contactsRepository.getMessageFor(contact)
    }
}
