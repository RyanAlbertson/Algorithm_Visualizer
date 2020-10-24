package main.java;

import main.java.util.Defs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
     * Writes a {@code graph} to a text file. {@code graph} is connected
     * and is cylic with high probability. {@code graph} is chosen to be either
     * directed or undirected.
     *
     * @param graph     {@link SimpleGraph} to write to a file.
     * @param graphSize # of nodes in {@code graph}. Used to determine file name.
     * @param algName   Used to determine if graphs should be converted to undirected.
     */
    private static void graphToFile(SimpleGraph<Integer, DefaultWeightedEdge> graph,
                                    String graphSize, String algName) {

        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<>();
        Random rand = new Random();
        for (Integer node : graph.vertexSet()) {
            line.append(node).append(" ");

            // Get random node coordinates within GUI bounds
            int xCenter = GUI.WINDOW_WIDTH / 2;
            int yCenter = GUI.GRAPH_HEIGHT / 2;
            int xLowerBound = -GUI.WINDOW_WIDTH / 2 + Defs.NODE_RADIUS;
            int xUpperBound = GUI.WINDOW_WIDTH / 2 - Defs.NODE_RADIUS;
            int textPadding = 30;
            int yLowerBound = -GUI.GRAPH_HEIGHT / 2 + Defs.NODE_RADIUS +
                    textPadding;
            int yUpperBound = GUI.GRAPH_HEIGHT / 2 - Defs.NODE_RADIUS;
            int x = rand.nextInt((xUpperBound - xLowerBound) + 1) + xLowerBound + xCenter;
            int y = rand.nextInt((yUpperBound - yLowerBound) + 1) + yLowerBound + yCenter;
            line.append(x).append(" ");
            line.append(y).append(" ");

            // Get adjacent nodes
            for (DefaultWeightedEdge edge : graph.edgesOf(node)) {
                // Need to check both nodes for each edge for undirected edges
                int source = graph.getEdgeSource(edge);
                int target = graph.getEdgeTarget(edge);
                int adj;

                // Make edges undirected if algorithm requires it
                if (Defs.isDirectedST.get(algName)) adj = target;
                else adj = (target == node) ? source : target;
                line.append(adj).append(" ");
            }
            lines.add(line.toString());
            line.delete(0, line.length() - 1);
        }

        // Write graph to file
        String graphFileName = Defs.graphFileNamesST.get(graphSize);
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
     * graphToFile to write the provided graph to a file.
     *
     * @param graphSize Defines how many nodes will be in generated graph.
     * @param algName   Used to determine if graph will be converted to undirected.
     * @return {@link SimpleGraph} which is connected and has {@code graphSize} nodes.
     * @throws IllegalArgumentException If {@code graphSize} is null.
     */
    public static SimpleGraph<Integer, DefaultWeightedEdge> generateGraph(
            String graphSize, String algName) {

        if (graphSize == null) {
            throw new IllegalArgumentException("SimpleGraph is null");
        }

        // Define graph sizes
        int numNodes = 0;
        switch (graphSize) {
            case "Small" -> numNodes = 10;
            case "Medium" -> numNodes = 25;
            case "Large" -> numNodes = 50;
            default -> throw new IllegalArgumentException("Invalid graph size. " +
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

        graphToFile(graph, graphSize, algName);

        return graph;
    }
}
