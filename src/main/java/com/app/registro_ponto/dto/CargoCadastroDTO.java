package com.app.registro_ponto.dto;

import jakarta.validation.constraints.NotBlank;

public class CargoCadastroDTO {

    @NotBlank
    private String titulo;

    @NotBlank
    private String grupo;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}
