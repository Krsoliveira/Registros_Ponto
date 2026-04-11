package com.app.registro_ponto.dto;

import java.util.List;

public class BancoHorasDTO {

    private double saldo;
    private String saldoFormatado;
    private double totalTrabalhado;
    private double totalEsperado;
    private double totalCompensado;
    private List<CompensacaoResponseDTO> compensacoes;

    public static class CompensacaoResponseDTO {
        private Long id;
        private String motivo;
        private Integer horasCompensadas;
        private String tipo;
        private String tipoLabel;
        private String dataCompensacao;
        private String criadoEm;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }

        public Integer getHorasCompensadas() { return horasCompensadas; }
        public void setHorasCompensadas(Integer horasCompensadas) { this.horasCompensadas = horasCompensadas; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public String getTipoLabel() { return tipoLabel; }
        public void setTipoLabel(String tipoLabel) { this.tipoLabel = tipoLabel; }

        public String getDataCompensacao() { return dataCompensacao; }
        public void setDataCompensacao(String dataCompensacao) { this.dataCompensacao = dataCompensacao; }

        public String getCriadoEm() { return criadoEm; }
        public void setCriadoEm(String criadoEm) { this.criadoEm = criadoEm; }
    }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public String getSaldoFormatado() { return saldoFormatado; }
    public void setSaldoFormatado(String saldoFormatado) { this.saldoFormatado = saldoFormatado; }

    public double getTotalTrabalhado() { return totalTrabalhado; }
    public void setTotalTrabalhado(double totalTrabalhado) { this.totalTrabalhado = totalTrabalhado; }

    public double getTotalEsperado() { return totalEsperado; }
    public void setTotalEsperado(double totalEsperado) { this.totalEsperado = totalEsperado; }

    public double getTotalCompensado() { return totalCompensado; }
    public void setTotalCompensado(double totalCompensado) { this.totalCompensado = totalCompensado; }

    public List<CompensacaoResponseDTO> getCompensacoes() { return compensacoes; }
    public void setCompensacoes(List<CompensacaoResponseDTO> compensacoes) { this.compensacoes = compensacoes; }
}
