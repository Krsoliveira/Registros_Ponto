package com.app.registro_ponto.servico;

import com.app.registro_ponto.dto.DashboardAdminDTO;
import com.app.registro_ponto.dto.DashboardFuncionarioDTO;
import com.app.registro_ponto.dto.RegistroPontoDTO;
import com.app.registro_ponto.modelo.Funcionario;
import com.app.registro_ponto.modelo.RegistroPonto;
import com.app.registro_ponto.repositorio.FuncionarioRepositorio;
import com.app.registro_ponto.repositorio.RegistroPontoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServico {

    private final FuncionarioRepositorio funcionarioRepositorio;
    private final RegistroPontoRepositorio registroPontoRepositorio;
    private final RegistroPontoServico registroPontoServico;
    private final BancoHorasServico bancoHorasServico;

    public DashboardServico(FuncionarioRepositorio funcionarioRepositorio,
                             RegistroPontoRepositorio registroPontoRepositorio,
                             RegistroPontoServico registroPontoServico,
                             BancoHorasServico bancoHorasServico) {
        this.funcionarioRepositorio  = funcionarioRepositorio;
        this.registroPontoRepositorio = registroPontoRepositorio;
        this.registroPontoServico    = registroPontoServico;
        this.bancoHorasServico       = bancoHorasServico;
    }

    @Transactional(readOnly = true)
    public DashboardAdminDTO getDashboardAdmin() {
        LocalDateTime agora       = LocalDateTime.now();
        LocalDateTime inicioSemana = agora.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        LocalDateTime inicioMes   = agora.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime inicio7Dias = agora.minusDays(6).with(LocalTime.MIN);
        LocalDateTime inicioDia   = LocalDate.now().atStartOfDay();

        List<Funcionario>    funcionarios    = funcionarioRepositorio.findAll();
        List<RegistroPonto>  registrosSemana = registroPontoRepositorio.findAllByPeriodo(inicioSemana, agora);
        List<RegistroPonto>  registrosMes    = registroPontoRepositorio.findAllByPeriodo(inicioMes, agora);
        List<RegistroPonto>  registros7Dias  = registroPontoRepositorio.findAllByPeriodo(inicio7Dias, agora);
        long pontoAberto = registroPontoRepositorio.countFuncionariosComPontoAberto(inicioDia, agora);

        DashboardAdminDTO dto = new DashboardAdminDTO();
        dto.setTotalFuncionarios(funcionarios.size());
        dto.setFuncionariosComPontoAberto(pontoAberto);
        dto.setTotalHorasSemana(registroPontoServico.calcularTotalHoras(registrosSemana));
        dto.setTotalHorasMes(registroPontoServico.calcularTotalHoras(registrosMes));
        dto.setHorasPorDia(calcularHorasPorDia(registros7Dias));
        dto.setTopHorasExtras(calcularTopHorasExtras(funcionarios, registrosSemana));
        dto.setUltimosRegistros(montarUltimosRegistros(registros7Dias));
        return dto;
    }

    @Transactional(readOnly = true)
    public DashboardFuncionarioDTO getDashboardFuncionario(String matricula) {
        Funcionario f = funcionarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        LocalDateTime agora       = LocalDateTime.now();
        LocalDateTime inicioSemana = agora.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        LocalDateTime inicioMes   = agora.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime inicio7Dias = agora.minusDays(6).with(LocalTime.MIN);
        LocalDateTime inicioDia   = LocalDate.now().atStartOfDay();

        List<RegistroPonto> semana  = registroPontoRepositorio.findByFuncionarioIdAndHoraMarcacaoBetweenOrderByHoraMarcacaoAsc(f.getId(), inicioSemana, agora);
        List<RegistroPonto> mes     = registroPontoRepositorio.findByFuncionarioIdAndHoraMarcacaoBetweenOrderByHoraMarcacaoAsc(f.getId(), inicioMes, agora);
        List<RegistroPonto> dias7   = registroPontoRepositorio.findByFuncionarioIdAndHoraMarcacaoBetweenOrderByHoraMarcacaoAsc(f.getId(), inicio7Dias, agora);
        List<RegistroPonto> hoje    = registroPontoRepositorio.findByFuncionarioIdAndHoraMarcacaoBetweenOrderByHoraMarcacaoAsc(f.getId(), inicioDia, agora);

        boolean dentroDoTrabalho = !hoje.isEmpty()
                && hoje.get(hoje.size() - 1).getTipoMarcacao().isEntrada();

        double horasSemana = registroPontoServico.calcularTotalHoras(semana);
        double horasMes    = registroPontoServico.calcularTotalHoras(mes);
        double horasExtras = Math.max(0, Math.round((horasSemana - f.getCargaHorariaSemanal()) * 100.0) / 100.0);

        double saldo = bancoHorasServico.getSaldoAtual(matricula);

        List<RegistroPontoDTO> historico = registroPontoServico.toDTO(
                dias7.stream().sorted(Comparator.comparing(RegistroPonto::getHoraMarcacao).reversed())
                        .limit(10).collect(Collectors.toList()));

        DashboardFuncionarioDTO dto = new DashboardFuncionarioDTO();
        dto.setNomeFuncionario(f.getNome());
        dto.setTurno(f.getTurno());
        dto.setDentroDoTrabalho(dentroDoTrabalho);
        dto.setHorasSemana(horasSemana);
        dto.setHorasMes(horasMes);
        dto.setHorasExtras(horasExtras);
        dto.setSaldoBancoHoras(saldo);
        dto.setSaldoFormatado(saldo > 0 ? String.format("+%.1fh", saldo)
                : saldo < 0 ? String.format("%.1fh", saldo) : "0h");
        dto.setHorasPorDia(calcularHorasPorDia(dias7));
        dto.setHistoricoRecente(historico);
        return dto;
    }

    // ── HELPERS ────────────────────────────────────────────────

    private List<DashboardAdminDTO.HorasPorDiaDTO> calcularHorasPorDia(List<RegistroPonto> marcacoes) {
        Map<LocalDate, Double> mapa = new LinkedHashMap<>();
        LocalDate hoje = LocalDate.now();
        for (int i = 6; i >= 0; i--) mapa.put(hoje.minusDays(i), 0.0);

        Map<LocalDate, List<RegistroPonto>> porDia = marcacoes.stream()
                .collect(Collectors.groupingBy(p -> p.getHoraMarcacao().toLocalDate()));

        porDia.forEach((dia, lista) -> {
            if (mapa.containsKey(dia)) mapa.put(dia, registroPontoServico.calcularHorasDia(lista));
        });

        return mapa.entrySet().stream()
                .map(e -> new DashboardAdminDTO.HorasPorDiaDTO(
                        e.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")),
                        Math.round(e.getValue() * 100.0) / 100.0))
                .collect(Collectors.toList());
    }

    private List<DashboardAdminDTO.FuncionarioResumoDTO> calcularTopHorasExtras(
            List<Funcionario> funcionarios, List<RegistroPonto> semana) {
        Map<Long, List<RegistroPonto>> porFunc = semana.stream()
                .collect(Collectors.groupingBy(r -> r.getFuncionario().getId()));

        return funcionarios.stream().map(f -> {
            double total  = registroPontoServico.calcularTotalHoras(porFunc.getOrDefault(f.getId(), Collections.emptyList()));
            double extras = Math.max(0, Math.round((total - f.getCargaHorariaSemanal()) * 100.0) / 100.0);
            DashboardAdminDTO.FuncionarioResumoDTO r = new DashboardAdminDTO.FuncionarioResumoDTO();
            r.setNome(f.getNome()); r.setMatricula(f.getMatricula());
            r.setTotalHoras(total); r.setHorasExtras(extras);
            return r;
        }).filter(r -> r.getHorasExtras() > 0)
          .sorted(Comparator.comparingDouble(DashboardAdminDTO.FuncionarioResumoDTO::getHorasExtras).reversed())
          .limit(5).collect(Collectors.toList());
    }

    private List<DashboardAdminDTO.RegistroRecenteDTO> montarUltimosRegistros(List<RegistroPonto> marcacoes) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        return marcacoes.stream()
                .sorted(Comparator.comparing(RegistroPonto::getHoraMarcacao).reversed())
                .limit(10)
                .map(r -> {
                    DashboardAdminDTO.RegistroRecenteDTO d = new DashboardAdminDTO.RegistroRecenteDTO();
                    d.setNomeFuncionario(r.getFuncionario().getNome());
                    d.setMatricula(r.getFuncionario().getMatricula());
                    d.setTipo(r.getTipoMarcacao().name());
                    d.setTipoLabel(r.getTipoMarcacao().getLabel());
                    d.setHorario(r.getHoraMarcacao().format(fmt));
                    d.setNomeLocal(r.getNomeLocal());
                    return d;
                }).collect(Collectors.toList());
    }
}
