package com.cedric.fgf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

object Settings {
    // This might be better called an "About" page.
    // Use this object to describe what FGF is,
    // explain how to use filters (once implemented),
    // toggle push notifications (once implemented),
    // and possibly just link to the FGF links ala linktree.
    @Composable
    fun SettingsScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Settings/About")
        }
    }
}