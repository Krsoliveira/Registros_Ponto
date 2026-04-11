package com.app.registro_ponto.controlador;

import com.app.registro_ponto.dto.BancoHorasDTO;
import com.app.registro_ponto.dto.CompensacaoRequestDTO;
import com.app.registro_ponto.modelo.Usuario;
import com.app.registro_ponto.servico.BancoHorasServico;
import com.app.registro_ponto.servico.CompensacaoServico;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/banco-horas")
public class CompensacaoControlador {

    private final CompensacaoServico compensacaoServico;
    private final BancoHorasServico bancoHorasServico;

    public CompensacaoControlador(CompensacaoServico compensacaoServico,
                                   BancoHorasServico bancoHorasServico) {
        this.compensacaoServico = compensacaoServico;
        this.bancoHorasServico  = bancoHorasServico;
    }

    @GetMapping
    public ResponseEntity<BancoHorasDTO> getBancoHoras(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(bancoHorasServico.getBancoHoras(resolverMatricula(usuario)));
    }

    @PostMapping("/compensacao")
    public ResponseEntity<String> registrarCompensacao(@AuthenticationPrincipal Usuario usuario,
                                                        @RequestBody CompensacaoRequestDTO req) {
        compensacaoServico.registrar(resolverMatricula(usuario), req);
        return ResponseEntity.ok("Compensação registrada com sucesso.");
    }

    private String resolverMatricula(Usuario usuario) {
        return usuario.getFuncionario() != null
                ? usuario.getFuncionario().getMatricula()
                : usuario.getLogin();
    }
}
