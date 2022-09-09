package org.example.business.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.business.gateway.JuegoDomainEventRepository;
import org.example.domain.Juego;
import org.example.domain.command.CrearRondaCommand;
import org.example.domain.values.JuegoId;
import org.example.domain.values.JugadorId;
import org.example.domain.values.Ronda;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class CrearRondaUseCase extends UseCaseForCommand<CrearRondaCommand> {

  private final JuegoDomainEventRepository repository;

  public CrearRondaUseCase(JuegoDomainEventRepository repository) {
    this.repository = repository;
  }

  @Override
  public Flux<DomainEvent> apply(Mono<CrearRondaCommand> crearRondaCommandMono) {
    return crearRondaCommandMono.flatMapMany((comando) ->
        repository
            .obtenerEventosPor(comando.getJuegoId()).collectList()
            .flatMapIterable(event -> {
              var juego = Juego.from(JuegoId.of(comando.getJuegoId()), event);
              var jugadores = comando.getJugadores().stream()
                  .map(JugadorId::of)
                  .collect(Collectors.toSet());
              var ronda = new Ronda(1, jugadores);
              juego.crearRonda(ronda, comando.getTiempo());

              return juego.getUncommittedChanges();

            })
    );
  }
}
