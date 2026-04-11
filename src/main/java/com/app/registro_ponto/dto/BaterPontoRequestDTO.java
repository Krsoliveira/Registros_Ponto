package com.app.registro_ponto.dto;

public class BaterPontoRequestDTO {
    private String tipoMarcacao;
    private String nomeLocal;
    private Double latitude;
    private Double longitude;

    public String getTipoMarcacao() { return tipoMarcacao; }
    public void setTipoMarcacao(String tipoMarcacao) { this.tipoMarcacao = tipoMarcacao; }

    public String getNomeLocal() { return nomeLocal; }
    public void setNomeLocal(String nomeLocal) { this.nomeLocal = nomeLocal; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
