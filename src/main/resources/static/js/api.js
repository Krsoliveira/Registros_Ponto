// ============================================================
// API Helper — gerencia token JWT automaticamente
// ============================================================

const API = {
    base: '/api',

    getToken() { return localStorage.getItem('token'); },
    getUser()  { return JSON.parse(localStorage.getItem('user') || 'null'); },

    saveSession(data) {
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify({
            login:          data.login,
            perfil:         data.perfil,
            nomeFuncionario: data.nomeFuncionario || data.login,
            matricula:      data.matricula || null,
            funcionarioId:  data.funcionarioId || null
        }));
    },

    clearSession() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    },

    async request(method, path, body = null) {
        const headers = { 'Content-Type': 'application/json' };
        const token = this.getToken();
        if (token) headers['Authorization'] = `Bearer ${token}`;

        const options = { method, headers };
        if (body) options.body = JSON.stringify(body);

        const response = await fetch(this.base + path, options);

        if (response.status === 401) {
            this.clearSession();
            window.location.href = '/index.html';
            throw new Error('Sessão expirada.');
        }
        return response;
    },

    get(path)        { return this.request('GET', path); },
    post(path, body) { return this.request('POST', path, body); },
};