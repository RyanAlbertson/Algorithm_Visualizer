import javax.swing.*;

public class Frame extends JFrame {

    private GraphPanel graphPanel;

    public Frame() {

        graphPanel = new GraphPanel();
        this.setBounds(-50, );
        this.add(graphPanel);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
