package org.senac.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.senac.entity.Time;
import org.senac.repository.TimeRepository;

import java.util.List;

@Path("/times")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeResource {

    @Inject
    TimeRepository repository;

    @GET
    public List<Time> listAll() {
        return repository.listAll();
    }

    @POST
    public Response add(Time time) {
        repository.persist(time);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Time timeAtualizado) {
        Time time = repository.findById(id);
        if (time == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        time.setNome(timeAtualizado.getNome());
        time.setCidade(timeAtualizado.getCidade());

        repository.persist(time);
        return Response.ok(time).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Time time = repository.findById(id);
        if (time == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        repository.delete(time);
        return Response.noContent().build();
    }
}
