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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ListView {

    data class ListItem(val title: String, val id: Int)
    // ^ This needs to be removed/changed to TodosItem(?) (which needs to be renamed).
    // That or this is creating a new thing below, in which case it would need to be adjusted.

    fun getJSONData(ctx: Context, onResult: (List<ListItem>) -> Unit) {
        val retrofit = Retrofit.Builder()
//            .baseUrl("https://www.reddit.com/r/FreeGameFindings/new/.json/")
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitAPI = retrofit.create(RetroFitAPI::class.java)
        val call: Call<List<TodosItem>> = retrofitAPI.getData()

        call.enqueue(object : Callback<List<TodosItem>> {
            override fun onResponse(
                call: Call<List<TodosItem>>,
                response: Response<List<TodosItem>>
            ) {
                if (response.isSuccessful) {
                    val lst: List<TodosItem> = response.body()!!
                    val result = lst.map { ListItem(it.title, it.id) }
                    onResult(result)
                }
            }

            override fun onFailure(call: Call<List<TodosItem>>, t: Throwable) {
                Toast.makeText(ctx, "Failed to get data.", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    @Composable
    fun DisplayListView() {
        val context = LocalContext.current
        var itemList by remember { mutableStateOf(emptyList<ListItem>()) }
//    val navController = rememberNavController()
        var selectedItem by remember { mutableStateOf<ListItem?>(null) }

        val db = FavouritesDatabase.getInstance(LocalContext.current)

        LaunchedEffect(key1 = true) {
            getJSONData(context) { items ->
                itemList = items
            }
        }

        LazyColumn {
            items(itemList) { item ->
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
                        Text(text = item.title, textAlign = TextAlign.Center)
                        Text(text = "AUTHOR")
                        Text(text = item.id.toString())
                        Text(text = "URL")
                    }
                }
                Divider()
            }
        }
        selectedItem?.let { item ->
            ExpandedItemView(item = item, onClose = { selectedItem = null })
        }
    }

// Not working, needs navigation but the option I was going to use is throwing errors.
//fun openItemView(item: ListItem): @Composable () -> Unit {
//    return {
//        Column(modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(text = item.title, textAlign = TextAlign.Center)
//            Text(text = "AUTHOR")
//            Text(text = item.id.toString())
//            Text(text = "URL")
//        }
//    }
//}

    @Composable
    fun AddItem(item: ListItem): Unit{
        val db = FavouritesDatabase.getInstance(LocalContext.current)
        LaunchedEffect(Unit){
            launch{
                val result = withContext(Dispatchers.IO){
                    db.favouriteItemDao().upsert(FavouriteItem(item.id,item.title) )
                }
            }
        }
    }

    @Composable
    fun ExpandedItemView(item: ListItem, onClose: () -> Unit){

        val db = FavouritesDatabase.getInstance(LocalContext.current)


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
//                    .height(100.dp)
//                    .width(100.dp)
                        .background(color = Color.Blue)
                )
                Text(
                    text = item.title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(text = "AUTHOR:" + " include author var", fontStyle = FontStyle.Italic)
                Text(text = "ID:" + item.id.toString())
                Row() {
                    //Add padding
                    Text(text = "URL" + "include url var")
                    Text(text = "R_URL" + " include r_url var")
                    // These should be converted into buttons or something to link out.
                }


                IconButton(
                    onClick = {AddItem(item)}, // PROBLEM HERE
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite"
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