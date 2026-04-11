package com.app.registro_ponto.dto;

public class CompensacaoRequestDTO {
    private String motivo;
    private Integer horasCompensadas;
    private String tipo;
    private String dataCompensacao;

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getHorasCompensadas() { return horasCompensadas; }
    public void setHorasCompensadas(Integer horasCompensadas) { this.horasCompensadas = horasCompensadas; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDataCompensacao() { return dataCompensacao; }
    public void setDataCompensacao(String dataCompensacao) { this.dataCompensacao = dataCompensacao; }
}
