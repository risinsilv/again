package edu.curtin.app.services;

import edu.curtin.app.exception.SimulationException;
import edu.curtin.app.factory.RailwayFactory;
import edu.curtin.app.factory.TownFactory;
import edu.curtin.app.model.Railway;
import edu.curtin.app.model.Town;
import edu.curtin.app.observer.RailwayObserver;
import edu.curtin.app.observer.TownObserver;
import edu.curtin.app.output.OutputService;
import edu.curtin.app.state.DualTrackCompleted;
import edu.curtin.app.state.DualTrackUnderConstruction;
import edu.curtin.app.state.SingleTrackCompleted;
import edu.curtin.app.state.SingleTrackUnderConstruction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Simulation {
    private final Map<String, Town> towns = new HashMap<>();
    private final List<Railway> railways = new ArrayList<>();
    private final TownFactory townFactory;
    private final RailwayFactory railwayFactory;
    private final OutputService outputService;
    private final TownsInput townsInput;
    private final Logger logger;
    private int day = 0;

    public Simulation(TownFactory tf, RailwayFactory rf, OutputService os, TownsInput ti, Logger logger) {
        this.townFactory = tf;
        this.railwayFactory = rf;
        this.outputService = os;
        this.townsInput = ti;
        this.logger = logger;
    }

    public void run() {
        logger.info("Simulation started.");
        try {
            while (System.in.available() == 0) {
                day++;
                logger.info("Day " + day);
                List<String> messages = new ArrayList<>();
                String msg;
                while ((msg = townsInput.nextMessage()) != null) {
                    try {
                        processMessage(msg, messages);
                    } catch (SimulationException se) {
                        logger.log(Level.WARNING, "Simulation error on message: '" + msg + "': " + se.getMessage());
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Unexpected error when processing message: '" + msg + "'", ex);
                    }
                }
                processDay(messages);
                outputService.display(day, messages, towns.values());
                outputService.writeDotFile(towns, railways);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.log(Level.SEVERE, "Simulation interrupted", e);
                    throw new AssertionError(e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO error in simulation loop", e);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Critical simulation error", ex);
            throw new RuntimeException(ex);
        } finally {
            logger.info("Simulation ended.");
        }
    }

    private void processMessage(String msg, List<String> messages) throws SimulationException {
        if (msg == null || msg.trim().isEmpty()) {
            logger.warning("Skipped null or empty message.");
            return;
        }
        String[] parts = msg.trim().split("\\s+");
        if (parts.length < 3) {
            logger.warning("Skipped invalid message (not enough parts): '" + msg + "'");
            return;
        }

        String type = parts[0];
        String townA = parts[1];
        String arg = parts[2];

        try {
            switch (type) {
                case "town-founding":
                    int population;
                    try {
                        population = Integer.parseInt(arg);
                    } catch (NumberFormatException nfe) {
                        logger.warning("Skipped town-founding (invalid population): '" + msg + "'");
                        return;
                    }
                    if (!towns.containsKey(townA)) {
                        Town t = townFactory.createTown(townA, population);
                        t.addObserver(new TownObserver());
                        towns.put(townA, t);
                        logger.info("Town founded: " + townA + " with population " + population);
                    } else {
                        logger.warning("Duplicate town-founding for " + townA + " skipped.");
                        return;
                    }
                    break;
                case "town-population":
                    int pop;
                    try {
                        pop = Integer.parseInt(arg);
                    } catch (NumberFormatException nfe) {
                        logger.warning("Skipped town-population (invalid population): '" + msg + "'");
                        return;
                    }
                    Town t = towns.get(townA);
                    if (t != null) {
                        t.setPopulation(pop);
                        logger.info("Population updated: " + townA + " = " + pop);
                    } else {
                        logger.warning("Population update for non-existent town: " + townA);
                        return;
                    }
                    break;
                case "railway-construction":
                    Town ta = towns.get(townA);
                    Town tb = towns.get(arg);
                    if (ta != null && tb != null && !findRailway(ta, tb)) {
                        Railway r = railwayFactory.createSingleTrack(ta, tb);
                        r.addObserver(new RailwayObserver());
                        railways.add(r);
                        ta.addRailway(r);
                        tb.addRailway(r);
                        logger.info("Railway construction started between " + townA + " and " + arg);
                    } else {
                        logger.warning("Railway construction skipped (invalid towns or already exists): " + msg);
                        return;
                    }
                    break;
                case "railway-duplication":
                    Town ta2 = towns.get(townA);
                    Town tb2 = towns.get(arg);
                    Railway existing = getRailway(ta2, tb2);
                    if (existing != null && existing.getState() instanceof SingleTrackCompleted) {
                        existing.setState(new DualTrackUnderConstruction());
                        existing.setConstructionDaysRemaining(5); // Reset for 5 days of upgrade
                        messages.add(msg);
                        logger.info("Railway duplication started between " + townA + " and " + arg);
                    } else if (existing == null) {
                        logger.warning("Railway duplication skipped (no railway exists between " + townA + " and " + arg + "): " + msg);
                        throw new SimulationException("Attempted duplication on missing railway between " + townA + " and " + arg);
                    } else if (!(existing.getState() instanceof SingleTrackCompleted)) {
                        logger.warning("Railway duplication skipped (not a single-track or already upgrading): " + msg);
                        throw new SimulationException("Attempted duplication on non-single-track railway between " + townA + " and " + arg);
                    }
                    break;
                default:
                    logger.warning("Unknown message type skipped: '" + msg + "'");
                    return;
            }
            messages.add(msg);
        } catch (SimulationException se) {
            throw se;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error processing message: '" + msg + "'", ex);
            throw new SimulationException("Severe error processing message: '" + msg + "'", ex);
        }
    }

    private boolean findRailway(Town a, Town b) {
        return getRailway(a, b) != null;
    }

    private Railway getRailway(Town a, Town b) {
        if (a == null || b == null) return null;
        for (Railway r : railways) {
            if ((r.getTownA() == a && r.getTownB() == b) ||
                    (r.getTownA() == b && r.getTownB() == a)) return r;
        }
        return null;
    }

    private void processDay(List<String> messages) {
        // 1. Each town produces goods
        for (Town t : towns.values()) {
            int produced = t.getPopulation();
            t.addGoods(produced);
            t.resetGoodsTransportedToday();
        }

        // 2. Railways: progress construction, flip direction, state transitions
        for (Railway r : railways) {
            if (r.isUnderConstruction()) {
                r.decrementConstructionDays();
                if (r.getConstructionDaysRemaining() == 0) {
                    if (r.getState() instanceof SingleTrackUnderConstruction)
                        r.setState(new SingleTrackCompleted());
                    else if (r.getState() instanceof DualTrackUnderConstruction)
                        r.setState(new DualTrackCompleted());
                    logger.info("Railway construction/upgrade completed between: " + r.getTownA().getName() + " and " + r.getTownB().getName());
                }
            } else if (r.isSingleTrack()) {
                r.flipDirection();
            }
        }

        // 3. Transport goods via railways
        for (Railway r : railways) {
            if (!r.canTransport() || r.isUnderConstruction()) continue;
            if (r.isSingleTrack()) {
                Town from = r.isDirectionAB() ? r.getTownA() : r.getTownB();
                Town to = r.isDirectionAB() ? r.getTownB() : r.getTownA();
                int amount = Math.min(100, from.getGoodsStockpile());
                from.removeGoods(amount);
                from.addGoodsTransportedToday(amount);
                if (amount > 0) {
                    logger.fine("Single-track transport: " + amount + " goods from " + from.getName() + " to " + to.getName());
                }
            } else if (r.isDualTrack()) {
                Town a = r.getTownA(), b = r.getTownB();
                int a2b = Math.min(100, a.getGoodsStockpile());
                int b2a = Math.min(100, b.getGoodsStockpile());
                a.removeGoods(a2b);
                b.removeGoods(b2a);
                a.addGoodsTransportedToday(a2b);
                b.addGoodsTransportedToday(b2a);
                if (a2b > 0) {
                    logger.fine("Dual-track transport: " + a2b + " goods from " + a.getName() + " to " + b.getName());
                }
                if (b2a > 0) {
                    logger.fine("Dual-track transport: " + b2a + " goods from " + b.getName() + " to " + a.getName());
                }
            }
        }
    }
}

//public class Simulation {
//    private final Map<String, Town> towns = new HashMap<>();
//    private final List<Railway> railways = new ArrayList<>();
//    private final TownFactory townFactory;
//    private final RailwayFactory railwayFactory;
//    private final OutputService outputService;
//    private final TownsInput townsInput;
//    private int day = 0;
//
//    public Simulation(TownFactory tf, RailwayFactory rf, OutputService os, TownsInput ti) {
//        this.townFactory = tf;
//        this.railwayFactory = rf;
//        this.outputService = os;
//        this.townsInput = ti;
//    }
//
//    public void run() throws IOException {
//        while (System.in.available() == 0) {
//            day++;
//            List<String> messages = new ArrayList<>();
//            String msg;
//            while ((msg = townsInput.nextMessage()) != null) {
//                processMessage(msg, messages);
//            }
//            processDay(messages);
//            outputService.display(day, messages, towns.values());
//            outputService.writeDotFile(towns, railways);
//            try { Thread.sleep(1000); } catch (InterruptedException e) { throw new AssertionError(e); }
//        }
//    }
//
//    private void processMessage(String msg, List<String> messages) {
//        if (msg == null || msg.trim().isEmpty()) return;
//        String[] parts = msg.trim().split("\\s+");
//        if (parts.length < 3) return; // invalid
//
//        String type = parts[0];
//        String townA = parts[1];
//        String arg = parts[2];
//
//        switch (type) {
//            case "town-founding":
//                try {
//                    int population = Integer.parseInt(arg);
//                    if (!towns.containsKey(townA)) {
//                        Town t = townFactory.createTown(townA, population);
//                        t.addObserver(new TownObserver());
//                        towns.put(townA, t);
//                    }
//                } catch (NumberFormatException ex) { return; }
//                break;
//            case "town-population":
//                try {
//                    int population = Integer.parseInt(arg);
//                    Town t = towns.get(townA);
//                    if (t != null) t.setPopulation(population);
//                } catch (NumberFormatException ex) { return; }
//                break;
//            case "railway-construction":
//                Town ta = towns.get(townA);
//                Town tb = towns.get(arg);
//                if (ta != null && tb != null && !findRailway(ta, tb)) {
//                    Railway r = railwayFactory.createSingleTrack(ta, tb);
//                    r.addObserver(new RailwayObserver());
//                    railways.add(r);
//                    ta.addRailway(r);
//                    tb.addRailway(r);
//                }
//                break;
//            case "railway-duplication":
//                Town ta2 = towns.get(townA);
//                Town tb2 = towns.get(arg);
//                Railway existing = getRailway(ta2, tb2);
//                if (existing != null
//                        && existing.getState() instanceof SingleTrackCompleted) {
//                    existing.setState(new DualTrackUnderConstruction());
//                    existing.setConstructionDaysRemaining(5); // Reset for 5 days of upgrade
//                    messages.add(msg);
//                }
//                break;
//
//            default: return;
//        }
//        messages.add(msg);
//    }
//
//    private boolean findRailway(Town a, Town b) {
//        return getRailway(a, b) != null;
//    }
//
//    private Railway getRailway(Town a, Town b) {
//        for (Railway r : railways) {
//            if ((r.getTownA() == a && r.getTownB() == b) ||
//                    (r.getTownA() == b && r.getTownB() == a)) return r;
//        }
//        return null;
//    }
//
//    private void processDay(List<String> messages) {
//        // 1. Each town produces goods
//        for (Town t : towns.values()) {
//            int produced = t.getPopulation();
//            t.addGoods(produced);
//            t.resetGoodsTransportedToday();
//        }
//
//        // 2. Railways: progress construction, flip direction, state transitions
//        for (Railway r : railways) {
//            if (r.isUnderConstruction()) {
//                r.decrementConstructionDays();
//                if (r.getConstructionDaysRemaining() == 0) {
//                    if (r.getState() instanceof SingleTrackUnderConstruction)
//                        r.setState(new SingleTrackCompleted());
//                    else if (r.getState() instanceof DualTrackUnderConstruction)
//                        r.setState(new DualTrackCompleted());
//                }
//            } else if (r.isSingleTrack()) {
//                r.flipDirection();
//            }
//        }
//
//        // 3. Transport goods via railways
//        for (Railway r : railways) {
//            if (!r.canTransport() || r.isUnderConstruction()) continue;
//            if (r.isSingleTrack()) {
//                Town from = r.isDirectionAB() ? r.getTownA() : r.getTownB();
//                Town to = r.isDirectionAB() ? r.getTownB() : r.getTownA();
//                int amount = Math.min(100, from.getGoodsStockpile());
//                from.removeGoods(amount);
//                from.addGoodsTransportedToday(amount);
//            } else if (r.isDualTrack()) {
//                Town a = r.getTownA(), b = r.getTownB();
//                int a2b = Math.min(100, a.getGoodsStockpile());
//                int b2a = Math.min(100, b.getGoodsStockpile());
//                a.removeGoods(a2b);
//                b.removeGoods(b2a);
//                a.addGoodsTransportedToday(a2b);
//                b.addGoodsTransportedToday(b2a);
//            }
//        }
//    }
//}

