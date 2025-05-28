package edu.curtin.app.exception;
/**
 * ============================================================
 * | Class Name  : SimulationException                       |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : Custom exception for simulation errors.   |
 * ============================================================
 */
public class SimulationException extends Exception {
    public SimulationException(String message) {
        super(message);
    }
    public SimulationException(String message, Throwable cause) {
        super(message, cause);
    }
}
