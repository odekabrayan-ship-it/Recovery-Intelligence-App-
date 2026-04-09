package com.harc.health.ui.insights

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ContactSupport
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.R
import com.harc.health.viewmodel.MainViewModel
import com.harc.health.ui.theme.*
import com.harc.health.model.HealthLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(viewModel: MainViewModel) {
    val healthLog by viewModel.healthLog.collectAsState()
    val recoveryScore by viewModel.recoveryScore.collectAsState()
    val uriHandler = LocalUriHandler.current
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.insights_behavioral_intelligence),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            IntelligenceFeed(healthLog)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            InsightHeader(stringResource(R.string.insights_behavioral_trends), stringResource(R.string.insights_clinical_interpretation))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Intake Analytics Card
            InterpretativeChartCard(
                title = stringResource(R.string.insights_intake_intensity),
                subtitle = stringResource(R.string.insights_metabolic_load),
                icon = Icons.Outlined.Analytics,
                interpretation = getIntakeInterpretation(healthLog),
                actionLabel = if (healthLog.alcoholUnits > 0) stringResource(R.string.insights_liver_recovery) else stringResource(R.string.insights_lung_health),
                onAction = {
                    val tab = if (healthLog.alcoholUnits > 0) "1. LIVER & METABOLIC SUPPORT" else "3. RESPIRATORY & LUNG HEALTH"
                    viewModel.navigateToRecoveryWithTab(tab)
                },
                color = if (healthLog.alcoholUnits > 3 || healthLog.cigarettes > 10) RedRisk else GreenRecovery
            ) {
                BarChart(
                    data = listOf(2f, 4f, 1f, 0f, 3f, 5f, (healthLog.alcoholUnits + healthLog.cigarettes/2f).toFloat()),
                    color = if (healthLog.alcoholUnits > 3 || healthLog.cigarettes > 10) RedRisk else BluePrimary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Recovery Correlation Card
            InterpretativeChartCard(
                title = stringResource(R.string.insights_resilience_dynamics),
                subtitle = stringResource(R.string.insights_system_stability),
                icon = Icons.Default.Timeline,
                interpretation = getRecoveryInterpretation(recoveryScore, healthLog),
                actionLabel = stringResource(R.string.insights_review_protocol),
                onAction = { viewModel.navigateToRecoveryWithTab("Daily Priorities") },
                color = if (recoveryScore < 60) AmberWarning else GreenRecovery
            ) {
                LineChart(
                    data = listOf(40f, 55f, 50f, 65f, 70f, 85f, recoveryScore.toFloat()),
                    color = BluePrimary
                )
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            InsightHeader(stringResource(R.string.insights_cause_effect), stringResource(R.string.insights_physiological_reactions))
            Spacer(modifier = Modifier.height(16.dp))
            CauseEffectChain(healthLog)

            Spacer(modifier = Modifier.height(32.dp))

            InsightHeader(stringResource(R.string.insights_scientific_intelligence), stringResource(R.string.insights_latest_findings))
            Spacer(modifier = Modifier.height(16.dp))
            ResearchFeaturedCard(onAction = { uriHandler.openUri("https://www.yimbik.org") })

            Spacer(modifier = Modifier.height(32.dp))

            InsightHeader(
                title = stringResource(R.string.insights_clinical_library), 
                subtitle = stringResource(R.string.insights_peer_reviewed_guides),
                onClick = { uriHandler.openUri("https://www.yimbik.org") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            HealthLibrarySection(onItemClick = { uriHandler.openUri("https://www.yimbik.org") })

            Spacer(modifier = Modifier.height(40.dp))
            
            ContactSection()

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun getIntakeInterpretation(log: HealthLog): String {
    val isHighIntake = log.alcoholUnits > 3 || log.cigarettes > 10
    val hasRecoveryActions = log.actionsCompleted.isNotEmpty()
    
    return when {
        isHighIntake && !hasRecoveryActions -> 
            stringResource(R.string.insights_intake_high_no_recovery)
        isHighIntake && hasRecoveryActions -> 
            stringResource(R.string.insights_intake_high_recovery)
        !isHighIntake && hasRecoveryActions -> 
            stringResource(R.string.insights_intake_low_recovery)
        else -> 
            stringResource(R.string.insights_intake_stable)
    }
}

@Composable
fun getRecoveryInterpretation(score: Int, log: HealthLog): String {
    val hydrationTaskDone = log.actionsCompleted.contains("h1") || log.actionsCompleted.contains("Hydration")
    
    return when {
        score < 40 -> stringResource(R.string.insights_recovery_critical)
        score < 60 && !hydrationTaskDone -> stringResource(R.string.insights_recovery_decreasing)
        score < 60 && hydrationTaskDone -> stringResource(R.string.insights_recovery_progress)
        score >= 80 -> stringResource(R.string.insights_recovery_high)
        else -> stringResource(R.string.insights_recovery_stable)
    }
}

@Composable
fun IntelligenceFeed(healthLog: HealthLog) {
    val insights = remember(healthLog) {
        val list = mutableListOf<Pair<Int, ImageVector>>()
        val alcoholDone = healthLog.alcoholUnits > 0
        val smokingDone = healthLog.cigarettes > 0
        val hydrationDone = healthLog.actionsCompleted.contains("h1") || healthLog.actionsCompleted.contains("Hydration") || healthLog.hydrationMl >= 2000
        val breathingDone = healthLog.actionsCompleted.contains("rl1") || healthLog.actionsCompleted.contains("Breathing")
        
        if (alcoholDone && !hydrationDone) list.add(R.string.insights_urgent_hydration to Icons.Default.WaterDrop)
        if (alcoholDone && hydrationDone) list.add(R.string.insights_success_hydration to Icons.Default.CheckCircle)
        
        if (smokingDone && !breathingDone) list.add(R.string.insights_vascular_stress to Icons.Default.Air)
        if (smokingDone && breathingDone) list.add(R.string.insights_breathing_reset to Icons.Default.CheckCircle)
        
        if (healthLog.hydrationMl < 1500 && !hydrationDone) list.add(R.string.insights_low_hydration to Icons.AutoMirrored.Outlined.TrendingDown)
        
        if (healthLog.actionsCompleted.size > 2) list.add(R.string.insights_high_consistency to Icons.AutoMirrored.Outlined.TrendingUp)
        
        if (list.isEmpty()) list.add(R.string.insights_equilibrium to Icons.Default.CheckCircle)
        list
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        insights.take(2).forEach { (resId, icon) ->
            val text = stringResource(resId)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (text.startsWith("SUCCESS") || text.startsWith("BAŞARI")) GreenRecovery.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (text.startsWith("SUCCESS") || text.startsWith("BAŞARI")) GreenRecovery.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = if (text.startsWith("SUCCESS") || text.startsWith("BAŞARI")) GreenRecovery else MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = text, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun CauseEffectChain(healthLog: HealthLog) {
    val hydrationDone = healthLog.actionsCompleted.contains("h1") || healthLog.actionsCompleted.contains("Hydration")
    val breathingDone = healthLog.actionsCompleted.contains("rl1") || healthLog.actionsCompleted.contains("Breathing")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            val steps = when {
                healthLog.alcoholUnits > 0 -> {
                    listOf(
                        Triple(stringResource(R.string.insights_alcohol_intake), stringResource(R.string.insights_metabolic_ethanol), RedRisk),
                        Triple(
                            if (hydrationDone) stringResource(R.string.insights_hydration_active) else stringResource(R.string.insights_hydration_deficit), 
                            if (hydrationDone) stringResource(R.string.insights_accelerating_toxin) else stringResource(R.string.insights_metabolic_delayed),
                            if (hydrationDone) GreenRecovery else RedRisk
                        ),
                        Triple(
                            if (hydrationDone) stringResource(R.string.insights_reduced_hangover) else stringResource(R.string.insights_increased_fatigue), 
                            if (hydrationDone) stringResource(R.string.insights_systemic_stability_returning) else stringResource(R.string.insights_neural_recovery_delayed), 
                            if (hydrationDone) GreenRecovery else AmberWarning
                        )
                    )
                }
                healthLog.cigarettes > 0 -> {
                    listOf(
                        Triple(stringResource(R.string.insights_nicotine_intake), stringResource(R.string.insights_vascular_constriction), RedRisk),
                        Triple(
                            if (breathingDone) stringResource(R.string.insights_breathing_protocol) else stringResource(R.string.insights_lung_stress), 
                            if (breathingDone) stringResource(R.string.insights_increasing_oxygen) else stringResource(R.string.insights_lower_oxygen), 
                            if (breathingDone) GreenRecovery else RedRisk
                        ),
                        Triple(
                            if (breathingDone) stringResource(R.string.insights_oxygen_stabilized) else stringResource(R.string.insights_recovery_slowed), 
                            if (breathingDone) stringResource(R.string.insights_vascular_decreasing) else stringResource(R.string.insights_reduced_cellular), 
                            if (breathingDone) GreenRecovery else AmberWarning
                        )
                    )
                }
                else -> {
                    listOf(
                        Triple(stringResource(R.string.insights_clean_protocol), stringResource(R.string.insights_zero_toxin), GreenRecovery),
                        Triple(stringResource(R.string.insights_stable_equilibrium), stringResource(R.string.insights_optimal_system), GreenRecovery),
                        Triple(stringResource(R.string.insights_rapid_repair), stringResource(R.string.insights_maximum_cellular), GreenRecovery)
                    )
                }
            }

            steps.forEachIndexed { index, (title, sub, color) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(modifier = Modifier.size(12.dp), shape = CircleShape, color = color) {}
                        if (index < steps.size - 1) {
                            Box(modifier = Modifier.width(2.dp).height(30.dp).background(color.copy(alpha = 0.3f)))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(text = sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun InterpretativeChartCard(
    title: String, 
    subtitle: String, 
    icon: ImageVector, 
    interpretation: String,
    actionLabel: String,
    onAction: () -> Unit,
    color: Color,
    chart: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                chart()
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Surface(
                color = color.copy(alpha = 0.05f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = interpretation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = actionLabel,
                        modifier = Modifier.clickable { onAction() },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun InsightHeader(title: String, subtitle: String, onClick: (() -> Unit)? = null) {
    Column(
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            if (onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ResearchFeaturedCard(onAction: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.insights_pulmonary_restoration),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.insights_study_desc),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                Text(stringResource(R.string.insights_access_publication), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun HealthLibrarySection(onItemClick: () -> Unit) {
    val items = listOf(
        stringResource(R.string.insights_hepatic_restoration) to Icons.AutoMirrored.Filled.MenuBook,
        stringResource(R.string.insights_sleep_architecture) to Icons.Default.Bedtime,
        stringResource(R.string.insights_intracellular_hydration) to Icons.Default.WaterDrop,
        stringResource(R.string.insights_neuro_chemical) to Icons.Default.Psychology
    )
    
    LazyRow(
        contentPadding = PaddingValues(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { (title, icon) ->
            Surface(
                modifier = Modifier
                    .size(width = 140.dp, height = 160.dp)
                    .clickable { onItemClick() },
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ContactSection() {
    val uriHandler = LocalUriHandler.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ContactSupport,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                stringResource(R.string.insights_questions_concerns),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.insights_reach_out),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            Button(
                onClick = { uriHandler.openUri("mailto:director-hac@yimbik.org") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("director-hac@yimbik.org")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedButton(
                onClick = { uriHandler.openUri("mailto:director@yimbik.org") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("director@yimbik.org", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun BarChart(data: List<Float>, color: Color) {
    val barBackgroundColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxVal = (data.maxOrNull() ?: 1f).coerceAtLeast(8f)
        val barWidth = size.width / (data.size * 2f)
        val space = (size.width - (barWidth * data.size)) / (data.size - 1)
        
        data.forEachIndexed { index, value ->
            val barHeight = (value / maxVal) * size.height
            val x = index * (barWidth + space)
            
            // Background bar
            drawRoundRect(
                color = barBackgroundColor,
                topLeft = Offset(x, 0f),
                size = Size(barWidth, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            
            // Value bar
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(color, color.copy(alpha = 0.7f))),
                topLeft = Offset(x, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        }
    }
}

@Composable
fun LineChart(data: List<Float>, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxVal = 100f
        val stepX = size.width / (data.size - 1)
        val path = Path()
        val fillPath = Path()
        
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (value / maxVal) * size.height
            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
            
            if (index == data.size - 1) {
                fillPath.lineTo(x, size.height)
                fillPath.close()
            }
        }
        
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(listOf(color.copy(alpha = 0.15f), Color.Transparent))
        )
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Final point indicator
        val lastX = (data.size - 1) * stepX
        val lastY = size.height - (data.last() / maxVal) * size.height
        drawCircle(color = Color.White, radius = 6.dp.toPx(), center = Offset(lastX, lastY))
        drawCircle(color = color, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
    }
}
