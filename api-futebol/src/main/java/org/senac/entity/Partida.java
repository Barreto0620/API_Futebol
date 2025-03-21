package org.senac.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "time_casa_id", nullable = false)
    private Time timeCasa;

    @ManyToOne
    @JoinColumn(name = "time_fora_id", nullable = false)
    private Time timeFora;

    private int golsCasa;
    private int golsFora;
    private LocalDate data;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Time getTimeCasa() {
        return timeCasa;
    }

    public void setTimeCasa(Time timeCasa) {
        this.timeCasa = timeCasa;
    }

    public Time getTimeFora() {
        return timeFora;
    }

    public void setTimeFora(Time timeFora) {
        this.timeFora = timeFora;
    }

    public int getGolsCasa() {
        return golsCasa;
    }

    public void setGolsCasa(int golsCasa) {
        this.golsCasa = golsCasa;
    }

    public int getGolsFora() {
        return golsFora;
    }

    public void setGolsFora(int golsFora) {
        this.golsFora = golsFora;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
