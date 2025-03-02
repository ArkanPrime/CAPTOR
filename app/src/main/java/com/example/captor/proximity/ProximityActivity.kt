package com.example.captor.proximity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.captor.R

class ProximityActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null

    private lateinit var proximityImage: ImageView
    private lateinit var proximityText: TextView
    private lateinit var distanceText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proximity)

        proximityImage = findViewById(R.id.proximity_image)
        proximityText = findViewById(R.id.proximity_text)
        distanceText = findViewById(R.id.distance_text)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        } ?: run {
            proximityText.text = "Capteur de proximité non disponible"
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val distance = it.values[0] // Distance détectée par le capteur

            // Mise à jour de l'affichage
            distanceText.text = "Distance : ${distance} cm"

            if (distance < (5.0f))
            {
                proximityImage.setImageResource(R.drawable.near)
                proximityText.text = "Objet proche"
            } else {
                proximityImage.setImageResource(R.drawable.far_from_screen)
                proximityText.text = "Objet éloigné"
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}
