package org.senac.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.senac.entity.Jogador;
import org.senac.repository.JogadorRepository;

import java.util.List;

@Path("/jogadores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JogadorResource {

    @Inject
    JogadorRepository repository;

    @GET
    public List<Jogador> listAll() {
        return repository.listAll();
    }

    @POST
    public Response add(Jogador jogador) {
        repository.persist(jogador);
        return Response.status(Response.Status.CREATED).entity(jogador).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Jogador jogador) {
        Jogador existingJogador = repository.findById(id);
        if (existingJogador == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        existingJogador.setNome(jogador.getNome());
        existingJogador.setIdade(jogador.getIdade());
        existingJogador.setTime(jogador.getTime()); // Correção

        repository.persist(existingJogador);
        return Response.ok(existingJogador).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        Jogador jogador = repository.findById(id);
        if (jogador == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        repository.delete(jogador);
        return Response.noContent().build();
    }
}
