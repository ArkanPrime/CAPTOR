package com.example.captor.direction

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.captor.R

class DirectionActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var linearAccelerometer: Sensor? = null

    private lateinit var directionText: TextView
    private lateinit var countdownText: TextView
    private lateinit var directionArrow: ImageView
    private lateinit var vibrator: Vibrator

    private var lastDirection: String = "Aucun mouvement"
    private var canDetectMovement = true
    private val resetHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direction)

        directionText = findViewById(R.id.direction_text)
        countdownText = findViewById(R.id.countdown_text)
        directionArrow = findViewById(R.id.direction_arrow)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        linearAccelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!canDetectMovement) return // Bloquer la détection pendant le cooldown

        event?.let {
            val x = it.values[0] // Mouvement gauche/droite
            val y = it.values[1] // Mouvement haut/bas

            // Détecter la direction du mouvement
            val newDirection = when {
                x > 1.5 -> "Droite"
                x < -1.5 -> "Gauche"
                y > 1.5 -> "Haut"
                y < -1.5 -> "Bas"
                else -> lastDirection
            }

            if (newDirection != lastDirection) {
                lastDirection = newDirection
                canDetectMovement = false // Bloquer temporairement la détection
                updateDirectionUI(newDirection)
                vibrate()

                // Démarrer le compte à rebours de 1.5 secondes
                startCountdown(1.5f)
            }
        }
    }

    private fun updateDirectionUI(direction: String) {
        directionText.text = direction

        val color = when (direction) {
            "Gauche" -> Color.BLUE
            "Droite" -> Color.YELLOW
            "Haut" -> Color.GREEN
            "Bas" -> Color.RED
            else -> Color.GRAY
        }
        directionText.setTextColor(color)

        val rotation = when (direction) {
            "Gauche" -> 180f
            "Droite" -> 0f
            "Haut" -> -90f
            "Bas" -> 90f
            else -> 0f
        }
        directionArrow.rotation = rotation
    }

    private fun startCountdown(seconds: Float) {
        var remainingTime = seconds

        // Rendre le texte visible
        countdownText.text = "${remainingTime.format(2)} sec"
        countdownText.visibility = TextView.VISIBLE

        val countdownRunnable = object : Runnable {
            override fun run() {
                if (remainingTime > 0) {
                    remainingTime -= 0.1f // Décrémenter toutes les 100ms
                    countdownText.text = "${remainingTime.format(2)} sec"
                    resetHandler.postDelayed(this, 100)
                } else {
                    canDetectMovement = true
                    resetDirection()
                }
            }
        }

        resetHandler.post(countdownRunnable)
    }

    private fun resetDirection() {
        lastDirection = "Aucun mouvement"
        updateDirectionUI("Aucun mouvement")
        countdownText.visibility = TextView.INVISIBLE // Masquer le timer après expiration
    }

    private fun vibrate() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(100)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        resetHandler.removeCallbacksAndMessages(null)
    }

    // Extension pour formater les décimales
    private fun Float.format(digits: Int) = "%.${digits}f".format(this)
}
