package com.app.registro_ponto.dto;

public class LoginResponseDTO {
    private String token;
    private String login;
    private String perfil;
    private Long funcionarioId;
    private String nomeFuncionario;
    private String matricula;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public Long getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(Long funcionarioId) { this.funcionarioId = funcionarioId; }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
}