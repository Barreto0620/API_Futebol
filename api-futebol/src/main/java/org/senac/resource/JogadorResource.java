// api-futebol\src\main\java\org\senac\resource\JogadorResource.java
package org.senac.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.senac.entity.Jogador;
import org.senac.entity.Time;
import org.senac.repository.JogadorRepository;
import org.senac.repository.TimeRepository;
import org.senac.idempotency.Idempotent;

import java.net.URI;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Jogadores", description = "Operações relacionadas aos jogadores")
public class JogadorResource {

    @Inject
    JogadorRepository repository;
    @Inject
    TimeRepository timeRepository;

    @GET
    @Operation(summary = "Listar jogadores", description = "Retorna a lista de todos os jogadores cadastrados.")
    @APIResponse(responseCode = "200", description = "Lista de jogadores",
                     content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                         schema = @Schema(type = SchemaType.ARRAY, implementation = Jogador.class)))
    public List<Jogador> listAll() {
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar jogador por ID", description = "Retorna os dados de um jogador específico.")
    @APIResponse(responseCode = "200", description = "Jogador encontrado",
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Jogador.class)))
    @APIResponse(responseCode = "404", description = "Jogador não encontrado para o ID informado.")
    public Response getById(
            @Parameter(description = "ID do jogador a ser buscado", required = true, example = "1")
            @PathParam("id") Long id) {
        Jogador jogador = repository.findById(id);
        return jogador != null ? Response.ok(jogador).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Transactional
    @Idempotent(expireAfter = 7200) // Exemplo: 2 horas de expiração para criação de jogador
    @Operation(summary = "Adicionar novo jogador", description = "Cria um novo jogador e o associa a um time existente.")
    @APIResponse(responseCode = "201", description = "Jogador criado com sucesso",
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Jogador.class)))
    @APIResponse(responseCode = "400", description = "Dados inválidos para o jogador (Ex: time_id não existe ou é nulo).")
    public Response add(
             @RequestBody(description = "Dados do novo jogador. O ID é ignorado. O 'time' deve conter pelo menos o 'id' de um time existente.",
                           required = true,
                           content = @Content(schema = @Schema(implementation = Jogador.class)))
             Jogador jogador) {

        if (jogador.getTime() == null || jogador.getTime().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O ID do time é obrigatório.").build();
        }

        Time time = timeRepository.findById(jogador.getTime().getId());
        if (time == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Time com ID " + jogador.getTime().getId() + " não encontrado.").build();
        }

        jogador.setId(null); // Esta linha já garante que o ID será nulo para a autogeração.
        jogador.setTime(time);

        repository.persist(jogador);
        return Response.created(URI.create("/jogadores/" + jogador.getId())).entity(jogador).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Idempotent // Usa o padrão de 1 hora de expiração
    @Operation(summary = "Atualizar jogador existente", description = "Atualiza nome, idade e/ou time de um jogador.")
    @APIResponse(responseCode = "200", description = "Jogador atualizado com sucesso",
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Jogador.class)))
    @APIResponse(responseCode = "404", description = "Jogador não encontrado para o ID informado.")
    @APIResponse(responseCode = "400", description = "Dados inválidos para atualização (Ex: time_id não existe).")
    public Response update(
            @Parameter(description = "ID do jogador a ser atualizado", required = true, example = "1")
            @PathParam("id") Long id,
            @RequestBody(description = "Dados atualizados do jogador. O ID no corpo é ignorado. O 'time' deve conter o 'id' de um time existente se for alterado.",
                           required = true,
                           content = @Content(schema = @Schema(implementation = Jogador.class)))
            Jogador jogadorAtualizado) {

        Jogador existingJogador = repository.findById(id);
        if (existingJogador == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Se um novo time for fornecido e tiver um ID válido, tente atualizá-lo.
        // Caso contrário (se 'time' não estiver no JSON ou 'id' for nulo),
        // o time existente do jogador será mantido.
        if (jogadorAtualizado.getTime() != null && jogadorAtualizado.getTime().getId() != null) {
            Time novoTime = timeRepository.findById(jogadorAtualizado.getTime().getId());
            if (novoTime == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Time com ID " + jogadorAtualizado.getTime().getId() + " não encontrado.").build();
            }
            existingJogador.setTime(novoTime);
        }

        // Atualiza nome e idade apenas se forem fornecidos no payload
        if (jogadorAtualizado.getNome() != null) {
            existingJogador.setNome(jogadorAtualizado.getNome());
        }
        if (jogadorAtualizado.getIdade() != null) {
            existingJogador.setIdade(jogadorAtualizado.getIdade());
        }

        // As modificações na entidade gerenciada pelo Panache são salvas automaticamente
        // ao final da transação (@Transactional).
        return Response.ok(existingJogador).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Idempotent // Torna a operação DELETE idempotente
    @Operation(summary = "Excluir jogador", description = "Exclui um jogador pelo seu ID.")
    @APIResponse(responseCode = "204", description = "Jogador excluído com sucesso.")
    @APIResponse(responseCode = "404", description = "Jogador não encontrado para o ID informado.")
    public Response delete(
             @Parameter(description = "ID do jogador a ser excluído", required = true, example = "1")
             @PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}