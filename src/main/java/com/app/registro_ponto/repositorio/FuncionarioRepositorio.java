package com.app.registro_ponto.repositorio;

import com.app.registro_ponto.modelo.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuncionarioRepositorio extends JpaRepository<Funcionario, Long> {
    Optional<Funcionario> findByMatricula(String matricula);
    boolean existsByMatricula(String matricula);
}