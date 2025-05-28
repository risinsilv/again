package edu.curtin.app.services;

import edu.curtin.app.exception.SimulationException;
import edu.curtin.app.factory.RailwayFactory;
import edu.curtin.app.factory.TownFactory;
import edu.curtin.app.model.Railway;
import edu.curtin.app.model.Town;
import edu.curtin.app.observer.RailwayObserver;
import edu.curtin.app.observer.TownObserver;
import edu.curtin.app.output.DotFileWriter;
import edu.curtin.app.state.DualTrackCompleted;
import edu.curtin.app.state.DualTrackUnderConstruction;
import edu.curtin.app.state.SingleTrackCompleted;
import edu.curtin.app.state.SingleTrackUnderConstruction;
import edu.curtin.app.view.ConsoleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * ============================================================
 * | Class Name  : Simulation                                |
 * | Author      : Risinu Silva                              |
 * | Date        : 11 April 2025                             |
 * | Description : Runs the main simulation loop, processes  |
 * |               messages, manages towns and railways,     |
 * |               and coordinates observers and output.     |
 * ============================================================
 */
public class Simulation {
    // Stores all towns by name
    private final Map<String, Town> towns = new HashMap<>();
    // List of all railways
    private final List<Railway> railways = new ArrayList<>();
    // Factories and utilities
    private final TownFactory townFactory;
    private final RailwayFactory railwayFactory;
    private final ConsoleView view;
    private final DotFileWriter writer;
    private final TownsInput townsInput;
    private final Logger logger;
    private int day = 0;

    // Lists to keep track of observers for cleanup
    private final List<TownObserver> townObservers = new ArrayList<>();
    private final List<RailwayObserver> railwayObservers = new ArrayList<>();

    public Simulation(TownFactory tf, RailwayFactory rf, ConsoleView view, DotFileWriter writer, TownsInput ti, Logger logger) {
        this.townFactory = tf;
        this.railwayFactory = rf;
        this.view = view;
        this.writer = writer;
        this.townsInput = ti;
        this.logger = logger;
    }

    // Main simulation loop
    public void run() {
        logger.info("Simulation started.");
        try {
            while (true) {
                // Check for user input to break the loop
                try {
                    if (System.in.available() != 0) {
                        break;
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "IO error in simulation loop", e);
                    break;
                }
                day++;
                logger.info(() -> "Day " + day);
                List<String> messages = new ArrayList<>();
                String msg;
                // Process all messages for the day
                while ((msg = townsInput.nextMessage()) != null) {
                    try {
                        processMessage(msg, messages);
                    } catch (SimulationException se) {
                        String finalMsg = msg;
                        logger.log(Level.WARNING, () -> "Simulation error on message: '" + finalMsg + "': " + se.getMessage());
                    }
                }
                // Process daily simulation logic
                processDay();
                // Display state and write DOT file
                view.display(day, messages, towns.values());
                writer.writeDotFile(towns, railways);
                try {
                    Thread.sleep(1000); // Simulate passage of time
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Simulation interrupted", e);
                    throw new AssertionError(e);
                }
            }
        } finally {
            // Remove all observers for cleanup
            for (Town t : towns.values()) {
                for (TownObserver obs : townObservers) {
                    t.removeObserver(obs);
                }
            }
            for (Railway r : railways) {
                for (RailwayObserver obs : railwayObservers) {
                    r.removeObserver(obs);
                }
            }
            logger.info("Simulation ended and all observers removed.");
        }
    }

    // Handles a single message and updates simulation state accordingly
    private void processMessage(String msg, List<String> messages) throws SimulationException {
        if (msg == null || msg.trim().isEmpty()) {
            logger.warning("Skipped null or empty message.");
            return;
        }
        String[] parts = msg.trim().split("\\s+");
        if (parts.length < 3) {
            logger.warning(() -> "Skipped invalid message (not enough parts): '" + msg + "'");
            return;
        }

        String type = parts[0];
        String townA = parts[1];
        String arg = parts[2];

        try {
            switch (type) {
                case "town-founding":
                    // Create a new town if it doesn't exist
                    int population;
                    try {
                        population = Integer.parseInt(arg);
                    } catch (NumberFormatException nfe) {
                        logger.warning(() -> "Skipped town-founding (invalid population): '" + msg + "'");
                        return;
                    }
                    if (towns.containsKey(townA)) {
                        logger.warning(() -> "Duplicate town-founding for " + townA + " skipped.");
                        return;
                    } else {
                        Town t = townFactory.createTown(townA, population);
                        TownObserver obs = new TownObserver();
                        t.addObserver(obs);
                        townObservers.add(obs);
                        towns.put(townA, t);
                        logger.info(() -> "Town founded: " + townA + " with population " + population);
                    }
                    break;
                case "town-population":
                    // Update population of an existing town
                    int pop;
                    try {
                        pop = Integer.parseInt(arg);
                    } catch (NumberFormatException nfe) {
                        logger.warning(() -> "Skipped town-population (invalid population): '" + msg + "'");
                        return;
                    }
                    Town t = towns.get(townA);
                    if (t != null) {
                        t.setPopulation(pop);
                        logger.info(() -> "Population updated: " + townA + " = " + pop);
                    } else {
                        logger.warning(() -> "Population update for non-existent town: " + townA);
                        return;
                    }
                    break;
                case "railway-construction":
                    // Start construction of a new single-track railway
                    Town ta = towns.get(townA);
                    Town tb = towns.get(arg);
                    if (ta != null && tb != null && !findRailway(ta, tb)) {
                        Railway r = railwayFactory.createSingleTrack(ta, tb);
                        RailwayObserver obs = new RailwayObserver();
                        r.addObserver(obs);
                        railwayObservers.add(obs);
                        railways.add(r);
                        ta.addRailway(r);
                        tb.addRailway(r);
                        logger.info(() -> "Railway construction started between " + townA + " and " + arg);
                    } else {
                        logger.warning(() -> "Railway construction skipped (invalid towns or already exists): " + msg);
                        return;
                    }
                    break;
                case "railway-duplication":
                    // Upgrade an existing single-track railway to dual-track
                    Town ta2 = towns.get(townA);
                    Town tb2 = towns.get(arg);
                    Railway existing = getRailway(ta2, tb2);
                    if (existing != null && existing.getState() instanceof SingleTrackCompleted) {
                        existing.setState(new DualTrackUnderConstruction());
                        existing.setConstructionDaysRemaining(5); // Reset for 5 days of upgrade
                        messages.add(msg);
                        logger.info(() -> "Railway duplication started between " + townA + " and " + arg);
                    } else if (existing == null) {
                        logger.warning(() -> "Railway duplication skipped (no railway exists between " + townA + " and " + arg + "): " + msg);
                        throw new SimulationException("Attempted duplication on missing railway between " + townA + " and " + arg);
                    } else if (!(existing.getState() instanceof SingleTrackCompleted)) {
                        logger.warning(() -> "Railway duplication skipped (not a single-track or already upgrading): " + msg);
                        throw new SimulationException("Attempted duplication on non-single-track railway between " + townA + " and " + arg);
                    }
                    break;
                default:
                    logger.warning(() -> "Unknown message type skipped: '" + msg + "'");
                    return;
            }
            messages.add(msg);
        } catch (SimulationException se) {
            throw se;
        }
    }

    // Checks if a railway exists between two towns
    private boolean findRailway(Town a, Town b) {
        return getRailway(a, b) != null;
    }

    // Returns the railway between two towns, or null if none exists
    private Railway getRailway(Town a, Town b) {
        if (a == null || b == null) {
            return null;
        }
        for (Railway r : railways) {
            if ((r.getTownA().equals(a) && r.getTownB().equals(b)) ||
                    (r.getTownA().equals(b) && r.getTownB().equals(a))) {
                return r;
            }
        }
        return null;
    }

    // Processes the daily simulation logic: goods production, railway construction, and goods transport
    private void processDay() {
        // 1. Each town produces goods equal to its population
        for (Town t : towns.values()) {
            int produced = t.getPopulation();
            t.addGoods(produced);
            t.resetGoodsTransportedToday();
        }

        // 2. Railways: progress construction, flip direction, handle state transitions
        for (Railway r : railways) {
            if (r.isUnderConstruction()) {
                r.decrementConstructionDays();
                if (r.getConstructionDaysRemaining() == 0) {
                    if (r.getState() instanceof SingleTrackUnderConstruction) {
                        r.setState(new SingleTrackCompleted());
                    } else if (r.getState() instanceof DualTrackUnderConstruction) {
                        r.setState(new DualTrackCompleted());
                    }
                    logger.info(() -> "Railway construction/upgrade completed between: " + r.getTownA().getName() + " and " + r.getTownB().getName());
                }
            } else if (r.isSingleTrack()) {
                r.flipDirection(); // Alternate direction for single-track
            }
        }

        // 3. Transport goods via railways
        for (Railway r : railways) {
            if (!r.canTransport() || r.isUnderConstruction()) {
                continue;
            }
            if (r.isSingleTrack()) {
                // Only one direction per day
                Town from = r.isDirectionAB() ? r.getTownA() : r.getTownB();
                Town to = r.isDirectionAB() ? r.getTownB() : r.getTownA();
                int amount = Math.min(100, from.getGoodsStockpile());
                from.removeGoods(amount);
                from.addGoodsTransportedToday(amount);
                if (amount > 0) {
                    logger.fine(() -> "Single-track transport: " + amount + " goods from " + from.getName() + " to " + to.getName());
                }
            } else if (r.isDualTrack()) {
                // Both directions simultaneously
                Town a = r.getTownA(), b = r.getTownB();
                int a2b = Math.min(100, a.getGoodsStockpile());
                int b2a = Math.min(100, b.getGoodsStockpile());
                a.removeGoods(a2b);
                b.removeGoods(b2a);
                a.addGoodsTransportedToday(a2b);
                b.addGoodsTransportedToday(b2a);
                if (a2b > 0) {
                    logger.fine(() -> "Dual-track transport: " + a2b + " goods from " + a.getName() + " to " + b.getName());
                }
                if (b2a > 0) {
                    logger.fine(() -> "Dual-track transport: " + b2a + " goods from " + b.getName() + " to " + a.getName());
                }
            }
        }
    }
}

