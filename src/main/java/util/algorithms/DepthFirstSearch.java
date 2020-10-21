package main.java.util.algorithms;

import main.java.GraphPanel;

import java.util.concurrent.TimeUnit;


/**
 *
 */
public class DepthFirstSearch implements Runnable {


    private GraphPanel graphPanel = null;
    private final Object pauseLock = new Object();
    int i = 0;


    /**
     * Starts the algorithm process.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        DFS(graphPanel.sourceNode);
    }


    /**
     * @return
     */
    private boolean isStopped() {

        return graphPanel.stop;
    }


    /**
     *
     */
    private void checkForPause() {

        synchronized (pauseLock) {
            boolean paused;
            while (paused = graphPanel.pause) {
                try {
                    if (paused) pauseLock.wait(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @param node Search will begin from this node.
     */
    private void DFS(Integer node) {

        int c = 0;
        while (c < 10) {
            if (isStopped()) return;
            checkForPause();
            System.out.print(i);
            i++;
            c++;

            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        i *= 2;
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
