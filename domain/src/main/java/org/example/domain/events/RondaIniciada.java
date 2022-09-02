package org.example.domain.events;

import co.com.sofka.domain.generic.DomainEvent;

public class RondaIniciada extends DomainEvent {
    public RondaIniciada() {
        super("cardgame.rondainiciada");
    }
}
