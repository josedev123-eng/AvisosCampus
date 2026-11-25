package com.rojas.avisoscampus.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rojas.avisoscampus.model.PermisoUi
import com.rojas.avisoscampus.notificacion.Notificador
import com.rojas.avisoscampus.ui.theme.BordeTarjeta
import com.rojas.avisoscampus.ui.theme.GrisEtiqueta
import com.rojas.avisoscampus.ui.theme.GrisTexto
import com.rojas.avisoscampus.ui.theme.RojoEtiqueta
import com.rojas.avisoscampus.ui.theme.RojoTexto
import com.rojas.avisoscampus.ui.theme.VerdeEtiqueta
import com.rojas.avisoscampus.ui.theme.VerdeTexto
import com.rojas.avisoscampus.viewmodel.AvisosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()

    // Paso 2: pedir el permiso de notificaciones
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        vm.permisoRespondido(concedido)
    }

    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) {
            vm.permisoRespondido(true)
        }
    }

    // Recibe los eventos del ViewModel y muestra la notificacion
    LaunchedEffect(Unit) {
        vm.eventos.collect { aviso ->
            val mostrado = Notificador.mostrar(contexto, aviso)
            // TODO Paso 8: si no se mostró, avisar al ViewModel
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("AvisosCampus", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Tarjeta(titulo = "Permiso de notificaciones") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Estado:", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    EtiquetaPermiso(permiso)
                }
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= 33) {
                            lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            vm.permisoRespondido(true)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Permitir notificaciones", fontWeight = FontWeight.Bold)
                }
            }

            Tarjeta(titulo = "Avisos") {
                Button(
                    onClick = { vm.avisarAhora() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Mostrar aviso", fontWeight = FontWeight.Bold)
                }
            }

            Tarjeta(titulo = "Recordatorio") {
                // TODO Paso 5: Recordar en 10 s, Cancelar y Faltan N s
            }

            Tarjeta(titulo = "Receptor de anuncios") {
                // TODO Paso 6: Switch Escuchar el modo avión
            }

            Tarjeta(titulo = "Historial") {
                // TODO Paso 7: lista de avisos
            }

        }
    }
}

@Composable
fun Tarjeta(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
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
                text = titulo,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            contenido()
        }
    }
}

@Composable
fun EtiquetaPermiso(permiso: PermisoUi) {
    val texto: String
    val fondo: Color
    val letra: Color
    when (permiso) {
        PermisoUi.SinPedir -> {
            texto = "Sin pedir"
            fondo = GrisEtiqueta
            letra = GrisTexto
        }
        PermisoUi.Concedido -> {
            texto = "Permitido"
            fondo = VerdeEtiqueta
            letra = VerdeTexto
        }
        PermisoUi.Denegado -> {
            texto = "Denegado"
            fondo = RojoEtiqueta
            letra = RojoTexto
        }
    }
    Text(
        text = texto,
        color = letra,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 2.dp)
    )
}
