package com.app.registro_ponto.repositorio;

import com.app.registro_ponto.modelo.Cargo;
import com.app.registro_ponto.modelo.GrupoCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoRepositorio extends JpaRepository<Cargo, Long> {

    List<Cargo> findAllByOrderByGrupoAscTituloAsc();

    List<Cargo> findAllByGrupoOrderByTituloAsc(GrupoCargo grupo);

    boolean existsByTituloIgnoreCaseAndGrupo(String titulo, GrupoCargo grupo);
}
