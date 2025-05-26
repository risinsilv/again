package edu.curtin.app.output;

import edu.curtin.app.model.Railway;
import edu.curtin.app.model.Town;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OutputService {

    public void display(int day, List<String> messages, Collection<Town> towns) {
        System.out.println("---");
        System.out.println("Day " + day + ":");
        for (String msg : messages) System.out.println(msg);
        for (Town t : towns) {
            System.out.printf("%s p:%d rs:%d rd:%d gs:%d gt:%d\n",
                    t.getName(), t.getPopulation(), countSingle(t), countDual(t),
                    t.getGoodsStockpile(), t.getGoodsTransportedToday());
        }
    }

    private int countSingle(Town t) {
        return (int) t.getRailways().stream()
                .filter(r -> r.isSingleTrack() && !r.isUnderConstruction())
                .count();
    }

    private int countDual(Town t) {
        return (int) t.getRailways().stream()
                .filter(r -> r.isDualTrack() && !r.isUnderConstruction())
                .count();
    }

    public void writeDotFile(Map<String, Town> towns, List<Railway> railways) {
        try (PrintWriter writer = new PrintWriter(new File("simoutput.dot"))) {
            writer.println("graph Towns {");
            for (String name : towns.keySet()) writer.println("    " + name);
            for (Railway r : railways) {
                String style = r.getState().getDotStyle();
                writer.printf("    %s -- %s %s\n", r.getTownA().getName(), r.getTownB().getName(), style);
            }
            writer.println("}");
        } catch (IOException e) {
            System.err.println("Error writing DOT file: " + e.getMessage());
        }
    }
}
