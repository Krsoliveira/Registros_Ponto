package com.app.registro_ponto.servico;

import com.app.registro_ponto.dto.BancoHorasDTO;
import com.app.registro_ponto.modelo.Compensacao;
import com.app.registro_ponto.modelo.Funcionario;
import com.app.registro_ponto.modelo.RegistroPonto;
import com.app.registro_ponto.repositorio.CompensacaoRepositorio;
import com.app.registro_ponto.repositorio.FuncionarioRepositorio;
import com.app.registro_ponto.repositorio.RegistroPontoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BancoHorasServico {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final FuncionarioRepositorio funcionarioRepositorio;
    private final RegistroPontoRepositorio registroPontoRepositorio;
    private final CompensacaoRepositorio compensacaoRepositorio;
    private final RegistroPontoServico registroPontoServico;

    public BancoHorasServico(FuncionarioRepositorio funcionarioRepositorio,
                              RegistroPontoRepositorio registroPontoRepositorio,
                              CompensacaoRepositorio compensacaoRepositorio,
                              RegistroPontoServico registroPontoServico) {
        this.funcionarioRepositorio  = funcionarioRepositorio;
        this.registroPontoRepositorio = registroPontoRepositorio;
        this.compensacaoRepositorio  = compensacaoRepositorio;
        this.registroPontoServico    = registroPontoServico;
    }

    @Transactional(readOnly = true)
    public BancoHorasDTO getBancoHoras(String matricula) {
        Funcionario f = funcionarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        // Calcula desde o início do mês atual
        LocalDateTime inicio = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime fim    = LocalDateTime.now();

        List<RegistroPonto> marcacoes = registroPontoRepositorio
                .findByFuncionarioIdAndHoraMarcacaoBetweenOrderByHoraMarcacaoAsc(f.getId(), inicio, fim);

        double totalTrabalhado = registroPontoServico.calcularTotalHoras(marcacoes);

        int diasUteis = contarDiasUteis(inicio.toLocalDate(), fim.toLocalDate());
        double horasPorDia   = f.getCargaHorariaSemanal() / 5.0;
        double totalEsperado = Math.round(diasUteis * horasPorDia * 100.0) / 100.0;

        double totalCompensado = compensacaoRepositorio.sumHorasCompensadasByFuncionarioId(f.getId());

        double saldo = Math.round((totalTrabalhado - totalEsperado - totalCompensado) * 100.0) / 100.0;

        List<Compensacao> compensacoes = compensacaoRepositorio
                .findByFuncionarioIdOrderByDataCompensacaoDesc(f.getId());

        List<BancoHorasDTO.CompensacaoResponseDTO> compDTOs = compensacoes.stream().map(c -> {
            BancoHorasDTO.CompensacaoResponseDTO dto = new BancoHorasDTO.CompensacaoResponseDTO();
            dto.setId(c.getId());
            dto.setMotivo(c.getMotivo());
            dto.setHorasCompensadas(c.getHorasCompensadas());
            dto.setTipo(c.getTipo().name());
            dto.setTipoLabel(c.getTipo().getLabel());
            dto.setDataCompensacao(c.getDataCompensacao().format(FMT));
            dto.setCriadoEm(c.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            return dto;
        }).collect(Collectors.toList());

        BancoHorasDTO dto = new BancoHorasDTO();
        dto.setSaldo(saldo);
        dto.setSaldoFormatado(formatarSaldo(saldo));
        dto.setTotalTrabalhado(totalTrabalhado);
        dto.setTotalEsperado(totalEsperado);
        dto.setTotalCompensado(totalCompensado);
        dto.setCompensacoes(compDTOs);
        return dto;
    }

    public double getSaldoAtual(String matricula) {
        try {
            return getBancoHoras(matricula).getSaldo();
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int contarDiasUteis(LocalDate inicio, LocalDate fim) {
        int count = 0;
        LocalDate d = inicio;
        while (!d.isAfter(fim)) {
            DayOfWeek dow = d.getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) count++;
            d = d.plusDays(1);
        }
        return count;
    }

    private String formatarSaldo(double saldo) {
        if (saldo > 0) return String.format("+%.1fh", saldo);
        if (saldo < 0) return String.format("%.1fh", saldo);
        return "0h";
    }
}
