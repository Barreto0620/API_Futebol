package org.senac.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.senac.entity.Time;

@ApplicationScoped
public class TimeRepository implements PanacheRepository<Time> {
}