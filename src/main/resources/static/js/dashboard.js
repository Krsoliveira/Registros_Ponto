// ============================================================
// Dashboard — Registro de Ponto
// ============================================================

let chartAdminInstance = null;
let chartFuncInstance  = null;
let tipoSelecionado    = null;
let geoCoords          = null;
let relatorioDebounceTimer = null;

// ── INIT ─────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    const user = API.getUser();
    if (!user || !API.getToken()) { window.location.href = '/index.html'; return; }

    setupUI(user);
    startClock();
    setDefaultDate();

    if (user.perfil === 'ADMIN') loadAdminHome();
    else loadFuncionarioHome();
});

// ── CLOCK ────────────────────────────────────────────────────
function startClock() {
    function tick() {
        const now  = new Date();
        const time = now.toLocaleTimeString('pt-BR');
        const date = now.toLocaleDateString('pt-BR', { weekday:'long', day:'numeric', month:'long', year:'numeric' });
        document.getElementById('pageClock').textContent = time;
        document.getElementById('pageDate').textContent  = date;
        const pc = document.getElementById('pontoClock');
        if (pc) pc.textContent = time;
        const pd = document.getElementById('pontoDate');
        if (pd) pd.textContent = date;
    }
    tick(); setInterval(tick, 1000);
}

function setDefaultDate() {
    const dataInput = document.getElementById('c-data');
    if (dataInput) dataInput.value = new Date().toISOString().split('T')[0];
}

// ── UI SETUP ─────────────────────────────────────────────────
function setupUI(user) {
    const nome = user.nomeFuncionario || user.login;
    document.getElementById('userName').textContent   = nome;
    document.getElementById('userRole').textContent   = user.perfil === 'ADMIN' ? 'Administrador' : 'Funcionário';
    document.getElementById('userAvatar').textContent = nome.charAt(0).toUpperCase();
    const isAdmin = user.perfil === 'ADMIN';
    document.querySelectorAll('.admin-only').forEach(el => el.style.display = isAdmin ? '' : 'none');
    document.querySelectorAll('.funcionario-only').forEach(el => el.style.display = isAdmin ? 'none' : '');
}

function logout() { API.clearSession(); window.location.href = '/index.html'; }

function toggleSidebar() {
    document.getElementById('sidebar').classList.toggle('open');
    document.getElementById('sidebarOverlay').classList.toggle('active');
}

// ── VIEW ROUTING ─────────────────────────────────────────────
function showView(viewName, linkEl) {
    const user = API.getUser();
    document.querySelectorAll('.view').forEach(v => v.style.display = 'none');
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    if (linkEl) linkEl.classList.add('active');

    const titles = { home:'Dashboard', ponto:'Bater Ponto', banco:'Banco de Horas', funcionarios:'Funcionários', cargos:'Cargos', relatorio:'Relatório' };
    document.getElementById('pageTitle').textContent = titles[viewName] || 'Dashboard';

    if (viewName === 'home') {
        if (user.perfil === 'ADMIN') { document.getElementById('view-home-admin').style.display = 'block'; loadAdminHome(); }
        else { document.getElementById('view-home-func').style.display = 'block'; loadFuncionarioHome(); }
    } else if (viewName === 'ponto') {
        document.getElementById('view-ponto').style.display = 'block'; carregarTiposPonto();
    } else if (viewName === 'banco') {
        document.getElementById('view-banco').style.display = 'block'; carregarBancoHoras();
    } else if (viewName === 'funcionarios') {
        document.getElementById('view-funcionarios').style.display = 'block';
        preencherSelectCargosFuncionario();
        carregarFuncionarios();
    } else if (viewName === 'cargos') {
        document.getElementById('view-cargos').style.display = 'block';
        preencherGruposNoFormCargo();
        carregarTabelaCargos();
    } else if (viewName === 'relatorio') {
        document.getElementById('view-relatorio').style.display = 'block';
        if (user.perfil !== 'ADMIN') {
            buscarRelatorio();
        } else {
            const m = document.getElementById('r-matricula');
            if (m && m.value.trim()) buscarRelatorio();
        }
    }

    if (window.innerWidth <= 768) toggleSidebar();
}

// ── ADMIN HOME ───────────────────────────────────────────────
async function loadAdminHome() {
    document.getElementById('view-home-admin').style.display = 'block';
    try {
        const res = await API.get('/dashboard/admin');
        if (!res.ok) return;
        const d = await res.json();
        document.getElementById('a-totalFunc').textContent   = d.totalFuncionarios;
        document.getElementById('a-pontoAberto').textContent = d.funcionariosComPontoAberto;
        document.getElementById('a-horasSemana').textContent = d.totalHorasSemana.toFixed(1) + 'h';
        document.getElementById('a-horasMes').textContent    = d.totalHorasMes.toFixed(1) + 'h';
        renderChartAdmin(d.horasPorDia);
        renderTopExtras(d.topHorasExtras);
        renderUltimosRegistros(d.ultimosRegistros);
    } catch (e) { console.error(e); }
}

function renderChartAdmin(dados) {
    const ctx = document.getElementById('chartAdmin').getContext('2d');
    if (chartAdminInstance) chartAdminInstance.destroy();
    chartAdminInstance = new Chart(ctx, {
        type: 'bar',
        data: { labels: dados.map(h => h.dia), datasets: [{ label:'Horas', data: dados.map(h => h.horas),
            backgroundColor:'rgba(99,102,241,0.6)', borderColor:'rgba(99,102,241,1)',
            borderWidth:1, borderRadius:6 }] },
        options: { responsive:true, maintainAspectRatio:false, plugins:{legend:{display:false}},
            scales: { x:{grid:{color:'rgba(255,255,255,0.05)'},ticks:{color:'#94a3b8'}},
                      y:{grid:{color:'rgba(255,255,255,0.05)'},ticks:{color:'#94a3b8'},beginAtZero:true} } }
    });
}

function renderTopExtras(lista) {
    const tbody = document.querySelector('#tableTopExtras tbody');
    const empty = document.getElementById('emptyTopExtras');
    if (!lista || !lista.length) { tbody.innerHTML=''; empty.style.display='block'; return; }
    empty.style.display = 'none';
    tbody.innerHTML = lista.map(f => `
        <tr>
            <td><strong>${f.nome}</strong><br><small style="color:var(--text-muted)">${f.matricula}</small></td>
            <td>${f.totalHoras.toFixed(1)}h</td>
            <td style="color:var(--success);font-weight:600">+${f.horasExtras.toFixed(1)}h</td>
        </tr>`).join('');
}

function renderUltimosRegistros(lista) {
    const tbody = document.querySelector('#tableUltimosRegistros tbody');
    const tipoColors = { ENTRADA:'badge-in', SAIDA_INTERVALO:'badge-out', RETORNO_INTERVALO:'badge-blue',
        SAIDA_INTERMEDIARIA:'badge-out', RETORNO_INTERMEDIARIO:'badge-blue', SAIDA_FINAL:'badge-out' };
    tbody.innerHTML = (lista||[]).map(r => `
        <tr>
            <td><strong>${r.nomeFuncionario}</strong><br><small style="color:var(--text-muted)">${r.matricula}</small></td>
            <td><span class="badge ${tipoColors[r.tipo]||''}">${r.tipoLabel}</span></td>
            <td>${r.horario}</td>
            <td style="color:var(--text-muted);font-size:.8rem">${r.nomeLocal||'--'}</td>
        </tr>`).join('');
}

// ── FUNCIONÁRIO HOME ─────────────────────────────────────────
async function loadFuncionarioHome() {
    document.getElementById('view-home-func').style.display = 'block';
    try {
        const res = await API.get('/dashboard/funcionario');
        if (!res.ok) return;
        const d = await res.json();
        document.getElementById('f-horasSemana').textContent = d.horasSemana.toFixed(1) + 'h';
        document.getElementById('f-horasMes').textContent    = d.horasMes.toFixed(1) + 'h';
        document.getElementById('f-horasExtras').textContent = d.horasExtras > 0 ? '+' + d.horasExtras.toFixed(1) + 'h' : '0h';

        const saldoEl   = document.getElementById('f-saldo');
        const saldoIcon = document.getElementById('saldoIcon');
        saldoEl.textContent  = d.saldoFormatado;
        saldoEl.style.color  = d.saldoBancoHoras >= 0 ? 'var(--success)' : 'var(--danger)';
        saldoIcon.className  = `stat-icon ${d.saldoBancoHoras >= 0 ? 'green' : 'red'}`;

        setStatusBanner(d.dentroDoTrabalho, 'statusBanner', 'statusDot', 'statusText');
        renderChartFunc(d.horasPorDia);
        renderHistorico(d.historicoRecente);
    } catch (e) { console.error(e); }
}

function setStatusBanner(dentro, bannerId, dotId, textId) {
    const dot = document.getElementById(dotId), text = document.getElementById(textId);
    if (!dot || !text) return;
    dot.className   = 'status-dot ' + (dentro ? 'inside' : 'outside');
    text.textContent = dentro ? 'Você está dentro do trabalho' : 'Você está fora do trabalho';
}

function renderChartFunc(dados) {
    const ctx = document.getElementById('chartFunc').getContext('2d');
    if (chartFuncInstance) chartFuncInstance.destroy();
    chartFuncInstance = new Chart(ctx, {
        type: 'line',
        data: { labels: dados.map(h => h.dia), datasets: [{ label:'Horas', data: dados.map(h => h.horas),
            borderColor:'#6366f1', backgroundColor:'rgba(99,102,241,0.15)',
            borderWidth:2, pointBackgroundColor:'#6366f1', pointRadius:4, fill:true, tension:0.3 }] },
        options: { responsive:true, maintainAspectRatio:false, plugins:{legend:{display:false}},
            scales: { x:{grid:{color:'rgba(255,255,255,0.05)'},ticks:{color:'#94a3b8'}},
                      y:{grid:{color:'rgba(255,255,255,0.05)'},ticks:{color:'#94a3b8'},beginAtZero:true} } }
    });
}

function renderHistorico(lista) {
    const tbody = document.querySelector('#tableHistorico tbody');
    const tipoColors = { ENTRADA:'badge-in', SAIDA_INTERVALO:'badge-out', RETORNO_INTERVALO:'badge-blue',
        SAIDA_INTERMEDIARIA:'badge-warn', RETORNO_INTERMEDIARIO:'badge-blue', SAIDA_FINAL:'badge-out' };
    tbody.innerHTML = (lista||[]).map(r => `
        <tr>
            <td><strong>${r.data}</strong> ${r.hora}</td>
            <td><span class="badge ${tipoColors[r.tipoMarcacao]||''}">${r.tipoLabel}</span></td>
            <td style="color:var(--text-muted);font-size:.8rem">${r.nomeLocal||'--'}</td>
        </tr>`).join('');
}

// ── BATER PONTO ──────────────────────────────────────────────
async function carregarTiposPonto() {
    tipoSelecionado = null; geoCoords = null;
    document.getElementById('tiposContainer').style.display = 'block';
    document.getElementById('pontoForm').style.display = 'none';
    document.getElementById('pontoMsg').textContent = '';
    document.getElementById('pontoMsg').className = 'ponto-msg';

    try {
        const res = await API.get('/ponto/tipos-disponiveis');
        if (!res.ok) {
            const msg = await res.text();
            document.getElementById('statusTextPonto').textContent = 'Não disponível';
            document.getElementById('tiposBtns').innerHTML =
                `<div class="empty-state"><i class="bi bi-exclamation-triangle"></i><br>${msg || 'Erro ao carregar marcações.'}</div>`;
            return;
        }
        const d = await res.json();
        setStatusBanner(d.dentroDoTrabalho, 'statusBannerPonto', 'statusDotPonto', 'statusTextPonto');
        renderTiposBtns(d);
    } catch(e) {
        console.error(e);
        document.getElementById('statusTextPonto').textContent = 'Erro de conexão';
        document.getElementById('tiposBtns').innerHTML =
            '<div class="empty-state"><i class="bi bi-wifi-off"></i><br>Erro de conexão com o servidor.</div>';
    }
}

function renderTiposBtns(d) {
    const container = document.getElementById('tiposBtns');
    container.innerHTML = '';

    if (d.turnoEncerrado) {
        container.innerHTML = '<div class="empty-state"><i class="bi bi-check-circle"></i><br>Turno encerrado. Até amanhã!</div>';
        return;
    }
    if (!d.tipos || !d.tipos.length) {
        container.innerHTML = '<div class="empty-state">Nenhuma marcação disponível.</div>';
        return;
    }

    d.tipos.forEach(t => {
        const btn = document.createElement('button');
        btn.className = `btn-tipo btn-tipo-${t.cor}`;
        btn.innerHTML = `<i class="bi ${getTipoIcon(t.tipo)}"></i><span>${t.label}</span>`;
        btn.onclick = () => selecionarTipo(t);
        container.appendChild(btn);
    });
}

function getTipoIcon(tipo) {
    const icons = {
        ENTRADA: 'bi-box-arrow-in-right',
        SAIDA_INTERVALO: 'bi-cup-hot',
        RETORNO_INTERVALO: 'bi-arrow-return-right',
        SAIDA_INTERMEDIARIA: 'bi-person-walking',
        RETORNO_INTERMEDIARIO: 'bi-arrow-return-right',
        SAIDA_FINAL: 'bi-box-arrow-right'
    };
    return icons[tipo] || 'bi-clock';
}

function selecionarTipo(tipoInfo) {
    tipoSelecionado = tipoInfo;
    geoCoords = null;
    document.getElementById('tiposContainer').style.display = 'none';
    document.getElementById('pontoForm').style.display = 'block';
    document.getElementById('btnConfirmar').disabled = true;
    document.getElementById('nomeLocalInput').value = '';

    const badge = document.getElementById('tipoSelecionadoBadge');
    badge.innerHTML = `<i class="bi ${getTipoIcon(tipoInfo.tipo)}"></i> ${tipoInfo.label}`;
    badge.className = `tipo-selecionado tipo-${tipoInfo.cor}`;

    solicitarGeolocalizacao();
}

function cancelarSelecao() {
    tipoSelecionado = null; geoCoords = null;
    document.getElementById('tiposContainer').style.display = 'block';
    document.getElementById('pontoForm').style.display = 'none';
}

function solicitarGeolocalizacao() {
    const geoStatus = document.getElementById('geoStatus');
    geoStatus.innerHTML = '<i class="bi bi-hourglass-split"></i> Obtendo localização...';
    geoStatus.className = 'geo-status geo-loading';

    if (!navigator.geolocation) {
        geoStatus.innerHTML = '<i class="bi bi-exclamation-triangle"></i> Geolocalização não suportada neste navegador.<br>'
            + '<button class="btn-geo-skip" onclick="prosseguirSemGeo()">Prosseguir sem GPS</button>';
        geoStatus.className = 'geo-status geo-error';
        return;
    }

    navigator.geolocation.getCurrentPosition(
        (pos) => {
            geoCoords = { latitude: pos.coords.latitude, longitude: pos.coords.longitude };
            geoStatus.innerHTML = `<i class="bi bi-geo-alt-fill"></i> Localização obtida ✓ (${geoCoords.latitude.toFixed(4)}, ${geoCoords.longitude.toFixed(4)})`;
            geoStatus.className = 'geo-status geo-ok';
            verificarPodeConfirmar();
        },
        () => {
            geoStatus.innerHTML = '<i class="bi bi-geo-alt-fill"></i> Permissão de localização negada.<br>'
                + '<button class="btn-geo-retry" onclick="solicitarGeolocalizacao()"><i class="bi bi-arrow-clockwise"></i> Tentar novamente</button>'
                + '<button class="btn-geo-skip" onclick="prosseguirSemGeo()">Prosseguir sem GPS</button>';
            geoStatus.className = 'geo-status geo-error';
        },
        { timeout: 10000, enableHighAccuracy: true }
    );
}

function prosseguirSemGeo() {
    geoCoords = { latitude: null, longitude: null };
    const geoStatus = document.getElementById('geoStatus');
    geoStatus.innerHTML = '<i class="bi bi-geo-alt"></i> Registro sem localização GPS';
    geoStatus.className = 'geo-status geo-skip';
    verificarPodeConfirmar();
}

function verificarPodeConfirmar() {
    const nomeLocal = document.getElementById('nomeLocalInput').value.trim();
    document.getElementById('btnConfirmar').disabled = !(geoCoords && nomeLocal.length > 0);
}

document.addEventListener('DOMContentLoaded', () => {
    const nomeInput = document.getElementById('nomeLocalInput');
    if (nomeInput) nomeInput.addEventListener('input', verificarPodeConfirmar);
});

async function confirmarPonto() {
    const nomeLocal = document.getElementById('nomeLocalInput').value.trim();
    if (!nomeLocal) { alert('Informe o nome do local.'); return; }
    if (!geoCoords)  { alert('Aguarde a geolocalização.'); return; }

    const btn    = document.getElementById('btnConfirmar');
    const msgDiv = document.getElementById('pontoMsg');
    btn.disabled = true;
    msgDiv.textContent = 'Registrando...';
    msgDiv.className   = 'ponto-msg';

    try {
        const res  = await API.post('/ponto/bater', {
            tipoMarcacao: tipoSelecionado.tipo,
            nomeLocal,
            latitude:  geoCoords.latitude,
            longitude: geoCoords.longitude
        });
        const text = await res.text();
        if (res.ok) {
            msgDiv.textContent = text;
            msgDiv.classList.add('success');
            tipoSelecionado = null; geoCoords = null;
            setTimeout(() => carregarTiposPonto(), 1500);
        } else {
            msgDiv.textContent = text;
            msgDiv.classList.add('error');
            btn.disabled = false;
        }
    } catch(e) {
        msgDiv.textContent = 'Erro de conexão.';
        msgDiv.classList.add('error');
        btn.disabled = false;
    }
}

// ── BANCO DE HORAS ───────────────────────────────────────────
async function carregarBancoHoras() {
    try {
        const res = await API.get('/banco-horas');
        if (!res.ok) return;
        const d = await res.json();

        const saldoEl = document.getElementById('saldoValor');
        saldoEl.textContent = d.saldoFormatado;
        saldoEl.className   = 'saldo-value ' + (d.saldo >= 0 ? 'saldo-positivo' : 'saldo-negativo');

        document.getElementById('saldoTrabalhado').textContent = d.totalTrabalhado.toFixed(1) + 'h';
        document.getElementById('saldoEsperado').textContent   = d.totalEsperado.toFixed(1) + 'h';
        document.getElementById('saldoCompensado').textContent = d.totalCompensado.toFixed(1) + 'h';

        renderCompensacoes(d.compensacoes);
    } catch(e) { console.error(e); }
}

function renderCompensacoes(lista) {
    const tbody = document.querySelector('#tableCompensacoes tbody');
    const empty = document.getElementById('emptyCompensacoes');
    if (!lista || !lista.length) { tbody.innerHTML=''; empty.style.display='block'; return; }
    empty.style.display = 'none';
    tbody.innerHTML = lista.map(c => `
        <tr>
            <td>${c.dataCompensacao}</td>
            <td><span class="badge badge-blue">${c.tipoLabel}</span></td>
            <td>${c.horasCompensadas}h</td>
            <td>${c.motivo}</td>
        </tr>`).join('');
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('formCompensacao');
    if (!form) return;
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const msgDiv = document.getElementById('compMsg');
        const btn    = form.querySelector('button[type=submit]');
        const dataVal = document.getElementById('c-data').value;
        const [ano, mes, dia] = dataVal.split('-');
        const dataFormatada   = `${dia}/${mes}/${ano}`;

        btn.disabled = true;
        msgDiv.textContent = 'Registrando...';
        msgDiv.className   = 'ponto-msg';

        try {
            const res = await API.post('/banco-horas/compensacao', {
                motivo:           document.getElementById('c-motivo').value,
                horasCompensadas: parseInt(document.getElementById('c-horas').value),
                tipo:             document.getElementById('c-tipo').value,
                dataCompensacao:  dataFormatada
            });
            if (res.ok) {
                msgDiv.textContent = 'Compensação registrada!';
                msgDiv.classList.add('success');
                form.reset();
                setDefaultDate();
                carregarBancoHoras();
            } else {
                msgDiv.textContent = await res.text();
                msgDiv.classList.add('error');
            }
        } catch(e) {
            msgDiv.textContent = 'Erro de conexão.';
            msgDiv.classList.add('error');
        } finally { btn.disabled = false; }
    });
});

// ── CARGOS (ADMIN) ───────────────────────────────────────────
function escapeHtml(text) {
    if (text == null) return '';
    const d = document.createElement('div');
    d.textContent = text;
    return d.innerHTML;
}

async function preencherGruposNoFormCargo() {
    const sel = document.getElementById('cargo-grupo');
    if (!sel) return;
    try {
        const res = await API.get('/cargos/grupos');
        if (!res.ok) return;
        const grupos = await res.json();
        sel.innerHTML = grupos.map(g => `<option value="${g.codigo}">${escapeHtml(g.nome)}</option>`).join('');
    } catch (e) { console.error(e); }
}

async function preencherSelectCargosFuncionario() {
    const sel = document.getElementById('c-cargo');
    if (!sel) return;
    const atual = sel.value;
    try {
        const res = await API.get('/cargos');
        if (!res.ok) return;
        const lista = await res.json();
        sel.innerHTML = '<option value="">— Sem cargo —</option>' + lista.map(c =>
            `<option value="${c.id}">${escapeHtml(c.titulo)} (${escapeHtml(c.grupoNome)})</option>`
        ).join('');
        if (atual) sel.value = atual;
    } catch (e) { console.error(e); }
}

async function carregarTabelaCargos() {
    const tbody = document.querySelector('#tableCargos tbody');
    const empty = document.getElementById('emptyCargos');
    if (!tbody) return;
    try {
        const res = await API.get('/cargos');
        if (!res.ok) return;
        const lista = await res.json();
        if (!lista.length) {
            tbody.innerHTML = '';
            if (empty) empty.style.display = 'block';
            return;
        }
        if (empty) empty.style.display = 'none';
        tbody.innerHTML = lista.map(c => `
            <tr>
                <td><strong>${escapeHtml(c.titulo)}</strong></td>
                <td><span class="badge badge-blue">${escapeHtml(c.grupoNome)}</span></td>
            </tr>`).join('');
    } catch (e) { console.error(e); }
}

document.addEventListener('DOMContentLoaded', () => {
    const formCargo = document.getElementById('formCargo');
    if (!formCargo) return;
    formCargo.addEventListener('submit', async (e) => {
        e.preventDefault();
        const msgDiv = document.getElementById('cargoMsg');
        const btn = formCargo.querySelector('button[type=submit]');
        const titulo = document.getElementById('cargo-titulo').value.trim();
        const grupo = document.getElementById('cargo-grupo').value;
        btn.disabled = true;
        msgDiv.textContent = 'Salvando...';
        msgDiv.className = 'ponto-msg';
        try {
            const res = await API.post('/cargos', { titulo, grupo });
            if (res.ok) {
                msgDiv.textContent = 'Cargo cadastrado!';
                msgDiv.classList.add('success');
                document.getElementById('cargo-titulo').value = '';
                carregarTabelaCargos();
            } else {
                msgDiv.textContent = await res.text();
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

// ── FUNCIONÁRIOS (ADMIN) ─────────────────────────────────────
async function carregarFuncionarios() {
    try {
        const res = await API.get('/funcionarios');
        if (!res.ok) return;
        const lista = await res.json();
        document.querySelector('#tableFuncionarios tbody').innerHTML = lista.map(f => `
            <tr>
                <td>${escapeHtml(f.matricula)}</td>
                <td><strong>${escapeHtml(f.nome)}</strong></td>
                <td>${f.cargo ? `${escapeHtml(f.cargo.titulo)} <small style="color:var(--text-muted)">(${escapeHtml(f.cargo.grupoNome)})</small>` : '—'}</td>
                <td>${escapeHtml(f.turno)}</td>
                <td>${f.cargaHorariaSemanal}h/sem</td>
            </tr>`).join('');
    } catch(e) {}
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('formCadastro');
    if (!form) return;
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const msgDiv = document.getElementById('cadastroMsg');
        const btn    = form.querySelector('button[type=submit]');
        btn.disabled = true;
        msgDiv.textContent = 'Cadastrando...';
        msgDiv.className   = 'ponto-msg';
        try {
            const cargoVal = document.getElementById('c-cargo').value;
            const body = {
                nome: document.getElementById('c-nome').value.trim(),
                matricula: document.getElementById('c-matricula').value.trim(),
                turno: document.getElementById('c-turno').value.trim(),
                cargaHorariaSemanal: parseInt(document.getElementById('c-carga').value) || 44,
                senha: document.getElementById('c-senha').value || null
            };
            if (cargoVal) body.cargoId = parseInt(cargoVal, 10);
            const res = await API.post('/funcionarios', body);
            if (res.ok) {
                const f = await res.json();
                msgDiv.textContent = `"${f.nome}" cadastrado com sucesso!`;
                msgDiv.classList.add('success');
                form.reset();
                document.getElementById('c-turno').value = 'Comercial';
                document.getElementById('c-carga').value = '44';
                document.getElementById('c-cargo').value = '';
                carregarFuncionarios();
            } else {
                msgDiv.textContent = await res.text();
                msgDiv.classList.add('error');
            }
        } catch(e) { msgDiv.textContent='Erro.'; msgDiv.classList.add('error'); }
        finally { btn.disabled = false; }
    });
});

// ── RELATÓRIO ────────────────────────────────────────────────
function relatorioEscapeHtml(text) {
    if (text == null) return '';
    const d = document.createElement('div');
    d.textContent = text;
    return d.innerHTML;
}

function limparRelatorioErro() {
    const el = document.getElementById('relatorioMsg');
    if (!el) return;
    el.textContent = '';
    el.className = 'ponto-msg';
    el.style.display = 'none';
}

function mostrarRelatorioErro(msg) {
    const el = document.getElementById('relatorioMsg');
    if (!el) return;
    el.textContent = msg;
    el.className = 'ponto-msg error';
    el.style.display = 'block';
}

function ocultarResultadoRelatorio() {
    const box = document.getElementById('relatorioResult');
    if (box) box.style.display = 'none';
}

function agendarBuscaRelatorio() {
    if (relatorioDebounceTimer) clearTimeout(relatorioDebounceTimer);
    relatorioDebounceTimer = setTimeout(() => buscarRelatorio(), 420);
}

async function buscarRelatorio() {
    const user = API.getUser();
    if (!user) return;

    const filtro = document.getElementById('r-filtro').value;
    let url = `/ponto/resumo?filtro=${encodeURIComponent(filtro)}`;

    if (user.perfil === 'ADMIN') {
        const matInput = document.getElementById('r-matricula');
        const matricula = matInput ? matInput.value.trim() : '';
        if (!matricula) {
            mostrarRelatorioErro('Digite a matrícula do funcionário. O relatório atualiza enquanto você digita.');
            ocultarResultadoRelatorio();
            document.querySelector('#tableRelatorio tbody').innerHTML = '';
            return;
        }
        url += `&matricula=${encodeURIComponent(matricula)}`;
    }

    limparRelatorioErro();

    try {
        const res = await API.get(url);
        const text = await res.text();
        if (!res.ok) {
            mostrarRelatorioErro(text || 'Não foi possível carregar o relatório.');
            ocultarResultadoRelatorio();
            document.querySelector('#tableRelatorio tbody').innerHTML = '';
            return;
        }
        let data;
        try {
            data = JSON.parse(text);
        } catch (e) {
            mostrarRelatorioErro('Resposta inválida do servidor.');
            ocultarResultadoRelatorio();
            return;
        }

        document.getElementById('r-nome').textContent = data.nomeFuncionario;
        document.getElementById('r-info').textContent = `Turno: ${data.turno} | Carga: ${data.cargaHorariaSemanal}h/sem`;
        document.getElementById('r-total').textContent = data.totalHoras.toFixed(1) + 'h';
        document.getElementById('r-extras').textContent = data.horasExtras > 0 ? '+' + data.horasExtras.toFixed(1) + 'h' : '0h';

        const saldoEl = document.getElementById('r-saldo');
        saldoEl.textContent = data.saldoBancoHoras > 0
            ? '+' + data.saldoBancoHoras.toFixed(1) + 'h'
            : data.saldoBancoHoras.toFixed(1) + 'h';
        saldoEl.style.color = data.saldoBancoHoras >= 0 ? 'var(--success)' : 'var(--danger)';

        const tipoColors = { ENTRADA:'badge-in', SAIDA_INTERVALO:'badge-out', RETORNO_INTERVALO:'badge-blue',
            SAIDA_INTERMEDIARIA:'badge-warn', RETORNO_INTERMEDIARIO:'badge-blue', SAIDA_FINAL:'badge-out' };
        document.querySelector('#tableRelatorio tbody').innerHTML = (data.registros || []).map(r => `
            <tr>
                <td>${relatorioEscapeHtml(r.data)}</td>
                <td>${relatorioEscapeHtml(r.hora)}</td>
                <td><span class="badge ${tipoColors[r.tipoMarcacao]||''}">${relatorioEscapeHtml(r.tipoLabel)}</span></td>
                <td style="color:var(--text-muted)">${relatorioEscapeHtml(r.nomeLocal) || '--'}</td>
            </tr>`).join('');

        document.getElementById('relatorioResult').style.display = 'block';
    } catch (e) {
        console.error(e);
        mostrarRelatorioErro('Erro de conexão.');
        ocultarResultadoRelatorio();
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('formRelatorio');
    if (!form) return;
    form.addEventListener('submit', (e) => {
        e.preventDefault();
        if (relatorioDebounceTimer) clearTimeout(relatorioDebounceTimer);
        buscarRelatorio();
    });

    const matInput = document.getElementById('r-matricula');
    if (matInput) {
        matInput.addEventListener('input', () => {
            const u = API.getUser();
            if (!u || u.perfil !== 'ADMIN') return;
            agendarBuscaRelatorio();
        });
    }

    const filtroSel = document.getElementById('r-filtro');
    if (filtroSel) {
        filtroSel.addEventListener('change', () => {
            if (relatorioDebounceTimer) clearTimeout(relatorioDebounceTimer);
            buscarRelatorio();
        });
    }
});
