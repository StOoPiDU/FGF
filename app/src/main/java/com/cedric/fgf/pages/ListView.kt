package com.cedric.fgf.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.rememberAsyncImagePainter
import com.cedric.fgf.R
import com.cedric.fgf.database.FavouriteItem
import com.cedric.fgf.database.FavouritesDatabase
import com.cedric.fgf.database.LatestDatabase
import com.cedric.fgf.database.LatestItem
import com.cedric.fgf.misc.FGFData
import com.cedric.fgf.misc.RetroFitAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


// List view object that is called within MainActivity
object ListView {

    // Defining the structure of a post item
    data class PostItem(
        val title: String,
        val author: String,
        val url: String,
        val id: String,
        val link_flair_css_class: String?, // This can be null. Might want to do the same elsewhere. \/
        val created_utc: Double,
        val thumbnail: String
    )

    // Getting the JSON data and building the post item
    fun getJSONData(ctx: Context, onResult: (List<PostItem>) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.reddit.com/r/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitAPI = retrofit.create(RetroFitAPI::class.java)
        val call: Call<FGFData> = retrofitAPI.getData()
        
        val db_li = LatestDatabase.getInstance(ctx)

        call.enqueue(object : Callback<FGFData> {
            override fun onResponse(call: Call<FGFData>, response: Response<FGFData>) {
                if (response.isSuccessful) {
                    val fgfData = response.body()
                    fgfData?.let {
                        val result = it.data.children.map { child ->
                            PostItem(
                                title = HtmlCompat.fromHtml(child.data.title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
                                author = child.data.author,
                                url = child.data.url,
                                id = child.data.id,
                                thumbnail = child.data.thumbnail,
                                created_utc = child.data.created_utc,
                                link_flair_css_class = child.data.link_flair_css_class
                            )
                        }
                        onResult(result)

                        GlobalScope.launch(Dispatchers.IO) {
                            // Wiping the DB
                            db_li.latestItemDao().deleteAllLatest()
                            // Rebuilding the DB with the most recent few items
                            result.take(10).forEach { item ->
                                db_li.latestItemDao().insert(LatestItem(item.id))
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<FGFData>, t: Throwable) {
                Log.e("ListView", "Failed to get data", t)
                Toast.makeText(ctx, "Failed to get data. Reddit might be down.", Toast.LENGTH_SHORT).show()
            }
        })
    }

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
    fun getImage(item: PostItem): Painter {
        return if (item.thumbnail != "default" && item.thumbnail != "self" && !item.thumbnail.isNullOrEmpty()) {
            rememberAsyncImagePainter(model = item.thumbnail)
        } else {
            painterResource(id = R.drawable.fgf_logo_whiteout)
        }
    }

    // Separated code to run a check for an image and save a boolean due
    // assumed bug in android studio. Just nice to isolate too I guess.
    @Composable
    fun hasImage(item: PostItem): Boolean {
        return item.thumbnail == "default" || item.thumbnail == "self" || item.thumbnail.isNullOrEmpty()
    }

    // List view of post items
    @Composable
    fun DisplayListView() {
        val context = LocalContext.current
        var itemList by remember { mutableStateOf(emptyList<PostItem>()) }
        var selectedItem by remember { mutableStateOf<PostItem?>(null) }

        val db = FavouritesDatabase.getInstance(LocalContext.current)

        LaunchedEffect(key1 = true) {
            getJSONData(context) { items ->
                itemList = items
            }
        }

        if (itemList.isNotEmpty()) {
            LazyColumn {
                items(itemList) { item ->
                    Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable{selectedItem = item}) {
                        Image(
                            modifier = Modifier
                                .size(110.dp) // Formerly 110.dp
                                .background(color = MaterialTheme.colorScheme.primary),
                            painter = getImage(item),
                            contentDescription = item.title + " thumbnail",
                            contentScale = ContentScale.FillHeight,
                        )
                        Column(modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth(),
    //                modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = shortenContent(item.title), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (item.link_flair_css_class == "Expired"){TextDecoration.LineThrough} else {null})
                            Text(text = "/u/" + item.author, color = MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (item.link_flair_css_class == "Expired"){TextDecoration.LineThrough} else {null})
                            if (Build.VERSION.SDK_INT >= 26) {
                                val instant = Instant.ofEpochSecond(item.created_utc.toLong())
                                val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm a")
                                val localTime = localDateTime.format(formatter)
                                Text(text = "Posted $localTime",
                                color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontStyle = FontStyle.Italic
                            )}
                        }
//                        if (item.link_flair_css_class == "Expired"){
//                            Box(modifier = Modifier.fillMaxHeight().width(30.dp).rotate(90f)){
//                                Text("Expired", color = Color.Red,fontWeight = FontWeight.Bold,
//                                    modifier = Modifier.rotate(90f).padding(8.dp))
//                            }
//                        }
                    }
                    Divider()
                }
            }
            selectedItem?.let { item ->
                ExpandedItemView(item = item, onClose = { selectedItem = null })
            }

        } else {
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(5000) // 5 seconds delay
                isLoading = false
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){ if (isLoading) {
                Text(
                    text = "Loading FreeGameFindings data.",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .padding(4.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Text(
                    text = "No data loaded.",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Reddit might be down at the moment, try again later.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            }
        }
    }

    // Expandable list view when clicking on a post item
    @Composable
    fun ExpandedItemView(item: PostItem, onClose: () -> Unit) {
        val context = LocalContext.current
        val db = FavouritesDatabase.getInstance(LocalContext.current)
        val coroutine = rememberCoroutineScope()

        // Try to add BackHandler to this, currently not working
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            backgroundColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (!hasImage(item)) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f),
//                        .background(color = MaterialTheme.colorScheme.primary),
                        painter = getImage(item),
                        contentDescription = item.title + " thumbnail",
                        contentScale = ContentScale.Fit
                    )
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
                                    db.favouriteItemDao().upsert(FavouriteItem(item.id, item.title, item.author, item.url, item.thumbnail, item.created_utc))
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

                // Old Icon
//                IconButton(
//                    onClick = { // Thanks Foo <3
//                        coroutine.launch {
//                            withContext(Dispatchers.IO) {
//                                val existingItem = db.favouriteItemDao().getItemById(item.id)
//                                if (existingItem == null) {
//                                    db.favouriteItemDao().upsert(FavouriteItem(item.id, item.title, item.author, item.thumbnail, item.url))
//                                } else {
//                                    db.favouriteItemDao().delete(existingItem)
//                                }
//                            }
//                        }
//                    },
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Favorite,
//                        contentDescription = "Favorite"
//                    )
//                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                ) { Text(text = "Close",color = MaterialTheme.colorScheme.onPrimary) }
            }
        }
    }
}