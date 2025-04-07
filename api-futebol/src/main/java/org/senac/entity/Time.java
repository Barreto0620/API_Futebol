package org.senac.entity;

import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Schema(description = "Representa um time de futebol")
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do time", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome do time", example = "Flamengo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @Column(nullable = false)
    @Schema(description = "Cidade do time", example = "Rio de Janeiro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cidade;

    @OneToMany(mappedBy = "time", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "Jogadores que pertencem ao time (geralmente não enviado na criação/atualização do time)", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonManagedReference // Mantém para evitar recursão Time -> Jogador -> Time
    private List<Jogador> jogadores;

    // Construtor padrão
    public Time() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public List<Jogador> getJogadores() { return jogadores; }
    public void setJogadores(List<Jogador> jogadores) { this.jogadores = jogadores; }
}