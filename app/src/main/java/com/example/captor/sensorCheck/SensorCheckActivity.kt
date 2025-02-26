package com.example.captor.sensorCheck

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.captor.R

class SensorCheckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_check)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Liste complète des capteurs à vérifier
        val requiredSensors = listOf(
            Sensor.TYPE_ACCELEROMETER to "Accéléromètre",
            Sensor.TYPE_GYROSCOPE to "Gyroscope",
            Sensor.TYPE_LIGHT to "Capteur de lumière",
            Sensor.TYPE_PROXIMITY to "Capteur de proximité",
            Sensor.TYPE_MAGNETIC_FIELD to "Magnétomètre",
            Sensor.TYPE_GRAVITY to "Capteur de gravité",
            Sensor.TYPE_ROTATION_VECTOR to "Capteur de rotation",
            Sensor.TYPE_AMBIENT_TEMPERATURE to "Capteur de température ambiante",
            Sensor.TYPE_RELATIVE_HUMIDITY to "Capteur d'humidité",
            Sensor.TYPE_PRESSURE to "Capteur de pression atmosphérique",
            Sensor.TYPE_STEP_DETECTOR to "Détection de pas",
            Sensor.TYPE_STEP_COUNTER to "Compteur de pas",
            Sensor.TYPE_LINEAR_ACCELERATION to "Accélération linéaire",
            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR to "Rotation géomagnétique",
            Sensor.TYPE_MOTION_DETECT to "Détection de mouvement",
            Sensor.TYPE_STATIONARY_DETECT to "Détection de stationnement",
            Sensor.TYPE_HEART_RATE to "Fréquence cardiaque",
            Sensor.TYPE_HEART_BEAT to "Capteur de battement de cœur"
        )

        val unavailableSensors = mutableListOf<String>()

        for ((sensorType, sensorName) in requiredSensors) {
            if (sensorManager.getDefaultSensor(sensorType) == null) {
                unavailableSensors.add(sensorName)
            }
        }

        val resultTextView = findViewById<TextView>(R.id.text_sensor_check)

        if (unavailableSensors.isEmpty()) {
            resultTextView.text = "Tous les capteurs requis sont disponibles sur cet appareil."
        } else {
            val missingSensorsText = unavailableSensors.joinToString("\n") { "- $it" }
            resultTextView.text = "Fonctionnalités indisponibles, capteurs manquants :\n$missingSensorsText"
        }
    }
}
