package com.cedric.fgf

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class NotificationHandler : ComponentActivity() {
    private val CHANNEL_ID = "fgf_posts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        createNotificationChannel()
        setContent {
            NotificationContent()
        }
    }

    @Composable
    fun NotificationContent() {
        val context = LocalContext.current
        var hasNotificationPermission by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasNotificationPermission = isGranted
            }
        )
        Button(onClick = {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White)) {
            Text(text = "Request Permission")
        }
        Button(onClick = {
            if (hasNotificationPermission) {
                showNotification(context)
            }
        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White)
        ) {
            Text(text = "Show Notification")
        }

    }

    // Is this not calling app level?
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "FGF Posts"
//            val descriptionText = "New FGF Posts Notifier"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

    private fun showNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSmallIcon(R.drawable.fgf_logo_whiteout) // Get my icon
            .setContentTitle("Hello world")
            .setContentText("This is a description")
            .build()
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(1, notification)
    }
}
