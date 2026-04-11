package com.app.registro_ponto.dto;

import java.util.List;

public class ResumoDTO {
    private String nomeFuncionario;
    private String turno;
    private int cargaHorariaSemanal;
    private double totalHoras;
    private double horasExtras;
    private List<RegistroPontoDTO> registros;

    public ResumoDTO() {}

    public ResumoDTO(String nomeFuncionario, String turno, int cargaHorariaSemanal,
                     double totalHoras, double horasExtras, List<RegistroPontoDTO> registros) {
        this.nomeFuncionario = nomeFuncionario;
        this.turno = turno;
        this.cargaHorariaSemanal = cargaHorariaSemanal;
        this.totalHoras = totalHoras;
        this.horasExtras = horasExtras;
        this.registros = registros;
    }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public int getCargaHorariaSemanal() { return cargaHorariaSemanal; }
    public void setCargaHorariaSemanal(int cargaHorariaSemanal) { this.cargaHorariaSemanal = cargaHorariaSemanal; }

    public double getTotalHoras() { return totalHoras; }
    public void setTotalHoras(double totalHoras) { this.totalHoras = totalHoras; }

    public double getHorasExtras() { return horasExtras; }
    public void setHorasExtras(double horasExtras) { this.horasExtras = horasExtras; }

    public List<RegistroPontoDTO> getRegistros() { return registros; }
    public void setRegistros(List<RegistroPontoDTO> registros) { this.registros = registros; }
}