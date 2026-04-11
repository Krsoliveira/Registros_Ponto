// ============================================================
// Dashboard Principal
// ============================================================

let chartAdminInstance = null;
let chartFuncInstance  = null;

// ── INIT ─────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    const user = API.getUser();
    if (!user || !API.getToken()) {
        window.location.href = '/index.html';
        return;
    }

    setupUI(user);
    startClock();

    if (user.perfil === 'ADMIN') {
        loadAdminHome();
    } else {
        loadFuncionarioHome();
    }
});

// ── CLOCK ────────────────────────────────────────────────────
function startClock() {
    function tick() {
        const now = new Date();
        const time = now.toLocaleTimeString('pt-BR');
        const date = now.toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });

        document.getElementById('pageClock').textContent = time;
        document.getElementById('pageDate').textContent = date;

        const pontoClock = document.getElementById('pontoClock');
        if (pontoClock) pontoClock.textContent = time;

        const pontoDate = document.getElementById('pontoDate');
        if (pontoDate) pontoDate.textContent = date;
    }
    tick();
    setInterval(tick, 1000);
}

// ── UI SETUP ──────────────────────────────────────────────────
function setupUI(user) {
    const nome = user.nomeFuncionario || user.login;
    document.getElementById('userName').textContent = nome;
    document.getElementById('userRole').textContent = user.perfil === 'ADMIN' ? 'Administrador' : 'Funcionário';
    document.getElementById('userAvatar').textContent = nome.charAt(0).toUpperCase();

    const isAdmin = user.perfil === 'ADMIN';

    document.querySelectorAll('.admin-only').forEach(el => {
        el.style.display = isAdmin ? '' : 'none';
    });
    document.querySelectorAll('.funcionario-only').forEach(el => {
        el.style.display = isAdmin ? 'none' : '';
    });
}

function logout() {
    API.clearSession();
    window.location.href = '/index.html';
}

// ── VIEW ROUTING ─────────────────────────────────────────────
function showView(viewName, linkEl) {
    const user = API.getUser();

    document.querySelectorAll('.view').forEach(v => v.style.display = 'none');
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));

    if (linkEl) linkEl.classList.add('active');

    const titles = {
        home:         'Dashboard',
        ponto:        'Bater Ponto',
        funcionarios: 'Funcionários',
        relatorio:    'Relatório',
    };
    document.getElementById('pageTitle').textContent = titles[viewName] || 'Dashboard';

    if (viewName === 'home') {
        if (user.perfil === 'ADMIN') {
            document.getElementById('view-home-admin').style.display = 'block';
            loadAdminHome();
        } else {
            document.getElementById('view-home-func').style.display = 'block';
            loadFuncionarioHome();
        }
    } else if (viewName === 'ponto') {
        document.getElementById('view-ponto').style.display = 'block';
        loadStatusPonto();
    } else if (viewName === 'funcionarios') {
        document.getElementById('view-funcionarios').style.display = 'block';
        carregarFuncionarios();
    } else if (viewName === 'relatorio') {
        document.getElementById('view-relatorio').style.display = 'block';
    }

    // Fecha sidebar no mobile
    if (window.innerWidth <= 768) {
        document.getElementById('sidebar').classList.remove('open');
    }
}

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('open');
}

// ── ADMIN HOME ───────────────────────────────────────────────
async function loadAdminHome() {
    document.getElementById('view-home-admin').style.display = 'block';
    try {
        const res = await API.get('/dashboard/admin');
        if (!res.ok) return;
        const d = await res.json();

        document.getElementById('a-totalFunc').textContent    = d.totalFuncionarios;
        document.getElementById('a-pontoAberto').textContent  = d.funcionariosComPontoAberto;
        document.getElementById('a-horasSemana').textContent  = d.totalHorasSemana.toFixed(1) + 'h';
        document.getElementById('a-horasMes').textContent     = d.totalHorasMes.toFixed(1) + 'h';

        renderChartAdmin(d.horasPorDia);
        renderTopExtras(d.topHorasExtras);
        renderUltimosRegistros(d.ultimosRegistros);
    } catch (e) { console.error('Erro ao carregar dashboard admin', e); }
}

function renderChartAdmin(horasPorDia) {
    const ctx = document.getElementById('chartAdmin').getContext('2d');
    if (chartAdminInstance) chartAdminInstance.destroy();

    chartAdminInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: horasPorDia.map(h => h.dia),
            datasets: [{
                label: 'Horas',
                data:  horasPorDia.map(h => h.horas),
                backgroundColor: 'rgba(99,102,241,0.6)',
                borderColor:     'rgba(99,102,241,1)',
                borderWidth: 1,
                borderRadius: 6,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' } },
                y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' }, beginAtZero: true }
            }
        }
    });
}

function renderTopExtras(lista) {
    const tbody = document.querySelector('#tableTopExtras tbody');
    const empty = document.getElementById('emptyTopExtras');

    if (!lista || lista.length === 0) {
        tbody.innerHTML = '';
        empty.style.display = 'block';
        return;
    }
    empty.style.display = 'none';
    tbody.innerHTML = lista.map(f => `
        <tr>
            <td><strong>${f.nome}</strong><br><small style="color:var(--text-muted)">${f.matricula}</small></td>
            <td>${f.totalHoras.toFixed(1)}h</td>
            <td style="color:var(--success);font-weight:600">+${f.horasExtras.toFixed(1)}h</td>
        </tr>
    `).join('');
}

function renderUltimosRegistros(lista) {
    const tbody = document.querySelector('#tableUltimosRegistros tbody');
    tbody.innerHTML = (lista || []).map(r => `
        <tr>
            <td>${r.nomeFuncionario}</td>
            <td><small style="color:var(--text-muted)">${r.matricula}</small></td>
            <td><span class="badge ${r.tipo === 'ENTRADA' ? 'badge-in' : 'badge-out'}">${r.tipo === 'ENTRADA' ? '↓ Entrada' : '↑ Saída'}</span></td>
            <td>${r.horario}</td>
        </tr>
    `).join('');
}

// ── FUNCIONARIO HOME ──────────────────────────────────────────
async function loadFuncionarioHome() {
    document.getElementById('view-home-func').style.display = 'block';
    try {
        const res = await API.get('/dashboard/funcionario');
        if (!res.ok) return;
        const d = await res.json();

        document.getElementById('f-horasSemana').textContent = d.horasSemana.toFixed(1) + 'h';
        document.getElementById('f-horasMes').textContent    = d.horasMes.toFixed(1) + 'h';
        document.getElementById('f-horasExtras').textContent = d.horasExtras > 0 ? '+' + d.horasExtras.toFixed(1) + 'h' : '0h';
        document.getElementById('f-turno').textContent       = d.turno;

        renderStatusBanner(d.comPontoAberto, 'statusBanner', 'statusDot', 'statusText');
        renderChartFunc(d.horasPorDia);
        renderHistorico(d.historicoRecente);
    } catch (e) { console.error('Erro ao carregar dashboard funcionário', e); }
}

function renderStatusBanner(comPontoAberto, bannerId, dotId, textId) {
    const dot  = document.getElementById(dotId);
    const text = document.getElementById(textId);
    if (!dot || !text) return;

    if (comPontoAberto) {
        dot.className  = 'status-dot inside';
        text.textContent = 'Você está dentro do trabalho';
    } else {
        dot.className  = 'status-dot outside';
        text.textContent = 'Você está fora do trabalho';
    }
}

function renderChartFunc(horasPorDia) {
    const ctx = document.getElementById('chartFunc').getContext('2d');
    if (chartFuncInstance) chartFuncInstance.destroy();

    chartFuncInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: horasPorDia.map(h => h.dia),
            datasets: [{
                label: 'Horas',
                data:  horasPorDia.map(h => h.horas),
                borderColor:     '#6366f1',
                backgroundColor: 'rgba(99,102,241,0.15)',
                borderWidth: 2,
                pointBackgroundColor: '#6366f1',
                pointRadius: 4,
                fill: true,
                tension: 0.3,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' } },
                y: { grid: { color: 'rgba(255,255,255,0.05)' }, ticks: { color: '#94a3b8' }, beginAtZero: true }
            }
        }
    });
}

function renderHistorico(lista) {
    const tbody = document.querySelector('#tableHistorico tbody');
    tbody.innerHTML = (lista || []).map(r => `
        <tr>
            <td>${r.data}</td>
            <td style="color:var(--success)">${r.horaEntrada}</td>
            <td><span class="${r.horaSaida === 'Aberto' ? 'badge badge-aberto' : ''}">${r.horaSaida}</span></td>
            <td>${r.horasTrabalhadas > 0 ? r.horasTrabalhadas.toFixed(1) + 'h' : '--'}</td>
        </tr>
    `).join('');
}

// ── BATER PONTO ───────────────────────────────────────────────
async function loadStatusPonto() {
    try {
        const res = await API.get('/dashboard/funcionario');
        if (!res.ok) return;
        const d = await res.json();
        renderStatusBanner(d.comPontoAberto, 'statusBannerPonto', 'statusDotPonto', 'statusTextPonto');
    } catch (e) {}
}

async function baterPonto() {
    const btn    = document.getElementById('btnBaterPonto');
    const msgDiv = document.getElementById('pontoMsg');

    btn.disabled = true;
    msgDiv.textContent  = 'Registrando...';
    msgDiv.className    = 'ponto-msg';

    try {
        const res  = await API.post('/ponto/bater');
        const text = await res.text();

        if (res.ok) {
            msgDiv.textContent = text;
            msgDiv.classList.add('success');
            await loadStatusPonto();
        } else {
            msgDiv.textContent = text;
            msgDiv.classList.add('error');
        }
    } catch (e) {
        msgDiv.textContent = 'Erro de conexão.';
        msgDiv.classList.add('error');
    } finally {
        btn.disabled = false;
    }
}

// ── FUNCIONÁRIOS (ADMIN) ──────────────────────────────────────
async function carregarFuncionarios() {
    try {
        const res = await API.get('/funcionarios');
        if (!res.ok) return;
        const lista = await res.json();
        const tbody = document.querySelector('#tableFuncionarios tbody');
        tbody.innerHTML = lista.map(f => `
            <tr>
                <td>${f.matricula}</td>
                <td><strong>${f.nome}</strong></td>
                <td>${f.turno}</td>
                <td>${f.cargaHorariaSemanal}h/sem</td>
            </tr>
        `).join('');
    } catch (e) {}
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('formCadastro');
    if (!form) return;
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const msgDiv = document.getElementById('cadastroMsg');
        const btn    = form.querySelector('button[type=submit]');

        const dto = {
            nome:               document.getElementById('c-nome').value.trim(),
            matricula:          document.getElementById('c-matricula').value.trim(),
            turno:              document.getElementById('c-turno').value.trim(),
            cargaHorariaSemanal: parseInt(document.getElementById('c-carga').value) || 44,
            senha:              document.getElementById('c-senha').value || null,
        };

        btn.disabled = true;
        msgDiv.textContent = 'Cadastrando...';
        msgDiv.className   = 'ponto-msg';

        try {
            const res = await API.post('/funcionarios', dto);
            if (res.ok) {
                msgDiv.textContent = `Funcionário "${dto.nome}" cadastrado com sucesso!`;
                msgDiv.classList.add('success');
                form.reset();
                document.getElementById('c-turno').value = 'Comercial';
                document.getElementById('c-carga').value  = '44';
                carregarFuncionarios();
            } else {
                const text = await res.text();
                msgDiv.textContent = text;
                msgDiv.classList.add('error');
            }
        } catch (err) {
            msgDiv.textContent = 'Erro de conexão.';
            msgDiv.classList.add('error');
        } finally {
            btn.disabled = false;
        }
    });
});

// ── RELATÓRIO ─────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('formRelatorio');
    if (!form) return;
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const user    = API.getUser();
        const filtro  = document.getElementById('r-filtro').value;
        const matInput = document.getElementById('r-matricula');
        const matricula = matInput ? matInput.value.trim() : '';

        // Admins podem consultar qualquer funcionário (ou próprio se vazio)
        // Funcionários só consultam os próprios dados (endpoint usa o token)
        const path = user.perfil === 'ADMIN' && matricula
            ? `/ponto/resumo?filtro=${filtro}`
            : `/ponto/resumo?filtro=${filtro}`;

        try {
            const res  = await API.get(path);
            const data = await res.json();

            document.getElementById('r-nome').textContent   = data.nomeFuncionario;
            document.getElementById('r-info').textContent   = `Turno: ${data.turno} | Carga semanal: ${data.cargaHorariaSemanal}h`;
            document.getElementById('r-total').textContent  = data.totalHoras.toFixed(1) + 'h';
            document.getElementById('r-extras').textContent = data.horasExtras > 0 ? '+' + data.horasExtras.toFixed(1) + 'h' : '0h';

            const tbody = document.querySelector('#tableRelatorio tbody');
            tbody.innerHTML = (data.registros || []).map(r => `
                <tr>
                    <td>${r.data}</td>
                    <td style="color:var(--success)">${r.horaEntrada}</td>
                    <td><span class="${r.horaSaida === 'Aberto' ? 'badge badge-aberto' : ''}">${r.horaSaida}</span></td>
                    <td>${r.horasTrabalhadas > 0 ? r.horasTrabalhadas.toFixed(1) + 'h' : '--'}</td>
                </tr>
            `).join('');

            document.getElementById('relatorioResult').style.display = 'block';
        } catch (err) {
            console.error('Erro no relatório', err);
        }
    });
});