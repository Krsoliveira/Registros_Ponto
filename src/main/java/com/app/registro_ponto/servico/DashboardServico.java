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

    public DashboardServico(FuncionarioRepositorio funcionarioRepositorio,
                             RegistroPontoRepositorio registroPontoRepositorio,
                             RegistroPontoServico registroPontoServico) {
        this.funcionarioRepositorio = funcionarioRepositorio;
        this.registroPontoRepositorio = registroPontoRepositorio;
        this.registroPontoServico = registroPontoServico;
    }

    @Transactional(readOnly = true)
    public DashboardAdminDTO getDashboardAdmin() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioSemana = agora.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        LocalDateTime inicioMes = agora.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime inicio7Dias = agora.minusDays(6).with(LocalTime.MIN);

        List<Funcionario> funcionarios = funcionarioRepositorio.findAll();
        List<RegistroPonto> registrosSemana = registroPontoRepositorio.findAllByPeriodo(inicioSemana, agora);
        List<RegistroPonto> registrosMes = registroPontoRepositorio.findAllByPeriodo(inicioMes, agora);
        List<RegistroPonto> registros7Dias = registroPontoRepositorio.findAllByPeriodo(inicio7Dias, agora);
        long pontoAberto = registroPontoRepositorio.countFuncionariosComPontoAberto();

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

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioSemana = agora.with(DayOfWeek.MONDAY).with(LocalTime.MIN);
        LocalDateTime inicioMes = agora.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        LocalDateTime inicio7Dias = agora.minusDays(6).with(LocalTime.MIN);

        List<RegistroPonto> registrosSemana = registroPontoRepositorio
                .findByFuncionarioIdAndHoraEntradaBetweenOrderByHoraEntradaDesc(f.getId(), inicioSemana, agora);
        List<RegistroPonto> registrosMes = registroPontoRepositorio
                .findByFuncionarioIdAndHoraEntradaBetweenOrderByHoraEntradaDesc(f.getId(), inicioMes, agora);
        List<RegistroPonto> registros7Dias = registroPontoRepositorio
                .findByFuncionarioIdAndHoraEntradaBetweenOrderByHoraEntradaDesc(f.getId(), inicio7Dias, agora);
        boolean pontoAberto = registroPontoRepositorio
                .findFirstByFuncionarioIdAndHoraSaidaIsNullOrderByHoraEntradaDesc(f.getId()).isPresent();

        double horasSemana = registroPontoServico.calcularTotalHoras(registrosSemana);
        double horasMes = registroPontoServico.calcularTotalHoras(registrosMes);
        double horasExtras = Math.max(0, Math.round((horasSemana - f.getCargaHorariaSemanal()) * 100.0) / 100.0);

        DashboardFuncionarioDTO dto = new DashboardFuncionarioDTO();
        dto.setNomeFuncionario(f.getNome());
        dto.setTurno(f.getTurno());
        dto.setComPontoAberto(pontoAberto);
        dto.setHorasSemana(horasSemana);
        dto.setHorasMes(horasMes);
        dto.setHorasExtras(horasExtras);
        dto.setHorasPorDia(calcularHorasPorDia(registros7Dias));
        dto.setHistoricoRecente(registroPontoServico.toDTO(registros7Dias.stream().limit(10).collect(Collectors.toList())));
        return dto;
    }

    private List<DashboardAdminDTO.HorasPorDiaDTO> calcularHorasPorDia(List<RegistroPonto> registros) {
        Map<LocalDate, Double> mapa = new LinkedHashMap<>();
        LocalDate hoje = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            mapa.put(hoje.minusDays(i), 0.0);
        }

        for (RegistroPonto r : registros) {
            if (r.getHoraSaida() != null) {
                LocalDate dia = r.getHoraEntrada().toLocalDate();
                if (mapa.containsKey(dia)) {
                    double horas = Duration.between(r.getHoraEntrada(), r.getHoraSaida()).toMinutes() / 60.0;
                    mapa.merge(dia, horas, Double::sum);
                }
            }
        }

        return mapa.entrySet().stream()
                .map(e -> new DashboardAdminDTO.HorasPorDiaDTO(
                        e.getKey().getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR")),
                        Math.round(e.getValue() * 100.0) / 100.0
                ))
                .collect(Collectors.toList());
    }

    private List<DashboardAdminDTO.FuncionarioResumoDTO> calcularTopHorasExtras(
            List<Funcionario> funcionarios, List<RegistroPonto> registrosSemana) {

        Map<Long, List<RegistroPonto>> porFuncionario = registrosSemana.stream()
                .collect(Collectors.groupingBy(r -> r.getFuncionario().getId()));

        return funcionarios.stream()
                .map(f -> {
                    List<RegistroPonto> regs = porFuncionario.getOrDefault(f.getId(), Collections.emptyList());
                    double total = registroPontoServico.calcularTotalHoras(regs);
                    double extras = Math.max(0, Math.round((total - f.getCargaHorariaSemanal()) * 100.0) / 100.0);

                    DashboardAdminDTO.FuncionarioResumoDTO r = new DashboardAdminDTO.FuncionarioResumoDTO();
                    r.setNome(f.getNome());
                    r.setMatricula(f.getMatricula());
                    r.setTotalHoras(total);
                    r.setHorasExtras(extras);
                    return r;
                })
                .filter(r -> r.getHorasExtras() > 0)
                .sorted(Comparator.comparingDouble(DashboardAdminDTO.FuncionarioResumoDTO::getHorasExtras).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<DashboardAdminDTO.RegistroRecenteDTO> montarUltimosRegistros(List<RegistroPonto> registros) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        List<DashboardAdminDTO.RegistroRecenteDTO> lista = new ArrayList<>();

        registros.stream()
                .sorted(Comparator.comparing(RegistroPonto::getHoraEntrada).reversed())
                .limit(8)
                .forEach(r -> {
                    DashboardAdminDTO.RegistroRecenteDTO entrada = new DashboardAdminDTO.RegistroRecenteDTO();
                    entrada.setNomeFuncionario(r.getFuncionario().getNome());
                    entrada.setMatricula(r.getFuncionario().getMatricula());
                    entrada.setTipo("ENTRADA");
                    entrada.setHorario(r.getHoraEntrada().format(fmt));
                    lista.add(entrada);

                    if (r.getHoraSaida() != null) {
                        DashboardAdminDTO.RegistroRecenteDTO saida = new DashboardAdminDTO.RegistroRecenteDTO();
                        saida.setNomeFuncionario(r.getFuncionario().getNome());
                        saida.setMatricula(r.getFuncionario().getMatricula());
                        saida.setTipo("SAIDA");
                        saida.setHorario(r.getHoraSaida().format(fmt));
                        lista.add(saida);
                    }
                });

        return lista.stream().limit(10).collect(Collectors.toList());
    }
}