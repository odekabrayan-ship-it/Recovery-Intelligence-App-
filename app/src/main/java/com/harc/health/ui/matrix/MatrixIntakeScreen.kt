package com.harc.health.ui.matrix

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.R
import kotlinx.coroutines.delay

@Composable
fun MatrixIntakeScreen(
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(0) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = step, label = "intake_step") { currentStep ->
            when (currentStep) {
                0 -> StepOpening { step = 1 }
                1 -> StepConfrontation { step = 2 }
                2 -> StepLoopRecognition { step = 3 }
                3 -> StepEntryPoints { step = 4 }
                4 -> StepHiddenTruth { step = 5 }
                5 -> StepRealCost { step = 6 }
                6 -> StepDangerHours { step = 7 }
                7 -> StepHonestyCheck { step = 8 }
                8 -> StepRecoveryReality { step = 9 }
                9 -> StepDecision { step = 10 }
                10 -> StepContract { step = 11 }
                11 -> StepFinalMessage(onComplete)
            }
        }
    }
}

@Composable
fun StepOpening(onNext: () -> Unit) {
    var showSecondPart by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(2000)
        showSecondPart = true
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(R.string.matrix_intake_opening_1),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 28.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        AnimatedVisibility(
            visible = showSecondPart,
            enter = fadeIn(tween(1000))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.matrix_intake_opening_2),
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(stringResource(R.string.settings_close).uppercase(), color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StepConfrontation(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(R.string.matrix_intake_confrontation_title),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        val options = listOf("Months", "Years", "I don't even know anymore")
        options.forEach { option ->
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                border = BorderStroke(1.dp, Color.DarkGray),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(option, color = Color.White)
            }
        }
    }
}

@Composable
fun StepLoopRecognition(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.matrix_intake_loop_recognition_title), color = Color.Red, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        val stages = listOf("Trigger", "Fantasy", "Escalation", "Pornography", "Masturbation", "Aftermath", "Repeat")
        stages.forEachIndexed { index, stage ->
            Text(stage, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Light)
            if (index < stages.size - 1) {
                Text("↓", color = Color.Red, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Text(stringResource(R.string.matrix_intake_loop_recognition_familiar), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("IT IS FAMILIAR", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StepEntryPoints(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.matrix_intake_entry_points_title), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        val points = listOf("Boredom", "Loneliness", "Stress", "Nighttime", "Scrolling", "Isolation", "Curiosity", "Emotional Escape")
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            points.forEach { point ->
                Surface(
                    modifier = Modifier.padding(4.dp).clickable { onNext() },
                    color = Color(0xFF1A1A1A),
                    border = BorderStroke(1.dp, Color.DarkGray),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(point, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 14.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Text(stringResource(R.string.matrix_intake_entry_points_feedback), color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}

@Composable
fun StepHiddenTruth(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.matrix_intake_hidden_truth_title), color = Color.Red, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            stringResource(R.string.matrix_intake_hidden_truth_1),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.matrix_intake_hidden_truth_2),
            color = Color.Red,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        val ladder = listOf("Isolation", "Scrolling", "Fantasy", "Escalation", "Relapse")
        ladder.forEachIndexed { index, item ->
            Text(item, color = if (index == 4) Color.Red else Color.White, fontSize = 16.sp)
            if (index < ladder.size - 1) Text("↓", color = Color.DarkGray)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            stringResource(R.string.matrix_intake_hidden_truth_3),
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("I UNDERSTAND THE LADDER", color = Color.Black)
        }
    }
}

@Composable
fun StepRealCost(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.matrix_intake_real_cost_title), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        val costs = listOf("Focus", "Confidence", "Discipline", "Relationships", "Emotional Stability", "Energy", "Sleep", "Self-respect")
        
        costs.forEach { cost ->
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                border = BorderStroke(1.dp, Color.DarkGray),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(cost, color = Color.White)
            }
        }
    }
}

@Composable
fun StepDangerHours(onNext: () -> Unit) {
    var selected by remember { mutableStateOf<String?>(null) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.matrix_intake_danger_hours_title), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        val hours = listOf("Late Night", "Early Morning", "After Stress", "While Alone", "After Social Media", "Boredom Periods")
        
        hours.forEach { hour ->
            Button(
                onClick = { selected = hour },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (selected == hour) Color.Red else Color(0xFF1A1A1A)),
                border = BorderStroke(1.dp, if (selected == hour) Color.Red else Color.DarkGray),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(hour, color = Color.White)
            }
        }
        
        if (selected != null) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(stringResource(R.string.matrix_intake_danger_hours_feedback), color = Color.Red, fontWeight = FontWeight.Black, fontSize = 12.sp)
            Text(if (selected == "Late Night") "HIGH RISK: 22:00 - 02:00" else "INCREASED SYSTEM VULNERABILITY", color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(4.dp)) {
                Text("LOG DANGER ZONE", color = Color.Black)
            }
        }
    }
}

@Composable
fun StepHonestyCheck(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.matrix_intake_honesty_check_title), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        val reactions = listOf("I delay action", "I keep scrolling", "I fantasize", "I isolate", "I negotiate", "I tell myself \"just once\"")
        
        reactions.forEach { reaction ->
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
                border = BorderStroke(1.dp, Color.DarkGray),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(reaction, color = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text(stringResource(R.string.matrix_intake_honesty_check_feedback), color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StepRecoveryReality(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.matrix_intake_recovery_reality_title), color = Color.Red, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            stringResource(R.string.matrix_intake_recovery_reality_1),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.matrix_intake_recovery_reality_2),
            color = Color.Red,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text("I ACCEPT THIS REALITY", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StepDecision(onNext: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.matrix_intake_decision_title), color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        
        val challenges = listOf("Passivity", "Escalation", "Fantasy feeding", "Unconscious repetition")
        challenges.forEach { challenge ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(challenge, color = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        Text(stringResource(R.string.matrix_intake_decision_prompt), color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(stringResource(R.string.matrix_intake_enter_system), fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        }
    }
}

@Composable
fun StepContract(onNext: () -> Unit) {
    val contracts = listOf(
        "I will not ignore escalation.",
        "I will interrupt urges early.",
        "I will act consciously during high-risk moments.",
        "I will remain honest with the system."
    )
    var agreedCount by remember { mutableStateOf(0) }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.matrix_intake_contract_title), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        contracts.forEachIndexed { index, contract ->
            val isAgreed = agreedCount > index
            val isCurrent = agreedCount == index
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(enabled = isCurrent) { agreedCount++ },
                color = if (isAgreed) Color.Red.copy(alpha = 0.1f) else if (isCurrent) Color(0xFF1A1A1A) else Color.Transparent,
                border = BorderStroke(1.dp, if (isAgreed) Color.Red else if (isCurrent) Color.White else Color.DarkGray),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (isAgreed) Icon(Icons.Default.Check, null, tint = Color.Red)
                    else Text("${index + 1}.", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(contract, color = if (isAgreed) Color.White else if (isCurrent) Color.White else Color.Gray)
                }
            }
        }
        
        if (agreedCount == contracts.size) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("SIGNED CONSCIOUSLY", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StepFinalMessage(onComplete: () -> Unit) {
    var showFinalButton by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(4000)
        showFinalButton = true
    }
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(R.string.matrix_intake_final_msg_1),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(R.string.matrix_intake_final_msg_2),
            color = Color.Red,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        if (showFinalButton) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.matrix_intake_final_msg_3), color = Color.Gray, letterSpacing = 4.sp, fontSize = 12.sp)
                Text(stringResource(R.string.matrix_intake_final_msg_4), color = Color.White, fontWeight = FontWeight.Light)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("BEGIN", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        } else {
            CircularProgressIndicator(color = Color.Red, modifier = Modifier.size(48.dp))
        }
    }
}
