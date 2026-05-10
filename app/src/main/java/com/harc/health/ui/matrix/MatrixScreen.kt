package com.harc.health.ui.matrix

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harc.health.R
import com.harc.health.logic.MatrixEngine
import com.harc.health.model.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixScreen(
    onBack: () -> Unit,
    userName: String = "User",
    viewModel: MatrixViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val progress = (state as? MatrixState.Success)?.progress
    val metrics by viewModel.performanceMetrics.collectAsState()
    val driftAlert by viewModel.driftAlert.collectAsState()
    val loopMap by viewModel.loopMap.collectAsState()
    var showUrgeFlow by remember { mutableStateOf(false) }
    var showRelapseAnalysis by remember { mutableStateOf(false) }
    var showFinalReport by remember { mutableStateOf(false) }
    var showClosingTransition by remember { mutableStateOf(false) }
    var showOpeningTransition by remember { mutableStateOf(false) }
    
    // Sync opening transition state when progress loads
    LaunchedEffect(progress) {
        if (progress != null && progress.hasCompletedIntake && progress.currentDay > progress.lastSeenOpeningDay) {
            showOpeningTransition = true
        }
    }

    var showDay1Diagnostic by remember { mutableStateOf(false) }
    var showDay2Diagnostic by remember { mutableStateOf(false) }
    var showDay3Diagnostic by remember { mutableStateOf(false) }
    var showDay4Diagnostic by remember { mutableStateOf(false) }
    var showDay5Diagnostic by remember { mutableStateOf(false) }
    var showDay6Diagnostic by remember { mutableStateOf(false) }
    var showDay7Diagnostic by remember { mutableStateOf(false) }
    var showDay10Diagnostic by remember { mutableStateOf(false) }
    var showDay15Diagnostic by remember { mutableStateOf(false) }
    var showDay21Diagnostic by remember { mutableStateOf(false) }
    var showDay30Diagnostic by remember { mutableStateOf(false) }
    
    // Day 13+ Emergency Night Mode Logic
    val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val isHighRiskHour = currentHour >= 22 || currentHour < 5
    var showNightMode by remember { mutableStateOf((progress?.currentDay ?: 0) >= 13 && isHighRiskHour) }

    Scaffold(
        topBar = {
            if (progress?.hasCompletedIntake == true) {
                TopAppBar(
                    title = { Text(stringResource(R.string.matrix_title), fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.Red
                    )
                )
            }
        },
        containerColor = Color.Black
    ) { padding ->
        if (state is MatrixState.Loading) {
            // System Loading / Initializing
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Red)
            }
        } else if (progress?.hasCompletedIntake != true) {
            MatrixIntakeScreen(onComplete = { viewModel.startSystem() })
        } else if (showOpeningTransition) {
            MatrixTransitionScreen(
                mode = TransitionMode.OPENING,
                dayNumber = progress?.currentDay ?: 1,
                metrics = metrics,
                onComplete = {
                    viewModel.markOpeningSeen(progress?.currentDay ?: 1)
                    showOpeningTransition = false
                }
            )
        } else if (showClosingTransition) {
            MatrixTransitionScreen(
                mode = TransitionMode.CLOSING,
                dayNumber = progress?.currentDay ?: 1,
                metrics = metrics,
                onComplete = {
                    showClosingTransition = false
                    viewModel.completeDay()
                }
            )
        } else if (showNightMode) {
            EmergencyNightMode(onOverride = { showNightMode = false })
        } else if (showDay1Diagnostic) {
            Day1DiagnosticFlow(onComplete = {
                showDay1Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay2Diagnostic) {
            Day2DiagnosticFlow(onComplete = {
                showDay2Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay3Diagnostic) {
            Day3DiagnosticFlow(onComplete = {
                showDay3Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay4Diagnostic) {
            Day4DiagnosticFlow(onComplete = {
                showDay4Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay5Diagnostic) {
            Day5DiagnosticFlow(onComplete = {
                showDay5Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay6Diagnostic) {
            Day6DiagnosticFlow(onComplete = {
                showDay6Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay7Diagnostic) {
            Day7DiagnosticFlow(onComplete = {
                showDay7Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay10Diagnostic) {
            Day10DiagnosticFlow(onComplete = {
                showDay10Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay15Diagnostic) {
            Day15DiagnosticFlow(onComplete = {
                showDay15Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay21Diagnostic) {
            Day21DiagnosticFlow(onComplete = {
                showDay21Diagnostic = false
                showClosingTransition = true
            })
        } else if (showDay30Diagnostic) {
            Day30DiagnosticFlow(
                userName = userName,
                metrics = metrics,
                onComplete = {
                    showDay30Diagnostic = false
                    showClosingTransition = true
                }
            )
        } else if (showUrgeFlow) {
            val currentDay = progress?.currentDay ?: 1
            UrgeInterventionFlow(
                currentDay = currentDay,
                lastFutureSelf = viewModel.lastFutureSelfMessage,
                onComplete = { showUrgeFlow = false }
            )
        } else if (showFinalReport) {
            FinalMatrixReport(
                metrics = metrics,
                onComplete = { 
                    showFinalReport = false 
                }
            )
        } else if (showRelapseAnalysis) {
            RelapseAnalysisFlow(
                onComplete = { trigger, env, emotion, cost, lesson, futureSelf, escalation ->
                    viewModel.logRelapse(trigger, env, emotion, cost, lesson, futureSelf, escalation)
                    showRelapseAnalysis = false
                },
                onCancel = { showRelapseAnalysis = false }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    if (driftAlert != null) {
                        DriftAlertCard(driftAlert!!)
                    }
                }

                item {
                    PhaseCard(progress?.phase ?: MatrixPhase.WAKE_UP, progress?.currentDay ?: 1)
                }

                item {
                    Button(
                        onClick = { showUrgeFlow = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            stringResource(R.string.matrix_urge_emergency),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                item {
                    if (progress?.phase == MatrixPhase.MAINTENANCE) {
                        MaintenanceModeCard()
                    } else {
                        DailyDirectiveSection(
                            day = progress?.currentDay ?: 1,
                            onStartDiagnostic = { 
                                when (progress?.currentDay) {
                                    1 -> showDay1Diagnostic = true
                                    2 -> showDay2Diagnostic = true
                                    3 -> showDay3Diagnostic = true
                                    4 -> showDay4Diagnostic = true
                                    5 -> showDay5Diagnostic = true
                                    6 -> showDay6Diagnostic = true
                                    7 -> showDay7Diagnostic = true
                                    10 -> showDay10Diagnostic = true
                                    15 -> showDay15Diagnostic = true
                                    21 -> showDay21Diagnostic = true
                                    30 -> showDay30Diagnostic = true
                                    else -> viewModel.completeDay()
                                }
                            },
                            onComplete = {
                                if ((progress?.currentDay ?: 0) == 30) {
                                    showFinalReport = true
                                }
                                showClosingTransition = true
                            }
                        )
                    }
                }

                item {
                    TextButton(
                        onClick = { showRelapseAnalysis = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "I FAILED / LOG RELAPSE",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (progress?.phase == MatrixPhase.REBUILD_CONTROL || progress?.phase == MatrixPhase.MAINTENANCE || (progress?.currentDay ?: 0) >= 1) {
                    item { LoopMapSection(loopMap) }
                }
            }
        }
    }
}

@Composable
fun DriftAlertCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF331111)),
        border = BorderStroke(1.dp, Color.Red),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                message,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PhaseCard(phase: MatrixPhase, day: Int) {
    val daysLeft = (30 - day).coerceAtLeast(0)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        border = BorderStroke(1.dp, Color.Red),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(
                        when (phase) {
                            MatrixPhase.WAKE_UP -> R.string.matrix_phase_wake_up
                            MatrixPhase.BREAK_THE_LOOP -> R.string.matrix_phase_break_loop
                            MatrixPhase.REBUILD_CONTROL -> R.string.matrix_phase_rebuild_control
                            MatrixPhase.MAINTENANCE -> R.string.matrix_maintenance_mode
                        }
                    ),
                    color = if (phase == MatrixPhase.MAINTENANCE) Color.Cyan else Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                
                if (phase != MatrixPhase.MAINTENANCE) {
                    Text(
                        stringResource(R.string.matrix_days_left, daysLeft),
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                if (phase == MatrixPhase.MAINTENANCE) "STABILIZATION ACTIVE" else stringResource(R.string.matrix_day_counter, day),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            if (phase != MatrixPhase.MAINTENANCE) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { day.toFloat() / 30f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = Color.Red,
                    trackColor = Color.DarkGray,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun MaintenanceModeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF001A1A)),
        border = BorderStroke(1.dp, Color.Cyan),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.Cyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("MAINTENANCE PROTOCOL", color = Color.Cyan, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "The matrix is dissolved. Your neural pathways are re-established. Maintain the discipline.",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DailyDirectiveSection(day: Int, onComplete: () -> Unit, onStartDiagnostic: () -> Unit) {
    val directive = MatrixEngine.getDirectiveForDay(day)
    
    Column {
        Text(
            stringResource(R.string.matrix_daily_directive),
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "MANDATORY ACTION: ${directive.title}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    directive.objective,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                if (day == 2) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DirectiveCheckItem("No phone in bed")
                        DirectiveCheckItem("No isolated scrolling")
                        DirectiveCheckItem("Danger Zones identified")
                    }
                }

                if (day == 3) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DirectiveCheckItem("Interrupt within 10 seconds")
                        DirectiveCheckItem("Move immediately upon awareness")
                        DirectiveCheckItem("No negotiation with small thoughts")
                    }
                }

                if (day == 4) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DirectiveCheckItem("Label fantasy immediately")
                        DirectiveCheckItem("No intentional replaying")
                        DirectiveCheckItem("Physical reset upon mental loop")
                    }
                }

                if (day == 5) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DirectiveCheckItem("No passive scrolling")
                        DirectiveCheckItem("No lying down with device")
                        DirectiveCheckItem("Catch 3 autopilot behaviors")
                    }
                }

                if (day == 6) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DirectiveCheckItem("Observe urges without panic")
                        DirectiveCheckItem("90-second conscious pause")
                        DirectiveCheckItem("3 urge observations completed")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = if (listOf(1, 2, 3, 4, 5, 6, 7, 10, 15, 21, 30).contains(day)) onStartDiagnostic else onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = if (listOf(1, 2, 3, 4, 5, 6, 7, 10, 15, 21, 30).contains(day)) Color.Red else Color.White),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        if (listOf(1, 2, 3, 4, 5, 6, 7, 10, 15, 21, 30).contains(day)) "START DIAGNOSTIC" else "MARK COMPLETE",
                        color = if (listOf(1, 2, 3, 4, 5, 6, 7, 10, 15, 21, 30).contains(day)) Color.White else Color.Black, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun UrgeInterventionFlow(
    currentDay: Int,
    lastFutureSelf: String? = null,
    onComplete: () -> Unit
) {
    var step by remember { mutableStateOf(if (currentDay <= 7) 0 else 1) }
    var countdown = 30
    if (currentDay >= 21) {
        countdown = 90
    } else if (currentDay >= 15) {
        countdown = 60
    }
    var countdownState by remember { mutableIntStateOf(countdown) }
    var responseTimer by remember { mutableIntStateOf(if (currentDay >= 3 && currentDay < 8) 10 else if (currentDay >= 8) 15 else 30) }
    var isTimerActive by remember { mutableStateOf(false) }
    var isResponseTimerActive by remember { mutableStateOf(currentDay >= 3) }
    var lockoutTimer by remember { mutableIntStateOf(10) }

    // Day 3 Sensor-based Verification simulation
    var stepsCount by remember { mutableIntStateOf(0) }
    val requiredSteps = 10
    var isVerifyingSteps by remember { mutableStateOf(false) }

    LaunchedEffect(isVerifyingSteps) {
        if (isVerifyingSteps) {
            while (stepsCount < requiredSteps) {
                delay(400) // Simulate walking
                stepsCount++
            }
            isVerifyingSteps = false
        }
    }

    val totalCountdown = countdown.toFloat()

    LaunchedEffect(step) {
        if (step == 0) {
            while (lockoutTimer > 0) {
                delay(1000)
                lockoutTimer--
            }
            step = 1
        }
    }

    LaunchedEffect(isTimerActive, countdownState) {
        if (isTimerActive && countdownState > 0) {
            delay(1000)
            countdownState--
        } else if (isTimerActive && countdownState == 0) {
            isTimerActive = false
        }
    }

    LaunchedEffect(isResponseTimerActive, responseTimer) {
        if (isResponseTimerActive && responseTimer > 0) {
            delay(1000)
            responseTimer--
        } else if (isResponseTimerActive && responseTimer == 0) {
            isResponseTimerActive = false
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
        when (step) {
            0 -> {
                Text("SYSTEM LOCKOUT", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 32.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("WAIT $lockoutTimer SECONDS", color = Color.White, fontSize = 24.sp)
                Text("DO NOT TOUCH YOUR DEVICE", color = Color.Gray)
            }
            1 -> {
                if (currentDay >= 3) {
                    Text("RESPONSE TIMER: $responseTimer", color = if (responseTimer < 5) Color.Red else Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                UrgeStep(
                    title = "RECOGNIZE THE LOOP",
                    desc = "You are currently being stimulated by a neural loop. This is not you. This is a system reaction.",
                    icon = Icons.Default.Visibility,
                    buttonText = "I SEE THE LOOP",
                    enabled = responseTimer > 0 || currentDay < 3,
                    onNext = { 
                        step = 2 
                        isResponseTimerActive = false
                    }
                )
            }
            2 -> {
                UrgeStep(
                    title = "PHYSICAL DISCONNECT",
                    desc = "Stand up. Move away from your current location. Place the device on a flat surface.",
                    icon = Icons.Default.DirectionsRun,
                    buttonText = "MOVING NOW",
                    onNext = { 
                        if (currentDay >= 3) {
                            isVerifyingSteps = true
                            step = 3
                        } else {
                            step = 4
                        }
                    }
                )
            }
            3 -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("VERIFYING MOVEMENT", color = Color.Red, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(
                        progress = { stepsCount.toFloat() / requiredSteps },
                        modifier = Modifier.size(120.dp),
                        color = Color.Red,
                        trackColor = Color.DarkGray,
                        strokeWidth = 8.dp,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("$stepsCount / $requiredSteps STEPS", color = Color.White)
                    if (stepsCount >= requiredSteps) {
                        Button(onClick = { step = 4 }, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                            Text("MOVEMENT CONFIRMED", color = Color.Black)
                        }
                    }
                }
            }
            4 -> {
                Text("STABILIZATION PHASE", color = Color.Red, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(24.dp))
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { countdownState / totalCountdown },
                        modifier = Modifier.size(200.dp),
                        color = Color.Red,
                        trackColor = Color.DarkGray,
                        strokeWidth = 12.dp,
                    )
                    Text("$countdownState", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(32.dp))
                if (!isTimerActive && countdownState > 0) {
                    Button(onClick = { isTimerActive = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                        Text("START BREATHING RESET")
                    }
                } else if (countdownState == 0) {
                    Button(onClick = { step = 5 }, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                        Text("STABILIZED", color = Color.Black)
                    }
                }
            }
            5 -> {
                if (lastFutureSelf != null) {
                    Text("MESSAGE FROM YOUR FUTURE SELF", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("\"$lastFutureSelf\"", color = Color.White, textAlign = TextAlign.Center, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(32.dp))
                }
                UrgeStep(
                    title = "PROTOCOL COMPLETE",
                    desc = "The urge has been observed and interrupted. You have regained control of the system.",
                    icon = Icons.Default.Verified,
                    buttonText = "EXIT EMERGENCY MODE",
                    onNext = onComplete
                )
            }
        }
    }
}

@Composable
fun RelapseAnalysisFlow(
    onComplete: (String, String, String, String, String, String, String) -> Unit,
    onCancel: () -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var trigger by remember { mutableStateOf("") }
    var environment by remember { mutableStateOf("") }
    var emotion by remember { mutableStateOf("") }
    var escalation by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var lesson by remember { mutableStateOf("") }
    var futureSelf by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("RELAPSE AUTOPSY", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
        Text("EXTRACTING DATA FROM FAILURE", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(32.dp))

        when (step) {
            1 -> RelapseInputStep("THE TRIGGER", "What was the very first thought or image?", trigger, { trigger = it }, "e.g., Boredom, Specific App, Stress", "NEXT") { step = 2 }
            2 -> RelapseInputStep("THE ENVIRONMENT", "Where were you? What was the physical setup?", environment, { environment = it }, "e.g., Bed, Bathroom, Alone at desk", "NEXT") { step = 3 }
            3 -> RelapseInputStep("THE EMOTION", "What were you feeling right before?", emotion, { emotion = it }, "e.g., Lonely, Tired, Anxious", "NEXT") { step = 4 }
            4 -> RelapseInputStep("THE ESCALATION", "How did it progress from thought to action?", escalation, { escalation = it }, "e.g., Just checking -> Scrolling -> Trance", "NEXT") { step = 5 }
            5 -> RelapseInputStep("THE COST", "What has this cost you right now?", cost, { cost = it }, "e.g., Energy, Clarity, Self-trust", "NEXT") { step = 6 }
            6 -> RelapseInputStep("THE LESSON", "What will you change in your environment?", lesson, { lesson = it }, "e.g., Phone leaves room at 9pm", "NEXT") { step = 7 }
            7 -> RelapseInputStep("FUTURE SELF", "Write a message to yourself for the next urge.", futureSelf, { futureSelf = it }, "Why is this not worth it?", "LOG FAILURE") { 
                onComplete(trigger, environment, emotion, cost, lesson, futureSelf, escalation)
            }
        }
        
        if (step < 7) {
            TextButton(onClick = onCancel, modifier = Modifier.padding(top = 16.dp)) {
                Text("CANCEL", color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun RelapseInputStep(
    title: String,
    desc: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    buttonText: String,
    onNext: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(desc, color = Color.Gray, textAlign = TextAlign.Center, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.DarkGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.DarkGray
            )
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = value.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(buttonText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EmergencyNightMode(onOverride: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Red, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("EMERGENCY NIGHT MODE", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "High-risk hours detected (22:00 - 05:00). Your willpower is lowest now. The machine is strongest.",
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onOverride,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("I AM CONSCIOUS / OVERRIDE", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FinalMatrixReport(metrics: MatrixMetrics, onComplete: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black).padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("FINAL SYSTEM REPORT", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        ReportMetricRow("PROTOCOL DURATION", "30 DAYS")
        ReportMetricRow("URGES INTERRUPTED", "${metrics.awarenessScore}")
        ReportMetricRow("URGE CONTROL", "${metrics.controlScore}%")
        ReportMetricRow("SYSTEM RESILIENCE", metrics.resilienceGrowth)
        ReportMetricRow("INTERRUPTION SPEED", metrics.interruptionSpeed)
        
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            "You are now operating outside the standard loop. Maintenance mode will remain active to ensure stability.",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onComplete, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
            Text("ENTER MAINTENANCE MODE", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ReportMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LoopMapSection(map: LoopMap) {
    Column {
        Text(
            "NEURAL LOOP MAP",
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
            border = BorderStroke(1.dp, Color.DarkGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                map.nodes.forEachIndexed { index, node ->
                    LoopNodeItem(
                        node = node,
                        isLast = index == map.nodes.size - 1,
                        isInterventionPoint = node.stage == LoopStage.MENTAL_SHIFT || node.stage == LoopStage.ESCALATION
                    )
                }
            }
        }
    }
}

@Composable
fun LoopNodeItem(node: LoopNode, isLast: Boolean, isInterventionPoint: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isInterventionPoint) Color(0xFF221111) else Color.Black
            ),
            border = BorderStroke(1.dp, if (isInterventionPoint) Color.Red else Color.DarkGray)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (node.stage) {
                        LoopStage.TRIGGER -> Icons.Default.Bolt
                        LoopStage.MENTAL_SHIFT -> Icons.Default.Psychology
                        LoopStage.ESCALATION -> Icons.Default.TrendingUp
                        LoopStage.BEHAVIOR -> Icons.Default.Error
                        LoopStage.AFTERMATH -> Icons.Default.History
                    },
                    contentDescription = null,
                    tint = if (isInterventionPoint) Color.Red else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        node.stage.name.replace("_", " "),
                        color = if (isInterventionPoint) Color.Red else Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        node.content,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        if (!isLast) {
            if (isInterventionPoint) {
                Text(
                    "⚠ INTERVENTION OPPORTUNITY",
                    color = Color.Red,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DirectiveCheckItem(text: String) {
    var checked by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { checked = !checked }
    ) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (checked) Color.Green else Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = if (checked) Color.White else Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun UrgeStep(
    title: String, 
    desc: String, 
    icon: ImageVector, 
    buttonText: String = "DONE",
    enabled: Boolean = true,
    onNext: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color.Red, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text(desc, color = Color.LightGray, textAlign = TextAlign.Center, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(4.dp),
            enabled = enabled
        ) {
            Text(buttonText, color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
