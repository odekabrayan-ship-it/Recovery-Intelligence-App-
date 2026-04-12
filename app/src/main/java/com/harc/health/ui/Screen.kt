package com.harc.health.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.harc.health.R

sealed class Screen(val route: String, val icon: ImageVector, @StringRes val label: Int) {
    object Home : Screen("home", Icons.Default.Home, R.string.nav_home)
    object Insights : Screen("insights", Icons.Default.Insights, R.string.nav_insights)
    object Coach : Screen("coach", Icons.Default.ContactSupport, R.string.nav_coach)
    object Intake : Screen("intake", Icons.Default.Add, R.string.intake_tab_intake)
    object Vitalis : Screen("vitalis", Icons.Default.AutoGraph, R.string.vitalis_command_center)
    object Recovery : Screen("recovery", Icons.Default.Restore, R.string.nav_recovery)
    object Profile : Screen("profile", Icons.Default.Person, R.string.settings_account_mgmt)
    object Lock : Screen("lock", Icons.Default.Lock, R.string.settings_security)
    object Therapeutic : Screen("therapeutic", Icons.Default.Spa, R.string.therapeutic_title)
    object Login : Screen("login", Icons.AutoMirrored.Filled.Login, R.string.login_sign_in)
}
