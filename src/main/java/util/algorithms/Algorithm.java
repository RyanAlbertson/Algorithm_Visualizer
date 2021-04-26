package main.java.util.algorithms;

import main.java.GraphPanel;

import java.util.concurrent.TimeUnit;

public abstract class Algorithm implements Runnable {


    protected GraphPanel gPanel;
    protected final Object pauseLock = new Object();


    /**
     * If user has stopped the animation, clears the animation.
     *
     * @return True if user has stopped the animation, false otherwise.
     */
    protected abstract boolean isStopped();


    /**
     * Starts an algorithm animation.
     */
    protected abstract void runAlgorithm();


    /**
     * Starts a shortest path algorithm starting at the given source {@code node}.
     *
     * @param node Source node of the search.
     */
    protected abstract void runAlgorithm(Integer node);


    /**
     * Constructs an {@link Algorithm}.
     *
     * @param gPanel The {@link javax.swing.JPanel} containing a graph.
     * @throws IllegalArgumentException If {@code graphPanel} is null.
     */
    protected Algorithm(GraphPanel gPanel) throws IllegalArgumentException {

        if (gPanel == null) {
            throw new IllegalArgumentException("GraphPanel is null");
        } else this.gPanel = gPanel;
    }


    /**
     * Checks if user has paused the animation. If so, the animation process is
     * held until the user has unpaused it.
     */
    protected void checkForPause() {

        while (gPanel.pause) {
            try {
                pauseLock.wait(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Repaints the animation within the {@link GraphPanel}. Does it slowly such
     * that the user can visualize the algorithm stepping through.
     */
    protected void animate() {

        try {
            // Update animation
            gPanel.repaint();
            TimeUnit.MILLISECONDS.sleep(gPanel.speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Starts the algorithm.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        if (gPanel.isShortPathAlg) runAlgorithm(gPanel.sourceNode);
        else runAlgorithm();
    }
}
