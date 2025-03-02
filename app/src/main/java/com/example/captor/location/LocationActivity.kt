package com.example.captor.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.captor.R
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.util.Locale

class LocationActivity : AppCompatActivity() {

    private lateinit var locationText: TextView
    private lateinit var addressText: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        locationText = findViewById(R.id.location_text)
        addressText = findViewById(R.id.address_text)
        mapView = this.findViewById(R.id.map_view)

        // Configuration OSM
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        // Initialiser le client de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialiser le callback de localisation
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { updateLocationUI(it) }
            }
        }

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Mise à jour toutes les 5 secondes
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @SuppressLint("SetTextI18n")
    private fun updateLocationUI(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        locationText.text = "Latitude: $latitude\nLongitude: $longitude"

        getAddress(latitude, longitude)

        // Mettre à jour la carte avec un marqueur
        val userLocation = GeoPoint(latitude, longitude)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(userLocation)

        val marker = Marker(mapView)
        marker.position = userLocation
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Ma position"
        mapView.overlays.clear()
        mapView.overlays.add(marker)
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                addressText.text = "Adresse: ${address.getAddressLine(0)}"
            } else {
                addressText.text = "Adresse non disponible"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            addressText.text = "Erreur lors de la récupération de l'adresse"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        mapView.onDetach()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}
