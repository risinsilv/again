package edu.curtin.app.view;

import edu.curtin.app.model.Town;

import java.util.Collection;
import java.util.List;

public class ConsoleView {
    public void display(int day, List<String> messages, Collection<Town> towns) {
        System.out.println("---");
        System.out.println("Day " + day + ":");
        for (String msg : messages){
            System.out.println(msg);
        }
        System.out.println("\n");
        for (Town t : towns) {
            System.out.printf("%s p:%d rs:%d rd:%d gs:%d gt:%d\n",
                    t.getName(), t.getPopulation(), countSingle(t), countDual(t),
                    t.getGoodsStockpile(), t.getGoodsTransportedToday());
        }
    }
//helper method count sungle railways
    private int countSingle(Town t) {
        return (int) t.getRailways().stream()
                .filter(r -> r.isSingleTrack())
                .count();
    }
//helper method to count dual railways
    private int countDual(Town t) {
        return (int) t.getRailways().stream()
                .filter(r -> r.isDualTrack() && !r.isUnderConstruction())
                .count();
    }
}
