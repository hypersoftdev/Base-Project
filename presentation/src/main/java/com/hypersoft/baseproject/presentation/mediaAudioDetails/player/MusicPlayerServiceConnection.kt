package com.hypersoft.baseproject.presentation.mediaAudioDetails.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicPlayerServiceConnection(private val context: Context) {

    private var service: MusicPlayerService? = null
    private var isBound = false

    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as? MusicPlayerService.LocalBinder
            service = localBinder?.getService()
            isBound = true
            _isServiceConnected.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
            _isServiceConnected.value = false
        }
    }

    fun bindService() {
        if (!isBound) {
            val intent = Intent(context, MusicPlayerService::class.java)

            // Handle Android version differences for foreground service
            try {
                // Start foreground service first, then bind
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
                // Bind after starting service
                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            } catch (ex: IllegalStateException) {
                // Service might already be running, just bind
                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    fun unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
            service = null
            _isServiceConnected.value = false
        }
    }

    fun getService(): MusicPlayerService? = service
}