package com.subrat.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.subrat.R
import com.subrat.medicine.AddMedicineActivity
import java.io.IOException

/**
 * This is Background service class for the alarm and notification management after it get triggred by alarm that we set.
 */
class AlarmSoundService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var alarmNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null

    private val alarmUri: Uri?
        get() {
            var alert: Uri? = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alert == null) {
                alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                if (alert == null) {
                    alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                }
            }
            return alert
        }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        playSound(this, alarmUri)
    }

    //play deafult alam sound
    private fun playSound(context: Context, alert: Uri?) {
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer!!.setDataSource(context, alert!!)
            val audioManager = context
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_ALARM)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
            }
        } catch (e: IOException) {
            println("OOPS")
        }

    }


    override fun onDestroy() {
        super.onDestroy()

        //On destory stop and release the media player
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        sendNotification(intent.getStringExtra("medicine"), intent.getLongExtra("id", -1), this)
        return super.onStartCommand(intent, flags, startId)
    }

    //handle notification
    private fun sendNotification(msg: String, id: Long?, context: Context) {
        alarmNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.alarm_sound)
        //get pending intent
        val intent = Intent(context, AddMedicineActivity::class.java)
        intent.putExtra("id", id)
        val contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        //Create notification
        mBuilder = NotificationCompat.Builder(context)
            .setContentTitle("Medicine Time")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
            .setContentText(msg)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)

        //notiy notification manager about new notification
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            assert(alarmNotificationManager != null)
            mBuilder!!.setChannelId(NOTIFICATION_CHANNEL_ID)
            alarmNotificationManager!!.createNotificationChannel(notificationChannel)
        }
        assert(alarmNotificationManager != null)
        val notification = mBuilder!!.build()
        alarmNotificationManager!!.notify(NOTIFICATION_ID, notification)
        startForeground(NOTIFICATION_ID, notification)
    }

    companion object {

        //Notification ID for Alarm
        val NOTIFICATION_ID = 1
        val NOTIFICATION_CHANNEL_ID = "10001"
    }
}
