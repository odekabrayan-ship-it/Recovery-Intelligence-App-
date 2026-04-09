package com.harc.health.ui.recovery

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.ui.theme.GreenRecovery
import kotlinx.coroutines.delay
import java.util.Locale

data class SoundSession(
    val id: String,
    val title: String,
    val durationMin: Int,
    val color: Color,
    val description: String,
    val impactFeedback: String
)

@Composable
fun SoundTherapyScreen(onSessionComplete: () -> Unit) {
    val sessions = listOf(
        SoundSession(
            "calm_session", 
            "Calm Session", 
            5, 
            Color(0xFF4CAF50), 
            "Gentle frequencies to soothe acute stress.",
            "Stress reduced! Cortisol decreased, emotional balance restored, focus enhanced."
        ),
        SoundSession(
            "sleep_session", 
            "Sleep Session", 
            15, 
            Color(0xFF3F51B5), 
            "Delta-wave stimulation for deep biological repair.",
            "Sleep quality enhanced! REM sleep improved, morning fatigue reduced."
        ),
        SoundSession(
            "focus_session", 
            "Focus Session", 
            10, 
            Color(0xFF00BCD4), 
            "Alpha-wave patterns to restore mental clarity.",
            "Focus restored! Anxiety decreased, mental clarity improved."
        )
    )

    var activeSession by remember { mutableStateOf<SoundSession?>(null) }
    var showImpactFeedback by remember { mutableStateOf<String?>(null) }

    if (showImpactFeedback != null) {
        ImpactFeedbackOverlay(feedback = showImpactFeedback!!, onDismiss = {
            showImpactFeedback = null
            onSessionComplete()
        })
    } else if (activeSession != null) {
        PlayerInterface(
            session = activeSession!!, 
            onBack = { activeSession = null }, 
            onComplete = {
                showImpactFeedback = activeSession!!.impactFeedback
                activeSession = null
            }
        )
    } else {
        SessionList(sessions = sessions, onSelect = { activeSession = it })
    }
}

@Composable
fun SessionList(sessions: List<SoundSession>, onSelect: (SoundSession) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Therapeutic Sound", 
                style = MaterialTheme.typography.headlineMedium, 
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Neuro-stimulation sessions designed to restore emotional and physiological balance.", 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(sessions) { session ->
            Card(
                onClick = { onSelect(session) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = session.color.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (session.id == "sleep_session") Icons.Default.NightsStay else Icons.Default.GraphicEq, 
                                contentDescription = null, 
                                tint = session.color
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(
                            text = session.title, 
                            style = MaterialTheme.typography.titleMedium, 
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${session.durationMin} min • ${session.description}", 
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerInterface(session: SoundSession, onBack: () -> Unit, onComplete: () -> Unit) {
    var isPlaying by remember { mutableStateOf(true) }
    var secondsRemaining by remember { mutableIntStateOf(session.durationMin * 60) }

    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )

    LaunchedEffect(isPlaying) {
        while (isPlaying && secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
        if (secondsRemaining == 0) onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A1A1A), Color.Black))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Relaxation Sound Session", 
                color = Color.White.copy(alpha = 0.6f), 
                style = MaterialTheme.typography.labelLarge,
                letterSpacing = 2.sp
            )
            Text(
                text = session.title, 
                color = Color.White, 
                fontSize = 32.sp, 
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(64.dp))

            // Pulsing visualizer circle
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size(240.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale),
                    shape = CircleShape,
                    color = session.color.copy(alpha = 0.15f)
                ) {}
                Surface(
                    modifier = Modifier
                        .size(180.dp),
                    shape = CircleShape,
                    color = session.color.copy(alpha = 0.3f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = String.format(Locale.getDefault(), "%02d:%02d", secondsRemaining / 60, secondsRemaining % 60),
                            color = Color.White,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Light,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.5f))
                }
                
                FilledIconButton(
                    onClick = { isPlaying = !isPlaying }, 
                    modifier = Modifier.size(80.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = session.color)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(onClick = onComplete) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Skip", tint = Color.White.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
fun ImpactFeedbackOverlay(feedback: String, onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.padding(32.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.CheckCircle, 
                contentDescription = null, 
                tint = GreenRecovery, 
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Session Complete",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = feedback,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenRecovery),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Continue Recovery", fontWeight = FontWeight.Bold)
            }
        }
    }
}
