package com.cedric.fgf.pages

import android.content.Intent
import android.net.Uri
import android.os.Build
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
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.cedric.fgf.R
import com.cedric.fgf.database.FavouriteItem
import com.cedric.fgf.database.FavouritesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// Favourite view object that is called within MainActivity
object Favourites {
    // Shortening the length of a title in list view
    fun shortenContent(content: String): String {
        return if (content.length > 50) {
            content.substring(0, 50) + "..."
        } else {
            content
        }
    }

    // This function checks for if a thumbnail exists for a post and return the result.
    // It will return either the thumbnail or the default FGF logo to display.
    @Composable
    fun getImage(item: FavouriteItem): Painter {
        return if (item.thumbnail != "default" && item.thumbnail != "self" && !item.thumbnail.isNullOrEmpty()) {
            rememberAsyncImagePainter(model = item.thumbnail)
        } else {
            painterResource(id = R.drawable.fgf_logo_whiteout)
        }
    }

    // Separated code to run a check for an image and save a boolean due
    // assumed bug in android studio. Just nice to isolate too I guess.
    @Composable
    fun hasImage(item: FavouriteItem): Boolean {
        return item.thumbnail == "default" || item.thumbnail == "self" || item.thumbnail.isNullOrEmpty()
    }

    // List view of favourited items
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

        if (itemList.isNotEmpty()) {
            LazyColumn {
                items(itemList) { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedItem = item }) {
                        Image(
                            modifier = Modifier
                                .size(100.dp)
                                .background(color = MaterialTheme.colorScheme.primary),
                            painter = getImage(item),
                            contentDescription = item.title + " thumbnail",
                            contentScale = ContentScale.FillHeight,
                        )
                        Column(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth(),
                            //                modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = shortenContent(item.title),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(text = "/u/" + item.author, color = MaterialTheme.colorScheme.onSurface)
                            if (Build.VERSION.SDK_INT >= 26) {
                                val instant = Instant.ofEpochSecond(item.created_utc.toLong())
                                val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a")
                                val localTime = localDateTime.format(formatter)
                                Text(text = "Posted $localTime",
                                    color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                    Divider()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You currently have not favourited anything.",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "From the home page, click on an item then click on the heart icon to save a post for later!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.onSurface
                )}
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
                .fillMaxHeight(),
            backgroundColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!hasImage(item)) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f),
//                            .background(color = Color.Blue),
                        painter = getImage(item),
                        contentDescription = item.title + " thumbnail",
                        contentScale = ContentScale.Fit)
                }
                Text(
                    text = item.title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(text = "Posted by /u/" + "${item.author}", fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurface)

                if (Build.VERSION.SDK_INT >= 26) {
                    val instant = Instant.ofEpochSecond(item.created_utc.toLong())
                    val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a")
                    val localTime = localDateTime.format(formatter)
                    Text(
                        text = "Posted $localTime",
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }

                Row() {
                    Button(
                        onClick = {val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://redd.it/" + item.id))
                            context.startActivity(intent)},
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                    ) { Text(text = "Reddit Link", color = MaterialTheme.colorScheme.onPrimary) }
                    Button(
                        onClick = {val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                            context.startActivity(intent)},
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                    ) { Text(text = "Direct Link", color = MaterialTheme.colorScheme.onPrimary) }
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
                                    db.favouriteItemDao().upsert(FavouriteItem(item.id, item.title, item.author, item.thumbnail, item.url, item.created_utc))
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
                        tint = if (isItemInDb) Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) { Text(text = "Close", color = MaterialTheme.colorScheme.onPrimary) }
            }
        }
    }
}