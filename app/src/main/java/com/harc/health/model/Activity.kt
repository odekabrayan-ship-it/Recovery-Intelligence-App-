package com.harc.health.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Activity(
    val id: String,
    val titleRes: Int,
    val categoryRes: Int,
    val targetAudienceRes: Int,
    val durationSeconds: Int,
    val steps: List<ActivityStep>,
    val impactDescRes: Int,
    val icon: ImageVector? = null,
    val whatToDORes: Int = 0,
    val whyToDORes: Int = 0,
    val physiologicalOutcomeRes: Int = 0
)

data class ActivityStep(
    val titleRes: Int,
    val instructionRes: Int,
    val durationSeconds: Int,
    val animationType: String = "none"
)
