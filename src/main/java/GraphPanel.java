package main.java;

import main.java.util.Defs;
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
 * Constructs and maintains a graph for use in {@link GUI}.
 *
 * @author Ryan Albertson
 */
public class GraphPanel extends JPanel {


    public static final Color SOURCE_COLOR = Color.GREEN;
    public static final Color TARGET_COLOR = Color.RED;
    public static final Color VISITED_COLOR = Color.CYAN;
    public static final Color UNVISITED_COLOR = Color.BLACK;
    public static final Color PATH_COLOR = Color.blue;

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
    public Integer targetNode;
    private HashMap<Integer, Double[]> nodeCoords;
    private HashMap<Integer, Shape> nodeShapes;
    public HashMap<Integer, LinkedList<Integer>> adjNodes;

    public SimpleGraph<Integer, DefaultWeightedEdge> graph;
    public int[] path;
    public boolean[] visited;
    public boolean stop;
    public boolean pause;
    private Thread algThread;


    /**
     * Constructs an initial graph for the GUI. Also manages node selection.
     */
    public GraphPanel() {

        // Defaults
        algName = "Dijkstra";
        graphSize = "Medium";
        graph = GraphGenerator.generateGraph(graphSize, algName);
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
     * Renders the current graph in the GUI. If an algorithm is running, it is
     * also rendered.
     * @param g {@link Graphics} object that is drawn on.
     */
    public void paint(Graphics g) {

        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2D.setFont(new Font("Ariel", Font.PLAIN, 18));
        g2D.drawString("Click nodes to define a source and target", 475, 15);
        g2D.setFont(new Font("Ariel", Font.PLAIN, 16));
        g2D.drawString("(Edges are not proportional to weight for BFS & DFS)",
                450, 30);

        for (Integer node : nodeCoords.keySet()) {
            double x = nodeCoords.get(node)[0];
            double y = nodeCoords.get(node)[1];
            for (Integer adjNode : adjNodes.get(node)) {
                g2D.setColor(UNVISITED_COLOR);
                g2D.setStroke(new BasicStroke(1f));
                try {
                    double adjX = nodeCoords.get(adjNode)[0];
                    double adjY = nodeCoords.get(adjNode)[1];
                    if (visited[node] && visited[adjNode]) {
                        g2D.setStroke(new BasicStroke(2f));
                        g2D.setColor(VISITED_COLOR);
                    }
                    g2D.draw(new Line2D.Double(x, y, adjX, adjY));
                } catch (NullPointerException e) {
                    // nodeNum has no adjacent nodes
                }
            }
        }

        // Need second loop so that nodes are painted over all lines
        for (Integer node : nodeCoords.keySet()) {
            if (node.equals(sourceNode)) g.setColor(SOURCE_COLOR);
            else if (node.equals(targetNode)) g.setColor(TARGET_COLOR);
            else g.setColor(visited[node] ? VISITED_COLOR : UNVISITED_COLOR);
            g2D.fill(nodeShapes.get(node));
        }

        // Draw path once algorithm has finished
        if (targetNode != null && (path[targetNode] != Integer.MAX_VALUE)) {

            g2D.setColor(PATH_COLOR);
            g2D.setStroke(new BasicStroke(3f));
            int currentNode = targetNode;
            int prevNode = path[targetNode];
            while (prevNode != Integer.MAX_VALUE) {
                double currentNodeX = nodeCoords.get(currentNode)[0];
                double currentNodeY = nodeCoords.get(currentNode)[1];
                double prevNodeX = nodeCoords.get(prevNode)[0];
                double prevNodeY = nodeCoords.get(prevNode)[1];
                g2D.draw(new Line2D.Double(currentNodeX, currentNodeY,
                        prevNodeX, prevNodeY));
                // Maintain source and target node coloring
                if (currentNode == sourceNode) g2D.setColor(SOURCE_COLOR);
                else if (currentNode == targetNode) g2D.setColor(TARGET_COLOR);
                g2D.fill(nodeShapes.get(currentNode));
                g2D.setColor(PATH_COLOR);
                if (prevNode == sourceNode) g2D.setColor(SOURCE_COLOR);
                else if (prevNode == targetNode) g2D.setColor(TARGET_COLOR);
                g2D.fill(nodeShapes.get(prevNode));
                g2D.setColor(PATH_COLOR);

                // Tranverse path back to source node
                currentNode = prevNode;
                prevNode = path[prevNode];
            }
        }
    }


    /**
     * Reads and stores graph data from a file, as defined by {@code graphSize}.
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("DM_DEFAULT_ENCODING")
    protected void initGraph() {

        int nodeCount = graph.vertexSet().size();
        nodeCoords = new HashMap<>(nodeCount);
        nodeShapes = new HashMap<>(nodeCount);
        adjNodes = new HashMap<>(nodeCount);

        //REPLACE FOLLOWING 5 LINES WITH THIS FOR USE OF AN EXECUTABLE
//        String graphFileName = graphFileNames.get(graphSize);
//        try {
//            Scanner s = new Scanner(new File(graphFileName),
        String graphFileName = Defs.graphFileNamesST.get(graphSize);
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
                        nodeNumY, Defs.NODE_RADIUS, Defs.NODE_RADIUS);
                nodeCoords.put(nodeNum, new Double[]{nodeNumX + Defs.NODE_RADIUS
                        / 2.0, nodeNumY + Defs.NODE_RADIUS / 2.0});
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
        //PATH SIZE NEEDS TO CHANGE IF NEW GRAPH SIZE IS SELECTED
        path = new int[nodeCoords.size()];
        Arrays.fill(path, Integer.MAX_VALUE);
    }


    /**
     * If an algorithm is running, it is unpaused. Otherwise, a new process
     * of the currently selected algorithm is started.
     */
    protected void startAlgorithm() {

        // Start new algorithm
        if (algThread == null || !algThread.isAlive()) {

            // Reset for next animation
            Arrays.fill(path, Integer.MAX_VALUE);
            Arrays.fill(visited, false);

            switch (algName) {
                case "Breadth-First Search" -> algThread =
                        new Thread(new BreadthFirstSearch(this));
                case "Depth-First Search" -> algThread =
                        new Thread(new DepthFirstSearch(this));
                case "Dijkstra" -> algThread =
                        new Thread(new Dijkstra(this));
                case "Bellman-Ford" -> Bellman_Ford.bellmanFord(this);
                case "Floyd_Warshall" -> Floyd_Warshall.floydWarshall(this);
                default -> throw new IllegalArgumentException("Invalid algorithm");
            }
            if (algThread != null) {
                stop = false;
                pause = false;
                algThread.start();
            }
            // Unpause current algorithm
        } else this.pause = false;
    }


    /**
     * Stops current algorithm, if it is running.
     */
    protected void stopAlgorithm() {

        if (algThread != null && algThread.isAlive()) {
            this.stop = true;
        }
        sourceNode = targetNode = null;
    }


    /**
     * Pauses current algorithm, if it is running.
     */
    protected void pauseAlgorithm() {

        if (algThread != null && algThread.isAlive()) {
            this.pause = true;
        }
    }
}

