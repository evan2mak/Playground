# C323 Project 10 - Gesture Playground App: Evan Tomak

This project is an app that has two screens, a sensor screen and a gestures screen. The sensor screen shows the user the name of the developer (me), their location (city and state), and sensor data (temperature and air pressure). 

Note: this sensor is for an emulator, so the city and location being fetched show up as the headquarters of Google by default for the emulator, which is Mountain View, California. I could not get the location to change in the extended controls, so in the video below the location is showing as Mountain View, California. 

The temperature also may be 0.0 degrees Celsius by default. It will fetch the correct temperature if you change the value in the extended controls menu. 

The functionality is described in more detail below:

## Sensor Screen

[X] The first screen the user sees is the sensor screen.

[X] The sensor screen shows the user the title of the screen, the name of the developer, the city and state that they are in, and the temperature and air pressure where they are.

[X] If the user clicks the "Gesture Playground" button, they will navigate to the Gesture Screen.

## Gesture Screen

[X] The gesture screen is the gesture playground.

[X] The user can drag a red ball across a screen, and the location of the ball is updated below in a log list.

[X] The log list shows the direction that the ball is currently being dragged in (top, top-left, top-right, bottom, bottom-left, bottom-right, left, or right).

[X] The log list will also show when the user double taps the screen.

[X] In vertical orientation, the red ball activity takes up the top portion of the screen, and the log list takes up the bottom portion of the screen.

[X] In horizontal orientation, the red ball activity takes up the left portion of the screen, and the log list takes up the right portion of the screen.

## 

The following functions/extensions are implemented:

## SensorActivity

Manages sensor data and location updates.

onCreate:

Initializes the activity, checks permissions, and sets up sensors and location fetching.

fetchLocation:

Fetches the current location and updates the UI.

updateLocationInfo: 

Updates location information using geocoding.

onSensorChanged: 

Handles sensor data changes and updates the UI.

onDestroy: 

Unregisters the sensor listener when the activity is destroyed.

SensorScreen: 

Composable function to display sensor data and location information.

## SensorViewModel

Manages and exposes sensor data (temperature and pressure) for the UI.

updateTemperature: 

Updates the temperature LiveData.

updatePressure: 

Updates the pressure LiveData.

## GestureActivity

Manages interactive gestures and provides a playground for gesture-based interactions.

onCreate:

Sets up the gesture handling UI and initializes the ball movement logic.

## LocationViewModel

Manages and exposes location-related data (city and state names).

updateCityName: 

Updates the current city name in the flow.

updateStateName: 

Updates the current state name in the flow.

## Video Walkthroughs

Here's a walkthrough of implemented user stories:

Sensor Screen: This clip shows the sensor screen. It shows the default location of the emulator (Google's headquarters - Mountain View, California), the temperature, and the air pressure. 
![My Video-3](https://github.com/evan2mak/Playground/assets/128643914/f8716867-8f7a-4a87-9b97-056313a17846)

Gesture Screen: This clip shows how the gesture screen works. It shows the ball getting dragged with the corresponding logs. Shows both orientations as well and the log of a double tap.
![My Video-4](https://github.com/evan2mak/Playground/assets/128643914/43bab573-d886-4549-8c2e-428917259ca4)


## Notes

The hardest part of this project was figuring out how to drag the ball. I had to do this sensitivity workaround because I couldn't get the drag quite right, but works for the most part. The logs were also pretty annoying with the angle of the drag. Otherwise not too bad!

## License

    Copyright 2023 Evan Tomak.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express implied.

    See the License for the specific language governing permissions and
    limitations under the License.

