package org.example.application.handle.usecase;

import org.example.application.handle.IntegrationHandle;
import org.example.business.usecase.IniciarCuentaRegresivaUseCase;
import org.example.domain.events.RondaIniciada;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Mono;



@Configuration
public class CuentaRegresivaEventHandle {

    private final IniciarCuentaRegresivaUseCase usecase;

    private final IntegrationHandle handle;

    public CuentaRegresivaEventHandle(IniciarCuentaRegresivaUseCase usecase, IntegrationHandle handle) {
        this.usecase = usecase;
        this.handle = handle;
    }

    @EventListener
    public void handleIniciarCuentaRegresiva(RondaIniciada event) {
        usecase.andThen(handle).apply(Mono.just(event)).block();
    }


}