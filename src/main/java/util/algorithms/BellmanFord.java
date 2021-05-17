package main.java.util.algorithms;


import main.java.GraphPanel;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Implements a Bellman-Ford algorithm to find a shortest path from a
 * {@link GraphPanel#sourceNode} to a {@link  GraphPanel#targetNode}.
 *
 * @author Ryan Albertson
 */
public class BellmanFord extends Algorithm {


    /**
     * Constructor.
     * @param gPanel {@link GraphPanel}.
     */
    public BellmanFord(GraphPanel gPanel) {

        super(gPanel);
    }


    /**
     * @see Algorithm#animate()
     */
    @Override
    protected void animate() {

        try {
            // Update animation
            gPanel.repaint();
            // Make Bellman-Ford animation faster
            int speed = gPanel.speed;
            if (speed > 0) {
                speed -= 325;
            }
            TimeUnit.MILLISECONDS.sleep(speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        checkForPause();
    }


    public void runAlgorithm(Integer node) {

        // Init min dist to each node from the source node
        double[] distanceTo = new double[gPanel.nodeCount];
        Arrays.fill(distanceTo, Double.POSITIVE_INFINITY);
        distanceTo[node] = 0.0;

        for (int i = 0; i < gPanel.nodeCount; i++) {
            gPanel.visitedEdges.clear();
            for (DefaultWeightedEdge edge : gPanel.graph.edgeSet()) {

                gPanel.visitedEdges.add(edge);

                // Update predecessor if there's a shorter path to the node
                int edgeSource = gPanel.graph.getEdgeSource(edge);
                int edgeTarget = gPanel.graph.getEdgeTarget(edge);
                double currDist = distanceTo[edgeTarget];
                double newDist = distanceTo[edgeSource] +
                        gPanel.graph.getEdgeWeight(edge);
                if (newDist < currDist) {
                    distanceTo[edgeTarget] = newDist;
                    gPanel.path[edgeTarget] = edgeSource;
                }

                // Consider the other direction of current edge
                edgeSource = gPanel.graph.getEdgeTarget(edge);
                edgeTarget = gPanel.graph.getEdgeSource(edge);
                currDist = distanceTo[edgeTarget];
                newDist = distanceTo[edgeSource] +
                        gPanel.graph.getEdgeWeight(edge);
                if (newDist < currDist) {
                    distanceTo[edgeTarget] = newDist;
                    gPanel.path[edgeTarget] = edgeSource;
                }

                // Check if user has stopped or paused algorithm
                animate();
                if (isStopped()) return;
            }
        }
    }


    public void runAlgorithm() {

        // This signature isn't needed for this algorithm.
    }
}
