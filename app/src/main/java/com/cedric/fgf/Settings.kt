package com.cedric.fgf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Settings {
    // This might be better called an "About" page.
    // Use this object to describe what FGF is,
    // explain how to use filters (once implemented),
    // toggle push notifications (once implemented),
    // and possibly just link to the FGF links ala linktree.
    @Composable
    fun SettingsScreen() {
        val context = LocalContext.current
        val coroutine = rememberCoroutineScope()
        val db = FavouritesDatabase.getInstance(LocalContext.current)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Welcome to the FGF Mobile Application!")

            // Add confirmation to this
            Button(onClick = {
                coroutine.launch {
                    withContext(Dispatchers.IO) {
                        db.favouriteItemDao().deleteAll()
                    }
                }
            }) {
                Text("Delete All Favourites")
            }
        }
    }
}