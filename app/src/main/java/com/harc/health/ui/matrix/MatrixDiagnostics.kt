package com.harc.health.ui.matrix

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween

@Composable
fun Day1DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    var trigger by remember { mutableStateOf("") }
    var mentalShift by remember { mutableStateOf("") }
    var escalation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SYSTEM DIAGNOSTIC: DAY 1",
            color = Color.Red,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            1 -> {
                Text("PHASE 1: TRIGGER IDENTIFICATION", color = Color.White, fontWeight = FontWeight.Bold)
                Text("What is the very first thing that starts the loop? (e.g., Boredom, Stress, Instagram, Bedroom)", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = trigger,
                    onValueChange = { trigger = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.Red)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { step = 2 }, enabled = trigger.isNotBlank(), modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("NEXT")
                }
            }
            2 -> {
                Text("PHASE 2: MENTAL SHIFT", color = Color.White, fontWeight = FontWeight.Bold)
                Text("What is the first thought that gives you 'permission'? (e.g., 'Just one look', 'I'm already stressed')", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = mentalShift,
                    onValueChange = { mentalShift = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.Red)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { step = 3 }, enabled = mentalShift.isNotBlank(), modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("NEXT")
                }
            }
            3 -> {
                Text("PHASE 3: ESCALATION", color = Color.White, fontWeight = FontWeight.Bold)
                Text("What is the first physical action you take? (e.g., Opening Incognito, Searching a keyword)", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = escalation,
                    onValueChange = { escalation = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.Red)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { step = 4 }, enabled = escalation.isNotBlank(), modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("NEXT")
                }
            }
            4 -> {
                Text("DIAGNOSTIC COMPLETE", color = Color.Green, fontWeight = FontWeight.Bold)
                Text("You have exposed the first three stages of your system. You are no longer acting in the dark.", color = Color.White, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("FINISH", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Day2DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    var dangerZone by remember { mutableStateOf("") }
    var autopilotPath by remember { mutableStateOf("") }
    var isolationTrigger by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SYSTEM DIAGNOSTIC: DAY 2",
            color = Color.Red,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
        Text(
            "ENVIRONMENTAL CONDITIONING",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            1 -> {
                Text("PHASE 1: DANGER ZONES", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Where is your awareness lowest? (e.g., Bed, Bathroom, Desk when alone)", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = dangerZone,
                    onValueChange = { dangerZone = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.Red)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { step = 2 }, enabled = dangerZone.isNotBlank(), modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("IDENTIFY DANGER ZONE")
                }
            }
            2 -> {
                Text("PHASE 2: AUTOPILOT PATH", color = Color.White, fontWeight = FontWeight.Bold)
                Text("What is the physical sequence? (e.g., Lying down -> Phone in hand -> Door closed)", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = autopilotPath,
                    onValueChange = { autopilotPath = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.Red)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { step = 3 }, enabled = autopilotPath.isNotBlank(), modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("MAP SEQUENCE")
                }
            }
            3 -> {
                Text("PHASE 3: ISOLATION TRIGGER", color = Color.White, fontWeight = FontWeight.Bold)
                Text("The combination of Isolation + Device + Passivity is fatal. When do you choose isolation?", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = isolationTrigger,
                    onValueChange = { isolationTrigger = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color.Red)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { step = 4 }, enabled = isolationTrigger.isNotBlank(), modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("COMPLETE MAPPING")
                }
            }
            4 -> {
                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.Green, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("MAPPING COMPLETE", color = Color.Green, fontWeight = FontWeight.Bold)
                Text(
                    "You have identified the physical triggers. Awareness is the first step to interruption.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("INITIALIZE PROTOCOL", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Day3DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    var detectionStage by remember { mutableStateOf("") }
    var defaultResponse by remember { mutableStateOf("") }
    var selectedLadderStage by remember { mutableStateOf<Int?>(null) }

    val stages = listOf(
        "Small Thought" to "The loop begins as a whisper. Easy to dismiss, but it's the seed.",
        "Fantasy" to "You start simulating the reward. The brain begins releasing dopamine.",
        "Restlessness" to "Physical tension begins. The body is preparing for the loop.",
        "Scrolling" to "Searching for a trigger. You are now actively feeding the urge.",
        "Arousal" to "The logical brain is shutting down. The lizard brain is taking over.",
        "Strong Urge" to "The 'Point of No Return'. Negotiation is almost impossible here.",
        "Relapse" to "System failure. The loop is completed and reinforced."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SYSTEM DIAGNOSTIC: DAY 3",
            color = Color.Red,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
        Text(
            "EARLY DETECTION PROTOCOL",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            1 -> {
                Text("SECTION 1: THE ESCALATION LADDER", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Tap each stage to understand the escalation.", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(modifier = Modifier.weight(1f)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        stages.forEachIndexed { index, (stage, desc) ->
                            val isSelected = selectedLadderStage == index
                            Card(
                                onClick = { selectedLadderStage = index },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF331111) else Color(0xFF111111)
                                ),
                                border = BorderStroke(1.dp, if (isSelected) Color.Red else Color.DarkGray)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        stage,
                                        color = if (index < 3) Color.Gray else if (index < 5) Color.Yellow else Color.Red,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (isSelected) {
                                        Text(desc, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                    }
                                }
                            }
                            if (index < stages.size - 1) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { step = 2 }, 
                    modifier = Modifier.fillMaxWidth(), 
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    enabled = selectedLadderStage != null
                ) {
                    Text("I UNDERSTAND THE STAGES")
                }
            }
            2 -> {
                Text("PHASE 2: DETECTION AUDIT", color = Color.White, fontWeight = FontWeight.Bold)
                Text("At which stage do you usually notice the loop?", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                val options = listOf("Fantasy", "Restlessness", "Scrolling", "Strong Urge", "Almost too late")
                options.forEach { option ->
                    Button(
                        onClick = { detectionStage = option; step = 3 },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (detectionStage == option) Color.Red else Color.DarkGray)
                    ) {
                        Text(option)
                    }
                }
            }
            3 -> {
                Text("PHASE 3: RESPONSE AUDIT", color = Color.White, fontWeight = FontWeight.Bold)
                Text("What is your current 'Default Response'?", color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                val options = listOf("Ignore it", "Negotiate with it", "Keep scrolling", "Lie down", "Wait too long")
                options.forEach { option ->
                    Button(
                        onClick = { defaultResponse = option; step = 4 },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (defaultResponse == option) Color.Red else Color.DarkGray)
                    ) {
                        Text(option)
                    }
                }
            }
            4 -> {
                Text("THE 10-SECOND RULE", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Every second you spend 'negotiating' after noticing an urge strengthens the neural pathway.\n\n" +
                    "For Day 3+, you must interrupt the loop within 10 seconds of awareness.\n\n" +
                    "The earlier you interrupt, the weaker the loop becomes.", 
                    color = Color.White, 
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { step = 5 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("START EARLY INTERRUPTION TRAINING", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            5 -> {
                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.Green, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("DIAGNOSTIC COMPLETE", color = Color.Green, fontWeight = FontWeight.Bold)
                Text(
                    "Speed is your strongest weapon.\n\n" +
                    "Rule: Interrupt every urge within 10 seconds. Move your body physically to reset the mind.", 
                    color = Color.White, 
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("INITIALIZE PROTOCOL", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Day4DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    var selectedFantasyTrigger by remember { mutableStateOf<String?>(null) }
    var selectedFrequency by remember { mutableStateOf<String?>(null) }
    var stabilizationTimer by remember { mutableStateOf(60) }
    var timerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning, stabilizationTimer) {
        if (timerRunning && stabilizationTimer > 0) {
            delay(1000)
            stabilizationTimer--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SYSTEM DIAGNOSTIC: DAY 4",
            color = Color.Red,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
        Text(
            "MENTAL ESCALATION AUDIT",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            1 -> {
                Text(
                    "\"The loop often begins mentally before physically.\"",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Most behavior starts with mental repetition. What usually happens before strong urges?",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                val choices = listOf("Fantasizing", "Replaying scenes", "Curiosity", "Suggestive scrolling")
                choices.forEach { choice ->
                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text(choice)
                    }
                }
            }
            2 -> {
                Text("THE HIDDEN STAGE", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Good. That is the hidden escalation stage. Fantasy feeds the loop and keeps the brain emotionally connected to it.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text("THE VISUAL FLOW", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                val flow = listOf("Trigger", "Fantasy", "Emotional Stimulation", "Escalation", "Strong Urge", "Relapse")
                flow.forEachIndexed { index, stage ->
                    Text(stage, color = if (stage == "Fantasy") Color.Red else Color.White, fontWeight = if (stage == "Fantasy") FontWeight.Bold else FontWeight.Normal)
                    if (index < flow.size - 1) Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("How often do you ignore the fantasy stage?", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                val frequencies = listOf("Always", "Often", "Sometimes", "Rarely")
                frequencies.forEach { freq ->
                    Button(
                        onClick = { selectedFrequency = freq; step = 3 },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedFrequency == freq) Color.Red else Color.DarkGray)
                    ) {
                        Text(freq)
                    }
                }
            }
            3 -> {
                Text("WHY FANTASY IS DANGEROUS", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Repeated fantasy keeps the reward system emotionally activated, making escalation easier.\n\nThe brain does not fully separate imagination from stimulation.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text("What usually triggers your fantasy?", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                val triggers = listOf("Boredom", "Loneliness", "Scrolling", "Memories", "Stress")
                triggers.forEach { trigger ->
                    Button(
                        onClick = { selectedFantasyTrigger = trigger; step = 4 },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedFantasyTrigger == trigger) Color.Red else Color.DarkGray)
                    ) {
                        Text(trigger)
                    }
                }
            }
            4 -> {
                Text("NEW RULE: LABEL & INTERRUPT", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "The moment fantasy begins:\n1. Recognize it\n2. Label it ('I am fantasizing')\n3. Interrupt immediately\n\nThe fantasy window is the best place to interrupt because arousal is still low.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text("INTERVENTION TRAINING", color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("If fantasy starts, you must:", color = Color.LightGray)
                Text("Stand up. Shift environment. Move.", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
                if (!timerRunning) {
                    Button(
                        onClick = { timerRunning = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("START 60s STABILIZATION DRILL")
                    }
                } else {
                    Text("$stabilizationTimer", color = Color.Red, fontSize = 48.sp, fontWeight = FontWeight.Black)
                    if (stabilizationTimer == 0) {
                        Button(
                            onClick = onComplete,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("COMPLETE DAY 4", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Day5DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    var selectedInterruption by remember { mutableStateOf<String?>(null) }
    var drillTimer by remember { mutableStateOf(60) }
    var timerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning, drillTimer) {
        if (timerRunning && drillTimer > 0) {
            delay(1000)
            drillTimer--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SYSTEM DIAGNOSTIC: DAY 5",
            color = Color.Red,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
        Text(
            "BREAK THE AUTOPILOT",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            1 -> {
                Text(
                    "\"A large part of your behavior has become automatic.\"",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "You often move toward relapse before consciously deciding. It's not random. It's autopilot.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "How often do you notice yourself already escalating before becoming fully aware?",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                val choices = listOf("Often", "Sometimes", "Almost every relapse", "Rarely")
                choices.forEach { choice ->
                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text(choice)
                    }
                }
            }
            2 -> {
                Text("THE AUTOPILOT SEQUENCE", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                val flow = listOf("Bored", "Grab phone automatically", "Scroll passively", "Fantasy begins", "Escalation strengthens", "Relapse risk increases")
                flow.forEachIndexed { index, stage ->
                    Text(stage, color = Color.White, textAlign = TextAlign.Center)
                    if (index < flow.size - 1) Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "The loop becomes powerful when actions happen without awareness.",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { step = 3 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("I UNDERSTAND", color = Color.Black)
                }
            }
            3 -> {
                Text("PASSIVITY IS DANGEROUS", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Autopilot grows strongest during passive states:\n• Lying down\n• Scrolling aimlessly\n• Isolation\n• Late-night fatigue",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "The less conscious and active you become, the easier the loop takes control.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { step = 4 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("START WAKE-UP DRILL")
                }
            }
            4 -> {
                Text("WAKE-UP INTERRUPTION", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Whenever you catch autopilot, you must interrupt it immediately.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("1. Stop immediately.", color = Color.Gray)
                Text("2. Look around your environment.", color = Color.Gray)
                Text("3. Stand up.", color = Color.Gray)
                Text("4. Move for 60 seconds.", color = Color.White, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(32.dp))
                if (!timerRunning) {
                    Button(
                        onClick = { timerRunning = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("START 60s MOVEMENT")
                    }
                } else {
                    Text("$drillTimer", color = Color.Red, fontSize = 48.sp, fontWeight = FontWeight.Black)
                    if (drillTimer == 0) {
                        Button(
                            onClick = { step = 5 },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("CONTINUE", color = Color.Black)
                        }
                    }
                }
            }
            5 -> {
                Text("WAKE UP EARLIER", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Relapse usually builds gradually through unconscious momentum. Every conscious interruption weakens the loop.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text("TODAY'S CHALLENGE:", color = Color.Gray, fontSize = 12.sp)
                Text("CATCH 3 UNCONSCIOUS BEHAVIORS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "No passive scrolling. No lying down with your device. Interrupt autopilot immediately.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("I AM CONSCIOUS. COMPLETE DAY 5.", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Day6DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    var observeTimer by remember { mutableStateOf(90) }
    var timerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(timerRunning, observeTimer) {
        if (timerRunning && observeTimer > 0) {
            delay(1000)
            observeTimer--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SYSTEM DIAGNOSTIC: DAY 6",
            color = Color.Red,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
        Text(
            "URGE TOLERANCE",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            1 -> {
                Text(
                    "\"An urge is not a command.\"",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "The loop trained you to react quickly whenever discomfort appeared. Today you learn to observe without obeying.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text("What do you usually feel when a strong urge appears?", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                val feelings = listOf("Panic", "Pressure", "Loss of control", "Urgency")
                feelings.forEach { feeling ->
                    Button(
                        onClick = { step = 2 },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text(feeling)
                    }
                }
            }
            2 -> {
                Text("THE URGE WAVE", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Urges behave like waves. They rise, peak, and weaken if not continuously fed.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                val waveStages = listOf("Urge Appears", "Intensity Rises", "Peak", "Weakening", "Passes")
                waveStages.forEachIndexed { index, stage ->
                    Text(stage, color = Color.White)
                    if (index < waveStages.size - 1) Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { step = 3 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("I UNDERSTAND", color = Color.Black)
                }
            }
            3 -> {
                Text("90-SECOND OBSERVATION", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "The goal is not to enjoy the urge. The goal is to remain conscious during it. Observe the sensations without reacting.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                if (!timerRunning) {
                    Button(
                        onClick = { timerRunning = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("START 90s OBSERVATION")
                    }
                } else {
                    Text("$observeTimer", color = Color.Red, fontSize = 48.sp, fontWeight = FontWeight.Black)
                    Text("Breathe slowly. Notice the sensations.", color = Color.Gray)
                    if (observeTimer == 0) {
                        Button(
                            onClick = { step = 4 },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("CONTINUE", color = Color.Black)
                        }
                    }
                }
            }
            4 -> {
                Text("URGE vs ESCALATION", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "An urge alone is not the full loop. Escalation happens when the urge is fed through fantasy, scrolling, or passivity.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Real control is remaining conscious when urges appear.",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { step = 5 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("PROCEED", color = Color.Black)
                }
            }
            5 -> {
                Text("TODAY'S CHALLENGE", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("DO NOT PANIC DURING URGES", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Complete 3 conscious urge observations today. Breathe. Observe. Stay conscious. Interrupt calmly.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("I AM IN CONTROL. COMPLETE DAY 6.", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Day7DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SYSTEM DIAGNOSTIC: DAY 7", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
        Text("THE MATRIX REVEALED", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        when (step) {
            1 -> {
                Text("Phase 1 Complete: Wake Up.", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("You have spent 7 days observing the machine. You no longer act in total darkness. The loop is visible.", color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("REVEAL PATTERN", color = Color.Black)
                }
            }
            2 -> {
                Text("THE PREDICTABLE MACHINE", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your relapses and urges follow a mathematical path. Boredom/Stress -> Phone -> Search -> Trance -> Regret.", color = Color.White, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Do you now see that you are not 'broken', but simply 'conditioned'?", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { step = 3 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                    Text("I SEE THE SYSTEM")
                }
            }
            3 -> {
                Text("ENTERING PHASE 2", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("BREAK THE LOOP", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Starting tomorrow, the protocol becomes aggressive. Observation is no longer enough. You must destroy the loop at the root.", color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("INITIATE PHASE 2", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Day10DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SYSTEM DIAGNOSTIC: DAY 10", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
        Text("CONFRONT THE NEGOTIATOR", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        when (step) {
            1 -> {
                Text("The Negotiator is the voice that says 'Just one look' or 'You've done well, you deserve a break.'", color = Color.White, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Negotiation is the first stage of relapse.", color = Color.Red, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("IDENTIFY THE VOICE", color = Color.Black)
                }
            }
            2 -> {
                Text("Which lie does your Negotiator use most?", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                listOf("Just checking", "I'm stressed", "One last time", "I can control it").forEach { lie ->
                    Button(onClick = { step = 3 }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                        Text(lie)
                    }
                }
            }
            3 -> {
                Text("THE RULE OF ZERO NEGOTIATION", color = Color.Red, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                Text("When the Negotiator speaks, you do not argue. You simply interrupt. Movement is the answer, not logic.", color = Color.White, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("END THE NEGOTIATION", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Day15DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SYSTEM DIAGNOSTIC: DAY 15", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
        Text("STAY IN THE GAP", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        when (step) {
            1 -> {
                Text("HALFWAY POINT REACHED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("The 'Gap' is the space between an urge and your reaction. Your freedom lives in making that gap wider.", color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("MEASURE THE GAP", color = Color.Black)
                }
            }
            2 -> {
                Text("Is your reaction time slowing down?", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text("On Day 1, you reacted in <1s. Now, you have the power to wait.", color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { step = 3 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                    Text("I AM GAINING CONTROL")
                }
            }
            3 -> {
                Text("STAY CONSCIOUS", color = Color.Red, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                Text("The next 15 days will test your endurance. The novelty of the program has worn off. Now comes the discipline.", color = Color.White, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("CONTINUE THE MISSION", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Day21DiagnosticFlow(onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SYSTEM DIAGNOSTIC: DAY 21", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
        Text("CONSCIOUS DOMINANCE", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        when (step) {
            1 -> {
                Text("ENTERING PHASE 3: REBUILD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("You are no longer fighting for survival. You are now training for dominance over your own biology.", color = Color.Gray, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                    Text("ASSESS DOMINANCE", color = Color.Black)
                }
            }
            2 -> {
                Text("Speed is your metric.", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text("When a fantasy appears, how quickly do you label it and return to reality?", color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))
                listOf("Instantly", "< 5 seconds", "I still struggle").forEach { speed ->
                    Button(onClick = { step = 3 }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                        Text(speed)
                    }
                }
            }
            3 -> {
                Text("THE ZERO-FANTASY RULE", color = Color.Red, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                Text("For the final 9 days, we allow zero mental simulations. If the thought appears, kill it instantly. Starve the loop.", color = Color.White, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("ESTABLISH DOMINANCE", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Day30DiagnosticFlow(
    userName: String,
    metrics: MatrixMetrics,
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var leavingBehind by remember { mutableStateOf("") }
    var isHolding by remember { mutableStateOf(false) }
    val holdProgress = remember { Animatable(0f) }
    val silenceAlpha = remember { Animatable(0f) }

    LaunchedEffect(isHolding) {
        if (isHolding) {
            holdProgress.animateTo(1f, tween(3000))
            if (holdProgress.value >= 1f) {
                step = 8 // Graduation Message
            }
        } else {
            holdProgress.snapTo(0f)
        }
    }

    LaunchedEffect(step) {
        if (step == 5) { // Silence Moment
            silenceAlpha.animateTo(1f, tween(2000))
            delay(5000)
            step = 6
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (step) {
            1 -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "30 days ago, the loop operated mostly automatically.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "Today, you recognize it.",
                        color = Color.Red,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(64.dp))
                    Button(
                        onClick = { step = 2 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("CONSCIOUSLY ENTER THE FINAL CHAPTER", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
            2 -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("THE JOURNEY", color = Color.Gray, letterSpacing = 4.sp, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    val timeline = listOf(
                        "Day 1" to "You saw the loop clearly.",
                        "Day 5" to "You began interrupting autopilot.",
                        "Day 9" to "You recognized hidden triggers.",
                        "Day 14" to "You reacted faster under pressure.",
                        "Day 18" to "You survived urges consciously.",
                        "Day 24" to "Your awareness strengthened.",
                        "Day 30" to "You are no longer fully controlled by unconscious repetition."
                    )
                    timeline.forEach { (day, desc) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(day, color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                            Text(desc, color = Color.White, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = { step = 3 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("REFLECT ON TRANSFORMATION", color = Color.Black)
                    }
                }
            }
            3 -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("OBSERVED EVIDENCE", color = Color.Gray, letterSpacing = 4.sp, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    ReflectionRow("Interruption Speed", metrics.interruptionSpeed)
                    ReflectionRow("Awareness Score", "${metrics.awarenessScore}%")
                    ReflectionRow("Control Growth", metrics.resilienceGrowth)
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "The app has observed your growth. You interrupt faster, recognize fantasy earlier, and remain more conscious during high-risk moments.",
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = { step = 4 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("FACE THE OLD LOOP", color = Color.Black)
                    }
                }
            }
            4 -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("THE CONFRONTATION", color = Color.Gray, letterSpacing = 4.sp, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(48.dp))
                    val oldLoop = listOf("Isolation", "Scrolling", "Fantasy", "Escalation", "Relapse", "Repeat")
                    oldLoop.forEach { stage ->
                        Text(stage, color = Color.DarkGray, fontSize = 24.sp, fontWeight = FontWeight.ExtraLight, letterSpacing = 2.sp)
                        if (stage != "Repeat") Text("↓", color = Color.Red.copy(alpha = 0.3f))
                    }
                    Spacer(modifier = Modifier.height(64.dp))
                    Text(
                        "This loop no longer operates in darkness.",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = { step = 5 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("ENTER THE SILENCE", color = Color.Black)
                    }
                }
            }
            5 -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(200.dp),
                        color = Color.White.copy(alpha = silenceAlpha.value * 0.2f),
                        strokeWidth = 1.dp
                    )
                    Text(
                        "PEACE",
                        color = Color.White.copy(alpha = silenceAlpha.value),
                        letterSpacing = 8.sp,
                        fontWeight = FontWeight.ExtraLight
                    )
                }
            }
            6 -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("THE FINAL QUESTION", color = Color.Gray, letterSpacing = 4.sp, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "What are you leaving behind?",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = leavingBehind,
                        onValueChange = { leavingBehind = it },
                        placeholder = { Text("Habits, lies, repetitions...", color = Color.DarkGray) },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { step = 7 },
                        enabled = leavingBehind.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("PREPARE TO RELEASE", color = Color.Black)
                    }
                }
            }
            7 -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("THE EXIT ACTION", color = Color.Gray, letterSpacing = 4.sp, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(64.dp))
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { holdProgress.value },
                            modifier = Modifier.size(160.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                            trackColor = Color.DarkGray
                        )
                        Surface(
                            modifier = Modifier
                                .size(120.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isHolding = true
                                            tryAwaitRelease()
                                            isHolding = false
                                        }
                                    )
                                },
                            shape = CircleShape,
                            color = if (isHolding) Color.Red else Color.DarkGray
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "HOLD TO\nEXIT THE LOOP",
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(48.dp))
                    Text(
                        "Release the version of yourself that was trapped.",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            8 -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "You are still human.\nYou will still face stress, urges, temptation, and difficult moments.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "But you are no longer blind inside the loop.\nYou recognize escalation earlier.\nYou interrupt faster.\nYou remain more conscious under pressure.",
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "And that changes everything.",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = { step = 9 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("RECEIVE CERTIFICATE", color = Color.Black)
                    }
                }
            }
            9 -> {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("CERTIFICATE OF EXIT", color = Color.White, letterSpacing = 4.sp, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("This certifies that", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(userName.uppercase(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("successfully completed", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("30 DAYS OUT OF THE MATRIX", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(32.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                CertificateMetric("AWARENESS", "${metrics.awarenessScore}%")
                                CertificateMetric("INTERRUPTION", metrics.interruptionSpeed)
                                CertificateMetric("CONTROL", metrics.resilienceGrowth)
                            }
                            Spacer(modifier = Modifier.height(48.dp))
                            Text("CONSCIOUS RECOVERY COMPLETED", color = Color.Gray, fontSize = 10.sp, letterSpacing = 2.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "Graduation is not the end of awareness.\nIt is the beginning of living consciously outside the loop.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { step = 10 },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("ENTER THE NEXT CHAPTER", fontWeight = FontWeight.Bold)
                    }
                }
            }
            10 -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TRANSFORMATION OF IDENTITY", color = Color.Gray, letterSpacing = 2.sp, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "You have evolved from a trapped person to a conscious survivor.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Text("WOULD YOU LIKE TO BECOME A RECOVERY GUIDE?", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Help others recognize their loops. Your experience is now a tool for the collective.", color = Color.LightGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(64.dp))
                    Text(
                        "Awareness replaced unconscious repetition.\nAnd that is where freedom begins.",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(64.dp))
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("EXIT TO MAINTENANCE MODE", color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun ReflectionRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CertificateMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.DarkGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
