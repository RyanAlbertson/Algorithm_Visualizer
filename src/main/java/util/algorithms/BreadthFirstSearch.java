package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;


/**
 * Implements a breadth first search to find a shortest path between a
 * {@link GraphPanel#sourceNode} and {@link  GraphPanel#targetNode}. Note that
 * the graph input is weighted and undirected. This implementation ignores the
 * weights.
 *
 * @author Ryan Albertson
 */
public class BreadthFirstSearch extends Algorithm {


    private final boolean[] visited;


    public BreadthFirstSearch(GraphPanel gPanel) {

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


    protected void runAlgorithm(Integer node) {

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


    protected void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
