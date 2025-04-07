package org.senac.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonBackReference; // Importar

@Entity
@Schema(description = "Representa uma partida de futebol")
public class Partida {

    // ... outros campos e anotações ...
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "time_casa_id", nullable = false)
    private Time timeCasa;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "time_fora_id", nullable = false)
    private Time timeFora;

    @Column(name = "gols_casa", nullable = false)
    private int golsCasa;

    @Column(name = "gols_fora", nullable = false)
    private int golsFora;

    @Column(nullable = false)
    private LocalDate data;


    @OneToOne(mappedBy = "partida", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "Destaque associado à partida (pode ser nulo se não houver destaque ou empate sem critério)")
    @JsonBackReference // <--- Adicionar aqui (Lado "de volta" para esta relação)
    private Destaque destaque;

    // Getters e Setters ...

    // Getter onde a anotação foi colocada
    public Destaque getDestaque() { return destaque; }
    public void setDestaque(Destaque destaque) {
        // Lógica de set bidirecional ...
        if (destaque != null) {
            destaque.setPartida(this);
        }
        if (this.destaque != null && this.destaque != destaque) {
             this.destaque.setPartida(null);
        }
        this.destaque = destaque;
    }
    // Outros Getters e Setters
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
}