import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {


    static final int WIDTH = 1280;
    static final int HEIGHT = 720;

    private JFrame frame;
    protected GraphPanel graph;
    private JButton startButton;
    private JButton stopButton;
    private JButton pauseButton;


    public Window() {

        frame = new JFrame();
        frame.setTitle("Algorithm Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(WIDTH, HEIGHT);
        initPanels();
        frame.setVisible(true);
    }


    private void initPanels() {

        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout(1, 7));
        menu.setPreferredSize(new Dimension(WIDTH, HEIGHT / 15));
        menu.setBackground(Color.DARK_GRAY);

        startButton = new JButton("Start");
        startButton.addActionListener(event -> startAlgorithm());
        menu.add(startButton);

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(event -> pauseAlgorithm());
        menu.add(pauseButton);

        stopButton = new JButton("Stop");
        startButton.addActionListener(event -> stopAlgorithm());
        menu.add(stopButton);

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
