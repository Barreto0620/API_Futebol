package org.senac.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.senac.entity.Destaque;
import org.senac.repository.DestaqueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.senac.idempotency.Idempotent; // Importe a anotação

import java.util.List;

@Path("/destaques")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Destaques", description = "Operações relacionadas aos destaques das partidas")
public class DestaqueResource {

    @Inject
    DestaqueRepository repository;

    @GET
    @Operation(summary = "Listar destaques", description = "Retorna a lista de todos os destaques")
    public List<Destaque> list() {
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar destaque por ID", description = "Retorna um destaque específico pelo seu ID")
    public Destaque get(@PathParam("id") Long id) {
        return repository.findById(id);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Idempotent // Torna a operação DELETE idempotente
    @Operation(summary = "Excluir destaque", description = "Exclui um destaque específico pelo seu ID")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}