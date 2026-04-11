// ============================================================
// Página de Login
// ============================================================

// Se já está autenticado, redireciona direto
(function () {
    if (localStorage.getItem('token') && localStorage.getItem('user')) {
        window.location.href = '/dashboard.html';
    }
})();

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const login = document.getElementById('login').value.trim();
    const senha = document.getElementById('senha').value;
    const errorDiv = document.getElementById('loginError');
    const btn = document.getElementById('loginBtn');

    errorDiv.style.display = 'none';
    btn.textContent = 'Entrando...';
    btn.disabled = true;

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ login, senha })
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify({
                login:           data.login,
                perfil:          data.perfil,
                nomeFuncionario: data.nomeFuncionario || data.login,
                matricula:       data.matricula || null,
                funcionarioId:   data.funcionarioId || null
            }));
            window.location.href = '/dashboard.html';
        } else {
            const msg = await response.text();
            errorDiv.textContent = msg || 'Login ou senha incorretos.';
            errorDiv.style.display = 'block';
        }
    } catch (err) {
        errorDiv.textContent = 'Erro de conexão. Verifique o servidor.';
        errorDiv.style.display = 'block';
    } finally {
        btn.textContent = 'Entrar';
        btn.disabled = false;
    }
});