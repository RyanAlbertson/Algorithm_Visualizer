package main.java.util.algorithms;

import main.java.GraphPanel;


/**
 *
 */
public class DepthFirstSearch extends GraphPanel {


    /**
     *
     */
    public static void depthFirstSearch(GraphPanel graphPanel) {

        if (graphPanel.initialStart) {
            graphPanel.initialStart = false;
            DFS(graphPanel.sourceNode);
            graphPanel.initialStart = true;
        } else graphPanel.stop = graphPanel.pause = false;
    }


    /**
     * @param node
     */
    private static void DFS(Integer node) {


        // continuously check pause and stop

        // when to be calling repaint?

    }
}
