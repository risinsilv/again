package edu.curtin.app.observer;

import edu.curtin.app.model.Town;
/**
 * ============================================================
 * | Class Name  : TownObserver                              |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : Observer for town updates.                |
 * ============================================================
 */
public class TownObserver implements Observer<Town> {
    @Override
    public void update(Town town) {
        System.out.println("[LOG] Town updated: " + town.getName());
    }
}
