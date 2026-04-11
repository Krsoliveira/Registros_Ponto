package com.app.registro_ponto.dto;

import java.util.List;

public class DashboardFuncionarioDTO {

    private String nomeFuncionario;
    private String turno;
    private boolean comPontoAberto;
    private double horasSemana;
    private double horasMes;
    private double horasExtras;
    private List<DashboardAdminDTO.HorasPorDiaDTO> horasPorDia;
    private List<RegistroPontoDTO> historicoRecente;

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public boolean isComPontoAberto() { return comPontoAberto; }
    public void setComPontoAberto(boolean comPontoAberto) { this.comPontoAberto = comPontoAberto; }

    public double getHorasSemana() { return horasSemana; }
    public void setHorasSemana(double horasSemana) { this.horasSemana = horasSemana; }

    public double getHorasMes() { return horasMes; }
    public void setHorasMes(double horasMes) { this.horasMes = horasMes; }

    public double getHorasExtras() { return horasExtras; }
    public void setHorasExtras(double horasExtras) { this.horasExtras = horasExtras; }

    public List<DashboardAdminDTO.HorasPorDiaDTO> getHorasPorDia() { return horasPorDia; }
    public void setHorasPorDia(List<DashboardAdminDTO.HorasPorDiaDTO> horasPorDia) { this.horasPorDia = horasPorDia; }

    public List<RegistroPontoDTO> getHistoricoRecente() { return historicoRecente; }
    public void setHistoricoRecente(List<RegistroPontoDTO> historicoRecente) { this.historicoRecente = historicoRecente; }
}