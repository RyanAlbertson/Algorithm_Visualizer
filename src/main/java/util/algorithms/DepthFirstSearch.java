package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


/**
 * Implements a depth first search to find a path between a
 * {@link GraphPanel#sourceNode} and {@link GraphPanel#targetNode}.
 *
 * @author Ryan Albertson
 */
public class DepthFirstSearch extends Algorithm {

    private final boolean[] visited;
    private final Map<Integer, Integer> prev;


    public DepthFirstSearch(GraphPanel gPanel) {

        super(gPanel);
        visited = new boolean[gPanel.nodeCount];
        prev = new HashMap<>(gPanel.nodeCount);
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

        // ALGORITHM DOESNT CHECK EVERY EDGE. ONLY ONE EDGE BETWEEN EVERY PAIR OF NODES.
        // NEED TO VISIT EVERY NODE WHILE NOT REVISITING PREVIOUSLY VISITED NODES.

        Stack<Integer> stack = new Stack<>();
        stack.push(node);
        prev.put(node, node);

        while (!stack.empty()) {
            Integer currNode = stack.pop();
            Integer prevNode = prev.get(currNode);

            visited[currNode] = true;
            gPanel.path[currNode] = prevNode;
            DefaultWeightedEdge edge = gPanel.graph.getEdge(prevNode, currNode);
            if (null == edge) edge = gPanel.graph.getEdge(currNode, prevNode);
            if (null != edge) gPanel.visitedEdges.add(edge);
            // Check if user has stopped or paused algorithm
            if (isStopped()) return;
            animate();

            // Stop DFS when target is found
            if (currNode.equals(gPanel.targetNode)) return;

            // Search adjacent nodes
            for (DefaultWeightedEdge adjEdge : gPanel.graph.edgesOf(currNode)) {
                Integer adjNode = gPanel.graph.getEdgeTarget(adjEdge);
                // Convert directed edges to undirected
                if (adjNode.equals(currNode)) adjNode = gPanel.graph.getEdgeSource(adjEdge);
                if (!visited[adjNode]) {
                    stack.push(adjNode);
                    prev.put(adjNode, currNode);
                }
            }
        }
    }


    protected void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
