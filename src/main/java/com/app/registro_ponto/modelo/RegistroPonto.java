package com.app.registro_ponto.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_ponto")
public class RegistroPonto {

    public enum TipoMarcacao {
        ENTRADA,
        SAIDA_INTERVALO,
        RETORNO_INTERVALO,
        SAIDA_INTERMEDIARIA,
        RETORNO_INTERMEDIARIO,
        SAIDA_FINAL;

        public String getLabel() {
            return switch (this) {
                case ENTRADA              -> "Entrada";
                case SAIDA_INTERVALO      -> "Saída Intervalo";
                case RETORNO_INTERVALO    -> "Retorno Intervalo";
                case SAIDA_INTERMEDIARIA  -> "Saída Intermediária";
                case RETORNO_INTERMEDIARIO -> "Retorno Intermediário";
                case SAIDA_FINAL          -> "Saída Final";
            };
        }

        public boolean isEntrada() {
            return this == ENTRADA || this == RETORNO_INTERVALO || this == RETORNO_INTERMEDIARIO;
        }

        public boolean isSaida() {
            return this == SAIDA_INTERVALO || this == SAIDA_INTERMEDIARIA || this == SAIDA_FINAL;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "hora_marcacao", nullable = false)
    private LocalDateTime horaMarcacao;

    @Column(name = "tipo_marcacao", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMarcacao tipoMarcacao;

    @Column(name = "nome_local")
    private String nomeLocal;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    public RegistroPonto() {}

    public RegistroPonto(Funcionario funcionario, LocalDateTime horaMarcacao,
                         TipoMarcacao tipoMarcacao, String nomeLocal,
                         Double latitude, Double longitude) {
        this.funcionario  = funcionario;
        this.horaMarcacao = horaMarcacao;
        this.tipoMarcacao = tipoMarcacao;
        this.nomeLocal    = nomeLocal;
        this.latitude     = latitude;
        this.longitude    = longitude;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public LocalDateTime getHoraMarcacao() { return horaMarcacao; }
    public void setHoraMarcacao(LocalDateTime horaMarcacao) { this.horaMarcacao = horaMarcacao; }

    public TipoMarcacao getTipoMarcacao() { return tipoMarcacao; }
    public void setTipoMarcacao(TipoMarcacao tipoMarcacao) { this.tipoMarcacao = tipoMarcacao; }

    public String getNomeLocal() { return nomeLocal; }
    public void setNomeLocal(String nomeLocal) { this.nomeLocal = nomeLocal; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}