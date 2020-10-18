package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 *
 */
public class GraphPanel extends JPanel {


    static final Color VISITED_COLOR = Color.RED;
    static final Color UNVISITED_COLOR = Color.BLACK;
    static final Color PATH_COLOR = Color.GREEN;
    static final int NODE_RADIUS = 20;
    static final HashMap<String, String> graphFileNames = new HashMap<>();

    static {
        graphFileNames.put("Small", "sm_graph.txt");
        graphFileNames.put("Medium", "md_graph.txt");
        graphFileNames.put("Large", "lg_graph.txt");
    }

    public enum MOUSE_STATE {
        SOURCE_NODE, TARGET_NODE, RESET {
            @Override
            public MOUSE_STATE next() {
                return values()[0];
            }
        };

        public MOUSE_STATE next() {
            return values()[ordinal() + 1];
        }
    }

    protected String algName;
    protected String graphSize;
    private MOUSE_STATE mouseState;
    protected Integer startNode;
    protected Integer endNode;
    private boolean[] visited;
    private HashMap<Integer, Double[]> nodeCoords;
    private HashMap<Integer, Shape> nodeShapes;
    private HashMap<Integer, LinkedList<Integer>> adjNodes;
    private Deque<Integer> path;


    /**
     *
     */
    public GraphPanel() {

        // Defaults
        algName = "Breath-First Search";
        graphSize = "Small";
        mouseState = MOUSE_STATE.SOURCE_NODE;
        
        this.setPreferredSize(new Dimension(GUI.WINDOW_WIDTH, GUI.GRAPH_HEIGHT));
        this.setBackground(Color.WHITE);
        initGraph();

        // Detect user-selected start and end nodes
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {

                super.mouseClicked(me);

                if (mouseState.equals(MOUSE_STATE.RESET)) {
                    startNode = endNode = null;
                    repaint();
                    mouseState = mouseState.next();
                } else {
                    for (Integer nodeNum : nodeShapes.keySet()) {
                        if (nodeShapes.get(nodeNum).contains(me.getPoint())) {
                            if (mouseState.equals(MOUSE_STATE.SOURCE_NODE)) {
                                startNode = nodeNum;
                            } else {
                                endNode = nodeNum;
                            }
                            repaint();
                            mouseState = mouseState.next();
                        }
                    }
                }
            }
        });
    }


    /**
     * @param g
     */
    public void paint(Graphics g) {

        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2D.setStroke(new BasicStroke(1f));

        g2D.setFont(new Font("Ariel", Font.PLAIN, 18));
        g2D.drawString("Click nodes to define a source and target", 475, 20);

        for (Integer nodeNum : nodeCoords.keySet()) {
            // Color all nodes and edges black initially
            g.setColor(Color.BLACK);
            double x = nodeCoords.get(nodeNum)[0] + NODE_RADIUS / 2.0;
            double y = nodeCoords.get(nodeNum)[1] + NODE_RADIUS / 2.0;
            for (Integer adjNode : adjNodes.get(nodeNum)) {
                try {
                    double adjX = nodeCoords.get(adjNode)[0] + NODE_RADIUS / 2.0;
                    double adjY = nodeCoords.get(adjNode)[1] + NODE_RADIUS / 2.0;
                    g2D.draw(new Line2D.Double(x, y, adjX, adjY));
                } catch (NullPointerException e) {
                    // nodeNum has no adjacent nodes
                }
            }
        }
        // Need second loop so that nodes are painted over all lines
        for (Integer nodeNum : nodeCoords.keySet()) {
            if (nodeNum.equals(startNode)) g.setColor(Color.GREEN);
            else if (nodeNum.equals(endNode)) g.setColor(Color.RED);
            else g.setColor(visited[nodeNum] ? VISITED_COLOR : UNVISITED_COLOR);
            g2D.fill(nodeShapes.get(nodeNum));
        }

        // Colors nodes and edges in path
        //HOW TO SLOW DOWN SUCH THAT ALGORITHM CAN BE VISUALIZED AS IT STEPS?
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        //USE SAME COLORS FOR VISISTED AND PATH NODES?
        g.setColor(PATH_COLOR);
        double prevX;
        double prevY;
        Integer nodeNum;
        Iterator<Integer> it = path.iterator();
        if (it.hasNext()) {
            nodeNum = it.next();
            prevX = nodeCoords.get(nodeNum)[0];
            prevY = nodeCoords.get(nodeNum)[1];
            while (it.hasNext()) {
                nodeNum = it.next();
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
    protected void initGraph() {

        nodeCoords = new HashMap<>();
        nodeShapes = new HashMap<>();
        adjNodes = new HashMap<>();
        path = new ArrayDeque<>();
        String graphFileName = graphFileNames.get(graphSize);
        String graphFileLocation = System.getProperty("user.dir")
                .concat("\\src\\main\\java\\resources\\graphs\\" + graphFileName);
        try {
            Scanner s = new Scanner(new File(graphFileLocation),
                    StandardCharsets.UTF_8);
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
                nodeCoords.put(nodeNum, new Double[]{nodeNumX, nodeNumY});
                nodeShapes.put(nodeNum, nodeShape);
                Iterator<Integer> it = lineData.listIterator(3);
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
}

