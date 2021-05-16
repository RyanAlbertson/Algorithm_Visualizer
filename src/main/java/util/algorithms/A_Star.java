package main.java.util.algorithms;


import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Implements an A* search algorithm to find a shortest path from a
 * {@link GraphPanel#sourceNode} to a {@link  GraphPanel#targetNode}.
 *
 * @author Ryan Albertson
 */
public class A_Star extends Algorithm {


    /**
     * Constructor.
     * @param gPanel {@link GraphPanel}.
     */
    public A_Star(GraphPanel gPanel) {

        super(gPanel);
    }


    public void runAlgorithm(Integer node) {

        // Init distance to reach each node through the search
        double[] distanceTo = new double[gPanel.nodeCount];
        Arrays.fill(distanceTo, Double.POSITIVE_INFINITY);
        distanceTo[node] = 0.0;

        // Init estimated remaining distance to target node from each node
        double[] distanceAfter = new double[gPanel.nodeCount];
        double targetX = gPanel.nodeCoords.get(gPanel.targetNode)[0];
        double targetY = gPanel.nodeCoords.get(gPanel.targetNode)[1];
        for (int i = 0; i < gPanel.nodeCount; i++) {
            double iX = gPanel.nodeCoords.get(i)[0];
            double iY = gPanel.nodeCoords.get(i)[1];
            double dist = Math.sqrt(Math.pow(Math.abs(iY - targetY), 2) +
                    Math.pow(Math.abs(iX - targetX), 2));
            distanceAfter[i] = dist;
        }

        // Node priority considers the sum: distance[i] + distanceAfter[i]
        Double[] prio = new Double[gPanel.nodeCount];
        for (int i = 0; i < gPanel.nodeCount; i++) prio[i] = Double.MAX_VALUE;

        boolean[] isExplored = new boolean[gPanel.nodeCount];

        // Order of nodes to be visited
        PriorityQueue<Integer> nodesQueue = new PriorityQueue<>((node1, node2) ->
                prio[node1].compareTo(prio[node2]));
        nodesQueue.add(node);

        // Store nodes & edges from previous iterations of loop. For animation purposes
        Map<Integer, Integer> prevNode = new HashMap<>();
        Map<Integer, DefaultWeightedEdge> edgeTo = new HashMap<>();

        while (!nodesQueue.isEmpty()) {
            Integer currentNode = nodesQueue.poll();
            isExplored[currentNode] = true;
            DefaultWeightedEdge edgeToCurrentNode = edgeTo.get(currentNode);
            if (null != edgeToCurrentNode) gPanel.visitedEdges.add(edgeToCurrentNode);
            Integer prev = prevNode.get(currentNode);
            if (null != prev) gPanel.path[currentNode] = prev;

            for (DefaultWeightedEdge edgeToAdjNode : gPanel.graph.edgesOf(currentNode)) {
                if (gPanel.visitedEdges.contains(edgeToAdjNode)) continue;
                Integer adjNode = gPanel.graph.getEdgeTarget(edgeToAdjNode);
                // Account for directed edges
                if (adjNode.equals(currentNode)) {
                    adjNode = gPanel.graph.getEdgeSource(edgeToAdjNode);
                }
                if (isExplored[adjNode]) continue;
                // Update adjNode's distance if this path is shorter than current
                double currentDist = distanceTo[adjNode];
                double newDist = distanceTo[currentNode] +
                        gPanel.graph.getEdgeWeight(edgeToAdjNode);
                if (newDist < currentDist) {
                    distanceTo[adjNode] = newDist;
                }
                // Update total distance if this estimated path is shorter
                double totalDist = distanceTo[adjNode] + distanceAfter[adjNode];
                if (totalDist < prio[adjNode]) {
                    prio[adjNode] = totalDist;
                    nodesQueue.add(adjNode);
                    prevNode.put(adjNode, currentNode);
                    edgeTo.put(adjNode, edgeToAdjNode);
                }
            }
            // Check if user has stopped or paused algorithm
            animate();
            if (isStopped()) return;
            // Stop algorithm if a path to the target has been found
            if (currentNode.equals(gPanel.targetNode)) break;
        }
    }


    public void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
