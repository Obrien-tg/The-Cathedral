package com.obrien.thecathedral.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.obrien.core.util.NotificationHelper

class PillarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("ritual_title") ?: "Ritual Start"
        val message = intent.getStringExtra("ritual_message") ?: "The time for discipline has arrived."
        
        NotificationHelper.showRitualNotification(context, title, message)
    }
}
