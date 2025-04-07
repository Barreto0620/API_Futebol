package org.senac.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference; // Importar

@Entity
@Schema(description = "Representa um time de futebol")
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do time", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome do time", example = "Flamengo")
    private String nome;

    @Column(nullable = false)
    @Schema(description = "Cidade do time", example = "Rio de Janeiro")
    private String cidade;

    @OneToMany(mappedBy = "time", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "Jogadores que pertencem ao time")
    @JsonManagedReference // <--- Adicionar esta anotação aqui
    private List<Jogador> jogadores;

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

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    // Getter para jogadores (onde a anotação foi colocada)
    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<Jogador> jogadores) {
        this.jogadores = jogadores;
    }
}