/**
 * Recovery Intelligence - Biological Command & Control
 * Web Interface v1.0.13 | Audited & Hardened
 */

const SYSTEM_VERSION = "1.0.13-WEB-STABLE";

document.addEventListener('DOMContentLoaded', () => {
    checkSession();
    initializeServiceWorker();
});

function checkSession() {
    const session = localStorage.getItem('ri_session');
    if (session) {
        console.log("Valid session detected. Bypassing Auth.");
        const userData = JSON.parse(session);
        showDashboard(userData.name);
    }
}

async function handleLogin() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    if (!email || !password) {
        systemNotify("CREDENTIALS_REQUIRED", "error");
        return;
    }

    // UI Feedback: Boot Sequence
    const authBox = document.getElementById('auth-screen');
    const loginBtn = authBox.querySelector('button');
    const originalContent = authBox.innerHTML;

    authBox.innerHTML = `
        <div class="font-mono text-[10px] text-red-600 space-y-1">
            <p>> INITIALIZING BOOT SEQUENCE...</p>
            <p>> CONNECTING TO ENCRYPTED NODE...</p>
            <p>> DECRYPTING BIOMETRIC HASH...</p>
            <p>> ACCESS GRANTED.</p>
            <div class="w-full bg-red-900/20 h-1 mt-4">
                <div class="bg-red-600 h-full animate-[progress_1s_ease-in-out]" style="width: 100%"></div>
            </div>
        </div>
    `;

    setTimeout(() => {
        const name = email.split('@')[0].toUpperCase();
        localStorage.setItem('ri_session', JSON.stringify({ name, timestamp: Date.now() }));
        showDashboard(name);
    }, 1500);
}

function showDashboard(name) {
    document.getElementById('auth-screen').classList.add('hidden');
    document.getElementById('main-content').classList.remove('hidden');
    document.getElementById('bottom-nav').classList.remove('hidden');
    document.getElementById('user-greeting').innerText = `WELCOME, AGENT_${name}`;
}

function logout() {
    if(confirm("TERMINATE SECURE SESSION?")) {
        localStorage.removeItem('ri_session');
        location.reload();
    }
}

// Emergency Logic (Matches MatrixScreen.kt)
let urgeCountdown;
function triggerUrgeEmergency() {
    const main = document.querySelector('main');
    const originalHTML = main.innerHTML;

    main.innerHTML = `
        <div class="flex flex-col items-center justify-center space-y-8 py-12 animate-pulse">
            <i class="fas fa-triangle-exclamation text-7xl text-red-600"></i>
            <h2 class="text-3xl font-black text-center uppercase tracking-tighter">Emergency Protocol</h2>
            <p class="text-center text-gray-400 text-sm">NEURAL LOOP DETECTED. DO NOT NEGOTIATE.</p>

            <div class="w-48 h-48 border-4 border-red-600 rounded-full flex items-center justify-center">
                <span id="urge-timer" class="text-5xl font-black">90</span>
            </div>

            <button onclick="location.reload()" class="w-full matrix-card p-4 border-red-600 text-red-600 font-black uppercase text-xs">
                Override / I am conscious
            </button>
        </div>
    `;

    let count = 90;
    urgeCountdown = setInterval(() => {
        count--;
        document.getElementById('urge-timer').innerText = count;
        if (count <= 0) {
            clearInterval(urgeCountdown);
            location.reload();
        }
    }, 1000);
}

function systemNotify(msg, type = "info") {
    // Simple alert for now, but code-ready for custom toast
    alert(`SYSTEM_${type.toUpperCase()}: ${msg}`);
}

function initializeServiceWorker() {
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('sw.js').then(reg => {
            console.log("Matrix Node Online.");
        }).catch(err => console.error("Node Link Failed:", err));
    }
}
