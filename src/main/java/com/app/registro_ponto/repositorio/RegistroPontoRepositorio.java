package com.app.registro_ponto.repositorio;

import com.app.registro_ponto.modelo.RegistroPonto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroPontoRepositorio extends JpaRepository<RegistroPonto, Long> {

    List<RegistroPonto> findByFuncionarioIdOrderByHoraMarcacaoDesc(Long funcionarioId);

    List<RegistroPonto> findByFuncionarioIdAndHoraMarcacaoBetweenOrderByHoraMarcacaoAsc(
            Long funcionarioId, LocalDateTime inicio, LocalDateTime fim);

    List<RegistroPonto> findByFuncionarioIdAndHoraMarcacaoBetweenOrderByHoraMarcacaoDesc(
            Long funcionarioId, LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT r FROM RegistroPonto r WHERE r.horaMarcacao BETWEEN :inicio AND :fim ORDER BY r.horaMarcacao DESC")
    List<RegistroPonto> findAllByPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
        SELECT r FROM RegistroPonto r
        WHERE r.funcionario.id = :funcionarioId
        AND r.horaMarcacao = (
            SELECT MAX(r2.horaMarcacao) FROM RegistroPonto r2
            WHERE r2.funcionario.id = :funcionarioId
            AND r2.horaMarcacao BETWEEN :inicio AND :fim
        )
    """)
    Optional<RegistroPonto> findUltimaMarcacaoDoDia(Long funcionarioId, LocalDateTime inicio, LocalDateTime fim);

    @Query("""
        SELECT COUNT(DISTINCT r.funcionario.id) FROM RegistroPonto r
        WHERE r.horaMarcacao BETWEEN :inicio AND :fim
        AND r.tipoMarcacao IN ('ENTRADA', 'RETORNO_INTERVALO', 'RETORNO_INTERMEDIARIO')
        AND r.funcionario.id NOT IN (
            SELECT r2.funcionario.id FROM RegistroPonto r2
            WHERE r2.horaMarcacao BETWEEN :inicio AND :fim
            AND r2.tipoMarcacao = 'SAIDA_FINAL'
        )
    """)
    long countFuncionariosComPontoAberto(LocalDateTime inicio, LocalDateTime fim);
}
