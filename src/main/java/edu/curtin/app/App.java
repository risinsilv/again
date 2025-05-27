package edu.curtin.app;

import edu.curtin.app.factory.RailwayFactory;
import edu.curtin.app.factory.TownFactory;
import edu.curtin.app.output.LoggerSetup;
import edu.curtin.app.output.OutputService;
import edu.curtin.app.services.Simulation;
import edu.curtin.app.services.TownsInput;

import java.io.IOException;

/**
 * Entry point into the application. To change the package, and/or the name of this class, make
 * sure to update the 'mainClass = ...' line in build.gradle.
 */
public class App
{
    public static void main(String[] args)
    {
        TownFactory townFactory = new TownFactory();
        RailwayFactory railwayFactory = new RailwayFactory();
        OutputService outputService = new OutputService();
        TownsInput townsInput = new TownsInput();

        Simulation sim = new Simulation(townFactory, railwayFactory, outputService, townsInput, LoggerSetup.getLogger());
        sim.run();
    }
}
