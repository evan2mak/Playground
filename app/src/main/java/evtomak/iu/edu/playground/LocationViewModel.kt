package evtomak.iu.edu.playground

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// LocationViewModel: Manages and exposes location-related data (city and state names).
class LocationViewModel : ViewModel() {
    private val _cityName = MutableStateFlow("Fetching...")
    private val _stateName = MutableStateFlow("Fetching...")

    val cityName: StateFlow<String> = _cityName
    val stateName: StateFlow<String> = _stateName

    // updateCityName: Updates the current city name in the flow.
    fun updateCityName(newCityName: String) {
        _cityName.value = newCityName
    }

    // updateStateName: Updates the current state name in the flow.
    fun updateStateName(newStateName: String) {
        _stateName.value = newStateName
    }
}
