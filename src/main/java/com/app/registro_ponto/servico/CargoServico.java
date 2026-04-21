package com.app.registro_ponto.servico;

import com.app.registro_ponto.dto.CargoCadastroDTO;
import com.app.registro_ponto.dto.GrupoCargoOpcaoDTO;
import com.app.registro_ponto.modelo.Cargo;
import com.app.registro_ponto.modelo.GrupoCargo;
import com.app.registro_ponto.repositorio.CargoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class CargoServico {

    private final CargoRepositorio cargoRepositorio;

    public CargoServico(CargoRepositorio cargoRepositorio) {
        this.cargoRepositorio = cargoRepositorio;
    }

    public List<GrupoCargoOpcaoDTO> listarGrupos() {
        return Arrays.stream(GrupoCargo.values())
                .map(g -> new GrupoCargoOpcaoDTO(g.name(), g.getTitulo()))
                .toList();
    }

    public List<Cargo> listarTodos() {
        return cargoRepositorio.findAllByOrderByGrupoAscTituloAsc();
    }

    public List<Cargo> listarPorGrupo(GrupoCargo grupo) {
        return cargoRepositorio.findAllByGrupoOrderByTituloAsc(grupo);
    }

    @Transactional
    public Cargo cadastrar(CargoCadastroDTO dto) {
        String titulo = dto.getTitulo() != null ? dto.getTitulo().trim() : "";
        if (titulo.isEmpty()) {
            throw new IllegalArgumentException("Título do cargo é obrigatório.");
        }
        GrupoCargo grupo = GrupoCargo.fromCodigo(dto.getGrupo());
        if (cargoRepositorio.existsByTituloIgnoreCaseAndGrupo(titulo, grupo)) {
            throw new IllegalArgumentException("Já existe um cargo com este nome neste grupo.");
        }
        return cargoRepositorio.save(new Cargo(titulo, grupo));
    }
}
