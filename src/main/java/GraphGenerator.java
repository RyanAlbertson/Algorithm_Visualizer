package main.java;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//RANDOMLY GENERATE GRAPH EACH TIME USER SELECTS THE GRAPH SIZE DROP DOWN BUTTON
public class GraphGenerator {

    // RANDOMLY CREATE COORDS FOR EACH NODE (WITHIN BOUNDS)
    // RANDOMLY ASSIGN ADJACENT NODES TO EACH NODE (HOW TO ENSURE THAT
    // GRAPH IS CONNECTED?)

    static void generateGraph(String size) {

        int numNodes = 0;
        switch (size) {
            case "Small" -> numNodes = 10;
            case "Medium" -> numNodes = 25;
            case "Large" -> numNodes = 50;
        }

        // Generate a graph
        StringBuilder line = new StringBuilder();
        Random random = new Random();
        List<String> lines = new ArrayList<String>();
        int maxAdjNodes = numNodes / 3;
        for (int i = 0; i < numNodes; i++) {
            int x = random.nextInt(GUI.WIDTH);
            int y = random.nextInt(GUI.HEIGHT);

            line.append(i).append(" ");
            line.append(x).append(" ");
        }

        //ADD RANDOM # OF ADJ NODES TO CURRENT NODE (random.getInt(maxAdjNodes),
        //AFTER ALL NODES AND EDGES ARE INITIALIZED, TEST WITH isConnected().
        //IF NOT CONNECTED, ADD AND EDGE BETWEEN EACH NODE IN THE SET OF
        //UNCONNECTED NODES TO A RANDOM ONE IN THE SET OF CONNECTED NODES

        // Write graph to file
        String fileName = GraphPanel.graphFileNames.get(size);
        Path file = Paths.get("\\resources\\graphs\\" + fileName);
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean isConnected() {

        return false;
    }
}
