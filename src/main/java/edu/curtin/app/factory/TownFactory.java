package edu.curtin.app.factory;

import edu.curtin.app.model.Town;
/**
 * ============================================================
 * | Class Name  : TownFactory                               |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : Factory for creating town objects.         |
 * ============================================================
 */
public class TownFactory {
    public Town createTown(String name, int population) {
        return new Town(name, population);
    }
}