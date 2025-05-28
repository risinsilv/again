package edu.curtin.app.observer;

import edu.curtin.app.model.Railway;
/**
 * ============================================================
 * | Class Name  : RailwayObserver                           |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : Observer for railway updates.             |
 * ============================================================
 */
public class RailwayObserver implements Observer<Railway> {
    @Override
    public void update(Railway railway) {
        System.out.println("[LOG] Railway updated between: " + railway.getTownA().getName() + " and " + railway.getTownB().getName());
    }
}
