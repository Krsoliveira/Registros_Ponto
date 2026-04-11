package com.app.registro_ponto.dto;

import java.util.List;

public class ResumoDTO {
    private String nomeFuncionario;
    private String turno;
    private int cargaHorariaSemanal;
    private double totalHoras;
    private double horasExtras;
    private double saldoBancoHoras;
    private List<RegistroPontoDTO> registros;

    public ResumoDTO() {}

    public ResumoDTO(String nomeFuncionario, String turno, int cargaHorariaSemanal,
                     double totalHoras, double horasExtras, double saldoBancoHoras,
                     List<RegistroPontoDTO> registros) {
        this.nomeFuncionario = nomeFuncionario;
        this.turno = turno;
        this.cargaHorariaSemanal = cargaHorariaSemanal;
        this.totalHoras = totalHoras;
        this.horasExtras = horasExtras;
        this.saldoBancoHoras = saldoBancoHoras;
        this.registros = registros;
    }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String v) { this.nomeFuncionario = v; }

    public String getTurno() { return turno; }
    public void setTurno(String v) { this.turno = v; }

    public int getCargaHorariaSemanal() { return cargaHorariaSemanal; }
    public void setCargaHorariaSemanal(int v) { this.cargaHorariaSemanal = v; }

    public double getTotalHoras() { return totalHoras; }
    public void setTotalHoras(double v) { this.totalHoras = v; }

    public double getHorasExtras() { return horasExtras; }
    public void setHorasExtras(double v) { this.horasExtras = v; }

    public double getSaldoBancoHoras() { return saldoBancoHoras; }
    public void setSaldoBancoHoras(double v) { this.saldoBancoHoras = v; }

    public List<RegistroPontoDTO> getRegistros() { return registros; }
    public void setRegistros(List<RegistroPontoDTO> v) { this.registros = v; }
}
