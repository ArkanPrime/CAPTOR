package com.example.captor.sensorList

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.captor.R

class SensorDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_detail)

        val sensorName = intent.getStringExtra("sensorName") ?: "Inconnu"
        val sensorType = intent.getIntExtra("sensorType", -1)
        val vendor = intent.getStringExtra("vendor") ?: "Inconnu"
        val version = intent.getIntExtra("version", 0)
        val maxRange = intent.getFloatExtra("maxRange", 0f)
        val resolution = intent.getFloatExtra("resolution", 0f)
        val power = intent.getFloatExtra("power", 0f)

        val detailsTextView = findViewById<TextView>(R.id.sensor_details)
        detailsTextView.text = """
            Nom: $sensorName
            Type: $sensorType
            Fabricant: $vendor
            Version: $version
            Portée Max: $maxRange
            Résolution: $resolution
            Consommation: ${power}mA
        """.trimIndent()
    }
}
