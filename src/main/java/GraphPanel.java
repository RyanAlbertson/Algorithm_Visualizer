package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
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
    static final int NODE_RADIUS = 10;
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

    private boolean firstPaint = true;
    private String algName = "Breath-First Search";
    protected String graphSize = "Small";
    private MOUSE_STATE mouseState = MOUSE_STATE.START_NODE;
    protected Integer startNode;
    protected Integer endNode;
    protected double centerX;
    protected double centerY;
    private boolean[] visited;
    private HashMap<Integer, double[]> nodeCoords;
    private HashMap<Integer, Shape> nodeShapes;
    private HashMap<Integer, LinkedList<Integer>> adjNodes;
    private Deque<Integer> path;


    /**
     *
     */
    public GraphPanel() {

        // Detect user-selected start and end nodes
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {

                super.mouseClicked(me);

                if (mouseState.equals(MOUSE_STATE.RESET)) {
                    startNode = endNode = null;
                } else {
                    for (Integer nodeNum : nodeShapes.keySet()) {
                        if (nodeShapes.get(nodeNum).contains(me.getPoint())) {
                            if (mouseState.equals(MOUSE_STATE.START_NODE)) {
                                startNode = nodeNum;
                            } else endNode = nodeNum;
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

        if (firstPaint) {
            initGraph();
        }

        Graphics2D g2D = (Graphics2D) g;
        super.paint(g2D);

        for (Integer nodeNum : nodeCoords.keySet()) {
            g.setColor(visited[nodeNum] ? VISITED_COLOR : UNVISITED_COLOR);
            if (path.contains(nodeNum)) g.setColor(PATH_COLOR);
            g2D.fill(nodeShapes.get(nodeNum));

            if (firstPaint) {
                g.setColor(Color.BLACK);
                double x = nodeCoords.get(nodeNum)[0];
                double y = nodeCoords.get(nodeNum)[1];
                for (Integer adjNode : adjNodes.get(nodeNum)) {
                    double adjX = nodeCoords.get(nodeNum)[0];
                    double adjY = nodeCoords.get(nodeNum)[0];
                    g2D.draw(new Line2D.Double(x, y, adjX, adjY));
                }
            }
        }
        firstPaint = false;

        g.setColor(PATH_COLOR);
        double prevX;
        double prevY;
        Integer nodeNum;
        Iterator it = path.iterator();
        if (it.hasNext()) {
            nodeNum = (Integer) it.next();
            prevX = nodeCoords.get(nodeNum)[0];
            prevY = nodeCoords.get(nodeNum)[1];

            while (it.hasNext()) {
                nodeNum = (Integer) it.next();
                double x = nodeCoords.get(nodeNum)[0];
                double y = nodeCoords.get(nodeNum)[1];
                g2D.draw(new Line2D.Double(prevX, prevY, x, y));
                prevX = nodeCoords.get(nodeNum)[0];
                prevY = nodeCoords.get(nodeNum)[1];
            }
        }
    }


    /**
     * Reads and stores graph data from a file, as defined by {@code graphSize}.
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("DM_DEFAULT_ENCODING")
    private void initGraph() {

        nodeCoords = new HashMap<>();
        nodeShapes = new HashMap<>();
        adjNodes = new HashMap<>();
        path = new ArrayDeque<>();

        String graphFileName = graphFileNames.get(graphSize);
        String graphFileLocation = System.getProperty("user.dir")
                .concat("\\src\\main\\java\\resources\\graphs\\" + graphFileName);
        try {
            Scanner s = new Scanner(new File(graphFileLocation));
            String line;
            while (s.hasNextLine() && !(line = s.nextLine()).isEmpty()) {

                LinkedList<Integer> lineData = new LinkedList<>();
                for (String str : line.split(" ")) {
                    if (!str.equals("")) lineData.add(Integer.parseInt(str));
                }
                Integer nodeNum = lineData.get(0);
                double nodeNumX = lineData.get(1);
                double nodeNumY = lineData.get(2);
                Shape nodeShape = new Ellipse2D.Double(nodeNumX,
                        nodeNumY, NODE_RADIUS, NODE_RADIUS);
                nodeCoords.put(nodeNum, new double[]{nodeNumX, nodeNumY});
                nodeShapes.put(nodeNum, nodeShape);
                ListIterator<Integer> it = lineData.listIterator();
                while (it.hasNext()) {
                    adjNodes.computeIfAbsent(nodeNum,
                            key -> new LinkedList<>()).add(it.next());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        visited = new boolean[nodeCoords.size()];
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
