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
            "Depth-First Search", "Dijkstra", "Bellman-Ford", "Floyd-Warshall");

    // Available speeds for the algorithm animations. Values are milliseconds
    public static final Map<String, Integer> speedST =
            Collections.unmodifiableMap(Map.ofEntries(
                    Map.entry("Slow", 1100),
                    Map.entry("Fast", 400),
                    Map.entry("Instant", 0)));


    // All graph sizes available with the visualizer
    public static final List<String> graphSizes = List.of("Small", "Medium", "Large");

    // Maps graph sizes to the number of nodes in each of them
    public static final Map<String, Integer> nodeCountST =
            Collections.unmodifiableMap(Map.ofEntries(
                    Map.entry("Small", 10),
                    Map.entry("Medium", 25),
                    Map.entry("Large", 50)));

    // Maps algorithms to whether or not the graph input is directed for each
    public static final Map<String, Boolean> isDirectedST =
            Collections.unmodifiableMap(Map.ofEntries(
                    Map.entry("Breadth-First Search", false),
                    Map.entry("Depth-First Search", false),
                    Map.entry("Dijkstra", false),
                    Map.entry("Bellman-Ford", true),
                    Map.entry("Floyd-Warshall", true)));

    // Pixel radius for nodes in the graph animations
    public static final int NODE_RADIUS = 20;
}
