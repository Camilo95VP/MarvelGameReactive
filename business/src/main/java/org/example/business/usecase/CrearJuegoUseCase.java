package org.example.business.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.business.gateway.ListaDeCartaService;
import org.example.business.gateway.model.CartaMaestra;
import org.example.domain.Juego;
import org.example.domain.JugadorFactory;
import org.example.domain.command.CrearJuegoCommand;
import org.example.domain.values.Carta;
import org.example.domain.values.CartaMaestraId;
import org.example.domain.values.JuegoId;
import org.example.domain.values.JugadorId;
import org.example.domain.values.Mazo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrearJuegoUseCase implements Function<Mono<CrearJuegoCommand>, Flux<DomainEvent>> {

    private final ListaDeCartaService listaDeCartaService;

    public CrearJuegoUseCase(ListaDeCartaService listaDeCartaService) {
        this.listaDeCartaService = listaDeCartaService;
    }

    @Override
    public Flux<DomainEvent> apply(Mono<CrearJuegoCommand> input) {
        return listaDeCartaService.obtenerCartasDeMarvel().collectList()
                .flatMapMany(cartas -> input.flatMapIterable(command -> {

                    //TODO: validaciones del comando
                    var factory = new JugadorFactory();
                    command.getJugadores()
                            .forEach((id, alias) ->
                                    factory.agregarJugador(JugadorId.of(id), alias, generarMazo(cartas))
                            );
                    var juego = new Juego(
                            JuegoId.of(command.getJuegoId()),
                            JugadorId.of(command.getJugadorPrincipalId()),
                            factory
                    );
                    return juego.getUncommittedChanges();
                }));

    }




    private Mazo generarMazo(List<CartaMaestra> cartas) {
        Collections.shuffle(cartas);
        var lista = cartas.stream().limit(5)
                .map(carta -> new Carta(CartaMaestraId.of(carta.getId()), carta.getPoder(), false, true))
                .collect(Collectors.toList());
        cartas.removeIf(cartaMaestra -> lista.stream().anyMatch(carta -> {
            var id = carta.value().cartaId().value();
            return cartaMaestra.getId().equals(id);
        }));
        return new Mazo(new HashSet<>(lista));
    }
}
