package org.senac.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.senac.entity.Jogador;

@ApplicationScoped
public class JogadorRepository implements PanacheRepository<Jogador> {

    public Jogador buscarPorId(Long id) {
        return findById(id);
    }
}
