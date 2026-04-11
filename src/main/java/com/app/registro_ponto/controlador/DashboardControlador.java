package com.app.registro_ponto.controlador;

import com.app.registro_ponto.dto.DashboardAdminDTO;
import com.app.registro_ponto.dto.DashboardFuncionarioDTO;
import com.app.registro_ponto.modelo.Usuario;
import com.app.registro_ponto.servico.DashboardServico;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardControlador {

    private final DashboardServico dashboardServico;

    public DashboardControlador(DashboardServico dashboardServico) {
        this.dashboardServico = dashboardServico;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardAdminDTO> dashboardAdmin() {
        return ResponseEntity.ok(dashboardServico.getDashboardAdmin());
    }

    @GetMapping("/funcionario")
    @PreAuthorize("hasRole('FUNCIONARIO')")
    public ResponseEntity<DashboardFuncionarioDTO> dashboardFuncionario(@AuthenticationPrincipal Usuario usuario) {
        String matricula = usuario.getFuncionario() != null
                ? usuario.getFuncionario().getMatricula()
                : usuario.getLogin();
        return ResponseEntity.ok(dashboardServico.getDashboardFuncionario(matricula));
    }
}