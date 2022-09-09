package org.example.business.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.business.gateway.JuegoDomainEventRepository;
import org.example.domain.Juego;
import org.example.domain.command.IniciarRondaCommand;
import org.example.domain.values.JuegoId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class
IniciarRondaUseCase extends UseCaseForCommand<IniciarRondaCommand> {

  private final JuegoDomainEventRepository repository;

  public IniciarRondaUseCase(JuegoDomainEventRepository repository) {
    this.repository = repository;
  }

  @Override
  public Flux<DomainEvent> apply(Mono<IniciarRondaCommand> iniciarRondaCommand) {
    return iniciarRondaCommand.flatMapMany(comando -> repository.obtenerEventosPor(
        comando.getJuegoId()).collectList().flatMapIterable(evento -> {
      var juego = Juego.from(JuegoId.of(comando.getJuegoId()), evento);
      juego.iniciarRonda();
      return juego.getUncommittedChanges();
    }));
  }
}
