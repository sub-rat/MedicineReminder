package com.subrat.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * This is The broadcase receiver class which receive the alam that was set and activate the alarmSound service.
 * this will work in background to listem for the alam that we set earlier.
 * the data we send in intent form our AddMedicineActivity come over here.
 */
class AlarmReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val intent1 = Intent(context, AlarmSoundService::class.java)
        intent1.putExtra("id", intent.getLongExtra("id", -1))
        intent1.putExtra("medicine", intent.getStringExtra("medicine"))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1)
        } else {
            context.startService(intent1)
        }
    }
}
