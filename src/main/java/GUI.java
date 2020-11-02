package main.java;

import main.java.util.Defs;

import javax.swing.*;
import java.awt.*;

/**
 * Launches a GUI application that allows a user to interactively generate graphs
 * and select start and end nodes for a shortest path algorithm. Then user can
 * select and start an algorithm and see it step through an animation.
 *
 * @author Ryan Albertson
 */
public class GUI extends JFrame {


    static final int WINDOW_WIDTH = 1500;
    static final int WINDOW_HEIGHT = 900;
    static final int GRAPH_HEIGHT = WINDOW_HEIGHT - (WINDOW_HEIGHT / 15);

    private final JFrame frame;
    protected GraphPanel graphPanel;
    private JComboBox<String> chooseAlgName;
    private JComboBox<String> chooseGraphSize;


    /**
     * Constructs a {@link GUI}.
     */
    public GUI() {

        frame = new JFrame();
        frame.setTitle("Algorithm Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        initGraphPanel();
        initMenuPanel();
        frame.pack();
        frame.setVisible(true);
    }


    /**
     * Initializes the graph component of the {@link GUI}.
     */
    private void initGraphPanel() {

        graphPanel = new GraphPanel();
        frame.add(graphPanel, BorderLayout.CENTER);
    }


    /**
     * If user chooses an algorithm, these statements execute in order.
     */
    private void chooseAlgNameActions() {

        graphPanel.stopAlgorithm();

        // Change graph to directed/undirected if needed for chosen algorithm
        String oldAlgName = graphPanel.algName;
        graphPanel.algName = (String) chooseAlgName.getSelectedItem();
        if (Defs.isDirectedST.get(graphPanel.algName) ^
                Defs.isDirectedST.get(oldAlgName)) {
            graphPanel.graph = GraphGenerator.generateGraph(
                    graphPanel.graphSize, graphPanel.algName);
        }
        graphPanel.initGraph();
        graphPanel.repaint();
    }


    /**
     * If user chooses a graph size, these statements execute in order.
     */
    private void chooseGraphSizeActions() {

        graphPanel.stopAlgorithm();
        graphPanel.graphSize = (String) chooseGraphSize.getSelectedItem();
        graphPanel.graph = GraphGenerator.generateGraph(
                (String) chooseGraphSize.getSelectedItem(),
                graphPanel.algName);
        graphPanel.initGraph();
        graphPanel.repaint();
    }


    /**
     * Initializes menu bar component of the {@link GUI}.
     */
    private void initMenuPanel() {

        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout(1, 7));
        menu.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT / 15));
        menu.setBackground(Color.DARK_GRAY);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        startButton.addActionListener(event -> graphPanel.startAlgorithm());
        menu.add(startButton);

        JButton pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        pauseButton.addActionListener(event -> graphPanel.pauseAlgorithm());
        menu.add(pauseButton);

        JButton stopButton = new JButton("Stop");
        stopButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        stopButton.addActionListener(event -> graphPanel.stopAlgorithm());
        menu.add(stopButton);

        chooseAlgName = new JComboBox<>();
        Defs.algNames.forEach(chooseAlgName::addItem);
        // Default. See GraphPanel constuctor
        chooseAlgName.setSelectedItem("Dijkstra");
        chooseAlgName.setFont(new Font("Ariel", Font.PLAIN, 18));
        chooseAlgName.addActionListener(event -> chooseAlgNameActions());
        menu.add(chooseAlgName);

        chooseGraphSize = new JComboBox<>();
        Defs.graphSizes.forEach(chooseGraphSize::addItem);
        // Default. See GraphPanel constructor
        chooseGraphSize.setSelectedItem("Medium");
        chooseGraphSize.setFont(new Font("Ariel", Font.PLAIN, 18));
        chooseGraphSize.addActionListener(event -> chooseGraphSizeActions());
        menu.add(chooseGraphSize);

        frame.add(menu, BorderLayout.NORTH);
    }


    /**
     * Launches the application.
     */
    public static void main(String[] args) {

        new GUI();
    }
}


// TODO:    -WEAKEN THE COUPLING OF graphPanel.java + ALG CLASSES.
//          -REFACTOR ENCAPSULATION OF PROJECT.
//          -ADD ERROR CHECKING/HANDLING.
//          -FIX FILE PATHS FOR EXECUTABLE.
