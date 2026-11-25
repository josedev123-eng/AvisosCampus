package com.rojas.avisoscampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rojas.avisoscampus.model.Aviso
import com.rojas.avisoscampus.model.Origen
import com.rojas.avisoscampus.ui.theme.BordeTarjeta
import com.rojas.avisoscampus.ui.theme.FilaGris
import com.rojas.avisoscampus.ui.theme.GrisTexto
import com.rojas.avisoscampus.ui.theme.OrigenBoton
import com.rojas.avisoscampus.ui.theme.OrigenModoAvion
import com.rojas.avisoscampus.ui.theme.OrigenRecordatorio
import com.rojas.avisoscampus.ui.theme.OrigenCargador

// Tarjeta con la lista de avisos. No conoce al ViewModel, solo recibe la lista.
@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BordeTarjeta)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Historial",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            if (avisos.isEmpty()) {
                Text(
                    text = "Sin avisos todavía",
                    color = GrisTexto,
                    fontSize = 13.sp
                )
            } else {
                // Son pocas filas, por eso uso Column con forEach y no LazyColumn
                avisos.take(20).forEach { aviso ->
                    FilaAviso(aviso)
                }
            }
        }
    }
}

@Composable
fun FilaAviso(aviso: Aviso) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FilaGris, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EtiquetaOrigen(aviso.origen)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = aviso.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = aviso.texto, color = GrisTexto, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = aviso.hora, color = GrisTexto, fontSize = 12.sp)
    }
}

@Composable
fun EtiquetaOrigen(origen: Origen) {
    val fondo = when (origen) {
        Origen.BOTON -> OrigenBoton
        Origen.RECORDATORIO -> OrigenRecordatorio
        Origen.MODO_AVION -> OrigenModoAvion
        Origen.CARGADOR -> OrigenCargador
    }
    // la etiqueta amarilla se lee mejor con letra oscura
    val letra = if (origen == Origen.RECORDATORIO) Color(0xFF3A2A00) else Color.White
    Text(
        text = origen.texto,
        color = letra,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}
