package com.app.registro_ponto.modelo;

public enum GrupoCargo {
    GERENCIA("Gerência"),
    LIDERANCA("Liderança"),
    ADMINISTRATIVO("Administrativo"),
    OPERACIONAL("Operacional");

    private final String titulo;

    GrupoCargo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public static GrupoCargo fromCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Grupo do cargo é obrigatório.");
        }
        try {
            return GrupoCargo.valueOf(codigo.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Grupo inválido. Use: GERENCIA, LIDERANCA, ADMINISTRATIVO, OPERACIONAL.");
        }
    }
}
