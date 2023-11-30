package evtomak.iu.edu.playground

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel : ViewModel() {
    private val _cityName = MutableStateFlow("Fetching...")
    private val _stateName = MutableStateFlow("Fetching...")

    val cityName: StateFlow<String> = _cityName
    val stateName: StateFlow<String> = _stateName

    fun updateCityName(newCityName: String) {
        _cityName.value = newCityName
    }

    fun updateStateName(newStateName: String) {
        _stateName.value = newStateName
    }
}
