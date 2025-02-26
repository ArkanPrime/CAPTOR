package com.example.captor.flash

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.captor.R

class FlashActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var flashButton: ImageButton
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isFlashOn = false

    private val shakeThreshold = 15f // Seuil de détection d'une secousse
    private var shakeCount = 0
    private val shakeResetTime = 1000L // Temps en ms pour compter les secousses
    private val cooldownTime = 500L // Temps d'attente avant de réactiver la détection
    private var canDetectShake = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash)

        flashButton = findViewById(R.id.flash_button)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        try {
            cameraId = cameraManager.cameraIdList[0] // Récupère l'ID de la caméra arrière
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        flashButton.setOnClickListener {
            toggleFlash()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!canDetectShake) return // Bloquer la détection pendant le cooldown

        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            // Calcul de l'accélération totale
            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

            // Détection d'une secousse si l'accélération dépasse le seuil
            if (acceleration > shakeThreshold) {
                shakeCount++

                // Si c'est la première secousse, on lance le timer pour compter les secousses
                if (shakeCount == 1) {
                    handler.postDelayed({
                        if (shakeCount >= 3) { // Seuil de 3 secousses en 1 seconde
                            toggleFlash()
                            canDetectShake = false // Désactiver la détection pendant un cooldown
                            handler.postDelayed({ canDetectShake = true }, cooldownTime)
                        }
                        shakeCount = 0 // Réinitialisation du compteur
                    }, shakeResetTime)
                }
            }
        }
    }

    private fun toggleFlash() {
        isFlashOn = !isFlashOn
        try {
            cameraId?.let {
                cameraManager.setTorchMode(it, isFlashOn)
            }
            updateButtonUI()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun updateButtonUI() {
        if (isFlashOn) {
            flashButton.setImageResource(R.drawable.ic_flash_on) // Icône allumée
        } else {
            flashButton.setImageResource(R.drawable.ic_flash_off) // Icône éteinte
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        handler.removeCallbacksAndMessages(null)
    }
}
