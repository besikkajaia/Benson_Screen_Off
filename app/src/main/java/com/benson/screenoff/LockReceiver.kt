package com.benson.screenoff

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

/** Принимает нажатие на виджет и блокирует экран через службу специальных возможностей. */
class LockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (!ScreenOffService.lock()) {
            Toast.makeText(context, context.getString(R.string.enable_hint), Toast.LENGTH_LONG).show()
        }
    }
}
