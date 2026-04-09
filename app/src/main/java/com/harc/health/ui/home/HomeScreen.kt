package com.harc.health.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.R
import com.harc.health.logic.ActionDecisionEngine
import com.harc.health.model.Activity
import com.harc.health.ui.recovery.ActivityPlayer
import com.harc.health.ui.recovery.getActivityForTask
import com.harc.health.ui.theme.*
import com.harc.health.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToProfile: () -> Unit,
    onNavigateToTherapeutic: () -> Unit,
    onNavigateToVitalis: () -> Unit
) {
    val scrollState = rememberScrollState()
    val userName by viewModel.userName.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val adeOutput by viewModel.adeOutput.collectAsState()
    val recoveryScore by viewModel.recoveryScore.collectAsState()
    
    var activeActivity by remember { mutableStateOf<Activity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            ProfessionalTopBar(
                name = userName,
                onSettingsClick = onNavigateToProfile
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                RecoveryStatusCard(score = recoveryScore)
                
                Spacer(modifier = Modifier.height(24.dp))

                // Shortcuts Section
                TherapeuticShortcut(onClick = onNavigateToTherapeutic)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                VitalisShortcut(onClick = onNavigateToVitalis)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                SectionHeader(
                    title = stringResource(R.string.home_biological_directives),
                    subtitle = stringResource(R.string.home_biological_directives_subtitle)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                adeOutput?.primaryActions?.let { actions ->
                    for (action in actions) {
                        AdeTaskItem(
                            action = action,
                            viewModel = viewModel,
                            onStartActivity = { activeActivity = it }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                StreakSection(days = streak)
            }
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
    }
}

@Composable
fun TherapeuticShortcut(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.tertiary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    stringResource(R.string.therapeutic_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.home_therapeutic_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
        }
    }
}

@Composable
fun VitalisShortcut(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoGraph, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    stringResource(R.string.vitalis_project_vitalis),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(R.string.home_vitalis_subtitle),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun AdeTaskItem(
    action: ActionDecisionEngine.AdeAction,
    viewModel: MainViewModel,
    onStartActivity: (Activity) -> Unit
) {
    val healthLog by viewModel.healthLog.collectAsState()
    val isCompleted = healthLog.actionsCompleted.contains(action.id)
    val activity = getActivityForTask(action.id)
    val isLiveRitual = activity != null
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "live")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse"
    )

    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse), label = "shimmer"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                if (isLiveRitual && !isCompleted && !isExpanded) {
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
            }
            .clip(RoundedCornerShape(24.dp))
            .clickable { isExpanded = !isExpanded },
        color = if (isCompleted) GreenRecovery.copy(alpha = 0.05f) 
                else if (isExpanded) MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.5.dp, 
            if (isCompleted) GreenRecovery.copy(alpha = 0.3f) 
            else if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            else if (isLiveRitual) BluePrimary.copy(alpha = shimmerAlpha)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        shadowElevation = if (isCompleted) 0.dp else 4.dp
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            if (isCompleted) GreenRecovery.copy(alpha = 0.15f) 
                            else if (isLiveRitual) BluePrimary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isCompleted && isLiveRitual) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = BluePrimary,
                            strokeWidth = 3.dp,
                            trackColor = BluePrimary.copy(alpha = 0.1f)
                        )
                    }
                    
                    Icon(
                        imageVector = when {
                            isCompleted -> Icons.Default.CheckCircle
                            action.id.startsWith("ca_") || action.id == "h1" -> Icons.Default.WaterDrop
                            isLiveRitual -> Icons.Default.Bolt
                            else -> Icons.Default.SelfImprovement
                        },
                        contentDescription = null,
                        tint = if (isCompleted) GreenRecovery 
                               else if (isLiveRitual) BluePrimary
                               else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(action.instructionRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isCompleted) GreenRecovery else MaterialTheme.colorScheme.onSurface
                        )
                        if (!isCompleted && isLiveRitual) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = BluePrimary, shape = CircleShape) {
                                Text(
                                    stringResource(R.string.home_live_tag),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isExpanded) stringResource(R.string.home_expert_directive) else stringResource(action.contextRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = if (isCompleted) GreenRecovery.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
            }

            if (isExpanded) {
                Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 20.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                    
                    LiveGuidanceSection(
                        title = stringResource(R.string.home_how_to_execute),
                        content = stringResource(action.contextRes),
                        icon = Icons.Default.PlayCircle,
                        color = if (isCompleted) GreenRecovery else MaterialTheme.colorScheme.primary
                    )
                    
                    if (action.biologicalRationaleRes != 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LiveGuidanceSection(
                            title = stringResource(R.string.home_biological_rationale_header),
                            content = stringResource(action.biologicalRationaleRes),
                            icon = Icons.Default.Psychology,
                            color = if (isCompleted) GreenRecovery.copy(alpha = 0.7f) else Color(0xFF9C27B0)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (activity != null) {
                                onStartActivity(activity)
                            } else {
                                viewModel.toggleAction(action.id)
                            }
                            isExpanded = false
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCompleted) GreenRecovery 
                                            else if (isLiveRitual) BluePrimary 
                                            else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(if (isCompleted) Icons.Default.Refresh else Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (isCompleted) stringResource(R.string.home_repeat_ritual) else if (isLiveRitual) stringResource(R.string.home_initiate_ritual) else stringResource(R.string.vitalis_log_completion), 
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveGuidanceSection(title: String, content: String, icon: ImageVector, color: Color) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Text(text = content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
        }
    }
}

@Composable
fun ProfessionalTopBar(name: String, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(R.string.home_biological_command),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
            Text(
                text = stringResource(R.string.home_hello, name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onSettingsClick() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
        Text(text = subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
    }
}

@Composable
fun RecoveryStatusCard(score: Int) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "Score"
    )

    val (status, color) = when {
        score >= 80 -> "OPTIMIZED" to GreenRecovery
        score >= 60 -> "STABLE" to BluePrimary
        score >= 40 -> "STRAINED" to AmberWarning
        else -> "CRITICAL" to RedRisk
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.15f),
                        startAngle = 0f, sweepAngle = 360f, useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(0f to color.copy(alpha = 0.3f), (animatedScore / 100f) to color, center = center),
                        startAngle = 270f, sweepAngle = (animatedScore / 100f) * 360f, useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${animatedScore.toInt()}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = color)
                    Text(text = "%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color.copy(alpha = 0.7f))
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(text = status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = color, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.home_recovery_index), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = stringResource(R.string.home_recovery_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StreakSection(days: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = stringResource(R.string.home_streak_title, days), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(text = stringResource(R.string.home_streak_desc), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) )
            }
        }
    }
}
