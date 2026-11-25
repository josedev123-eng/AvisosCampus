package com.rojas.avisoscampus.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ModoAvionReceiver(private val alCambiar: (Boolean) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // onReceive debe ser corto: solo lee el dato y avisa
        if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            val activo = intent.getBooleanExtra("state", false)
            alCambiar(activo)
        }
    }
}
