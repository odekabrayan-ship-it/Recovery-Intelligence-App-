package com.harc.health.ui.matrix

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.logic.MatrixEngine
import kotlinx.coroutines.delay

enum class TransitionMode { CLOSING, OPENING }

@Composable
fun MatrixTransitionScreen(
    mode: TransitionMode,
    dayNumber: Int,
    metrics: MatrixMetrics,
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    val day = MatrixEngine.getDirectiveForDay(dayNumber)
    val nextDay = MatrixEngine.getDirectiveForDay(dayNumber + 1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = step, label = "transition_step") { currentStep ->
            when (mode) {
                TransitionMode.CLOSING -> ClosingFlow(currentStep, day, nextDay) { step++ }
                TransitionMode.OPENING -> OpeningFlow(currentStep, day, nextDay, metrics) { step++ }
            }
        }
    }

    // Handle completion
    LaunchedEffect(step) {
        val maxSteps = if (mode == TransitionMode.CLOSING) 4 else 6
        if (step > maxSteps) {
            onComplete()
        }
    }
}

@Composable
private fun ClosingFlow(step: Int, day: com.harc.health.model.MatrixDay, nextDay: com.harc.health.model.MatrixDay, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (step) {
            1 -> {
                Text("DAY ${day.dayNumber} COMPLETE", color = Color.Gray, letterSpacing = 4.sp, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Text(day.closingReflection, color = Color.White, textAlign = TextAlign.Center, fontSize = 18.sp, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(48.dp))
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("REFLECT", color = Color.Black)
                }
            }
            2 -> {
                Text("RECOGNITION", color = Color.Red, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text("The system has logged your behavioral reality. You are moving from unconscious repetition to conscious observation.", color = Color.LightGray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(48.dp))
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("CONTINUE", color = Color.Black)
                }
            }
            3 -> {
                Text("PROGRESSION", color = Color.Gray, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Text("You are evolving. The loop is weakening as your awareness strengthens.", color = Color.White, textAlign = TextAlign.Center, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(48.dp))
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("NEXT", color = Color.Black)
                }
            }
            4 -> {
                Text("OVERNIGHT TENSION", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 10.sp, letterSpacing = 4.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Text(day.nextDayPreview, color = Color.White, textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Light)
                Spacer(modifier = Modifier.height(64.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("PREPARE FOR TOMORROW", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun OpeningFlow(step: Int, day: com.harc.health.model.MatrixDay, nextDay: com.harc.health.model.MatrixDay, metrics: MatrixMetrics, onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (step) {
            1 -> {
                Text("DAY ${day.dayNumber}", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 32.sp, letterSpacing = 8.sp)
                Text(day.phase.name.replace("_", " "), color = Color.Gray, fontSize = 12.sp, letterSpacing = 4.sp)
                Spacer(modifier = Modifier.height(48.dp))
                Text(day.openingBridge, color = Color.White, textAlign = TextAlign.Center, fontSize = 18.sp, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(64.dp))
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("ENTER CONSCIOUSLY", color = Color.Black)
                }
            }
            2 -> {
                Text("THE PSYCHOLOGICAL BRIDGE", color = Color.Gray, fontSize = 10.sp, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Text("Every day is a step deeper into the system. You are not just doing tasks; you are re-wiring your neural architecture.", color = Color.White, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(48.dp))
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("UNDERSTOOD", color = Color.Black)
                }
            }
            3 -> {
                Text("WHAT HAS CHANGED?", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Your awareness score is ${metrics.awarenessScore}%. Your interruption speed is ${metrics.interruptionSpeed}. Identity transformation in progress.", color = Color.LightGray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(48.dp))
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("CONTINUE EVOLUTION", color = Color.Black)
                }
            }
            4 -> {
                Text("THE NEW THREAT", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 4.sp)
                Spacer(modifier = Modifier.height(32.dp))
                Text(day.newThreat, color = Color.White, textAlign = TextAlign.Center, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(48.dp))
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("FACE IT", color = Color.Black)
                }
            }
            5 -> {
                Text("TODAY'S OBJECTIVE", color = Color.Gray, fontSize = 10.sp, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(day.objective, color = Color.White, textAlign = TextAlign.Center, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(48.dp))
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), shape = RoundedCornerShape(4.dp)) {
                    Text("I ACCEPT THE MISSION", fontWeight = FontWeight.Black)
                }
            }
            6 -> {
                val daysLeft = 30 - day.dayNumber
                Text("$daysLeft DAYS UNTIL EXIT", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraLight, letterSpacing = 4.sp)
                Spacer(modifier = Modifier.height(64.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("BEGIN DAY ${day.dayNumber}", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
