package com.harc.health.ui.recovery

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.harc.health.R
import com.harc.health.logic.RecoveryEngine
import com.harc.health.model.Activity
import com.harc.health.model.RecoveryTask
import com.harc.health.viewmodel.MainViewModel
import com.harc.health.ui.theme.*

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.core.graphics.toColorInt
import com.harc.health.logic.ResilienceSystem
import java.util.Calendar

@Composable
fun RecoveryPlanScreen(viewModel: MainViewModel) {
    val healthLog by viewModel.healthLog.collectAsState()
    var activeActivity by remember { mutableStateOf<Activity?>(null) }
    var showQuickFeedback by remember { mutableStateOf<Activity?>(null) }
    val listState = rememberLazyListState()
    
    var simulatedHour by remember { mutableStateOf<Int?>(null) }
    val actualHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentHour = simulatedHour ?: actualHour
    val isNight = currentHour !in 6..21
    val resilienceSystems = remember(healthLog) { RecoveryEngine.getResilienceSystems(healthLog) }

    val timeCategories = listOf(
        R.string.recovery_cat_morning to (6..11),
        R.string.recovery_cat_midday to (12..14),
        R.string.recovery_cat_afternoon to (15..17),
        R.string.recovery_cat_evening to (18..21),
        R.string.recovery_cat_night to (22..23)
    )

    val bgColor = when (currentHour) {
        in 6..11 -> Brush.verticalGradient(listOf(Color(0xFFFFFAF0), MaterialTheme.colorScheme.background)) // Morning Amber
        in 12..17 -> Brush.verticalGradient(listOf(Color(0xFFF0F8FF), MaterialTheme.colorScheme.background)) // Mid-day Blue
        in 18..21 -> Brush.verticalGradient(listOf(Color(0xFFFFF5EE), MaterialTheme.colorScheme.background)) // Evening Sunset
        else -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF020617))) // Night Indigo
    }

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recovery_biological_resilience),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        color = if (isNight) BluePrimary else MaterialTheme.colorScheme.primary
                    )
                    
                    // Simulation Toggle for Testing
                    Surface(
                        onClick = { 
                            simulatedHour = if (simulatedHour == null) 23 else if (simulatedHour == 23) 10 else null 
                        },
                        shape = CircleShape,
                        color = if (isNight) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = if (simulatedHour == null) stringResource(R.string.recovery_live_time) else stringResource(R.string.recovery_simulating, currentHour),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isNight) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(resilienceSystems) { system ->
                        ResilienceCard(system, isNight)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                // --- ADAPTIVE NARRATIVE BRIEFING ---
                val briefingRes = remember(healthLog) { RecoveryEngine.getBiologicalBriefing(healthLog) }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isNight) Color(0xFF1E293B).copy(alpha = 0.8f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, null, tint = if (isNight) BluePrimary else MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(stringResource(R.string.recovery_biological_briefing), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = if (isNight) BluePrimary else MaterialTheme.colorScheme.primary)
                            Text(stringResource(briefingRes), style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp, color = if (isNight) Color.White else MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.recovery_daily_journey),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = if (isNight) BluePrimary else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.recovery_daily_journey_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isNight) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- THE CHRONO-PROTOCOL (TOUR GUIDE SECTION) ---
            timeCategories.forEach { (categoryRes, range) ->
                val isActiveTime = currentHour in range
                item {
                RecoverySectionHeader(
                        title = stringResource(categoryRes),
                        targetAudience = if (isActiveTime) stringResource(R.string.recovery_status_recommended) else stringResource(R.string.recovery_status_scheduled),
                        icon = when {
                            range.first < 12 -> Icons.Default.WbSunny
                            range.first < 18 -> Icons.Default.Cloud
                            else -> Icons.Default.NightsStay
                        },
                        isHighlighted = isActiveTime,
                        isNight = isNight
                    )
                }
                items(RecoveryEngine.getTasksForCategory(categoryRes)) { task ->
                    RecoveryTaskItem(task, viewModel, isPriority = isActiveTime, isNight = isNight, onStartActivity = { activeActivity = it }, onQuickComplete = { showQuickFeedback = it })
                }
            }

            // --- TRADITIONAL CATEGORIES (Optional/Secondary) ---
            item {
                Text(
                    text = stringResource(R.string.recovery_targeted_repair),
                    modifier = Modifier.padding(top = 32.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = if (isNight) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline
                )
            }
            
            item {
                RecoverySectionHeader(
                    title = stringResource(R.string.recovery_cat_liver_metabolic),
                    targetAudience = stringResource(R.string.recovery_audience_alcohol),
                    icon = Icons.Default.LocalHospital,
                    isNight = isNight
                )
            }
            items(RecoveryEngine.getTasksForCategory(R.string.recovery_cat_liver_metabolic)) { task ->
                RecoveryTaskItem(task, viewModel, isNight = isNight, onStartActivity = { activeActivity = it }, onQuickComplete = { showQuickFeedback = it })
            }

            // 2. CARDIOVASCULAR & CIRCULATION
            item {
                RecoverySectionHeader(
                    title = stringResource(R.string.recovery_cat_cardiovascular),
                    targetAudience = stringResource(R.string.recovery_audience_both_short),
                    icon = Icons.Default.Favorite,
                    isNight = isNight
                )
            }
            items(RecoveryEngine.getTasksForCategory(R.string.recovery_cat_cardiovascular)) { task ->
                RecoveryTaskItem(task, viewModel, isNight = isNight, onStartActivity = { activeActivity = it }, onQuickComplete = { showQuickFeedback = it })
            }

            // 3. RESPIRATORY & LUNG HEALTH
            item {
                RecoverySectionHeader(
                    title = stringResource(R.string.recovery_cat_respiratory),
                    targetAudience = stringResource(R.string.recovery_audience_smoking_short),
                    icon = Icons.Default.Air,
                    isNight = isNight
                )
            }
            items(RecoveryEngine.getTasksForCategory(R.string.recovery_cat_respiratory)) { task ->
                RecoveryTaskItem(task, viewModel, isNight = isNight, onStartActivity = { activeActivity = it }, onQuickComplete = { showQuickFeedback = it })
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }


        if (activeActivity != null) {
            ActivityPlayer(
                activity = activeActivity!!,
                onComplete = {
                    viewModel.toggleAction(activeActivity!!.id)
                    activeActivity = null
                },
                onBack = { activeActivity = null }
            )
        }

        if (showQuickFeedback != null) {
            CompletionRitualOverlay(
                activity = showQuickFeedback!!,
                onLog = { showQuickFeedback = null }
            )
        }
    }
}

@Composable
fun ResilienceCard(system: ResilienceSystem, isNight: Boolean = false) {
    Surface(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (isNight) Color(0xFF1E293B) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, Color(system.color.toColorInt()).copy(alpha = 0.2f)),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(system.color.toColorInt()), CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(system.nameRes).uppercase(),
                    style = MaterialTheme.typography.labelSmall, 
                    fontWeight = FontWeight.Black, 
                    color = if (isNight) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    stringResource(R.string.recovery_lvl), 
                    style = MaterialTheme.typography.labelSmall, 
                    color = if (isNight) BluePrimary else MaterialTheme.colorScheme.primary, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    system.level.toString(), 
                    style = MaterialTheme.typography.headlineSmall, 
                    fontWeight = FontWeight.Black, 
                    color = if (isNight) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { system.xp.toFloat() / system.nextLevelXp },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = Color(system.color.toColorInt()),
                trackColor = Color(system.color.toColorInt()).copy(alpha = 0.1f),
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "${system.xp}/${system.nextLevelXp} XP", 
                style = MaterialTheme.typography.labelSmall, 
                color = if (isNight) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecoverySectionHeader(
    title: String, 
    targetAudience: String, 
    icon: ImageVector,
    isHighlighted: Boolean = false,
    isNight: Boolean = false
) {
    val baseColor = if (isNight) Color.White else MaterialTheme.colorScheme.onBackground
    val secondaryColor = if (isNight) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isHighlighted) BluePrimary else secondaryColor,
                modifier = Modifier.size(if (isHighlighted) 22.dp else 18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = if (isHighlighted) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = if (isHighlighted) BluePrimary else baseColor,
                letterSpacing = 1.sp
            )
        }
        Surface(
            color = if (isHighlighted) BluePrimary.copy(alpha = 0.1f) else (if (isNight) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = targetAudience.uppercase(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) BluePrimary else secondaryColor,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun RecoveryTaskItem(
    task: RecoveryTask, 
    viewModel: MainViewModel, 
    isPriority: Boolean = false,
    isNight: Boolean = false,
    onStartActivity: (Activity) -> Unit,
    onQuickComplete: (Activity) -> Unit = {}
) {
    val healthLog by viewModel.healthLog.collectAsState()
    val isCompleted = healthLog.actionsCompleted.contains(task.id)
    val activity = getActivityForTask(task.id)
    val isLiveRitual = activity != null

    val infiniteTransition = rememberInfiniteTransition(label = "live")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.02f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse), label = "p"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                if (isLiveRitual && !isCompleted) {
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                if (isCompleted) return@clickable
                if (activity != null) {
                    if (isLiveRitual) onStartActivity(activity)
                    else {
                        viewModel.toggleAction(task.id)
                        onQuickComplete(activity)
                    }
                } else {
                    viewModel.toggleAction(task.id)
                }
            },
        color = when {
            isCompleted -> GreenRecovery.copy(alpha = if (isNight) 0.2f else 0.1f)
            isPriority -> RedRisk.copy(alpha = if (isNight) 0.2f else 0.1f)
            else -> if (isNight) Color(0xFF1E293B) else MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = when {
                isCompleted -> GreenRecovery.copy(alpha = 0.5f)
                isLiveRitual -> BluePrimary.copy(alpha = 0.8f)
                isPriority -> RedRisk.copy(alpha = 0.5f)
                else -> if (isNight) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.outlineVariant
            }
        ),
        shadowElevation = if (isCompleted || isNight) 0.dp else 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        when {
                            isCompleted -> GreenRecovery.copy(alpha = 0.1f)
                            isLiveRitual -> BluePrimary.copy(alpha = 0.1f)
                            else -> if (isNight) Color.White.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLiveRitual && !isCompleted) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = BluePrimary,
                        strokeWidth = 2.dp,
                        trackColor = BluePrimary.copy(alpha = 0.1f)
                    )
                }
                Icon(
                    imageVector = when {
                        isCompleted -> Icons.Default.CheckCircle
                        isLiveRitual -> Icons.Default.Bolt
                        else -> Icons.Default.RadioButtonUnchecked
                    },
                    contentDescription = null,
                    tint = if (isCompleted) GreenRecovery else if (isLiveRitual) BluePrimary else (if (isNight) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                    text = stringResource(task.titleRes),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black,
                    color = if (isCompleted) GreenRecovery else (if (isNight) Color.White else MaterialTheme.colorScheme.onSurface)
                )
                if (isLiveRitual && !isCompleted) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.home_live_tag),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = BluePrimary
                    )
                }
            }
            Text(
                text = stringResource(task.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = if (isNight) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
            }
            
            if (!isCompleted) {
                if (isLiveRitual) {
                    IconButton(
                        onClick = { onStartActivity(activity) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = BluePrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Button(
                    onClick = {
                        viewModel.toggleAction(task.id)
                        activity?.let { onQuickComplete(it) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = (if (isLiveRitual) GreenRecovery else BluePrimary).copy(alpha = 0.1f), 
                        contentColor = if (isLiveRitual) GreenRecovery else BluePrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.vitalis_complete), 
                        style = MaterialTheme.typography.labelMedium, 
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
