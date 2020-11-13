package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class Kruskal implements Runnable {

    private final GraphPanel gPanel;
    private final Object pauseLock = new Object();
    private int[] parent;


    /**
     *
     * @param gPanel
     */
    public Kruskal(GraphPanel gPanel) {

        if (gPanel == null) {
            throw new IllegalArgumentException("GraphPanel is null");
        } else {
            this.gPanel = gPanel;
            parent = new int[gPanel.nodeCount];
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
            Arrays.fill(gPanel.visited, false);
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
     * Traces through edges of a set of edges to find the root node.
     * @param node Search starts at this node.
     * @return The root of the set that {@code node} belongs to.
     */
    private int findParent(int node) {

        if (parent[node] != node) {
            return findParent(parent[node]);
        }
        return node;
    }


    /**
     *
     */
    private void kruskal() {

        // All edges in the graph (min. priority by weight)
        PriorityQueue<DefaultWeightedEdge> edgesPQ = new PriorityQueue<>((e1, e2) ->
        {
            Double e1Weight = gPanel.graph.getEdgeWeight(e1);
            Double e2Weight = gPanel.graph.getEdgeWeight(e2);
            return e1Weight.compareTo(e2Weight);
        });
        edgesPQ.addAll(gPanel.graph.edgeSet());

        // Initialize each node's parent to itself.
        for (int node = 0; node < gPanel.nodeCount; node++) {
            parent[node] = node;
        }

        // Build the MST
        int sizeMST = 0;
        while (sizeMST < gPanel.nodeCount - 1) {
            // Check if user has stopped or paused algorithm
            if (isStopped()) return;
            checkForPause();

            DefaultWeightedEdge leastEdge = edgesPQ.poll();

            // Find root nodes for each set of edges.
            int sourceNode = gPanel.graph.getEdgeSource(leastEdge);
            int targetNode = gPanel.graph.getEdgeTarget(leastEdge);
            int set1Root = findParent(sourceNode);
            int set2Root = findParent(targetNode);

            // Cycle exists, ignore this edge.
            if (set1Root == set2Root) continue;

            // Add edge to MST. (mark as visited)
            gPanel.visited[sourceNode] = true;
            gPanel.visited[targetNode] = true;

            parent[set2Root] = set1Root;
            sizeMST++;

            animate();
        }
    }


    /**
     *
     */
    @Override
    public void run() {

        kruskal();
    }
}


