# PROMPTS.md · Fase 2 (rama mejora-ia)

Mejoras elegidas con ayuda de Gemini en Android Studio:

1. **Dos canales:** canal `Urgentes` con `IMPORTANCE_HIGH` (emergente) y el canal `Avisos`; el recordatorio usa el urgente. Cambia el Notificador y el ViewModel.
2. **Texto largo:** el texto completo se ve al expandir la notificación con `BigTextStyle`. Cambia el Notificador.
3. **Receptor del cargador:** escucha `ACTION_POWER_CONNECTED` y `ACTION_POWER_DISCONNECTED` y avisa, con su propio switch. Cambia Receiver, ViewModel y View.

| Prompt que escribí | Qué respondió Gemini | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Quiero agregar un segundo canal llamado Urgentes con importancia alta para que el recordatorio salga como notificación emergente» | Creó el canal `urgentes` en `crearCanal` y propuso pasar un `Context` al `AvisosViewModel` para llamar a `Notificador.mostrar(context, aviso, urgente = true)` dentro de `recordarEn` | Dejé el canal en el Notificador, pero en vez del Context agregué `urgente: Boolean = false` al `Aviso` y el ViewModel emite `aviso.copy(urgente = true)`. El Notificador elige el canal según ese dato | La regla del lab prohíbe Context en el ViewModel. Así el ViewModel solo decide *qué* es urgente y la pantalla sigue siendo la que muestra la notificación |
| «¿Cómo hago que el recordatorio también suene o aparezca arriba en Android 7?» | Explicó que en versiones antes de Android 8 no hay canales y la importancia se controla con `setPriority` | Usé `PRIORITY_HIGH` cuando el aviso es urgente y `PRIORITY_DEFAULT` en los demás | Mi `minSdk` es 24, entonces tiene que funcionar también sin canales |
| «Haz que el texto de la notificación se vea completo cuando la expando» | Agregó `.setStyle(NotificationCompat.BigTextStyle().bigText(aviso.texto))` y además un `setSubText` con la hora | Dejé el `BigTextStyle` y quité el `setSubText` | El subtexto repetía la hora que ya muestra Android en la notificación |
| «Agrega un BroadcastReceiver que avise cuando se conecta o desconecta el cargador» | Creó `CargadorReceiver` y lo registró en el `AndroidManifest.xml` con un `<receiver>` estático; dentro de `onReceive` llamaba a `NotificationManagerCompat.notify` | Lo cambié a un receptor dinámico igual que el de modo avión: recibe una función `alCambiar`, se registra con `ContextCompat.registerReceiver` y `RECEIVER_NOT_EXPORTED` dentro de un `DisposableEffect` y se quita en `onDispose` | Desde Android 8 estas acciones no llegan a receptores del Manifest. Además `onReceive` debe ser corto y no notificar: el aviso pasa por el ViewModel y el Repository para quedar en el historial |
| «Pon un switch para el cargador en la tarjeta Receptor de anuncios» | Agregó el switch pero guardaba el estado con `remember { mutableStateOf(false) }` en la pantalla | Lo moví al ViewModel como `escuchandoCargador` (StateFlow) con `cambiarEscuchaCargador(valor)` | Con `remember` se perdía al rotar la pantalla y el receptor se quitaba; en el ViewModel sobrevive a la rotación |
| «Agrega una etiqueta de color para el origen Cargador en el historial» | Agregó `CARGADOR("Cargador")` en el enum `Origen` y un color verde | Lo dejé, solo moví el color a `Color.kt` como `OrigenCargador` | Así todos los colores del tema quedan en un solo archivo |

## Revisión final

- En `viewmodel/` no hay `Context`, `NotificationManager` ni `BroadcastReceiver`.
- Los dos receptores (`ModoAvionReceiver` y `CargadorReceiver`) se quitan con `unregisterReceiver` en `onDispose`.
- El `PendingIntent` sigue siendo `FLAG_IMMUTABLE`.
