package org.example.business.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.business.gateway.JuegoDomainEventRepository;
import org.example.domain.Juego;
import org.example.domain.command.FinalizarRondaCommand;
import org.example.domain.values.JuegoId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FinalizarRondaUseCase extends UseCaseForCommand<FinalizarRondaCommand> {
private final JuegoDomainEventRepository repository;

  public FinalizarRondaUseCase(JuegoDomainEventRepository repository) {
    this.repository = repository;
  }

  @Override
  public Flux<DomainEvent> apply(Mono<FinalizarRondaCommand> finalizarRondaCommandMono) {
    return finalizarRondaCommandMono.flatMapMany(comando->
        repository.obtenerEventosPor(comando.getJuegoId())
            .collectList()
            .flatMapIterable(event->{
              var juego = Juego.from(JuegoId.of(comando.getJuegoId()),event);

              return juego.getUncommittedChanges();
            })

        );
  }
}
