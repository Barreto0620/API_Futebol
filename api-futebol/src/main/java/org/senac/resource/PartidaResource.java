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
import org.senac.entity.Destaque;
import org.senac.entity.Jogador;
import org.senac.entity.Partida;
import org.senac.entity.Time;
import org.senac.repository.DestaqueRepository;
import org.senac.repository.JogadorRepository;
import org.senac.repository.PartidaRepository;
import org.senac.repository.TimeRepository; // IMPORTAÇÃO ADICIONADA
import org.senac.idempotency.Idempotent;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

@Path("/partidas") // <--- ANOTAÇÃO @Path ADICIONADA
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Partidas", description = "Operações relacionadas às partidas de futebol")
public class PartidaResource {

    @Inject
    PartidaRepository partidaRepository;
    @Inject
    JogadorRepository jogadorRepository;
    @Inject
    DestaqueRepository destaqueRepository;
    @Inject
    TimeRepository timeRepository; // <--- INJEÇÃO ADICIONADA

    @GET
    @Operation(summary = "Listar partidas", description = "Retorna a lista de todas as partidas cadastradas.")
    @APIResponse(responseCode = "200", description = "Lista de partidas",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                            schema = @Schema(type = SchemaType.ARRAY, implementation = Partida.class)))
    public List<Partida> listAll() {
        return partidaRepository.listAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar partida por ID", description = "Retorna os dados de uma partida específica pelo seu ID.")
    @APIResponse(responseCode = "200", description = "Partida encontrada",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Partida.class)))
    @APIResponse(responseCode = "404", description = "Partida não encontrada para o ID informado.")
    public Response get(
            @Parameter(description = "ID da partida a ser buscada", required = true, example = "1")
            @PathParam("id") Long id) {
        Optional<Partida> partidaOpt = partidaRepository.findByIdOptional(id);
        return partidaOpt.map(partida -> Response.ok(partida).build())
                                 .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    @Idempotent(expireAfter = 86400) // Exemplo: 24 horas de expiração para criação de partida
    @Operation(summary = "Criar nova partida", description = "Cria uma nova partida e tenta gerar um destaque automaticamente (com lógica simplificada).")
    @APIResponse(responseCode = "201", description = "Partida criada com sucesso (com URI no header Location)",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Partida.class)))
    @APIResponse(responseCode = "400", description = "Dados inválidos fornecidos para a partida (Ex: time não existe).")
    public Response create(
            @RequestBody(description = "Dados da nova partida. IDs dos times devem existir. O ID da partida e o destaque são ignorados/gerados.",
                          required = true,
                          content = @Content(schema = @Schema(implementation = Partida.class)))
            Partida partidaInput) {

        // Validação de times antes de persistir a partida
        if (partidaInput.getTimeCasa() == null || partidaInput.getTimeCasa().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O ID do time da casa é obrigatório.").build();
        }
        if (partidaInput.getTimeFora() == null || partidaInput.getTimeFora().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O ID do time de fora é obrigatório.").build();
        }

        // Buscar os times no banco de dados para garantir que existam e associar as entidades gerenciadas
        Time timeCasaDB = timeRepository.findById(partidaInput.getTimeCasa().getId());
        if (timeCasaDB == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Time da casa com ID " + partidaInput.getTimeCasa().getId() + " não encontrado.").build();
        }

        Time timeForaDB = timeRepository.findById(partidaInput.getTimeFora().getId());
        if (timeForaDB == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Time de fora com ID " + partidaInput.getTimeFora().getId() + " não encontrado.").build();
        }
        
        partidaInput.setTimeCasa(timeCasaDB); // <--- ATUALIZAÇÃO PARA USAR A ENTIDADE GERENCIADA
        partidaInput.setTimeFora(timeForaDB); // <--- ATUALIZAÇÃO PARA USAR A ENTIDADE GERENCIADA


        partidaInput.setId(null); // Garante que o ID é nulo para autogeração
        partidaInput.setDestaque(null); // Garante que o destaque não é definido no input

        partidaRepository.persist(partidaInput);

        Time vencedor = null;
        int maxGols = 0;
        if (partidaInput.getGolsCasa() > partidaInput.getGolsFora()) {
            vencedor = partidaInput.getTimeCasa();
            maxGols = partidaInput.getGolsCasa();
        } else if (partidaInput.getGolsFora() > partidaInput.getGolsCasa()) {
            vencedor = partidaInput.getTimeFora();
            maxGols = partidaInput.getGolsFora();
        }

        if (vencedor != null) { // A condição vencedor.getId() != null é implicita se vencedor não for nulo aqui, já que timeCasaDB/timeForaDB são encontrados
            List<Jogador> jogadores = jogadorRepository.list("time", vencedor);
            if (!jogadores.isEmpty()) {
                Jogador jogadorDestaque = jogadores.get(0);
                Destaque d = new Destaque();
                d.setPartida(partidaInput);
                d.setJogador(jogadorDestaque);
                d.setGolsMarcados(maxGols);
                destaqueRepository.persist(d);
                partidaInput.setDestaque(d);
            } else {
                System.err.println("Alerta: Time vencedor (ID: " + vencedor.getId() + ") não possui jogadores cadastrados. Destaque não gerado.");
            }
        } else { // Caso de empate
             System.out.println("Info: Partida (ID: " + partidaInput.getId() + ") resultou em empate. Destaque não gerado automaticamente.");
        }

        return Response.created(URI.create("/partidas/" + partidaInput.getId())).entity(partidaInput).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Idempotent // Usa o padrão de 1 hora de expiração
    @Operation(summary = "Atualizar partida existente", description = "Atualiza os dados de uma partida existente. O destaque NÃO é recalculado automaticamente aqui.")
    @APIResponse(responseCode = "200", description = "Partida atualizada com sucesso",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Partida.class)))
    @APIResponse(responseCode = "404", description = "Partida não encontrada para o ID informado.")
    @APIResponse(responseCode = "400", description = "Dados inválidos fornecidos para atualização.")
    public Response update(
            @Parameter(description = "ID da partida a ser atualizada", required = true, example = "1")
            @PathParam("id") Long id,
            @RequestBody(description = "Dados atualizados da partida. IDs dos times devem existir. O ID no corpo é ignorado.",
                          required = true,
                          content = @Content(schema = @Schema(implementation = Partida.class)))
            Partida dadosAtualizacao) {

        Partida partida = partidaRepository.findById(id);
        if (partida == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Validação e busca de times para atualização
        if (dadosAtualizacao.getTimeCasa() == null || dadosAtualizacao.getTimeCasa().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O ID do time da casa é obrigatório na atualização.").build();
        }
        Time newTimeCasa = timeRepository.findById(dadosAtualizacao.getTimeCasa().getId());
        if (newTimeCasa == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Time da casa com ID " + dadosAtualizacao.getTimeCasa().getId() + " não encontrado.").build();
        }
        partida.setTimeCasa(newTimeCasa); // <--- ATUALIZAÇÃO PARA USAR A ENTIDADE GERENCIADA

        if (dadosAtualizacao.getTimeFora() == null || dadosAtualizacao.getTimeFora().getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O ID do time de fora é obrigatório na atualização.").build();
        }
        Time newTimeFora = timeRepository.findById(dadosAtualizacao.getTimeFora().getId());
        if (newTimeFora == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Time de fora com ID " + dadosAtualizacao.getTimeFora().getId() + " não encontrado.").build();
        }
        partida.setTimeFora(newTimeFora); // <--- ATUALIZAÇÃO PARA USAR A ENTIDADE GERENCIADA


        partida.setGolsCasa(dadosAtualizacao.getGolsCasa());
        partida.setGolsFora(dadosAtualizacao.getGolsFora());
        partida.setData(dadosAtualizacao.getData());

        return Response.ok(partida).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Idempotent // Torna a operação DELETE idempotente
    @Operation(summary = "Excluir partida", description = "Exclui uma partida pelo seu ID. O destaque associado também será excluído (cascade).")
    @APIResponse(responseCode = "204", description = "Partida excluída com sucesso.")
    @APIResponse(responseCode = "404", description = "Partida não encontrada para o ID informado.")
    public Response delete(
             @Parameter(description = "ID da partida a ser excluída", required = true, example = "1")
             @PathParam("id") Long id) {
        boolean deleted = partidaRepository.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}