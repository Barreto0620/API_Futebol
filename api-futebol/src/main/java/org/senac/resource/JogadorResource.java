package org.senac.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.senac.entity.Jogador;
import org.senac.repository.JogadorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Path("/jogadores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Jogadores", description = "Operações relacionadas aos jogadores")
public class JogadorResource {

    @Inject
    JogadorRepository repository;

    @GET
    @Operation(summary = "Listar jogadores", description = "Retorna a lista de todos os jogadores")
    public List<Jogador> listAll() {
        return repository.listAll();
    }

    @POST
    @Transactional
    @Operation(summary = "Adicionar jogador", description = "Cria um novo jogador")
    public Response add(Jogador jogador) {
        repository.persist(jogador);
        return Response.status(Response.Status.CREATED).entity(jogador).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar jogador", description = "Atualiza os dados de um jogador existente")
    public Response update(@PathParam("id") Long id, Jogador jogador) {
        Jogador existingJogador = repository.findById(id);
        if (existingJogador == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        existingJogador.setNome(jogador.getNome());
        existingJogador.setIdade(jogador.getIdade());
        existingJogador.setTime(jogador.getTime());
        repository.persist(existingJogador);
        return Response.ok(existingJogador).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Excluir jogador", description = "Exclui um jogador pelo seu ID")
    public Response delete(@PathParam("id") Long id) {
        Jogador jogador = repository.findById(id);
        if (jogador == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        repository.delete(jogador);
        return Response.noContent().build();
    }
}
