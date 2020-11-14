package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Implements a breadth first search to find a shortest path between a
 * {@link GraphPanel#sourceNode} and {@link  GraphPanel#targetNode}. Note that
 * the graph input is weighted and undirected. This implementation ignores the
 * weights.
 *
 * @author Ryan Albertson
 */
public class BreadthFirstSearch implements Runnable {

    private final GraphPanel gPanel;
    private final Object pauseLock = new Object();
    private final boolean[] visited;


    /**
     * Constructs a {@link BreadthFirstSearch}.
     *
     * @param gPanel The {@link javax.swing.JPanel} containing a graph.
     * @throws IllegalArgumentException If {@code graphPanel} is null.
     */
    public BreadthFirstSearch(GraphPanel gPanel) throws IllformedLocaleException {

        if (gPanel == null) {
            throw new IllegalArgumentException("Error: GraphPanel is null");
        } else {
            this.gPanel = gPanel;
            visited = new boolean[gPanel.nodeCount];
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
     * Search starts at {@code node} and iterates by layer of adjacent nodes.
     * It stops when the {@link GraphPanel#targetNode} is found. The preceding
     * node is stored for each node such that a path back to
     * {@link GraphPanel#sourceNode} can be traced.
     *
     * @param node Source node of the search.
     */
    private void bfs(Integer node) {

        Deque<Integer> queue = new ArrayDeque<>();
        queue.addLast(node);
        visited[node] = true;

        search:
        while (!queue.isEmpty()) {
            // Check if user has stopped or paused algorithm
            if (isStopped()) return;
            checkForPause();

            Integer currentNode = queue.removeFirst();
            // source node equals target node
            if (currentNode.equals(gPanel.targetNode)) {
                animate();
                break;
            }

            for (DefaultWeightedEdge edge : gPanel.graph.edgesOf(currentNode)) {

                Integer adjNode = gPanel.graph.getEdgeTarget(edge);
                if (adjNode.equals(currentNode)) adjNode =
                        gPanel.graph.getEdgeSource(edge);

                // Stop BFS when target is found
                if (adjNode.equals(gPanel.targetNode)) {
                    gPanel.path[adjNode] = currentNode;
                    animate();
                    break search;
                }

                // Explore unvisited neighbors of currentNode
                if (!visited[adjNode]) {
                    visited[adjNode] = true;
                    gPanel.path[adjNode] = currentNode;
                    queue.addLast(adjNode);
                    gPanel.visitedEdges.add(edge);
                    animate();
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

        bfs(gPanel.sourceNode);
    }
}
