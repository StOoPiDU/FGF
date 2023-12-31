package com.cedric.fgf.pages

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cedric.fgf.R
import com.cedric.fgf.database.FavouritesDatabase
import com.cedric.fgf.misc.NotificationHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Settings {
    // This might be better called an "About" page.
    // Use this object to describe what FGF is,
    // and possibly just link to the FGF links ala linktree.
    @Composable
    fun SettingsScreen() {
        val context = LocalContext.current
        val coroutine = rememberCoroutineScope()
        val db = FavouritesDatabase.getInstance(LocalContext.current)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.fgf_logo_whiteout),
                contentDescription = "logo",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(50.dp)
            )
            Text(
                "Welcome to the FGF Mobile Application!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                "FreeGameFindings is a group where users share their free video game promotion finds. You can view this reformatted list of the  latest posts to FGF and use the buttons available to check out the posts on your device.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                "You can also enable notifications for your device to allow you to be sent a notification whenever a new post is live on FGF.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Calling the Notification buttons and the logic from it
            NotificationHandler().NotificationContent()

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Delete All Favourites?",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f)) // Adding the required space to have the switch at the end.
                Button(
                    onClick = {
                        coroutine.launch {
                            withContext(Dispatchers.IO) {
                                db.favouriteItemDao().deleteAll()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Text(
                "Note: This will completely wipe your saved posts and leave you with none.",
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                "Want to see more from FGF?",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row() {
                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.reddit.com/r/FreeGameFindings/")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) { Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.reddit_logo),
                    contentDescription = "Reddit",
                ) }

                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://twitter.com/FreeGameFinding")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) { Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.twitter_logo),
                    contentDescription = "Twitter",
                ) }
            }

            Row(){
                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/channel/UCSKIK4aebYPJSCUkvqRXvHw/featured")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) { Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.youtube_logo),
                    contentDescription = "YouTube",
                ) }

                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.tiktok.com/@freegamefindings")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) { Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(id = R.drawable.tiktok_logo),
                    contentDescription = "TikTok",
                ) }
            }
        }
    }
}