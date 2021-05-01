package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;


/**
 * Implements Dijkstra's algorithm to find a shortest path from a
 * {@link GraphPanel#sourceNode} to a {@link  GraphPanel#targetNode}.
 *
 * @author Ryan Albertson
 */
public class Dijkstra extends Algorithm {


    /**
     * Constructor.
     * @param gPanel {@link GraphPanel}.
     */
    public Dijkstra(GraphPanel gPanel) {

        super(gPanel);
    }


    public void runAlgorithm(Integer node) {

        double[] distanceTo = new double[gPanel.nodeCount];
        Arrays.fill(distanceTo, Double.POSITIVE_INFINITY);
        distanceTo[node] = 0.0;

        // Order of nodes to be visited
        Deque<Integer> nodesQueue = new ArrayDeque<>(
                gPanel.graph.vertexSet().size());
        nodesQueue.addLast(node);

        // Order of adjacent edges to be explored at each node (min. priority bv weight)
        PriorityQueue<DefaultWeightedEdge> edgesPQ = new PriorityQueue<>((e1, e2) ->
        {
            Double e1Weight = gPanel.graph.getEdgeWeight(e1);
            Double e2Weight = gPanel.graph.getEdgeWeight(e2);
            return e1Weight.compareTo(e2Weight);
        });
        boolean[] isExplored = new boolean[gPanel.nodeCount];

        while (!nodesQueue.isEmpty()) {
            Integer currentNode = nodesQueue.poll();
            edgesPQ.addAll(gPanel.graph.edgesOf(currentNode));
            while (!edgesPQ.isEmpty()) {
                // Find nearest adjacent node from available adjacent edges
                DefaultWeightedEdge leastEdge = edgesPQ.poll();
                if (gPanel.visitedEdges.contains(leastEdge)) continue;
                Integer adjNode = gPanel.graph.getEdgeTarget(leastEdge);
                // Account for directed edges
                if (adjNode.equals(currentNode)) {
                    adjNode = gPanel.graph.getEdgeSource(leastEdge);
                }
                if (isExplored[adjNode]) continue;
                nodesQueue.addLast(adjNode);
                gPanel.visitedEdges.add(leastEdge);

                // Update adjNode's distance if proposed predecessor is shorter
                double currentDist = distanceTo[adjNode];
                double newDist = distanceTo[currentNode] +
                        gPanel.graph.getEdgeWeight(leastEdge);
                if (newDist < currentDist) {
                    distanceTo[adjNode] = newDist;
                    gPanel.path[adjNode] = currentNode;
                }
                // Check if user has stopped or paused algorithm
                if (isStopped()) return;
                animate();
            }
            isExplored[currentNode] = true;
        }
    }


    public void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
