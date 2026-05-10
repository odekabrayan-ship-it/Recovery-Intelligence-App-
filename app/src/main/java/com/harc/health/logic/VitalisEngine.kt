package com.harc.health.logic

import androidx.annotation.StringRes
import com.harc.health.R
import com.harc.health.model.*
import kotlin.math.*

/**
 * Vitalis™ Professional-Grade Bioregulatory Engine
 */
object VitalisEngine {

    fun calculateVitalisData(healthLog: HealthLog): VitalisData {
        // 1. RAW BIO-METRIC PROCESSING
        val sensorAdjustment = calculateSensorPrecisionAdjustment(healthLog)
        
        val sleepScore = (calculateSleepScore(healthLog) * sensorAdjustment).roundToInt().coerceIn(0, 100)
        val cardioScore = (calculateCardioScore(healthLog) * sensorAdjustment).roundToInt().coerceIn(0, 100)
        val metabolicScore = (calculateMetabolicScore(healthLog) * sensorAdjustment).roundToInt().coerceIn(0, 100)
        val stressScore = (calculateStressScore(healthLog) * sensorAdjustment).roundToInt().coerceIn(0, 100)
        val cognitiveScore = (calculateCognitiveScore(healthLog) * sensorAdjustment).roundToInt().coerceIn(0, 100)

        // 2. MODULE GENERATION (Context-Aware)
        val sleepModule = generateSleepActivities(sleepScore, healthLog)
        val cardioModule = generateCardioActivities(cardioScore, healthLog)
        val metabolicModule = generateMetabolicActivities(metabolicScore, healthLog)
        val stressModule = generateStressActivities(stressScore, healthLog)
        val physicalModule = generatePhysicalActivities(healthLog)
        val cognitiveModule = generateCognitiveActivities(cognitiveScore, healthLog)
        
        // 3. LONGEVITY MODELING
        val longevityScore = listOf(
            sleepScore, cardioScore, metabolicScore, 
            stressScore, cognitiveScore
        ).average().roundToInt()

        val systems = mutableMapOf<String, SystemHealth>()

        systems["VASCULAR HEMODYNAMICS"] = SystemHealth(
            statusRes = interpretStatusRes(cardioScore),
            score = cardioScore,
            trendRes = R.string.status_stable,
            trendDescriptionRes = R.string.dividend_vascular_elasticity,
            confidence = 91,
            implicationRes = R.string.reward_ts_vascular_desc,
            actionRes = R.string.act_st_vagal_ppg_title
        )

        systems["NEURAL CONNECTIVITY"] = SystemHealth(
            statusRes = interpretStatusRes(longevityScore),
            score = longevityScore,
            trendRes = if (healthLog.actionsCompleted.size > 3) R.string.status_stable else R.string.status_stable, // Logic placeholder
            trendDescriptionRes = R.string.dividend_neural_coherence,
            confidence = 94,
            implicationRes = R.string.intel_equilibrium_desc,
            actionRes = R.string.act_co_dual_task_title
        )
        
        systems["METABOLIC EFFICIENCY"] = SystemHealth(
            statusRes = interpretStatusRes(metabolicScore),
            score = metabolicScore,
            trendRes = R.string.vitalis_active_analysis,
            trendDescriptionRes = R.string.dividend_metabolic_throughput,
            confidence = 88,
            implicationRes = R.string.act_me1_bio,
            actionRes = R.string.act_me_soleus_title
        )

        systems["SLEEP ARCHITECTURE"] = SystemHealth(
            statusRes = interpretStatusRes(sleepScore),
            score = sleepScore,
            trendRes = R.string.status_stable,
            trendDescriptionRes = R.string.dividend_glymphatic_flow,
            confidence = 92,
            implicationRes = R.string.act_sl4_bio,
            actionRes = R.string.act_sl_glymphatic_title
        )

        val priorityActions = gatherPriorityActions(sleepModule, cardioModule, metabolicModule, stressModule)

        return VitalisData(
            longevityScore = longevityScore,
            trajectoryRes = determineTrajectoryRes(longevityScore),
            systems = systems,
            sleepModule = sleepModule,
            cardioModule = cardioModule,
            metabolicModule = metabolicModule,
            stressModule = stressModule,
            physicalModule = physicalModule,
            cognitiveModule = cognitiveModule,
            recoveryModule = generateRecoveryActivities(),
            protocolModule = generateStructuredProtocols(),
            patternModule = generatePatternAnalysis(healthLog),
            trajectoryModule = generateTrajectoryModeling(longevityScore),
            researchModule = generateResearchHub(),
            priorityActions = priorityActions
        )
    }

    // --- BIO-METRIC CALCULATORS ---

    private fun calculateSleepScore(log: HealthLog): Int {
        val duration = (log.sleepHours / 8.0 * 40).coerceAtMost(40.0)
        val consistency = (log.bedtimeConsistency.toDouble() / 100.0 * 30)
        val quality = (log.sleepQuality.toDouble() / 100.0 * 30)
        return (duration + consistency + quality).roundToInt().coerceIn(0, 100)
    }

    private fun calculateCardioScore(log: HealthLog): Int {
        val rhrEffect = (100 - log.restingHeartRate).coerceIn(0, 100) * 0.4
        val activityEffect = (log.activityMinutes / 60.0 * 40).coerceAtMost(40.0)
        val recoveryEffect = (log.heartRateRecovery / 50.0 * 20).coerceAtMost(20.0)
        return (rhrEffect + activityEffect + recoveryEffect).roundToInt().coerceIn(0, 100)
    }

    private fun calculateMetabolicScore(log: HealthLog): Int {
        val regularity = log.mealRegularityScore * 0.4
        val hunger = log.hungerStability * 0.3
        val energy = ((log.energyLevelMorning + log.energyLevelAfternoon + log.energyLevelEvening) / 30.0 * 30)
        return (regularity + hunger + energy).roundToInt().coerceIn(0, 100)
    }

    private fun calculateStressScore(log: HealthLog): Int {
        val perceived = (100 - log.stressLevel) * 0.3
        val emotional = (100 - log.emotionalTension) * 0.2
        val hrvEffect = (log.hrv / 100.0 * 25).coerceAtMost(25.0)
        
        // PPG Bio-feedback Integration
        val ppgPrecision = log.sensorPrecisionMap["action_vagal_ppg"] ?: 0.5
        val ppgBonus = (ppgPrecision * 25) 
        
        return (perceived + emotional + hrvEffect + ppgBonus).roundToInt().coerceIn(0, 100)
    }

    private fun calculateCognitiveScore(log: HealthLog): Int {
        val focus = log.perceivedFocusQuality * 0.4
        val duration = (log.focusDurationMinutes / 120.0 * 20).coerceAtMost(20.0)
        val fatigue = (100 - log.mentalFatigueLevel) * 0.15
        
        // Optical Flow / Neural Divergence Integration
        val opticalPrecision = log.sensorPrecisionMap["action_neural_divergence"] ?: 0.5
        val opticalBonus = (opticalPrecision * 25)
        
        return (focus + duration + fatigue + opticalBonus).roundToInt().coerceIn(0, 100)
    }

    // --- MODULE GENERATORS ---

    private fun generateSleepActivities(score: Int, log: HealthLog): SleepModuleData {
        val activities = mutableListOf<VitalisActivity>()
        
        // Glymphatic Waste Clearance - Expert Intervention
        activities.add(
            VitalisActivity(
                id = "sl_glymphatic_clearance",
                titleRes = R.string.act_sl_glymphatic_title,
                phaseRes = R.string.phase_evening,
                instructionsRes = R.string.act_sl_glymphatic_ipre,
                biologicalRationaleRes = R.string.act_sl_glymphatic_bio,
                physiologicalOutcomeRes = R.string.act_sl_glymphatic_phys,
                researchInsightRes = R.string.act_sl_glymphatic_rsch,
                interactionType = InteractionType.TACTILE_MASSAGE,
                actionId = "action_glymphatic_drainage"
            )
        )

        // Morning Light Anchor - Optical Interaction
        activities.add(
            VitalisActivity(
                id = "sl_light_anchor",
                titleRes = R.string.notif_morning_title,
                phaseRes = R.string.phase_morning,
                instructionsRes = R.string.notif_morning_msg_2,
                biologicalRationaleRes = R.string.reward_sl1_msg,
                physiologicalOutcomeRes = R.string.dividend_neural_coherence,
                interactionType = InteractionType.LIGHT_WASH,
                actionId = "action_circadian_anchor"
            )
        )
        
        val priority = if (score < 70) listOf(PriorityAction("sl_glymphatic_clearance", R.string.act_sl_glymphatic_title, R.string.act_sl_glymphatic_ipre, R.string.act_sl_glymphatic_bio, R.string.act_sl_glymphatic_phys, R.string.act_sl_glymphatic_rsch, "High")) else emptyList()
        return SleepModuleData(score, if (score > 80) R.string.vitalis_synchronized else R.string.vitalis_drifting, activities, priority)
    }

    private fun generateCardioActivities(score: Int, log: HealthLog): CardiovascularModuleData {
        val activities = mutableListOf<VitalisActivity>()
        
        // Vascular Flush - Dive Reflex Interactive
        activities.add(
            VitalisActivity(
                id = "ca_vascular_flush",
                titleRes = R.string.reward_cc2_title,
                phaseRes = R.string.phase_morning,
                instructionsRes = R.string.act_st4_ipre,
                biologicalRationaleRes = R.string.act_st4_bio,
                physiologicalOutcomeRes = R.string.dividend_vascular_elasticity,
                interactionType = InteractionType.ISOMETRIC_PULSE, // Using isometric pulse as proxy for dive reflex feedback
                actionId = "action_vascular_flush"
            )
        )
        return CardiovascularModuleData(score, activities, emptyList())
    }

    private fun generateMetabolicActivities(score: Int, log: HealthLog): MetabolicModuleData {
        val activities = mutableListOf<VitalisActivity>()
        
        // Soleus Metabolism Ignition - Expert Intervention
        activities.add(
            VitalisActivity(
                id = "me_soleus_ignition",
                titleRes = R.string.act_me_soleus_title,
                phaseRes = R.string.phase_daytime,
                instructionsRes = R.string.act_me_soleus_ipre,
                biologicalRationaleRes = R.string.act_me_soleus_bio,
                physiologicalOutcomeRes = R.string.act_me_soleus_phys,
                researchInsightRes = R.string.act_me_soleus_rsch,
                interactionType = InteractionType.ISOMETRIC_PULSE,
                actionId = "action_soleus_pushup"
            )
        )

        // Myokine Surge Ignition - High-Velocity Accelerometer Interaction
        activities.add(
            VitalisActivity(
                id = "me_myokine_pulse",
                titleRes = R.string.act_me_myokine_pulse_title,
                phaseRes = R.string.phase_daytime,
                instructionsRes = R.string.act_me_myokine_pulse_ipre,
                biologicalRationaleRes = R.string.act_me_myokine_pulse_bio,
                physiologicalOutcomeRes = R.string.act_me_myokine_pulse_phys,
                researchInsightRes = R.string.act_me_myokine_pulse_rsch,
                interactionType = InteractionType.GAIT_SYNCHRONIZATION,
                actionId = "action_myokine_pulse"
            )
        )
        
        val priority = mutableListOf<PriorityAction>()
        if (score < 75) {
            priority.add(PriorityAction("me_soleus_ignition", R.string.act_me_soleus_title, R.string.act_me_soleus_ipre, R.string.act_me_soleus_bio, R.string.act_me_soleus_phys, R.string.act_me_soleus_rsch, "High"))
        }
        if (score < 80) {
            priority.add(PriorityAction("me_myokine_pulse", R.string.act_me_myokine_pulse_title, R.string.act_me_myokine_pulse_ipre, R.string.act_me_myokine_pulse_bio, R.string.act_me_myokine_pulse_phys, R.string.act_me_myokine_pulse_rsch, "Medium"))
        }

        return MetabolicModuleData(score, activities, priority)
    }

    private fun generateStressActivities(score: Int, log: HealthLog): StressModuleData {
        val activities = mutableListOf<VitalisActivity>()
        
        // Neural Divergence Sync - Expert Intervention
        activities.add(
            VitalisActivity(
                id = "st_neural_divergence",
                titleRes = R.string.act_st_divergence_title,
                phaseRes = R.string.phase_execution,
                instructionsRes = R.string.act_st_divergence_ipre,
                biologicalRationaleRes = R.string.act_st_divergence_bio,
                physiologicalOutcomeRes = R.string.act_st_divergence_phys,
                researchInsightRes = R.string.act_st_divergence_rsch,
                interactionType = InteractionType.OPTICAL_FLOW,
                actionId = "action_neural_divergence"
            )
        )

        // Vagal Brake Calibration - PPG Feedback
        activities.add(
            VitalisActivity(
                id = "st_vagal_ppg",
                titleRes = R.string.act_st_vagal_ppg_title,
                phaseRes = R.string.phase_execution,
                instructionsRes = R.string.act_st_vagal_ppg_ipre,
                biologicalRationaleRes = R.string.act_st_vagal_ppg_bio,
                physiologicalOutcomeRes = R.string.act_st_vagal_ppg_phys,
                researchInsightRes = R.string.act_st_vagal_ppg_rsch,
                interactionType = InteractionType.BIO_FEEDBACK_PPG,
                actionId = "action_vagal_ppg"
            )
        )

        // Physiological Sigh - Breath Sync
        activities.add(
            VitalisActivity(
                id = "st_vagal_sync",
                titleRes = R.string.act_st1_title,
                phaseRes = R.string.phase_execution,
                instructionsRes = R.string.act_st1_ipre,
                biologicalRationaleRes = R.string.act_st1_bio,
                physiologicalOutcomeRes = R.string.act_st1_phys,
                researchInsightRes = R.string.act_st1_rsch,
                interactionType = InteractionType.BREATH_SYNC,
                actionId = "action_vagal_sigh"
            )
        )

        val priority = if (score < 70) {
            listOf(PriorityAction("st_vagal_ppg", R.string.act_st_vagal_ppg_title, R.string.act_st_vagal_ppg_ipre, R.string.act_st_vagal_ppg_bio, R.string.act_st_vagal_ppg_phys, R.string.act_st_vagal_ppg_rsch, "High"))
        } else emptyList()

        return StressModuleData(score, activities, priority)
    }

    private fun generatePhysicalActivities(log: HealthLog): PhysicalActivityModuleData {
        val activities = mutableListOf<VitalisActivity>()
        
        activities.add(
            VitalisActivity(
                id = "ph_joint_priming",
                titleRes = R.string.act_ph1_title,
                phaseRes = R.string.phase_daytime,
                instructionsRes = R.string.act_ph1_ipre,
                biologicalRationaleRes = R.string.act_ph1_bio,
                physiologicalOutcomeRes = R.string.dividend_systemic_resilience,
                interactionType = InteractionType.TACTILE_MASSAGE, // Guided joint flossing
                actionId = "action_joint_priming"
            )
        )
        
        activities.add(
            VitalisActivity(
                id = "ph_iso_loading",
                titleRes = R.string.act_ph3_title,
                phaseRes = R.string.phase_morning,
                instructionsRes = R.string.act_ph3_ipre,
                biologicalRationaleRes = R.string.act_ph3_bio,
                physiologicalOutcomeRes = R.string.dividend_mitochondrial_biogenesis,
                interactionType = InteractionType.ISOMETRIC_PULSE,
                actionId = "action_iso_loading"
            )
        )

        // Vestibular Equilibrium Reset - Gyroscope Stabilization
        activities.add(
            VitalisActivity(
                id = "ph_gyro_balance",
                titleRes = R.string.act_ph_gyro_balance_title,
                phaseRes = R.string.phase_daytime,
                instructionsRes = R.string.act_ph_gyro_balance_ipre,
                biologicalRationaleRes = R.string.act_ph_gyro_balance_bio,
                physiologicalOutcomeRes = R.string.act_ph_gyro_balance_phys,
                researchInsightRes = R.string.act_ph_gyro_balance_rsch,
                interactionType = InteractionType.GYRO_STABILIZATION,
                actionId = "action_gyro_balance"
            )
        )
        
        return PhysicalActivityModuleData(80, activities, emptyList())
    }

    private fun generateCognitiveActivities(score: Int, log: HealthLog): CognitiveModuleData {
        val activities = mutableListOf<VitalisActivity>()
        
        activities.add(
            VitalisActivity(
                id = "co_neural_sync",
                titleRes = R.string.vitalis_neural_coherence,
                phaseRes = R.string.phase_daytime,
                instructionsRes = R.string.act_co1_ipre,
                biologicalRationaleRes = R.string.act_co1_bio,
                physiologicalOutcomeRes = R.string.dividend_neural_coherence,
                interactionType = InteractionType.OPTICAL_FLOW,
                actionId = "action_cognitive_sync"
            )
        )

        // Neural Cross-Talk Protocol - Dual-Task Interaction
        activities.add(
            VitalisActivity(
                id = "co_dual_task",
                titleRes = R.string.act_co_dual_task_title,
                phaseRes = R.string.phase_daytime,
                instructionsRes = R.string.act_co_dual_task_ipre,
                biologicalRationaleRes = R.string.act_co_dual_task_bio,
                physiologicalOutcomeRes = R.string.act_co_dual_task_phys,
                researchInsightRes = R.string.act_co_dual_task_rsch,
                interactionType = InteractionType.COGNITIVE_DUAL_TASK,
                actionId = "action_dual_task"
            )
        )

        // Optic Nerve Safety Reset - Depth Focus Interaction
        activities.add(
            VitalisActivity(
                id = "co_depth_shift",
                titleRes = R.string.act_co_depth_shift_title,
                phaseRes = R.string.phase_evening,
                instructionsRes = R.string.act_co_depth_shift_ipre,
                biologicalRationaleRes = R.string.act_co_depth_shift_bio,
                physiologicalOutcomeRes = R.string.act_co_depth_shift_phys,
                researchInsightRes = R.string.act_co_depth_shift_rsch,
                interactionType = InteractionType.DEPTH_FOCUS_SHIFT,
                actionId = "action_depth_shift"
            )
        )
        
        return CognitiveModuleData(score, activities, emptyList())
    }

    private fun generateRecoveryActivities(): RecoveryModuleData {
        return RecoveryModuleData(85, emptyList(), emptyList())
    }

    private fun generateStructuredProtocols(): LongevityProtocolsModuleData {
        return LongevityProtocolsModuleData(
            activeProtocols = listOf(
                LongevityProtocol(
                    id = "lp_autophagy",
                    titleRes = R.string.lp_autophagy_title,
                    purposeRes = R.string.lp_autophagy_purpose,
                    instructionsRes = listOf(R.string.lp_autophagy_step1, R.string.lp_autophagy_step2, R.string.lp_autophagy_step3),
                    shortTermBenefitRes = R.string.lp_autophagy_benefit_short,
                    longTermBenefitRes = R.string.lp_autophagy_benefit_long,
                    researchInsightRes = R.string.lp_autophagy_insight
                ),
                LongevityProtocol(
                    id = "lp_thermal",
                    titleRes = R.string.lp_thermal_title,
                    purposeRes = R.string.lp_thermal_purpose,
                    instructionsRes = listOf(R.string.lp_thermal_step1, R.string.lp_thermal_step2, R.string.lp_thermal_step3),
                    shortTermBenefitRes = R.string.lp_thermal_benefit_short,
                    longTermBenefitRes = R.string.lp_thermal_benefit_long,
                    researchInsightRes = R.string.lp_thermal_insight
                ),
                LongevityProtocol(
                    id = "lp_vo2",
                    titleRes = R.string.lp_vo2_title,
                    purposeRes = R.string.lp_vo2_purpose,
                    instructionsRes = listOf(R.string.lp_vo2_step1, R.string.lp_vo2_step2, R.string.lp_vo2_step3),
                    shortTermBenefitRes = R.string.lp_vo2_benefit_short,
                    longTermBenefitRes = R.string.lp_vo2_benefit_long,
                    researchInsightRes = R.string.lp_vo2_insight
                ),
                LongevityProtocol(
                    id = "lp_glycemic",
                    titleRes = R.string.lp_glycemic_title,
                    purposeRes = R.string.lp_glycemic_purpose,
                    instructionsRes = listOf(R.string.lp_glycemic_step1, R.string.lp_glycemic_step2, R.string.lp_glycemic_step3),
                    shortTermBenefitRes = R.string.lp_glycemic_benefit_short,
                    longTermBenefitRes = R.string.lp_glycemic_benefit_long,
                    researchInsightRes = R.string.lp_glycemic_insight
                ),
                LongevityProtocol(
                    id = "lp_glymphatic",
                    titleRes = R.string.lp_glymphatic_title,
                    purposeRes = R.string.lp_glymphatic_purpose,
                    instructionsRes = listOf(R.string.lp_glymphatic_step1, R.string.lp_glymphatic_step2, R.string.lp_glymphatic_step3),
                    shortTermBenefitRes = R.string.lp_glymphatic_benefit_short,
                    longTermBenefitRes = R.string.lp_glymphatic_benefit_long,
                    researchInsightRes = R.string.lp_glymphatic_insight
                ),
                LongevityProtocol(
                    id = "lp_senolytic",
                    titleRes = R.string.lp_senolytic_title,
                    purposeRes = R.string.lp_senolytic_purpose,
                    instructionsRes = listOf(R.string.lp_senolytic_step1, R.string.lp_senolytic_step2, R.string.lp_senolytic_step3),
                    shortTermBenefitRes = R.string.lp_senolytic_benefit_short,
                    longTermBenefitRes = R.string.lp_senolytic_benefit_long,
                    researchInsightRes = R.string.lp_senolytic_insight
                ),
                LongevityProtocol(
                    id = "lp_apob",
                    titleRes = R.string.lp_apob_title,
                    purposeRes = R.string.lp_apob_purpose,
                    instructionsRes = listOf(R.string.lp_apob_step1, R.string.lp_apob_step2, R.string.lp_apob_step3),
                    shortTermBenefitRes = R.string.lp_apob_benefit_short,
                    longTermBenefitRes = R.string.lp_apob_benefit_long,
                    researchInsightRes = R.string.lp_apob_insight
                ),
                LongevityProtocol(
                    id = "lp_skeletal",
                    titleRes = R.string.lp_skeletal_title,
                    purposeRes = R.string.lp_skeletal_purpose,
                    instructionsRes = listOf(R.string.lp_skeletal_step1, R.string.lp_skeletal_step2, R.string.lp_skeletal_step3),
                    shortTermBenefitRes = R.string.lp_skeletal_benefit_short,
                    longTermBenefitRes = R.string.lp_skeletal_benefit_long,
                    researchInsightRes = R.string.lp_skeletal_insight
                ),
                LongevityProtocol(
                    id = "lp_dna",
                    titleRes = R.string.lp_dna_title,
                    purposeRes = R.string.lp_dna_purpose,
                    instructionsRes = listOf(R.string.lp_dna_step1, R.string.lp_dna_step2, R.string.lp_dna_step3),
                    shortTermBenefitRes = R.string.lp_dna_benefit_short,
                    longTermBenefitRes = R.string.lp_dna_benefit_long,
                    researchInsightRes = R.string.lp_dna_insight
                )
            ),
            adherenceScore = 88,
            behavioralStabilityRes = R.string.vitalis_stable
        )
    }

    private fun generatePatternAnalysis(log: HealthLog): PatternAnalysisModuleData {
        return PatternAnalysisModuleData(
            identifiedPatterns = listOf(
                VitalisPattern(R.string.act_sl3_title, R.string.act_sl3_ipre, R.string.act_sl3_bio) // Logic placeholder
            ),
            insightLevelRes = R.string.vitalis_gathering
        )
    }

    private fun generateTrajectoryModeling(score: Int): TrajectoryModuleData {
        val directives = mutableListOf<TrajectoryDirective>()
        if (score > 80) {
            directives.add(TrajectoryDirective(R.string.intel_equilibrium_title_optimized))
        } else {
            directives.add(TrajectoryDirective(R.string.verdict_caution_signal))
        }

        return TrajectoryModuleData(
            projectedHealthAge = 0,
            riskTrendRes = if (score > 75) R.string.vitalis_optimal else R.string.status_stable,
            futureDirectives = directives
        )
    }

    private fun generateResearchHub(): ResearchModuleData {
        return ResearchModuleData(
            currentInsights = listOf(
                ResearchInsight(R.string.lp_vo2_title, R.string.lp_vo2_insight, R.string.lp_vo2_benefit_long), // Logic placeholder
                ResearchInsight(R.string.lp_apob_title, R.string.lp_apob_insight, R.string.lp_apob_benefit_long)
            )
        )
    }

    private fun gatherPriorityActions(vararg modules: Any): List<PriorityAction> {
        val list = mutableListOf<PriorityAction>()
        modules.forEach { module ->
            when (module) {
                is SleepModuleData -> list.addAll(module.priorityActions)
                is MetabolicModuleData -> list.addAll(module.priorityActions)
                is CardiovascularModuleData -> list.addAll(module.priorityActions)
                is StressModuleData -> list.addAll(module.priorityActions)
            }
        }
        return list.distinctBy { it.id }.take(3)
    }

    // --- BIOLOGICAL GAIN ENGINE (Unified Rewards) ---

    data class LongevityDividend(@StringRes val titleRes: Int, @StringRes val gainRes: Int, @StringRes val metricRes: Int, @StringRes val descriptionRes: Int)

    fun getBiologicalGain(id: String): LongevityDividend {
        return when (id) {
            "lp_autophagy" -> LongevityDividend(R.string.reward_lp_autophagy_title, R.string.reward_lp_autophagy_gain, R.string.reward_lp_autophagy_metric, R.string.reward_lp_autophagy_desc)
            "lp_thermal" -> LongevityDividend(R.string.reward_lp_thermal_title, R.string.reward_lp_thermal_gain, R.string.reward_lp_thermal_metric, R.string.reward_lp_thermal_desc)
            "lp_vo2" -> LongevityDividend(R.string.reward_lp_vo2_title, R.string.reward_lp_vo2_gain, R.string.reward_lp_vo2_metric, R.string.reward_lp_vo2_desc)
            "lp_glycemic" -> LongevityDividend(R.string.reward_lp_glycemic_title, R.string.reward_lp_glycemic_gain, R.string.reward_lp_glycemic_metric, R.string.reward_lp_glycemic_desc)
            "lp_glymphatic" -> LongevityDividend(R.string.reward_lp_glymphatic_title, R.string.reward_lp_glymphatic_gain, R.string.reward_lp_glymphatic_metric, R.string.reward_lp_glymphatic_desc)
            "ts_dopamine" -> LongevityDividend(R.string.reward_ts_dopamine_title, R.string.reward_ts_dopamine_gain, R.string.reward_ts_dopamine_metric, R.string.reward_ts_dopamine_desc)
            "ts_vascular" -> LongevityDividend(R.string.reward_ts_vascular_title, R.string.reward_ts_vascular_gain, R.string.reward_ts_vascular_metric, R.string.reward_ts_vascular_desc)
            "ts_cortisol" -> LongevityDividend(R.string.reward_ts_cortisol_title, R.string.reward_ts_cortisol_gain, R.string.reward_ts_cortisol_metric, R.string.reward_ts_cortisol_desc)
            "ts_liver" -> LongevityDividend(R.string.reward_ts_liver_title, R.string.reward_ts_liver_gain, R.string.reward_ts_liver_metric, R.string.reward_ts_liver_desc)
            else -> LongevityDividend(R.string.reward_generic_title, R.string.reward_generic_gain, R.string.reward_generic_metric, R.string.reward_generic_desc)
        }
    }

    private fun interpretStatusRes(score: Int): Int = when {
        score >= 85 -> R.string.status_optimized
        score >= 70 -> R.string.status_stable
        else -> R.string.status_critical
    }

    private fun determineTrajectoryRes(score: Int): Int = when {
        score >= 85 -> R.string.vitalis_trajectory_optimal
        score >= 70 -> R.string.vitalis_trajectory_stable
        else -> R.string.vitalis_trajectory_drift
    }

    // --- BIO-STATE VERIFICATION & ADAPTATION ---

    /**
     * Calculates a multiplier based on the precision of sensor-driven tasks.
     * Higher precision in interactive tasks "validates" the biological scores.
     */
    private fun calculateSensorPrecisionAdjustment(log: HealthLog): Double {
        if (log.sensorPrecisionMap.isEmpty()) return 1.0
        val avgPrecision = log.sensorPrecisionMap.values.average()
        // Precision above 0.8 provides a 5% bonus to scores; below 0.5 penalizes by 10%
        return when {
            avgPrecision >= 0.85 -> 1.05
            avgPrecision < 0.5 -> 0.90
            else -> 1.0
        }
    }

    /**
     * Translates sensor precision data into Bio-State Verification metrics.
     * High accuracy across sensor-driven tasks triggers "Bio-State Verified" status.
     */
    fun verifyBioState(accuracy: Double): Boolean {
        return accuracy >= 0.85 // Clinical precision threshold for biological breakthroughs
    }
}
