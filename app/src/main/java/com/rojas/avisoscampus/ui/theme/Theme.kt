package com.rojas.avisoscampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = AzulOscuro,
    background = FondoApp,
    surface = Color.White,
    surfaceVariant = FilaGris,
    outline = AzulPrimario,
    outlineVariant = BordeTarjeta,
    error = RojoTexto,
    errorContainer = RojoEtiqueta,
    onErrorContainer = RojoTexto
)

@Composable
fun AvisosCampusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
