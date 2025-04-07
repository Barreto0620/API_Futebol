package org.senac.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Schema(description = "Representa um jogador de futebol")
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do jogador", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome do jogador", example = "Pelé", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Column(nullable = false)
    @Schema(description = "Idade do jogador", example = "35", requiredMode = Schema.RequiredMode.REQUIRED)
    private int idade;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    @Schema(description = "Time ao qual o jogador pertence", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonBackReference
    private Time time;

    // Construtor padrão
    public Jogador() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }
    public Time getTime() { return time; }
    public void setTime(Time time) { this.time = time; }
}