package org.example.business.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.business.gateway.ListaDeCartaService;
import org.example.business.gateway.model.CartaMaestra;
import org.example.domain.command.CrearJuegoCommand;
import org.example.domain.events.JuegoCreado;
import org.example.domain.events.JugadorAgregado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.function.Predicate;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearJuegoUseCaseTest {

    @Mock
    private ListaDeCartaService listaDeCartasService;

    @InjectMocks
    private CrearJuegoUseCase useaCase;

    @Test
    void crearJuego(){
        //arrange

        var command = new CrearJuegoCommand();
        command.setJuegoId("XXX");
        command.setJugadores(new HashMap<>());
        command.getJugadores().put("123","Juan");
        command.getJugadores().put("456","Carlos");
        command.setJugadorPrincipalId("123");

        when(listaDeCartasService.obtenerCartasDeMarvel()).thenReturn(history());

        StepVerifier.create(useaCase.apply(Mono.just(command)))
                .expectNextMatches(new Predicate<DomainEvent>() {
                    @Override
                    public boolean test(DomainEvent domainEvent) {
                        var event = (JuegoCreado) domainEvent;
                        return "XXX".equals(event.aggregateRootId()) && "123".equals(event.getJugadorPrincipal().value());
                    }
                }).expectNextMatches(new Predicate<DomainEvent>() {
                    @Override
                    public boolean test(DomainEvent domainEvent) {
                        var event = (JugadorAgregado) domainEvent;
                        return "123".equals(event.getJugadorId().value())
                                && "Juan".equals(event.getAlias());
                    }
                }).expectNextMatches(new Predicate<DomainEvent>() {
                    @Override
                    public boolean test(DomainEvent domainEvent) {
                        var event = (JugadorAgregado) domainEvent;
                        return "456".equals(event.getJugadorId().value())
                                && "Carlos".equals(event.getAlias());
                    }
                }).expectComplete().verify();



    }

    private Flux<CartaMaestra> history() {
        return  Flux.just(
                new CartaMaestra("1","tarjeta1"),
                new CartaMaestra("2","tarjeta2"),
                new CartaMaestra("3","tarjeta2"),
                new CartaMaestra("4","tarjeta2"),
                new CartaMaestra("5","tarjeta2"),
                new CartaMaestra("6","tarjeta2"),
                new CartaMaestra("7","tarjeta2"),
                new CartaMaestra("8","tarjeta2"),
                new CartaMaestra("9","tarjeta2"),
                new CartaMaestra("10","tarjeta2")
        );
    }

}