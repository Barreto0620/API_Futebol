package org.senac.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation; // Import correto para Operation
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag; // Import correto para Tag
import org.senac.entity.Destaque;
import org.senac.entity.Jogador;
import org.senac.entity.Partida;
import org.senac.entity.Time;
import org.senac.repository.DestaqueRepository;
import org.senac.repository.JogadorRepository;
import org.senac.repository.PartidaRepository;

import java.net.URI; // Para o header Location no POST
import java.util.List;
import java.util.Optional;

@Path("/partidas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Partidas", description = "Operações relacionadas às partidas de futebol")
public class PartidaResource {

    @Inject
    PartidaRepository partidaRepository;
    @Inject
    JogadorRepository jogadorRepository; // Necessário para lógica de criação do destaque
    @Inject
    DestaqueRepository destaqueRepository; // Necessário para lógica de criação do destaque

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
    @Operation(summary = "Criar nova partida", description = "Cria uma nova partida e tenta gerar um destaque automaticamente (com lógica simplificada).")
    @APIResponse(responseCode = "201", description = "Partida criada com sucesso (com URI no header Location)",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Partida.class)))
    @APIResponse(responseCode = "400", description = "Dados inválidos fornecidos para a partida (Ex: time não existe).") // Assumindo validações futuras
    public Response create(
            @RequestBody(description = "Dados da nova partida. IDs dos times devem existir. O ID da partida e o destaque são ignorados/gerados.",
                         required = true,
                         content = @Content(schema = @Schema(implementation = Partida.class)))
            Partida partidaInput) {

        // TODO: Adicionar validações (ex: timeCasa != timeFora, buscar Times pelo ID para garantir que existem)
        // Como estamos recebendo a entidade, o ID da partida e o Destaque são ignorados aqui, pois serão gerados.
        partidaInput.setId(null);
        partidaInput.setDestaque(null);

        partidaRepository.persist(partidaInput);

        // Lógica simplificada para criar destaque (como antes)
        Time vencedor = null;
        int maxGols = 0;
        if (partidaInput.getGolsCasa() > partidaInput.getGolsFora()) {
            vencedor = partidaInput.getTimeCasa(); // Assumindo que timeCasa foi persistido ou já existe
            maxGols = partidaInput.getGolsCasa();
        } else if (partidaInput.getGolsFora() > partidaInput.getGolsCasa()) {
            vencedor = partidaInput.getTimeFora(); // Assumindo que timeFora foi persistido ou já existe
            maxGols = partidaInput.getGolsFora();
        }

        if (vencedor != null && vencedor.getId() != null) { // Garante que o time vencedor é uma entidade válida
             List<Jogador> jogadores = jogadorRepository.list("time", vencedor);
             if (!jogadores.isEmpty()) {
                 Jogador jogadorDestaque = jogadores.get(0); // Lógica simplificada
                 Destaque d = new Destaque();
                 d.setPartida(partidaInput);
                 d.setJogador(jogadorDestaque);
                 d.setGolsMarcados(maxGols);
                 destaqueRepository.persist(d);
                 partidaInput.setDestaque(d); // Associa de volta para retornar no response
             } else {
                 System.err.println("Alerta: Time vencedor (ID: " + vencedor.getId() + ") não possui jogadores cadastrados. Destaque não gerado.");
             }
         } else if (vencedor == null) {
              System.out.println("Info: Partida (ID: " + partidaInput.getId() + ") resultou em empate. Destaque não gerado automaticamente.");
         } else {
              // Caso onde vencedor não é nulo, mas ID é nulo (não deveria acontecer se os times foram buscados/validados)
              System.err.println("Erro: Time vencedor inválido para partida ID: " + partidaInput.getId());
         }

        // Retorna 201 Created com o local do novo recurso e a entidade criada
        return Response.created(URI.create("/partidas/" + partidaInput.getId())).entity(partidaInput).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar partida existente", description = "Atualiza os dados de uma partida existente. O destaque NÃO é recalculado automaticamente aqui.")
    @APIResponse(responseCode = "200", description = "Partida atualizada com sucesso",
                 content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Partida.class)))
    @APIResponse(responseCode = "404", description = "Partida não encontrada para o ID informado.")
    @APIResponse(responseCode = "400", description = "Dados inválidos fornecidos para atualização.") // Assumindo validações futuras
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

        // TODO: Validar se os times em dadosAtualizacao existem no banco antes de setar

        // Atualiza os campos permitidos
        partida.setTimeCasa(dadosAtualizacao.getTimeCasa());
        partida.setTimeFora(dadosAtualizacao.getTimeFora());
        partida.setGolsCasa(dadosAtualizacao.getGolsCasa());
        partida.setGolsFora(dadosAtualizacao.getGolsFora());
        partida.setData(dadosAtualizacao.getData());
        // Nota: Não mexemos no ID nem no destaque aqui

        // O persist não é estritamente necessário com Panache em métodos @Transactional,
        // mas pode deixar mais explícito. Hibernate gerencia a entidade 'partida'.
        // partidaRepository.persist(partida);

        return Response.ok(partida).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
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