package com.app.registro_ponto.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(
        name = "cargos",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cargo_titulo_grupo",
                columnNames = {"titulo", "grupo"}
        )
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GrupoCargo grupo;

    public Cargo() {}

    public Cargo(String titulo, GrupoCargo grupo) {
        this.titulo = titulo;
        this.grupo = grupo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public GrupoCargo getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoCargo grupo) {
        this.grupo = grupo;
    }

    @JsonProperty("grupoNome")
    public String getGrupoNome() {
        return grupo != null ? grupo.getTitulo() : null;
    }
}
