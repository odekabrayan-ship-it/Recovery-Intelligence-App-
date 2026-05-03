function handleLogin() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    if (!email || !password) {
        alert("Credentials required for Biological Command access.");
        return;
    }

    // Simulate authentication
    document.getElementById('auth-screen').classList.add('hidden');
    document.getElementById('main-content').classList.remove('hidden');
    document.getElementById('bottom-nav').classList.remove('hidden');

    // Update UI with "name" from email
    const name = email.split('@')[0];
    document.getElementById('user-greeting').innerText = `Hello, ${name.charAt(0).toUpperCase() + name.slice(1)}`;
}

function logout() {
    if(confirm("Terminate secure session?")) {
        location.reload();
    }
}

// Service Worker Registration for PWA
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('sw.js')
            .then(reg => console.log('Service Worker Registered'))
            .catch(err => console.log('Service Worker Failed', err));
    });
}
