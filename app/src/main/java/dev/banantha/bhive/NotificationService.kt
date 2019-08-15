package dev.banantha.bhive

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class NotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.notification?.let {
            println(it.title)
            println(it.body)
            sendNotification(it.title, it.body)
        }
    }

    private fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(this, HiveActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "B Hive",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String?) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theColony = sharedPreferences.getString("web_service", "")
        val cipher = sharedPreferences.getString("cipher", "")
        val activity = "/FCM/"
        val url = "$theColony$cipher$activity$token"

        val queue = Volley.newRequestQueue(this)

        val jsonRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener
            {
                response -> sendNotification("FCM Token", response)
            },
            Response.ErrorListener
            {
                sendNotification("FCM Token", "FCM Token Error!")
            })

        queue.add(jsonRequest)
    }
}