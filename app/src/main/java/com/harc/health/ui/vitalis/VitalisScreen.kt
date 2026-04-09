@file:OptIn(ExperimentalMaterial3Api::class)

package com.harc.health.ui.vitalis

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.harc.health.R
import com.harc.health.logic.ActionDecisionEngine
import com.harc.health.logic.VitalisDictionary
import com.harc.health.model.*
import com.harc.health.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.util.*

// DESIGN SYSTEM CONSTANTS
val VitalisDeepNavy = Color(0xFF0A0E14)
val VitalisSlate = Color(0xFF161B22)
val VitalisSteel = Color(0xFF8B949E)
val VitalisMutedGold = Color(0xFFC9A66B)
val VitalisGlow = Color(0xFF58A6FF)
val VitalisEmerald = Color(0xFF3FB950)
val VitalisPureWhite = Color(0xFFF0F6FC)
val VitalisClinicalBlue = Color(0xFF1F6FEB)
val VitalisMelatoninAmber = Color(0xFFFFB74D)

val StandardPadding = 20.dp
val SectionSpacing = 32.dp
val CardCornerRadius = 24.dp

@Composable
fun VitalisScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val vitalisData by viewModel.vitalisData.collectAsState()
    val adeOutput by viewModel.adeOutput.collectAsState()
    var currentSection by remember { mutableStateOf("home") }
    var selectedModuleTitle by remember { mutableStateOf<String?>(null) }
    
    // Dialog States
    var activeActivity by remember { mutableStateOf<VitalisActivity?>(null) }
    var activeProtocol by remember { mutableStateOf<LongevityProtocol?>(null) }
    var activeAdeAction by remember { mutableStateOf<ActionDecisionEngine.AdeAction?>(null) }
    var showCelebration by remember { mutableStateOf<Int?>(null) }

    CircadianEnvironment { circadianColor ->
        Scaffold(
            containerColor = VitalisDeepNavy,
            topBar = {
                val title = when(currentSection) {
                    "home" -> "VITALIS COMMAND"
                    "intelligence" -> "BIO-INTELLIGENCE"
                    "protocols" -> "LONGEVITY PROTOCOLS"
                    "help" -> "EVIDENCE BASE"
                    "detail" -> "DOMAIN ANALYSIS"
                    "dictionary" -> "COMPOUND DICTIONARY"
                    else -> "VITALIS OS"
                }
                VitalisEnvironmentTopBar(
                    sectionTitle = title,
                    isHome = currentSection == "home",
                    accentColor = circadianColor,
                    onBack = { 
                        when (currentSection) {
                            "detail" -> currentSection = "intelligence"
                            "dictionary" -> currentSection = "help"
                            else -> currentSection = "home"
                        }
                    }
                )
            },
            bottomBar = {
                VitalisEnvironmentBottomBar(currentSection, circadianColor) { section: String ->
                    currentSection = section
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                Crossfade(targetState = currentSection, label = "section") { section ->
                    when(section) {
                        "home" -> VitalisHomeSection(vitalisData, adeOutput, circadianColor) { adeAction ->
                            activeAdeAction = adeAction
                        }
                        "intelligence" -> VitalisIntelligenceSection(vitalisData, circadianColor) { moduleTitle ->
                            selectedModuleTitle = moduleTitle
                            currentSection = "detail"
                        }
                        "protocols" -> VitalisProtocolsSection(vitalisData, circadianColor) { protocol ->
                            activeProtocol = protocol
                        }
                        "help" -> VitalisHelpCentreSection(circadianColor) {
                            currentSection = "dictionary"
                        }
                        "detail" -> VitalisModuleDetailScreen(selectedModuleTitle, vitalisData, circadianColor) { activity ->
                            activeActivity = activity
                        }
                        "dictionary" -> VitalisDictionarySection(circadianColor)
                    }
                }
            }

            // --- LIVE INTERVENTION DIALOGS (Inherited from the high-engagement model) ---
            if (activeAdeAction != null) {
                AdeInterventionDialog(accent = circadianColor, action = activeAdeAction!!, onComplete = { viewModel.toggleAction(activeAdeAction!!.id); showCelebration = activeAdeAction!!.instructionRes; activeAdeAction = null }, onDismiss = { activeAdeAction = null })
            }

            if (activeActivity != null) {
                ActivityInterventionDialog(accent = circadianColor, activity = activeActivity!!, onComplete = { viewModel.toggleAction(activeActivity!!.id); showCelebration = activeActivity!!.titleRes; activeActivity = null }, onDismiss = { activeActivity = null })
            }

            if (activeProtocol != null) {
                ProtocolExecutionDialog(accent = circadianColor, protocol = activeProtocol!!, onComplete = { viewModel.toggleAction(activeProtocol!!.id); showCelebration = R.string.vitalis_complete; activeProtocol = null }, onDismiss = { activeProtocol = null })
            }

            if (showCelebration != null) {
                LongevityCelebrationDialog(accent = circadianColor, itemNameRes = showCelebration!!, onDismiss = { showCelebration = null })
            }
        }
    }
}

@Composable
fun CircadianEnvironment(content: @Composable (Color) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    
    val circadianAccent = when(hour) {
        in 7..10 -> VitalisClinicalBlue
        in 11..17 -> VitalisGlow
        in 18..20 -> VitalisMutedGold
        else -> VitalisMelatoninAmber
    }
    
    content(circadianAccent)
}

@Composable
fun VitalisHomeSection(
    data: VitalisData, 
    ade: ActionDecisionEngine.AdeOutput?, 
    accent: Color,
    onExecuteAction: (ActionDecisionEngine.AdeAction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = StandardPadding),
        verticalArrangement = Arrangement.spacedBy(SectionSpacing)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)); GlobalStatusHeader(ade?.globalState ?: "SYNCING...") }
        
        item {
            ClinicalBioMap(data, accent)
        }

        item { LongevityScoreCard(data, accent) }

        if (ade != null) {
            item {
                ActionEngineCard(ade, accent, onExecuteAction)
            }
        }

        item { DomainHealthGrid(data.systems, accent) }
        
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun GlobalStatusHeader(state: String) {
    Column {
        Text(
            text = "SYSTEM STATUS",
            style = MaterialTheme.typography.labelSmall,
            color = VitalisSteel,
            letterSpacing = 2.sp
        )
        Text(
            text = state.uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = VitalisPureWhite
        )
    }
}

@Composable
fun LongevityScoreCard(data: VitalisData, accent: Color) {
    val offset = data.trajectoryModule.futureDirectives.firstOrNull() ?: "Predicting Trajectory..."
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardCornerRadius),
        color = VitalisSlate,
        border = BorderStroke(1.dp, VitalisSteel.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                CircularProgressIndicator(
                    progress = { data.longevityScore / 100f },
                    modifier = Modifier.fillMaxSize(),
                    color = accent,
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round,
                )
                Text(
                    text = data.longevityScore.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = VitalisPureWhite
                )
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column {
                Text(
                    text = "BIOLOGICAL TRAJECTORY",
                    style = MaterialTheme.typography.labelSmall,
                    color = VitalisSteel,
                    letterSpacing = 1.sp
                )
                Text(
                    text = offset,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun ActionEngineCard(ade: ActionDecisionEngine.AdeOutput, accent: Color, onExecute: (ActionDecisionEngine.AdeAction) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CardCornerRadius),
        color = accent.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = accent)
                Spacer(modifier = Modifier.width(12.dp))
                Text("EXPERT DIRECTIVES", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = accent, letterSpacing = 1.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ade.primaryActions.forEach { action ->
                Surface(
                    onClick = { onExecute(action) },
                    modifier = Modifier.padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = VitalisDeepNavy.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(action.instructionRes), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = VitalisPureWhite)
                            Text(stringResource(action.contextRes), style = MaterialTheme.typography.bodySmall, color = VitalisSteel)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = VitalisSteel, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DomainHealthGrid(systems: Map<String, SystemHealth>, accent: Color) {
    Column {
        Text("BIO-REGULATORY SYSTEMS", style = MaterialTheme.typography.labelSmall, color = VitalisSteel, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        val systemList = systems.entries.toList()
        for (i in systemList.indices step 2) {
            Row(modifier = Modifier.fillMaxWidth()) {
                DomainCard(systemList[i].key, systemList[i].value, accent, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                if (i + 1 < systemList.size) {
                    DomainCard(systemList[i+1].key, systemList[i+1].value, accent, modifier = Modifier.weight(1f))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun DomainCard(name: String, system: SystemHealth, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = VitalisSlate,
        border = BorderStroke(1.dp, VitalisSteel.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(name.uppercase(), style = MaterialTheme.typography.labelSmall, color = VitalisSteel, fontSize = 9.sp)
                Text(system.trend, style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("${system.score}%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = VitalisPureWhite)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { system.score / 100f },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                color = accent,
                trackColor = Color.White.copy(alpha = 0.05f)
            )
        }
    }
}

@Composable
fun VitalisIntelligenceSection(data: VitalisData, accent: Color, onModuleClick: (String) -> Unit) {
    val modules = listOf(
        "SLEEP & CIRCADIAN" to data.sleepModule.activities,
        "CARDIOVASCULAR" to data.cardioModule.activities,
        "METABOLIC & GLYCEMIC" to data.metabolicModule.activities,
        "STRESS & NEURO" to data.stressModule.activities,
        "PHYSICAL & ARTICULAR" to data.physicalModule.activities,
        "COGNITIVE & NEURAL" to data.cognitiveModule.activities
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = StandardPadding),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        
        items(modules) { (title, activities) ->
            val insight = activities.firstOrNull()?.let { stringResource(it.researchInsightRes) } ?: "SYSTEM SYNCHRONIZING..."
            IntelligenceModulePlaceholder(title, insight, accent) {
                onModuleClick(title)
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun IntelligenceModulePlaceholder(title: String, insight: String, accent: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = VitalisSlate,
        border = BorderStroke(1.dp, VitalisSteel.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Analytics, null, tint = accent, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VitalisPureWhite)
                    Text("DOMAIN ACTIVE", style = MaterialTheme.typography.labelSmall, color = accent)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(insight, style = MaterialTheme.typography.bodyMedium, color = VitalisSteel, lineHeight = 20.sp)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("VIEW CLINICAL ANALYSIS", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = VitalisPureWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = VitalisPureWhite, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun VitalisProtocolsSection(data: VitalisData, accent: Color, onProtocolClick: (LongevityProtocol) -> Unit) {
    val protocols = data.protocolModule.activeProtocols
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = StandardPadding),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { 
            Spacer(modifier = Modifier.height(16.dp))
            Text("STRUCTURED LONGEVITY", style = MaterialTheme.typography.labelSmall, color = VitalisSteel, letterSpacing = 2.sp)
        }
        
        items(protocols) { protocol ->
            ProtocolCard(protocol, accent) { onProtocolClick(protocol) }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun ProtocolCard(protocol: LongevityProtocol, accent: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = VitalisSlate,
        border = BorderStroke(1.dp, VitalisSteel.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("PROTOCOL", style = MaterialTheme.typography.labelSmall, color = accent, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(protocol.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = VitalisPureWhite)
            Spacer(modifier = Modifier.height(12.dp))
            Text(protocol.purpose, style = MaterialTheme.typography.bodyMedium, color = VitalisSteel)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = accent.copy(alpha = 0.1f), shape = CircleShape) {
                    Text(
                        text = "ACTIVE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = accent
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("INITIATE", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = VitalisPureWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.PlayCircle, null, tint = VitalisPureWhite, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun VitalisHelpCentreSection(accent: Color, onOpenDictionary: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = StandardPadding),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }
        
        item {
            Surface(
                onClick = onOpenDictionary,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = accent,
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("COMPOUND DICTIONARY", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = VitalisDeepNavy)
                        Text("Clinical evidence on compounds & intake.", style = MaterialTheme.typography.bodyMedium, color = VitalisDeepNavy.copy(alpha = 0.7f))
                    }
                    Icon(Icons.Default.MenuBook, null, tint = VitalisDeepNavy, modifier = Modifier.size(40.dp))
                }
            }
        }

        item {
            Text("VITALIS EVIDENCE BASE", style = MaterialTheme.typography.labelSmall, color = VitalisSteel, letterSpacing = 2.sp)
        }

        item { HelpItemCard("Protocol Hierarchy", "Understanding how Vitalis prioritizes interventions.", Icons.Default.Layers) }
        item { HelpItemCard("Bio-Marker Mapping", "Clinical definitions of tracked metrics.", Icons.Default.Biotech) }
    }
}

@Composable
fun HelpItemCard(title: String, desc: String, icon: ImageVector) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = VitalisSlate,
        border = BorderStroke(1.dp, VitalisSteel.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = VitalisSteel, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VitalisPureWhite)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = VitalisSteel)
            }
        }
    }
}

@Composable
fun VitalisModuleDetailScreen(title: String?, data: VitalisData, accent: Color, onActivityClick: (VitalisActivity) -> Unit) {
    val activities = when(title) {
        "SLEEP & CIRCADIAN" -> data.sleepModule.activities
        "CARDIOVASCULAR" -> data.cardioModule.activities
        "METABOLIC & GLYCEMIC" -> data.metabolicModule.activities
        "STRESS & NEURO" -> data.stressModule.activities
        "PHYSICAL & ARTICULAR" -> data.physicalModule.activities
        "COGNITIVE & NEURAL" -> data.cognitiveModule.activities
        else -> emptyList()
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = StandardPadding),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(title ?: "DOMAIN ANALYSIS", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = VitalisPureWhite)
        }

        // --- NEW: BIOMETRIC MARKERS SECTION ---
        item {
            Text("CLINICAL MARKERS", style = MaterialTheme.typography.labelSmall, color = VitalisSteel, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = VitalisSlate) {
                Column(modifier = Modifier.padding(20.dp)) {
                    MarkerItem("HRV Baseline", "64ms", "Optimal", accent)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
                    MarkerItem("Systemic pH", "7.38", "Balanced", accent)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))
                    MarkerItem("Glycemic Load", "Low", "Stable", accent)
                }
            }
        }

        item {
            Text("TARGETED DIRECTIVES", style = MaterialTheme.typography.labelSmall, color = VitalisSteel, letterSpacing = 2.sp)
        }

        items(activities) { activity ->
            Surface(
                onClick = { onActivityClick(activity) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = VitalisSlate,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(activity.titleRes), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VitalisPureWhite)
                        Text(stringResource(activity.phaseRes).uppercase(), style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.Black)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = accent)
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun MarkerItem(label: String, value: String, status: String, accent: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = VitalisSteel)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = VitalisPureWhite)
        }
        Text(status.uppercase(), style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.Black)
    }
}

@Composable
fun VitalisDictionarySection(accent: Color) {
    var query by remember { mutableStateOf("") }
    val result = remember(query) { VitalisDictionary.search(query) }
    
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = StandardPadding)) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = VitalisSlate,
            border = BorderStroke(1.dp, VitalisSteel.copy(alpha = 0.2f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, null, tint = VitalisSteel)
                Spacer(modifier = Modifier.width(12.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = VitalisPureWhite),
                    modifier = Modifier.weight(1f),
                    cursorBrush = Brush.verticalGradient(listOf(accent, accent))
                ) {
                    if (query.isEmpty()) Text("Search compounds (e.g. NAC, B1)...", color = VitalisSteel)
                    it()
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Crossfade(targetState = query.isEmpty(), label = "dict") { isEmpty ->
            if (isEmpty) {
                Column {
                    Text("CLINICALLY INDEXED COMPOUNDS", style = MaterialTheme.typography.labelSmall, color = VitalisSteel, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Vitalis references clinical trials to provide evidence-based guidance on therapeutic intake.", color = VitalisSteel)
                }
            } else {
                DictionaryResultContent(result)
            }
        }
    }
}

@Composable
fun DictionaryResultContent(result: VitalisDictionary.Entry?) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (result != null) {
            Text(stringResource(result.nameRes).uppercase(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = VitalisPureWhite)
            Text(stringResource(result.impactAreaRes).uppercase(), style = MaterialTheme.typography.labelLarge, color = VitalisSteel, letterSpacing = 2.sp)
            
            Spacer(modifier = Modifier.height(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = result.verdict.color.copy(alpha = 0.1f),
                    border = BorderStroke(2.dp, result.verdict.color)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when(result.verdict) {
                                VitalisDictionary.Verdict.PROTECT -> Icons.Default.Shield
                                VitalisDictionary.Verdict.CAUTION -> Icons.Default.Warning
                                VitalisDictionary.Verdict.UNKNOWN -> Icons.Default.Help
                            },
                            contentDescription = null,
                            tint = result.verdict.color,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(result.verdict.labelRes), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = result.verdict.color, letterSpacing = 1.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), color = VitalisSlate) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("CLINICAL RATIONALE", style = MaterialTheme.typography.labelSmall, color = VitalisSteel, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(stringResource(result.rationaleRes), style = MaterialTheme.typography.bodyLarge, color = VitalisPureWhite, lineHeight = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun VitalisEnvironmentTopBar(sectionTitle: String, isHome: Boolean, accentColor: Color, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "VITALIS OS",
                    style = MaterialTheme.typography.labelSmall,
                    color = VitalisSteel,
                    letterSpacing = 4.sp
                )
                Text(
                    text = sectionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = VitalisPureWhite
                )
            }
        },
        navigationIcon = {
            if (!isHome) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = VitalisPureWhite)
                }
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, "Notifications", tint = VitalisPureWhite)
            }
        }
    )
}

@Composable
fun VitalisEnvironmentBottomBar(current: String, accent: Color, onNavigate: (String) -> Unit) {
    NavigationBar(
        containerColor = VitalisDeepNavy,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = current == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Default.GridView, null) },
            label = { Text("COMMAND") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = accent,
                selectedTextColor = accent,
                indicatorColor = Color.Transparent,
                unselectedIconColor = VitalisSteel,
                unselectedTextColor = VitalisSteel
            )
        )
        NavigationBarItem(
            selected = current == "intelligence" || current == "detail",
            onClick = { onNavigate("intelligence") },
            icon = { Icon(Icons.Default.Analytics, null) },
            label = { Text("INTEL") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = accent,
                selectedTextColor = accent,
                indicatorColor = Color.Transparent,
                unselectedIconColor = VitalisSteel,
                unselectedTextColor = VitalisSteel
            )
        )
        NavigationBarItem(
            selected = current == "protocols",
            onClick = { onNavigate("protocols") },
            icon = { Icon(Icons.Default.Terminal, null) },
            label = { Text("PROTOCOLS") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = accent,
                selectedTextColor = accent,
                indicatorColor = Color.Transparent,
                unselectedIconColor = VitalisSteel,
                unselectedTextColor = VitalisSteel
            )
        )
        NavigationBarItem(
            selected = current == "help" || current == "dictionary",
            onClick = { onNavigate("help") },
            icon = { Icon(Icons.Default.MenuBook, null) },
            label = { Text("LIBRARY") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = accent,
                selectedTextColor = accent,
                indicatorColor = Color.Transparent,
                unselectedIconColor = VitalisSteel,
                unselectedTextColor = VitalisSteel
            )
        )
    }
}

@Composable
fun ClinicalBioMap(data: VitalisData, accent: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "map")
    Surface(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(CardCornerRadius),
        color = VitalisSlate,
        border = BorderStroke(1.dp, VitalisSteel.copy(alpha = 0.1f))
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Column {
                Text("NEURAL CONNECTIVITY & SYSTEMIC SYNC", style = MaterialTheme.typography.labelSmall, color = VitalisSteel, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    repeat(16) { i ->
                        val animHeight by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween((800..2000).random(), easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "h"
                        )
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height((80 * animHeight).dp)
                                .padding(horizontal = 2.dp),
                            color = if (i % 4 == 0) accent else accent.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        ) {}
                    }
                }
            }
        }
    }
}

// --- SHARED DIALOG COMPONENTS (Inherited from Engagement Engine) ---

@Composable
fun AdeInterventionDialog(accent: Color, action: ActionDecisionEngine.AdeAction, onComplete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VitalisSlate,
        title = { Text(stringResource(action.instructionRes).uppercase(), fontWeight = FontWeight.Black, color = VitalisPureWhite) },
        text = {
            Column {
                Text(stringResource(action.contextRes), color = VitalisSteel)
                Spacer(modifier = Modifier.height(16.dp))
                Surface(color = accent.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(stringResource(action.researchInsightRes), modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = accent)
                }
            }
        },
        confirmButton = {
            Button(onClick = onComplete, colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = VitalisDeepNavy)) {
                Text("LOG COMPLETION", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CLOSE", color = VitalisSteel) }
        }
    )
}

@Composable
fun ActivityInterventionDialog(accent: Color, activity: VitalisActivity, onComplete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VitalisSlate,
        title = { Text(stringResource(activity.titleRes).uppercase(), fontWeight = FontWeight.Black, color = VitalisPureWhite) },
        text = {
            Column {
                Text(stringResource(activity.instructionsRes), color = VitalisSteel)
                Spacer(modifier = Modifier.height(16.dp))
                Surface(color = accent.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(stringResource(activity.researchInsightRes), modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = accent)
                }
            }
        },
        confirmButton = {
            Button(onClick = onComplete, colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = VitalisDeepNavy)) {
                Text("COMPLETE", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CLOSE", color = VitalisSteel) }
        }
    )
}

@Composable
fun ProtocolExecutionDialog(accent: Color, protocol: LongevityProtocol, onComplete: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = VitalisDeepNavy) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(StandardPadding)) {
                item {
                    Text("LONGEVITY PROTOCOL", color = accent, style = MaterialTheme.typography.labelLarge, letterSpacing = 2.sp)
                    Text(protocol.title, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = VitalisPureWhite)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Surface(color = VitalisSlate, shape = RoundedCornerShape(16.dp)) {
                        Text(protocol.purpose, modifier = Modifier.padding(20.dp), color = VitalisSteel)
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("EXECUTION STEPS", style = MaterialTheme.typography.labelSmall, color = VitalisSteel, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                items(protocol.instructions) { step ->
                    Row(modifier = Modifier.padding(vertical = 12.dp)) {
                        Surface(shape = CircleShape, color = accent, modifier = Modifier.size(24.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text((protocol.instructions.indexOf(step) + 1).toString(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = VitalisDeepNavy)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(step, color = VitalisPureWhite, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = VitalisDeepNavy),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("LOG PROTOCOL COMPLETED", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("CANCEL", color = VitalisSteel)
                    }
                }
            }
        }
    }
}

@Composable
fun LongevityCelebrationDialog(accent: Color, itemNameRes: Int, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = VitalisSlate,
            border = BorderStroke(2.dp, accent)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.1f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, null, tint = accent, modifier = Modifier.size(40.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("BIOLOGICAL OPTIMIZATION", style = MaterialTheme.typography.labelSmall, color = accent, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(itemNameRes).uppercase(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = VitalisPureWhite, textAlign = TextAlign.Center)
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("Intervention logged. System recalibrating...", textAlign = TextAlign.Center, color = VitalisSteel)
                
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = VitalisDeepNavy)
                ) {
                    Text("CONTINUE SCAN")
                }
            }
        }
    }
}
