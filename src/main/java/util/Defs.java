package main.java.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Definitions.
 */
public class Defs {

    // All algorithms available with the visualizer
    public static final List<String> algNames = List.of("Breadth-First Search",
            "Depth-First Search", "Dijkstra", "Kruskal", "Prim");

    // Maps algorithms to whether they should use minimum connected graphs or not.
    public static final Map<String, Boolean> isShortPathAlg =
            Collections.unmodifiableMap(Map.ofEntries(
                    Map.entry("Breadth-First Search", true),
                    Map.entry("Depth-First Search", true),
                    Map.entry("Dijkstra", true),
                    Map.entry("Kruskal", false),
                    Map.entry("Prim", false)));

    // Available speeds for the algorithm animations. Values are milliseconds
    public static final Map<String, Integer> speedST =
            Collections.unmodifiableMap(Map.ofEntries(
                    Map.entry("Slow", 1300),
                    Map.entry("Fast", 500),
                    Map.entry("Instant", 0)));

    // All graph sizes available with the visualizer
    public static final List<String> graphSizes = List.of("Small", "Medium", "Large");

    // Maps graph sizes to the number of nodes in each of them
    public static final Map<String, Integer> nodeCountST =
            Collections.unmodifiableMap(Map.ofEntries(
                    Map.entry("Small", 10),
                    Map.entry("Medium", 25),
                    Map.entry("Large", 50)));

    // Pixel radius for nodes in the graph animations
    public static final int NODE_RADIUS = 20;
}
