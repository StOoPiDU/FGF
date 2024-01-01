package com.cedric.fgf.misc

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.cedric.fgf.R

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                )
            } else {mutableStateOf(true)}
        }

        val workManager = WorkManager.getInstance(this)

        var notificationToggle by remember{
            mutableStateOf(true)
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                hasNotificationPermission = isGranted
            }
        )

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) && !hasNotificationPermission) {
            Button(
                onClick = {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Request Permission")
            }
        }

        if (hasNotificationPermission) {
            Button(onClick = {
                if (notificationToggle) {
                    notificationToggle = false
                    workManager.enqueueUniquePeriodicWork(
                        "updateCheckRequest",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        updateCheckRequest)
                } else {
                    notificationToggle = true
                    workManager.cancelAllWork()
                    workManager.pruneWork()
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(text = if (notificationToggle) "Enable Notifications" else "Disable Notifications")
            }
        }

        Button(onClick = {
            if (hasNotificationPermission) {
                showTestNotification(context)
            }
        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Text(text = "Show Test Notification")
        }

    }

    private fun showTestNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.fgf_logo_whiteout)
            .setContentTitle("New FGF Post Is Live (Test)")
            .setContentText("[Steam] (Game) StOoPiD Puzzle Game")
            .build()
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(1, notification)
    }
}
