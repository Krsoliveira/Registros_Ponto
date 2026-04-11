package com.app.registro_ponto.dto;

import java.util.List;

public class DashboardFuncionarioDTO {

    private String nomeFuncionario;
    private String turno;
    private boolean dentroDoTrabalho;
    private double horasSemana;
    private double horasMes;
    private double horasExtras;
    private double saldoBancoHoras;
    private String saldoFormatado;
    private List<DashboardAdminDTO.HorasPorDiaDTO> horasPorDia;
    private List<RegistroPontoDTO> historicoRecente;

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String v) { this.nomeFuncionario = v; }

    public String getTurno() { return turno; }
    public void setTurno(String v) { this.turno = v; }

    public boolean isDentroDoTrabalho() { return dentroDoTrabalho; }
    public void setDentroDoTrabalho(boolean v) { this.dentroDoTrabalho = v; }

    public double getHorasSemana() { return horasSemana; }
    public void setHorasSemana(double v) { this.horasSemana = v; }

    public double getHorasMes() { return horasMes; }
    public void setHorasMes(double v) { this.horasMes = v; }

    public double getHorasExtras() { return horasExtras; }
    public void setHorasExtras(double v) { this.horasExtras = v; }

    public double getSaldoBancoHoras() { return saldoBancoHoras; }
    public void setSaldoBancoHoras(double v) { this.saldoBancoHoras = v; }

    public String getSaldoFormatado() { return saldoFormatado; }
    public void setSaldoFormatado(String v) { this.saldoFormatado = v; }

    public List<DashboardAdminDTO.HorasPorDiaDTO> getHorasPorDia() { return horasPorDia; }
    public void setHorasPorDia(List<DashboardAdminDTO.HorasPorDiaDTO> v) { this.horasPorDia = v; }

    public List<RegistroPontoDTO> getHistoricoRecente() { return historicoRecente; }
    public void setHistoricoRecente(List<RegistroPontoDTO> v) { this.historicoRecente = v; }
}
