package evtomak.iu.edu.playground

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SensorViewModel : ViewModel() {
    private val _temperature = MutableLiveData<Float>()
    val temperature: LiveData<Float> = _temperature

    private val _pressure = MutableLiveData<Float>()
    val pressure: LiveData<Float> = _pressure

    fun updateTemperature(newTemp: Float) {
        _temperature.postValue(newTemp)
    }

    fun updatePressure(newPressure: Float) {
        _pressure.postValue(newPressure)
    }
}
