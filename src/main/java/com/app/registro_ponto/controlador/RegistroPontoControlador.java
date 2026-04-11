package com.app.registro_ponto.controlador;

import com.app.registro_ponto.dto.BaterPontoRequestDTO;
import com.app.registro_ponto.dto.RegistroPontoDTO;
import com.app.registro_ponto.dto.ResumoDTO;
import com.app.registro_ponto.dto.TiposDisponiveisDTO;
import com.app.registro_ponto.modelo.Usuario;
import com.app.registro_ponto.servico.BancoHorasServico;
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
    private final BancoHorasServico bancoHorasServico;

    public RegistroPontoControlador(RegistroPontoServico registroPontoServico,
                                     BancoHorasServico bancoHorasServico) {
        this.registroPontoServico = registroPontoServico;
        this.bancoHorasServico    = bancoHorasServico;
    }

    @GetMapping("/tipos-disponiveis")
    public ResponseEntity<TiposDisponiveisDTO> tiposDisponiveis(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(registroPontoServico.getTiposDisponiveis(resolverMatricula(usuario)));
    }

    @PostMapping("/bater")
    public ResponseEntity<String> baterPonto(@AuthenticationPrincipal Usuario usuario,
                                              @RequestBody BaterPontoRequestDTO req) {
        return ResponseEntity.ok(registroPontoServico.baterPonto(resolverMatricula(usuario), req));
    }

    @GetMapping("/registros")
    public ResponseEntity<List<RegistroPontoDTO>> listarRegistros(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(registroPontoServico.listarRegistros(resolverMatricula(usuario)));
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoDTO> resumo(@AuthenticationPrincipal Usuario usuario,
                                             @RequestParam(defaultValue = "week") String filtro) {
        LocalDateTime fim    = LocalDateTime.now();
        LocalDateTime inicio = filtro.equalsIgnoreCase("month")
                ? fim.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN)
                : fim.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);

        String matricula = resolverMatricula(usuario);
        double saldo     = bancoHorasServico.getSaldoAtual(matricula);
        return ResponseEntity.ok(registroPontoServico.gerarResumo(matricula, inicio, fim, saldo));
    }

    private String resolverMatricula(Usuario usuario) {
        return usuario.getFuncionario() != null
                ? usuario.getFuncionario().getMatricula()
                : usuario.getLogin();
    }
}
