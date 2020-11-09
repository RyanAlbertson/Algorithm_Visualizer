package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * Implements a depth first search to find a path between a
 * {@link GraphPanel#sourceNode} and {@link GraphPanel#targetNode}.
 * Note that DFS only finds a shortest path on a tree graph, but the input graph
 * is connected and likely cylic. Therefore this algorithm only provides a single
 * path, out of all possible, which itself is probabilistically not the shortest.
 *
 * @author Ryan Albertson
 */
public class DepthFirstSearch implements Runnable {

    private final GraphPanel gPanel;
    private final Object pauseLock = new Object();
    private boolean targetFound = false;


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
     * Recursively checks the given node's neighbors until the
     * {@link GraphPanel#targetNode} is found. The preceding node for every node
     * is stored such that a path can be traced from the
     * {@link GraphPanel#targetNode} to the {@link GraphPanel#sourceNode}.
     *
     * @param node DFS is started at this node.
     */
    private void dfs(Integer node) {

        // Stop algorithm if target has been found further down the recursion
        if (targetFound) return;
        // Check if user has stopped or paused algorithm
        if (isStopped()) return;
        checkForPause();

        gPanel.visited[node] = true;
        animate();

        // Stop DFS when target is found
        if (node.equals(gPanel.targetNode)) {
            targetFound = true;
            return;
        }

        // Recursively check adjacent nodes
        for (DefaultWeightedEdge edge : gPanel.graph.edgesOf(node)) {
            Integer adjNode = gPanel.graph.getEdgeTarget(edge);
            if (adjNode.equals(node)) adjNode =
                    gPanel.graph.getEdgeSource(edge);
            // Convert directed edges to undirected
            if (adjNode.equals(node)) adjNode = gPanel.graph.getEdgeSource(edge);
            if (!gPanel.visited[adjNode]) {
                gPanel.path[adjNode] = node;
                dfs(adjNode);
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

        dfs(gPanel.sourceNode);
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
        } else this.gPanel = graphPanel;
    }
}
