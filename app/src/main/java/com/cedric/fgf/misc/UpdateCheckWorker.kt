package com.cedric.fgf.misc

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cedric.fgf.MainActivity
import com.cedric.fgf.R
import com.cedric.fgf.database.LatestDatabase
import com.cedric.fgf.database.LatestItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class UpdateCheckWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            val appContext = applicationContext

            val intent = Intent(appContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent: PendingIntent = PendingIntent.getActivity(appContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            data class CheckItem(val title: String, val id: String)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com/r/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val retrofitAPI = retrofit.create(RetroFitAPI::class.java)
            val response = retrofitAPI.getData().execute()

            if (response.isSuccessful) {
                val db_li = LatestDatabase.getInstance(appContext)
                // If the latest item DB is empty this code wont run. Trying to stop extra calls and crashes.
                if (db_li.latestItemDao().getAll() == null) { return Result.failure()}
                val fgfData = response.body()
                fgfData?.let {
                    val check = it.data.children.map { child ->
                        CheckItem(title = child.data.title, id = child.data.id)
                    }

                    var notificationId = (0..1000).random() // This should prob be a static int but I don't want it to be overwritten

                    GlobalScope.launch(Dispatchers.IO) {
                        check.take(3).forEach { check ->
                            if (db_li.latestItemDao().getLatestItemById(check.id) == null) {
                                db_li.latestItemDao().insert(LatestItem(check.id))
                                // Create and post the notification
                                val notification = NotificationCompat.Builder(appContext, "fgf_posts")
                                    .setSmallIcon(R.drawable.fgf_logo_whiteout)
                                    .setContentTitle("New FGF Post Is Live")
                                    .setContentText(check.title)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true)
                                    .build()
                                val notificationManager = ContextCompat.getSystemService(appContext, NotificationManager::class.java)
                                notificationManager?.notify(1, notification)
                                notificationId++
                            }
                        }
                    }
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}


// Schedule the worker to run periodically
val constraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

val updateCheckRequest = PeriodicWorkRequestBuilder<UpdateCheckWorker>(5, TimeUnit.MINUTES)
    .setConstraints(constraints)
    .build()

//WorkManager.getInstance(context).enqueueUniquePeriodicWork("UpdateCheck", ExistingPeriodicWorkPolicy.KEEP, updateCheckRequest)
