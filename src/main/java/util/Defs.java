package main.java.util;

import java.util.HashMap;

/**
 * Definitions used for {@link main.java.AlgorithmVisualizer}.
 */
public class Defs {

    // All algorithms available with the visualizer
    public static final String[] algNames = {"Breadth-First Search",
            "Depth-First Search", "Dijkstra", "Bellman-Ford", "Floyd-Warshall"};

    // All graph sizes available with the visualizer
    public static final String[] graphSizes = {"Small", "Medium", "Large"};

    // Maps graph sizes to their respective file names
    public static final HashMap<String, String> graphFileNamesST = new HashMap<>();

    static {
        graphFileNamesST.put("Small", "sm_graph.txt");
        graphFileNamesST.put("Medium", "md_graph.txt");
        graphFileNamesST.put("Large", "lg_graph.txt");
    }

    // Maps graph sizes to the number of nodes in each of them
    public static final HashMap<String, Integer> numNodesST = new HashMap<>();

    static {
        numNodesST.put("Small", 10);
        numNodesST.put("Medium", 25);
        numNodesST.put("Large", 50);
    }

    // Maps algorithms to whether or not the graph input is directed for each
    public static final HashMap<String, Boolean> isDirectedST = new HashMap<>();

    static {
        isDirectedST.put("Breadth-First Search", false);
        isDirectedST.put("Depth-First Search", false);
        isDirectedST.put("Dijkstra", true);
        isDirectedST.put("Bellman-Ford", true);
        isDirectedST.put("Floyd-Warshall", true);
    }

    // Pixel radius for nodes in the graph animations
    public static final int NODE_RADIUS = 20;
}
