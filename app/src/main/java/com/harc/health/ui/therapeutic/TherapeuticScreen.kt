package com.harc.health.ui.therapeutic

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.R

enum class SanctuaryDomain(val titleRes: Int, val icon: ImageVector, val color: Color) {
    HOME(R.string.domain_home, Icons.Default.GridView, Color(0xFF81D4FA)),
    HEART(R.string.domain_heart, Icons.Default.Favorite, Color(0xFFF48FB1)),
    MIND(R.string.domain_mind, Icons.Default.Psychology, Color(0xFFCE93D8)),
    BODY(R.string.domain_body, Icons.Default.SelfImprovement, Color(0xFFA5D6A7))
}

data class TherapeuticSession(
    val id: String,
    val protocolId: String,
    val titleRes: Int,
    val durationRes: Int,
    val domain: SanctuaryDomain,
    val descriptionRes: Int,
    val frequencyRes: Int,
    val targetRes: Int,
    val icon: ImageVector,
    val intensity: Float,
    val audioResName: String,
    val guidanceId: String? = null,
    val guidanceRes: Int? = null
)

data class GuidanceEntry(
    val id: String,
    val titleRes: Int,
    val descRes: Int,
    val guidanceRes: Int,
    val domain: SanctuaryDomain,
    val icon: ImageVector,
    val rationaleRes: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TherapeuticScreen(
    onBack: () -> Unit,
    onSessionClick: (TherapeuticSession) -> Unit
) {
    var selectedDomain by remember { mutableStateOf(SanctuaryDomain.HOME) }
    var selectedEmotion by remember { mutableStateOf<String?>(null) }
    var prepSessionToShow by remember { mutableStateOf<TherapeuticSession?>(null) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "sanctuary_breath")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "breath"
    )

        val allSessions = remember {
        listOf(
            // HOME / GENERAL
            TherapeuticSession("st_1", "P-432", R.string.session_stress_reset_title, R.string.duration_10min, SanctuaryDomain.HOME, R.string.session_stress_reset_desc, R.string.freq_432hz, R.string.target_amygdala, Icons.Default.Cloud, 0.4f, "hz_432", "g_1", R.string.session_stress_reset_guidance),
            TherapeuticSession("st_2", "P-174", R.string.session_panic_brake_title, R.string.duration_5min, SanctuaryDomain.BODY, R.string.session_panic_brake_desc, R.string.freq_174hz, R.string.target_vagal_tone, Icons.Default.FlashOn, 0.8f, "hz_174", "g_2", R.string.session_panic_brake_guidance),
            
            // BODY
            TherapeuticSession("sl_1", "P-DLT", R.string.session_insomnia_relief_title, R.string.duration_20min, SanctuaryDomain.BODY, R.string.session_insomnia_relief_desc, R.string.freq_delta, R.string.target_sleep_architecture, Icons.Default.Bedtime, 0.2f, "brown_noise", "g_14", R.string.session_insomnia_relief_guidance),
            TherapeuticSession("sl_2", "P-OFF", R.string.session_overthinker_off_title, R.string.duration_20min, SanctuaryDomain.BODY, R.string.session_overthinker_off_desc, R.string.freq_delta, R.string.target_sleep_architecture, Icons.Default.Nightlight, 0.3f, "brown_noise", "g_13", R.string.session_overthinker_off_guidance),
            TherapeuticSession("pn_1", "P-174", R.string.session_chronic_pain_title, R.string.duration_15min, SanctuaryDomain.BODY, R.string.session_chronic_pain_desc, R.string.freq_174hz, R.string.target_acute, Icons.Default.MedicalServices, 0.5f, "hz_174", "g_17", R.string.session_chronic_pain_guidance),
            TherapeuticSession("ba_1", "P-528", R.string.session_body_acceptance_title, R.string.duration_12min, SanctuaryDomain.BODY, R.string.session_body_acceptance_desc, R.string.freq_432hz, R.string.target_systemic, Icons.Default.AccessibilityNew, 0.4f, "hz_528", "g_16", R.string.session_body_acceptance_guidance),
            TherapeuticSession("nm_1", "P-GND", R.string.session_post_nightmare_title, R.string.duration_5min, SanctuaryDomain.BODY, R.string.session_post_nightmare_desc, R.string.freq_174hz, R.string.target_acute, Icons.Default.WbTwilight, 0.7f, "hz_174", "g_15", R.string.session_post_nightmare_guidance),

            // HEART
            TherapeuticSession("fg_1", "P-396", R.string.session_forgiveness_title, R.string.duration_12min, SanctuaryDomain.HEART, R.string.session_forgiveness_desc, R.string.freq_396hz, R.string.target_emotional_resilience, Icons.Default.Favorite, 0.4f, "hz_396", "g_11", R.string.session_forgiveness_guidance),
            TherapeuticSession("bt_1", "P-741", R.string.session_betrayal_healing_title, R.string.duration_15min, SanctuaryDomain.HEART, R.string.session_betrayal_healing_desc, R.string.freq_741hz, R.string.target_systemic_reset, Icons.Default.Shield, 0.5f, "hz_741", "g_3", R.string.session_betrayal_healing_guidance),
            TherapeuticSession("gr_1", "P-417", R.string.session_grief_integration_title, R.string.duration_15min, SanctuaryDomain.HEART, R.string.session_grief_integration_desc, R.string.freq_396hz, R.string.target_emotional_resilience, Icons.Default.HistoryEdu, 0.3f, "hz_417", "g_12", R.string.session_grief_integration_guidance),
            TherapeuticSession("mr_1", "P-DLY", R.string.session_delay_marriage_title, R.string.duration_10min, SanctuaryDomain.HEART, R.string.session_delay_marriage_desc, R.string.freq_432hz, R.string.target_systemic, Icons.Default.CalendarMonth, 0.4f, "hz_432", "g_5", R.string.session_delay_marriage_guidance),
            TherapeuticSession("pr_1", "P-PRN", R.string.session_delay_parenthood_title, R.string.duration_12min, SanctuaryDomain.HEART, R.string.session_delay_parenthood_desc, R.string.freq_528hz, R.string.target_systemic, Icons.Default.ChildCare, 0.4f, "hz_528", "g_18", R.string.session_delay_parenthood_guidance),
            TherapeuticSession("sj_1", "P-528", R.string.session_self_judgment_title, R.string.duration_10min, SanctuaryDomain.HEART, R.string.session_self_judgment_desc, R.string.freq_528hz, R.string.target_emotional_resilience, Icons.Default.SelfImprovement, 0.4f, "hz_528", "g_9", R.string.session_self_judgment_guidance),
            TherapeuticSession("rg_1", "P-396", R.string.session_regret_release_title, R.string.duration_12min, SanctuaryDomain.HEART, R.string.session_regret_release_desc, R.string.freq_396hz, R.string.target_emotional_resilience, Icons.Default.Undo, 0.4f, "hz_396", "g_10", R.string.session_regret_release_guidance),

            // MIND
            TherapeuticSession("cl_1", "P-ALP", R.string.session_focus_alpha_title, R.string.duration_15min, SanctuaryDomain.MIND, R.string.session_focus_alpha_desc, R.string.freq_alpha, R.string.target_dopamine_stability, Icons.Default.Bolt, 0.6f, "alpha_10hz", "g_19", R.string.session_focus_alpha_guidance),
            TherapeuticSession("bn_1", "P-SHD", R.string.session_burnout_shield_title, R.string.duration_15min, SanctuaryDomain.MIND, R.string.session_burnout_shield_desc, R.string.freq_alpha, R.string.target_systemic_reset, Icons.Default.VpnLock, 0.5f, "alpha_10hz", "g_4", R.string.session_burnout_shield_guidance),
            TherapeuticSession("sa_1", "P-BUF", R.string.session_social_buffer_title, R.string.duration_5min, SanctuaryDomain.MIND, R.string.session_social_buffer_desc, R.string.freq_174hz, R.string.target_vagal_tone, Icons.Default.Groups, 0.6f, "hz_174", "g_20", R.string.session_social_buffer_guidance),
            TherapeuticSession("mf_1", "P-INT", R.string.session_manifest_intent_title, R.string.duration_10min, SanctuaryDomain.MIND, R.string.session_manifest_intent_desc, R.string.freq_alpha, R.string.target_dopamine_stability, Icons.Default.TrackChanges, 0.7f, "alpha_10hz", "g_21", R.string.session_manifest_intent_guidance),
            TherapeuticSession("cr_1", "P-DLY", R.string.session_delay_career_title, R.string.duration_12min, SanctuaryDomain.MIND, R.string.session_delay_career_desc, R.string.freq_alpha, R.string.target_systemic, Icons.Default.WorkHistory, 0.5f, "alpha_10hz", "g_22", R.string.session_delay_career_guidance),
            TherapeuticSession("ct_1", "P-396", R.string.session_comparison_trap_title, R.string.duration_10min, SanctuaryDomain.MIND, R.string.session_comparison_trap_desc, R.string.freq_396hz, R.string.target_emotional_resilience, Icons.Default.VisibilityOff, 0.4f, "hz_396", "g_23", R.string.session_comparison_trap_guidance),
            TherapeuticSession("is_1", "P-741", R.string.session_imposter_dissolver_title, R.string.duration_12min, SanctuaryDomain.MIND, R.string.session_imposter_dissolver_desc, R.string.freq_741hz, R.string.target_systemic_reset, Icons.Default.Gavel, 0.5f, "hz_741", "g_24", R.string.session_imposter_dissolver_guidance),
            TherapeuticSession("cb_1", "P-THG", R.string.session_creative_block_title, R.string.duration_15min, SanctuaryDomain.MIND, R.string.session_creative_block_desc, R.string.freq_alpha, R.string.target_dopamine_stability, Icons.Default.Brush, 0.6f, "alpha_10hz", "g_25", R.string.session_creative_block_guidance),
            TherapeuticSession("am_1", "P-852", R.string.session_abundance_mindset_title, R.string.duration_12min, SanctuaryDomain.MIND, R.string.session_abundance_mindset_desc, R.string.freq_alpha, R.string.target_systemic, Icons.Default.Diamond, 0.5f, "hz_852", "g_26", R.string.session_abundance_mindset_guidance),
            TherapeuticSession("cg_1", "P-GMA", R.string.session_courage_title, R.string.duration_10min, SanctuaryDomain.MIND, R.string.session_courage_desc, R.string.freq_alpha, R.string.target_systemic, Icons.Default.ShieldMoon, 0.6f, "hz_432", "g_27", R.string.session_courage_guidance)
        )
    }

    val guidanceLibrary = remember {
        listOf(
            GuidanceEntry("g_1", R.string.session_stress_reset_title, R.string.session_stress_reset_desc, R.string.session_stress_reset_guidance, SanctuaryDomain.BODY, Icons.Default.Spa, R.string.session_stress_reset_rationale),
            GuidanceEntry("g_2", R.string.session_panic_brake_title, R.string.session_panic_brake_desc, R.string.session_panic_brake_guidance, SanctuaryDomain.BODY, Icons.Default.Warning, R.string.session_panic_brake_rationale),
            GuidanceEntry("g_3", R.string.session_betrayal_healing_title, R.string.session_betrayal_healing_desc, R.string.session_betrayal_healing_guidance, SanctuaryDomain.HEART, Icons.Default.FavoriteBorder, R.string.session_betrayal_healing_rationale),
            GuidanceEntry("g_4", R.string.session_burnout_shield_title, R.string.session_burnout_shield_desc, R.string.session_burnout_shield_guidance, SanctuaryDomain.MIND, Icons.Default.Shield, R.string.session_burnout_shield_rationale),
            GuidanceEntry("g_5", R.string.session_delay_marriage_title, R.string.session_delay_marriage_desc, R.string.session_delay_marriage_guidance, SanctuaryDomain.HEART, Icons.Default.Favorite, R.string.session_delay_marriage_rationale),
            GuidanceEntry("g_9", R.string.session_self_judgment_title, R.string.session_self_judgment_desc, R.string.session_self_judgment_guidance, SanctuaryDomain.HEART, Icons.Default.SelfImprovement, R.string.session_self_judgment_rationale),
            GuidanceEntry("g_10", R.string.session_regret_release_title, R.string.session_regret_release_desc, R.string.session_regret_release_guidance, SanctuaryDomain.HEART, Icons.Default.Undo, R.string.session_regret_release_rationale),
            GuidanceEntry("g_11", R.string.session_forgiveness_title, R.string.session_forgiveness_desc, R.string.session_forgiveness_guidance, SanctuaryDomain.HEART, Icons.Default.Favorite, R.string.session_forgiveness_rationale),
            GuidanceEntry("g_12", R.string.session_grief_integration_title, R.string.session_grief_integration_desc, R.string.session_grief_integration_guidance, SanctuaryDomain.HEART, Icons.Default.HistoryEdu, R.string.session_grief_integration_rationale),
            GuidanceEntry("g_13", R.string.session_overthinker_off_title, R.string.session_overthinker_off_desc, R.string.session_overthinker_off_guidance, SanctuaryDomain.BODY, Icons.Default.Nightlight, R.string.session_overthinker_off_rationale),
            GuidanceEntry("g_14", R.string.session_insomnia_relief_title, R.string.session_insomnia_relief_desc, R.string.session_insomnia_relief_guidance, SanctuaryDomain.BODY, Icons.Default.Bedtime, R.string.session_insomnia_relief_rationale),
            GuidanceEntry("g_15", R.string.session_post_nightmare_title, R.string.session_post_nightmare_desc, R.string.session_post_nightmare_guidance, SanctuaryDomain.BODY, Icons.Default.WbTwilight, R.string.session_post_nightmare_rationale),
            GuidanceEntry("g_16", R.string.session_body_acceptance_title, R.string.session_body_acceptance_desc, R.string.session_body_acceptance_guidance, SanctuaryDomain.BODY, Icons.Default.AccessibilityNew, R.string.session_body_acceptance_rationale),
            GuidanceEntry("g_17", R.string.session_chronic_pain_title, R.string.session_chronic_pain_desc, R.string.session_chronic_pain_guidance, SanctuaryDomain.BODY, Icons.Default.MedicalServices, R.string.session_chronic_pain_rationale),
            GuidanceEntry("g_18", R.string.session_delay_parenthood_title, R.string.session_delay_parenthood_desc, R.string.session_delay_parenthood_guidance, SanctuaryDomain.HEART, Icons.Default.ChildCare, R.string.session_delay_parenthood_rationale),
            GuidanceEntry("g_19", R.string.session_focus_alpha_title, R.string.session_focus_alpha_desc, R.string.session_focus_alpha_guidance, SanctuaryDomain.MIND, Icons.Default.Bolt, R.string.session_focus_alpha_rationale),
            GuidanceEntry("g_20", R.string.session_social_buffer_title, R.string.session_social_buffer_desc, R.string.session_social_buffer_guidance, SanctuaryDomain.MIND, Icons.Default.Groups, R.string.session_social_buffer_rationale),
            GuidanceEntry("g_21", R.string.session_manifest_intent_title, R.string.session_manifest_intent_desc, R.string.session_manifest_intent_guidance, SanctuaryDomain.MIND, Icons.Default.TrackChanges, R.string.session_manifest_intent_rationale),
            GuidanceEntry("g_22", R.string.session_delay_career_title, R.string.session_delay_career_desc, R.string.session_delay_career_guidance, SanctuaryDomain.MIND, Icons.Default.WorkHistory, R.string.session_delay_career_rationale),
            GuidanceEntry("g_23", R.string.session_comparison_trap_title, R.string.session_comparison_trap_desc, R.string.session_comparison_trap_guidance, SanctuaryDomain.MIND, Icons.Default.VisibilityOff, R.string.session_comparison_trap_rationale),
            GuidanceEntry("g_24", R.string.session_imposter_dissolver_title, R.string.session_imposter_dissolver_desc, R.string.session_imposter_dissolver_guidance, SanctuaryDomain.MIND, Icons.Default.Gavel, R.string.session_imposter_dissolver_rationale),
            GuidanceEntry("g_25", R.string.session_creative_block_title, R.string.session_creative_block_desc, R.string.session_creative_block_guidance, SanctuaryDomain.MIND, Icons.Default.Brush, R.string.session_creative_block_rationale),
            GuidanceEntry("g_26", R.string.session_abundance_mindset_title, R.string.session_abundance_mindset_desc, R.string.session_abundance_mindset_guidance, SanctuaryDomain.MIND, Icons.Default.Diamond, R.string.session_abundance_mindset_rationale),
            GuidanceEntry("g_27", R.string.session_courage_title, R.string.session_courage_desc, R.string.session_courage_guidance, SanctuaryDomain.MIND, Icons.Default.ShieldMoon, R.string.session_courage_rationale)
        )
    }

    val filteredSessions = remember(selectedEmotion, selectedDomain) {
        if (selectedEmotion != null) {
            allSessions.filter { it.intensity > 0.5f }
        } else if (selectedDomain != SanctuaryDomain.HOME) {
            allSessions.filter { it.domain == selectedDomain }
        } else {
            allSessions
        }
    }

    if (prepSessionToShow != null) {
        val guidance = guidanceLibrary.find { it.id == prepSessionToShow?.guidanceId } ?: guidanceLibrary[0]
        ModalBottomSheet(
            onDismissRequest = { prepSessionToShow = null },
            containerColor = Color(0xFF0F1216),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.2f)) }
        ) {
            GuidanceDetailView(
                entry = guidance,
                onStartSession = {
                    val session = prepSessionToShow!!
                    prepSessionToShow = null
                    onSessionClick(session)
                }
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.therapeutic_header), style = MaterialTheme.typography.labelLarge, letterSpacing = 3.sp, fontWeight = FontWeight.Black)
                        Text(stringResource(R.string.therapeutic_sub_header), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontSize = 8.sp)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF080A0C)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Bio-Sync Glow
            Canvas(modifier = Modifier.fillMaxSize().blur(120.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(colors = listOf(selectedDomain.color.copy(alpha = 0.15f), Color.Transparent), center = center, radius = (size.maxDimension / 2) * breathScale)
                )
            }


            Column(modifier = Modifier.padding(padding)) {
                DomainTabs(selectedDomain) { 
                    selectedDomain = it
                    selectedEmotion = null
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (selectedDomain == SanctuaryDomain.HOME) {
                        item {
                            EmotionalCompass(selectedEmotion) { selectedEmotion = if (selectedEmotion == it) null else it }
                        }
                        
                        item {
                            QuickNeuralBrakes { prepSessionToShow = it }
                        }

                        item {
                            PreparationLibrary(guidanceLibrary) { guidance ->
                                // Find a session matching this guidance or just show the guidance
                                val matchingSession = allSessions.find { it.guidanceId == guidance.id }
                                if (matchingSession != null) {
                                    prepSessionToShow = matchingSession
                                } else {
                                    // Fallback: If no direct session, just show guidance as informational
                                    // (We'll handle this by making onStartSession optional in GuidanceDetailView)
                                    prepSessionToShow = TherapeuticSession(
                                        id = "info",
                                        protocolId = "P-INF",
                                        titleRes = guidance.titleRes,
                                        durationRes = R.string.duration_infinite,
                                        domain = guidance.domain,
                                        descriptionRes = guidance.descRes,
                                        frequencyRes = R.string.freq_432hz,
                                        targetRes = R.string.target_systemic,
                                        icon = guidance.icon,
                                        intensity = 0f,
                                        audioResName = "",
                                        guidanceId = guidance.id,
                                        guidanceRes = guidance.guidanceRes
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = if (selectedEmotion != null) stringResource(R.string.recommended_for, selectedEmotion!!.uppercase()) else stringResource(R.string.active_protocols),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selectedEmotion != null) Color.Red else Color.White.copy(alpha = 0.5f),
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(filteredSessions) { session ->
                        NeuroSessionCard(session) { prepSessionToShow = it }
                    }
                    
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
fun PreparationLibrary(items: List<GuidanceEntry>, onSelect: (GuidanceEntry) -> Unit) {
    Column {
        Text(stringResource(R.string.clinical_prep_library), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(items) { entry ->
                Surface(
                    onClick = { onSelect(entry) },
                    modifier = Modifier.size(width = 200.dp, height = 120.dp),
                    color = Color.White.copy(alpha = 0.03f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, entry.domain.color.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Icon(entry.icon, null, tint = entry.domain.color, modifier = Modifier.size(24.dp))
                        Text(stringResource(entry.titleRes), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 18.sp, maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
fun GuidanceDetailView(entry: GuidanceEntry, onStartSession: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            color = entry.domain.color.copy(alpha = 0.1f),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(entry.icon, null, tint = entry.domain.color, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(stringResource(entry.titleRes).uppercase(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White, textAlign = TextAlign.Center)
        Text(stringResource(R.string.player_emotional_guidance), style = MaterialTheme.typography.labelSmall, color = entry.domain.color, letterSpacing = 2.sp)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White.copy(alpha = 0.02f),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
        ) {
            Text(
                text = stringResource(entry.guidanceRes),
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                lineHeight = 28.sp,
                textAlign = TextAlign.Justify
            )
        }

        if (entry.rationaleRes != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(R.string.clinical_insight), style = MaterialTheme.typography.labelSmall, color = entry.domain.color, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = entry.domain.color.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, entry.domain.color.copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, null, tint = entry.domain.color, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(entry.rationaleRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        if (onStartSession != null) {
            Button(
                onClick = onStartSession,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = entry.domain.color),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.Black)
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(R.string.begin_sync), fontWeight = FontWeight.Black, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        Text(
            text = "${stringResource(R.string.prep_steps_title)}\n${stringResource(R.string.prep_step_1)}\n${stringResource(R.string.prep_step_2)}\n${stringResource(R.string.prep_step_3)}\n${stringResource(R.string.prep_step_4)}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.4f),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun EmotionalCompass(selected: String?, onSelect: (String) -> Unit) {
    val emotions = listOf(
        stringResource(R.string.emotion_overwhelmed) to Color.Red,
        stringResource(R.string.emotion_scattered) to Color(0xFFCE93D8),
        stringResource(R.string.emotion_heavy) to Color(0xFFF48FB1),
        stringResource(R.string.emotion_burned_out) to Color(0xFFFFB74D)
    )

    Column {
        Text(stringResource(R.string.current_bio_state), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(emotions) { (label, color) ->
                val isSelected = selected == label
                Surface(
                    onClick = { onSelect(label) },
                    modifier = Modifier.animateContentSize(),
                    color = if (isSelected) color else color.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = label, 
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), 
                        style = MaterialTheme.typography.labelMedium, 
                        color = if (isSelected) Color.Black else color, 
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun QuickNeuralBrakes(onSessionClick: (TherapeuticSession) -> Unit) {
    val haptic = LocalHapticFeedback.current
    val brakes = remember {
        listOf(
            TherapeuticSession(
                id = "q_hz_174",
                protocolId = "P-ACT",
                titleRes = R.string.brake_panic,
                durationRes = R.string.duration_5min,
                domain = SanctuaryDomain.BODY,
                descriptionRes = R.string.session_panic_brake_desc,
                frequencyRes = R.string.freq_174hz,
                targetRes = R.string.target_acute,
                icon = Icons.Default.FlashOn,
                intensity = 0.9f,
                audioResName = "hz_174",
                guidanceId = "g_2",
                guidanceRes = R.string.session_panic_brake_guidance
            ) to Color(0xFFFF5252),
            TherapeuticSession(
                id = "q_hz_396",
                protocolId = "P-ACT",
                titleRes = R.string.brake_anger,
                durationRes = R.string.duration_5min,
                domain = SanctuaryDomain.HEART,
                descriptionRes = R.string.session_forgiveness_desc,
                frequencyRes = R.string.freq_396hz,
                targetRes = R.string.target_acute,
                icon = Icons.Default.Whatshot,
                intensity = 0.85f,
                audioResName = "hz_396",
                guidanceId = "g_11",
                guidanceRes = R.string.session_forgiveness_guidance
            ) to Color(0xFFFF7043),
            TherapeuticSession(
                id = "q_brown_noise",
                protocolId = "P-ACT",
                titleRes = R.string.brake_rest,
                durationRes = R.string.duration_10min,
                domain = SanctuaryDomain.BODY,
                descriptionRes = R.string.session_overthinker_off_desc,
                frequencyRes = R.string.freq_delta,
                targetRes = R.string.target_acute,
                icon = Icons.Default.Bedtime,
                intensity = 0.8f,
                audioResName = "brown_noise",
                guidanceId = "g_13",
                guidanceRes = R.string.session_overthinker_off_guidance
            ) to Color(0xFF81C784)
        )
    }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.acute_neural_brakes), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f), letterSpacing = 1.sp)
            Surface(
                color = Color.Red.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(Color.Red, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.live_intervention), style = MaterialTheme.typography.labelSmall, color = Color.Red, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            brakes.forEach { (session, color) ->
                var isPressed by remember { mutableStateOf(false) }
                val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "press_scale")

                Surface(
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSessionClick(session)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .scale(scale)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    if (event.type == androidx.compose.ui.input.pointer.PointerEventType.Press) isPressed = true
                                    if (event.type == androidx.compose.ui.input.pointer.PointerEventType.Release) isPressed = false
                                }
                            }
                        },
                    color = Color.White.copy(alpha = 0.02f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, color.copy(alpha = 0.15f))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(color.copy(alpha = 0.1f), Color.Transparent))))
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), CircleShape).border(1.dp, color.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(session.icon, null, tint = color, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(session.titleRes).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = color, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DomainTabs(selected: SanctuaryDomain, onSelect: (SanctuaryDomain) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        SanctuaryDomain.entries.forEach { domain ->
            val isSelected = selected == domain
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onSelect(domain) }.padding(8.dp)) {
                Box(
                    modifier = Modifier.size(48.dp).background(if (isSelected) domain.color.copy(alpha = 0.15f) else Color.Transparent, CircleShape).border(1.dp, if (isSelected) domain.color else Color.White.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(domain.icon, null, tint = if (isSelected) domain.color else Color.White.copy(alpha = 0.4f), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(stringResource(domain.titleRes).uppercase(), style = MaterialTheme.typography.labelSmall, color = if (isSelected) domain.color else Color.White.copy(alpha = 0.4f), fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal, fontSize = 9.sp)
            }
        }
    }
}

@Composable
fun NeuroSessionCard(session: TherapeuticSession, onClick: (TherapeuticSession) -> Unit) {
    val haptic = LocalHapticFeedback.current
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "alpha"
    )

    Surface(
        onClick = { 
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick(session) 
        },
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.03f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).background(session.domain.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(session.icon, null, tint = session.domain.color, modifier = Modifier.size(28.dp))
                // Live Pulse Indicator
                if (session.intensity > 0.5f) {
                    Box(
                        modifier = Modifier.fillMaxSize().border(2.dp, session.domain.color.copy(alpha = pulseAlpha), CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text(session.protocolId, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(session.titleRes).uppercase(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = session.domain.color.copy(alpha = 0.2f), shape = CircleShape) {
                        Text(stringResource(session.frequencyRes), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = session.domain.color, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(session.targetRes), style = MaterialTheme.typography.labelSmall, color = session.domain.color, letterSpacing = 1.sp)
                    if (session.intensity > 0.7f) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.label_live_sync), style = MaterialTheme.typography.labelSmall, color = Color.Red, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    val syncingCount = remember(session.id) { (100..2500).random() }
                    Box(modifier = Modifier.size(4.dp).background(Color(0xFF81C784), CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.global_syncing, syncingCount), style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.3f), fontSize = 8.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(session.descriptionRes), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f), lineHeight = 16.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(24.dp))
        }
    }
}


