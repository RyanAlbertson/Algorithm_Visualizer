package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;


/**
 * Implements a depth first search to find a path between a
 * {@link GraphPanel#sourceNode} and {@link GraphPanel#targetNode}.
 *
 * @author Ryan Albertson
 */
public class DepthFirstSearch extends Algorithm {

    private final boolean[] visited;
    private boolean targetFound;


    /**
     * Constructor.
     * @param gPanel {@link GraphPanel}.
     */
    public DepthFirstSearch(GraphPanel gPanel) {

        super(gPanel);
        visited = new boolean[gPanel.nodeCount];
        targetFound = false;
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

        // Stop DFS when target is found
        if (node.equals(gPanel.targetNode)) {
            targetFound = true;
            return;
        }
        visited[node] = true;

        // Recursively check adjacent nodes
        for (DefaultWeightedEdge edge : gPanel.graph.edgesOf(node)) {
            // Stop searching if target has been found further down the recursion
            if (targetFound) return;
            Integer adjNode = gPanel.graph.getEdgeTarget(edge);
            // Convert directed edges to undirected
            if (adjNode.equals(node)) adjNode = gPanel.graph.getEdgeSource(edge);
            if (!visited[adjNode]) {
                gPanel.visitedEdges.add(edge);
                gPanel.path[adjNode] = node;
                // Check if user has stopped or paused algorithm
                if (isStopped()) return;
                animate();
                runAlgorithm(adjNode);
            }
        }
    }


    protected void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
