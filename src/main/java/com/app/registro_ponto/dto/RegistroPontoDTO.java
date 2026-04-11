package com.app.registro_ponto.dto;

public class RegistroPontoDTO {
    private Long id;
    private String data;
    private String horaEntrada;
    private String horaSaida;
    private double horasTrabalhadas;

    public RegistroPontoDTO() {}

    public RegistroPontoDTO(Long id, String data, String horaEntrada, String horaSaida, double horasTrabalhadas) {
        this.id = id;
        this.data = data;
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.horasTrabalhadas = horasTrabalhadas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }

    public String getHoraSaida() { return horaSaida; }
    public void setHoraSaida(String horaSaida) { this.horaSaida = horaSaida; }

    public double getHorasTrabalhadas() { return horasTrabalhadas; }
    public void setHorasTrabalhadas(double horasTrabalhadas) { this.horasTrabalhadas = horasTrabalhadas; }
}