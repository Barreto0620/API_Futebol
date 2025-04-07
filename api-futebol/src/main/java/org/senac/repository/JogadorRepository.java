package org.senac.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.senac.entity.Jogador;

/**
 * Repositório para a entidade {@link Jogador}.
 * <p>
 * Esta classe fornece operações CRUD utilizando Panache.
 * </p>
 */
@ApplicationScoped
public class JogadorRepository implements PanacheRepository<Jogador> {

    /**
     * Busca um jogador pelo seu ID.
     *
     * @param id Identificador do jogador.
     * @return Jogador encontrado ou {@code null} se não existir.
     */
    public Jogador buscarPorId(Long id) {
        return findById(id);
    }
}
