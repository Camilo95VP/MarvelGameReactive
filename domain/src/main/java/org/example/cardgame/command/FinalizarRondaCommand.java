package org.example.cardgame.command;

import co.com.sofka.domain.generic.Command;

import java.util.Set;

public class FinalizarRondaCommand extends Command {
    private String juegoId;

    private Set<String> jugadoresSelecionados;

    private String jugadorPotenciado;

    public void setJugadoresSelecionados(Set<String> jugadoresSelecionados) {
        this.jugadoresSelecionados = jugadoresSelecionados;
    }

    public String getJuegoId() {
        return juegoId;
    }

    public Set<String> getJugadoresSelecionados() {
        return jugadoresSelecionados;
    }

    public String getJugadorPotenciado() {
        return jugadorPotenciado;
    }

    public void setJugadorPotenciado(String jugadorPotenciado) {
        this.jugadorPotenciado = jugadorPotenciado;
    }

    public void setJuegoId(String juegoId) {
        this.juegoId = juegoId;
    }
}
