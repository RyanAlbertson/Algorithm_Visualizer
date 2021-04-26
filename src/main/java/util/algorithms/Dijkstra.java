package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;


/**
 * Implements Dijkstra's algorithm to find a shortest path from a
 * {@link GraphPanel#sourceNode}source node to all other nodes. Note that the
 * input graph is directed but I choose to implement an undirected algorithm
 * in an effort to make the animations more appealing.
 *
 * @author Ryan Albertson
 */
public class Dijkstra extends Algorithm {


    public Dijkstra(GraphPanel gPanel) {

        super(gPanel);
    }


    protected boolean isStopped() {

        return gPanel.stop;
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
        Integer currentNode;
        Integer adjNode;

        while (!nodesQueue.isEmpty()) {
            currentNode = nodesQueue.poll();
            edgesPQ.addAll(gPanel.graph.edgesOf(currentNode));
            while (!edgesPQ.isEmpty()) {
                // Check if user has stopped or paused algorithm
                if (isStopped()) return;
                checkForPause();

                // Find nearest adjacent node from available adjacent edges.
                DefaultWeightedEdge leastEdge = edgesPQ.poll();
                adjNode = gPanel.graph.getEdgeTarget(leastEdge);
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
                animate();
            }
            isExplored[currentNode] = true;
        }
    }


    public void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
