package edu.curtin.app.state;
/**
 * ============================================================
 * | Class Name  : DualTrackUnderConstruction                |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : State for dual-track railways under       |
 * |               construction.                             |
 * ============================================================
 */

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
