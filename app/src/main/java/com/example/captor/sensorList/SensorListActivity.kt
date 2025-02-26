package com.example.captor.sensorList

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.captor.R

class SensorListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_list)

        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        val listView = findViewById<ListView>(R.id.list_sensors)
        val sensorNames = sensorList.map { it.name }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sensorNames)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedSensor = sensorList[position]

            val intent = Intent(this, SensorDetailActivity::class.java).apply {
                putExtra("sensorName", selectedSensor.name)
                putExtra("sensorType", selectedSensor.type)
                putExtra("vendor", selectedSensor.vendor)
                putExtra("version", selectedSensor.version)
                putExtra("maxRange", selectedSensor.maximumRange)
                putExtra("resolution", selectedSensor.resolution)
                putExtra("power", selectedSensor.power)
            }
            startActivity(intent)
        }
    }
}
