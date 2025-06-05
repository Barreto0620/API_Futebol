package org.senac.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore; // <-- IMPORTANTE: Use esta importação!

@Entity
@Table(name = "jogador")
public class Jogador extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore // <--- MUDANÇA AQUI: Agora usando @JsonIgnore do Jackson
    public Long id;

    @Column(nullable = false)
    public String nome;

    @Column(nullable = true)
    public Integer idade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id")
    @JsonBackReference
    public Time time;

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

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}