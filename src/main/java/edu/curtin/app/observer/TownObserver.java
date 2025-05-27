package edu.curtin.app.observer;

import edu.curtin.app.model.Town;

public class TownObserver implements Observer<Town> {
    @Override
    public void update(Town town) {
        System.out.println("[LOG] Town updated: " + town.getName());
    }
}
