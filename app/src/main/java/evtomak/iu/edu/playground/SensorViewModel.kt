package evtomak.iu.edu.playground

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// SensorViewModel: Manages and exposes sensor data (temperature and pressure) for the UI.
class SensorViewModel : ViewModel() {
    private val _temperature = MutableLiveData<Float>()
    val temperature: LiveData<Float> = _temperature

    private val _pressure = MutableLiveData<Float>()
    val pressure: LiveData<Float> = _pressure

    // updateTemperature: Updates the temperature LiveData.
    fun updateTemperature(newTemp: Float) {
        _temperature.postValue(newTemp)
    }

    // updatePressure: Updates the pressure LiveData.
    fun updatePressure(newPressure: Float) {
        _pressure.postValue(newPressure)
    }
}
