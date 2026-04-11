package com.app.registro_ponto.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "compensacoes")
public class Compensacao {

    public enum TipoCompensacao {
        CHEGAR_TARDE, SAIR_CEDO;

        public String getLabel() {
            return this == CHEGAR_TARDE ? "Chegar Mais Tarde" : "Sair Mais Cedo";
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "horas_compensadas", nullable = false)
    private Integer horasCompensadas;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoCompensacao tipo;

    @Column(name = "data_compensacao", nullable = false)
    private LocalDate dataCompensacao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Compensacao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getHorasCompensadas() { return horasCompensadas; }
    public void setHorasCompensadas(Integer horasCompensadas) { this.horasCompensadas = horasCompensadas; }

    public TipoCompensacao getTipo() { return tipo; }
    public void setTipo(TipoCompensacao tipo) { this.tipo = tipo; }

    public LocalDate getDataCompensacao() { return dataCompensacao; }
    public void setDataCompensacao(LocalDate dataCompensacao) { this.dataCompensacao = dataCompensacao; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}