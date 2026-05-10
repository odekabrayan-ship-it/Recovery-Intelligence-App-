package com.harc.health.ui.vitalis

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

@Composable
fun BioFeedbackPPGView(
    onPulseDetected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    
    var redIntensity by remember { mutableStateOf(0f) }
    var pulseDetected by remember { mutableStateOf(false) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AndroidView<PreviewView>(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy: ImageProxy ->
                        val intensity = analyzeImage(imageProxy)
                        redIntensity = intensity
                        onPulseDetected(intensity)
                        imageProxy.close()
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis
                        )
                        // Enable torch for PPG
                        camera.cameraControl.enableTorch(true)
                    } catch (exc: Exception) {
                        Log.e("PPG", "Use case binding failed", exc)
                    }

                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.size(1.dp) // Keep it small/hidden as we only need analysis
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "PLACE FINGER OVER CAMERA",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
            // A simple visual indicator of "pulse" based on intensity changes
            // In a real app, this would be a more complex FFT or peak detection
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
private fun analyzeImage(imageProxy: ImageProxy): Float {
    val buffer = imageProxy.planes[0].buffer
    val data = ByteArray(buffer.remaining())
    buffer.get(data)
    
    // Average the red channel (first plane in YUV is Y, but we'll just take a sample)
    // For real PPG we need more precise RGB extraction if possible, 
    // but many Android cameras use YUV. We'll look at the luminance (Y) 
    // which changes with blood volume flow when the torch is on.
    var sum = 0L
    for (i in data.indices step 10) {
        sum += data[i].toInt() and 0xFF
    }
    val avg = sum.toFloat() / (data.size / 10)
    return avg
}
