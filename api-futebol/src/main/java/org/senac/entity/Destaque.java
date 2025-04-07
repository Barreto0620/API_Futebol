package org.senac.entity;

import io.swagger.v3.oas.annotations.media.Schema; // Certifique-se que a importação correta é usada
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Schema(description = "Entidade que representa o destaque de uma partida (geralmente o jogador com mais gols)")
public class Destaque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do destaque", example = "1", accessMode = Schema.AccessMode.READ_ONLY) // ID é geralmente lido, não escrito
    private Long id;

    @OneToOne // Destaque pertence a uma Partida
    @JoinColumn(name = "partida_id", nullable = false, unique = true)
    @Schema(description = "Partida relacionada ao destaque", requiredMode = Schema.RequiredMode.REQUIRED) // Campo obrigatório
    @JsonManagedReference
    private Partida partida;

    @ManyToOne(fetch = FetchType.LAZY) // Vários destaques podem ser do mesmo Jogador
    @JoinColumn(name = "jogador_id", nullable = false)
    @Schema(description = "Jogador que foi o destaque da partida", requiredMode = Schema.RequiredMode.REQUIRED) // Campo obrigatório
    private Jogador jogador;

    @Column(name = "gols_marcados")
    @Schema(description = "Quantidade de gols marcados pelo jogador destaque na partida", example = "3", requiredMode = Schema.RequiredMode.REQUIRED) // Campo obrigatório
    private int golsMarcados;

    // Construtor vazio padrão
    public Destaque() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Partida getPartida() { return partida; }
    public void setPartida(Partida partida) { this.partida = partida; }
    public Jogador getJogador() { return jogador; }
    public void setJogador(Jogador jogador) { this.jogador = jogador; }
    public int getGolsMarcados() { return golsMarcados; }
    public void setGolsMarcados(int golsMarcados) { this.golsMarcados = golsMarcados; }
}