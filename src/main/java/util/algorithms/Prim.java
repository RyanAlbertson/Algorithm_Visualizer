package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;


/**
 * Implements a Prim's algorithm to find the minimum spanning tree of a
 * {@link org.jgrapht.graph.DefaultUndirectedWeightedGraph}.
 *
 * @author Ryan Albertson
 */
public class Prim extends Algorithm {


    private final Set<DefaultWeightedEdge> reached;
    private final boolean[] inMST;


    /**
     * Constructor.
     * @param gPanel {@link GraphPanel}.
     */
    public Prim(GraphPanel gPanel) {

        super(gPanel);
        inMST = new boolean[gPanel.nodeCount];
        reached = new HashSet<>(gPanel.nodeCount);
    }


    /**
     * Finds the closest reachable edge that is reachable from the current tree.
     *
     * @return Closest reachable edge for current tree. Returns null if no
     * unvisited edge exists.
     */
    private DefaultWeightedEdge getLeastEdge() {

        double leastDist = Double.POSITIVE_INFINITY;
        DefaultWeightedEdge leastEdge = null;

        // Look at all reachable edges from current tree
        Set<DefaultWeightedEdge> reachableEdges = new HashSet<>();
        for (DefaultWeightedEdge edge : reached) {
            int edgeSource = gPanel.graph.getEdgeSource(edge);
            int edgeTarget = gPanel.graph.getEdgeTarget(edge);

            reachableEdges.addAll(gPanel.graph.edgesOf(edgeSource));
            reachableEdges.addAll(gPanel.graph.edgesOf(edgeTarget));
            for (DefaultWeightedEdge adjEdge : reachableEdges) {
                int adjEdgeSource = gPanel.graph.getEdgeSource(adjEdge);
                int adjEdgeTarget = gPanel.graph.getEdgeTarget(adjEdge);
                // Ignore edges to nodes that are already in MST
                if (inMST[adjEdgeSource] && inMST[adjEdgeTarget]) continue;

                double adjDist = gPanel.graph.getEdgeWeight(adjEdge);
                if (adjDist < leastDist) {
                    leastDist = adjDist;
                    leastEdge = adjEdge;
                }
            }
        }
        inMST[gPanel.graph.getEdgeSource(leastEdge)] = true;
        inMST[gPanel.graph.getEdgeTarget(leastEdge)] = true;

        return leastEdge;
    }


    protected void runAlgorithm() {

        // Start at the smallest edge
        PriorityQueue<DefaultWeightedEdge> edgesPQ = new PriorityQueue<>((e1, e2) ->
        {
            Double e1Weight = gPanel.graph.getEdgeWeight(e1);
            Double e2Weight = gPanel.graph.getEdgeWeight(e2);
            return e1Weight.compareTo(e2Weight);
        });
        edgesPQ.addAll(gPanel.graph.edgeSet());
        DefaultWeightedEdge edge = edgesPQ.poll();
        gPanel.visitedEdges.add(edge);
        reached.add(edge);

        // Build MST
        while (gPanel.visitedEdges.size() < gPanel.nodeCount - 1) {
            // Find and add least costly edge to MST
            edge = getLeastEdge();
            if (edge == null) return;
            gPanel.visitedEdges.add(edge);
            reached.add(edge);
            // Check if user has stopped or paused algorithm
            animate();
            if (isStopped()) return;
        }
    }


    protected void runAlgorithm(Integer node) {

        // This signature isn't needed for this algorithm.
    }
}


