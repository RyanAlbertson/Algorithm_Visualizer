import java.awt.*;
import java.util.HashMap;

/**
 *
 */
public class GraphPanel extends Window {


    static final int NODE_DIMENSION = 10;
    static final Color VISITED_COLOR = Color.RED;
    static final Color UNVISITED_COLOR = Color.BLACK;
    static final Color PATH_COLOR = Color.GREEN;

    private String algName;
    private String graphSize;
    private boolean[] visited;
    private HashMap<Integer, Integer[]> adj;


    /**
     *
     */
    public GraphPanel() {

        // Defaults
        this.setAlgName("Breadth-First Search");
        this.setGraphSize("Small");
//        this.setPreferredSize(new Dimension(1280, 720 - (720 / 15)));
//        this.setBackground(Color.RED);
    }

    //HOW TO REPAINT nodes/edges WHEN NEEDED?
    @Override
    public void paint(Graphics g) {

        g.setColor(UNVISITED_COLOR);
        // Loop through all nodes and mark them as unvisited
        g.drawOval(0, 0, NODE_DIMENSION, NODE_DIMENSION);
        g.fillOval(0, 0, NODE_DIMENSION, NODE_DIMENSION);
        // Loop through all edges and paint them as unvisited
    }


    private void initGraph() {

        // READ NODES AND ADJACENCY'S FROM FILE. UPDATE adj AS YOU GO.

        int numNodes = 0;


        visited = new boolean[numNodes + 1];
    }


    protected void startAlgorithm() {
    }


    protected void stopAlgorithm() {
    }


    protected void pauseAlgorithm() {
    }


    /**
     * Sets which algorithm will be animated.
     *
     * @param algName Name of an algorithm to be animated.
     */
    protected void setAlgName(String algName) {

        this.algName = algName;
    }


    /**
     * Sets the size of the graph input for the algorithm.
     *
     * @param graphSize Size of the graph.
     */
    protected void setGraphSize(String graphSize) {

        this.graphSize = graphSize;
    }
}
