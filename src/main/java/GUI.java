package main.java;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class GUI extends JFrame {

    static final int WIDTH = 1280;
    static final int HEIGHT = 720;

    private final JFrame frame;
    protected GraphPanel graph;
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
        frame.setSize(WIDTH, HEIGHT);
        initGraphPanel();
        initMenuPanel();
        frame.pack();
        frame.setVisible(true);
    }


    /**
     *
     */
    private void initGraphPanel() {

        Color graphBackgroundColor = Color.WHITE;
        int graphWidth = WIDTH;
        int graphHeight = HEIGHT - (HEIGHT / 15);
        graph = new GraphPanel(graphBackgroundColor, graphWidth, graphHeight);

        frame.add(graph, BorderLayout.CENTER);
    }


    /**
     *
     */
    private void initMenuPanel() {

        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout(1, 7));
        menu.setPreferredSize(new Dimension(WIDTH, HEIGHT / 15));
        menu.setBackground(Color.DARK_GRAY);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(event -> graph.startAlgorithm());
        menu.add(startButton);

        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(event -> graph.pauseAlgorithm());
        menu.add(pauseButton);

        JButton stopButton = new JButton("Stop");
        startButton.addActionListener(event -> graph.stopAlgorithm());
        menu.add(stopButton);

        String[] algNames = {"Breadth-First Search", "Depth-First Search",
                "Dijkstra", "Bellman-Ford", "Floyd-Warshall"};
        chooseAlgName = new JComboBox<>(algNames);
        chooseAlgName.addActionListener(event ->
                graph.algName = (String) chooseAlgName.getSelectedItem());
        menu.add(chooseAlgName);

        String[] graphSizes = {"Small", "Medium", "Large"};
        chooseGraphSize = new JComboBox<>(graphSizes);
        chooseGraphSize.addActionListener(event -> GraphGenerator.
                generateGraph(((String) chooseGraphSize.getSelectedItem())));
        chooseGraphSize.addActionListener(event -> graph.initGraph());
        chooseGraphSize.addActionListener(event -> graph.repaint());
        menu.add(chooseGraphSize);

        frame.add(menu, BorderLayout.NORTH);
    }
}
