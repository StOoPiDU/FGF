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

class MainActivity : ComponentActivity() {

    //private val BASE_URL = "https://www.reddit.com/r/FreeGameFindings/new/.json/"
    private val BASE_URL = "https://jsonplaceholder.typicode.com/"

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FGFTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(backgroundColor = Color.Blue,
                                title = {
                                    Text(
                                        text = "JSON Parsing w/ Andyroid",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                })
                        }
                    ) {
                        val output = remember { mutableStateOf(StringBuilder()) }
                        LaunchedEffect(Unit) {
                            val apiService = Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .baseUrl(BASE_URL)
                                .build().create(RetroFitAPI::class.java)

                            val retrofitData = apiService.getData()

                            retrofitData.enqueue(object : Callback<List<todosItem>?> {
                                override fun onResponse(
                                    call: Call<List<todosItem>?>,
                                    response: Response<List<todosItem>?>
                                ) {
                                    val rspBody = response.body()

                                    // Convert this into a LazyColumn with divs and stuff
                                    val strData = StringBuilder()
                                    for (data in rspBody!!) {
                                        strData.append(data.id)
                                        strData.append(data.title)
                                        strData.append("\n\n")
                                    }
                                    output.value = strData
                                }

                                override fun onFailure(call: Call<List<todosItem>?>, t: Throwable) {
                                    TODO("Not yet implemented")
                                }
                            })
                        }
                        LazyColumn {
                            item {
                                displayData(s = output.value)
                                Divider()
                                Divider()
                                Divider()
                                Divider()
                                Divider() //  Testing purposes, convert list into this structuring instead
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun displayData(s:StringBuilder){
    Text(text = s.toString())
}