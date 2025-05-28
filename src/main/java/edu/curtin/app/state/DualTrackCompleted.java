package edu.curtin.app.state;
/**
 * ============================================================
 * | Class Name  : DualTrackCompleted                        |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : State for completed dual-track railways.  |
 * ============================================================
 */
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