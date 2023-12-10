package com.cedric.fgf

//import android.content.Context
//import androidx.work.Constraints
//import androidx.work.NetworkType
//import androidx.work.PeriodicWorkRequestBuilder
//import androidx.work.Worker
//import androidx.work.WorkerParameters
//import java.util.concurrent.TimeUnit
//
//class UpdateCheckWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
//    override fun doWork(): Result {
//        // Perform the network request to check for updates using Retrofit
//        // Check if the data has been updated, and if so, send a local notification
//        return Result.success()
//    }
//}
//
//// Schedule the worker to run periodically
//val constraints = Constraints.Builder()
//    .setRequiredNetworkType(NetworkType.CONNECTED)
//    .build()
//
//val updateCheckRequest = PeriodicWorkRequestBuilder<UpdateCheckWorker>(5, TimeUnit.MINUTES)
//    .setConstraints(constraints)
//    .build()
//
//WorkManager.getInstance(context).enqueueUniquePeriodicWork("UpdateCheck", ExistingPeriodicWorkPolicy.KEEP, updateCheckRequest)
