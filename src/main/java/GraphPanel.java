import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {

    static final int NODE_WIDTH = 10;
    static final int NODE_HEIGHT = 10;


    GraphPanel() {

        this.setPreferredSize(new Dimension(1280, 720));
        this.setAlignmentX(-20);
        this.setAlignmentY(-20);
    }

    public void paint(Graphics g) {

        g.setColor(Color.BLACK);
        g.drawOval(0, 0, NODE_WIDTH, NODE_HEIGHT);
        g.fillOval(0, 0, NODE_WIDTH, NODE_HEIGHT);
    }
}
