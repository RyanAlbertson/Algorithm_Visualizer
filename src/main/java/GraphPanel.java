import java.awt.*;

public class GraphPanel extends Window {


    static final int NODE_DIMENSION = 10;
    static final Color VISITED_COLOR = Color.RED;
    static final Color UNVISITED_COLOR = Color.BLACK;
    static final Color PATH_COLOR = Color.GREEN;


    public GraphPanel() {

//        this.setPreferredSize(new Dimension(1280, 720 - (720 / 15)));
//        this.setBackground(Color.RED);
    }

    //HOW TO REPAINT nodes/edges WHEN NEEDED?
    @Override
    public void paint(Graphics g) {

        g.setColor(UNVISITED_COLOR);
        // Loop through all nodes and mark them as unvisited
        g.drawOval(0, 0, NODE_DIMENSION, NODE_DIMENSION);
        g.fillOval(0, 0, NODE_DIMENSION, NODE_DIMENSION);
        // Loop through all edges and paint them as unvisited
    }
}
