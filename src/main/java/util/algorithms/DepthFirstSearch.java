package main.java.util.algorithms;

import main.java.GraphPanel;

import java.util.concurrent.TimeUnit;


/**
 *
 */
public class DepthFirstSearch implements Runnable {


    private final GraphPanel graphPanel;
    private final Object pauseLock = new Object();


    /**
     * @return True if user has stopped the animation, false otherwise.
     */
    private boolean isStopped() {

        return graphPanel.stop;
    }


    /**
     * Checks if user has paused the animation. If so, the animation process is
     * held until the user has unpaused it.
     */
    private void checkForPause() {

        synchronized (pauseLock) {
            boolean paused;
            while (paused = graphPanel.pause) {
                try {
                    pauseLock.wait(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @param node Initial argument is the source node
     */
    private void dfs(Integer node) {

        // Check if user has stopped or paused algorithm
        if (isStopped()) return;
        checkForPause();

        if (node.equals(graphPanel.sourceNode)) return;

        graphPanel.visited[node] = true;

        try {
            graphPanel.repaint();
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Integer adj : graphPanel.adjNodes.get(node)) {
            if (!graphPanel.visited[adj]) {
                graphPanel.path[adj] = node;
                dfs(adj);
            }
        }
    }


    /**
     * Starts the algorithm process.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        // Don't start algorithm if user hasn't selected source & target nodes
        if (graphPanel.sourceNode != null || graphPanel.targetNode != null) {
            dfs(graphPanel.sourceNode);
        }
    }


    /**
     * Constructs a {@link DepthFirstSearch}.
     *
     * @param graphPanel The {@link javax.swing.JPanel} containing a graph.
     * @throws IllegalArgumentException If {@code graphPanel} is null.
     */
    public DepthFirstSearch(GraphPanel graphPanel) {

        if (graphPanel == null) {
            throw new IllegalArgumentException("Error: GraphPanel is null");
        } else {
            this.graphPanel = graphPanel;
        }
    }
}
