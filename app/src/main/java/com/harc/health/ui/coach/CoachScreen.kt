package com.harc.health.ui.coach

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpCenter
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harc.health.R
import com.harc.health.viewmodel.MainViewModel

@Composable
fun CoachScreen(viewModel: MainViewModel) {
    var showHelpCenter by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Custom Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.HelpCenter, contentDescription = null, tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (showHelpCenter) stringResource(R.string.coach_help_centre) else stringResource(R.string.coach_expert_help),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (showHelpCenter) stringResource(R.string.coach_clinically_precise) else stringResource(R.string.coach_real_time_support),
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        if (showHelpCenter) {
            HelpCenterContent(onSwitchToCoach = { showHelpCenter = false })
        } else {
            CoachChatContent(viewModel, onBackToHelp = { showHelpCenter = true })
        }
    }
}

@Composable
fun HelpCenterContent(onSwitchToCoach: () -> Unit) {
    val categories = listOf(
        "AFTER DRINKING (HANGOVER & ACUTE EFFECTS)" to listOf(
            "Why do I have a headache and nausea after drinking alcohol?" to "Alcohol is a diuretic and produces acetaldehyde, a toxic byproduct. These cause brain dehydration and stomach lining irritation, leading to the 'sick' feeling.",
            "Why does my heart rate increase after drinking?" to "Alcohol can trigger a surge in adrenaline and stress hormones. It also causes blood vessels to dilate and then constrict, forcing the heart to pump harder.",
            "Why do I experience anxiety the day after drinking alcohol?" to "Often called 'Hangxiety', this is caused by the brain trying to rebalance chemicals after the sedative effects of alcohol wear off, leading to over-excitation.",
            "Why do I feel fatigue even after sleeping after alcohol use?" to "Alcohol prevents you from entering deep REM sleep. Even if you sleep 8 hours, your brain hasn't properly rested or cleared toxins.",
            "How long do hangover symptoms typically last?" to "Symptoms usually peak as blood alcohol levels return to zero and can last up to 24 hours depending on the quantity consumed and individual metabolic rates.",
            "What is the most effective way to recover from a hangover?" to "A combination of aggressive rehydration (water + electrolytes), B-complex vitamins, and low-intensity rest to allow the liver to process acetaldehyde.",
            "Does caffeine worsen hangover symptoms?" to "Yes. Caffeine is a diuretic that increases dehydration and can further elevate an already stressed heart rate.",
            "Is it safe to consume alcohol again while experiencing a hangover?" to "No. 'Hair of the dog' only delays the metabolic crash and places extreme, compounding stress on your liver and cardiovascular system.",
            "Why do I feel dehydrated after drinking alcohol?" to "Alcohol suppresses the antidiuretic hormone (ADH), which signals your kidneys to keep water. This causes the body to expel up to 4x the amount of fluid consumed.",
            "What are signs that a hangover may require medical attention?" to "Confusion, seizures, slow/irregular breathing, low body temperature (blue-tinged skin), or persistent vomiting."
        ),
        "🚬 SMOKING & NICOTINE DEPENDENCE" to listOf(
            "Why do I experience strong nicotine cravings at certain times?" to "Nicotine creates a dopamine loop. Cravings are often triggered by ritual habits (coffee, stress, meals) where your brain expects that dopamine spike.",
            "How long does a nicotine craving typically last?" to "The peak intensity of a physical craving usually lasts only 3 to 5 minutes. If you can distract yourself for this window, the urge will subside.",
            "What techniques help reduce cigarette cravings quickly?" to "Deep breathing is clinically proven. It mimics the deep inhale of smoking but delivers pure oxygen, which calms the nervous system instantly.",
            "Why do I feel irritable or restless when I don’t smoke?" to "This is nicotine withdrawal. Your brain has adapted to having a stimulant and reacts with stress signals when the nicotine level drops.",
            "How does smoking affect my lung function over time?" to "Tar and carbon monoxide reduce oxygen exchange. Over time, this causes inflammation and reduces the elasticity of the air sacs (alveoli).",
            "Can lung function improve after reducing or stopping smoking?" to "Yes. Within weeks, lung cilia begin to recover and clear mucus better. Within months, inflammation significantly decreases.",
            "Why do I feel a stronger urge to smoke after meals or alcohol?" to "These are 'conditioned cues'. Meals and alcohol trigger the brain's reward system, which has been trained to seek the additional dopamine hit from nicotine.",
            "What are early signs of nicotine dependence increasing?" to "Needing to smoke sooner after waking up, smoking even when ill, or feeling intense anxiety when you are unable to smoke."
        ),
        "😴 SLEEP & RECOVERY" to listOf(
            "Why does alcohol disrupt sleep quality?" to "Alcohol acts as a sedative initially but causes a 'rebound effect' later in the night, fragmented sleep, and suppression of REM cycles.",
            "Why do I wake up feeling unrefreshed after drinking?" to "Your brain was unable to enter deep restorative stages. Additionally, the body was working hard to metabolize toxins instead of repairing tissue.",
            "How does smoking affect sleep patterns?" to "Nicotine is a stimulant that can make falling asleep harder and leads to lighter, less restful sleep as withdrawal begins during the night.",
            "What can I do to improve sleep after alcohol consumption?" to "Drink water, use a dark/cool room, and avoid screens. Try a guided 'Sleep Prep' session to signal the nervous system to calm down."
        ),
        "🧠 ANXIETY, MOOD & MENTAL STATE" to listOf(
            "Why does alcohol increase next-day anxiety?" to "The brain compensates for alcohol's depressant effect by increasing excitatory chemicals. When alcohol leaves, the brain remains in an over-excited, anxious state.",
            "Why do I feel low or depressed after drinking?" to "Alcohol is a central nervous system depressant that temporarily depletes serotonin and dopamine, leading to a 'comedown' effect the next day.",
            "How does nicotine affect stress and mood regulation?" to "Nicotine creates a cycle where you feel 'stressed' because of withdrawal, and smoking only relieves the stress it created in the first place.",
            "What are effective ways to reduce anxiety without alcohol or smoking?" to "Controlled box breathing, physical movement, and grounding techniques (like the 5-4-3-2-1 method) are highly effective.",
            "Why do cravings feel mentally difficult to resist?" to "The brain's executive function (the 'braking' system) is weakened during cravings, while the impulsive reward system becomes hyper-active."
        ),
        "⚠️ BODY SIGNALS & PHYSICAL SYMPTOMS" to listOf(
            "Why is my heart beating faster than normal after alcohol or smoking?" to "Nicotine is a stimulant that increases heart rate, and alcohol's metabolic byproduct (acetaldehyde) also triggers a stress response in the heart.",
            "Why do I experience shortness of breath after smoking?" to "Nicotine causes immediate vascular constriction and airway inflammation, reducing the amount of oxygen that can reach your bloodstream.",
            "What causes dizziness or lightheadedness after drinking?" to "Alcohol-induced dehydration lowers blood pressure, and its effect on the inner ear can disrupt your sense of balance.",
            "Why do I feel physically weak or shaky after alcohol?" to "Alcohol can cause low blood sugar (hypoglycemia) and mineral depletion, leading to muscle weakness and tremors.",
            "What are signs of dehydration after alcohol use?" to "Extreme thirst, dark-colored urine, dry mouth, dizziness, and a 'tight' feeling or headache in the brain."
        ),
        "🧩 BEHAVIOR & CONTROL" to listOf(
            "How can I tell if my alcohol use is becoming excessive?" to "Signs include increased tolerance, neglecting responsibilities, or feeling like you 'need' a drink to function or relax.",
            "How can I recognize if my smoking habit is increasing?" to "Notice if you are smoking more cigarettes per day, smoking more deeply, or feeling more restless between cigarettes.",
            "Why do I continue using alcohol or cigarettes despite negative effects?" to "Addictive substances 'rewire' the brain's priority system, making the substance feel as necessary for survival as food or water.",
            "What are practical steps to reduce alcohol or smoking gradually?" to "Set clear daily limits, use 'delay tactics' (wait 5 mins before acting on a craving), and replace the habit with a recovery activity."
        ),
        "⚡ IMMEDIATE ACTION & RECOVERY" to listOf(
            "What should I do right now to reduce hangover symptoms?" to "Sip 500ml of water with electrolytes slowly. Avoid bright lights and do a 'Heart Rate Calming' session.",
            "What helps reduce nicotine cravings within a few minutes?" to "Try the '3-Minute Craving Control' breathing exercise or the 'Craving Resistance Timer' to break the impulse.",
            "What are the most important recovery steps after drinking?" to "Hydration, restoring B-vitamins, and allowing your liver 24-48 hours of complete rest from all toxins.",
            "How can I stabilize my body after alcohol consumption?" to "Focus on steady breathing to lower your heart rate and consume a light, potassium-rich snack like a banana."
        ),
        "🚨 WARNING & SAFETY" to listOf(
            "When should I seek medical help after drinking alcohol?" to "If you experience severe confusion, persistent vomiting, seizures, or extremely slow or irregular breathing.",
            "What symptoms after drinking may indicate a serious problem?" to "Chest pain, severe abdominal pain, or a complete inability to stay conscious.",
            "When does shortness of breath or heart symptoms require urgent attention?" to "If shortness of breath is accompanied by chest tightness, pain radiating to the arm, or if your heart rate remains dangerously high even at rest."
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        ResearchInsightCard()

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                .clickable { onSwitchToCoach() }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.coach_need_advice), fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.coach_chat_with_expert), style = MaterialTheme.typography.bodySmall)
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.coach_common_questions),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(categories) { (category, questions) ->
                CategorySection(category, questions)
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun ResearchInsightCard() {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Science, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.coach_research_insight), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.coach_research_desc),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun CategorySection(title: String, questions: List<Pair<String, String>>) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        questions.forEach { (question, answer) ->
            ExpandableQuestionItem(question, answer)
        }
    }
}

@Composable
fun ExpandableQuestionItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        color = if (expanded) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.HelpOutline,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (expanded) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun CoachChatContent(viewModel: MainViewModel, onBackToHelp: () -> Unit) {
    var inputText by remember { mutableStateOf("") }
    val initialMessage = stringResource(R.string.coach_hello)
    val messages = remember { mutableStateListOf(
        CoachMessage(initialMessage, false)
    ) }

    Column(modifier = Modifier.fillMaxSize()) {
        TextButton(
            onClick = onBackToHelp,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(stringResource(R.string.coach_back_to_help))
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                CoachBubble(message)
            }
        }

        Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.coach_describe_feeling)) },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            messages.add(CoachMessage(inputText, true))
                            val response = getInternalCoachResponse(inputText)
                            messages.add(CoachMessage(response, false))
                            inputText = ""
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun CoachBubble(message: CoachMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val containerColor = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSecondaryContainer

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = containerColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 0.dp,
                bottomEnd = if (message.isUser) 0.dp else 16.dp
            )
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

data class CoachMessage(val text: String, val isUser: Boolean)

fun getInternalCoachResponse(input: String): String {
    val lower = input.lowercase()
    return when {
        lower.contains("hangover") || lower.contains("sick") -> "It sounds like your body is processing alcohol. Priority #1 is rehydration with electrolytes. Have you had at least 500ml of water in the last hour?"
        lower.contains("craving") || lower.contains("smoke") -> "Cravings usually peak within 3-5 minutes. Let's do a 3-minute Breathing Reset together right now. Focus on slow exhales."
        lower.contains("anxiety") || lower.contains("anxious") -> "Alcohol-induced anxiety (hangxiety) is caused by a chemical rebound in your brain. It's temporary. Try the 'Quick Calm' sound session."
        else -> "I'm here to help. You can ask me about specific symptoms, craving management, or how to follow your Recovery Plan."
    }
}
