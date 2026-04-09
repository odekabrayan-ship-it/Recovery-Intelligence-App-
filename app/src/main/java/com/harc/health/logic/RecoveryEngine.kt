package com.harc.health.logic

import com.harc.health.R
import com.harc.health.model.HealthLog
import com.harc.health.model.RecoveryTask

data class ResilienceSystem(
    val nameRes: Int,
    val level: Int,
    val xp: Int,
    val nextLevelXp: Int,
    val descriptionRes: Int,
    val color: String // Hex string for UI
)

object RecoveryEngine {
    fun getResilienceSystems(log: HealthLog): List<ResilienceSystem> {
        val systems = mutableListOf<ResilienceSystem>()

        // 1. Hepatic Resilience (Drinkers focus)
        val hepaticActions = listOf("h1", "h4", "h2", "lv1", "ng1", "p1", "p2")
        val hepaticXp = log.actionsCompleted.count { it in hepaticActions } * 15
        systems.add(calculateSystem(R.string.recovery_system_hepatic_name, hepaticXp, R.string.recovery_system_hepatic_desc, "#FF9800"))

        // 2. Pulmonary Power (Smokers focus)
        val pulmonaryActions = listOf("rl1", "rl4", "rl5", "rl3", "p3")
        val pulmonaryXp = log.actionsCompleted.count { it in pulmonaryActions } * 15
        systems.add(calculateSystem(R.string.recovery_system_pulmonary_name, pulmonaryXp, R.string.recovery_system_pulmonary_desc, "#2196F3"))

        // 3. Neural Stability (Both)
        val neuralActions = listOf("nc1", "nc2", "nc3", "bh1", "bh3", "bh2", "sl1", "sl4", "sl2")
        val neuralXp = log.actionsCompleted.count { it in neuralActions } * 12
        systems.add(calculateSystem(R.string.recovery_system_neural_name, neuralXp, R.string.recovery_system_neural_desc, "#9C27B0"))

        // 4. Vascular Integrity (Both)
        val vascularActions = listOf("cc1", "cc3", "cc4", "cc2", "p6")
        val vascularXp = log.actionsCompleted.count { it in vascularActions } * 15
        systems.add(calculateSystem(R.string.recovery_system_vascular_name, vascularXp, R.string.recovery_system_vascular_desc, "#F44336"))

        return systems
    }

    private fun calculateSystem(nameRes: Int, totalXp: Int, descRes: Int, color: String): ResilienceSystem {
        val level = (totalXp / 100) + 1
        val xpInLevel = totalXp % 100
        return ResilienceSystem(nameRes, level, xpInLevel, 100, descRes, color)
    }

    fun getBiologicalBriefing(log: HealthLog): Int {
        return when {
            log.alcoholUnits > 8 -> R.string.briefing_critical_toxicity
            log.alcoholUnits > 3 -> R.string.briefing_moderate_inflammation
            log.cigarettes > 15 -> R.string.briefing_vascular_constriction
            log.sleepHours < 5 -> R.string.briefing_neural_depletion
            log.actionsCompleted.size > 5 -> R.string.briefing_excellent_compliance
            else -> R.string.briefing_default
        }
    }

    fun getTimeBasedCategories(): List<Int> = listOf(
        R.string.recovery_cat_morning,
        R.string.recovery_cat_midday,
        R.string.recovery_cat_afternoon,
        R.string.recovery_cat_evening,
        R.string.recovery_cat_night
    )

    fun calculateScore(log: HealthLog): Int {
        val alcoholImpact = (log.alcoholUnits * 8.0).coerceAtMost(40.0)
        val smokingImpact = (log.cigarettes * 2.0).coerceAtMost(30.0)
        
        val hydrationScore = (log.hydrationMl / 2500.0).coerceAtMost(1.0) * 25
        val sleepScore = (log.sleepHours / 8.0).coerceAtMost(1.0) * 25
        val actionBoost = log.actionsCompleted.size * 5.0
        
        val baseResilience = 70.0
        val finalScore = baseResilience - alcoholImpact - smokingImpact + hydrationScore + sleepScore + actionBoost
        
        return finalScore.toInt().coerceIn(0, 100)
    }

    fun getRecommendedPlans(log: HealthLog): List<Int> {
        val plans = mutableListOf<Int>()
        
        // Add Time-Based Journey Plans
        plans.addAll(getTimeBasedCategories())

        if (log.alcoholUnits > 0) plans.add(R.string.recovery_cat_liver_metabolic)
        if (log.cigarettes > 0) plans.add(R.string.recovery_cat_respiratory)
        plans.add(R.string.recovery_cat_metabolic)
        plans.add(R.string.recovery_cat_sleep_arch)
        plans.add(R.string.recovery_cat_cognitive)
        plans.add(R.string.recovery_cat_behavioral)
        if (log.stressLevel > 50 || log.alcoholUnits > 5 || log.cigarettes > 10) {
            plans.add(R.string.recovery_cat_cardiovascular)
        }
        return plans.distinct()
    }

    fun getTasksForCategory(categoryRes: Int): List<RecoveryTask> {
        return when (categoryRes) {
            R.string.recovery_cat_morning -> listOf(
                RecoveryTask("h1", R.string.act_h1_title, categoryRes, R.string.act_h1_desc, 10),
                RecoveryTask("h4", R.string.act_h4_title, categoryRes, R.string.act_h4_desc, 10),
                RecoveryTask("cc2", R.string.act_cc2_title, categoryRes, R.string.act_cc2_desc, 11),
                RecoveryTask("rl1", R.string.act_rl1_title, categoryRes, R.string.act_rl1_desc, 8)
            )
            R.string.recovery_cat_midday -> listOf(
                RecoveryTask("mn1", R.string.act_mn1_title, categoryRes, R.string.act_mn1_desc, 9),
                RecoveryTask("cc3", R.string.act_cc3_title, categoryRes, R.string.act_cc3_desc, 10),
                RecoveryTask("cc1", R.string.act_cc1_title, categoryRes, R.string.act_cc1_desc, 8)
            )
            R.string.recovery_cat_afternoon -> listOf(
                RecoveryTask("mn5", R.string.act_mn5_title, categoryRes, R.string.act_mn5_desc, 7),
                RecoveryTask("cc4", R.string.act_cc4_title, categoryRes, R.string.act_cc4_desc, 8),
                RecoveryTask("nc2", R.string.act_nc2_title, categoryRes, R.string.act_nc2_desc, 9)
            )
            R.string.recovery_cat_evening -> listOf(
                RecoveryTask("ng1", R.string.act_ng1_title, categoryRes, R.string.act_ng1_desc, 7),
                RecoveryTask("rl5", R.string.act_rl5_title, categoryRes, R.string.act_rl5_desc, 8),
                RecoveryTask("nc1", R.string.act_nc1_title, categoryRes, R.string.act_nc1_desc, 10)
            )
            R.string.recovery_cat_night -> listOf(
                RecoveryTask("sl4", R.string.act_rc_sl4_title, categoryRes, R.string.act_rc_sl4_desc, 8),
                RecoveryTask("sl1", R.string.act_rc_sl1_title, categoryRes, R.string.act_rc_sl1_desc, 9),
                RecoveryTask("sl2", R.string.act_rc_sl2_title, categoryRes, R.string.act_rc_sl2_desc, 10)
            )
            R.string.recovery_cat_liver_metabolic -> listOf(
                RecoveryTask("h1", R.string.act_h1_title, categoryRes, R.string.act_h1_desc, 10),
                RecoveryTask("h4", R.string.act_h4_title, categoryRes, R.string.act_h4_desc, 10),
                RecoveryTask("h2", R.string.act_h2_title, categoryRes, R.string.act_h2_desc, 8),
                RecoveryTask("lv1", R.string.act_lv1_title, categoryRes, R.string.act_lv1_desc, 9),
                RecoveryTask("ng1", R.string.act_ng1_title, categoryRes, R.string.act_ng1_desc, 7)
            )
            R.string.recovery_cat_cardiovascular -> listOf(
                RecoveryTask("cc1", R.string.act_cc1_title, categoryRes, R.string.act_cc1_desc, 8),
                RecoveryTask("cc3", R.string.act_cc3_title, categoryRes, R.string.act_cc3_desc, 10),
                RecoveryTask("cc4", R.string.act_cc4_title, categoryRes, R.string.act_cc4_desc, 8),
                RecoveryTask("cc2", R.string.act_cc2_title, categoryRes, R.string.act_cc2_desc, 11)
            )
            R.string.recovery_cat_respiratory -> listOf(
                RecoveryTask("rl1", R.string.act_rl1_title, categoryRes, R.string.act_rl1_desc, 8),
                RecoveryTask("rl4", R.string.act_rl4_title, categoryRes, R.string.act_rl4_desc, 9),
                RecoveryTask("rl5", R.string.act_rl5_title, categoryRes, R.string.act_rl5_desc, 8),
                RecoveryTask("rl3", R.string.act_rl3_title, categoryRes, R.string.act_rl3_desc, 9)
            )
            R.string.recovery_cat_metabolic -> listOf(
                RecoveryTask("mn1", R.string.act_mn1_title, categoryRes, R.string.act_mn1_desc, 9),
                RecoveryTask("mn5", R.string.act_mn5_title, categoryRes, R.string.act_mn5_desc, 7),
                RecoveryTask("mn4", R.string.act_mn4_title, categoryRes, R.string.act_mn4_desc, 8)
            )
            R.string.recovery_cat_sleep_arch -> listOf(
                RecoveryTask("sl1", R.string.act_rc_sl1_title, categoryRes, R.string.act_rc_sl1_desc, 9),
                RecoveryTask("sl4", R.string.act_rc_sl4_title, categoryRes, R.string.act_rc_sl4_desc, 8),
                RecoveryTask("sl2", R.string.act_rc_sl2_title, categoryRes, R.string.act_rc_sl2_desc, 10)
            )
            R.string.recovery_cat_cognitive -> listOf(
                RecoveryTask("nc1", R.string.act_nc1_title, categoryRes, R.string.act_nc1_desc, 10),
                RecoveryTask("nc2", R.string.act_nc2_title, categoryRes, R.string.act_nc2_desc, 9),
                RecoveryTask("nc3", R.string.act_nc3_title, categoryRes, R.string.act_nc3_desc, 8)
            )
            R.string.recovery_cat_behavioral -> listOf(
                RecoveryTask("bh1", R.string.act_bh1_title, categoryRes, R.string.act_bh1_desc, 9),
                RecoveryTask("bh3", R.string.act_bh3_title, categoryRes, R.string.act_bh3_desc, 10),
                RecoveryTask("bh2", R.string.act_bh2_title, categoryRes, R.string.act_bh2_desc, 10)
            )
            else -> emptyList()
        }
    }

    fun getPriorityTasks(log: HealthLog): List<RecoveryTask> {
        val priorities = mutableListOf<RecoveryTask>()
        if (log.hydrationMl < 1000) priorities.add(RecoveryTask("p1", R.string.act_p1_title, R.string.recovery_cat_priority, R.string.act_p1_desc, 15))
        if (log.alcoholUnits > 4) priorities.add(RecoveryTask("p2", R.string.act_p2_title, R.string.recovery_cat_priority, R.string.act_p2_desc, 12))
        if (log.cigarettes > 10) priorities.add(RecoveryTask("p3", R.string.act_p3_title, R.string.recovery_cat_priority, R.string.act_p3_desc, 10))
        if (log.alcoholUnits > 0 || log.cigarettes > 0) priorities.add(RecoveryTask("p6", R.string.act_p6_title, R.string.recovery_cat_priority, R.string.act_p6_desc, 12))
        return priorities.take(3)
    }
}
