package com.erthru.myroom.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.erthru.myroom.R
import com.erthru.myroom.ui.login.LoginActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        val i = Intent("CHAT")
        sendBroadcast(i)

        showNotification(p0?.notification?.body!!)

    }

    fun showNotification(message:String){

        val i = Intent(applicationContext,LoginActivity::class.java)
        val pi = PendingIntent.getActivity(applicationContext, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val channelId = applicationContext.resources.getString(R.string.default_notification_channel_id)
            val notifyId = 1
            val importance = NotificationManager.IMPORTANCE_HIGH

            val mChannel = NotificationChannel(channelId,"myroom",importance)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(mChannel)

            val notification = Notification.Builder(this,channelId)
                .setContentTitle("My Room")
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notif)
                .setColor(resources.getColor(R.color.colorAccent))
                .setContentIntent(pi)
                .build()


            manager.notify(notifyId,notification)

        }else{
            val builder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notif)
                .setColor(resources.getColor(R.color.colorAccent))
                .setContentTitle("My Room")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pi)

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0,builder.build())
        }

    }

}