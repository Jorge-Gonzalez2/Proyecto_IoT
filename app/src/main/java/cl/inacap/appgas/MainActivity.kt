package cl.inacap.appgas

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            var gasLevel by remember { mutableStateOf(0f) }
            val percentage = gasLevel.toInt()

            val levelText = when {
                percentage <= 20 -> "Nivel Básico"
                percentage <= 40 -> "Nivel Medio"
                percentage <= 60 -> "Nivel Alto"
                percentage <= 80 -> "Nivel Muy Alto"
                else -> "Peligro"
            }

            val badgeBg = when {
                percentage <= 20 -> Color(0xFFE8F5E9)
                percentage <= 40 -> Color(0xFFFBF0CC)
                percentage <= 60 -> Color(0xFFFFE0B2)
                percentage <= 80 -> Color(0xFFFFCCBC)
                else -> Color(0xFFFFEBEE)
            }

            val badgeFg = when {
                percentage <= 20 -> Color(0xFF2E7D32)
                percentage <= 40 -> Color(0xFF8C730A)
                percentage <= 60 -> Color(0xFFE65100)
                percentage <= 80 -> Color(0xFFD84315)
                else -> Color(0xFFC62828)
            }

            val progressColor = when {
                percentage <= 20 -> Color(0xFF4CAF50)
                percentage <= 40 -> Color(0xFFFFC107)
                percentage <= 60 -> Color(0xFFFF9800)
                percentage <= 80 -> Color(0xFFFF5722)
                else -> Color(0xFFF44336)
            }

            val descriptionText = when {
                percentage <= 20 -> "Entorno seguro, sin riesgos"
                percentage <= 40 -> "Variación detectada, en supervisión"
                percentage <= 60 -> "Concentración moderada de gas"
                percentage <= 80 -> "¡Atención! Riesgo elevado de fuga"
                else -> "¡PELIGRO! Evacue y ventile el área"
            }

            // Titulos
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF3F5F2))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "No más fugas",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1C2821)
                    )
                    Text(
                        text = "Hogar - Sensor Cocina",
                        fontSize = 14.sp,
                        color = Color(0xFF55645D)
                    )
                }

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
                        LinearProgressIndicator(
                            progress = { gasLevel / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50)),
                            color = progressColor,
                            trackColor = Color(0xFFE0E0E0),
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Básico", fontSize = 12.sp, color = if (percentage <= 20) Color(0xFF1C2821) else Color(0xFF55645D), fontWeight = if (percentage <= 20) FontWeight.Bold else FontWeight.Normal)
                    Text("Medio", fontSize = 12.sp, color = if (percentage in 21..40) Color(0xFF1C2821) else Color(0xFF55645D), fontWeight = if (percentage in 21..40) FontWeight.Bold else FontWeight.Normal)
                    Text("Alto", fontSize = 12.sp, color = if (percentage in 41..60) Color(0xFF1C2821) else Color(0xFF55645D), fontWeight = if (percentage in 41..60) FontWeight.Bold else FontWeight.Normal)
                    Text("Muy Alto", fontSize = 12.sp, color = if (percentage in 61..80) Color(0xFF1C2821) else Color(0xFF55645D), fontWeight = if (percentage in 61..80) FontWeight.Bold else FontWeight.Normal)
                    Text("Peligro", fontSize = 12.sp, color = if (percentage > 80) Color(0xFF1C2821) else Color(0xFF55645D), fontWeight = if (percentage > 80) FontWeight.Bold else FontWeight.Normal)
                }

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
                            text = "Dispositivo Conectado",
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
                                onClick = {
                                    Toast.makeText(context, "No disponible", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E4534))
                            ) {
                                Text(text = "Detalles", color = Color.White)
                            }
                        }
                    }
                }

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
                            onValueChange = { gasLevel = it },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF2E4534),
                                activeTrackColor = progressColor
                            )
                        )
                    }
                }
            }
        }
    }
}