package com.app.registro_ponto.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "funcionarios")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String matricula;

    @Column(nullable = false)
    @NotBlank
    private String nome;

    @Column(nullable = false)
    private String turno;

    @Column(name = "carga_horaria_semanal", nullable = false)
    @NotNull
    private Integer cargaHorariaSemanal;

    @Column(nullable = false)
    private boolean ativo = true;

    public Funcionario() {}

    public Funcionario(String matricula, String nome, String turno, Integer cargaHorariaSemanal) {
        this.matricula = matricula;
        this.nome = nome;
        this.turno = turno != null ? turno : "Comercial";
        this.cargaHorariaSemanal = cargaHorariaSemanal != null ? cargaHorariaSemanal : 44;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public Integer getCargaHorariaSemanal() { return cargaHorariaSemanal; }
    public void setCargaHorariaSemanal(Integer cargaHorariaSemanal) { this.cargaHorariaSemanal = cargaHorariaSemanal; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}