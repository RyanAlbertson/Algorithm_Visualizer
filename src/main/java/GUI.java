package main.java;

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

        graph = new GraphPanel();

        frame.add(graph, BorderLayout.CENTER);
    }


    /**
     *
     */
    private void initMenuPanel() {

        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout(1, 7));
        menu.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT / 15));
        menu.setBackground(Color.DARK_GRAY);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        startButton.addActionListener(event -> graph.startAlgorithm());
        menu.add(startButton);

        JButton pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        pauseButton.addActionListener(event -> graph.pauseAlgorithm());
        menu.add(pauseButton);

        JButton stopButton = new JButton("Stop");
        stopButton.setFont(new Font("Ariel", Font.PLAIN, 18));
        startButton.addActionListener(event -> graph.stopAlgorithm());
        menu.add(stopButton);

        String[] algNames = {"Breadth-First Search", "Depth-First Search",
                "Dijkstra", "Bellman-Ford", "Floyd-Warshall"};
        chooseAlgName = new JComboBox<>(algNames);
        chooseAlgName.setFont(new Font("Ariel", Font.PLAIN, 18));
        chooseAlgName.addActionListener(event ->
                graph.algName = (String) chooseAlgName.getSelectedItem());
        menu.add(chooseAlgName);

        String[] graphSizes = {"Small", "Medium", "Large"};
        chooseGraphSize = new JComboBox<>(graphSizes);
        chooseGraphSize.setFont(new Font("Ariel", Font.PLAIN, 18));
        chooseGraphSize.addActionListener(event -> graph.repaint());
        chooseGraphSize.addActionListener(event -> graph.initGraph());
        chooseGraphSize.addActionListener(event -> GraphGenerator.
                generateGraph((String) chooseGraphSize.getSelectedItem()));
        chooseGraphSize.addActionListener(event -> graph.graphSize =
                (String) chooseGraphSize.getSelectedItem());
        menu.add(chooseGraphSize);

        frame.add(menu, BorderLayout.NORTH);
    }
}
