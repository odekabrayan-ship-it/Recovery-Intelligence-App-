package com.harc.health.logic

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

/**
 * BioSensorManager: High-fidelity hardware interface for Bio-State Verification.
 * Monitors Gyroscope for stability and Accelerometer for gait/isometric pulse.
 */
class BioSensorManager(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _stabilityScore = MutableStateFlow(1.0f)
    val stabilityScore = _stabilityScore.asStateFlow()

    private val _pulseIntensity = MutableStateFlow(0.0f)
    val pulseIntensity = _pulseIntensity.asStateFlow()

    private var isMonitoring = false

    fun startMonitoring() {
        if (isMonitoring) return
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        isMonitoring = true
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(this)
        isMonitoring = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                // Calculate rotation magnitude
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)
                // Stability is inverse to movement magnitude, normalized
                val stability = (1.0f - (magnitude / 2.0f)).coerceIn(0.0f, 1.0f)
                _stabilityScore.value = stability
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitude = sqrt(x * x + y * y + z * z)
                // Basic movement detection for pulse/gait
                _pulseIntensity.value = magnitude
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
