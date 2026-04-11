package com.app.registro_ponto.controlador;

import com.app.registro_ponto.dto.RegistroPontoDTO;
import com.app.registro_ponto.dto.ResumoDTO;
import com.app.registro_ponto.modelo.Usuario;
import com.app.registro_ponto.servico.RegistroPontoServico;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RestController
@RequestMapping("/api/ponto")
public class RegistroPontoControlador {

    private final RegistroPontoServico registroPontoServico;

    public RegistroPontoControlador(RegistroPontoServico registroPontoServico) {
        this.registroPontoServico = registroPontoServico;
    }

    @PostMapping("/bater")
    public ResponseEntity<String> baterPonto(@AuthenticationPrincipal Usuario usuario) {
        String matricula = resolverMatricula(usuario);
        return ResponseEntity.ok(registroPontoServico.baterPonto(matricula));
    }

    @GetMapping("/registros")
    public ResponseEntity<List<RegistroPontoDTO>> listarRegistros(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(registroPontoServico.listarRegistros(resolverMatricula(usuario)));
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoDTO> resumo(@AuthenticationPrincipal Usuario usuario,
                                             @RequestParam(defaultValue = "week") String filtro) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = filtro.equalsIgnoreCase("month")
                ? fim.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN)
                : fim.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);

        return ResponseEntity.ok(registroPontoServico.gerarResumo(resolverMatricula(usuario), inicio, fim));
    }

    private String resolverMatricula(Usuario usuario) {
        return usuario.getFuncionario() != null
                ? usuario.getFuncionario().getMatricula()
                : usuario.getLogin();
    }
}