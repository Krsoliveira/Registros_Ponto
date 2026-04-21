package com.app.registro_ponto.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "funcionarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @CreationTimestamp
    @Column(name = "data_cadastro", updatable = false)
    private LocalDate dataCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;

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

    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }
}