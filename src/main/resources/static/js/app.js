// Clock and Date updates
function updateClock() {
    const now = new Date();
    document.getElementById('clock').textContent = now.toLocaleTimeString('pt-BR');
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('date').textContent = now.toLocaleDateString('pt-BR', options);
}
setInterval(updateClock, 1000);
updateClock();

// Tab switching
function showTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.tab-btn').forEach(el => el.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');
    event.currentTarget.classList.add('active');
}

// Bater Ponto
document.getElementById('clockForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const matricula = document.getElementById('employeeNumber').value;
    const msgDiv = document.getElementById('clockMessage');
    msgDiv.textContent = "Processando...";
    msgDiv.className = "message";

    try {
        const response = await fetch(`/api/records/clock?employeeNumber=${matricula}`, { method: 'POST' });
        const data = await response.text();
        if (response.ok) {
            msgDiv.textContent = data;
            msgDiv.classList.add('success');
            fetchRecords();
        } else {
            msgDiv.textContent = data;
            msgDiv.classList.add('error');
        }
    } catch (error) {
        msgDiv.textContent = "Erro de rede.";
        msgDiv.classList.add('error');
    }
});

// Cadastrar Funcionário
document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('newEmpName').value;
    const empNumber = document.getElementById('newEmpNumber').value;
    const shift = document.getElementById('newEmpShift').value;
    const workload = document.getElementById('newEmpWorkload').value;
    const msgDiv = document.getElementById('registerMessage');
    
    msgDiv.textContent = "Cadastrando...";
    msgDiv.className = "message";

    try {
        const response = await fetch(`/api/employees`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, employeeNumber: empNumber, shift, weeklyWorkload: workload })
        });
        if (response.ok) {
            msgDiv.textContent = "Funcionário cadastrado com sucesso!";
            msgDiv.classList.add('success');
            document.getElementById('registerForm').reset();
        } else {
            const data = await response.text();
            msgDiv.textContent = data;
            msgDiv.classList.add('error');
        }
    } catch (error) {
        msgDiv.textContent = "Erro de rede.";
        msgDiv.classList.add('error');
    }
});

// Fetch Records simple
async function fetchRecords() {
    const matricula = document.getElementById('employeeNumber').value;
    if (!matricula) return;
    const list = document.getElementById('recordsList');
    list.innerHTML = "<li>Carregando...</li>";

    try {
        const response = await fetch(`/api/records?employeeNumber=${matricula}`);
        if (response.ok) {
            const records = await response.json();
            list.innerHTML = "";
            if (records.length === 0) { list.innerHTML = "<li>Nenhum registro.</li>"; return; }
            records.forEach(r => {
                const clockIn = new Date(r.clockInTime).toLocaleTimeString('pt-BR');
                const clockOut = r.clockOutTime ? new Date(r.clockOutTime).toLocaleTimeString('pt-BR') : 'Aberto';
                list.innerHTML += `<li><span>📅 ${new Date(r.clockInTime).toLocaleDateString('pt-BR')}</span> <span style="text-align:right;"><span class="in">↓</span> ${clockIn} <br> <span class="out">↑</span> ${clockOut}</span></li>`;
            });
        }
    } catch (error) { list.innerHTML = "<li>Erro.</li>"; }
}

// Fetch Dashboard Summary
document.getElementById('dashboardForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const matricula = document.getElementById('dashEmpNumber').value;
    const filter = document.getElementById('dashFilter').value;
    const msgDiv = document.getElementById('dashMessage');
    const contentDiv = document.getElementById('dashContent');
    
    msgDiv.textContent = "Buscando...";
    msgDiv.className = "message";
    contentDiv.style.display = 'none';

    try {
        const response = await fetch(`/api/records/summary?employeeNumber=${matricula}&filter=${filter}`);
        if (response.ok) {
            const data = await response.json();
            msgDiv.textContent = "";
            
            document.getElementById('dashName').textContent = data.employeeName;
            document.getElementById('dashInfo').textContent = `Turno: ${data.shift} | Carga Semanal: ${data.weeklyWorkload}h`;
            document.getElementById('dashTotalHours').textContent = data.totalHoursWorked.toFixed(1) + 'h';
            
            const otElem = document.getElementById('dashOvertime');
            otElem.textContent = data.overtime > 0 ? `+${data.overtime.toFixed(1)}h` : '0h';
            if (data.overtime > 0) otElem.style.color = '#4ade80';
            else otElem.style.color = 'var(--text-main)';

            contentDiv.style.display = 'block';
        } else {
            msgDiv.textContent = await response.text();
            msgDiv.classList.add('error');
        }
    } catch (error) {
        msgDiv.textContent = "Erro ao buscar resumo.";
        msgDiv.classList.add('error');
    }
});
