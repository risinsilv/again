package edu.curtin.app.state;
/**
 * ============================================================
 * | Class Name  : RailwayState                              |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : Interface for railway state behaviors.    |
 * ============================================================
 */
public interface RailwayState {
    boolean canTransport();
    boolean isSingle();
    boolean isDual();
    String getDotStyle();
}
