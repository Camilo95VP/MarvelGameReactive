package org.example.business.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.business.gateway.JuegoDomainEventRepository;
import org.example.domain.Juego;
import org.example.domain.events.RondaTerminada;
import org.example.domain.values.JuegoId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class DeterminarGanadorUseCase extends UseCaseForEvent<RondaTerminada> {
    private final JuegoDomainEventRepository repository;

    public DeterminarGanadorUseCase(JuegoDomainEventRepository repository){
        this.repository = repository;
    }


    @Override
    public Flux<DomainEvent> apply(Mono<RondaTerminada> rondaTerminadaMono) {
        return rondaTerminadaMono.flatMapMany((event) -> repository
                .obtenerEventosPor(event.aggregateRootId())
                .collectList()
                .flatMapIterable(events -> {
                    var juego = Juego.from(JuegoId.of(event.aggregateRootId()), events);
                    var jugadoresVivos = juego.jugadores().values().stream()
                            .filter(jugador -> jugador.mazo().value().cantidad() > 0)
                            .collect(Collectors.toList());
                    if(jugadoresVivos.size()  == 1){
                        var jugador = jugadoresVivos.get(0);
                        juego.finalizarJuego(jugador.identity(), jugador.alias());
                    } else if(event.getJugadorIds().size() == 1){
                        event.getJugadorIds().stream().findFirst()
                                .flatMap(jugadorId -> jugadoresVivos.stream()
                                .filter(jugador -> jugador.identity().value().equals(jugadorId.value()))
                                .findFirst()).ifPresent(jugador -> juego.finalizarJuego(jugador.identity(), jugador.alias()));
                    }
                    return juego.getUncommittedChanges();
                }));
    }
}