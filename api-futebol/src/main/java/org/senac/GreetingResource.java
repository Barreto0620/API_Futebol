package org.senac;

import jakarta.ws.rs.Path;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.senac.resource.TimeResource;
import org.senac.resource.JogadorResource;
import org.senac.resource.PartidaResource;
import org.senac.resource.DestaqueResource;

@Path("/")
public class GreetingResource {

    @Path("times")
    @Operation(
        summary = "Rotas referentes aos times",
        description = "Essa rota é responsável por gerenciar os times cadastrados no sistema."
    )
    public Class<TimeResource> times() {
        return TimeResource.class;
    }

    @Path("jogadores")
    @Operation(
        summary = "Rotas referentes aos jogadores",
        description = "Essa rota é responsável por gerenciar os jogadores cadastrados no sistema."
    )
    public Class<JogadorResource> jogadores() {
        return JogadorResource.class;
    }

    @Path("partidas")
    @Operation(
        summary = "Rotas referentes às partidas",
        description = "Essa rota é responsável por gerenciar as partidas realizadas entre os times."
    )
    public Class<PartidaResource> partidas() {
        return PartidaResource.class;
    }

    @Path("destaques")
    @Operation(
        summary = "Rotas referentes aos destaques das partidas",
        description = "Essa rota é responsável por gerenciar os destaques de cada partida (jogadores que mais se destacaram)."
    )
    public Class<DestaqueResource> destaques() {
        return DestaqueResource.class;
    }
}
