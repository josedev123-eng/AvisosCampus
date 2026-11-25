package com.rojas.avisoscampus.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.rojas.avisoscampus.model.PermisoUi
import com.rojas.avisoscampus.notificacion.Notificador
import com.rojas.avisoscampus.receiver.ModoAvionReceiver
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
    val faltan by vm.faltan.collectAsState()
    val escuchando by vm.escuchandoAvion.collectAsState()
    val avisos by vm.avisos.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

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
            if (!mostrado) {
                vm.noSePudoMostrar()
            }
        }
    }

    // Paso 6: registrar el receptor solo mientras el switch esta activo
    DisposableEffect(escuchando) {
        if (escuchando) {
            val receptor = ModoAvionReceiver { activo -> vm.alCambiarModoAvion(activo) }
            ContextCompat.registerReceiver(
                contexto,
                receptor,
                IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose { contexto.unregisterReceiver(receptor) }
        } else {
            onDispose { }
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

            // Paso 8: mensaje cuando no hay permiso
            mensaje?.let { texto ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = texto,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, contexto.packageName)
                        contexto.startActivity(intent)
                    },
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Abrir ajustes", fontWeight = FontWeight.Bold)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = { vm.recordarEn(10) },
                        enabled = faltan == null
                    ) {
                        Text("Recordar en 10 s", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { vm.cancelarRecordatorio() },
                        enabled = faltan != null,
                        border = BorderStroke(
                            1.5.dp,
                            if (faltan != null) MaterialTheme.colorScheme.primary else BordeTarjeta
                        )
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Bold)
                    }
                }
                faltan?.let { s ->
                    Text(
                        text = "Faltan $s s",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Tarjeta(titulo = "Receptor de anuncios") {
                FilaSwitch(
                    texto = "Escuchar el modo avión",
                    activo = escuchando,
                    alCambiar = { vm.cambiarEscucha(it) }
                )
                Text(
                    text = "Activa o desactiva el modo avión del celular",
                    color = GrisTexto,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            HistorialAvisos(avisos)

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

@Composable
fun FilaSwitch(texto: String, activo: Boolean, alCambiar: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = texto, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = activo,
            onCheckedChange = alCambiar,
            colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
