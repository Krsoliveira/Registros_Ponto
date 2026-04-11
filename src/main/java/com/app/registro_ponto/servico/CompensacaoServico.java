package com.app.registro_ponto.servico;

import com.app.registro_ponto.dto.CompensacaoRequestDTO;
import com.app.registro_ponto.modelo.Compensacao;
import com.app.registro_ponto.modelo.Funcionario;
import com.app.registro_ponto.repositorio.CompensacaoRepositorio;
import com.app.registro_ponto.repositorio.FuncionarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CompensacaoServico {

    private final CompensacaoRepositorio compensacaoRepositorio;
    private final FuncionarioRepositorio funcionarioRepositorio;

    public CompensacaoServico(CompensacaoRepositorio compensacaoRepositorio,
                               FuncionarioRepositorio funcionarioRepositorio) {
        this.compensacaoRepositorio = compensacaoRepositorio;
        this.funcionarioRepositorio = funcionarioRepositorio;
    }

    @Transactional
    public Compensacao registrar(String matricula, CompensacaoRequestDTO req) {
        Funcionario f = funcionarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        if (req.getMotivo() == null || req.getMotivo().isBlank()) {
            throw new IllegalArgumentException("Motivo é obrigatório.");
        }
        if (req.getHorasCompensadas() == null || (req.getHorasCompensadas() != 1 && req.getHorasCompensadas() != 2)) {
            throw new IllegalArgumentException("Horas compensadas deve ser 1 ou 2.");
        }

        Compensacao.TipoCompensacao tipo;
        try {
            tipo = Compensacao.TipoCompensacao.valueOf(req.getTipo());
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de compensação inválido. Use CHEGAR_TARDE ou SAIR_CEDO.");
        }

        LocalDate data;
        try {
            data = LocalDate.parse(req.getDataCompensacao(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            try {
                data = LocalDate.parse(req.getDataCompensacao());
            } catch (Exception e2) {
                throw new IllegalArgumentException("Data inválida. Use dd/MM/yyyy.");
            }
        }

        Compensacao c = new Compensacao();
        c.setFuncionario(f);
        c.setMotivo(req.getMotivo());
        c.setHorasCompensadas(req.getHorasCompensadas());
        c.setTipo(tipo);
        c.setDataCompensacao(data);
        return compensacaoRepositorio.save(c);
    }
}
