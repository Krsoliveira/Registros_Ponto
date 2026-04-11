package com.app.registro_ponto.servico;

import com.app.registro_ponto.dto.FuncionarioDTO;
import com.app.registro_ponto.modelo.Funcionario;
import com.app.registro_ponto.modelo.Usuario;
import com.app.registro_ponto.repositorio.FuncionarioRepositorio;
import com.app.registro_ponto.repositorio.UsuarioRepositorio;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FuncionarioServico {

    private final FuncionarioRepositorio funcionarioRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioServico(FuncionarioRepositorio funcionarioRepositorio,
                               UsuarioRepositorio usuarioRepositorio,
                               PasswordEncoder passwordEncoder) {
        this.funcionarioRepositorio = funcionarioRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Funcionario cadastrar(FuncionarioDTO dto) {
        if (funcionarioRepositorio.existsByMatricula(dto.getMatricula())) {
            throw new IllegalArgumentException("Matrícula já cadastrada.");
        }
        if (usuarioRepositorio.existsByLogin(dto.getMatricula())) {
            throw new IllegalArgumentException("Login já em uso.");
        }

        Funcionario f = new Funcionario();
        f.setMatricula(dto.getMatricula());
        f.setNome(dto.getNome());
        f.setTurno(dto.getTurno() != null && !dto.getTurno().isBlank() ? dto.getTurno() : "Comercial");
        f.setCargaHorariaSemanal(dto.getCargaHorariaSemanal() != null ? dto.getCargaHorariaSemanal() : 44);
        f = funcionarioRepositorio.save(f);

        // Cria conta de usuário: login = matrícula, senha padrão = matrícula (ou a fornecida)
        String senhaBruta = (dto.getSenha() != null && !dto.getSenha().isBlank())
                ? dto.getSenha() : dto.getMatricula();

        Usuario u = new Usuario();
        u.setLogin(dto.getMatricula());
        u.setSenha(passwordEncoder.encode(senhaBruta));
        u.setPerfil(Usuario.Perfil.FUNCIONARIO);
        u.setFuncionario(f);
        usuarioRepositorio.save(u);

        return f;
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepositorio.findAll();
    }

    public Funcionario buscarPorMatricula(String matricula) {
        return funcionarioRepositorio.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));
    }
}