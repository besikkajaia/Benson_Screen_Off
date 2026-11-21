package com.benson.screenoff

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

class ScreenOffService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var cover: View? = null
    private val removeCoverRunnable = Runnable { removeCover() }

    // Когда экран погас, чёрную накладку убираем
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            removeCover()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        registerReceiver(screenReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    override fun onUnbind(intent: Intent?): Boolean {
        removeCover()
        try {
            unregisterReceiver(screenReceiver)
        } catch (e: IllegalArgumentException) {
            // уже снят
        }
        if (instance === this) instance = null
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // события не нужны
    }

    override fun onInterrupt() {
        // ничего не делаем
    }

    /** Сначала мгновенно чернеет экран, потом идёт блокировка. */
    fun lockWithCover(): Boolean {
        showCover()
        val ok = performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
        if (!ok) removeCover()
        return ok
    }

    private fun showCover() {
        if (cover != null) return
        try {
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val view = View(this)
            view.setBackgroundColor(Color.BLACK)
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.OPAQUE
            )
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            wm.addView(view, params)
            cover = view
            // страховка: если что-то пошло не так, накладка сама исчезнет через 2 секунды
            handler.postDelayed(removeCoverRunnable, 2000)
        } catch (e: Exception) {
            cover = null
        }
    }

    private fun removeCover() {
        handler.removeCallbacks(removeCoverRunnable)
        val view = cover ?: return
        cover = null
        try {
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.removeView(view)
        } catch (e: Exception) {
            // уже убрана
        }
    }

    companion object {
        @Volatile
        var instance: ScreenOffService? = null

        /** true, если блокировка запущена */
        fun lock(): Boolean = instance?.lockWithCover() ?: false
    }
}
