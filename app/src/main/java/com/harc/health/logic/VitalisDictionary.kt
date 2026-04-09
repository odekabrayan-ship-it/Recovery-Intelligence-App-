package com.harc.health.logic

import androidx.compose.ui.graphics.Color
import com.harc.health.ui.theme.VitalisEmerald
import com.harc.health.ui.theme.RedRisk
import com.harc.health.R

object VitalisDictionary {

    enum class Verdict(val color: Color, val labelRes: Int, val signalRes: Int) {
        PROTECT(VitalisEmerald, R.string.verdict_protect_label, R.string.verdict_protect_signal),
        CAUTION(RedRisk, R.string.verdict_caution_label, R.string.verdict_caution_signal),
        UNKNOWN(Color(0xFFFFD600), R.string.verdict_unknown_label, R.string.verdict_unknown_signal)
    }

    data class Entry(
        val id: String,
        val nameRes: Int,
        val verdict: Verdict,
        val rationaleRes: Int,
        val impactAreaRes: Int
    )

    private val DATABASE = listOf(
        Entry("sauna", R.string.ent_sauna, Verdict.PROTECT, R.string.rat_sauna, R.string.area_cardio),
        Entry("hiit", R.string.ent_hiit, Verdict.PROTECT, R.string.rat_hiit, R.string.area_metabolic),
        Entry("nmn", R.string.ent_nmn, Verdict.PROTECT, R.string.rat_nmn, R.string.area_cellular),
        Entry("magnesium", R.string.ent_magnesium, Verdict.PROTECT, R.string.rat_magnesium, R.string.area_neurological),
        Entry("omega3", R.string.ent_omega3, Verdict.PROTECT, R.string.rat_omega3, R.string.area_vascular),
        Entry("zone2", R.string.ent_zone2, Verdict.PROTECT, R.string.rat_zone2, R.string.area_metabolic),
        Entry("fasting", R.string.ent_fasting, Verdict.PROTECT, R.string.rat_fasting, R.string.area_cellular),
        Entry("alcohol", R.string.ent_alcohol, Verdict.CAUTION, R.string.rat_alcohol, R.string.area_systemic),
        Entry("nicotine", R.string.ent_nicotine, Verdict.CAUTION, R.string.rat_nicotine, R.string.area_cardio),
        Entry("sugar", R.string.ent_sugar, Verdict.CAUTION, R.string.rat_sugar, R.string.area_vascular),
        Entry("nac", R.string.ent_nac, Verdict.PROTECT, R.string.rat_nac, R.string.area_metabolic),
        Entry("thiamine", R.string.ent_thiamine, Verdict.PROTECT, R.string.rat_thiamine, R.string.area_neurological)
    )

    fun search(query: String): Entry? {
        if (query.isBlank()) return null
        val normalized = query.lowercase().trim()
        
        // Match by ID or simplified search logic
        return DATABASE.find { 
            it.id.contains(normalized) || normalized.contains(it.id)
        }
    }
}
