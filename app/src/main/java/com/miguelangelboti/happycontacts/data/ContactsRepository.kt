package com.miguelangelboti.happycontacts.data

import com.miguelangelboti.happycontacts.BuildConfig
import com.miguelangelboti.happycontacts.data.model.Contact
import com.miguelangelboti.happycontacts.ui.utils.firstWord
import com.theokanning.openai.OpenAiService
import com.theokanning.openai.completion.CompletionRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ContactsRepository(
    private val dataSource: ContactsDataSource,
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun fetchContacts(): List<Contact> {
        return withContext(dispatcher) {
            dataSource.fetchContacts()
        }
    }

    suspend fun getMessageFor(contact: Contact): String? {
        return withContext(dispatcher) {
            OpenAiService(BuildConfig.GPT3_API_KEY)
                .createCompletion(
                    "text-davinci-001",
                    CompletionRequest.builder()
                        .prompt("Mensaje de cumpleaños en español para ${contact.name.firstWord()} con emojis:")
                        .maxTokens(400)
                        .build()
                )
                .choices
                .firstOrNull()
                ?.text
                ?.trim()
        }
    }
}
