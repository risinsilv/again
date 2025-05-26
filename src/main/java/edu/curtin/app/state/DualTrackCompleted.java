package edu.curtin.app.state;

public class DualTrackCompleted implements RailwayState {
    @Override
    public boolean canTransport() { return true; }
    @Override
    public boolean isSingle() { return false; }
    @Override
    public boolean isDual() { return true; }
    @Override
    public String getDotStyle() { return "[color=\"black:black\"]"; }
}