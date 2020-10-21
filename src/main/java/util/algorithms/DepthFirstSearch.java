package main.java.util.algorithms;

import main.java.GraphPanel;

import java.util.concurrent.TimeUnit;


/**
 *
 */
public class DepthFirstSearch extends Thread {


    private GraphPanel graphPanel;
    private volatile boolean running = true;
    private volatile boolean paused;
    private final Object pauseLock = new Object();
    int i = 0;
    

    // REDO USING BELOW
    // https://stackoverflow.com/questions/11989589/how-to-pause-and-resume-a-thread-in-java-from-another-thread


    /**
     * Starts the algorithm process.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        DFS(graphPanel.sourceNode);
        graphPanel.initialStart = true;
    }


    public void stopAlgorithm() {

        running = false;
        // you might also want to interrupt() the Thread that is
        // running this Runnable, too, or perhaps call:
        // to unblock
    }

    public void pauseAlgorithm() {

        // you may want to throw an IllegalStateException if !running
        graphPanel.start = false;
        paused = true;
    }

    public void resumeAlgorithm() {

        synchronized (pauseLock) {
            running = true;
            graphPanel.start = false;
            paused = false;
            pauseLock.notifyAll(); // Unblocks thread
        }
    }


    private void checkForPause() {

        synchronized (pauseLock) {
            while (paused = graphPanel.pause) {
                try {
                    pauseLock.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            pauseLock.notify(); // needs to call from somewheres else
        }


//        if (paused) {
//            if (graphPanel.start) resume();
//        } else if (graphPanel.pause) pause();
//        if (graphPanel.stop) stop();

//        synchronized (pauseLock) {
//            if (!running) { // may have changed while waiting to
//                // synchronize on pauseLock
//                return;
//            }
//            if (paused) {
//                try {
//                    synchronized (pauseLock) {
//                        pauseLock.wait(); // will cause this Thread to block until
//                        // another thread calls pauseLock.notifyAll()
//                        // Note that calling wait() will
//                        // relinquish the synchronized lock that this
//                        // thread holds on pauseLock so another thread
//                        // can acquire the lock to call notifyAll()
//                        // (link with explanation below this code)
//                    }
//                } catch (InterruptedException e) {
//                    return;
//                }
//                if (!running) { // running might have changed since we paused
//                    return;
//                }
//            }
//        }
    }


    /**
     * @param node Search will begin from this node.
     */
    private void DFS(Integer node) {

        int c = 0;
        while (c < 10) {
            checkForPause();
            System.out.print(i);
            i++;
            c++;

            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            // continuously check pause and stop

            // when to be calling repaint?

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
        } else if (graphPanel.initialStart) {
            this.graphPanel = graphPanel;
//            this.graphPanel.initialStart = false;
//            this.graphPanel.pause = false;
//            this.graphPanel.stop = false;
        }
    }
}
