package edu.curtin.app.observer;

import edu.curtin.app.model.Railway;

public class RailwayObserver implements Observer<Railway> {
    @Override
    public void update(Railway railway) {
        System.out.println("[LOG] Railway updated between: " + railway.getTownA().getName() + " and " + railway.getTownB().getName());
    }
}
