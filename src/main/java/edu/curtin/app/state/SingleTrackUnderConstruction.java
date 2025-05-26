package edu.curtin.app.state;

public class SingleTrackUnderConstruction implements RailwayState {
    @Override
    public boolean canTransport() { return false; }
    @Override
    public boolean isSingle() { return true; }
    @Override
    public boolean isDual() { return false; }
    @Override
    public String getDotStyle() { return "[style=\"dashed\"]"; }
}
