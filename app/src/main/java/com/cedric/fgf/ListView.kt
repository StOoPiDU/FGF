package com.cedric.fgf

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import androidx.core.text.HtmlCompat

object ListView {

    data class PostItem(
        val title: String,
        val author: String,
        val url: String,
        val id: String
    )

    fun getJSONData(ctx: Context, onResult: (List<PostItem>) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.reddit.com/r/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitAPI = retrofit.create(RetroFitAPI::class.java)
        val call: Call<FGFData> = retrofitAPI.getData()

        call.enqueue(object : Callback<FGFData> {
            override fun onResponse(call: Call<FGFData>, response: Response<FGFData>) {
                if (response.isSuccessful) {
                    Toast.makeText(ctx, "Data Loaded", Toast.LENGTH_SHORT).show()
                    val fgfData = response.body()
                    fgfData?.let {
                        val result = it.data.children.map { child ->
                            PostItem(
                                title = HtmlCompat.fromHtml(child.data.title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
                                author = child.data.author,
                                url = child.data.url,
                                id = child.data.id
                            )
                        }
                        onResult(result)
                    }
                }
            }

            override fun onFailure(call: Call<FGFData>, t: Throwable) {
                Log.e("ListView", "Failed to get data", t)
                Toast.makeText(ctx, "Failed to get data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun shortenContent(content: String): String {
        // Implement your URL shortening logic here
        // For simplicity, let's assume a maximum display length of 20 characters
        return if (content.length > 30) {
            content.substring(0, 30) + "..."
        } else {
            content
        }
    }

    @Composable
    fun DisplayListView() {
        val context = LocalContext.current
        var itemList by remember { mutableStateOf(emptyList<PostItem>()) }
        var selectedItem by remember { mutableStateOf<PostItem?>(null) }

        LaunchedEffect(key1 = true) {
            getJSONData(context) { items ->
                itemList = items
            }
        }
        LazyColumn {
            items(itemList) { item ->
                // This clickable seems to be laggy as hell. Hopefully that's just an emulation issue.
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable{selectedItem = item}) {
                    Image(
                        modifier = Modifier
                            .size(100.dp)
                            .background(color = Color.Blue),
                        painter = painterResource(id = R.drawable.fgf_logo_whiteout),
                        contentDescription = "test image",
                        contentScale = ContentScale.FillHeight,)
                    Column(modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(),
//                modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = shortenContent(item.title), textAlign = TextAlign.Center)
                        Text(text = shortenContent(item.author))
                        Text(text = shortenContent(item.url))
                    }
                }
                Divider()
            }
        }
        selectedItem?.let { item ->
            ExpandedItemView(item = item, onClose = { selectedItem = null })
        }
    }

    @Composable
    fun ExpandedItemView(item: PostItem, onClose: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
            // ^ can try thing like 0.9f for an argument, this is nice though
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.fgf_logo_whiteout),
                    contentDescription = "My Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Blue)
                )
                Text(
                    text = item.title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(text = "AUTHOR:" + " ${shortenContent(item.author)}", fontStyle = FontStyle.Italic)
                Row() {
                    //Add padding
                    Text(text = "URL:" + "${shortenContent(item.url)}")
                    Text(text = "ID:" + "${shortenContent(item.id)}")
//                    Text(text = "R_URL:" + "I have no idea what this is cedric")
                    // These should be converted into buttons or something to link out.
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) { Text(text = "Close") }
            }
        }
    }
}