package com.harc.health.ui.therapeutic

import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.R
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TherapeuticPlayerScreen(
    session: TherapeuticSession,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val primaryColor = session.domain.color
    var isPlaying by remember { mutableStateOf(true) }
    var showScript by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(1L) }
    
    // Bio-Sync Engine State
    val infiniteTransition = rememberInfiniteTransition(label = "bio_sync")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(5000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "breath"
    )

    // Handle physical back button
    BackHandler(onBack = onBack)
    
    // Audio Playback Engine
    val mediaPlayer = remember { MediaPlayer() }
    
    LaunchedEffect(session.audioResName) {
        val resId = context.resources.getIdentifier(session.audioResName, "raw", context.packageName)
        if (resId != 0) {
            try {
                mediaPlayer.reset()
                val afd = context.resources.openRawResourceFd(resId)
                mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                mediaPlayer.isLooping = true
                mediaPlayer.prepare()
                if (isPlaying) mediaPlayer.start()
                duration = mediaPlayer.duration.toLong()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    DisposableEffect(Unit) { onDispose { mediaPlayer.release() } }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            mediaPlayer.start()
            while (isPlaying) {
                currentPosition = mediaPlayer.currentPosition.toLong()
                kotlinx.coroutines.delay(1000)
            }
        } else { mediaPlayer.pause() }
    }

    val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.player_session_header), style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp, color = primaryColor, fontWeight = FontWeight.Black)
                        Text(stringResource(session.titleRes).uppercase(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.Close, null, tint = Color.White) } },
                actions = {
                    IconButton(onClick = { showScript = !showScript }) {
                        Icon(if (showScript) Icons.Default.AutoGraph else Icons.Default.Description, null, tint = if (showScript) primaryColor else Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF080A0C)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Atmospheric Bio-Glow
            Canvas(modifier = Modifier.fillMaxSize().blur(100.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(listOf(primaryColor.copy(alpha = 0.1f), Color.Transparent), center = center, radius = (size.maxDimension / 2) * breathScale)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                LiveNeuralInstructionOverlay(session, isPlaying, primaryColor)

                // HIGH-FIDELITY VISUALIZER / SCRIPT
                Box(
                    modifier = Modifier.fillMaxWidth().height(320.dp).clip(RoundedCornerShape(32.dp)).background(Color.White.copy(alpha = 0.02f)).border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!showScript) {
                        BioSyncVisualizer(isPlaying, primaryColor, breathScale)
                    } else {
                        Text(
                            text = stringResource(session.descriptionRes), 
                            modifier = Modifier.padding(32.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // LIVE BIOLOGICAL FEED
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BioMetricMiniCard(Modifier.weight(1f), stringResource(R.string.label_frequency), stringResource(session.frequencyRes), primaryColor)
                    BioMetricMiniCard(Modifier.weight(1f), stringResource(R.string.label_intensity), "${(session.intensity * 100).toInt()}%", primaryColor)
                    BioMetricMiniCard(Modifier.weight(1f), stringResource(R.string.label_sync), if (isPlaying) stringResource(R.string.state_active) else stringResource(R.string.state_paused), primaryColor)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // INTELLIGENCE FEED (SCROLLING TEXT)
                IntelligenceFeedOverlay(isPlaying, stringResource(session.frequencyRes))

                Spacer(modifier = Modifier.height(40.dp))

                // PROGRESS & CONTROLS
                PlaybackEngine(
                    progress = progress,
                    isPlaying = isPlaying,
                    onToggle = { isPlaying = !isPlaying },
                    onSeek = { mediaPlayer.seekTo((it * duration).toInt()) },
                    accent = primaryColor
                )
                
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun LiveNeuralInstructionOverlay(session: TherapeuticSession, isPlaying: Boolean, primaryColor: Color) {
    val guidanceRes = session.guidanceRes ?: return
    val guidanceText = stringResource(guidanceRes)
    val instructions = remember(guidanceText) { 
        guidanceText.split(". ").filter { it.isNotBlank() }.map { it.trim().uppercase() + "." } 
    }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                kotlinx.coroutines.delay(12000) 
                currentIndex = (currentIndex + 1) % instructions.size
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        color = Color(0xFF12151A).copy(alpha = 0.8f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(primaryColor.copy(alpha = 0.1f), CircleShape)
                    .border(1.dp, primaryColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Hearing, null, tint = primaryColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.live_clinical_guidance),
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                AnimatedContent(
                    targetState = instructions[currentIndex],
                    transitionSpec = { 
                        (fadeIn(tween(800)) + slideInVertically(tween(800))).togetherWith(
                            fadeOut(tween(800)) + slideOutVertically(tween(800))
                        )
                    },
                    label = "instruction"
                ) { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 16.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun BioSyncVisualizer(isPlaying: Boolean, color: Color, scale: Float) {
    Box(contentAlignment = Alignment.Center) {
        // Outer Breath Anchor
        Canvas(modifier = Modifier.size(240.dp).graphicsLayer { scaleX = scale; scaleY = scale }) {
            drawCircle(color.copy(alpha = 0.05f))
            drawCircle(color.copy(alpha = 0.2f), style = Stroke(2.dp.toPx()))
        }
        
        // Inner Frequency Core
        Canvas(modifier = Modifier.size(120.dp)) {
            val path = Path()
            val centerY = size.height / 2
            val width = size.width
            path.moveTo(0f, centerY)
            for (x in 0..width.toInt()) {
                val y = centerY + sin(x * 0.1f + (if (isPlaying) System.currentTimeMillis() * 0.005f else 0f)) * 20f
                path.lineTo(x.toFloat(), y)
            }
            drawPath(path, color, style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
        }
        
        Text(
            text = if (scale > 1.0f) stringResource(R.string.label_exhale) else stringResource(R.string.label_inhale),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = color.copy(alpha = 0.6f),
            modifier = Modifier.offset(y = 100.dp)
        )
    }
}

@Composable
fun BioMetricMiniCard(modifier: Modifier, label: String, value: String, color: Color) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.02f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.6f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
fun IntelligenceFeedOverlay(isPlaying: Boolean, frequency: String) {
    val signals = listOf(
        stringResource(R.string.signal_sync_oscillations),
        stringResource(R.string.signal_target_nodes, frequency),
        stringResource(R.string.signal_reduce_hyperarousal),
        stringResource(R.string.signal_calibrate_vagal),
        stringResource(R.string.signal_optimize_balance)
    )
    var currentSignalIndex by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            kotlinx.coroutines.delay(4000)
            currentSignalIndex = (currentSignalIndex + 1) % signals.size
        }
    }

    AnimatedContent(
        targetState = signals[currentSignalIndex],
        transitionSpec = { fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically() },
        label = "signal"
    ) { signal ->
        Text(
            text = signal.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PlaybackEngine(progress: Float, isPlaying: Boolean, onToggle: () -> Unit, onSeek: (Float) -> Unit, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Slider(
            value = progress,
            onValueChange = onSeek,
            colors = SliderDefaults.colors(thumbColor = accent, activeTrackColor = accent, inactiveTrackColor = Color.White.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            IconButton(onClick = {}, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.Replay10, null, tint = Color.White.copy(alpha = 0.5f)) }
            
            Surface(
                onClick = onToggle,
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = accent,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, modifier = Modifier.size(40.dp), tint = Color.Black)
                }
            }

            IconButton(onClick = {}, modifier = Modifier.size(48.dp)) { Icon(Icons.Default.Forward30, null, tint = Color.White.copy(alpha = 0.5f)) }
        }
    }
}
