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
        val sleepScore = calculateSleepScore(healthLog)
        val cardioScore = calculateCardioScore(healthLog)
        val metabolicScore = calculateMetabolicScore(healthLog)
        val stressScore = calculateStressScore(healthLog)
        val cognitiveScore = calculateCognitiveScore(healthLog)

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
        systems["NEURAL CONNECTIVITY"] = SystemHealth(
            statusRes = interpretStatusRes(longevityScore),
            score = longevityScore,
            trendRes = if (healthLog.actionsCompleted.size > 3) R.string.status_stable else R.string.status_stable, // Logic placeholder
            confidence = 94,
            implicationRes = R.string.intel_equilibrium_desc,
            actionRes = R.string.act_sigh_title
        )
        
        systems["METABOLIC EFFICIENCY"] = SystemHealth(
            statusRes = interpretStatusRes(metabolicScore),
            score = metabolicScore,
            trendRes = R.string.vitalis_active_analysis,
            confidence = 88,
            implicationRes = R.string.act_me1_bio,
            actionRes = R.string.act_me2_title
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
        val perceived = (100 - log.stressLevel) * 0.4
        val emotional = (100 - log.emotionalTension) * 0.3
        val hrvEffect = (log.hrv / 100.0 * 30).coerceAtMost(30.0)
        return (perceived + emotional + hrvEffect).roundToInt().coerceIn(0, 100)
    }

    private fun calculateCognitiveScore(log: HealthLog): Int {
        val focus = log.perceivedFocusQuality * 0.5
        val duration = (log.focusDurationMinutes / 120.0 * 30).coerceAtMost(30.0)
        val fatigue = (100 - log.mentalFatigueLevel) * 0.2
        return (focus + duration + fatigue).roundToInt().coerceIn(0, 100)
    }

    // --- MODULE GENERATORS ---

    private fun generateSleepActivities(score: Int, log: HealthLog): SleepModuleData {
        val activities = mutableListOf<VitalisActivity>()
        if (log.sleepHours < 7) {
            activities.add(VitalisActivity("sl_ext", R.string.act_sl3_title, R.string.phase_evening, R.string.act_sl3_ipre, R.string.act_sl3_bio, R.string.act_sl3_phys, R.string.act_sl3_rsch))
        }
        activities.add(VitalisActivity("sl_1", R.string.act_sl1_title, R.string.phase_morning, R.string.act_sl1_ipre, R.string.act_sl1_bio, R.string.act_sl1_phys, R.string.act_sl1_rsch))
        
        val priority = if (score < 70) listOf(PriorityAction("sl_1", R.string.act_sl1_title, R.string.act_sl1_ipre, R.string.act_sl1_bio, R.string.act_sl1_phys, R.string.act_sl1_rsch, "High")) else emptyList()
        return SleepModuleData(score, if (score > 80) R.string.vitalis_synchronized else R.string.vitalis_drifting, activities, priority)
    }

    private fun generateCardioActivities(score: Int, log: HealthLog): CardiovascularModuleData {
        val activities = mutableListOf<VitalisActivity>()
        activities.add(VitalisActivity("ca_1", R.string.act_ca1_title, R.string.phase_morning, R.string.act_ca1_ipre, R.string.act_ca1_bio, R.string.act_ca1_phys, R.string.act_ca1_rsch))
        return CardiovascularModuleData(score, activities, emptyList())
    }

    private fun generateMetabolicActivities(score: Int, log: HealthLog): MetabolicModuleData {
        val activities = mutableListOf<VitalisActivity>()
        activities.add(VitalisActivity("me_1", R.string.act_me1_title, R.string.phase_daytime, R.string.act_me1_ipre, R.string.act_me1_bio, R.string.act_me1_phys, R.string.act_me1_rsch))
        activities.add(VitalisActivity("me_2", R.string.act_me2_title, R.string.phase_daytime, R.string.act_me2_ipre, R.string.act_me2_bio, R.string.act_me2_phys, R.string.act_me2_rsch))
        
        val priority = if (score < 75) {
            listOf(PriorityAction("me_1", R.string.act_me1_title, R.string.act_me1_ipre, R.string.act_me1_bio, R.string.act_me1_phys, R.string.act_me1_rsch, "High"))
        } else emptyList()

        return MetabolicModuleData(score, activities, priority)
    }

    private fun generateStressActivities(score: Int, log: HealthLog): StressModuleData {
        val activities = mutableListOf<VitalisActivity>()
        activities.add(VitalisActivity("st_1", R.string.act_st1_title, R.string.phase_execution, R.string.act_st1_ipre, R.string.act_st1_bio, R.string.act_st1_phys, R.string.act_st1_rsch))
        activities.add(VitalisActivity("co_1", R.string.act_co1_title, R.string.phase_daytime, R.string.act_co1_ipre, R.string.act_co1_bio, R.string.act_co1_phys, R.string.act_co1_rsch))

        return StressModuleData(score, activities, emptyList())
    }

    private fun generatePhysicalActivities(log: HealthLog): PhysicalActivityModuleData {
        val activities = listOf(
            VitalisActivity("ph_1", R.string.act_ph1_title, R.string.phase_daytime, R.string.act_ph1_ipre, R.string.act_ph1_bio, R.string.act_ph1_phys, R.string.act_ph1_rsch),
            VitalisActivity("ph_2", R.string.act_ph2_title, R.string.phase_morning, R.string.act_ph2_ipre, R.string.act_ph2_bio, R.string.act_ph2_phys, R.string.act_ph2_rsch)
        )
        return PhysicalActivityModuleData(80, activities, emptyList())
    }

    private fun generateCognitiveActivities(score: Int, log: HealthLog): CognitiveModuleData {
        val activities = listOf(
            VitalisActivity("co_1", R.string.act_co1_title, R.string.phase_daytime, R.string.act_co1_ipre, R.string.act_co1_bio, R.string.act_co1_phys, R.string.act_co1_rsch)
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
}
