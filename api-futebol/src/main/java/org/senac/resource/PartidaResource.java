package org.senac.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.senac.entity.Partida;
import org.senac.repository.PartidaRepository;

import java.util.List;

@Path("/partidas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PartidaResource {

    @Inject
    PartidaRepository repository;

    @GET
    public List<Partida> listAll() {
        return repository.listAll();
    }

    @POST
    public Response add(Partida partida) {
        repository.persist(partida);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Partida partidaAtualizada) {
        Partida partida = repository.findById(id);
        if (partida == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        partida.setTimeCasa(partidaAtualizada.getTimeCasaId());
        partida.setTimeFora(partidaAtualizada.getTimeForaId());
        partida.setGolsCasa(partidaAtualizada.getGolsCasa());
        partida.setGolsFora(partidaAtualizada.getGolsFora());
        partida.setData(partidaAtualizada.getData());

        repository.persist(partida);
        return Response.ok(partida).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Partida partida = repository.findById(id);
        if (partida == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        repository.delete(partida);
        return Response.noContent().build();
    }
}
