package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Implements a Prim's algorithm to find the minimum spanning tree of a
 * {@link org.jgrapht.graph.DefaultUndirectedWeightedGraph}.
 *
 * @author Ryan Albertson
 */
public class Prim implements Runnable {


    private final GraphPanel gPanel;
    private final Set<DefaultWeightedEdge> reached;
    private final boolean[] inMST;
    private final Object pauseLock = new Object();


    /**
     * Constructs a {@link Prim}.
     *
     * @param gPanel The {@link javax.swing.JPanel} containing a graph.
     * @throws IllegalArgumentException If {@code gPanel} is null.
     */
    public Prim(GraphPanel gPanel) throws IllegalArgumentException {

        if (gPanel == null) {
            throw new IllegalArgumentException("Error: GraphPanel is null");
        } else {
            this.gPanel = gPanel;
            inMST = new boolean[gPanel.nodeCount];
            reached = new HashSet<>(gPanel.nodeCount);
        }
    }


    /**
     * If user has stopped the animation, clears the animation .
     *
     * @return True if user has stopped the animation, false otherwise.
     */
    private boolean isStopped() {

        if (gPanel.stop) {
            // Clear animation
            Arrays.fill(gPanel.path, Integer.MAX_VALUE);
            gPanel.visitedEdges = new HashSet<>(gPanel.nodeCount);
            gPanel.repaint();
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
            while (gPanel.pause) {
                try {
                    pauseLock.wait(100);
                } catch (InterruptedException e) {
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

        // Update animation
        gPanel.repaint();
        try {
            TimeUnit.MILLISECONDS.sleep(gPanel.speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Finds the closest reachable edge that is reachable from the current tree.
     *
     * @return Closest reachable edge for current tree. Returns null if no
     * unvisited edge exists.
     */
    private DefaultWeightedEdge getLeastEdge() {

        double leastDist = Double.POSITIVE_INFINITY;
        DefaultWeightedEdge leastEdge = null;

        // Look at all reachable edges from current tree
        Set<DefaultWeightedEdge> reachableEdges = new HashSet<>();
        for (DefaultWeightedEdge edge : reached) {
            int edgeSource = gPanel.graph.getEdgeSource(edge);
            int edgeTarget = gPanel.graph.getEdgeTarget(edge);

            reachableEdges.addAll(gPanel.graph.edgesOf(edgeSource));
            reachableEdges.addAll(gPanel.graph.edgesOf(edgeTarget));
            for (DefaultWeightedEdge adjEdge : reachableEdges) {
                int adjEdgeSource = gPanel.graph.getEdgeSource(adjEdge);
                int adjEdgeTarget = gPanel.graph.getEdgeTarget(adjEdge);
                // Ignore edges to nodes that are already in MST
                if (inMST[adjEdgeSource] && inMST[adjEdgeTarget]) continue;

                double adjDist = gPanel.graph.getEdgeWeight(adjEdge);
                if (adjDist < leastDist) {
                    leastDist = adjDist;
                    leastEdge = adjEdge;
                }
            }
        }
        inMST[gPanel.graph.getEdgeSource(leastEdge)] = true;
        inMST[gPanel.graph.getEdgeTarget(leastEdge)] = true;

        return leastEdge;
    }


    /**
     * Uses a Prim's algorithm implementation to find the MST of a
     * {@link GraphPanel#graph}.
     */
    private void prim() {

        // Start at the smallest edge
        PriorityQueue<DefaultWeightedEdge> edgesPQ = new PriorityQueue<>((e1, e2) ->
        {
            Double e1Weight = gPanel.graph.getEdgeWeight(e1);
            Double e2Weight = gPanel.graph.getEdgeWeight(e2);
            return e1Weight.compareTo(e2Weight);
        });
        edgesPQ.addAll(gPanel.graph.edgeSet());
        DefaultWeightedEdge edge = edgesPQ.poll();
        gPanel.visitedEdges.add(edge);
        reached.add(edge);

        // Build MST
        while (gPanel.visitedEdges.size() < gPanel.nodeCount - 1) {
            // Check if user has stopped or paused algorithm
            if (isStopped()) return;
            checkForPause();

            // Find and add least costly edge to MST
            edge = getLeastEdge();
            if (edge == null) return;
            gPanel.visitedEdges.add(edge);
            reached.add(edge);
            animate();
        }

    }


    /**
     * Starts the algorithm process.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        prim();
    }
}


