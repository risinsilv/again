package edu.curtin.app.observer;

import edu.curtin.app.model.Town;

public class TownObserver implements Observer {
    @Override
    public void update(Object arg) {
        if (arg instanceof Town) {
            Town town = (Town) arg;
            System.out.println("[LOG] Town updated: " + town.getName());
        }
    }
}
