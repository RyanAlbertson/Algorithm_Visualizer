package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayDeque;
import java.util.Deque;


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


    /**
     * Constructor.
     * @param gPanel {@link GraphPanel}.
     */
    public BreadthFirstSearch(GraphPanel gPanel) {

        super(gPanel);
        visited = new boolean[gPanel.nodeCount];
    }


    protected void runAlgorithm(Integer node) {

        Deque<Integer> queue = new ArrayDeque<>();
        queue.addLast(node);
        visited[node] = true;

        search:
        while (!queue.isEmpty()) {
            Integer currentNode = queue.removeFirst();
            // source node equals target node
            if (currentNode.equals(gPanel.targetNode)) {
                checkForPause();
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
                    // Check if user has stopped or paused algorithm
                    if (isStopped()) return;
                    animate();
                    break search;
                }

                // Explore unvisited neighbors of currentNode
                if (!visited[adjNode]) {
                    visited[adjNode] = true;
                    gPanel.path[adjNode] = currentNode;
                    queue.addLast(adjNode);
                    gPanel.visitedEdges.add(edge);
                    // Check if user has stopped or paused algorithm
                    if (isStopped()) return;
                    animate();
                }
            }
        }
    }


    protected void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
