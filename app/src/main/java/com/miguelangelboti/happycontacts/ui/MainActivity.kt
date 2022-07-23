package com.miguelangelboti.happycontacts.ui

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.miguelangelboti.happycontacts.R.string
import com.miguelangelboti.happycontacts.data.model.Contact
import com.miguelangelboti.happycontacts.ui.components.ContactList
import com.miguelangelboti.happycontacts.ui.components.NavigationBar
import com.miguelangelboti.happycontacts.ui.components.Screen
import com.miguelangelboti.happycontacts.ui.components.Screen.BirthdaysScreen
import com.miguelangelboti.happycontacts.ui.components.Screen.ContactsScreen
import com.miguelangelboti.happycontacts.ui.theme.HappyContactsTheme
import com.miguelangelboti.happycontacts.ui.utils.initial
import com.miguelangelboti.happycontacts.ui.utils.monthAsString
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory(applicationContext as Application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content(
                contactsFlow = viewModel.contacts,
                screens = listOf(BirthdaysScreen, ContactsScreen),
            )
        }
    }

    @Composable
    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    private fun Content(
        contactsFlow: Flow<List<Contact>>,
        screens: List<Screen>,
    ) {
        var selectedContact by remember { mutableStateOf<Contact?>(null) }
        val navHostController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val onContactSelected: (Contact) -> Unit = {
            selectedContact = it
            coroutineScope.launch { modalBottomSheetState.show() }
        }
        LaunchedEffect(modalBottomSheetState.currentValue) {
            if (modalBottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
                selectedContact = null
            }
        }

        HappyContactsTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { CenterAlignedTopAppBar(title = { Text(text = stringResource(id = string.app_name)) }) },
                    bottomBar = {
                        NavigationBar(
                            navHostController = navHostController,
                            items = screens,
                        )
                    },
                ) { innerPadding ->
                    val contactsPermissionState = rememberPermissionState(android.Manifest.permission.READ_CONTACTS)
                    if (contactsPermissionState.status.isGranted) {
                        Screens(innerPadding, navHostController, contactsFlow, onContactSelected)
                    } else {
                        RequestPermissionScreen(innerPadding, contactsPermissionState)
                    }
                }
                selectedContact?.let {
                    ModalBottomSheet(
                        state = modalBottomSheetState,
                        contact = it,
                    )
                }
            }
        }
    }

    @Composable
    fun Screens(
        innerPadding: PaddingValues,
        navHostController: NavHostController,
        contactsFlow: Flow<List<Contact>>,
        onContactSelected: (Contact) -> Unit
    ) {
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navHostController,
            startDestination = "Birthdays"
        ) {
            composable(route = BirthdaysScreen.route) {
                BirthdaysScreen(
                    contactsFlow = contactsFlow,
                    onContactClickListener = onContactSelected,
                )
            }
            composable(route = ContactsScreen.route) {
                ContactsScreen(
                    contactsFlow = contactsFlow,
                    onContactClickListener = onContactSelected,
                )
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterialApi::class)
    fun ModalBottomSheet(
        state: ModalBottomSheetState,
        contact: Contact
    ) {
        var message by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()
        val clipboardManager = LocalClipboardManager.current
        val shimmerInstance = rememberShimmer(ShimmerBounds.View)
        val generateMessage = {
            coroutineScope.launch {
                message = ""
                message = viewModel.getMessageFor(contact) ?: ""
            }
        }
        ModalBottomSheetLayout(
            content = { },
            sheetState = state,
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
            sheetContent = {
                LaunchedEffect(state) {
                    generateMessage()
                }
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                        .align(CenterHorizontally)
                        .width(30.dp)
                        .height(6.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.defaultMinSize(minHeight = 200.dp)) {
                        if (message.isEmpty()) {
                            Column(modifier = Modifier.shimmer(shimmerInstance)) {
                                ShimmerBox()
                                ShimmerBox(0.9f)
                                ShimmerBox(0.9f)
                                ShimmerBox(0.7f)
                            }
                        } else {
                            Text(
                                color = MaterialTheme.colorScheme.onSurface,
                                text = message
                            )
                        }
                    }
                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { generateMessage() }
                    ) {
                        Text(stringResource(id = string.new_message))
                    }
                    Button(
                        modifier = Modifier.align(Alignment.End),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(message))
                            coroutineScope.launch { state.hide() }
                            Toast.makeText(this@MainActivity, string.message_copied, Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(stringResource(id = string.copy))
                    }
                }
            }
        )
    }
}

@Composable
fun BirthdaysScreen(
    contactsFlow: Flow<List<Contact>>,
    onContactClickListener: (Contact) -> Unit,
) {
    ContactList(
        contactsFlow = contactsFlow,
        keySelector = { it.birthday.monthAsString(capitalize = true) },
        onItemClickListener = onContactClickListener,
    )
}

@Composable
fun ShimmerBox(widthFraction: Float = 1f) {
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(widthFraction)
            .height(18.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
    )
}

@Composable
fun ContactsScreen(
    contactsFlow: Flow<List<Contact>>,
    onContactClickListener: (Contact) -> Unit,
) {
    ContactList(
        contactsFlow = contactsFlow,
        keySelector = { it.name.initial() },
        onItemClickListener = onContactClickListener,
    )
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun RequestPermissionScreen(
    innerPadding: PaddingValues,
    contactsPermissionState: PermissionState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "The contacts permission is important for this app. Please grant the permission.",
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { contactsPermissionState.launchPermissionRequest() }) {
            Text("Request permission")
        }
    }
}
