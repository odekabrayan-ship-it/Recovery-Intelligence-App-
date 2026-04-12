package com.harc.health.logic

import androidx.annotation.StringRes
import com.harc.health.R
import com.harc.health.model.*
import kotlin.math.roundToInt

/**
 * Action Decision Engine (A.D.E.)
 * The central control algorithm for HARC Health.
 * 
 * REBUILT: Now functions as a Directives Dispatcher for the Vitalis expert system.
 * It gathers research-backed instructions and presents them as primary engagements.
 */
object ActionDecisionEngine {

    data class AdeOutput(
        @StringRes val globalStateRes: Int,
        val systemBalanceIndex: Int,
        val primaryActions: List<AdeAction>,
        val confidence: Double,
        val intelligenceSignalsRes: List<Int>
    )

    data class AdeAction(
        val id: String,
        val type: ActionType,
        @StringRes val instructionRes: Int,
        val urgency: Urgency,
        val priorityScore: Double,
        @StringRes val contextRes: Int = 0,
        @StringRes val researchInsightRes: Int = 0,
        @StringRes val biologicalRationaleRes: Int = 0,
        @StringRes val physiologicalOutcomeRes: Int = 0
    )

    enum class ActionType {
        Regulation, Interruption, Execution, Restriction, Protocol
    }

    enum class Urgency {
        Immediate, Within30m, Scheduled
    }

    fun execute(healthLog: HealthLog, vitalisData: VitalisData): AdeOutput {
        // 1. STATE FUSION
        val scores = listOf(
            vitalisData.sleepModule.sleepScore,
            vitalisData.cardioModule.healthScore,
            vitalisData.cognitiveModule.performanceScore,
            vitalisData.metabolicModule.efficiencyScore,
            vitalisData.stressModule.stressLoadScore,
            vitalisData.physicalModule.consistencyScore
        ).map { it.toDouble() }
        
        val globalAvg = if (scores.isNotEmpty()) scores.average() else 0.0
        val balanceIndex = (100 - (scores.maxOrNull()?.minus(scores.minOrNull() ?: 0.0) ?: 0.0)).roundToInt().coerceIn(0, 100)
        
        val globalStateRes = when {
            globalAvg < 40 -> R.string.status_critical
            globalAvg < 65 -> R.string.status_strained
            globalAvg < 85 -> R.string.status_stable
            else -> R.string.status_optimized
        }

        // 2. SIGNAL PROCESSING
        val signals = mutableListOf<Int>()
        if (healthLog.stressLevel > 70) signals.add(R.string.vitalis_acute_neural_load)
        if (healthLog.bedtimeConsistency < 80) signals.add(R.string.vitalis_drifting)

        // 3. DIRECTIVE GATHERING (Consolidated from all Vitalis Modules)
        val candidates = gatherAllDirectives(vitalisData)

        // 4. PRIORITY DISPATCHING
        val finalSelection = candidates
            .sortedByDescending { it.priorityScore }
            .distinctBy { it.instructionRes }
            .take(5)

        return AdeOutput(
            globalStateRes = globalStateRes,
            systemBalanceIndex = balanceIndex,
            primaryActions = finalSelection,
            confidence = calculateConfidence(vitalisData),
            intelligenceSignalsRes = signals
        )
    }

    private fun gatherAllDirectives(vitalis: VitalisData): List<AdeAction> {
        val list = mutableListOf<AdeAction>()
        
        // 1. SLEEP & CIRCADIAN MODULE
        vitalis.sleepModule.priorityActions.forEach {
            list.add(AdeAction(
                id = it.id, 
                type = ActionType.Execution, 
                instructionRes = it.titleRes, 
                urgency = Urgency.Immediate, 
                priorityScore = 95.0, 
                contextRes = it.reasoningRes, 
                researchInsightRes = it.researchInsightRes,
                biologicalRationaleRes = it.biologicalRationaleRes,
                physiologicalOutcomeRes = it.physiologicalOutcomeRes
            ))
        }
        
        // 2. CARDIOVASCULAR & CIRCULATORY MODULE
        vitalis.cardioModule.priorityActions.forEach {
            list.add(AdeAction(
                id = it.id, 
                type = ActionType.Regulation, 
                instructionRes = it.titleRes, 
                urgency = Urgency.Within30m, 
                priorityScore = 92.0, 
                contextRes = it.reasoningRes, 
                researchInsightRes = it.researchInsightRes,
                biologicalRationaleRes = it.biologicalRationaleRes,
                physiologicalOutcomeRes = it.physiologicalOutcomeRes
            ))
        }

        // 3. METABOLIC & GLYCEMIC MODULE
        vitalis.metabolicModule.priorityActions.forEach {
            list.add(AdeAction(
                id = it.id, 
                type = ActionType.Protocol, 
                instructionRes = it.titleRes, 
                urgency = Urgency.Within30m, 
                priorityScore = 90.0, 
                contextRes = it.reasoningRes, 
                researchInsightRes = it.researchInsightRes,
                biologicalRationaleRes = it.biologicalRationaleRes,
                physiologicalOutcomeRes = it.physiologicalOutcomeRes
            ))
        }

        // 4. NEUROENDOCRINE & STRESS MODULE
        vitalis.stressModule.priorityActions.forEach {
            list.add(AdeAction(
                id = it.id, 
                type = ActionType.Regulation, 
                instructionRes = it.titleRes, 
                urgency = Urgency.Immediate, 
                priorityScore = 94.0, 
                contextRes = it.reasoningRes, 
                researchInsightRes = it.researchInsightRes,
                biologicalRationaleRes = it.biologicalRationaleRes,
                physiologicalOutcomeRes = it.physiologicalOutcomeRes
            ))
        }

        // 5. PHYSICAL & ARTICULAR MODULE
        vitalis.physicalModule.priorityActions.forEach {
            list.add(AdeAction(
                id = it.id, 
                type = ActionType.Interruption, 
                instructionRes = it.titleRes, 
                urgency = Urgency.Scheduled, 
                priorityScore = 85.0, 
                contextRes = it.reasoningRes, 
                researchInsightRes = it.researchInsightRes,
                biologicalRationaleRes = it.biologicalRationaleRes,
                physiologicalOutcomeRes = it.physiologicalOutcomeRes
            ))
        }

        // 6. COGNITIVE & NEURAL MODULE
        vitalis.cognitiveModule.priorityActions.forEach {
            list.add(AdeAction(
                id = it.id, 
                type = ActionType.Execution, 
                instructionRes = it.titleRes, 
                urgency = Urgency.Scheduled, 
                priorityScore = 82.0, 
                contextRes = it.reasoningRes, 
                researchInsightRes = it.researchInsightRes,
                biologicalRationaleRes = it.biologicalRationaleRes,
                physiologicalOutcomeRes = it.physiologicalOutcomeRes
            ))
        }

        return list
    }

    private fun calculateConfidence(vitalisData: VitalisData): Double {
        val confidences = vitalisData.systems.values.map { it.confidence }
        return if (confidences.isNotEmpty()) confidences.average() / 100.0 else 0.98
    }
}
