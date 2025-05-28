package edu.curtin.app.state;
/**
 * ============================================================
 * | Class Name  : SingleTrackUnderConstruction              |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : State for single-track railways under     |
 * |               construction.                             |
 * ============================================================
 */
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
