package main.java;

import main.java.util.Defs;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class GUI extends JFrame {


    static final int WINDOW_WIDTH = 1280;
    static final int WINDOW_HEIGHT = 720;
    static final int GRAPH_HEIGHT = WINDOW_HEIGHT - (WINDOW_HEIGHT / 15);

    private final JFrame frame;
    protected GraphPanel graphPanel;
    private JComboBox<String> chooseAlgName;
    private JComboBox<String> chooseGraphSize;


    /**
     *
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
     *
     */
    private void initGraphPanel() {

        graphPanel = new GraphPanel();
        frame.add(graphPanel, BorderLayout.CENTER);
    }


    /**
     *
     */
    private void initMenuPanel() {

        // CHECK ORDERING OF BUTTONS, SOMETHING IS MESSING UP GUI RENDERING

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

        chooseAlgName = new JComboBox<>(Defs.algNames);
        chooseAlgName.setFont(new Font("Ariel", Font.PLAIN, 18));
        chooseAlgName.addActionListener(event -> graphPanel.initGraph());
        chooseAlgName.addActionListener(event ->
                graphPanel.algName = (String) chooseAlgName.getSelectedItem());
        // Change graph to directed/undirected if needed
        chooseAlgName.addActionListener(event -> {
            if (Defs.isDirectedST.get(graphPanel.algName) !=
                    Defs.isDirectedST.get((String) chooseAlgName.getSelectedItem())
            ) {
                GraphGenerator.generateGraph(graphPanel.graphSize,
                        (String) chooseAlgName.getSelectedItem());
            }
        });
        menu.add(chooseAlgName);

        chooseGraphSize = new JComboBox<>(Defs.graphSizes);
        chooseGraphSize.setFont(new Font("Ariel", Font.PLAIN, 18));
        chooseGraphSize.addActionListener(event -> graphPanel.repaint());
        chooseGraphSize.addActionListener(event -> graphPanel.initGraph());
        chooseGraphSize.addActionListener(event -> graphPanel.graph =
                GraphGenerator.generateGraph(
                        (String) chooseGraphSize.getSelectedItem(),
                        graphPanel.algName));
        chooseGraphSize.addActionListener(event -> graphPanel.graphSize =
                (String) chooseGraphSize.getSelectedItem());
        menu.add(chooseGraphSize);

        frame.add(menu, BorderLayout.NORTH);
    }
}
