package com.app.registro_ponto.servico;

import com.app.registro_ponto.dto.BaterPontoRequestDTO;
import com.app.registro_ponto.dto.RegistroPontoDTO;
import com.app.registro_ponto.dto.ResumoDTO;
import com.app.registro_ponto.dto.TiposDisponiveisDTO;
import com.app.registro_ponto.modelo.Funcionario;
import com.app.registro_ponto.modelo.RegistroPonto;
import com.app.registro_ponto.modelo.RegistroPonto.TipoMarcacao;
import com.app.registro_ponto.repositorio.FuncionarioRepositorio;
import com.app.registro_ponto.repositorio.RegistroPontoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistroPontoServico {

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final FuncionarioRepositorio funcionarioRepositorio;
    private final RegistroPontoRepositorio registroPontoRepositorio;

    public RegistroPontoServico(FuncionarioRepositorio funcionarioRepositorio,
                                 RegistroPontoRepositorio registroPontoRepositorio) {
        this.funcionarioRepositorio = funcionarioRepositorio;
        this.registroPontoRepositorio = registroPontoRepositorio;
    }

    // ── TIPOS DISPONÍVEIS ──────────────────────────────────────

    @Transactional(readOnly = true)
    public TiposDisponiveisDTO getTiposDisponiveis(String matricula) {
        Funcionario f = buscarFuncionario(matricula);
        List<RegistroPonto> hoje = getMarcacoesHoje(f.getId());

        TiposDisponiveisDTO dto = new TiposDisponiveisDTO();

        if (hoje.isEmpty()) {
            dto.setDentroDoTrabalho(false);
            dto.setTurnoEncerrado(false);
            dto.setTipos(List.of(new TiposDisponiveisDTO.TipoDisponivel(
                    TipoMarcacao.ENTRADA.name(), TipoMarcacao.ENTRADA.getLabel(), "green")));
            return dto;
        }

        TipoMarcacao ultima = hoje.get(hoje.size() - 1).getTipoMarcacao();
        boolean usouIntervalo     = hoje.stream().anyMatch(p -> p.getTipoMarcacao() == TipoMarcacao.SAIDA_INTERVALO);
        boolean usouIntermediaria = hoje.stream().anyMatch(p -> p.getTipoMarcacao() == TipoMarcacao.SAIDA_INTERMEDIARIA);
        boolean encerrado         = hoje.stream().anyMatch(p -> p.getTipoMarcacao() == TipoMarcacao.SAIDA_FINAL);

        dto.setDentroDoTrabalho(ultima.isEntrada());
        dto.setTurnoEncerrado(encerrado);

        if (encerrado) {
            dto.setTipos(List.of());
            return dto;
        }

        List<TiposDisponiveisDTO.TipoDisponivel> tipos = new ArrayList<>();

        if (ultima == TipoMarcacao.SAIDA_INTERVALO) {
            tipos.add(new TiposDisponiveisDTO.TipoDisponivel(
                    TipoMarcacao.RETORNO_INTERVALO.name(), TipoMarcacao.RETORNO_INTERVALO.getLabel(), "blue"));
        } else if (ultima == TipoMarcacao.SAIDA_INTERMEDIARIA) {
            tipos.add(new TiposDisponiveisDTO.TipoDisponivel(
                    TipoMarcacao.RETORNO_INTERMEDIARIO.name(), TipoMarcacao.RETORNO_INTERMEDIARIO.getLabel(), "blue"));
        } else {
            // Está dentro — quais saídas estão disponíveis?
            if (!usouIntervalo) {
                tipos.add(new TiposDisponiveisDTO.TipoDisponivel(
                        TipoMarcacao.SAIDA_INTERVALO.name(), TipoMarcacao.SAIDA_INTERVALO.getLabel(), "orange"));
            }
            if (!usouIntermediaria) {
                tipos.add(new TiposDisponiveisDTO.TipoDisponivel(
                        TipoMarcacao.SAIDA_INTERMEDIARIA.name(), TipoMarcacao.SAIDA_INTERMEDIARIA.getLabel(), "orange"));
            }
            tipos.add(new TiposDisponiveisDTO.TipoDisponivel(
                    TipoMarcacao.SAIDA_FINAL.name(), TipoMarcacao.SAIDA_FINAL.getLabel(), "red"));
        }

        dto.setTipos(tipos);
        return dto;
    }

    // ── BATER PONTO ────────────────────────────────────────────

    @Transactional
    public String baterPonto(String matricula, BaterPontoRequestDTO req) {
        Funcionario f = buscarFuncionario(matricula);
        TiposDisponiveisDTO disponiveis = getTiposDisponiveis(matricula);

        TipoMarcacao tipo;
        try {
            tipo = TipoMarcacao.valueOf(req.getTipoMarcacao());
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de marcação inválido: " + req.getTipoMarcacao());
        }

        boolean tipoPermitido = disponiveis.getTipos().stream()
                .anyMatch(t -> t.getTipo().equals(tipo.name()));
        if (!tipoPermitido) {
            throw new IllegalArgumentException("Marcação '" + tipo.getLabel() + "' não permitida neste momento.");
        }

        RegistroPonto registro = new RegistroPonto(
                f, LocalDateTime.now(), tipo,
                req.getNomeLocal(), req.getLatitude(), req.getLongitude());
        registroPontoRepositorio.save(registro);

        return tipo.getLabel() + " registrada para " + f.getNome()
                + " às " + registro.getHoraMarcacao().format(FMT_HORA)
                + (req.getNomeLocal() != null && !req.getNomeLocal().isBlank()
                   ? " — " + req.getNomeLocal() : "");
    }

    // ── LISTAGEM ───────────────────────────────────────────────

    public List<RegistroPontoDTO> listarRegistros(String matricula) {
        Funcionario f = buscarFuncionario(matricula);
        return toDTO(registroPontoRepositorio.findByFuncionarioIdOrderByHoraMarcacaoDesc(f.getId()));
    }

    // ── RESUMO ─────────────────────────────────────────────────

    public ResumoDTO gerarResumo(String matricula, LocalDateTime inicio, LocalDateTime fim, double saldoBancoHoras) {
        Funcionario f = buscarFuncionario(matricula);
        List<RegistroPonto> registros = registroPontoRepositorio
                .findByFuncionarioIdAndHoraMarcacaoBetweenOrderByHoraMarcacaoDesc(f.getId(), inicio, fim);

        double totalHoras = calcularTotalHoras(registros);
        double semanasNoPeriodo = Math.max(1.0, Duration.between(inicio, fim).toDays() / 7.0);
        double horasExtras = Math.max(0, Math.round((totalHoras - f.getCargaHorariaSemanal() * semanasNoPeriodo) * 100.0) / 100.0);

        return new ResumoDTO(f.getNome(), f.getTurno(), f.getCargaHorariaSemanal(),
                totalHoras, horasExtras, saldoBancoHoras, toDTO(registros));
    }

    // ── CÁLCULO DE HORAS ───────────────────────────────────────

    public double calcularTotalHoras(List<RegistroPonto> marcacoes) {
        Map<LocalDate, List<RegistroPonto>> porDia = marcacoes.stream()
                .collect(Collectors.groupingBy(p -> p.getHoraMarcacao().toLocalDate()));
        double total = porDia.values().stream().mapToDouble(this::calcularHorasDia).sum();
        return Math.round(total * 100.0) / 100.0;
    }

    public double calcularHorasDia(List<RegistroPonto> marcacoesDoDia) {
        marcacoesDoDia.sort(Comparator.comparing(RegistroPonto::getHoraMarcacao));
        double totalMinutos = 0;
        LocalDateTime entryTime = null;

        for (RegistroPonto p : marcacoesDoDia) {
            if (p.getTipoMarcacao().isEntrada()) {
                entryTime = p.getHoraMarcacao();
            } else if (p.getTipoMarcacao().isSaida() && entryTime != null) {
                totalMinutos += Duration.between(entryTime, p.getHoraMarcacao()).toMinutes();
                entryTime = null;
            }
        }
        return Math.round((totalMinutos / 60.0) * 100.0) / 100.0;
    }

    // ── HELPERS ────────────────────────────────────────────────

    private List<RegistroPonto> getMarcacoesHoje(Long funcionarioId) {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDia    = inicioDia.plusDays(1);
        return registroPontoRepositorio
                .findByFuncionarioIdAndHoraMarcacaoBetweenOrderByHoraMarcacaoAsc(funcionarioId, inicioDia, fimDia);
    }

    private Funcionario buscarFuncionario(String matricula) {
        return funcionarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));
    }

    public List<RegistroPontoDTO> toDTO(List<RegistroPonto> lista) {
        return lista.stream().map(r -> new RegistroPontoDTO(
                r.getId(),
                r.getHoraMarcacao().format(FMT_DATA),
                r.getHoraMarcacao().format(FMT_HORA),
                r.getTipoMarcacao().name(),
                r.getTipoMarcacao().getLabel(),
                r.getNomeLocal(),
                r.getLatitude(),
                r.getLongitude()
        )).collect(Collectors.toList());
    }
}
