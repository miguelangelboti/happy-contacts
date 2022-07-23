package com.miguelangelboti.happycontacts.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.miguelangelboti.happycontacts.data.model.Contact
import com.miguelangelboti.happycontacts.ui.theme.HappyContactsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Composable
fun ContactList(
    contactsFlow: Flow<List<Contact>>,
    keySelector: (Contact) -> String,
    onItemClickListener: (Contact) -> Unit,
) {
    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(2000)
            refreshing = false
        }
    }
    val contacts by contactsFlow.collectAsState(initial = emptyList())
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = { refreshing = true },
    ) {
        InternalContactList(contacts, keySelector, onItemClickListener)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun InternalContactList(
    contacts: List<Contact>,
    keySelector: (Contact) -> String,
    onClickListener: (Contact) -> Unit,
) {
    LazyColumn {
        contacts.groupBy { keySelector(it) }
            .forEach {
                val (initial, contactsForInitial) = it
                stickyHeader {
                    CharacterHeader(initial)
                }
                items(
                    items = contactsForInitial,
                    key = { contact -> contact.name },
                ) { contact ->
                    ContactItem(
                        modifier = Modifier.clickable { onClickListener(contact) },
                        contact
                    )
                    Divider(modifier = Modifier.alpha(0.2f), color = Color.Black)
                }
            }
    }
}

@Composable
private fun CharacterHeader(initial: String) {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .padding(horizontal = 24.dp, vertical = 12.dp),

            text = initial
        )
    }
}

@Composable
@Preview(showBackground = true)
fun Preview() {
    HappyContactsTheme {
        InternalContactList(
            contacts = listOf(
                Contact("A", Date(), null),
                Contact("B", Date(), null),
                Contact("C", Date(), null),
            ),
            keySelector = { it.name },
            onClickListener = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewDark() {
    HappyContactsTheme(isDarkTheme = true) {
        InternalContactList(
            contacts = listOf(
                Contact("A", Date(), null),
                Contact("B", Date(), null),
                Contact("C", Date(), null),
            ),
            keySelector = { it.name },
            onClickListener = {}
        )
    }
}
