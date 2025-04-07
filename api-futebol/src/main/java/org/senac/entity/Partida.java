package org.senac.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference; // Pode precisar se Time tiver referência para Partida

@Entity
@Schema(description = "Representa uma partida de futebol")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da partida", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "time_casa_id", nullable = false)
    @Schema(description = "Time que joga em casa", requiredMode = Schema.RequiredMode.REQUIRED)
    // Se Time tivesse List<Partida> casa, precisaria @JsonManagedReference aqui ou Back no Time
    private Time timeCasa;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "time_fora_id", nullable = false)
    @Schema(description = "Time que joga fora de casa", requiredMode = Schema.RequiredMode.REQUIRED)
     // Se Time tivesse List<Partida> fora, precisaria @JsonManagedReference aqui ou Back no Time
    private Time timeFora;

    @Column(name = "gols_casa", nullable = false)
    @Schema(description = "Número de gols marcados pelo time da casa", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private int golsCasa;

    @Column(name = "gols_fora", nullable = false)
    @Schema(description = "Número de gols marcados pelo time visitante", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private int golsFora;

    @Column(nullable = false)
    @Schema(description = "Data da partida", example = "2025-04-06", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate data;

    @OneToOne(mappedBy = "partida", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "Destaque associado à partida (pode ser nulo ou criado automaticamente)", accessMode = Schema.AccessMode.READ_ONLY) // Geralmente lido, não definido diretamente na criação da partida
    @JsonBackReference // Mantém para evitar recursão Partida -> Destaque -> Partida
    private Destaque destaque;

    // Construtor padrão
    public Partida() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Time getTimeCasa() { return timeCasa; }
    public void setTimeCasa(Time timeCasa) { this.timeCasa = timeCasa; }
    public Time getTimeFora() { return timeFora; }
    public void setTimeFora(Time timeFora) { this.timeFora = timeFora; }
    public int getGolsCasa() { return golsCasa; }
    public void setGolsCasa(int golsCasa) { this.golsCasa = golsCasa; }
    public int getGolsFora() { return golsFora; }
    public void setGolsFora(int golsFora) { this.golsFora = golsFora; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public Destaque getDestaque() { return destaque; }
    public void setDestaque(Destaque destaque) {
        if (destaque != null) { destaque.setPartida(this); }
        if (this.destaque != null && this.destaque != destaque) { this.destaque.setPartida(null); }
        this.destaque = destaque;
    }
}