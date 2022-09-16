package org.example.cardgame;

import co.com.sofka.domain.generic.EventChange;
import org.example.cardgame.events.CartaPuestaEnTablero;
import org.example.cardgame.events.CartaQuitadaDelMazo;
import org.example.cardgame.events.CartaQuitadaDelTablero;
import org.example.cardgame.events.CartasAsignadasAJugador;
import org.example.cardgame.events.JuegoCreado;
import org.example.cardgame.events.JuegoFinalizado;
import org.example.cardgame.events.JugadorAgregado;
import org.example.cardgame.events.RondaCreada;
import org.example.cardgame.events.RondaIniciada;
import org.example.cardgame.events.RondaTerminada;
import org.example.cardgame.events.TableroCreado;
import org.example.cardgame.events.TiempoCambiadoDelTablero;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JuegoEventChange extends EventChange {
    public JuegoEventChange(Juego juego) {
        apply((JuegoCreado event) -> {
            juego.jugadores = new HashMap<>();
            juego.jugadorPrincipal = event.getJugadorPrincipal();
        });
        apply((JugadorAgregado event) -> {
            juego.jugadores.put(event.getJugadorId(),
                    new Jugador(event.getJugadorId(), event.getAlias(), event.getMazo())
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
            AtomicInteger counter = new AtomicInteger();
            juego.tablero.partida().values().stream().forEach(c -> {
                if(c.size() > 0)  { counter.getAndIncrement(); }
            });
            if(counter.get() == 2){
                var r = new Random();
                var idJugador = juego.tablero.partida().keySet().stream().collect(Collectors.toList()).get(r.nextInt((1 - 0) + 1) + 0);
                juego.selecciocarJuador(idJugador.value());
            }
        });

        apply((CartaQuitadaDelTablero event) -> {
            juego.tablero.quitarCarta(event.getJugadorId(), event.getCarta());
        });

        apply((CartaQuitadaDelMazo event) -> {
            juego.jugadores.get(event.getJugadorId()).quitarCartaDeMazo(event.getCarta());
        });

        apply((RondaIniciada event) -> {
            if(Objects.isNull(juego.ronda)){
                throw new IllegalArgumentException("Debe existir una ronda creada");
            }
            juego.ronda = juego.ronda.iniciarRonda();
            juego.tablero.habilitarApuesta();
            juego.tablero.partida().forEach((key, Value) -> juego.tablero.partida().put(key,new HashSet<>()));
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
