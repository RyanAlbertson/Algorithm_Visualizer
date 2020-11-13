package main.java;

import main.java.util.Defs;
import main.java.util.algorithms.*;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * Constructs an user-interactive graph for use in {@link GUI}.
 *
 * @author Ryan Albertson
 */
public class GraphPanel extends JPanel {

    public static final Color SOURCE_COLOR = new Color(0, 170, 19);
    public static final Color TARGET_COLOR = new Color(225, 6, 0);
    public static final Color VISITED_COLOR = new Color(0, 181, 226);
    public static final Color UNVISITED_COLOR = new Color(0, 0, 0);
    public static final Color PATH_COLOR = new Color(250, 108, 36);

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

    private Thread algThread;
    private MOUSE_STATE mouseState;
    protected String algName;
    protected String graphSize;
    protected HashMap<Integer, Integer[]> nodeCoords;
    protected HashMap<Integer, Shape> nodeShapes;
    protected boolean isMinConnected;
    public DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;
    public Set<DefaultWeightedEdge> visitedEdges;
    public Set<DefaultWeightedEdge> pathEdges;
    public int[] path;
    public boolean[] visited;
    public int nodeCount;
    public Integer sourceNode;
    public Integer targetNode;
    public int speed;
    public boolean stop;
    public boolean pause;


    /**
     * Constructs an initial graph for the GUI. Also manages node selection.
     */
    public GraphPanel() {

        // Defaults
        algName = "Dijkstra";
        speed = Defs.speedST.get("Fast");
        graphSize = "Large";
        isMinConnected = Defs.isMinConnected.get(algName);
        nodeCount = Defs.nodeCountST.get(graphSize);
        visitedEdges = new HashSet<>();
        pathEdges = new HashSet<>();

        GraphGenerator.generateGraph(this);
        mouseState = MOUSE_STATE.SOURCE_NODE;

        this.setPreferredSize(new Dimension(GUI.WINDOW_WIDTH, GUI.GRAPH_HEIGHT));
        this.setBackground(Color.WHITE);

        // Detect user-selected start and end nodes
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {

                super.mouseClicked(me);

                // Block node selection during animation
                if (algThread != null && algThread.isAlive()) return;
                // Block node selection during MST algorithms
                if (!isMinConnected) return;

                if (mouseState.equals(MOUSE_STATE.RESET)) {
                    sourceNode = targetNode = null;
                    resetAnimation();
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
     * Renders the current graph in the GUI. If an algorithm is running, this is
     * also rendered.
     *
     * @param g {@link Graphics} object that is drawn on.
     * @see JPanel#paintComponent(Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2D.setFont(new Font("Ariel", Font.PLAIN, 18));
        g2D.drawString("Click nodes to define a source and target",
                (float) (GUI.WINDOW_WIDTH * 0.39), 15);
        g2D.setFont(new Font("Ariel", Font.PLAIN, 16));
        g2D.drawString("(Edges are not proportional to weight for BFS & DFS)",
                (float) (GUI.WINDOW_WIDTH * 0.37), 30);

        // Draw edges
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            Integer edgeSource = graph.getEdgeSource(edge);
            Integer edgeTarget = graph.getEdgeTarget(edge);

            int sourceX = nodeCoords.get(edgeSource)[0];
            int sourceY = nodeCoords.get(edgeSource)[1];
            int targetX = nodeCoords.get(edgeTarget)[0];
            int targetY = nodeCoords.get(edgeTarget)[1];

            g2D.setColor(UNVISITED_COLOR);
            g2D.setStroke(new BasicStroke(2f));
            if (visited[edgeSource] && visited[edgeTarget]) {
                g2D.setStroke(new BasicStroke(3f));
                g2D.setColor(VISITED_COLOR);
            }
            g2D.draw(new Line2D.Double(sourceX, sourceY, targetX, targetY));
        }

        // Draw nodes over the edges
        for (Integer node = 0; node < nodeCount; node++) {
            if (node.equals(sourceNode)) g.setColor(SOURCE_COLOR);
            else if (node.equals(targetNode)) g.setColor(TARGET_COLOR);
            else g2D.setColor(visited[node] ? VISITED_COLOR : UNVISITED_COLOR);
            g2D.fill(nodeShapes.get(node));
        }

        // Draw path once algorithm has found target node
        if (targetNode != null && (path[targetNode] != Integer.MAX_VALUE)) {
            g2D.setColor(PATH_COLOR);
            g2D.setStroke(new BasicStroke(4f));
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
     * Resets the current animation.
     */
    public void resetAnimation() {

        pause = false;
        Arrays.fill(visited, false);
        Arrays.fill(path, Integer.MAX_VALUE);
        if (stop) {
            sourceNode = null;
            targetNode = null;
        }
        repaint();
    }


    /**
     * If an algorithm is running, it is unpaused. Otherwise, a new process
     * of the currently selected algorithm is started.
     */
    protected void startAlgorithm() {

        // Start new algorithm
        if (algThread == null || !algThread.isAlive()) {
            // Don't start algorithm if user hasn't selected source & target nodes
            if (isMinConnected && (sourceNode == null || targetNode == null)) return;

            stop = false;
            resetAnimation();
            switch (algName) {
                case "Breadth-First Search" -> algThread =
                        new Thread(new BreadthFirstSearch(this));
                case "Depth-First Search" -> algThread =
                        new Thread(new DepthFirstSearch(this));
                case "Dijkstra" -> algThread =
                        new Thread(new Dijkstra(this));
                case "Kruskal" -> algThread =
                        new Thread(new Kruskal(this));
                case "Prim" -> algThread =
                        new Thread(new Prim(this));
                default -> throw new IllegalArgumentException("Invalid algorithm");
            }
            algThread.start();

            // Unpause current algorithm
        } else pause = false;
    }


    /**
     * Stops current algorithm, if it is running.
     */
    protected void stopAlgorithm() {

        if (algThread != null) {
            stop = true;
            resetAnimation();
        }
    }


    /**
     * Pauses current algorithm, if it is running.
     */
    protected void pauseAlgorithm() {

        if (algThread != null) {
            pause = true;
        }
    }
}

