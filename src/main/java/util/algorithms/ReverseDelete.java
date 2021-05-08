package main.java.util.algorithms;

import main.java.GraphPanel;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Implements a Reverse Delete algorithm to find the mimimum spanning tree of a
 * {@link org.jgrapht.graph.DefaultUndirectedWeightedGraph}.
 *
 * @author Ryan Albertson
 */
public class ReverseDelete extends Algorithm {

    private final DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge> graphCopy;

    /**
     * Constructor
     * @param gPanel {@link GraphPanel}.
     */
    public ReverseDelete(GraphPanel gPanel) {

        super(gPanel);
        graphCopy = (DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge>) gPanel.graph.clone();
    }


    /**
     * @param graph {@link DefaultUndirectedWeightedGraph} to check for connectivity.
     * @return True if {@code graph} is connected, otherwise false.
     */
    private static boolean isConnected(DefaultUndirectedWeightedGraph<Integer,
            DefaultWeightedEdge> graph) {

        Set<Integer> vertices = graph.vertexSet();

        // Edges cases
        if (vertices.size() <= 1) return true;
        if (vertices.size() - 1 > graph.edgeSet().size()) return false;

        Set<Integer> visited = new HashSet<>();
        Deque<DefaultWeightedEdge> queue = new ArrayDeque<>();
        Integer startNode = vertices.iterator().next();

        // BFS to determine connectivity
        visited.add(startNode);
        graph.edgesOf(startNode).forEach(queue::addLast);
        while (visited.size() != vertices.size() && queue.size() != 0) {
            DefaultWeightedEdge edge = queue.pollFirst();
            Integer source = graph.getEdgeSource(edge);
            Integer target = graph.getEdgeTarget(edge);
            Integer adjNode = visited.contains(source) ? target : source;
            if (!visited.contains(adjNode)) {
                visited.add(adjNode);
                graph.edgesOf(adjNode).forEach(queue::addLast);
            }
        }
        return visited.size() == vertices.size();
    }


    /**
     * @see Algorithm#animate()
     */
    @Override
    protected void animate() {

        try {
            // Update animation
            gPanel.repaint();
            // Make reverse delete animation faster
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


    protected void runAlgorithm() {

        // All edges in the graph (decreasing order by weight)
        List<DefaultWeightedEdge> edges = new ArrayList<>(graphCopy.edgeSet().size());
        edges.addAll(graphCopy.edgeSet());
        edges.sort((e1, e2) ->
        {
            Double e1Weight = graphCopy.getEdgeWeight(e1);
            Double e2Weight = graphCopy.getEdgeWeight(e2);
            return e2Weight.compareTo(e1Weight);
        });

        // Initially mark all edges as visited.
        gPanel.visitedEdges.addAll(edges);

        // Remove largest edges if they don't disconnect graph
        for (DefaultWeightedEdge edge : edges) {
            int source = graphCopy.getEdgeSource(edge);
            int target = graphCopy.getEdgeTarget(edge);
            graphCopy.removeEdge(edge);

            // Ignore if this edge disconnects current graph
            if (!isConnected(graphCopy)) {
                graphCopy.addEdge(source, target);
            } else {
                // Otherwise remove edge from the graph
                gPanel.visitedEdges.remove(edge); // thread 1
                animate();                        // thread 2
                if (isStopped()) return;
            }
        }
    }


    protected void runAlgorithm(Integer node) {

        // This signature isn't needed for this algorithm.
    }
}
