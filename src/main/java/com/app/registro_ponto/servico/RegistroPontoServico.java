package com.app.registro_ponto.servico;

import com.app.registro_ponto.dto.RegistroPontoDTO;
import com.app.registro_ponto.dto.ResumoDTO;
import com.app.registro_ponto.modelo.Funcionario;
import com.app.registro_ponto.modelo.RegistroPonto;
import com.app.registro_ponto.repositorio.FuncionarioRepositorio;
import com.app.registro_ponto.repositorio.RegistroPontoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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

    @Transactional
    public String baterPonto(String matricula) {
        Funcionario f = funcionarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        Optional<RegistroPonto> aberto = registroPontoRepositorio
                .findFirstByFuncionarioIdAndHoraSaidaIsNullOrderByHoraEntradaDesc(f.getId());

        if (aberto.isPresent()) {
            RegistroPonto r = aberto.get();
            r.setHoraSaida(LocalDateTime.now());
            registroPontoRepositorio.save(r);
            return "Saída registrada para " + f.getNome() + " às " + r.getHoraSaida().format(FMT_HORA);
        } else {
            RegistroPonto r = new RegistroPonto(f, LocalDateTime.now());
            registroPontoRepositorio.save(r);
            return "Entrada registrada para " + f.getNome() + " às " + r.getHoraEntrada().format(FMT_HORA);
        }
    }

    public List<RegistroPontoDTO> listarRegistros(String matricula) {
        Funcionario f = funcionarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));
        return toDTO(registroPontoRepositorio.findByFuncionarioIdOrderByHoraEntradaDesc(f.getId()));
    }

    public ResumoDTO gerarResumo(String matricula, LocalDateTime inicio, LocalDateTime fim) {
        Funcionario f = funcionarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        List<RegistroPonto> registros = registroPontoRepositorio
                .findByFuncionarioIdAndHoraEntradaBetweenOrderByHoraEntradaDesc(f.getId(), inicio, fim);

        double totalHoras = calcularTotalHoras(registros);
        double semanasNoPeriodo = Math.max(1.0, Duration.between(inicio, fim).toDays() / 7.0);
        double horasExtras = Math.max(0, Math.round((totalHoras - f.getCargaHorariaSemanal() * semanasNoPeriodo) * 100.0) / 100.0);

        return new ResumoDTO(f.getNome(), f.getTurno(), f.getCargaHorariaSemanal(),
                totalHoras, horasExtras, toDTO(registros));
    }

    public double calcularTotalHoras(List<RegistroPonto> registros) {
        double total = registros.stream()
                .filter(r -> r.getHoraSaida() != null)
                .mapToLong(r -> Duration.between(r.getHoraEntrada(), r.getHoraSaida()).toMinutes())
                .sum() / 60.0;
        return Math.round(total * 100.0) / 100.0;
    }

    public List<RegistroPontoDTO> toDTO(List<RegistroPonto> registros) {
        return registros.stream().map(r -> {
            double horas = r.getHoraSaida() != null
                    ? Math.round(Duration.between(r.getHoraEntrada(), r.getHoraSaida()).toMinutes() / 60.0 * 100.0) / 100.0
                    : 0.0;
            return new RegistroPontoDTO(
                    r.getId(),
                    r.getHoraEntrada().format(FMT_DATA),
                    r.getHoraEntrada().format(FMT_HORA),
                    r.getHoraSaida() != null ? r.getHoraSaida().format(FMT_HORA) : "Aberto",
                    horas
            );
        }).collect(Collectors.toList());
    }
}