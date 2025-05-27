package edu.curtin.app.exception;

public class SimulationException extends Exception {
    public SimulationException(String message) {
        super(message);
    }
    public SimulationException(String message, Throwable cause) {
        super(message, cause);
    }
}
