package main.java;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Generates a {@link SimpleGraph} that is connected and weighted. Then writes it
 * to a file. The graph size can be provided.
 *
 * @author Ryan Albertson
 */
public class GraphGenerator extends DefaultWeightedEdge {

    /**
     * @param graph {@link SimpleGraph} to check for connectivity.
     * @return True if {@code graph} is connected, otherwise false.
     */
    private static boolean isConnected(SimpleGraph<Integer, DefaultWeightedEdge> graph) {

        Set<Integer> vertices = graph.vertexSet();

        // Edges cases
        if (vertices.size() <= 1) return true;
        if (vertices.size() - 1 > graph.edgeSet().size()) return false;

        Set<Integer> visited = new HashSet<>();
        Deque<DefaultWeightedEdge> queue = new LinkedList<>();
        Integer startNode = vertices.iterator().next();

        visited.add(startNode);
        graph.edgesOf(startNode).forEach(queue::addLast);
        while (visited.size() != vertices.size() && queue.size() != 0) {
            DefaultWeightedEdge currentEdge = queue.pollFirst();
            Integer source = graph.getEdgeSource(currentEdge);
            Integer dest = graph.getEdgeTarget(currentEdge);

            Integer targetNode = visited.contains(source) ? dest : source;
            if (!visited.contains(targetNode)) {
                visited.add(targetNode);
                graph.edgesOf(targetNode).forEach(queue::addLast);
            }
        }

        return visited.size() == vertices.size();
    }


    /**
     * Writes {@code graph} to a text file.
     *
     * @param graph     {@link SimpleGraph} to write to a file.
     * @param graphSize # of nodes in {@code graph}. Used to determine file name.
     */
    private static void graphToFile(SimpleGraph<Integer, DefaultWeightedEdge> graph,
                                    String graphSize) {

        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<>();
        Random rand = new Random();
        for (Integer node : graph.vertexSet()) {
            line.append(node).append(" ");

            // Get random node coordinates within GUI bounds
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

            // Get adjacent nodes
            for (DefaultWeightedEdge edge : graph.edgesOf(node)) {
                try {
                    // Adjacencies are undirected, so choose correct node in edge
                    int source;
                    int target;
                    int adj;

                    Method getSource = edge.getClass().getDeclaredMethod("getSource");
                    getSource.setAccessible(true);
                    source = Integer.parseInt(getSource.invoke(edge).toString());

                    Method getTarget = edge.getClass().getDeclaredMethod("getTarget");
                    getTarget.setAccessible(true);
                    target = Integer.parseInt(getTarget.invoke(edge).toString());

                    adj = target == node ? source : target;
                    line.append(adj).append(" ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            lines.add(line.toString());
            line.delete(0, line.length() - 1);
        }

        // Write graph to file
        String graphFileName = GraphPanel.graphFileNames.get(graphSize);
        //CHANGE FOR USE WITH AN EXECUTABLE...JUST USE graphFileName?
        String graphFileLocation = System.getProperty("user.dir")
                .concat("\\src\\main\\java\\resources\\graphs\\" + graphFileName);
        File file = new File(graphFileLocation);
        OutputStreamWriter writer = null;
        try {
            // Delete to prevent overwrite
            if (!file.createNewFile()) {
                new FileOutputStream(graphFileLocation, false).close();
            }
            FileOutputStream outFile = new FileOutputStream(graphFileLocation);
            writer = new OutputStreamWriter(outFile, StandardCharsets.UTF_8);
            for (String nodeData : lines) {
                writer.write(nodeData + System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Generates a connected and weighted {@link SimpleGraph}. Calls
     * {@link #graphToFile(SimpleGraph, String)} to write the graph to a file.
     *
     * @param graphSize Defines how many nodes will be in generated graph.
     * @return {@link SimpleGraph} which is connected and has {@code graphSize} nodes.
     * @throws IllegalArgumentException If {@code graphSize} is null.
     */
    public static SimpleGraph<Integer, DefaultWeightedEdge> generateGraph(
            String graphSize) {

        if (graphSize == null) {
            throw new IllegalArgumentException("Error: SimpleGraph is null");
        }

        // Define graph sizes
        int numNodes = 0;
        switch (graphSize) {
            case "Small" -> numNodes = 10;
            case "Medium" -> numNodes = 25;
            case "Large" -> numNodes = 50;
            default -> throw new IllegalArgumentException("Error: invalid graph size\n" +
                    "Available graph sizes: Small, Medium, Large");
        }

        SimpleGraph<Integer, DefaultWeightedEdge> graph =
                new SimpleGraph<>(DefaultWeightedEdge.class);
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

        return graph;
    }
}
