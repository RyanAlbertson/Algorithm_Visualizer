package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Implements a Dijkstra algorithm to find the shortest path from a
 * {@link GraphPanel#sourceNode}source node to all other nodes. Note that the
 * input graph is directed but I choose to implement an undirected algorithm
 * in an effort to make the animations more substantial.
 *
 * @author Ryan Albertson
 */
public class Dijkstra implements Runnable {


    private final GraphPanel graphPanel;
    private final Object pauseLock = new Object();


    /**
     * If user has stopped the animation, clears the animation .
     *
     * @return True if user has stopped the animation, false otherwise.
     */
    private boolean isStopped() {

        if (graphPanel.stop) {
            // Clear animation
            Arrays.fill(graphPanel.path, Integer.MAX_VALUE);
            Arrays.fill(graphPanel.visited, false);
            graphPanel.repaint();
            return true;
        }
        return false;
    }


    /**
     * Checks if user has paused the animation. If so, the animation process is
     * held until the user has unpaused it.
     */
    private void checkForPause() {

        synchronized (pauseLock) {
            boolean paused;
            while (paused = graphPanel.pause) {
                try {
                    pauseLock.wait(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Repaints the animation within the {@link GraphPanel}. Does it slowly such
     * that the user can visualize the algorithm stepping through.
     */
    private void animate() {

        try {
            // Update animation, slowly
            graphPanel.repaint();
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns the unvisited node with the least distance from the source node.
     *
     * @param distanceTo Distances to all nodes from source node.
     */
    private Integer getNearestReachableNode(double[] distanceTo) {

        Integer nearestNode = null;
        double shortestDist = Double.POSITIVE_INFINITY;

        for (int i = 0; i < distanceTo.length; i++) {

            if (graphPanel.visited[i]) continue;

            double currentDist = distanceTo[i];
            if (currentDist < shortestDist) {
                shortestDist = currentDist;
                nearestNode = i;
            }
        }
        return nearestNode;
    }


    /**
     * Starts a Dijkstra search at the provided source {@code node}. It finds the
     * shortest path to all nodes from {@code node}.
     *
     * @param node Source node of the search.
     */
    private void dijkstra(Integer node) {

        double[] distanceTo = new double[graphPanel.graph.vertexSet().size()];
        Arrays.fill(distanceTo, Double.POSITIVE_INFINITY);
        distanceTo[node] = 0.0;

        while (true) {
            // Check if user has stopped or paused algorithm
            if (isStopped()) return;
            checkForPause();

            Integer currentNode = getNearestReachableNode(distanceTo);

            // Algorithm finished
            if (currentNode == null) break;

            graphPanel.visited[currentNode] = true;
            animate();

            // Test new paths to adjacent nodes, update their distances if needed
            List<DefaultWeightedEdge> allEdges = new ArrayList<>();
            // Account for undirected edges
            allEdges.addAll(graphPanel.graph.incomingEdgesOf(currentNode));
            allEdges.addAll(graphPanel.graph.outgoingEdgesOf(currentNode));
            for (DefaultWeightedEdge edge : allEdges) {

                int adjNode = graphPanel.graph.getEdgeTarget(edge);
                if (adjNode == currentNode) {
                    adjNode = graphPanel.graph.getEdgeSource(edge);
                }
                if (graphPanel.visited[adjNode]) continue;

                double currentDist = distanceTo[adjNode];
                double newDist = distanceTo[currentNode] +
                        graphPanel.graph.getEdgeWeight(edge);

                // Update edge distance if new path is shorter
                if (newDist < currentDist) {
                    distanceTo[adjNode] = newDist;
                    graphPanel.path[adjNode] = currentNode;
                }
            }

        }
    }


    /**
     * Starts the algorithm process.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        // Don't start algorithm if user hasn't selected source & target nodes
        if (graphPanel.sourceNode != null && graphPanel.targetNode != null) {
            dijkstra(graphPanel.sourceNode);
        }
    }


    /**
     * Constructs a {@link Dijkstra}.
     *
     * @param graphPanel The {@link javax.swing.JPanel} containing a graph.
     * @throws IllegalArgumentException If {@code graphPanel} is null.
     */
    public Dijkstra(GraphPanel graphPanel) {

        if (graphPanel == null) {
            throw new IllegalArgumentException("GraphPanel is null");
        } else this.graphPanel = graphPanel;
    }
}
