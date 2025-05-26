package edu.curtin.app.factory;

import edu.curtin.app.model.Town;

public class TownFactory {
    public Town createTown(String name, int population) {
        return new Town(name, population);
    }
}