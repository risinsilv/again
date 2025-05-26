package edu.curtin.app.state;

public class DualTrackUnderConstruction implements RailwayState {
    @Override
    public boolean canTransport() { return true; } // single track still works
    @Override
    public boolean isSingle() { return false; }
    @Override
    public boolean isDual() { return true; }
    @Override
    public String getDotStyle() { return "[style=\"dashed\",color=\"black:black\"]"; }
}
