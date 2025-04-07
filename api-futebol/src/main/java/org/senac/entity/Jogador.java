package org.senac.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonBackReference; // Importar

@Entity
@Schema(description = "Representa um jogador do time")
public class Jogador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do jogador", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome do jogador", example = "Pelé")
    private String nome;

    @Column(nullable = false)
    @Schema(description = "Idade do jogador", example = "35")
    private int idade;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    @Schema(description = "Time ao qual o jogador pertence")
    @JsonBackReference // <--- Adicionar esta anotação aqui
    private Time time;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    // Getter para time (onde a anotação foi colocada)
    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}