package edu.curtin.app.state;

public class SingleTrackCompleted implements RailwayState {
    @Override
    public boolean canTransport() { return true; }
    @Override
    public boolean isSingle() { return true; }
    @Override
    public boolean isDual() { return false; }
    @Override
    public String getDotStyle() { return ""; }
}
