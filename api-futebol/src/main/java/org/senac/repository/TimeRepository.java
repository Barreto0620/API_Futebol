package org.senac.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.senac.entity.Time;

/**
 * Repositório para a entidade {@link Time}.
 * <p>
 * Esta classe fornece operações CRUD utilizando Panache.
 * </p>
 */
@ApplicationScoped
public class TimeRepository implements PanacheRepository<Time> {
}
