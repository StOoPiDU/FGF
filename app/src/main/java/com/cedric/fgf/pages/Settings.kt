package com.cedric.fgf.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cedric.fgf.database.FavouritesDatabase
import com.cedric.fgf.misc.NotificationHandler
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
            Text("Welcome to the FGF Mobile Application!",
                textAlign = TextAlign.Center,)

            // Calling the Notification buttons and the logic from it
            NotificationHandler().NotificationContent()

            // Add confirmation to this
            Button(onClick = {
                coroutine.launch {
                    withContext(Dispatchers.IO) {
                        db.favouriteItemDao().deleteAll()
                    }
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White)
            ) {
                Text("Delete All Favourites")
            }
        }
    }
}