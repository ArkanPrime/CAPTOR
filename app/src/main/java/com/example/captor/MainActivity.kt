package com.example.captor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.captor.sensorList.SensorListActivity
import com.example.captor.sensorCheck.SensorCheckActivity
import com.example.captor.accelerometer.AccelerometerActivity
import com.example.captor.direction.DirectionActivity
import com.example.captor.flash.FlashActivity
import com.example.captor.location.LocationActivity
import com.example.captor.proximity.ProximityActivity



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnShowSensors = findViewById<Button>(R.id.btn_show_sensors)
        val btnCheckSensors = findViewById<Button>(R.id.btn_check_sensors)
        val btnAccelerometer = findViewById<Button>(R.id.btn_accelerometer)
        val btnDirection = findViewById<Button>(R.id.btn_direction)
        val btnFlash = findViewById<Button>(R.id.btn_flash)
        val btnProximity = findViewById<Button>(R.id.btn_proximity)
        val btnLocation = findViewById<Button>(R.id.btn_location)


        btnShowSensors.setOnClickListener {
            startActivity(Intent(this, SensorListActivity::class.java))
        }

        btnCheckSensors.setOnClickListener {
            startActivity(Intent(this, SensorCheckActivity::class.java))
        }

        btnAccelerometer.setOnClickListener {
            startActivity(Intent(this, AccelerometerActivity::class.java))
        }

        btnDirection.setOnClickListener {
            startActivity(Intent(this, DirectionActivity::class.java))
        }

        btnFlash.setOnClickListener {
            startActivity(Intent(this, FlashActivity::class.java))
        }
        btnProximity.setOnClickListener {
            startActivity(Intent(this, ProximityActivity::class.java))
        }
        btnLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }
    }
}
