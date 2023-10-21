package com.cedric.fgf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

object Favourites {
    // Build/rebuild ListView to include only things that have
    // been "favourited" from the list view.
    // Store this via system somehow and let it function as the
    // ListView does.
    // The ListView "ExpandedItemView" should include a heart
    // button as a means of adding the item here. OnClick or
    // something along those lines.
    @Composable
    fun FavouriteScreen() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Favourite")
        }
    }
}