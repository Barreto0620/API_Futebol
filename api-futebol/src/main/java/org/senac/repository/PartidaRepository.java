package org.senac.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.senac.entity.Partida;

/**
 * Repositório para a entidade {@link Partida}.
 * <p>
 * Esta classe fornece operações CRUD utilizando Panache.
 * </p>
 */
@ApplicationScoped
public class PartidaRepository implements PanacheRepository<Partida> {
    // Métodos adicionais podem ser implementados aqui se necessário.
}
