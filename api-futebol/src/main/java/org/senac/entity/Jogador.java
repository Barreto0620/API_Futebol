// api-futebol\src\main\java\org\senac\entity\Jogador.java
package org.senac.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference; // Adicione esta importação se não estiver presente

@Entity
@Table(name = "jogador")
public class Jogador extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String nome;

    // ALtere de 'int' para 'Integer'
    @Column(nullable = true) // 'idade' pode ser nula se não for fornecida
    public Integer idade; // <-- Mude para Integer

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id")
    @JsonBackReference // Evita loop infinito em serialização JSON
    public Time time;

    // Getters e Setters (se você não estiver usando Lombok ou record)
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

    public Integer getIdade() { // <-- Mude para Integer
        return idade;
    }

    public void setIdade(Integer idade) { // <-- Mude para Integer
        this.idade = idade;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}