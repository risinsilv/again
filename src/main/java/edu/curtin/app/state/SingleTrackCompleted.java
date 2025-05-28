package edu.curtin.app.state;
/**
 * ============================================================
 * | Class Name  : SingleTrackCompleted                      |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : State for completed single-track railways.|
 * ============================================================
 */
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
