package org.example.business.gateway;


import org.example.business.gateway.model.CartaMaestra;
import reactor.core.publisher.Flux;


public interface ListaDeCartaService {
    Flux<CartaMaestra> obtenerCartasDeMarvel();
}
