package com.app.registro_ponto.dto;

public class FuncionarioDTO {
    private String matricula;
    private String nome;
    private String turno;
    private Integer cargaHorariaSemanal;
    private Long cargoId;
    private String senha;

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public Integer getCargaHorariaSemanal() { return cargaHorariaSemanal; }
    public void setCargaHorariaSemanal(Integer cargaHorariaSemanal) { this.cargaHorariaSemanal = cargaHorariaSemanal; }

    public Long getCargoId() { return cargoId; }
    public void setCargoId(Long cargoId) { this.cargoId = cargoId; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}