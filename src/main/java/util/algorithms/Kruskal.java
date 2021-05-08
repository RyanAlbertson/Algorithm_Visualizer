package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.PriorityQueue;

/**
 * Implements Kruskal's algorithm to find the minimum spanning tree of a
 * {@link org.jgrapht.graph.DefaultUndirectedWeightedGraph}.
 *
 * @author Ryan Albertson
 */
public class Kruskal extends Algorithm {

    private final int[] parent;


    /**
     * Constructor.
     * @param gPanel {@link GraphPanel}.
     */
    public Kruskal(GraphPanel gPanel) {

        super(gPanel);
        parent = new int[gPanel.nodeCount];
    }


    /**
     * Traces through edges of a set of edges to find the root node.
     * @param node Search starts at this node.
     * @return The root of the set that {@code node} belongs to.
     */
    protected int findParent(int node) {

        if (parent[node] != node) {
            return findParent(parent[node]);
        }
        return node;
    }


    protected void runAlgorithm() {

        // All edges in the graph (min. priority by weight)
        PriorityQueue<DefaultWeightedEdge> edgesPQ = new PriorityQueue<>((e1, e2) ->
        {
            Double e1Weight = gPanel.graph.getEdgeWeight(e1);
            Double e2Weight = gPanel.graph.getEdgeWeight(e2);
            return e1Weight.compareTo(e2Weight);
        });
        edgesPQ.addAll(gPanel.graph.edgeSet());

        // Initialize each node's parent node to itself.
        for (int node = 0; node < gPanel.nodeCount; node++) {
            parent[node] = node;
        }

        // Build the MST
        int sizeMST = 0;
        while (sizeMST < gPanel.nodeCount - 1) {
            DefaultWeightedEdge leastEdge = edgesPQ.poll();

            // Find root nodes for each pair of edges.
            int sourceNode = gPanel.graph.getEdgeSource(leastEdge);
            int targetNode = gPanel.graph.getEdgeTarget(leastEdge);
            int set1Root = findParent(sourceNode);
            int set2Root = findParent(targetNode);

            // Cycle exists, ignore this edge.
            if (set1Root == set2Root) continue;

            // Add edge to MST. (mark as visited)
            gPanel.visitedEdges.add(leastEdge);

            parent[set2Root] = set1Root;
            sizeMST++;
            // Check if user has stopped or paused algorithm
            animate();
            if (isStopped()) return;
        }
    }


    protected void runAlgorithm(Integer node) {

        // This signature isn't needed for this algorithm.
    }
}
