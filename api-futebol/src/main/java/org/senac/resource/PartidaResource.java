package org.senac.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.senac.entity.Partida;
import org.senac.entity.Time;
import org.senac.entity.Destaque;
import org.senac.entity.Jogador;
import org.senac.repository.PartidaRepository;
import org.senac.repository.JogadorRepository;
import org.senac.repository.DestaqueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional; // Usar Optional para clareza

@Path("/partidas")
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

    @GET
    @Operation(summary = "Listar partidas", description = "Retorna a lista de todas as partidas")
    public List<Partida> listAll() {
        return partidaRepository.listAll();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar partida por ID", description = "Retorna os dados de uma partida específica")
    public Response get(@PathParam("id") Long id) {
        // Usar findByIdOptional para tratamento mais claro de 'não encontrado'
        Optional<Partida> partidaOpt = partidaRepository.findByIdOptional(id);
        return partidaOpt.map(partida -> Response.ok(partida).build())
                       .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    @Operation(summary = "Criar partida", description = "Cria uma nova partida e tenta gerar um destaque (lógica simplificada)")
    public Response create(Partida partidaInput) {
        // Validações básicas podem ser adicionadas aqui (ex: times diferentes, data válida)

        // Persiste a partida primeiro para ter um ID
        partidaRepository.persist(partidaInput);

        // Lógica para tentar criar o destaque (SIMPLIFICADA - NÃO identifica o maior artilheiro)
        Time vencedor = null;
        int maxGols = 0;

        if (partidaInput.getGolsCasa() > partidaInput.getGolsFora()) {
            vencedor = partidaInput.getTimeCasa();
            maxGols = partidaInput.getGolsCasa();
        } else if (partidaInput.getGolsFora() > partidaInput.getGolsCasa()) {
            vencedor = partidaInput.getTimeFora();
            maxGols = partidaInput.getGolsFora();
        }
        // Se houve vencedor (não foi empate)
        if (vencedor != null) {
            // Busca jogadores do time vencedor
            List<Jogador> jogadores = jogadorRepository.list("time", vencedor);
            if (!jogadores.isEmpty()) {
                // ** LÓGICA SIMPLIFICADA: Seleciona o primeiro jogador encontrado do time vencedor **
                // ** Para uma lógica correta (maior artilheiro), seria necessário ter dados de gols individuais **
                Jogador jogadorDestaque = jogadores.get(0);

                // Cria e persiste o Destaque
                Destaque d = new Destaque();
                d.setPartida(partidaInput); // Associa o destaque à partida recém-criada
                d.setJogador(jogadorDestaque);
                d.setGolsMarcados(maxGols); // ** ATRIBUI O TOTAL DE GOLS DO TIME AO JOGADOR ** (Incorreto para destaque real)

                destaqueRepository.persist(d);
                // Associa o destaque criado de volta à partida (importante para a resposta JSON)
                partidaInput.setDestaque(d);

            } else {
                // Log ou tratamento caso o time vencedor não tenha jogadores cadastrados
                System.err.println("Alerta: Time vencedor (ID: " + vencedor.getId() + ") não possui jogadores cadastrados. Destaque não gerado.");
            }
        } else {
             // Log ou tratamento para empates
             System.out.println("Info: Partida (ID: " + partidaInput.getId() + ") resultou em empate. Destaque não gerado automaticamente.");
        }


        // Retorna a partida criada (agora potencialmente com o destaque associado)
        // O status CREATED geralmente retorna a localização do novo recurso no header Location
        // Aqui estamos retornando a entidade criada no corpo, o que também é comum.
        return Response.status(Response.Status.CREATED).entity(partidaInput).build();
    }


    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar partida", description = "Atualiza os dados de uma partida existente. O destaque NÃO é recalculado automaticamente aqui.")
    public Response update(@PathParam("id") Long id, Partida dadosAtualizacao) {
        Partida partida = partidaRepository.findById(id);
        if (partida == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Atualiza os campos da partida
        // Idealmente, validar se os times existem antes de setar
        partida.setTimeCasa(dadosAtualizacao.getTimeCasa());
        partida.setTimeFora(dadosAtualizacao.getTimeFora());
        partida.setGolsCasa(dadosAtualizacao.getGolsCasa());
        partida.setGolsFora(dadosAtualizacao.getGolsFora());
        partida.setData(dadosAtualizacao.getData());

        // ** Nota: A atualização do destaque não é feita automaticamente aqui. **
        // Se o resultado da partida mudar, o destaque existente pode ficar inconsistente.
        // Seria necessário adicionar lógica para remover/recalcular o destaque se desejado.

        // partidaRepository.persist(partida); // Não necessário em JTA se a entidade está gerenciada
        return Response.ok(partida).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Excluir partida", description = "Exclui uma partida pelo seu ID (e seu destaque associado, devido ao CascadeType.REMOVE)")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = partidaRepository.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
} 