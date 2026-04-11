package com.app.registro_ponto.repositorio;

import com.app.registro_ponto.modelo.Compensacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompensacaoRepositorio extends JpaRepository<Compensacao, Long> {

    List<Compensacao> findByFuncionarioIdOrderByDataCompensacaoDesc(Long funcionarioId);

    @Query("SELECT COALESCE(SUM(c.horasCompensadas), 0) FROM Compensacao c WHERE c.funcionario.id = :funcionarioId")
    double sumHorasCompensadasByFuncionarioId(Long funcionarioId);
}
