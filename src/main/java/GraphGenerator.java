package main.java;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 */
public class GraphGenerator extends DefaultEdge {

    /**
     * @param graph
     * @return
     */
    public static boolean isConnected(SimpleGraph<Integer, DefaultEdge> graph) {

        Set<Integer> vertices = graph.vertexSet();

        // Edges cases
        if (vertices.size() <= 1) return true;
        if (vertices.size() - 1 > graph.edgeSet().size()) return false;

        Set<Integer> visited = new HashSet<>();
        Deque<DefaultEdge> queue = new LinkedList<DefaultEdge>();
        Integer startNode = vertices.iterator().next();

        visited.add(startNode);
        graph.edgesOf(startNode).forEach(queue::addLast);
        while (visited.size() != vertices.size() && queue.size() != 0) {
            DefaultEdge currentEdge = queue.pollFirst();
            Integer source = graph.getEdgeSource(currentEdge);
            Integer dest = graph.getEdgeSource(currentEdge);

            Integer targetNode = visited.contains(source) ? dest : source;
            if (!visited.contains(targetNode)) {
                visited.add(targetNode);
                graph.edgesOf(targetNode).forEach(queue::addLast);
            }
        }

        return visited.size() == vertices.size();
    }


    /**
     * @param graph
     * @param graphSize
     */
    public static void graphToFile(SimpleGraph<Integer, DefaultEdge> graph,
                                   String graphSize) {

        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<>();
        Random rand = new Random();
        for (Integer node : graph.vertexSet()) {
            line.append(node).append(" ");
            // Get random node coordinates within GUI bounds.
            int xCenter = GUI.WINDOW_WIDTH / 2;
            int yCenter = GUI.GRAPH_HEIGHT / 2;
            int xLowerBound = -GUI.WINDOW_WIDTH / 2 + GraphPanel.NODE_RADIUS;
            int xUpperBound = GUI.WINDOW_WIDTH / 2 - GraphPanel.NODE_RADIUS;
            int yLowerBound = -GUI.GRAPH_HEIGHT / 2 + GraphPanel.NODE_RADIUS;
            int yUpperBound = GUI.GRAPH_HEIGHT / 2 - GraphPanel.NODE_RADIUS;
            int x = rand.nextInt((xUpperBound - xLowerBound) + 1) + xLowerBound + xCenter;
            int y = rand.nextInt((yUpperBound - yLowerBound) + 1) + yLowerBound + yCenter;
            line.append(x).append(" ");
            line.append(y).append(" ");
            for (DefaultEdge edge : graph.edgesOf(node)) {
                try {
                    Method getTarget = edge.getClass().getDeclaredMethod("getTarget");
                    getTarget.setAccessible(true);
                    line.append(getTarget.invoke(edge)).append(" ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            lines.add(line.toString());
            line.delete(0, line.length() - 1);
        }

        // Write graph to file
        String fileName = GraphPanel.graphFileNames.get(graphSize);
        String graphFileLocation = System.getProperty("user.dir")
                .concat("\\src\\main\\java\\resources\\graphs\\" + fileName);
        Path file = Paths.get(graphFileLocation);
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param graphSize
     */
    public static void generateGraph(String graphSize) {

        // Define graph sizes
        int numNodes = 0;
        switch (graphSize) {
            case "Small" -> numNodes = 10;
            case "Medium" -> numNodes = 25;
            case "Large" -> numNodes = 50;
            default -> throw new IllegalArgumentException("Error: invalid graph size");
        }

        SimpleGraph<Integer, DefaultEdge> graph =
                new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        for (int i = 0; i < numNodes; i++) graph.addVertex(i);

        // Add random edges until graph is connected
        Random rand = new Random();
        do {
            int u = rand.nextInt(numNodes);
            int v = rand.nextInt(numNodes);
            while (u == v) v = rand.nextInt(numNodes);
            graph.addEdge(u, v);
        } while (!isConnected(graph));

        graphToFile(graph, graphSize);
    }
}
