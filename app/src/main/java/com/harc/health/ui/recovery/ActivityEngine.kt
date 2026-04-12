package com.harc.health.ui.recovery

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.R
import com.harc.health.model.Activity
import com.harc.health.model.ActivityStep
import com.harc.health.ui.theme.GreenRecovery
import com.harc.health.ui.theme.BluePrimary
import com.harc.health.ui.theme.AmberWarning
import kotlinx.coroutines.delay
import kotlin.math.sin

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityPlayer(
    activity: Activity,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    var timeLeftInStep by remember { mutableIntStateOf(activity.steps[currentStepIndex].durationSeconds) }
    var isPaused by remember { mutableStateOf(false) }
    var showGuidance by remember { mutableStateOf(true) }
    var showCompletion by remember { mutableStateOf(false) }
    val currentStep = activity.steps[currentStepIndex]
    val category = stringResource(activity.categoryRes)

    val bgColor by animateColorAsState(
        targetValue = when {
            category.contains("Respiratory", true) || activity.categoryRes == R.string.cat_respiratory -> Color(0xFFE3F2FD)
            category.contains("Cardiovascular", true) || activity.categoryRes == R.string.cat_cardiovascular -> Color(0xFFFFF3E0)
            category.contains("Metabolic", true) || activity.categoryRes == R.string.cat_metabolic -> Color(0xFFE8F5E9)
            category.contains("Stress", true) || activity.categoryRes == R.string.cat_stress -> Color(0xFFF3E5F5)
            category.contains("Liver", true) || activity.categoryRes == R.string.cat_liver -> Color(0xFFFFF9C4)
            category.contains("Sleep", true) || activity.categoryRes == R.string.cat_sleep_arch -> Color(0xFFE8EAF6)
            category.contains("Cognitive", true) || activity.categoryRes == R.string.cat_cognitive -> Color(0xFFF3E5F5)
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(1200), label = "bg"
    )

    BackHandler {
        if (showCompletion || showGuidance) onBack() else { isPaused = true; onBack() }
    }

    LaunchedEffect(currentStepIndex, isPaused, showGuidance, showCompletion) {
        if (!isPaused && !showGuidance && !showCompletion) {
            while (timeLeftInStep > 0) {
                delay(1000)
                timeLeftInStep--
            }
            if (currentStepIndex < activity.steps.size - 1) {
                currentStepIndex++
                timeLeftInStep = activity.steps[currentStepIndex].durationSeconds
            } else {
                showCompletion = true
            }
        }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(activity.titleRes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                        Text(category.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    TextButton(
                        onClick = { showCompletion = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = GreenRecovery)
                    ) {
                        Text("Complete Now", fontWeight = FontWeight.Black)
                    }
                    IconButton(onClick = { showGuidance = true }) { Icon(Icons.AutoMirrored.Outlined.HelpOutline, null) }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            "STEP ${currentStepIndex + 1}/${activity.steps.size}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedContent(
                        targetState = stringResource(currentStep.titleRes), 
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "stepTitle"
                    ) { title ->
                        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(currentStep.instructionRes),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(modifier = Modifier.size(340.dp).graphicsLayer { translationY = -20f }, contentAlignment = Alignment.Center) {
                    CoreBiologicalAnimation(currentStep.animationType, timeLeftInStep, currentStep.durationSeconds, vibrator)
                    IntelligenceFeed(timeLeftInStep, category)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTime(timeLeftInStep),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = { isPaused = !isPaused },
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause, null, modifier = Modifier.size(32.dp))
                            }
                        }
                        
                        Button(
                            onClick = { 
                                if (currentStepIndex < activity.steps.size - 1) { 
                                    currentStepIndex++
                                    timeLeftInStep = activity.steps[currentStepIndex].durationSeconds 
                                } else showCompletion = true 
                            },
                            modifier = Modifier.height(72.dp).weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentStepIndex == activity.steps.size - 1) GreenRecovery else MaterialTheme.colorScheme.primary
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text(if (currentStepIndex < activity.steps.size - 1) "Next Phase" else "Complete Ritual", fontWeight = FontWeight.Black, fontSize = 18.sp)
                        }
                    }
                }
            }

            if (showGuidance) {
                ExpertGuidanceOverlay(activity) { showGuidance = false }
            }

            if (showCompletion) {
                CompletionRitualOverlay(activity, onComplete)
            }
        }
    }
}

@Composable
fun IntelligenceFeed(timeLeft: Int, category: String) {
    val signals = when {
        category.contains("Respiratory", true) || category == stringResource(R.string.cat_respiratory) -> 
            listOf("Optimizing alveolar gas exchange...", "Reducing bronchial inflammation...", "Stabilizing O2 saturation...")
        category.contains("Cardiovascular", true) || category == stringResource(R.string.cat_cardiovascular) -> 
            listOf("Enhancing vagal tone...", "Modulating heart rate variability...", "Reducing vascular resistance...")
        category.contains("Metabolic", true) || category == stringResource(R.string.cat_metabolic) || category == stringResource(R.string.cat_liver) -> 
            listOf("Restoring osmotic balance...", "Activating Phase II detoxification...", "Neutralizing acetaldehyde...")
        category.contains("Sleep", true) || category == stringResource(R.string.cat_sleep_arch) -> 
            listOf("Synchronizing circadian oscillators...", "Promoting melatonin synthesis...", "Lowering core temperature...")
        category.contains("Cognitive", true) || category == stringResource(R.string.cat_cognitive) -> 
            listOf("Calibrating reward circuitry...", "Increasing D2 receptor sensitivity...", "Engaging Prefrontal Cortex...")
        else -> listOf("Calibrating neural baseline...", "Optimizing allostatic load...", "Regulating systemic pH...")
    }
    
    val currentSignal = signals[(timeLeft / 5) % signals.size]
    
    AnimatedVisibility(
        visible = timeLeft > 0,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut()
    ) {
        Text(
            text = currentSignal,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(y = 150.dp)
        )
    }
}

@Composable
fun CoreBiologicalAnimation(type: String, timeLeft: Int, totalTime: Int, vibrator: Vibrator?) {
    val infiniteTransition = rememberInfiniteTransition(label = "bio")
    
    when (type) {
        "breathe" -> {
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.7f, targetValue = 1.3f,
                animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutQuart), RepeatMode.Reverse), label = "b"
            )

            // Haptic Feedback Logic with state tracking to avoid spamming
            var lastVibratedDirection by remember { mutableStateOf<Boolean?>(null) } // true for expanding, false for contracting
            val isExpanding = remember(scale) { 
                // This is a simple way to detect direction change at the bounds
                scale > 1.25f || scale < 0.75f 
            }

            LaunchedEffect(scale) {
                if (vibrator != null) {
                    if (scale >= 1.29f && lastVibratedDirection != false) { // Peak (Start Exhale)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(60)
                        }
                        lastVibratedDirection = false
                    } else if (scale <= 0.71f && lastVibratedDirection != true) { // Valley (Start Inhale)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 20, 40, 20), -1)) // Double-tap
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(20)
                        }
                        lastVibratedDirection = true
                    }
                }
            }

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(240.dp).graphicsLayer { scaleX = scale; scaleY = scale }) {
                    drawCircle(
                        brush = Brush.radialGradient(listOf(BluePrimary.copy(alpha = 0.3f), Color.Transparent)),
                        radius = size.minDimension / 1.5f
                    )
                    drawCircle(BluePrimary, style = Stroke(6.dp.toPx(), cap = StrokeCap.Round))
                }
                Text(
                    text = if (scale > 1.0f) "EXHALE" else "INHALE", 
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black, 
                    color = BluePrimary
                )
            }
        }
        "craving_wave" -> {
            val waveOffset by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 2 * Math.PI.toFloat(),
                animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)), label = "w"
            )
            Canvas(modifier = Modifier.size(300.dp)) {
                val path = androidx.compose.ui.graphics.Path()
                val midY = size.height / 2
                for (x in 0..size.width.toInt()) {
                    val y = (midY + sin(x * 0.04 + waveOffset) * 50).toFloat()
                    if (x == 0) path.moveTo(x.toFloat(), y) else path.lineTo(x.toFloat(), y)
                }
                drawPath(path, color = AmberWarning, style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))
                
                val path2 = androidx.compose.ui.graphics.Path()
                for (x in 0..size.width.toInt()) {
                    val y = (midY + sin(x * 0.06 - waveOffset) * 30).toFloat()
                    if (x == 0) path2.moveTo(x.toFloat(), y) else path2.lineTo(x.toFloat(), y)
                }
                drawPath(path2, color = AmberWarning.copy(alpha = 0.3f), style = Stroke(4.dp.toPx()))
            }
        }
        "pulse" -> {
            val pulse by infiniteTransition.animateFloat(
                initialValue = 0.8f, targetValue = 1.2f,
                animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse), label = "p"
            )
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(200.dp).graphicsLayer { scaleX = pulse; scaleY = pulse }) {
                    drawCircle(color = BluePrimary.copy(alpha = 0.2f))
                    drawCircle(color = BluePrimary, style = Stroke(4.dp.toPx()))
                }
                Icon(Icons.Default.Adjust, null, modifier = Modifier.size(40.dp), tint = BluePrimary)
            }
        }
        else -> {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { timeLeft.toFloat() / totalTime },
                    modifier = Modifier.size(220.dp),
                    strokeWidth = 14.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
                Icon(Icons.Default.Timer, null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
fun ExpertGuidanceOverlay(activity: Activity, onDismiss: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
            Text("EXPERT DIRECTIVE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text(stringResource(activity.titleRes), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            
            Spacer(modifier = Modifier.height(32.dp))
            GuidanceSection("BIOLOGICAL PURPOSE", stringResource(activity.whyToDORes), Icons.Default.Psychology)
            GuidanceSection("THE PROTOCOL", stringResource(activity.whatToDORes), Icons.AutoMirrored.Filled.FactCheck)
            GuidanceSection("EXPECTED OUTCOME", stringResource(activity.physiologicalOutcomeRes), Icons.Default.AutoGraph)
            
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onDismiss, 
                modifier = Modifier.fillMaxWidth().height(64.dp), 
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Text("INITIATE RITUAL", fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun CompletionRitualOverlay(activity: Activity, onLog: () -> Unit) {
    val dividendRes = remember(activity.id) {
        when {
            activity.id.contains("ca") || activity.id.contains("vascular") -> R.string.dividend_vascular_elasticity
            activity.id.contains("sl") || activity.id.contains("sleep") -> R.string.dividend_glymphatic_flow
            activity.id.contains("me") || activity.id.contains("metabolic") -> R.string.dividend_metabolic_throughput
            activity.id.contains("st") || activity.id.contains("vagal") -> R.string.dividend_cortisol_reduction
            activity.id.contains("co") || activity.id.contains("coherence") -> R.string.dividend_neural_coherence
            activity.id.contains("lp") -> R.string.dividend_mitochondrial_biogenesis
            else -> R.string.dividend_systemic_resilience
        }
    }

    val nextRitualRes = remember(activity.id) {
        when {
            // Sleep/Evening rituals -> Next in morning
            activity.id.contains("sl") || activity.id.contains("evening") || activity.id.contains("night") || activity.id == "circadian_sync" -> 
                R.string.next_ritual_morning
            
            // Morning/Hydration rituals -> Next in evening/pre-sleep
            activity.id.contains("morning") || activity.id == "hydration_action" || activity.id == "h1" -> 
                R.string.next_ritual_pre_sleep
            
            // Metabolic/Glucose rituals -> Post-meal
            activity.id.contains("me") || activity.id.contains("glycemic") || activity.id == "h4" -> 
                R.string.next_ritual_post_meal
            
            // Acute stress/Vagal rituals -> On stress or Midday
            activity.id == "vagal_reset" || activity.id == "st_1" || activity.id == "cc1" -> 
                R.string.next_ritual_stress
                
            // Cognitive/Dopamine -> Midday
            activity.id == "dopamine_reset" -> 
                R.string.next_ritual_midday
                
            // Physical/Respiratory -> Post-exercise
            activity.id.contains("rl") || activity.id == "diaphragmatic_breathing" -> 
                R.string.next_ritual_post_exercise
            
            // Urgent/Persistent
            activity.id.contains("urgent") || activity.id == "p1" || activity.id == "urge_surfing" -> 
                R.string.next_ritual_urgent

            else -> R.string.next_ritual_daily
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black.copy(alpha = 0.96f)) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Verified,
                contentDescription = null,
                tint = GreenRecovery,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                stringResource(R.string.feedback_instant_gratification),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bio-Shift Description
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Text(
                    text = stringResource(activity.impactDescRes),
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Health Dividends Section
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.feedback_health_dividends),
                        style = MaterialTheme.typography.labelLarge,
                        color = GreenRecovery,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GreenRecovery.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(dividendRes),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Next Directive Section
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.feedback_next_ritual),
                        style = MaterialTheme.typography.labelLarge,
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BluePrimary.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(nextRitualRes),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onLog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenRecovery),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(stringResource(R.string.feedback_close_ritual), fontWeight = FontWeight.Black, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun GuidanceSection(title: String, content: String, icon: ImageVector) {
    if (content.isEmpty()) return
    Row(modifier = Modifier.padding(vertical = 16.dp)) {
        Box(
            modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape), 
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.sp)
            Text(text = content, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp, fontWeight = FontWeight.Medium)
        }
    }
}

private fun formatTime(seconds: Int): String = "%02d:%02d".format(seconds / 60, seconds % 60)

object ActivityPresets {
    val ALL_ACTIVITIES = listOf(
        Activity(
            id = "hydration_action",
            titleRes = R.string.act_osmotic_title,
            categoryRes = R.string.cat_metabolic,
            targetAudienceRes = R.string.audience_drinkers,
            durationSeconds = 300,
            impactDescRes = R.string.act_osmotic_impact,
            steps = listOf(
                ActivityStep(R.string.act_osmotic_prep_title, R.string.act_osmotic_prep_desc, 30),
                ActivityStep(R.string.act_osmotic_sip_title, R.string.act_osmotic_sip_desc, 270, "timer")
            ),
            whyToDORes = R.string.act_osmotic_why,
            whatToDORes = R.string.act_osmotic_what,
            physiologicalOutcomeRes = R.string.act_osmotic_outcome
        ),
        Activity(
            id = "diaphragmatic_breathing",
            titleRes = R.string.act_pulmonary_title,
            categoryRes = R.string.cat_respiratory,
            targetAudienceRes = R.string.audience_smokers,
            durationSeconds = 300,
            impactDescRes = R.string.act_pulmonary_impact,
            steps = listOf(
                ActivityStep(R.string.act_pulmonary_posture_title, R.string.act_pulmonary_posture_desc, 30),
                ActivityStep(R.string.act_pulmonary_ritual_title, R.string.act_pulmonary_ritual_desc, 270, "breathe")
            ),
            whyToDORes = R.string.act_pulmonary_why,
            whatToDORes = R.string.act_pulmonary_what,
            physiologicalOutcomeRes = R.string.act_pulmonary_outcome
        ),
        Activity(
            id = "vagal_reset",
            titleRes = R.string.act_vagal_title,
            categoryRes = R.string.cat_cardiovascular,
            targetAudienceRes = R.string.audience_both,
            durationSeconds = 60,
            impactDescRes = R.string.act_vagal_impact,
            steps = listOf(
                ActivityStep(R.string.act_vagal_cold_title, R.string.act_vagal_cold_desc, 60, "timer")
            ),
            whyToDORes = R.string.act_vagal_why,
            whatToDORes = R.string.act_vagal_what,
            physiologicalOutcomeRes = R.string.act_vagal_outcome
        ),
        Activity(
            id = "lv1",
            titleRes = R.string.act_hepatic_title,
            categoryRes = R.string.cat_liver,
            targetAudienceRes = R.string.audience_drinkers,
            durationSeconds = 120,
            impactDescRes = R.string.act_hepatic_impact,
            steps = listOf(ActivityStep(R.string.act_hepatic_loading_title, R.string.act_hepatic_loading_desc, 120, "timer")),
            whyToDORes = R.string.act_hepatic_why,
            whatToDORes = R.string.act_hepatic_what,
            physiologicalOutcomeRes = R.string.act_hepatic_outcome
        ),
        Activity(
            id = "rl5",
            titleRes = R.string.act_steam_title,
            categoryRes = R.string.cat_respiratory,
            targetAudienceRes = R.string.audience_smokers,
            durationSeconds = 600,
            impactDescRes = R.string.act_steam_impact,
            steps = listOf(
                ActivityStep(R.string.act_steam_inhale_title, R.string.act_steam_inhale_desc, 600, "breathe")
            ),
            whyToDORes = R.string.act_steam_why,
            whatToDORes = R.string.act_steam_what,
            physiologicalOutcomeRes = R.string.act_steam_outcome
        ),
        Activity(
            id = "h4",
            titleRes = R.string.act_thiamine_title,
            categoryRes = R.string.cat_metabolic,
            targetAudienceRes = R.string.audience_drinkers,
            durationSeconds = 60,
            impactDescRes = R.string.act_thiamine_impact,
            steps = listOf(ActivityStep(R.string.act_thiamine_intake_title, R.string.act_thiamine_intake_desc, 60, "timer")),
            whyToDORes = R.string.act_thiamine_why,
            whatToDORes = R.string.act_thiamine_what,
            physiologicalOutcomeRes = R.string.act_thiamine_outcome
        ),
        Activity(
            id = "rl4",
            titleRes = R.string.act_inspiratory_title,
            categoryRes = R.string.cat_respiratory,
            targetAudienceRes = R.string.audience_smokers,
            durationSeconds = 180,
            impactDescRes = R.string.act_inspiratory_impact,
            steps = listOf(ActivityStep(R.string.act_inspiratory_res_title, R.string.act_inspiratory_res_desc, 180, "breathe")),
            whyToDORes = R.string.act_inspiratory_why,
            whatToDORes = R.string.act_inspiratory_what,
            physiologicalOutcomeRes = R.string.act_inspiratory_outcome
        ),
        Activity(
            id = "cc1",
            titleRes = R.string.act_coherence_title,
            categoryRes = R.string.cat_cardiovascular,
            targetAudienceRes = R.string.audience_both,
            durationSeconds = 300,
            impactDescRes = R.string.act_coherence_impact,
            steps = listOf(ActivityStep(R.string.act_coherence_step_title, R.string.act_coherence_step_desc, 300, "breathe")),
            whyToDORes = R.string.act_coherence_why,
            whatToDORes = R.string.act_coherence_what,
            physiologicalOutcomeRes = R.string.act_coherence_outcome
        ),
        Activity(
            id = "rl3",
            titleRes = R.string.act_postural_title,
            categoryRes = R.string.cat_respiratory,
            targetAudienceRes = R.string.audience_smokers,
            durationSeconds = 300,
            impactDescRes = R.string.act_postural_impact,
            steps = listOf(ActivityStep(R.string.act_postural_pos_title, R.string.act_postural_pos_desc, 300, "breathe")),
            whyToDORes = R.string.act_postural_why,
            whatToDORes = R.string.act_postural_what,
            physiologicalOutcomeRes = R.string.act_postural_outcome
        ),
        Activity(
            id = "sl1",
            titleRes = R.string.act_reset_title,
            categoryRes = R.string.cat_stress,
            targetAudienceRes = R.string.audience_both,
            durationSeconds = 300,
            impactDescRes = R.string.act_reset_impact,
            steps = listOf(ActivityStep(R.string.act_reset_cycle_title, R.string.act_reset_cycle_desc, 300, "breathe")),
            whyToDORes = R.string.act_reset_why,
            whatToDORes = R.string.act_reset_what,
            physiologicalOutcomeRes = R.string.act_reset_outcome
        ),
        Activity(
            id = "st_1",
            titleRes = R.string.act_sigh_title,
            categoryRes = R.string.cat_stress,
            targetAudienceRes = R.string.audience_both,
            durationSeconds = 120,
            impactDescRes = R.string.act_sigh_impact,
            steps = listOf(ActivityStep(R.string.act_sigh_cycle_title, R.string.act_sigh_cycle_desc, 120, "breathe")),
            whyToDORes = R.string.act_sigh_why,
            whatToDORes = R.string.act_sigh_what,
            physiologicalOutcomeRes = R.string.act_sigh_outcome
        ),
        Activity(
            id = "urge_surfing",
            titleRes = R.string.act_surfing_title,
            categoryRes = R.string.cat_behavioral,
            targetAudienceRes = R.string.audience_both,
            durationSeconds = 300,
            impactDescRes = R.string.act_surfing_impact,
            steps = listOf(ActivityStep(R.string.act_surfing_step_title, R.string.act_surfing_step_desc, 300, "craving_wave")),
            whyToDORes = R.string.act_surfing_why,
            whatToDORes = R.string.act_surfing_what,
            physiologicalOutcomeRes = R.string.act_surfing_outcome
        ),
        Activity(
            id = "circadian_sync",
            titleRes = R.string.act_circadian_title,
            categoryRes = R.string.cat_sleep_arch,
            targetAudienceRes = R.string.audience_both,
            durationSeconds = 180,
            impactDescRes = R.string.act_circadian_impact,
            steps = listOf(ActivityStep(R.string.act_circadian_step_title, R.string.act_circadian_step_desc, 180, "pulse")),
            whyToDORes = R.string.act_circadian_why,
            whatToDORes = R.string.act_circadian_what,
            physiologicalOutcomeRes = R.string.act_circadian_outcome
        ),
        Activity(
            id = "dopamine_reset",
            titleRes = R.string.act_dopamine_title,
            categoryRes = R.string.cat_cognitive,
            targetAudienceRes = R.string.audience_both,
            durationSeconds = 300,
            impactDescRes = R.string.act_dopamine_impact,
            steps = listOf(ActivityStep(R.string.act_dopamine_step_title, R.string.act_dopamine_step_desc, 300, "pulse")),
            whyToDORes = R.string.act_dopamine_why,
            whatToDORes = R.string.act_dopamine_what,
            physiologicalOutcomeRes = R.string.act_dopamine_outcome
        )
    )

    fun getActivityById(id: String) = ALL_ACTIVITIES.find { it.id == id }
}

fun getActivityForTask(taskId: String): Activity? {
    val idToActivity = when (taskId) {
        "h1", "p1", "ca_Hydration + Electrolytes" -> "hydration_action"
        "h4" -> "h4"
        "lv1" -> "lv1"
        "cc1" -> "cc1"
        "cc3", "p6" -> "vagal_reset"
        "rl1", "p3" -> "diaphragmatic_breathing"
        "rl4" -> "rl4"
        "rl5" -> "rl5"
        "rl3" -> "rl3"
        "sl1" -> "sl1"
        "sl2" -> "circadian_sync"
        "nc1" -> "dopamine_reset"
        "bh3" -> "urge_surfing"
        "st_1" -> "st_1"
        else -> taskId
    }
    return ActivityPresets.getActivityById(idToActivity)
}
