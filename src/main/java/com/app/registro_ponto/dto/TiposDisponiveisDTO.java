package com.app.registro_ponto.dto;

import java.util.List;

public class TiposDisponiveisDTO {

    private boolean dentroDoTrabalho;
    private boolean turnoEncerrado;
    private List<TipoDisponivel> tipos;

    public static class TipoDisponivel {
        private String tipo;
        private String label;
        private String cor;

        public TipoDisponivel() {}
        public TipoDisponivel(String tipo, String label, String cor) {
            this.tipo = tipo;
            this.label = label;
            this.cor = cor;
        }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public String getCor() { return cor; }
        public void setCor(String cor) { this.cor = cor; }
    }

    public boolean isDentroDoTrabalho() { return dentroDoTrabalho; }
    public void setDentroDoTrabalho(boolean dentroDoTrabalho) { this.dentroDoTrabalho = dentroDoTrabalho; }

    public boolean isTurnoEncerrado() { return turnoEncerrado; }
    public void setTurnoEncerrado(boolean turnoEncerrado) { this.turnoEncerrado = turnoEncerrado; }

    public List<TipoDisponivel> getTipos() { return tipos; }
    public void setTipos(List<TipoDisponivel> tipos) { this.tipos = tipos; }
}
