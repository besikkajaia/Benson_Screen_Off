package com.benson.screenoff

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast

class LockActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!ScreenOffService.lock()) {
            // Служба ещё не включена: открываем настройки специальных возможностей
            Toast.makeText(this, getString(R.string.enable_hint), Toast.LENGTH_LONG).show()
            startActivity(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
        finish()
        overridePendingTransition(0, 0)
    }
}
