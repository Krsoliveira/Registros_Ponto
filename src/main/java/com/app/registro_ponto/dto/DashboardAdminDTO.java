package com.app.registro_ponto.dto;

import java.util.List;

public class DashboardAdminDTO {

    private int totalFuncionarios;
    private long funcionariosComPontoAberto;
    private double totalHorasSemana;
    private double totalHorasMes;
    private List<HorasPorDiaDTO> horasPorDia;
    private List<FuncionarioResumoDTO> topHorasExtras;
    private List<RegistroRecenteDTO> ultimosRegistros;

    public static class HorasPorDiaDTO {
        private String dia;
        private double horas;

        public HorasPorDiaDTO() {}
        public HorasPorDiaDTO(String dia, double horas) { this.dia = dia; this.horas = horas; }

        public String getDia() { return dia; }
        public void setDia(String dia) { this.dia = dia; }

        public double getHoras() { return horas; }
        public void setHoras(double horas) { this.horas = horas; }
    }

    public static class FuncionarioResumoDTO {
        private String nome;
        private String matricula;
        private double totalHoras;
        private double horasExtras;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getMatricula() { return matricula; }
        public void setMatricula(String matricula) { this.matricula = matricula; }

        public double getTotalHoras() { return totalHoras; }
        public void setTotalHoras(double totalHoras) { this.totalHoras = totalHoras; }

        public double getHorasExtras() { return horasExtras; }
        public void setHorasExtras(double horasExtras) { this.horasExtras = horasExtras; }
    }

    public static class RegistroRecenteDTO {
        private String nomeFuncionario;
        private String matricula;
        private String tipo;
        private String horario;

        public String getNomeFuncionario() { return nomeFuncionario; }
        public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }

        public String getMatricula() { return matricula; }
        public void setMatricula(String matricula) { this.matricula = matricula; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public String getHorario() { return horario; }
        public void setHorario(String horario) { this.horario = horario; }
    }

    public int getTotalFuncionarios() { return totalFuncionarios; }
    public void setTotalFuncionarios(int totalFuncionarios) { this.totalFuncionarios = totalFuncionarios; }

    public long getFuncionariosComPontoAberto() { return funcionariosComPontoAberto; }
    public void setFuncionariosComPontoAberto(long funcionariosComPontoAberto) { this.funcionariosComPontoAberto = funcionariosComPontoAberto; }

    public double getTotalHorasSemana() { return totalHorasSemana; }
    public void setTotalHorasSemana(double totalHorasSemana) { this.totalHorasSemana = totalHorasSemana; }

    public double getTotalHorasMes() { return totalHorasMes; }
    public void setTotalHorasMes(double totalHorasMes) { this.totalHorasMes = totalHorasMes; }

    public List<HorasPorDiaDTO> getHorasPorDia() { return horasPorDia; }
    public void setHorasPorDia(List<HorasPorDiaDTO> horasPorDia) { this.horasPorDia = horasPorDia; }

    public List<FuncionarioResumoDTO> getTopHorasExtras() { return topHorasExtras; }
    public void setTopHorasExtras(List<FuncionarioResumoDTO> topHorasExtras) { this.topHorasExtras = topHorasExtras; }

    public List<RegistroRecenteDTO> getUltimosRegistros() { return ultimosRegistros; }
    public void setUltimosRegistros(List<RegistroRecenteDTO> ultimosRegistros) { this.ultimosRegistros = ultimosRegistros; }
}