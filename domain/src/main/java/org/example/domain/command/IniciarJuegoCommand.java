package org.example.domain.command;

import co.com.sofka.domain.generic.Command;
import org.example.domain.values.JuegoId;

public class IniciarJuegoCommand extends Command {
    private String juegoId;

    public String getJuegoId() {
        return juegoId;
    }

    public void setJuegoId(String juegoId) {
        this.juegoId = juegoId;
    }
}
