package main.java.util.algorithms;

import main.java.GraphPanel;

import java.util.concurrent.TimeUnit;


/**
 *
 */
public class DepthFirstSearch implements Runnable {


    private GraphPanel graphPanel;


    /**
     * Starts the algorithm.
     */
    public void run() {
        DFS(graphPanel.sourceNode);
    }


    /**
     * @param node Search will begin from this node.
     */
    private void DFS(Integer node) {

        int i = 0;
        while (i < 10) {
            System.out.print("+");
            i++;

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // continuously check pause and stop

            // when to be calling repaint?

        }
    }


    /**
     * Constructs a {@link DepthFirstSearch} object.
     *
     * @param graphPanel The {@link javax.swing.JPanel} containing a graph.
     * @throws IllegalArgumentException If {@code graphPanel} is null.
     */
    public DepthFirstSearch(GraphPanel graphPanel) {

        if (graphPanel == null) {
            throw new IllegalArgumentException("Error: GraphPanel is null");
        }

        if (graphPanel.initialStart) {
            this.graphPanel = graphPanel;
            graphPanel.initialStart = false;


            graphPanel.initialStart = true;
        } else graphPanel.stop = graphPanel.pause = false;
    }
}
