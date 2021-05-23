package main.java.util.algorithms;


import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

/**
 * Implements a Floyd-Warshall algorithm to find a shortest path from a
 * {@link GraphPanel#sourceNode} to a {@link  GraphPanel#targetNode}.
 *
 * @author Ryan Albertson
 */
public class FloydWarshall extends Algorithm {
    

    /**
     * Constructor.
     * @param gPanel {@link GraphPanel}.
     */
    public FloydWarshall(GraphPanel gPanel) {

        super(gPanel);
    }


    /**
     * @param skip The animation pause will be effectively removed if skip is true.
     * @see Algorithm#animate()
     */
    protected void animate(boolean skip) {

        int speed = gPanel.speed > 0 ? gPanel.speed - 350 : gPanel.speed;
        if (skip) speed = 0;
        try {
            // Update animation
            gPanel.repaint();
            TimeUnit.MILLISECONDS.sleep(speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkForPause();
    }


    public void runAlgorithm(Integer node) {

        // distanceTo[i][j] is the min weight path from node i to node j
        double[][] distanceTo = new double[gPanel.nodeCount][gPanel.nodeCount];
        for (double[] row : distanceTo) Arrays.fill(row, Double.MAX_VALUE);

        // Pointers to next nodes in paths within the graph
        int[][] next = new int[gPanel.nodeCount][gPanel.nodeCount];
        for (int[] row : next) Arrays.fill(row, -1);

        // Init the weights of direct edges between node pairs
        for (DefaultWeightedEdge edge : gPanel.graph.edgeSet()) {
            int edgeSource = gPanel.graph.getEdgeSource(edge);
            int edgeTarget = gPanel.graph.getEdgeTarget(edge);

            distanceTo[edgeSource][edgeTarget] = gPanel.graph.getEdgeWeight(edge);
            next[edgeSource][edgeTarget] = edgeTarget;
            // Consider the other direction of current edge
            distanceTo[edgeTarget][edgeSource] = distanceTo[edgeSource][edgeTarget];
            next[edgeTarget][edgeSource] = edgeSource;
        }

        // For every node in the graph, iterate through every pair of nodes
        for (int k = 0; k < gPanel.nodeCount; k++) {
            // Check if we can reroute path i~j through node k
            for (int i = 0; i < gPanel.nodeCount; i++) {
                for (int j = 0; j < gPanel.nodeCount; j++) {
                    // Bypass animation pause if no updates were made
                    boolean skipAnimation = true;
                    // Update distance if shorter path is found between i~j
                    if (distanceTo[i][k] + distanceTo[k][j] < distanceTo[i][j]) {
                        distanceTo[i][j] = distanceTo[i][k] + distanceTo[k][j];
                        // Update pointer for this path
                        next[i][j] = next[i][k];

                        DefaultWeightedEdge edge = gPanel.graph.getEdge(i, k);
                        if (null == edge) edge = gPanel.graph.getEdge(k, i);
                        if (null != edge) {
                            gPanel.visitedEdges.add(edge);
                            skipAnimation = false;
                        }
                        edge = gPanel.graph.getEdge(k, j);
                        if (null == edge) edge = gPanel.graph.getEdge(j, k);
                        if (null != edge) {
                            gPanel.visitedEdges.add(edge);
                            skipAnimation = false;
                        }
                    } else {
                        DefaultWeightedEdge edge = gPanel.graph.getEdge(i, j);
                        if (null == edge) edge = gPanel.graph.getEdge(j, i);
                        if (null != edge) {
                            gPanel.visitedEdges.add(edge);
                            skipAnimation = false;
                        }
                    }
                    // Check if user has stopped or paused algorithm
                    animate(skipAnimation);
                    if (isStopped()) return;
                }
            }
            gPanel.visitedEdges.clear();

            int u = gPanel.sourceNode;
            int v = gPanel.targetNode;
            // If path exists
            if (next[u][v] != -1) {
                // Build path and then trace in reverse order for animation
                List<Integer> path = new LinkedList<>();
                path.add(u);
                while (u != v) {
                    u = next[u][v];
                    path.add(u);
                }
                ListIterator<Integer> it = path.listIterator(path.size());
                Integer curr = path.get(path.size() - 1);
                while (it.hasPrevious() && !curr.equals(gPanel.sourceNode)) {
                    int prev = it.previous();
                    gPanel.path[curr] = prev;
                    curr = prev;
                }

                // Check if user has stopped or paused algorithm
                animate(true);
                if (isStopped()) return;
            }
        }
    }


    public void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
