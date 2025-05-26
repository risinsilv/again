package edu.curtin.app.model;

import edu.curtin.app.state.RailwayState;

public class Railway {
    private final Town townA;
    private final Town townB;
    private RailwayState state;
    private int constructionDaysRemaining;
    private boolean directionAB; // true: A->B, false: B->A

    public Railway(Town a, Town b, RailwayState state, int constructionDays) {
        this.townA = a;
        this.townB = b;
        this.state = state;
        this.constructionDaysRemaining = constructionDays;
        this.directionAB = true;
    }

    public Town getTownA() { return townA; }
    public Town getTownB() { return townB; }
    public RailwayState getState() { return state; }
    public void setState(RailwayState state) { this.state = state; }
    public int getConstructionDaysRemaining() { return constructionDaysRemaining; }
    public void decrementConstructionDays() { if (constructionDaysRemaining > 0) constructionDaysRemaining--; }
    public boolean isUnderConstruction() { return constructionDaysRemaining > 0; }
    public boolean isDualTrack() { return state.isDual(); }
    public boolean isSingleTrack() { return state.isSingle(); }
    public boolean canTransport() { return state.canTransport(); }
    public boolean isDirectionAB() { return directionAB; }
    public void flipDirection() { directionAB = !directionAB; }
}