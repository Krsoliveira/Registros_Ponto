package com.app.registro_ponto.controlador;

import com.app.registro_ponto.dto.LoginRequestDTO;
import com.app.registro_ponto.dto.LoginResponseDTO;
import com.app.registro_ponto.modelo.Usuario;
import com.app.registro_ponto.seguranca.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthControlador(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getSenha())
            );
            Usuario usuario = (Usuario) auth.getPrincipal();
            String token = jwtUtil.generateToken(usuario);

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            response.setLogin(usuario.getLogin());
            response.setPerfil(usuario.getPerfil().name());

            if (usuario.getFuncionario() != null) {
                response.setFuncionarioId(usuario.getFuncionario().getId());
                response.setNomeFuncionario(usuario.getFuncionario().getNome());
                response.setMatricula(usuario.getFuncionario().getMatricula());
            } else {
                response.setNomeFuncionario(usuario.getLogin());
            }

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Login ou senha incorretos.");
        }
    }
}