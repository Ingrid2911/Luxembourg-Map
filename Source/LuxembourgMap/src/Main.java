import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
class Graph {
    Map<Integer, Node> nodes = new HashMap<>();
    List<Edge> edges = new ArrayList<>();

    public void addNode(int id, double longitude, double latitude) {
        nodes.put(id, new Node(id, longitude, latitude));
    }

    public void addEdge(int from, int to, int length) {
        edges.add(new Edge(from, to,length ));
    }
}


public class Main {
    public static void main(String[] args) {
        // Calea către fișierul XML
        String filePath = "src/hartaLuxembourg.xml";

        // Parsare fișier XML
        Graph graph = SAXGraphParser.parseGraph(filePath);

        // Creare fereastră pentru vizualizare
        JFrame frame = new JFrame("Luxembourg map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());

        MapVisualizer visualizer = new MapVisualizer(graph);
        frame.add(visualizer, BorderLayout.CENTER);

        frame.setVisible(true);

        }
    }






