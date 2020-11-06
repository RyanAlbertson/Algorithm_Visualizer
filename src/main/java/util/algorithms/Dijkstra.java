package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
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
     * Starts a Dijkstra search at the provided source {@code node}. It finds the
     * shortest path to all nodes from {@code node}.
     *
     * @param node Source node of the search.
     */
    private void dijkstra(Integer node) {

        double[] distanceTo = new double[graphPanel.graph.vertexSet().size()];
        Arrays.fill(distanceTo, Double.POSITIVE_INFINITY);
        distanceTo[node] = 0.0;

        // Order of nodes to be visited
        Deque<Integer> nodesQueue = new ArrayDeque<>(
                graphPanel.graph.vertexSet().size());
        nodesQueue.addLast(node);

        // Order of adjacent edges to be explored at each node
        PriorityQueue<DefaultWeightedEdge> edges = new PriorityQueue<>((e1, e2) ->
        {
            Double e1Weight = Math.abs(graphPanel.graph.getEdgeWeight(e1));
            Double e2Weight = Math.abs(graphPanel.graph.getEdgeWeight(e2));
            return e1Weight.compareTo(e2Weight);
        });
        Integer currentNode;
        Integer adjNode;

        // Whether a specific edge has previously been explored.
        HashMap<DefaultWeightedEdge, Boolean> isVisited = new HashMap<>(
                graphPanel.graph.edgeSet().size());
        graphPanel.graph.edgeSet().forEach(edge -> isVisited.put(edge, false));

        graphPanel.visited[node] = true;

        while (!nodesQueue.isEmpty()) {
            currentNode = nodesQueue.poll();

            // Undirected graph, so use both
            edges.addAll(graphPanel.graph.incomingEdgesOf(currentNode));
            edges.addAll(graphPanel.graph.outgoingEdgesOf(currentNode));

            // Find nearest adjacent node from available adjacent edges.
            DefaultWeightedEdge leastEdge;
            do {
                // Check if user has stopped or paused algorithm
                if (isStopped()) return;
                checkForPause();

                leastEdge = edges.poll();
                if (leastEdge == null || isVisited.get(leastEdge)) continue;

                if (graphPanel.graph.getEdgeSource(leastEdge).equals(currentNode)) {
                    adjNode = graphPanel.graph.getEdgeTarget(leastEdge);
                } else {
                    adjNode = graphPanel.graph.getEdgeSource(leastEdge);
                }
                graphPanel.visited[adjNode] = true;

                double currentDist = distanceTo[adjNode];
                double newDist = distanceTo[currentNode] +
                        graphPanel.graph.getEdgeWeight(leastEdge);

                // Update adjNode's distance if proposed path is shorter
                if (newDist < currentDist) {
                    distanceTo[adjNode] = newDist;
                    graphPanel.path[adjNode] = currentNode;
                    animate();
                }

                nodesQueue.addLast(adjNode);
                isVisited.put(leastEdge, true);
            }
            while (!edges.isEmpty());
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
