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
import org.senac.entity.Time;
import org.senac.repository.TimeRepository;

import java.net.URI;
import java.util.List;

@Path("/times")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Times", description = "Operações relacionadas aos times de futebol")
public class TimeResource {

    @Inject
    TimeRepository repository;

    @GET
    @Operation(summary = "Listar times", description = "Retorna a lista de todos os times cadastrados, incluindo seus jogadores.")
    @APIResponse(responseCode = "200", description = "Lista de times",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(type = SchemaType.ARRAY, implementation = Time.class)))
    public List<Time> listAll() {
        // Atenção: Retornar a entidade Time diretamente pode causar problema de N+1 selects
        // para buscar os jogadores de cada time. Considere usar fetch join na query
        // ou DTOs com projeções se a performance for crítica.
        return repository.listAll();
    }

    @GET // Adicionado GET por ID
    @Path("/{id}")
    @Operation(summary = "Buscar time por ID", description = "Retorna os dados de um time específico, incluindo seus jogadores.")
    @APIResponse(responseCode = "200", description = "Time encontrado",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Time.class)))
    @APIResponse(responseCode = "404", description = "Time não encontrado para o ID informado.")
    public Response getById(
            @Parameter(description = "ID do time a ser buscado", required = true, example = "1")
            @PathParam("id") Long id) {
        Time time = repository.findById(id);
        // Novamente, atenção ao N+1 para buscar jogadores.
        return time != null ? Response.ok(time).build() : Response.status(Response.Status.NOT_FOUND).build();
    }


    @POST
    @Transactional
    @Operation(summary = "Adicionar novo time", description = "Cria um novo time de futebol.")
    @APIResponse(responseCode = "201", description = "Time criado com sucesso",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Time.class)))
    @APIResponse(responseCode = "400", description = "Dados inválidos para o time.")
    public Response add(
            @RequestBody(description = "Dados do novo time. O ID e a lista de jogadores são ignorados.",
                         required = true,
                         content = @Content(schema = @Schema(implementation = Time.class))) // Exemplo usa entidade, idealmente seria um TimeCriacaoDTO
            Time time) {
        // Garante que não está tentando definir ID ou jogadores na criação
        time.setId(null);
        time.setJogadores(null); // Jogadores devem ser adicionados separadamente

        repository.persist(time);
        return Response.created(URI.create("/times/" + time.getId())).entity(time).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar time existente", description = "Atualiza o nome e a cidade de um time existente.")
    @APIResponse(responseCode = "200", description = "Time atualizado com sucesso",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Time.class)))
    @APIResponse(responseCode = "404", description = "Time não encontrado para o ID informado.")
    @APIResponse(responseCode = "400", description = "Dados inválidos para atualização.")
    public Response update(
            @Parameter(description = "ID do time a ser atualizado", required = true, example = "1")
            @PathParam("id") Long id,
            @RequestBody(description = "Novos dados para o time (nome e cidade). ID e jogadores no corpo são ignorados.",
                         required = true,
                         content = @Content(schema = @Schema(implementation = Time.class))) // Exemplo usa entidade, idealmente seria um TimeAtualizacaoDTO
            Time timeAtualizado) {
        Time time = repository.findById(id);
        if (time == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // Atualiza apenas os campos permitidos
        time.setNome(timeAtualizado.getNome());
        time.setCidade(timeAtualizado.getCidade());
        // Não atualizamos a lista de jogadores aqui, isso deve ser feito via /jogadores provavelmente

        // repository.persist(time); // Não estritamente necessário
        return Response.ok(time).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Excluir time", description = "Exclui um time pelo seu ID. Associar jogadores também serão excluídos (cascade).")
    @APIResponse(responseCode = "204", description = "Time excluído com sucesso.")
    @APIResponse(responseCode = "404", description = "Time não encontrado para o ID informado.")
    public Response delete(
             @Parameter(description = "ID do time a ser excluído", required = true, example = "1")
             @PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id); // Usar deleteById é mais idiomático com Panache
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}