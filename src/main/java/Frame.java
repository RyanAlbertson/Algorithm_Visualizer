import javax.swing.*;

public class Frame extends JFrame {

    private GraphPanel graphPanel;

    public Frame() {

        graphPanel = new GraphPanel();
        this.add(graphPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        // BORDER LAYOUT - MENU IS AT NORTH BORDER, GRAPH IS IN CENTER
        // NEED START AND STOP BUTTONS
        // NEED COMBO BOX (DROP DOWN TO CHOOSE ALGORITHM + 1 FOR CHOOSING GRAPHS)
    }
}
