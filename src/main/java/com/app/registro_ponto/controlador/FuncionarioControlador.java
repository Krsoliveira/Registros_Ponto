package com.app.registro_ponto.controlador;

import com.app.registro_ponto.dto.FuncionarioDTO;
import com.app.registro_ponto.modelo.Funcionario;
import com.app.registro_ponto.servico.FuncionarioServico;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioControlador {

    private final FuncionarioServico funcionarioServico;

    public FuncionarioControlador(FuncionarioServico funcionarioServico) {
        this.funcionarioServico = funcionarioServico;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Funcionario> cadastrar(@RequestBody FuncionarioDTO dto) {
        return ResponseEntity.ok(funcionarioServico.cadastrar(dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Funcionario>> listarTodos() {
        return ResponseEntity.ok(funcionarioServico.listarTodos());
    }
}