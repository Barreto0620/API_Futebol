package org.senac.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.senac.entity.Partida;

@ApplicationScoped
public class PartidaRepository implements PanacheRepository<Partida> {
    // Métodos adicionais podem ser implementados aqui se necessário
}
