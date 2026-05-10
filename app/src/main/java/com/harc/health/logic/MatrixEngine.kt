package com.harc.health.logic

import com.harc.health.model.*

object MatrixEngine {
    val dailyDirectives = listOf(
        MatrixDay(1, "SEE THE LOOP", "Objective: Expose the system. Identify the stages of your behavioral loop and perform your first conscious interruption.", 
            listOf(
                MatrixActivity("m1_1", "The Diagnostic", "Identify your primary triggers and escalation patterns.", MatrixActivityType.ANALYSIS),
                MatrixActivity("m1_2", "Intervention Drill", "Perform a physical reset to prove conscious control.", MatrixActivityType.ACTION)
            ), MatrixPhase.WAKE_UP,
            closingReflection = "Today you saw the loop clearly for the first time.",
            nextDayPreview = "Tomorrow you will confront the physical environment that shapes your behavior.",
            openingBridge = "30 days ago, the loop operated mostly automatically. Today, you recognize it.",
            newThreat = "The physical environment is the silent architect of your behavior."
        ),
        MatrixDay(2, "ENVIRONMENTAL FRICTION", "Objective: Break the isolation loop. No phone in bed. No isolated scrolling. Identify the physical 'Danger Zones' where you lose control.", 
            listOf(MatrixActivity("m2_1", "Danger Zones", "Map your physical triggers.", MatrixActivityType.ACTION)), MatrixPhase.WAKE_UP,
            closingReflection = "Today you identified the physical 'Danger Zones' where you lose control.",
            nextDayPreview = "Tomorrow you will confront the speed of escalation.",
            openingBridge = "Now that you recognize your physical triggers, you must learn to move before the trance begins.",
            newThreat = "Escalation starts earlier than expected. Speed is your only defense."
        ),
        MatrixDay(3, "PHYSICAL DISPLACEMENT", "Objective: Break the trance. Stand up and move 10 steps immediately at the first sign of an urge. The body must move to reset the mind.", 
            listOf(MatrixActivity("m3_1", "Detection Drill", "Act before escalation.", MatrixActivityType.DETECTION)), MatrixPhase.WAKE_UP,
            closingReflection = "Today you learned to break the trance through physical movement.",
            nextDayPreview = "Tomorrow you will look inside at the mental fuel of the loop.",
            openingBridge = "Now that your body knows how to reset, your mind must learn to label the simulation.",
            newThreat = "Fantasy is the beginning of the loop. If you feed the mind, the body cannot resist."
        ),
        MatrixDay(4, "MENTAL AUDIT", "Objective: Identify the mental fuel. Fantasy is the beginning of the loop. Label the thought immediately: 'This is a simulation.'", 
            listOf(MatrixActivity("m4_1", "Mental Audit", "Catch the thought before it becomes an action.", MatrixActivityType.ANALYSIS)), MatrixPhase.WAKE_UP,
            closingReflection = "Today you began to label the simulation before it became an action.",
            nextDayPreview = "Tomorrow you will confront automatic behavior.",
            openingBridge = "Now that you can label fantasy, you must learn to catch yourself when the system enters autopilot.",
            newThreat = "The 10-second window is your only point of control."
        ),
        MatrixDay(5, "AUTOPILOT INTERRUPTION", "Objective: Destroy the 10-second window. You must interrupt the behavioral chain within 10 seconds of awareness.", 
            listOf(MatrixActivity("m5_1", "Pattern Interrupt", "Destroy automatic behavior.", MatrixActivityType.INTERRUPTION)), MatrixPhase.WAKE_UP,
            closingReflection = "Today you began interrupting autopilot behavior within the 10-second window.",
            nextDayPreview = "Tomorrow the system will challenge you to observe the wave.",
            openingBridge = "Now that you recognize autopilot behavior, you must learn to stay conscious during urges.",
            newThreat = "An urge is not a command. It is a wave that can be observed."
        ),
        MatrixDay(6, "URGE TOLERANCE", "Objective: An urge is not a command. Observe the urge wave for 90 seconds without reacting. Discomfort is survivable.", 
            listOf(MatrixActivity("m6_1", "Urge Wave", "Observe urges without automatic reaction.", MatrixActivityType.CHALLENGE)), MatrixPhase.WAKE_UP,
            closingReflection = "Today you learned that an urge is not a command. You survived the wave.",
            nextDayPreview = "Tomorrow the system will reveal your actual loop structure.",
            openingBridge = "Now that urges are becoming more visible, the system will expose your complete loop structure.",
            newThreat = "The Matrix only works when it is hidden. Today it is revealed."
        ),
        MatrixDay(7, "THE MATRIX REVEALED", "Objective: System Review. Your behavior follows a predictable pattern. You are now seeing the system that previously controlled you.",
            listOf(MatrixActivity("m7_1", "Reveal", "Deep awareness of the behavioral system.", MatrixActivityType.LOGGING)), MatrixPhase.WAKE_UP,
            closingReflection = "Today you saw the full matrix of your behavioral system.",
            nextDayPreview = "Tomorrow we enter Phase 2: BREAK THE LOOP.",
            openingBridge = "You have completed the WAKE UP phase. You now see the system. Now you must break it.",
            newThreat = "Knowledge is not enough. You must become aggressive in your interruption."
        ),
        
        MatrixDay(8, "AGGRESSIVE INTERRUPTION", "Objective: Speed is the only defense. The longer you stay passive, the stronger the urge becomes. Stand up and leave the room in <15s.", 
            listOf(MatrixActivity("m8_1", "15-Second Rule", "Stand up and exit your current position within 15 seconds of an urge.", MatrixActivityType.INTERRUPTION)), MatrixPhase.BREAK_THE_LOOP,
            closingReflection = "Today you practiced aggressive interruption to reclaim speed of control.",
            nextDayPreview = "Tomorrow we target the apps that feed your curiosity.",
            openingBridge = "Yesterday you learned to observe urges consciously. Today you will learn to interrupt escalation faster.",
            newThreat = "Fast interruption matters, but hidden triggers continue feeding escalation."
        ),
        MatrixDay(9, "ELIMINATE PASSIVITY", "Objective: Kill curiosity clicks. Relapse starts long before explicit content. Identify the 'innocent' scrolls that lead to the cliff.", 
            listOf(MatrixActivity("m9_1", "Passivity Audit", "Identify the exact apps that feed your curiosity loop.", MatrixActivityType.ANALYSIS)), MatrixPhase.BREAK_THE_LOOP,
            closingReflection = "Today you identified the apps and behaviors that feed your curiosity loop.",
            nextDayPreview = "Tomorrow hesitation and negotiation will be challenged directly.",
            openingBridge = "Now that you see the 'innocent' scrolls, you must confront the voice that negotiates your relapse.",
            newThreat = "The Negotiator is the first stage of relapse. It speaks in 'just once' and 'just checking'."
        ),
        MatrixDay(10, "CONFRONT THE NEGOTIATOR", "Objective: End internal permission. Confront 'just a quick look' thoughts. Negotiation is the sound of the loop winning.", 
            listOf(MatrixActivity("m10_1", "The Final No", "Choose to interrupt immediately instead of debating with the urge.", MatrixActivityType.INTERRUPTION)), MatrixPhase.BREAK_THE_LOOP,
            closingReflection = "Today you learned to stop negotiating with the urge and say the final No.",
            nextDayPreview = "Tomorrow we reclaim your attention from hyper-stimulation.",
            openingBridge = "Negotiation has been weakened. Now we must detox from the hyper-stimulation that feeds it.",
            newThreat = "Your brain has been trained to chase novelty. Silence is now your weapon."
        ),
        MatrixDay(11, "RECLAIM ATTENTION", "Objective: Detox from hyper-stimulation. Your brain has been trained to chase novelty. Practice 15 minutes of single-task focus.", 
            listOf(MatrixActivity("m11_1", "Focus Drill", "Complete one deep-work task without switching screens or tabs.", MatrixActivityType.CHALLENGE)), MatrixPhase.BREAK_THE_LOOP),
        MatrixDay(12, "ISOLATION KILLER", "Objective: Exposure to light. Isolation strengthens compulsive behavior. Spend 0 minutes isolated with your device today.", 
            listOf(MatrixActivity("m12_1", "Open Door Policy", "Do not use your device in private rooms today.", MatrixActivityType.ACTION)), MatrixPhase.BREAK_THE_LOOP),
        MatrixDay(13, "NIGHT PROTOCOL", "Objective: Secure the vulnerable window. No phone in bed. Devices must be physically unreachable 30 mins before sleep.", 
            listOf(MatrixActivity("m13_1", "Zero Dark Thirty", "Execute a strict physical separation from your device at night.", MatrixActivityType.CHALLENGE)), MatrixPhase.BREAK_THE_LOOP),
        MatrixDay(14, "CONDITIONED, NOT BROKEN", "Objective: Reframe the failure. You repeated a loop until it became automatic. You are a biological machine being re-programmed.", 
            listOf(MatrixActivity("m14_1", "Machine Review", "Audit your progress. You are not a bad person; you are a conditioned person.", MatrixActivityType.ANALYSIS)), MatrixPhase.BREAK_THE_LOOP),
        MatrixDay(15, "STAY IN THE GAP", "Objective: Control the space. Between the urge and the action, there is a gap. Your power lives in that gap. Stay there for 2 minutes.", 
            listOf(MatrixActivity("m15_1", "The 120s Gap", "Wait and breathe through the peak without reaching for a device.", MatrixActivityType.CHALLENGE)), MatrixPhase.BREAK_THE_LOOP),
        MatrixDay(16, "ROUTINE DESTRUCTION", "Objective: Break the habit path. Change your desk setup, your seat, and your phone's home screen. Habit lives in familiarity.", 
            listOf(MatrixActivity("m16_1", "Physical Pivot", "Do not follow your standard daily path. Break the geography of the loop.", MatrixActivityType.ACTION)), MatrixPhase.BREAK_THE_LOOP),
        MatrixDay(17, "PHYSIOLOGICAL DISRUPTION", "Objective: Shock the system. Strong urges weaken when the body becomes active. Perform 30 pushups the moment an urge peaks.", 
            listOf(MatrixActivity("m17_1", "System Shock", "Use intense physical movement to bridge the urge peak.", MatrixActivityType.ACTION)), MatrixPhase.BREAK_THE_LOOP),
        MatrixDay(18, "THE ESCALATION LADDER", "Objective: Know your steps. Relapse is never an 'accident.' Identify the exact ladder you climb from boredom to searching.", 
            listOf(MatrixActivity("m18_1", "Ladder Audit", "Map the steps: Trigger -> Curiosity -> Passivity -> Fantasy -> Search.", MatrixActivityType.ANALYSIS)), MatrixPhase.BREAK_THE_LOOP),
        MatrixDay(19, "VOID MANAGEMENT", "Objective: Fill the emptiness. Many urges escape boredom, not sexual need. Schedule 3 productive anchors today.", 
            listOf(MatrixActivity("m19_1", "Anchor Tasks", "Fill high-risk windows with non-negotiable productive work.", MatrixActivityType.ACTION)), MatrixPhase.BREAK_THE_LOOP),
        MatrixDay(20, "DOPAMINE RE-WIRING", "Objective: Earn the reward. Your brain expects instant stimulation. Practice effort-based rewards. No entertainment before effort.", 
            listOf(MatrixActivity("m20_1", "Effort First", "Complete a difficult task before allowing any passive stimulation.", MatrixActivityType.CHALLENGE)), MatrixPhase.BREAK_THE_LOOP),
        
        MatrixDay(21, "CONSCIOUS DOMINANCE", "Objective: Speed of interruption. You react faster now. Interrupt every urge before it reaches 'Fantasy' stage today.", 
            listOf(MatrixActivity("m21_1", "Speed Audit", "Review your average interruption speed. Aim for <5 seconds.", MatrixActivityType.ANALYSIS),
                   MatrixActivity("m21_2", "Zero-Fantasy Challenge", "Shut down every mental simulation immediately upon detection.", MatrixActivityType.CHALLENGE)), MatrixPhase.REBUILD_CONTROL),
        MatrixDay(22, "NON-REACTIVE OBSERVATION", "Objective: Ultimate Stoicism. An urge does not control you unless you automatically obey it. Watch it like a passing storm.", 
            listOf(MatrixActivity("m22_1", "90s Stillness", "Breathe slowly and observe sensations for 90s without moving or looking at a screen.", MatrixActivityType.CHALLENGE)), MatrixPhase.REBUILD_CONTROL),
        MatrixDay(23, "NEUROPLASTICITY DRILLS", "Objective: Repetition is the cure. Control is a muscle. Train the 'Interrupt' response even when no urge is present.", 
            listOf(MatrixActivity("m23_1", "Shadow Boxing", "Practice the physical movement of leaving your device 5 times today.", MatrixActivityType.ACTION)), MatrixPhase.REBUILD_CONTROL),
        MatrixDay(24, "FANTASY LOCKDOWN", "Objective: Starve the flame. Fantasy is the fuel for escalation. If you don't feed the mind, the body cannot act.", 
            listOf(MatrixActivity("m24_1", "Mental Labeling", "Label every fantasy 'Simulation' and pivot to a physical object in the room.", MatrixActivityType.CHALLENGE)), MatrixPhase.REBUILD_CONTROL),
        MatrixDay(25, "BEHAVIORAL FORENSICS", "Objective: Total transparency. You are no longer blind. Analyze your data. Where are your remaining weak points?", 
            listOf(MatrixActivity("m25_1", "Deep Audit", "Identify the one remaining 'safety' behavior you use to feed the loop.", MatrixActivityType.ANALYSIS)), MatrixPhase.REBUILD_CONTROL),
        MatrixDay(26, "LATE-NIGHT MASTERY", "Objective: 100% Consciousness. Nighttime is where the 'Autopilot' is strongest. Stay awake to your choices until the moment you sleep.", 
            listOf(MatrixActivity("m26_1", "Guardian Protocol", "Maintain 100% awareness during the final 2 hours of the day.", MatrixActivityType.CHALLENGE)), MatrixPhase.REBUILD_CONTROL),
        MatrixDay(27, "PRESSURE TESTING", "Objective: Strength in the storm. Stay conscious during the strongest urge of the week. Intensity is just data.", 
            listOf(MatrixActivity("m27_1", "Pressure Drill", "Wait out a strong urge without the 'escape' of passivity or fantasy.", MatrixActivityType.CHALLENGE)), MatrixPhase.REBUILD_CONTROL),
        MatrixDay(28, "THE NEW STRUCTURE", "Objective: Permanent Architecture. Removing the loop is not enough. You must build a life that doesn't require an 'escape'.", 
            listOf(MatrixActivity("m28_1", "Structure Reinforcement", "Lock in your exercise, sleep, and work protocols as permanent laws.", MatrixActivityType.ACTION)), MatrixPhase.REBUILD_CONTROL),
        MatrixDay(29, "THE AWAKENING", "Objective: Compare the version of you from Day 1. You no longer react; you respond. The machine has been mastered.", 
            listOf(MatrixActivity("m29_1", "Metric Review", "Review your drop in escalation depth and increase in interruption speed.", MatrixActivityType.ANALYSIS)), MatrixPhase.REBUILD_CONTROL),
        MatrixDay(30, "OUT OF THE MATRIX", "Objective: Final Transformation. 30 days ago, you were a slave to a loop. Today, you are the architect of your own focus.", 
            listOf(MatrixActivity("m30_1", "Exit Report", "Complete your final reflection. You have stepped out of the behavioral matrix.", MatrixActivityType.LOGGING)), MatrixPhase.REBUILD_CONTROL)
    )

    fun getDirectiveForDay(day: Int): MatrixDay {
        return dailyDirectives.firstOrNull { it.dayNumber == day } ?: dailyDirectives.last()
    }
}
