package evtomak.iu.edu.playground

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

val LightGreen = ComposeColor(0xFF90EE90)

fun Double.toDegrees(): Double {
    return this * 180 / Math.PI
}

class GestureActivity : ComponentActivity() {

    private lateinit var ballImageView: ImageView
    private lateinit var ballBitmap: Bitmap
    private lateinit var ballCanvas: Canvas
    private val ballMatrix = Matrix()
    private val ballStartPosition = PointF(100f, 150f)
    private var isDragging = false
    private var lastTapTime: Long = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var gestureLog by remember { mutableStateOf(emptyList<String>()) }
            val ballPosition = remember { mutableStateOf(ballStartPosition) }

            val configuration = LocalConfiguration.current

            // Determine the appropriate orientation based on the device orientation
            val isVertical = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

            val density = LocalDensity.current.density
            val screenWidth = LocalConfiguration.current.screenWidthDp * density
            val screenHeight = LocalConfiguration.current.screenHeightDp * density

            if (isVertical) {
                // Vertical layout
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(LightGreen)
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, _, _ ->
                                    if (!isDragging) {
                                        // Initialize the initialTouchOffset on the first drag
                                        isDragging = true
                                    }

                                    // Calculate the new position based on the drag
                                    ballMatrix.reset()

                                    // Adjust the sensitivity by multiplying with a scaling factor
                                    val sensitivity = .25f // You can adjust this value
                                    val adjustedPan = Offset(
                                        pan.x * sensitivity,
                                        pan.y * sensitivity
                                    )

                                    val newX = ballPosition.value.x + adjustedPan.x
                                    val newY = ballPosition.value.y + adjustedPan.y

                                    // Constrain the ball within the screen boundaries
                                    val constrainedX = newX.coerceIn(0f, screenWidth)
                                    val constrainedY = newY.coerceIn(0f, screenHeight)

                                    ballMatrix.postTranslate(
                                        constrainedX,
                                        constrainedY
                                    )

                                    // Calculate the angle of the drag gesture
                                    val angle =
                                        Math.atan2(pan.y.toDouble(), pan.x.toDouble()).toDegrees()

                                    // Determine the direction based on the angle
                                    val direction = when {
                                        angle >= -22.5 && angle < 22.5 -> "right"
                                        angle >= 22.5 && angle < 67.5 -> "bottom-right"
                                        angle >= 67.5 && angle < 112.5 -> "bottom"
                                        angle >= 112.5 && angle < 157.5 -> "bottom-left"
                                        angle >= 157.5 || angle < -157.5 -> "left"
                                        angle >= -157.5 && angle < -112.5 -> "top-left"
                                        angle >= -112.5 && angle < -67.5 -> "top"
                                        angle >= -67.5 && angle < -22.5 -> "top-right"
                                        else -> ""
                                    }

                                    Log.d("GestureActivity", "Pan offset: $pan")
                                    Log.d("GestureActivity", "Direction: $direction")

                                    // Add the log entry based on the direction
                                    if (direction.isNotEmpty() && isDragging) {
                                        gestureLog =
                                            gestureLog + "You moved the ball to the $direction."
                                        Log.d("GestureActivity", "Gesture Log Updated: $gestureLog")
                                    }

                                    // Update the ball position on the ImageView
                                    ballImageView.imageMatrix = ballMatrix
                                    // Update the ball position in the state
                                    ballPosition.value = PointF(
                                        constrainedX,
                                        constrainedY
                                    )
                                }
                            }
                    ) {
                        ballImageView = remember { ImageView(this@GestureActivity) }
                        ballBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
                        ballCanvas = Canvas(ballBitmap)
                        val ballRadius = 10f
                        ballCanvas.drawCircle(
                            ballRadius,
                            ballRadius,
                            ballRadius,
                            Paint().apply { color = Color.RED }
                        )

                        ballImageView.setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastTapTime < 500) {
                                    gestureLog = gestureLog + "You double tapped."
                                    Log.d("GestureActivity", "Gesture Log Updated: $gestureLog")
                                }
                                lastTapTime = currentTime
                            }
                            false
                        }

                        // Place the ImageView inside the Box
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Gesture playground",
                                color = ComposeColor.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                            AndroidView(
                                factory = { ballImageView },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Draw the ball on the Canvas
                                ballCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
                                ballCanvas.drawCircle(
                                    ballPosition.value.x,
                                    ballPosition.value.y,
                                    ballRadius,
                                    Paint().apply { color = Color.RED }
                                )
                                ballImageView.setImageBitmap(ballBitmap)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(ComposeColor.Gray)
                    ) {
                        // Log of gestures goes here
                        gestureLog.reversed().forEach { logEntry ->
                            Log.d("GestureActivity", "Displaying Log Entry: $logEntry")
                            Text(
                                text = logEntry,
                                color = ComposeColor.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            } else {
                // Horizontal layout
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(LightGreen)
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, _, _ ->
                                    if (!isDragging) {
                                        // Initialize the initialTouchOffset on the first drag
                                        isDragging = true
                                    }

                                    // Calculate the new position based on the drag
                                    ballMatrix.reset()

                                    // Adjust the sensitivity by multiplying with a scaling factor
                                    val sensitivity = .25f // You can adjust this value
                                    val adjustedPan = Offset(
                                        pan.x * sensitivity,
                                        pan.y * sensitivity
                                    )

                                    val newX = ballPosition.value.x + adjustedPan.x
                                    val newY = ballPosition.value.y + adjustedPan.y

                                    // Constrain the ball within the screen boundaries
                                    val constrainedX = newX.coerceIn(0f, screenWidth)
                                    val constrainedY = newY.coerceIn(0f, screenHeight)

                                    ballMatrix.postTranslate(
                                        constrainedX,
                                        constrainedY
                                    )

                                    // Calculate the angle of the drag gesture
                                    val angle =
                                        Math.atan2(pan.y.toDouble(), pan.x.toDouble()).toDegrees()

                                    // Determine the direction based on the angle
                                    val direction = when {
                                        angle >= -22.5 && angle < 22.5 -> "right"
                                        angle >= 22.5 && angle < 67.5 -> "bottom-right"
                                        angle >= 67.5 && angle < 112.5 -> "bottom"
                                        angle >= 112.5 && angle < 157.5 -> "bottom-left"
                                        angle >= 157.5 || angle < -157.5 -> "left"
                                        angle >= -157.5 && angle < -112.5 -> "top-left"
                                        angle >= -112.5 && angle < -67.5 -> "top"
                                        angle >= -67.5 && angle < -22.5 -> "top-right"
                                        else -> ""
                                    }

                                    Log.d("GestureActivity", "Pan offset: $pan")
                                    Log.d("GestureActivity", "Direction: $direction")

                                    // Add the log entry based on the direction
                                    if (direction.isNotEmpty() && isDragging) {
                                        gestureLog =
                                            gestureLog + "You moved the ball to the $direction."
                                        Log.d("GestureActivity", "Gesture Log Updated: $gestureLog")
                                    }

                                    // Update the ball position on the ImageView
                                    ballImageView.imageMatrix = ballMatrix
                                    // Update the ball position in the state
                                    ballPosition.value = PointF(
                                        constrainedX,
                                        constrainedY
                                    )
                                }
                            }
                    ) {
                        ballImageView = remember { ImageView(this@GestureActivity) }
                        ballBitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
                        ballCanvas = Canvas(ballBitmap)
                        val ballRadius = 10f
                        ballCanvas.drawCircle(
                            ballRadius,
                            ballRadius,
                            ballRadius,
                            Paint().apply { color = Color.RED }
                        )

                        ballImageView.setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastTapTime < 500) {
                                    gestureLog = gestureLog + "You double tapped."
                                    Log.d("GestureActivity", "Gesture Log Updated: $gestureLog")
                                }
                                lastTapTime = currentTime
                            }
                            false
                        }

                        // Place the ImageView inside the Box
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Gesture playground",
                                color = ComposeColor.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                            AndroidView(
                                factory = { ballImageView },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Draw the ball on the Canvas
                                ballCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
                                ballCanvas.drawCircle(
                                    ballPosition.value.x,
                                    ballPosition.value.y,
                                    ballRadius,
                                    Paint().apply { color = Color.RED }
                                )
                                ballImageView.setImageBitmap(ballBitmap)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .background(ComposeColor.Gray)
                    ) {
                        // Log of gestures goes here
                        gestureLog.reversed().forEach { logEntry ->
                            Log.d("GestureActivity", "Displaying Log Entry: $logEntry")
                            Text(
                                text = logEntry,
                                color = ComposeColor.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
