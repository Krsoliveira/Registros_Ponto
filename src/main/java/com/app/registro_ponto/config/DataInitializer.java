package com.app.registro_ponto.config;

import com.app.registro_ponto.modelo.Usuario;
import com.app.registro_ponto.repositorio.UsuarioRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner inicializarAdmin(UsuarioRepositorio usuarioRepositorio,
                                              PasswordEncoder passwordEncoder) {
        return args -> {
            if (!usuarioRepositorio.existsByLogin("admin")) {
                Usuario admin = new Usuario();
                admin.setLogin("admin");
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setPerfil(Usuario.Perfil.ADMIN);
                usuarioRepositorio.save(admin);
                System.out.println("==========================================");
                System.out.println(" Usuário ADMIN criado com sucesso!");
                System.out.println(" Login: admin  |  Senha: admin123");
                System.out.println("==========================================");
            }
        };
    }
}