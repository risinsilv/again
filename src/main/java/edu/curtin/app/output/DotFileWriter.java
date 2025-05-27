package edu.curtin.app.output;

import edu.curtin.app.model.Railway;
import edu.curtin.app.model.Town;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class DotFileWriter {
    public void writeDotFile(Map<String, Town> towns, List<Railway> railways) {
        try (PrintWriter writer = new PrintWriter(new File("simoutput.dot"))) {
            writer.println("graph Towns {");
            for (String name : towns.keySet()){
                writer.println("    " + name);
            }
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
