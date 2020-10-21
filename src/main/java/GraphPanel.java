package main.java;

import main.java.util.algorithms.*;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;

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


    public static final Color VISITED_COLOR = Color.RED;
    public static final Color UNVISITED_COLOR = Color.BLACK;
    public static final Color PATH_COLOR = Color.GREEN;
    public static final int NODE_RADIUS = 20;

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
    public Integer sourceNode;
    private Integer targetNode;
    private HashMap<Integer, Double[]> nodeCoords;
    private HashMap<Integer, Shape> nodeShapes;
    private HashMap<Integer, LinkedList<Integer>> adjNodes;

    public SimpleGraph<Integer, DefaultWeightedEdge> graph;
    public Deque<Integer> path;
    public boolean[] visited;
    public boolean initialStart = true;  // Allows for dual use of start button
    public boolean start = false;
    public boolean stop = false;
    public boolean pause = false;
    private Thread algThread;


    /**
     *
     */
    public GraphPanel() {

        // Defaults
        algName = "Breath-First Search";
        graphSize = "Small";
        GraphGenerator.generateGraph(graphSize);
        mouseState = MOUSE_STATE.SOURCE_NODE;

        path = new ArrayDeque<>();
        this.setPreferredSize(new Dimension(GUI.WINDOW_WIDTH, GUI.GRAPH_HEIGHT));
        this.setBackground(Color.WHITE);
        initGraph();

        // Detect user-selected start and end nodes
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {

                super.mouseClicked(me);

                if (mouseState.equals(MOUSE_STATE.RESET)) {
                    sourceNode = targetNode = null;
                    repaint();
                    mouseState = mouseState.next();
                } else {
                    for (Integer nodeNum : nodeShapes.keySet()) {
                        if (nodeShapes.get(nodeNum).contains(me.getPoint())) {
                            if (mouseState.equals(MOUSE_STATE.SOURCE_NODE)) {
                                sourceNode = nodeNum;
                            } else {
                                targetNode = nodeNum;
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
            if (nodeNum.equals(sourceNode)) g.setColor(Color.GREEN);
            else if (nodeNum.equals(targetNode)) g.setColor(Color.RED);
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

        //REPLACE FOLLOWING 5 LINES WITH THIS FOR USE OF AN EXECUTABLE
//        String graphFileName = graphFileNames.get(graphSize);
//        try {
//            Scanner s = new Scanner(new File(graphFileName),
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
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        visited = new boolean[nodeCoords.size()];
    }


    /**
     *
     */
    protected void startAlgorithm() {

        // Reset for next animation
        if (algThread != null && !algThread.isAlive()) {
            path.clear();
            Arrays.fill(visited, false);
        }

        // Start new algorithm
        if (algThread == null || !algThread.isAlive()) {

            switch (algName) {
                case "Breadth-First Search" -> BreadthFirstSearch.breadthFirstSearch(this);
                case "Depth-First Search" -> {
                    algThread = new Thread(new DepthFirstSearch(this));
                    algThread.start();
                }
                case "Dijkstra" -> Dijkstra.dijkstra(this);
                case "Bellman-Ford" -> Bellman_Ford.bellmanFord(this);
                case "Floyd_Warshall" -> Floyd_Warshall.floydWarshall(this);
                default -> throw new IllegalArgumentException("Invalid algorithm");
            }
            // Unpause current algorithm
        } else this.pause = false;
    }


    /**
     *
     */
    protected void stopAlgorithm() {

        if (algThread != null && algThread.isAlive()) {

            switch (algName) {
                case "Breadth-First Search", "Depth-First Search", "Dijkstra",
                        "Bellman-Ford", "Floyd_Warshall" -> algThread.;
                default -> throw new IllegalArgumentException("Invalid algorithm");
            }
        }
    }


    /**
     *
     */
    protected void pauseAlgorithm() {

        if (algThread != null && algThread.isAlive()) {

            switch (algName) {
                case "Breadth-First Search", "Depth-First Search", "Dijkstra",
                        "Bellman-Ford", "Floyd_Warshall" -> {
                    try {
                        algThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                default -> throw new IllegalArgumentException("Invalid algorithm");
            }
        }
    }
}

