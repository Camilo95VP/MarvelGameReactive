package org.example.domain;

import co.com.sofka.domain.generic.EventChange;
import org.example.domain.events.CartaPuestaEnTablero;
import org.example.domain.events.CartaQuitadaDelMazo;
import org.example.domain.events.CartaQuitadaDelTablero;
import org.example.domain.events.CartasAsignadasAJugador;
import org.example.domain.events.JuegoCreado;
import org.example.domain.events.JuegoFinalizado;
import org.example.domain.events.JugadorAgregado;
import org.example.domain.events.RondaCreada;
import org.example.domain.events.RondaIniciada;
import org.example.domain.events.RondaTerminada;
import org.example.domain.events.TableroCreado;
import org.example.domain.events.TiempoCambiadoDelTablero;

import java.util.HashMap;
import java.util.Objects;

public class JuegoEventChange extends EventChange {
    public JuegoEventChange(Juego juego) {
        apply((JuegoCreado event) -> {
            juego.jugadores = new HashMap<>();
            juego.jugadorPrincipal = event.getJugadorPrincipal();
        });
        apply((JugadorAgregado event) -> {
            juego.jugadores.put(event.getJuegoId(),
                    new Jugador(event.getJuegoId(), event.getAlias(), event.getMazo())
            );
        });

        apply((RondaCreada event) -> {
            if(Objects.isNull(juego.tablero)){
                throw new IllegalArgumentException("Debe existir el tablero primero");
            }
            juego.ronda = event.getRonda();
            juego.tablero.ajustarTiempo(event.getTiempo());
        });

        apply((TableroCreado event) -> {
            juego.tablero = new Tablero(event.getTableroId(), event.getJugadorIds());
        });

        apply((TiempoCambiadoDelTablero event) -> {
            juego.tablero.ajustarTiempo(event.getTiempo());
        });

        apply((CartaPuestaEnTablero event) -> {
            if(Boolean.FALSE.equals(juego.tablero.estaHabilitado())){
                throw new IllegalArgumentException("No se puede jugar por que el tablero no esta habilitado");
            }
            juego.tablero.adicionarPartida(event.getJugadorId(), event.getCarta());
        });

        apply((CartaQuitadaDelTablero event) -> {
            juego.tablero.quitarCarta(event.getJugadorId(), event.getCarta());
        });

        apply((CartaQuitadaDelMazo event) -> {
            juego.jugadores.get(event.getJugadorId()).quitarCartaDeMazo(event.getCarta());
        });

        apply((RondaIniciada event) -> {
            juego.ronda = juego.ronda.iniciarRonda();
            juego.tablero.habilitarApuesta();
        });

        apply((RondaTerminada event) -> {
            juego.ronda = juego.ronda.terminarRonda();
            juego.tablero.inhabilitarApuesta();
        });

        apply((CartasAsignadasAJugador event) -> {
            var jugador =  juego.jugadores().get(event.getGanadorId());
            event.getCartasApuesta().forEach(jugador::agregarCartaAMazo);
        });

        apply((JuegoFinalizado event) -> {
            juego.ganador = juego.jugadores.get(event.getJugadorId());
        });

    }
}
