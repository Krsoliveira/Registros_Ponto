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

    Optional<RegistroPonto> findFirstByFuncionarioIdAndHoraSaidaIsNullOrderByHoraEntradaDesc(Long funcionarioId);

    List<RegistroPonto> findByFuncionarioIdOrderByHoraEntradaDesc(Long funcionarioId);

    List<RegistroPonto> findByFuncionarioIdAndHoraEntradaBetweenOrderByHoraEntradaDesc(
            Long funcionarioId, LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT r FROM RegistroPonto r WHERE r.horaSaida IS NULL ORDER BY r.horaEntrada DESC")
    List<RegistroPonto> findAllComPontoAberto();

    @Query("SELECT r FROM RegistroPonto r WHERE r.horaEntrada BETWEEN :inicio AND :fim ORDER BY r.horaEntrada DESC")
    List<RegistroPonto> findAllByPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COUNT(DISTINCT r.funcionario.id) FROM RegistroPonto r WHERE r.horaSaida IS NULL")
    long countFuncionariosComPontoAberto();
}