import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {


    static final int WIDTH = 1280;
    static final int HEIGHT = 720;

    private final JFrame frame;
    protected GraphPanel graph;
    protected String algName;
    protected String graphSize;
    private JComboBox<String> chooseAlgName;
    private JComboBox<String> chooseGraphSize;

    // MAKE PROTECTED FIELDS THAT STORE VISITED/UNVISITED NODES AND THE EDGES
    // AND PREDECESSOR LISTS, ETC... USED FOR PAINTING THE GRAPHS.


    public Window() {

        frame = new JFrame();
        frame.setTitle("Algorithm Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(WIDTH, HEIGHT);
        initPanels();
        frame.setVisible(true);

        algName = "Breadth-First Search";
        graphSize = "Small";
    }


    private void initPanels() {

        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout(1, 7));
        menu.setPreferredSize(new Dimension(WIDTH, HEIGHT / 15));
        menu.setBackground(Color.DARK_GRAY);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(event -> startAlgorithm());
        menu.add(startButton);

        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(event -> pauseAlgorithm());
        menu.add(pauseButton);

        JButton stopButton = new JButton("Stop");
        startButton.addActionListener(event -> stopAlgorithm());
        menu.add(stopButton);

        String[] algNames = {"Breadth-First Search", "Depth-First Search",
                "Dijkstra", "Bellman-Ford", "Floyd-Warshall"};
        chooseAlgName = new JComboBox<>(algNames);
        chooseAlgName.addActionListener(event ->
                algName = (String) chooseAlgName.getSelectedItem());
        menu.add(chooseAlgName);

        String[] graphSizes = {"Small", "Medium", "Large"};
        chooseGraphSize = new JComboBox<>(graphSizes);
        chooseGraphSize.addActionListener(event ->
                graphSize = (String) chooseGraphSize.getSelectedItem());
        menu.add(chooseGraphSize);

        graph = new GraphPanel();
        graph.setPreferredSize(new Dimension(WIDTH, HEIGHT - (HEIGHT / 15)));

        frame.add(menu, BorderLayout.NORTH);
        frame.add(graph, BorderLayout.CENTER);
    }


    // PUT THESE IN ANOTHER CLASS? ////////

    private void startAlgorithm() {
    }

    private void stopAlgorithm() {
    }

    private void pauseAlgorithm() {
    }
}
