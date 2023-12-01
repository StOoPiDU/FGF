package com.cedric.fgf

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



object Favourites {
//    @Composable
//    fun FavouriteScreen() {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.White),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(text = "No Favourited Items") // Add If/Switch case for this
//        }

    // Defining the structure of a post item
//    data class FavouriteItem(
//        val title: String,
//        val author: String,
//        val url: String,
//        val id: String,
////        val link_flair_css_class: String,
//        val thumbnail: String
//    )

    // Shortening the length of a title in list view
    fun shortenContent(content: String): String {
        return if (content.length > 50) {
            content.substring(0, 50) + "..."
        } else {
            content
        }
    }

    @Composable
    fun DisplayFavouritesView() {
        val context = LocalContext.current
        var itemList by remember { mutableStateOf(emptyList<FavouriteItem>()) }
        var selectedItem by remember { mutableStateOf<FavouriteItem?>(null) }

        val coroutine = rememberCoroutineScope()

        val db = FavouritesDatabase.getInstance(LocalContext.current)

        LaunchedEffect(key1 = true) {
            coroutine.launch {
                withContext(Dispatchers.IO) {
                    itemList = db.favouriteItemDao().getAll()
                }
            }
        }

        LazyColumn {
            items(itemList) { item ->
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable{selectedItem = item}) {
                    Image(
                        modifier = Modifier
                            .size(100.dp)
                            .background(color = Color.Blue),
//                        painter = painterResource(id = R.drawable.fgf_logo_whiteout),
                        painter = rememberAsyncImagePainter(model = item.thumbnail),
                        contentDescription = item.title + " thumbnail",
                        contentScale = ContentScale.FillHeight,)
                    Column(modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
//                modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = Favourites.shortenContent(item.title), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                        Text(text = "/u/" + item.author)
                    }
                }
                Divider()
            }
        }
        selectedItem?.let { item ->
            ExpandedItemView(item = item, onClose = { selectedItem = null })
        }
    }

    // Expandable list view when clicking on a post item
    @Composable
    fun ExpandedItemView(item: FavouriteItem, onClose: () -> Unit) {
        val context = LocalContext.current
        val db = FavouritesDatabase.getInstance(LocalContext.current)
        val coroutine = rememberCoroutineScope()

        // Try to add BackHandler to this, currently not working
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
            // ^ can try something like 0.9f for an argument, this is nice though
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                if (!item.thumbnail.contains(item.id)) { // Poor attempt at adding logic for missing thumbnail imagery
                Image(
                    painter = rememberAsyncImagePainter(model = item.thumbnail).also {
                        Log.d("Painter", "$it")
                    },
                    contentDescription = item.title + " thumbnail",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f),
                    contentScale = ContentScale.Fit
//                        .background(color = Color.Blue)
                )
//                }
                Text(
                    text = item.title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(text = "Posted by /u/" + "${item.author}", fontStyle = FontStyle.Italic)

                Row() {
                    Button(
                        onClick = {val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://redd.it/" + item.id))
                            context.startActivity(intent)}, modifier = Modifier.padding(8.dp)
                    ) { Text(text = "Reddit Link") }
                    Button(
                        onClick = {val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                            context.startActivity(intent)}, modifier = Modifier.padding(8.dp)
                    ) { Text(text = "Direct Link") }
                }

                // Saving a post item to the local favourites database
                var isItemInDb by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    val existingItem = db.favouriteItemDao().getItemById(item.id)
                    isItemInDb = existingItem != null
                }

                IconButton(
                    onClick = {
                        coroutine.launch {
                            withContext(Dispatchers.IO) {
                                val existingItem = db.favouriteItemDao().getItemById(item.id)
                                if (existingItem != null) {
                                    db.favouriteItemDao().delete(existingItem)
                                    isItemInDb = false
                                } else {
                                    db.favouriteItemDao().upsert(FavouriteItem(item.id, item.title, item.author, item.thumbnail, item.url))
                                    isItemInDb = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
//                        imageVector = if (isItemInDb) Icons.Outlined.Favorite else Icons.Default.Favorite,
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = if (isItemInDb) Color.Red else Color.Black
                    )
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) { Text(text = "Close") }
            }
        }
    }
}