package com.cedric.fgf.misc

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.cedric.fgf.R

class NotificationHandler : ComponentActivity() {
    private val CHANNEL_ID = "fgf_posts"
    private val SHARED_PREF_NAME = "notification_prefs"

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
        var hasNotificationPermission by rememberSaveable {
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

        var notificationToggle by rememberSaveable{
//            mutableStateOf(true)
            mutableStateOf(getNotificationToggleState(context))
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
                Text(text = "Request Notification Permission")
            }
        }

        if (hasNotificationPermission) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                ) {
                Text("Toggle Notifications", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f)) // Adding the required space to have the switch at the end.
                Switch(
                    modifier = Modifier.scale(1.3f),
                    checked = notificationToggle,
                    onCheckedChange = { newToggleState ->
                        notificationToggle = newToggleState
                        if (newToggleState) {
                            workManager.enqueueUniquePeriodicWork(
                                "updateCheckRequest",
                                ExistingPeriodicWorkPolicy.UPDATE,
                                updateCheckRequest
                            )
                        } else {
                            workManager.cancelAllWork()
                            workManager.pruneWork()
                        }
                        saveNotificationToggleState(context, newToggleState)
                    }
                )
            }
        }

        if (hasNotificationPermission) {
            Button(
                onClick = {showTestNotification(context)},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Show Test Notification")
            }
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

    private fun getNotificationToggleState(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        return sharedPreferences.getBoolean("notification_toggle", false)
    }

    private fun saveNotificationToggleState(context: Context, state: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("notification_toggle", state)
            apply()
        }
    }
}
