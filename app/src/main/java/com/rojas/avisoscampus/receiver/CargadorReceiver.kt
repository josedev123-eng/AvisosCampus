package com.rojas.avisoscampus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CargadorReceiver(private val alCambiar: (Boolean) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Igual que el de modo avion: corto, solo avisa si se conecto o no
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> alCambiar(true)
            Intent.ACTION_POWER_DISCONNECTED -> alCambiar(false)
        }
    }
}
