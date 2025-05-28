package edu.curtin.app;

import edu.curtin.app.factory.RailwayFactory;
import edu.curtin.app.factory.TownFactory;
import edu.curtin.app.output.DotFileWriter;
import edu.curtin.app.output.LoggerSetup;

import edu.curtin.app.services.Simulation;
import edu.curtin.app.services.TownsInput;
import edu.curtin.app.view.ConsoleView;

import java.io.Console;
import java.io.IOException;


public class App {
    public static void main(String[] args) {
        TownFactory townFactory = new TownFactory();
        RailwayFactory railwayFactory = new RailwayFactory();
        TownsInput townsInput = new TownsInput();
        ConsoleView consoleView = new ConsoleView();
        DotFileWriter writer = new DotFileWriter();

        Simulation sim = new Simulation(townFactory, railwayFactory, consoleView, writer, townsInput, LoggerSetup.getLogger());
        sim.run();
    }
}
