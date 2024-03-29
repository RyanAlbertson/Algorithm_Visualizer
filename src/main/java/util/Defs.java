package main.java.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Definitions.
 */
public class Defs {

    // All algorithms available with the visualizer
    public static final List<String> algNames = List.of("Depth-First Search",
            "Breadth-First Search", "Dijkstra", "A*", "Bellman-Ford",
            "Floyd-Warshall", "Reverse Delete", "Kruskal", "Prim");

    // Maps algorithms to whether they should use minimum connected graphs or not.
    public static final Map<String, Boolean> isShortPathAlg =
            Collections.unmodifiableMap(Map.ofEntries(
                    Map.entry("Depth-First Search", true),
                    Map.entry("Breadth-First Search", true),
                    Map.entry("Dijkstra", true),
                    Map.entry("A*", true),
                    Map.entry("Bellman-Ford", true),
                    Map.entry("Floyd-Warshall", true),
                    Map.entry("Reverse Delete", false),
                    Map.entry("Kruskal", false),
                    Map.entry("Prim", false)));


    // All speeds available
    public static final List<String> speeds = List.of("Slow", "Fast", "Instant");
    
    // Maps speeds to a millisecond value
    public static final Map<String, Integer> speedST =
            Collections.unmodifiableMap(Map.ofEntries(
                    Map.entry("Slow", 1400),
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

    // Pixel radius for nodes in the graph animations
    public static final int NODE_RADIUS = 20;
}
