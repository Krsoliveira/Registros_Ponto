package com.app.registro_ponto.controlador;

import com.app.registro_ponto.dto.CargoCadastroDTO;
import com.app.registro_ponto.dto.GrupoCargoOpcaoDTO;
import com.app.registro_ponto.modelo.Cargo;
import com.app.registro_ponto.modelo.GrupoCargo;
import com.app.registro_ponto.servico.CargoServico;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cargos")
public class CargoControlador {

    private final CargoServico cargoServico;

    public CargoControlador(CargoServico cargoServico) {
        this.cargoServico = cargoServico;
    }

    @GetMapping("/grupos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GrupoCargoOpcaoDTO>> grupos() {
        return ResponseEntity.ok(cargoServico.listarGrupos());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Cargo>> listar(@RequestParam(required = false) String grupo) {
        if (grupo != null && !grupo.isBlank()) {
            return ResponseEntity.ok(cargoServico.listarPorGrupo(GrupoCargo.fromCodigo(grupo)));
        }
        return ResponseEntity.ok(cargoServico.listarTodos());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cargo> cadastrar(@Valid @RequestBody CargoCadastroDTO dto) {
        return ResponseEntity.ok(cargoServico.cadastrar(dto));
    }
}
