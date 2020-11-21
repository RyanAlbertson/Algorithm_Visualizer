package main.java;

import main.java.util.Defs;

import javax.swing.*;
import java.awt.*;

/**
 * Launches a GUI application that allows users to interactively generate graphs
 * and control algorithm animations on those graphs.
 *
 * @author Ryan Albertson
 */
public class GUI extends JFrame {

    static final int WINDOW_WIDTH = 1500;
    static final int WINDOW_HEIGHT = 900;
    static final int GRAPH_HEIGHT = WINDOW_HEIGHT - (WINDOW_HEIGHT / 15);

    private final JFrame frame;
    protected GraphPanel gPanel;
    private JComboBox<String> chooseSpeed;
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

        gPanel = new GraphPanel();
        frame.add(gPanel, BorderLayout.CENTER);
    }


    /**
     * If user chooses an algorithm, these statements execute in order.
     */
    private void chooseAlgNameActions() {

        // Don't interupt an animation in progress
        if (gPanel.algThread != null && gPanel.algThread.isAlive()) {
            chooseAlgName.setSelectedItem(gPanel.algName);
            return;
        }
        String prevAlgName = gPanel.algName;
        gPanel.algName = (String) chooseAlgName.getSelectedItem();
        // Regenerate graph if needed, for the given algorithm
        if (Defs.isShortPathAlg.get(gPanel.algName) ^
                Defs.isShortPathAlg.get(prevAlgName)) {
            gPanel.isShortPathAlg = Defs.isShortPathAlg.get(gPanel.algName);
            GraphGenerator.generateGraph(gPanel);
        }
        gPanel.pauseAlgorithm();
        gPanel.resetAnimation();
    }


    /**
     * If user chooses a graph size, these statements execute in order.
     */
    private void chooseGraphSizeActions() {

        gPanel.stopAlgorithm();
        gPanel.graphSize = (String) chooseGraphSize.getSelectedItem();
        GraphGenerator.generateGraph(gPanel);
        gPanel.resetAnimation();

    }


    /**
     * Initializes menu bar component of the {@link GUI}.
     */
    private void initMenuPanel() {

        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout());
        menu.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT / 15));
        menu.setBackground(Color.DARK_GRAY);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        startButton.addActionListener(event -> gPanel.startAlgorithm());
        menu.add(startButton);

        JButton pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        pauseButton.addActionListener(event -> gPanel.pauseAlgorithm());
        menu.add(pauseButton);

        JButton stopButton = new JButton("Stop");
        stopButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        stopButton.addActionListener(event -> gPanel.stopAlgorithm());
        menu.add(stopButton);

        chooseSpeed = new JComboBox<>();
        Defs.speedST.keySet().forEach(chooseSpeed::addItem);
        // Default. See GraphPanel constuctor
        chooseSpeed.setSelectedItem("Fast");
        chooseSpeed.setFont(new Font("Ariel", Font.PLAIN, 18));
        chooseSpeed.addActionListener(event ->
                gPanel.speed = Defs.speedST.get(
                        chooseSpeed.getSelectedItem()));
        menu.add(chooseSpeed);

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
        chooseGraphSize.setSelectedItem("Large");
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


// TODO:    -WEAKEN THE COUPLING OF ALG CLASSES.
//          -REFACTOR ENCAPSULATION OF PROJECT.
//          -ADD MORE ERROR CHECKING.
