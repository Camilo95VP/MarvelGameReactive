package org.example.business.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.business.gateway.JuegoDomainEventRepository;
import org.example.domain.command.PonerCartaEnTablero;
import org.example.domain.events.CartaPuestaEnTablero;
import org.example.domain.events.CartaQuitadaDelMazo;
import org.example.domain.events.JuegoCreado;
import org.example.domain.events.JugadorAgregado;
import org.example.domain.events.RondaCreada;
import org.example.domain.events.RondaIniciada;
import org.example.domain.events.TableroCreado;
import org.example.domain.values.Carta;
import org.example.domain.values.CartaMaestraId;
import org.example.domain.values.JugadorId;
import org.example.domain.values.Mazo;
import org.example.domain.values.Ronda;
import org.example.domain.values.TableroId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PonerCartaEnTableroUseCaseTest {

    @Mock
    private JuegoDomainEventRepository repository;

    @InjectMocks
    private PonerCartaEnTableroUseCase useCase;

    @Test
    void ponerCarta() {
        //arrange
        var command = new PonerCartaEnTablero();
        command.setCartaId("xxxxx");
        command.setJuegoId("fffff");
        command.setJugadorId("yyyyy");
        when(repository.obtenerEventosPor("fffff")).thenReturn(history());
        //act
        StepVerifier.create(useCase.apply(Mono.just(command)))
                .expectNextMatches(domainEvent -> {
                    var event = (CartaPuestaEnTablero) domainEvent;
                    Assertions.assertEquals("yyyyy", event.getJugadorId().value());
                    return "xxxxx".equals(event.getCarta().value().cartaId().value());
                })
                .expectNextMatches(domainEvent -> {
                    var event = (CartaQuitadaDelMazo) domainEvent;
                    Assertions.assertEquals("yyyyy", event.getJugadorId().value());
                    return "xxxxx".equals(event.getCarta().value().cartaId().value());                    })
                .expectComplete()
                .verify();
    }

    private Flux<DomainEvent> history() {
        var jugadorId = JugadorId.of("yyyyy");
        var jugador2Id = JugadorId.of("hhhhhh");
        var cartas = Set.of(new Carta(
                CartaMaestraId.of("xxxxx"),
                20,
                false, true
        ));
        var ronda = new Ronda(1, Set.of(jugadorId, jugador2Id));
        return Flux.just(
                new JuegoCreado(jugadorId),
                new JugadorAgregado(jugadorId, "raul", new Mazo(cartas)),
                new TableroCreado(new TableroId(), Set.of(jugadorId, jugador2Id)),
                new RondaCreada(ronda, 30),
                new RondaIniciada()
        );
    }

}