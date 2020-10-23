package main.java.util.algorithms;

import main.java.GraphPanel;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * Implements a depth first search to find a path between a
 * {@link GraphPanel#sourceNode} and {@link GraphPanel#targetNode}.
 * Note that DFS only finds a shortest path on a tree graph, and the given graph
 * is connected but likely cylic. Therefore this algorithm only provides a single
 * path, out of all possible, which itself is probabilistically not the shortest.
 *
 * @author Ryan Albertson
 */
public class DepthFirstSearch implements Runnable {


    private final GraphPanel graphPanel;
    private final Object pauseLock = new Object();
    private boolean targetFound = false;


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
     * Recursively checks the given node's neighbors until the
     * {@link GraphPanel#targetNode} is found. The preceding node for every node
     * is stored such that a path can be traced from the target node to the
     * {@link GraphPanel#sourceNode}.
     *
     * @param node Initial argument is the source node.
     */
    private void dfs(Integer node) {

        // Check if user has stopped or paused algorithm
        if (isStopped()) return;
        checkForPause();

        // Stop DFS when target is found
        if (node.equals(graphPanel.targetNode)) targetFound = true;
        if (targetFound) {
            animate();
            return;
        }

        graphPanel.visited[node] = true;
        animate();

        for (Integer adj : graphPanel.adjNodes.get(node)) {
            if (!graphPanel.visited[adj]) {
                graphPanel.path[adj] = node;
                dfs(adj);
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
            dfs(graphPanel.sourceNode);
        }
    }


    /**
     * Constructs a {@link DepthFirstSearch}.
     *
     * @param graphPanel The {@link javax.swing.JPanel} containing a graph.
     * @throws IllegalArgumentException If {@code graphPanel} is null.
     */
    public DepthFirstSearch(GraphPanel graphPanel) {

        if (graphPanel == null) {
            throw new IllegalArgumentException("GraphPanel is null");
        } else this.graphPanel = graphPanel;
    }
}
