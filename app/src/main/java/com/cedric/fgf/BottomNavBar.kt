package com.cedric.fgf

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavBar(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavBar(
        route = "LISTVIEW",
        title = "List",
        icon = Icons.Default.Home
    )

    object Favourites : BottomNavBar(
        route = "FAVOURITES",
        title = "Favourites",
        icon = Icons.Default.Favorite // Use this icon exactly for the "ExpandedListView"
    )

    object Settings : BottomNavBar(
        route = "SETTINGS",
        title = "Settings",
        icon = Icons.Default.Settings
//        icon = Icons.Default.Info
    )

    companion object {
        fun values() = listOf(Home, Favourites, Settings)
    }
}