package edu.curtin.app.state;

public interface RailwayState {
    boolean canTransport();
    boolean isSingle();
    boolean isDual();
    String getDotStyle();
}
