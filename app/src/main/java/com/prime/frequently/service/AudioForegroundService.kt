package com.prime.frequently.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

// Phase 9: runs BinauralPlayer in foreground, exposes controls via Binder
class AudioForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
