package org.example.cardgame.events;

import co.com.sofka.domain.generic.DomainEvent;

public class JugadorSeleccionado extends DomainEvent {
    private final String jugadorSeleccionado;
    public JugadorSeleccionado(String jugadorSeleccionado) {
        super("cardgame.JugadorSeleccionado");
        this.jugadorSeleccionado = jugadorSeleccionado;
    }

    public String getJugadorSeleccionado() {
        return jugadorSeleccionado;
    }
}
