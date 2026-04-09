package com.harc.health.ui.intake

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.R
import com.harc.health.viewmodel.MainViewModel
import com.harc.health.ui.theme.*
import com.harc.health.logic.RecoveryEngine
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntakeLoggingScreen(viewModel: MainViewModel, onNavigateBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    var showPostLogFeedback by remember { mutableStateOf<String?>(null) } 
    var showPurgeDialog by remember { mutableStateOf(false) }

    if (showPurgeDialog) {
        AlertDialog(
            onDismissRequest = { showPurgeDialog = false },
            title = { Text(stringResource(R.string.intake_purge_confirm).uppercase(), fontWeight = FontWeight.Black) },
            text = { Text(stringResource(R.string.intake_purge_desc)) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteData(); showPurgeDialog = false }) {
                    Text(stringResource(R.string.settings_confirm_delete).uppercase(), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPurgeDialog = false }) {
                    Text(stringResource(R.string.settings_cancel).uppercase(), fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.intake_logging_system).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.settings_close))
                    }
                },
                actions = {
                    IconButton(onClick = { showPurgeDialog = true }) {
                        Icon(Icons.Default.RestartAlt, contentDescription = "Recalibrate System", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        PaddingTabLabel(stringResource(R.string.intake_tab_intake), Icons.Default.BarChart)
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        PaddingTabLabel(stringResource(R.string.intake_tab_sleep), Icons.Default.Bedtime)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (selectedTab == 0) {
                    IntakeTabContent(viewModel) { showPostLogFeedback = it }
                } else {
                    SleepTabContent(viewModel, onLogged = onNavigateBack)
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }

            AnimatedVisibility(
                visible = showPostLogFeedback != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                PostLogGuidanceOverlay(
                    type = showPostLogFeedback ?: "",
                    viewModel = viewModel,
                    onClose = onNavigateBack
                )
            }
        }
    }
}

@Composable
fun PaddingTabLabel(label: String, icon: ImageVector) {
    Row(
        modifier = Modifier.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label.uppercase(), fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun IntakeTabContent(viewModel: MainViewModel, onLogged: (String) -> Unit) {
    var subTab by remember { mutableIntStateOf(0) }
    var pendingValue by remember { mutableIntStateOf(1) }
    
    LaunchedEffect(subTab) {
        pendingValue = 1
    }

    Column {
        SystemImpactAnalyzer(
            viewModel = viewModel,
            pendingAlcohol = if (subTab == 0) pendingValue else 0,
            pendingCigarettes = if (subTab == 1) pendingValue else 0
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IntakeTabButton(
                label = stringResource(R.string.intake_alcohol),
                isSelected = subTab == 0,
                onClick = { subTab = 0 },
                modifier = Modifier.weight(1f)
            )
            IntakeTabButton(
                label = stringResource(R.string.intake_smoking),
                isSelected = subTab == 1,
                onClick = { subTab = 1 },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (subTab == 0) {
            AlcoholLoggingSection(viewModel, pendingValue, { pendingValue = it }) { onLogged("Alcohol") }
        } else {
            SmokingLoggingSection(viewModel, pendingValue, { pendingValue = it }) { onLogged("Smoking") }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        HistoricalComparisonModule(viewModel)
    }
}

@Composable
fun HistoricalComparisonModule(viewModel: MainViewModel) {
    val weeklyAlcohol by viewModel.weeklyAlcohol.collectAsState()
    val weeklyCigarettes by viewModel.weeklyCigarettes.collectAsState()
    val monthlyAlcohol by viewModel.monthlyAlcohol.collectAsState()
    val monthlyCigarettes by viewModel.monthlyCigarettes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.intake_compare_period).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ComparisonCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.intake_weekly_load),
                alcValue = weeklyAlcohol,
                cigValue = weeklyCigarettes
            )
            ComparisonCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.intake_monthly_load),
                alcValue = monthlyAlcohol,
                cigValue = monthlyCigarettes
            )
        }
    }
}

@Composable
fun ComparisonCard(modifier: Modifier, title: String, alcValue: Int, cigValue: Int) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, fontSize = 8.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalBar, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text("$alcValue", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.SmokingRooms, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text("$cigValue", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun IntakeTabButton(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label.uppercase(), fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun SystemImpactAnalyzer(viewModel: MainViewModel, pendingAlcohol: Int, pendingCigarettes: Int) {
    val healthLog by viewModel.healthLog.collectAsState()
    
    val currentAlcohol = healthLog.alcoholUnits
    val currentCigarettes = healthLog.cigarettes
    
    val previewLog = healthLog.copy(
        alcoholUnits = currentAlcohol + pendingAlcohol,
        cigarettes = currentCigarettes + pendingCigarettes
    )
    
    val currentScore = RecoveryEngine.calculateScore(healthLog)
    val previewScore = RecoveryEngine.calculateScore(previewLog)
    val delta = previewScore - currentScore

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                stringResource(R.string.intake_analyzer_active).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                stringResource(R.string.intake_bio_load_summary).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AnalyzerStatItem(
                label = stringResource(R.string.intake_alcohol),
                value = (currentAlcohol + pendingAlcohol).toString(),
                subLabel = "UNITS",
                isPending = pendingAlcohol > 0
            )
            AnalyzerStatItem(
                label = stringResource(R.string.intake_smoking),
                value = (currentCigarettes + pendingCigarettes).toString(),
                subLabel = "CIGS",
                isPending = pendingCigarettes > 0
            )
            AnalyzerStatItem(
                label = stringResource(R.string.intake_recovery_index_preview),
                value = "$previewScore%",
                subLabel = if (delta != 0) "${if (delta > 0) "+" else ""}$delta" else "STABLE",
                valueColor = when {
                    delta < 0 -> MaterialTheme.colorScheme.error
                    delta > 0 -> GreenRecovery
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

@Composable
fun AnalyzerStatItem(label: String, value: String, subLabel: String, valueColor: Color = MaterialTheme.colorScheme.onSurface, isPending: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = if (isPending) MaterialTheme.colorScheme.primary else valueColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = subLabel,
                style = MaterialTheme.typography.labelSmall,
                color = if (isPending) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 2.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AlcoholLoggingSection(viewModel: MainViewModel, quantity: Int, onQuantityChange: (Int) -> Unit, onLogged: () -> Unit) {
    Column {
        Text(
            stringResource(R.string.intake_standard_drinks).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        QuantitySelector(quantity, onQuantityChange)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { repeat(quantity) { viewModel.addDrink() }; onLogged() },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(R.string.intake_confirm_alcohol).uppercase(), fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun SmokingLoggingSection(viewModel: MainViewModel, quantity: Int, onQuantityChange: (Int) -> Unit, onLogged: () -> Unit) {
    Column {
        Text(
            stringResource(R.string.intake_quantity_cigarettes).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        QuantitySelector(quantity, onQuantityChange)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { repeat(quantity) { viewModel.addCigarette() }; onLogged() },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(R.string.intake_confirm_smoking).uppercase(), fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun QuantitySelector(quantity: Int, onValueChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = { if (quantity > 1) onValueChange(quantity - 1) },
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Remove, contentDescription = null)
            }
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "UNITS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Surface(
            onClick = { onValueChange(quantity + 1) },
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    }
}

@Composable
fun SleepTabContent(viewModel: MainViewModel, onLogged: () -> Unit) {
    var hours by remember { mutableDoubleStateOf(7.5) }
    var quality by remember { mutableIntStateOf(70) }
    var consistency by remember { mutableIntStateOf(80) }
    var awakenings by remember { mutableIntStateOf(0) }
    
    Column {
        Text(stringResource(R.string.intake_sleep_alignment).uppercase(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        Text(stringResource(R.string.intake_sleep_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SleepMetricSlider(stringResource(R.string.intake_duration_hours), hours, 4.0..12.0) { hours = (it * 2).toInt() / 2.0 }
        Spacer(modifier = Modifier.height(24.dp))
        
        SleepMetricSlider(stringResource(R.string.intake_subjective_quality), quality.toDouble(), 0.0..100.0) { quality = it.toInt() }
        Spacer(modifier = Modifier.height(24.dp))
        
        SleepMetricSlider(stringResource(R.string.intake_bedtime_consistency), consistency.toDouble(), 0.0..100.0) { consistency = it.toInt() }
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(stringResource(R.string.intake_night_awakenings).uppercase(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(0, 1, 2, 3, 4).forEach { value ->
                FilterChip(
                    selected = awakenings == value,
                    onClick = { awakenings = value },
                    label = { Text(if (value == 4) "4+" else value.toString(), fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Button(
            onClick = { 
                viewModel.logSleep(hours, quality, consistency, awakenings)
                onLogged()
            },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(stringResource(R.string.intake_integrate_sleep).uppercase(), fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun SleepMetricSlider(label: String, value: Double, range: ClosedRange<Double>, onValueChange: (Double) -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                if (label == stringResource(R.string.intake_duration_hours)) String.format(Locale.US, "%.1fh", value) else "${value.toInt()}%", 
                fontWeight = FontWeight.Black, 
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Slider(
            value = value.toFloat(), 
            onValueChange = { onValueChange(it.toDouble()) }, 
            valueRange = range.start.toFloat()..range.endInclusive.toFloat()
        )
    }
}

@Composable
fun PostLogGuidanceOverlay(type: String, viewModel: MainViewModel, onClose: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)).clickable(enabled = false) {},
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenRecovery, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.intake_logged, if (type == "Alcohol") stringResource(R.string.intake_alcohol) else stringResource(R.string.intake_smoking)).uppercase(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (type == "Alcohol") stringResource(R.string.intake_alcohol_feedback)
                           else stringResource(R.string.intake_smoking_feedback),
                    textAlign = TextAlign.Center, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                if (type == "Alcohol") {
                    RecoveryActionButton(Icons.Default.WaterDrop, stringResource(R.string.intake_start_hydration), BluePrimary) { viewModel.addWater(); onClose() }
                    Spacer(modifier = Modifier.height(12.dp))
                    RecoveryActionButton(Icons.Default.HealthAndSafety, stringResource(R.string.intake_liver_support), GreenRecovery) { viewModel.navigateToRecoveryWithTab("1. LIVER & METABOLIC SUPPORT"); onClose() }
                } else {
                    RecoveryActionButton(Icons.Default.Air, stringResource(R.string.intake_breathing_exercise), BluePrimary) { viewModel.navigateToRecoveryWithTab("3. RESPIRATORY & LUNG HEALTH"); onClose() }
                }
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = onClose) { Text(stringResource(R.string.intake_return).uppercase(), fontWeight = FontWeight.Black) }
            }
        }
    }
}

@Composable
fun RecoveryActionButton(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.1f), contentColor = color)
    ) {
        Icon(icon, contentDescription = null); Spacer(modifier = Modifier.width(12.dp)); Text(label.uppercase(), fontWeight = FontWeight.Black)
    }
}
