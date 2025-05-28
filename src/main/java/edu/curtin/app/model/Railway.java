package edu.curtin.app.model;

import edu.curtin.app.observer.Observer;
import edu.curtin.app.observer.Subject;
import edu.curtin.app.state.DualTrackCompleted;
import edu.curtin.app.state.DualTrackUnderConstruction;
import edu.curtin.app.state.RailwayState;
import edu.curtin.app.state.SingleTrackCompleted;

import java.util.ArrayList;
import java.util.List;
/**
 * ============================================================
 * | Class Name  : Railway                                   |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : Represents a railway between two towns,   |
 * |               tracks construction state, direction, and |
 * |               supports observer notifications.          |
 * ============================================================
 */
public class Railway implements Subject<Railway> {
    private final Town townA;
    private final Town townB;
    private RailwayState state;
    private int constructionDaysRemaining;
    private boolean directionAB; // true: A->B, false: B->A
    private final List<Observer<Railway>> observers = new ArrayList<>();

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
    public void decrementConstructionDays() { if (constructionDaysRemaining > 0) {constructionDaysRemaining--;} }
    public boolean isUnderConstruction() { return constructionDaysRemaining > 0; }
//    public boolean isDualTrack() { return state.isDual(); }
//    public boolean isSingleTrack() { return state.isSingle(); }
    public boolean canTransport() { return state.canTransport(); }
    public boolean isDirectionAB() { return directionAB; }
    public void flipDirection() { directionAB = !directionAB; }
    public boolean isSingleTrack() {
        return state instanceof SingleTrackCompleted || state instanceof DualTrackUnderConstruction;
    }

    public boolean isDualTrack() {
        return state instanceof DualTrackCompleted;
    }
    public void setConstructionDaysRemaining(int days) {
        this.constructionDaysRemaining = days;
        notifyObservers();
    }

    @Override
    public void addObserver(Observer<Railway> o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer<Railway> o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer<Railway> o : observers){
            o.update(this);
        }
    }
}