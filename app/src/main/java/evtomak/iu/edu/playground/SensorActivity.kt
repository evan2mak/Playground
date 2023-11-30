package evtomak.iu.edu.playground

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import java.util.*

class SensorActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private val sensorViewModel by viewModels<SensorViewModel>()
    private val locationViewModel by viewModels<LocationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Checking and requesting location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SensorActivity", "Requesting location permissions")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            return
        }
        else {
            Log.d("SensorActivity", "Location permissions granted")
        }

        // Initialize sensor manager and register sensor listeners
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)?.also { temperatureSensor ->
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)?.also { pressureSensor ->
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Fetch and update location information
        fetchLocation()

        // Set the UI content
        setContent {
            SensorScreen(sensorViewModel, this@SensorActivity, locationViewModel)
        }
    }

    private fun fetchLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationProvider = LocationManager.NETWORK_PROVIDER

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Check if the network provider is enabled
            if (locationManager.isProviderEnabled(locationProvider)) {
                val location = locationManager.getLastKnownLocation(locationProvider)

                // Check if the last known location is available
                if (location != null) {
                    updateLocationInfo(location)
                }
                else {
                    // Request location updates
                    locationManager.requestLocationUpdates(
                        locationProvider,
                        0,
                        0f,
                        object : LocationListener {
                            override fun onLocationChanged(newLocation: Location) {
                                // Once a new location is received, update the location info and stop listening for updates
                                updateLocationInfo(newLocation)
                                locationManager.removeUpdates(this)
                            }

                            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                                // Handle status changes if needed
                            }

                            override fun onProviderEnabled(provider: String) {
                                // Handle provider enabled if needed
                            }

                            override fun onProviderDisabled(provider: String) {
                                // Handle provider disabled if needed
                            }
                        }
                    )
                }
            } else {
                Log.e("SensorActivity", "Network provider is not enabled")
            }
        } else {
            Log.e("SensorActivity", "Location permissions not granted")
        }
    }

    private fun updateLocationInfo(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val cityName = address.locality ?: "Unknown"
                    val stateName = address.adminArea ?: "Unknown"

                    // Log the fetched city and state
                    Log.d("SensorActivity", "City: $cityName, State: $stateName")

                    locationViewModel.updateCityName(cityName)
                    locationViewModel.updateStateName(stateName)
                }
            }
        } catch (e: Exception) {
            Log.e("SensorActivity", "Error fetching location: ${e.message}")
        }
    }




    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                sensorViewModel.updateTemperature(event.values[0])
            }
            Sensor.TYPE_PRESSURE -> {
                sensorViewModel.updatePressure(event.values[0])
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not needed for this implementation
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}

@Composable
fun SensorScreen(sensorViewModel: SensorViewModel, activity: Activity, locationViewModel: LocationViewModel) {
    val temperature by sensorViewModel.temperature.observeAsState(initial = 0f)
    val pressure by sensorViewModel.pressure.observeAsState(initial = 0f)
    val cityName = locationViewModel.cityName.collectAsState()
    val stateName = locationViewModel.stateName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sensors Playground",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Name: Evan Tomak", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Display the fetched city and state
        Text(text = "Location:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "City: ${cityName.value}")
        Text(text = "State: ${stateName.value}")

        // Display the sensor data
        Text(text = "Sensor Data:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "Temperature: $temperature °C")
        Text(text = "Air Pressure: $pressure hPa")

        Button(onClick = {
            activity.startActivity(Intent(activity, GestureActivity::class.java))
        }) {
            Text(text = "GESTURE PLAYGROUND")
        }
    }
}
