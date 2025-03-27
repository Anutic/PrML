//package com.example.mycalc2.notifications
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.os.Build
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import com.example.mycalc2.MainActivity
//import com.example.mycalc2.R
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//
//        Log.d("FCM", "Сообщение получено: ${remoteMessage.data}")
//
//        val title = remoteMessage.notification?.title ?: "Новое уведомление"
//        val message = remoteMessage.notification?.body ?: "Проверь приложение!"
//
//        showNotification(title, message)
//    }
//
//    private fun showNotification(title: String, message: String) {
//        val channelId = "my_channel_id"
//        val notificationId = 101
//
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val builder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(android.R.drawable.ic_dialog_info) // Добавь иконку в res/drawable
//            .setContentTitle(title)
//            .setContentText(message)
//            .setAutoCancel(true)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Создаём канал для Android 8+ (API 26+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Основной канал уведомлений",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(notificationId, builder.build())
//    }
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.d("FCM", "Обновлённый токен: $token")
//
//        // Отправить токен в Firestore или Backend
//        sendTokenToServer(token)
//    }
//
//    private fun sendTokenToServer(token: String) {
//        // Реализуй сохранение токена в Firestore или API сервера
//    }
//}
package com.example.mycalc2.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mycalc2.CalculatorActivity
import com.example.mycalc2.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel_id"

        val intent = Intent(this, CalculatorActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this@MyFirebaseMessagingService)) {
            if (ActivityCompat.checkSelfPermission(
                    this@MyFirebaseMessagingService,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

}