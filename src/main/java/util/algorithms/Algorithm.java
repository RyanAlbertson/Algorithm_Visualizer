package main.java.util.algorithms;

import main.java.GraphPanel;

import java.util.concurrent.TimeUnit;

public abstract class Algorithm implements Runnable {

    protected GraphPanel gPanel;
    private boolean isAlive;
    private final Object lock;
    private volatile boolean paused;
    protected volatile boolean stopped;


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
            throw new IllegalArgumentException("ERROR: GraphPanel is null");
        } else {
            this.gPanel = gPanel;
            lock = new Object();
            isAlive = true;
            paused = false;
            stopped = false;
        }
    }


    /**
     * Checks if user has paused the animation. If so, the animation process is
     * held until the user has unpaused it.
     */
    protected void checkForPause() {

        synchronized (lock) {
            while (paused) {
                try {
                    lock.wait(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @return True if animation is currently stopped. False otherwise.
     */
    protected boolean isStopped() {

        if (stopped) {
            // Clear the current animation
            gPanel.resetAnimation();
            isAlive = false;
            return true;
        }
        return false;
    }


    /**
     * @return True if animation is currently paused. False otherwise.
     */
    public boolean isPaused() {

        return paused;
    }


    /**
     * Repaints the animation within the {@link GraphPanel}. Does it slowly such
     * that the user can visualize the algorithm stepping through.
     */
    protected void animate() {

        checkForPause();
        try {
            // Update animation
            gPanel.repaint();
            TimeUnit.MILLISECONDS.sleep(gPanel.speed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Signals to pause the animation.
     */
    public void pause() {

        paused = true;
    }


    /**
     * Signals to unpause the animation.
     */
    public void unPause() {

        paused = false;
    }


    /**
     * Signals to stop the animation.
     */
    public void stop() {

        stopped = true;
    }


    /**
     * @return True if algorithm animation is live. False otherwise.
     */
    public boolean isAlive() {

        return isAlive;
    }


    /**
     * Starts the algorithm.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        isAlive = true;
        if (gPanel.isShortPathAlg) runAlgorithm(gPanel.sourceNode);
        else runAlgorithm();
        isAlive = false;
    }
}
