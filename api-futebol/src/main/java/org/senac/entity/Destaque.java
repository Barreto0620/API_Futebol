package org.senac.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference; // Importar

@Entity
@Schema(description = "Entidade que representa o destaque de uma partida (geralmente o jogador com mais gols)")
public class Destaque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do destaque", example = "1")
    private Long id;

    @OneToOne // Destaque pertence a uma Partida
    @JoinColumn(name = "partida_id", nullable = false, unique = true)
    @Schema(description = "Partida relacionada ao destaque")
    @JsonManagedReference // <--- Adicionar aqui (Lado "principal" para esta relação)
    private Partida partida;

    @ManyToOne(fetch = FetchType.LAZY) // Vários destaques podem ser do mesmo Jogador
    @JoinColumn(name = "jogador_id", nullable = false)
    @Schema(description = "Jogador que foi o destaque da partida")
    // @JsonBackReference // NÃO coloque aqui, senão o Jogador some do Destaque. A relação Jogador<->Time já foi tratada.
    private Jogador jogador;

    @Column(name = "gols_marcados")
    @Schema(description = "Quantidade de gols marcados pelo jogador destaque na partida", example = "3")
    private int golsMarcados;

    // Construtores...

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Getter onde a anotação foi colocada
    public Partida getPartida() { return partida; }
    public void setPartida(Partida partida) { this.partida = partida; }

    public Jogador getJogador() { return jogador; }
    public void setJogador(Jogador jogador) { this.jogador = jogador; }

    public int getGolsMarcados() { return golsMarcados; }
    public void setGolsMarcados(int golsMarcados) { this.golsMarcados = golsMarcados; }
}