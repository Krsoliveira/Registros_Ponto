package com.app.registro_ponto.repositorio;

import com.app.registro_ponto.modelo.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepositorio extends JpaRepository<Funcionario, Long> {
    Optional<Funcionario> findByMatricula(String matricula);
    boolean existsByMatricula(String matricula);

    @Query("SELECT DISTINCT f FROM Funcionario f LEFT JOIN FETCH f.cargo ORDER BY f.nome ASC")
    List<Funcionario> findAllComCargo();
}