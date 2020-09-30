package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 *
 */
public class GraphPanel extends JPanel {


    static final Color VISITED_COLOR = Color.RED;
    static final Color UNVISITED_COLOR = Color.BLACK;
    static final Color PATH_COLOR = Color.GREEN;
    static final double NODE_RADIUS = 0.5;
    static final HashMap<String, String> graphFileNames = new HashMap<>();

    static {
        graphFileNames.put("Small", "sm_graph.txt");
        graphFileNames.put("Medium", "md_graph.txt");
        graphFileNames.put("Large", "lg_graph.txt");
    }

    public enum MOUSE_STATE {
        START_NODE, END_NODE, RESET {
            @Override
            public MOUSE_STATE next() {
                return values()[0];
            }
        };

        public MOUSE_STATE next() {
            return values()[ordinal() + 1];
        }
    }

    private MOUSE_STATE mouseState;
    private String algName;
    protected String graphSize;
    protected Integer startNode;
    protected Integer endNode;
    private boolean[] visited;
    private HashMap<Shape, Integer> allNodes;
    private HashMap<Integer, LinkedList<Integer[]>> adjNodes;
    private Deque<Integer> path;


    /**
     *
     */
    public GraphPanel() {

        // Defaults
        this.setAlgName("Breadth-First Search");
        this.graphSize = "Small";
        mouseState = MOUSE_STATE.START_NODE;

        initGraph();

        // Detect user-selected start and end nodes
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {

                super.mouseClicked(me);

                if (mouseState.equals(MOUSE_STATE.RESET)) {
                    startNode = endNode = null;
                } else {
                    for (Shape shape : allNodes.keySet()) {
                        if (shape.contains(me.getPoint())) {
                            if (mouseState.equals(MOUSE_STATE.START_NODE)) {
                                startNode = allNodes.get(shape);
                            } else {
                                endNode = allNodes.get(shape);
                            }
                        }
                    }
                }
                repaint();
                mouseState = mouseState.next();
            }
        });
    }


    /**
     * @param g
     */
    public void paint(Graphics g) {

        super.paint(g);

        // Paint start and end nodes or unpaint if either is null

        // Loop through all nodes and paint them as unvisited or visited
//        g.setColor(UNVISITED_COLOR);
//        g.setColor(VISITED_COLOR);
//        g.drawOval(0, 0, NODE_DIMENSION, NODE_DIMENSION);
//        g.fillOval(0, 0, NODE_DIMENSION, NODE_DIMENSION);

        // Loop through all edges and paint them as unvisited
//        g.setColor(UNVISITED_COLOR);
//        g.drawLine(0, 0, 0, 0);

        // Loop through nodes in path and paint them, along with their edges
//        g.setColor(PATH_COLOR);
    }


    /**
     * Reads and stores graph data from a file, determined by {@code graphSize}.
     */
    private void initGraph() {

        String graphFileName = graphFileNames.get(graphSize);
        String graphFileLocation = System.getProperty("user.dir");
        graphFileLocation = graphFileLocation.concat("\\src\\main\\java\\" +
                "resources\\graphs\\" + graphFileName);
        try {
            Scanner s = new Scanner(new File(graphFileLocation));
            String line;
            while (s.hasNextLine() && !(line = s.nextLine()).isEmpty()) {
                LinkedList<Integer> lineData = new LinkedList<>();
                for (String str : line.split(" ")) {
                    if (!str.equals("")) lineData.add(Integer.parseInt(str));
                }
                Integer currentNode = lineData.get(0);
                double currentNodeX = lineData.get(1);
                double currentNodeY = lineData.get(2);
                Shape nodeShape = new Ellipse2D.Double(currentNodeX,
                        currentNodeY, NODE_RADIUS, NODE_RADIUS);
                allNodes = new HashMap<>();
                adjNodes = new HashMap<>();
                allNodes.put(nodeShape, currentNode);
                ListIterator<Integer> it = lineData.listIterator(3);
                while (it.hasNext()) {
                    adjNodes.computeIfAbsent(currentNode, key ->
                            new LinkedList<>()).add(
                            new Integer[]{currentNode, it.next()});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        visited = new boolean[allNodes.size()];
    }


    /**
     *
     */
    protected void startAlgorithm() {
    }


    /**
     *
     */
    protected void stopAlgorithm() {
    }


    /**
     *
     */
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
}
