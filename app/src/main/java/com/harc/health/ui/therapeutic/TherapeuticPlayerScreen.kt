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
import com.harc.health.logic.VitalisEngine
import com.harc.health.ui.vitalis.LongevityCelebrationDialog
import com.harc.health.viewmodel.MainViewModel
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TherapeuticPlayerScreen(
    session: TherapeuticSession,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val primaryColor = session.domain.color
    var isPlaying by remember { mutableStateOf(true) }
    var showScript by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(1L) }
    var showCelebration by remember { mutableStateOf<String?>(null) }
    
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
                
                Spacer(modifier = Modifier.height(40.dp))

                // Professional Session Completion Action
                Button(
                    onClick = { 
                        viewModel.completeProtocol(session.id)
                        showCelebration = session.id
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp),
                    enabled = progress > 0.9f || !isPlaying 
                ) {
                    Text(stringResource(R.string.vitalis_neural_recalibration).uppercase(), fontWeight = FontWeight.Black)
                }
                
                Spacer(modifier = Modifier.height(60.dp))
            }

            if (showCelebration != null) {
                LongevityCelebrationDialog(accent = primaryColor, protocolId = showCelebration!!, onDismiss = { showCelebration = null; onBack() })
            }
        }
    }
}

@Composable
fun LiveNeuralInstructionOverlay(session: TherapeuticSession, isPlaying: Boolean, accent: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "a"
    )

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(if (isPlaying) accent else Color.Gray, CircleShape).graphicsLayer { this.alpha = if (isPlaying) alpha else 1f })
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isPlaying) stringResource(R.string.vitalis_neural_optimization).uppercase() else stringResource(R.string.state_paused).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = if (isPlaying) accent else Color.Gray,
                letterSpacing = 2.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${stringResource(R.string.vitalis_module_detail_domain)}: ${session.domain.name.uppercase()}",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun BioSyncVisualizer(isPlaying: Boolean, accent: Color, scale: Float) {
    Box(contentAlignment = Alignment.Center) {
        // Core Pulse
        Surface(
            shape = CircleShape,
            color = accent.copy(alpha = 0.05f),
            modifier = Modifier.size(200.dp * scale).border(1.dp, accent.copy(alpha = 0.2f * scale), CircleShape)
        ) {}
        
        // Neural Waveform
        Canvas(modifier = Modifier.size(240.dp)) {
            val path = Path()
            val centerY = size.height / 2
            val width = size.width
            val frequency = 10f
            val amplitude = 20f * (if (isPlaying) scale else 1f)
            
            path.moveTo(0f, centerY)
            for (x in 0..width.toInt()) {
                val y = centerY + amplitude * sin((x.toFloat() / width) * frequency * 2 * Math.PI).toFloat()
                path.lineTo(x.toFloat(), y)
            }
            
            drawPath(
                path = path,
                color = accent.copy(alpha = 0.6f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(64.dp).graphicsLayer { this.scaleX = scale; this.scaleY = scale }
        )
    }
}

@Composable
fun BioMetricMiniCard(modifier: Modifier, label: String, value: String, accent: Color) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = accent)
    }
}

@Composable
fun IntelligenceFeedOverlay(isPlaying: Boolean, frequency: String) {
    val feedItems = listOf(
        stringResource(R.string.signal_sync_oscillations),
        stringResource(R.string.signal_target_nodes, frequency),
        stringResource(R.string.signal_reduce_hyperarousal),
        stringResource(R.string.signal_calibrate_vagal),
        stringResource(R.string.signal_optimize_balance)
    )
    var currentItemIndex by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                kotlinx.coroutines.delay(4000)
                currentItemIndex = (currentItemIndex + 1) % feedItems.size
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = feedItems[currentItemIndex],
            transitionSpec = { fadeIn(tween(1000)) togetherWith fadeOut(tween(1000)) },
            label = "feed"
        ) { text ->
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PlaybackEngine(
    progress: Float,
    isPlaying: Boolean,
    onToggle: () -> Unit,
    onSeek: (Float) -> Unit,
    accent: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = progress,
            onValueChange = onSeek,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(72.dp)
                    .background(accent, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
