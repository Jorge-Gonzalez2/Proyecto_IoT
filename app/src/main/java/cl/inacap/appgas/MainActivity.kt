package cl.inacap.appgas

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    // Solicita el permiso solo cuando se intenta mostrar una alerta real.
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) showDangerNotification()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
            GasScreen(onDanger = { requestDangerNotification() })
        }
    }

    private fun requestDangerNotification() {
        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            showDangerNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "gas_alerts", "Alertas de gas", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Avisos del simulador de gas" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun showDangerNotification() {
        val builder = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, "gas_alerts")
        } else {
            Notification.Builder(this)
        }
        val notification = builder
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("¡Peligro de gas! (simulación)")
            .setContentText("El nivel simulado superó el 80 %.")
            .setAutoCancel(true)
            .build()
        getSystemService(NotificationManager::class.java).notify(1, notification)
    }
}

@Composable
fun GasScreen(onDanger: () -> Unit = {}) {
    val context = LocalContext.current
    var gasLevel by remember { mutableStateOf(50f) }
    val percentage = gasLevel.toInt()
    val levelText = getLevelText(percentage)
    val badgeBg = getBadgeBackground(percentage)
    val badgeFg = getBadgeForeground(percentage)
    val progressColor = getProgressColor(percentage)
    val descriptionText = getDescriptionText(percentage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F5F2))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        HeaderSection()

        GasLevelCard(
            gasLevel = gasLevel,
            percentage = percentage,
            levelText = levelText,
            badgeBg = badgeBg,
            badgeFg = badgeFg,
            progressColor = progressColor,
            descriptionText = descriptionText
        )

        LevelScaleSection(percentage = percentage)

        if (percentage > 80) {
            DangerAlertSection()
        }

        DeviceSection(
            onDetailsClick = {
                Toast.makeText(context, "No disponible", Toast.LENGTH_SHORT).show()
            }
        )

        SensorSimulatorSection(
            gasLevel = gasLevel,
            onGasLevelChange = { newLevel ->
                // Notificar solo al cruzar de nivel normal a peligro.
                if (gasLevel <= 80f && newLevel > 80f) onDanger()
                gasLevel = newLevel
            },
            progressColor = progressColor
        )
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Home, contentDescription = "Hogar", tint = Color(0xFF2E7D32),
            modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.size(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("No más fugas", fontSize = 24.sp, fontWeight = FontWeight.Bold,
                color = Color(0xFF1C2821))
            Text("Hogar - Sensor Cocina · Simulación", fontSize = 13.sp,
                color = Color(0xFF55645D))
        }
    }
}

@Composable
fun DangerAlertSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = "Advertencia",
                tint = Color(0xFFC62828), modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text("¡PELIGRO! Nivel de gas elevado", fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828))
                Text("Alerta de prueba: nivel simulado superior al 80 %.",
                    fontSize = 13.sp, color = Color(0xFF7F2424))
            }
        }
    }
}

@Composable
fun GasLevelCard(
    gasLevel: Float,
    percentage: Int,
    levelText: String,
    badgeBg: Color,
    badgeFg: Color,
    progressColor: Color,
    descriptionText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (percentage > 80) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = "Estado del gas",
                tint = progressColor,
                modifier = Modifier.size(38.dp)
            )

            LinearProgressIndicator(
                progress = { gasLevel / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50)),
                color = progressColor,
                trackColor = Color(0xFFE0E0E0)
            )

            Text(
                text = "$percentage%",
                fontSize = 50.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1C2821)
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = badgeBg
            ) {
                Text(
                    text = levelText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeFg
                )
            }

            Text(
                text = descriptionText,
                fontSize = 14.sp,
                color = Color(0xFF55645D)
            )
        }
    }
}

@Composable
fun LevelScaleSection(percentage: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LevelLabel(
            text = "Básico",
            isSelected = percentage <= 20
        )
        LevelLabel(
            text = "Medio",
            isSelected = percentage in 21..40
        )
        LevelLabel(
            text = "Alto",
            isSelected = percentage in 41..60
        )
        LevelLabel(
            text = "Muy Alto",
            isSelected = percentage in 61..80
        )
        LevelLabel(
            text = "Peligro",
            isSelected = percentage > 80
        )
    }
}

@Composable
fun LevelLabel(text: String, isSelected: Boolean) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = if (isSelected) Color(0xFF1C2821) else Color(0xFF55645D),
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
fun DeviceSection(onDetailsClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "📶 Dispositivo conectado",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C2821)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WiFi - última lectura hace 2s",
                    fontSize = 13.sp,
                    color = Color(0xFF55645D)
                )

                Button(
                    onClick = onDetailsClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E4534)
                    )
                ) {
                    Text(text = "Detalles", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SensorSimulatorSection(
    gasLevel: Float,
    onGasLevelChange: (Float) -> Unit,
    progressColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Simulador de Sensor (Potenciómetro)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C2821)
            )

            Slider(
                value = gasLevel,
                onValueChange = onGasLevelChange,
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF2E4534),
                    activeTrackColor = progressColor
                )
            )
        }
    }
}

fun getLevelText(percentage: Int): String {
    return when {
        percentage <= 20 -> "Nivel Básico"
        percentage <= 40 -> "Nivel Medio"
        percentage <= 60 -> "Nivel Alto"
        percentage <= 80 -> "Nivel Muy Alto"
        else -> "Peligro"
    }
}

fun getBadgeBackground(percentage: Int): Color {
    return when {
        percentage <= 20 -> Color(0xFFE8F5E9)
        percentage <= 40 -> Color(0xFFFBF0CC)
        percentage <= 60 -> Color(0xFFFFE0B2)
        percentage <= 80 -> Color(0xFFFFCCBC)
        else -> Color(0xFFFFEBEE)
    }
}

fun getBadgeForeground(percentage: Int): Color {
    return when {
        percentage <= 20 -> Color(0xFF2E7D32)
        percentage <= 40 -> Color(0xFF8C730A)
        percentage <= 60 -> Color(0xFFE65100)
        percentage <= 80 -> Color(0xFFD84315)
        else -> Color(0xFFC62828)
    }
}

fun getProgressColor(percentage: Int): Color {
    return when {
        percentage <= 20 -> Color(0xFF4CAF50)
        percentage <= 40 -> Color(0xFFFFC107)
        percentage <= 60 -> Color(0xFFFF9800)
        percentage <= 80 -> Color(0xFFFF5722)
        else -> Color(0xFFF44336)
    }
}

fun getDescriptionText(percentage: Int): String {
    return when {
        percentage <= 20 -> "Entorno seguro, sin riesgos"
        percentage <= 40 -> "Variación detectada, en supervisión"
        percentage <= 60 -> "Concentración moderada de gas"
        percentage <= 80 -> "¡Atención! Riesgo elevado de fuga"
        else -> "¡PELIGRO! Evacue y ventile el área"
    }
}

@Preview(showBackground = true)
@Composable
fun GasScreenPreview() {
    GasScreen()
}
