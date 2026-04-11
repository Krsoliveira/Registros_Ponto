package com.app.registro_ponto.dto;

public class RegistroPontoDTO {
    private Long id;
    private String data;
    private String hora;
    private String tipoMarcacao;
    private String tipoLabel;
    private String nomeLocal;
    private Double latitude;
    private Double longitude;

    public RegistroPontoDTO() {}

    public RegistroPontoDTO(Long id, String data, String hora, String tipoMarcacao,
                             String tipoLabel, String nomeLocal, Double latitude, Double longitude) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.tipoMarcacao = tipoMarcacao;
        this.tipoLabel = tipoLabel;
        this.nomeLocal = nomeLocal;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getTipoMarcacao() { return tipoMarcacao; }
    public void setTipoMarcacao(String tipoMarcacao) { this.tipoMarcacao = tipoMarcacao; }

    public String getTipoLabel() { return tipoLabel; }
    public void setTipoLabel(String tipoLabel) { this.tipoLabel = tipoLabel; }

    public String getNomeLocal() { return nomeLocal; }
    public void setNomeLocal(String nomeLocal) { this.nomeLocal = nomeLocal; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
