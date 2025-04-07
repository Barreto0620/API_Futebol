package org.senac.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.senac.entity.Time;
import org.senac.repository.TimeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Path("/times")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Times", description = "Operações relacionadas aos times de futebol")
public class TimeResource {

    @Inject
    TimeRepository repository;

    @GET
    @Operation(summary = "Listar times", description = "Retorna a lista de todos os times")
    public List<Time> listAll() {
        return repository.listAll();
    }

    @POST
    @Transactional
    @Operation(summary = "Adicionar time", description = "Cria um novo time")
    public Response add(Time time) {
        repository.persist(time);
        return Response.status(Response.Status.CREATED).entity(time).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar time", description = "Atualiza os dados de um time existente")
    public Response update(@PathParam("id") Long id, Time timeAtualizado) {
        Time time = repository.findById(id);
        if (time == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        time.setNome(timeAtualizado.getNome());
        time.setCidade(timeAtualizado.getCidade());
        return Response.ok(time).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Excluir time", description = "Exclui um time pelo seu ID")
    public Response delete(@PathParam("id") Long id) {
        Time time = repository.findById(id);
        if (time == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        repository.delete(time);
        return Response.noContent().build();
    }
}
