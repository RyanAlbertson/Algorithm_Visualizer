package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Arrays;
import java.util.HashSet;


/**
 * Implements a depth first search to find a path between a
 * {@link GraphPanel#sourceNode} and {@link GraphPanel#targetNode}.
 * Note that DFS only finds a shortest path on a tree graph, but the input graph
 * is connected and likely cylic. Therefore this algorithm only provides a single
 * path, out of all possible, which itself is probabilistically not the shortest.
 *
 * @author Ryan Albertson
 */
public class DepthFirstSearch extends Algorithm {

    private final boolean[] visited;
    private boolean targetFound = false;


    public DepthFirstSearch(GraphPanel gPanel) {

        super(gPanel);
        visited = new boolean[gPanel.nodeCount];
    }


    protected boolean isStopped() {

        if (gPanel.stop) {
            Arrays.fill(gPanel.path, Integer.MAX_VALUE);
            gPanel.visitedEdges = new HashSet<>(gPanel.nodeCount);
            gPanel.repaint();
            return true;
        }
        return false;
    }


    /**
     * Recursively checks the given node's neighbors until the
     * {@link GraphPanel#targetNode} is found. The preceding node for every node
     * is stored such that a path can be traced from the
     * {@link GraphPanel#targetNode} to the {@link GraphPanel#sourceNode}.
     *
     * @param node DFS is started at this node.
     */
    protected void runAlgorithm(Integer node) {

        // Stop algorithm if target has been found further down the recursion
        if (targetFound) return;
        // Check if user has stopped or paused algorithm
        if (isStopped()) return;
        checkForPause();

        visited[node] = true;

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
            if (!visited[adjNode]) {
                gPanel.visitedEdges.add(edge);
                gPanel.path[adjNode] = node;
                animate();
                runAlgorithm(adjNode);
            }
        }
    }


    protected void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
