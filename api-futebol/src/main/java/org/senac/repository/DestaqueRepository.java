package org.senac.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.senac.entity.Destaque;

/**
 * Repositório para a entidade {@link Destaque}.
 * <p>
 * Esta classe fornece operações CRUD utilizando Panache.
 * </p>
 */
@ApplicationScoped
public class DestaqueRepository implements PanacheRepository<Destaque> {
}
