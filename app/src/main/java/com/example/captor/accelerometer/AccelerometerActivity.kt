package com.example.captor.accelerometer

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.captor.R
import java.util.LinkedList

class AccelerometerActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var linearAccelerometer: Sensor? = null

    private lateinit var backgroundLayout: RelativeLayout
    private lateinit var textValues: TextView

    private val accelerationValues = LinkedList<Float>() // Liste des dernières valeurs
    private val maxValuesStored = 10 // Nombre de valeurs à conserver pour la moyenne

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accelerometer)

        backgroundLayout = findViewById(R.id.accelerometer_layout)
        textValues = findViewById(R.id.text_accelerometer)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        linearAccelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            // Ajouter la nouvelle valeur à la liste
            accelerationValues.add(acceleration)

            // Limiter la taille de la liste à 10 valeurs
            if (accelerationValues.size > maxValuesStored) {
                accelerationValues.removeFirst()
            }

            // Calculer la moyenne des X dernières valeurs
            val averageAcceleration = accelerationValues.average().toFloat()

            // Affichage des valeurs et de la moyenne
            textValues.text = """
                Accélération linéaire :
                X: ${x.format(2)}
                Y: ${y.format(2)}
                Z: ${z.format(2)}
                Total: ${acceleration.format(2)}
                Moyenne (10 dernières): ${averageAcceleration.format(2)}
            """.trimIndent()

            // Seuils basés sur la moyenne pour des transitions plus fluides
            val newColor = when {
                averageAcceleration < 1.5 -> Color.GREEN   // Immobile
                averageAcceleration in 1.5..5.5 -> Color.BLACK // Mouvement normal plus visible
                else -> Color.RED   // Secousse forte
            }

            // Animation plus fluide
            animateBackgroundColor(newColor, 300)
        }
    }

    private fun animateBackgroundColor(newColor: Int, duration: Long) {
        val colorFrom = (backgroundLayout.background as? android.graphics.drawable.ColorDrawable)?.color ?: Color.WHITE
        val colorTo = newColor

        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = duration // Animation fluide
        colorAnimation.addUpdateListener { animator ->
            backgroundLayout.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun Float.format(digits: Int) = "%.${digits}f".format(this)
}
