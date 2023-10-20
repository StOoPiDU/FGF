package com.cedric.fgf

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cedric.fgf.ui.theme.FGFTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState


//class MainActivity : ComponentActivity() {
//
//    //private val BASE_URL = "https://www.reddit.com/r/FreeGameFindings/new/.json/"
//    private val BASE_URL = "https://jsonplaceholder.typicode.com/"
//
//    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            FGFTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Scaffold(
//                        topBar = {
//                            TopAppBar(backgroundColor = Color.Blue,
//                                title = {
//                                    Text(
//                                        text = "JSON Parsing w/ Andyroid",
//                                        modifier = Modifier.fillMaxWidth(),
//                                        textAlign = TextAlign.Center,
//                                        color = Color.White
//                                    )
//                                })
//                        }
//                    ) {
//                        val output = remember { mutableStateOf(StringBuilder()) }
//                        LaunchedEffect(Unit) {
//                            val apiService = Retrofit.Builder()
//                                .addConverterFactory(GsonConverterFactory.create())
//                                .baseUrl(BASE_URL)
//                                .build().create(RetroFitAPI::class.java)
//
//                            val retrofitData = apiService.getData()
//
//                            retrofitData.enqueue(object : Callback<List<TodosItem>?> {
//                                override fun onResponse(
//                                    call: Call<List<TodosItem>?>,
//                                    response: Response<List<TodosItem>?>
//                                ) {
//                                    val rspBody = response.body()
//
//                                    // Convert this into a LazyColumn with divs and stuff
//                                    val strData = StringBuilder()
//                                    for (data in rspBody!!) {
//                                        strData.append(data.id)
//                                        strData.append(data.title)
//                                        strData.append("\n\n")
//                                    }
//                                    output.value = strData
//                                }
//
//                                override fun onFailure(call: Call<List<TodosItem>?>, t: Throwable) {
//                                    TODO("Not yet implemented")
//                                }
//                            })
//                        }
//                        LazyColumn {
//                            item {
//                                displayData(s = output.value)
//                                Divider()
//                                Divider()
//                                Divider()
//                                Divider()
//                                Divider() //  Testing purposes, convert list into this structuring instead
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun displayData(s:StringBuilder){
//    Text(text = s.toString())
//}


// ITERATION ONE

//class MainActivity : ComponentActivity() {
//
//    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            FGFTheme {
//                Surface(modifier = Modifier.fillMaxSize(), color = Color.Blue) {
//                    Scaffold(
//                        topBar = {
//                            TopAppBar(backgroundColor = Color.Blue,
//                                title = {
//                                    Text(
//                                        text = "JSON Parsing w/ Andyroid",
//                                        modifier = Modifier.fillMaxWidth(),
//                                        textAlign = TextAlign.Center,
//                                        color = Color.White
//                                    )
//                                })
//                        }) {
//                        displayListView()
//                    }
//                }
//            }
//        }
//    }
//}
//
//fun getJSONData(postList: MutableList<String>, ctx: Context) {
//    val retrofit = Retrofit.Builder()
//        //.baseUrl("https://www.reddit.com/r/FreeGameFindings/new/.json/")
//        .baseUrl("https://jsonplaceholder.typicode.com/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val retrofitAPI = retrofit.create(RetroFitAPI::class.java)
//    val call: Call<List<TodosItem>> = retrofitAPI.getData()
//
//    call!!.enqueue(object : Callback<List<TodosItem>?> {
//        override fun onResponse(
//            call: Call<List<TodosItem>?>,
//            response: Response<List<TodosItem>?>
//        ) {
//            if (response.isSuccessful) {
//                var lst: List<TodosItem> = ArrayList()
//                lst = response.body()!!
////                var postList = lst
//
//                for (i in lst.indices) {
//                    postList.add(lst[i].title)
////                    postList.add("${lst[i].id} - ${lst[i].title}")
////                    postList.add(lst[i].id.toString())
////                    postList.add(lst[i].userId.toString())
//                }
//            }
//        }
//
//        override fun onFailure(call: Call<List<TodosItem>?>, t: Throwable) {
//            Toast.makeText(ctx, "Failed to get data.", Toast.LENGTH_SHORT)
//                .show()
//        }
//    })
//}
//
//@Composable
//fun displayListView() {
//    val context = LocalContext.current
//    val postList = remember { mutableStateListOf<String>() }
//    getJSONData(postList, context)
//
//    LazyColumn {
//        items(postList) { item ->
//            Text(item, modifier = Modifier.padding(15.dp))
//            Divider()
//        }
//    }
//}




// ITERATION ONE - Two

//data class ListItem(val title: String, val id: Int)
//
//fun getJSONData(ctx: Context): List<ListItem> {
//    val retrofit = Retrofit.Builder()
//        .baseUrl("https://jsonplaceholder.typicode.com/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    val retrofitAPI = retrofit.create(RetroFitAPI::class.java)
//    val call: Call<List<TodosItem>> = retrofitAPI.getData()
//
//    val result = mutableListOf<ListItem>()
//
//    call!!.enqueue(object : Callback<List<TodosItem>?> {
//        override fun onResponse(
//            call: Call<List<TodosItem>?>,
//            response: Response<List<TodosItem>?>
//        ) {
//            if (response.isSuccessful) {
//                var lst: List<TodosItem> = ArrayList()
//                lst = response.body()!!
//
//                for (i in lst.indices) {
//                    result.add(ListItem(lst[i].title, lst[i].id))
//                }
//            }
//        }
//
//        override fun onFailure(call: Call<List<TodosItem>?>, t: Throwable) {
//            Toast.makeText(ctx, "Failed to get data.", Toast.LENGTH_SHORT)
//                .show()
//        }
//    })
//
//    return result
//}
//
//@Composable
//fun displayListView() {
//    val context = LocalContext.current
//    val itemList = remember { getJSONData(context) }
//
//    LazyColumn {
//        items(itemList) { item ->
//            Text(item.title, modifier = Modifier.padding(15.dp))
////            Text(item.id.toString(), modifier = Modifier.padding(15.dp))
//            Divider()
//        }
//    }
//}



// ITERATION TWO
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FGFTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Blue) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                backgroundColor = Color.Blue,
                                title = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.fgf_logo_whiteout),
                                        contentDescription = "logo",
                                        tint = Color.White,
                                    )
                                    Text(
                                        text = "FreeGameFindings",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                }
                            )
                        }
                    ) {
                        displayListView()
                    }
                }
            }
        }
    }
}

data class ListItem(val title: String, val id: Int)

fun getJSONData(ctx: Context, onResult: (List<ListItem>) -> Unit) {
    val retrofit = Retrofit.Builder()
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
fun displayListView() {
    val context = LocalContext.current
    var itemList by remember { mutableStateOf(emptyList<ListItem>()) }
//    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf<ListItem?>(null) }

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
fun ExpandedItemView(item: ListItem, onClose: () -> Unit){
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

            Button(
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {Text(text = "Close")}
        }
    }
}

