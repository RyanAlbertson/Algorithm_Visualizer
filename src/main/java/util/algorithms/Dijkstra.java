package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;


/**
 * Implements a Dijkstra algorithm to find the shortest path from a
 * {@link GraphPanel#sourceNode}source node to all other nodes. Note that the
 * input graph is directed but I choose to implement an undirected algorithm
 * in an effort to make the animations more appealing.
 *
 * @author Ryan Albertson
 */
public class Dijkstra implements Runnable {

    private final GraphPanel gPanel;
    private final Object pauseLock = new Object();


    /**
     * Constructs a {@link Dijkstra}.
     *
     * @param gPanel The {@link javax.swing.JPanel} containing a graph.
     * @throws IllegalArgumentException If {@code graphPanel} is null.
     */
    public Dijkstra(GraphPanel gPanel) {

        if (gPanel == null) {
            throw new IllegalArgumentException("GraphPanel is null");
        } else this.gPanel = gPanel;
    }


    /**
     * If user has stopped the animation, clears the animation.
     *
     * @return True if user has stopped the animation, false otherwise.
     */
    private boolean isStopped() {

        return gPanel.stop;
    }


    /**
     * Checks if user has paused the animation. If so, the animation process is
     * held until the user has unpaused it.
     */
    private void checkForPause() {

        synchronized (pauseLock) {
            boolean paused;
            while (paused = gPanel.pause) {
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
            // Update animation
            gPanel.repaint();
            TimeUnit.MILLISECONDS.sleep(gPanel.speed);
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

        double[] distanceTo = new double[gPanel.nodeCount];
        Arrays.fill(distanceTo, Double.POSITIVE_INFINITY);
        distanceTo[node] = 0.0;

        // Order of nodes to be visited
        Deque<Integer> nodesQueue = new ArrayDeque<>(
                gPanel.graph.vertexSet().size());
        nodesQueue.addLast(node);

        // Order of adjacent edges to be explored at each node (min. priority bv weight)
        PriorityQueue<DefaultWeightedEdge> edgesPQ = new PriorityQueue<>((e1, e2) ->
        {
            Double e1Weight = gPanel.graph.getEdgeWeight(e1);
            Double e2Weight = gPanel.graph.getEdgeWeight(e2);
            return e1Weight.compareTo(e2Weight);
        });
        boolean[] isExplored = new boolean[gPanel.nodeCount];
        gPanel.visited[node] = true;
        Integer currentNode;
        Integer adjNode;

        while (!nodesQueue.isEmpty()) {
            currentNode = nodesQueue.poll();
            edgesPQ.addAll(gPanel.graph.edgesOf(currentNode));
            do {
                // Check if user has stopped or paused algorithm
                if (isStopped()) return;
                checkForPause();

                // Find nearest adjacent node from available adjacent edges.
                DefaultWeightedEdge leastEdge = edgesPQ.poll();
                adjNode = gPanel.graph.getEdgeTarget(leastEdge);
                if (adjNode.equals(currentNode)) {
                    adjNode = gPanel.graph.getEdgeSource(leastEdge);
                }
                if (isExplored[adjNode]) continue;
                gPanel.visited[adjNode] = true;

                // Update adjNode's distance if proposed predecessor is shorter
                double currentDist = distanceTo[adjNode];
                double newDist = distanceTo[currentNode] +
                        gPanel.graph.getEdgeWeight(leastEdge);
                if (newDist < currentDist) {
                    distanceTo[adjNode] = newDist;
                    gPanel.path[adjNode] = currentNode;
                    animate();
                }
                nodesQueue.addLast(adjNode);
            }
            while (!edgesPQ.isEmpty());
            isExplored[currentNode] = true;
        }
    }


    /**
     * Starts the algorithm process.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        dijkstra(gPanel.sourceNode);
    }
}
