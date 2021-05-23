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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


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
    public static final Color MST_COLOR = new Color(255, 0, 127);

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

    private MOUSE_STATE mouseState;
    protected Algorithm algorithm;
    protected String graphSize;
    protected HashMap<Integer, Shape> nodeShapes;
    public HashMap<Integer, Integer[]> nodeCoords;
    public String algName;
    public boolean isShortPathAlg;
    public DefaultUndirectedWeightedGraph<Integer, DefaultWeightedEdge> graph;
    public Set<DefaultWeightedEdge> visitedEdges;
    public int[] path;
    public int nodeCount;
    public Integer sourceNode;
    public Integer targetNode;
    public int speed;


    /**
     * Constructs an initial graph for the GUI. Also manages node selection.
     */
    public GraphPanel() {

        // Defaults
        algName = "A*";
        speed = Defs.speedST.get("Fast");
        graphSize = "Large";
        isShortPathAlg = Defs.isShortPathAlg.get(algName);
        nodeCount = Defs.nodeCountST.get(graphSize);
        visitedEdges = ConcurrentHashMap.newKeySet();
        path = new int[nodeCount];

        GraphGenerator.generateGraph(this);
        mouseState = MOUSE_STATE.SOURCE_NODE;

        this.setPreferredSize(new Dimension(GUI.WINDOW_WIDTH, GUI.GRAPH_HEIGHT));
        this.setBackground(Color.WHITE);

        // Detect user-selected source and target nodes
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);

                // Block node selection during animation
                if (null != algorithm && algorithm.isAlive()) return;
                // Block node selection during MST algorithms
                if (!isShortPathAlg) return;

                if (mouseState.equals(MOUSE_STATE.RESET)) {
                    resetAnimation();
                    mouseState = mouseState.next();
                } else {
                    for (Integer node = 0; node < nodeCount; node++) {
                        if (nodeShapes.get(node).contains(me.getPoint())) {
                            if (mouseState.equals(MOUSE_STATE.SOURCE_NODE)) {
                                sourceNode = node;
                            } else if (!node.equals(sourceNode)) {
                                targetNode = node;
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
        if (isShortPathAlg && null != algorithm && !algorithm.isAlive()) {
            g2D.drawString("Click nodes to define a source and target",
                    (float) (GUI.WINDOW_WIDTH * 0.39), 15);
            g2D.setFont(new Font("Ariel", Font.PLAIN, 16));
        }
        // Draw edges
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            Integer edgeSource = graph.getEdgeSource(edge);
            Integer edgeTarget = graph.getEdgeTarget(edge);

            g2D.setColor(UNVISITED_COLOR);
            g2D.setStroke(new BasicStroke(2f));
            int sourceX = nodeCoords.get(edgeSource)[0];
            int sourceY = nodeCoords.get(edgeSource)[1];
            int targetX = nodeCoords.get(edgeTarget)[0];
            int targetY = nodeCoords.get(edgeTarget)[1];
            g2D.draw(new Line2D.Double(sourceX, sourceY, targetX, targetY));
        }

        // Draw unvisited nodes on top of the edges
        for (DefaultWeightedEdge edge : graph.edgeSet()) {
            g2D.setColor(UNVISITED_COLOR);
            g2D.fill(nodeShapes.get(graph.getEdgeSource(edge)));
            g2D.fill(nodeShapes.get(graph.getEdgeTarget(edge)));
        }

        // Draw visited edges and nodes on top of unvisited components
        for (DefaultWeightedEdge edge : visitedEdges) {
            if (isShortPathAlg) {
                g2D.setColor(VISITED_COLOR);
                g2D.setStroke(new BasicStroke(3f));
            } else {
                g2D.setColor(MST_COLOR);
                g2D.setStroke(new BasicStroke(6f));
            }
            g2D.fill(nodeShapes.get(graph.getEdgeSource(edge)));
            g2D.fill(nodeShapes.get(graph.getEdgeTarget(edge)));

            Integer edgeSource = graph.getEdgeSource(edge);
            Integer edgeTarget = graph.getEdgeTarget(edge);
            int sourceX = nodeCoords.get(edgeSource)[0];
            int sourceY = nodeCoords.get(edgeSource)[1];
            int targetX = nodeCoords.get(edgeTarget)[0];
            int targetY = nodeCoords.get(edgeTarget)[1];
            g2D.draw(new Line2D.Double(sourceX, sourceY, targetX, targetY));

        }

        // Draw path once algorithm has found target node
        if (targetNode != null && (path[targetNode] != Integer.MAX_VALUE)) {
            g2D.setColor(PATH_COLOR);
            g2D.setStroke(new BasicStroke(4f));
            int currentNode = targetNode;
            int prevNode = path[targetNode];
            // Tranverse path back to source node
            while (prevNode != Integer.MAX_VALUE) {
                double currentNodeX = nodeCoords.get(currentNode)[0];
                double currentNodeY = nodeCoords.get(currentNode)[1];
                double prevNodeX = nodeCoords.get(prevNode)[0];
                double prevNodeY = nodeCoords.get(prevNode)[1];
                g2D.draw(new Line2D.Double(currentNodeX, currentNodeY,
                        prevNodeX, prevNodeY));
                g2D.fill(nodeShapes.get(prevNode));
                currentNode = prevNode;
                prevNode = path[prevNode];
            }
        }

        // Draw source and target nodes for shortest path algortihms
        if (isShortPathAlg) {
            if (sourceNode != null) {
                g2D.setColor(SOURCE_COLOR);
                g2D.fill(nodeShapes.get(sourceNode));
            }
            if (targetNode != null) {
                g2D.setColor(TARGET_COLOR);
                g2D.fill(nodeShapes.get(targetNode));
            }
        }
    }


    /**
     * Resets the current animation.
     */
    public void resetAnimation() {

        Arrays.fill(path, Integer.MAX_VALUE);
        visitedEdges = ConcurrentHashMap.newKeySet();
        if (null != algorithm && !algorithm.isAlive()) {
            sourceNode = null;
            targetNode = null;
        }
        repaint();
    }


    /**
     * If an algorithm is running, then it is unpaused. Otherwise, a new process
     * of the currently selected algorithm is started.
     */
    protected void startAlgorithm() {

        // Start new algorithm
        if (null == algorithm || !algorithm.isAlive()) {
            // Don't start algorithm if user hasn't selected source & target nodes
            if (isShortPathAlg && (sourceNode == null || targetNode == null)) return;
            switch (algName) {
                case "Depth-First Search":
                    algorithm = new DepthFirstSearch(this);
                    break;
                case "Breadth-First Search":
                    algorithm = new BreadthFirstSearch(this);
                    break;
                case "Dijkstra":
                    algorithm = new Dijkstra(this);
                    break;
                case "A*":
                    algorithm = new A_Star(this);
                    break;
                case "Bellman-Ford":
                    algorithm = new BellmanFord(this);
                    break;
                case "Floyd-Warshall":
                    algorithm = new FloydWarshall(this);
                    break;
                case "Reverse Delete":
                    algorithm = new ReverseDelete(this);
                    break;
                case "Kruskal":
                    algorithm = new Kruskal(this);
                    break;
                case "Prim":
                    algorithm = new Prim(this);
                    break;
                default:
                    throw new IllegalArgumentException("ERROR: Invalid algorithm");
            }
            new Thread(algorithm).start();

            // Unpause if animation is live
        } else {
            algorithm.unPause();
        }
    }


    /**
     * Stops current algorithm, if it is running.
     */
    protected void stopAlgorithm() {

        if (algorithm != null && algorithm.isAlive()) algorithm.stop();
        else resetAnimation();
    }


    /**
     * Pauses current algorithm, if it is running.
     */
    protected void pauseAlgorithm() {

        if (algorithm != null) {
            if (algorithm.isPaused()) algorithm.unPause();
            else algorithm.pause();
        }
    }
}

