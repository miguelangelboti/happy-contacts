package com.miguelangelboti.happycontacts.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.miguelangelboti.happycontacts.R

@Composable
fun NavigationBar(
    navHostController: NavHostController,
    items: List<Screen>,
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    androidx.compose.material3.NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                icon = {
                    Icon(
                        painter = painterResource(item.drawableResId),
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(item.titleResId)) },
                onClick = {
                    navHostController.navigate(route = item.route) {
                        // Pop up to the start destination of the graph to avoid building up a large
                        // stack of destinations on the back stack as users select items.
                        navHostController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when re-selecting the same item.
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item.
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class Screen(
    val route: String,
    @DrawableRes val drawableResId: Int,
    @StringRes val titleResId: Int,
) {
    object BirthdaysScreen : Screen("Birthdays", R.drawable.ic_celebration, R.string.birthdays)
    object ContactsScreen : Screen("Contacts", R.drawable.ic_contacts, R.string.contacts)
}
