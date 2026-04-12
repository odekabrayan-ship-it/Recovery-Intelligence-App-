package com.harc.health.model

import androidx.annotation.StringRes

/**
 * VitalisData: The core data structure for the 12-module Longevity System.
 * Rebuilt as an activity-driven execution model.
 */
data class VitalisData(
    val longevityScore: Int = 0,
    @StringRes val trajectoryRes: Int = 0,
    val systems: Map<String, SystemHealth> = emptyMap(),
    
    // The 12 Expert Modules
    val sleepModule: SleepModuleData = SleepModuleData(),
    val cardioModule: CardiovascularModuleData = CardiovascularModuleData(),
    val metabolicModule: MetabolicModuleData = MetabolicModuleData(),
    val stressModule: StressModuleData = StressModuleData(),
    val physicalModule: PhysicalActivityModuleData = PhysicalActivityModuleData(),
    val cognitiveModule: CognitiveModuleData = CognitiveModuleData(),
    val recoveryModule: RecoveryModuleData = RecoveryModuleData(),
    val protocolModule: LongevityProtocolsModuleData = LongevityProtocolsModuleData(),
    val patternModule: PatternAnalysisModuleData = PatternAnalysisModuleData(),
    val trajectoryModule: TrajectoryModuleData = TrajectoryModuleData(),
    val researchModule: ResearchModuleData = ResearchModuleData(),
    
    val priorityActions: List<PriorityAction> = emptyList()
)

data class SystemHealth(
    @StringRes val statusRes: Int,
    val score: Int,
    @StringRes val trendRes: Int,
    @StringRes val trendDescriptionRes: Int = 0,
    val confidence: Int,
    @StringRes val implicationRes: Int,
    @StringRes val actionRes: Int
)

// Shared Activity Structure - Now supporting Localization via StringRes
data class VitalisActivity(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val phaseRes: Int, // "Morning", "Daytime", "Evening", "Execution"
    @StringRes val instructionsRes: Int,
    @StringRes val biologicalRationaleRes: Int,
    @StringRes val physiologicalOutcomeRes: Int,
    @StringRes val researchInsightRes: Int = 0,
    val duration: String = "",
    val isCompleted: Boolean = false
)

data class SleepModuleData(
    val sleepScore: Int = 0,
    @StringRes val circadianAlignmentRes: Int = 0,
    val activities: List<VitalisActivity> = emptyList(),
    val priorityActions: List<PriorityAction> = emptyList()
)

data class CardiovascularModuleData(
    val healthScore: Int = 0,
    val activities: List<VitalisActivity> = emptyList(),
    val priorityActions: List<PriorityAction> = emptyList()
)

data class MetabolicModuleData(
    val efficiencyScore: Int = 0,
    val activities: List<VitalisActivity> = emptyList(),
    val priorityActions: List<PriorityAction> = emptyList()
)

data class StressModuleData(
    val stressLoadScore: Int = 0,
    val activities: List<VitalisActivity> = emptyList(),
    val priorityActions: List<PriorityAction> = emptyResActions()
)

data class PhysicalActivityModuleData(
    val consistencyScore: Int = 0,
    val activities: List<VitalisActivity> = emptyList(),
    val priorityActions: List<PriorityAction> = emptyResActions()
)

data class CognitiveModuleData(
    val performanceScore: Int = 0,
    val activities: List<VitalisActivity> = emptyList(),
    val priorityActions: List<PriorityAction> = emptyResActions()
)

data class RecoveryModuleData(
    val restorationScore: Int = 0,
    val activities: List<VitalisActivity> = emptyList(),
    val priorityActions: List<PriorityAction> = emptyResActions()
)

data class LongevityProtocolsModuleData(
    val activeProtocols: List<LongevityProtocol> = emptyList(),
    val adherenceScore: Int = 0,
    val completionRate: Int = 0,
    @StringRes val behavioralStabilityRes: Int = 0,
    val priorityActions: List<PriorityAction> = emptyResActions()
)

data class PatternAnalysisModuleData(
    val identifiedPatterns: List<VitalisPattern> = emptyList(),
    @StringRes val insightLevelRes: Int = 0
)

data class TrajectoryModuleData(
    val projectedHealthAge: Int = 0,
    @StringRes val riskTrendRes: Int = 0,
    val futureDirectives: List<TrajectoryDirective> = emptyList()
)

data class TrajectoryDirective(
    @StringRes val directiveRes: Int,
    val args: List<String> = emptyList()
)

data class ResearchModuleData(
    val currentInsights: List<ResearchInsight> = emptyList()
)

data class VitalisPattern(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val impactRes: Int
)

data class ResearchInsight(
    @StringRes val topicRes: Int,
    @StringRes val simplifiedEvidenceRes: Int,
    @StringRes val referenceRes: Int
)

data class LongevityProtocol(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val purposeRes: Int,
    val instructionsRes: List<Int>,
    @StringRes val shortTermBenefitRes: Int,
    @StringRes val longTermBenefitRes: Int,
    @StringRes val researchInsightRes: Int = 0,
    @StringRes val statusRes: Int = 0,
    @StringRes val timeOfDayRes: Int = 0
)

data class PriorityAction(
    val id: String = "",
    @StringRes val titleRes: Int,
    @StringRes val reasoningRes: Int,
    @StringRes val biologicalRationaleRes: Int = 0,
    @StringRes val physiologicalOutcomeRes: Int = 0,
    @StringRes val researchInsightRes: Int = 0,
    val priority: String // "High", "Medium", "Low"
)

fun emptyResActions() = emptyList<PriorityAction>()
