import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;


public class MapVisualizer extends JPanel {
    private final Graph graph;
    private final KDTree tree;
    private final double minLongitude, maxLongitude, minLatitude, maxLatitude;
    private Node startNode = null;
    private Node endNode = null;
    private Node nearestNode = null;
    private boolean isStartNodeSelected = false; // pentru a urmări dacă a fost selectat deja nodul de start
    SAXGraphParser mapParser = new SAXGraphParser();
    Map<Integer, Vector<Edge>> mapArcList = mapParser.getArcList();
    List<Integer> nodePath = new ArrayList<>();//stocheaza nodurile din drumul minim
    private List<Edge> edgePath = new ArrayList<>(); // Stochează muchiile din drumul minim
    private JButton createDijkstraButton() {
        JButton PrimButton = new JButton("Algoritmul Dijkstra");
        PrimButton.setBackground(Color.WHITE);
        PrimButton.setFocusPainted(false);

        PrimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Calculăm drumul minim între nodul 1 și nodul 100 (sau orice alt nod dorit)
                nodePath = Dijkstra.dijkstraWithPath(graph, startNode.id, endNode.id);
                if (!nodePath.isEmpty()) {
                    edgePath = new ArrayList<>();
                    // Construim lista de arce din drumul minim
                    for (int i = 0; i < nodePath.size() - 1; i++) {
                        int from = nodePath.get(i);
                        int to = nodePath.get(i + 1);
                        // Căutăm arcurile care conectează aceste noduri
                        for (Edge edge : mapArcList.getOrDefault(from, new Vector<>())) {
                            if (edge.to == to) {
                                edgePath.add(edge);  // Adăugăm muchia în drumul minim
                                //System.out.println("Muchia adaugata este "+edge.from+" "+edge.to+" "+edge.length);
                                break;
                            }
                        }
                    }
                    repaint();  // Reîmpinge panoul pentru a desena drumul minim
                }
            }
        });
        return PrimButton;
    }

    public MapVisualizer(Graph graph) {
        this.graph = graph;
        this.tree=new KDTree(new ArrayList<>(graph.nodes.values()));

        this.add(createDijkstraButton());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickX = e.getX();
                int clickY = e.getY();
                updateNearestNode(clickX, clickY);  // Actualizează nearestNode

                // În funcție de dacă am selectat deja nodul de start, îl actualizăm pe cel de destinație
                if (!isStartNodeSelected) {
                    startNode = nearestNode;  // Salvează nodul selectat ca start
                    isStartNodeSelected = true;  // Marchează că am selectat nodul de start
                    System.out.println("Start Node: " + startNode.id);
                } else {
                    endNode = nearestNode;  // Salvează nodul selectat ca destinație
                    System.out.println("End Node: " + endNode.id);
                    isStartNodeSelected = false;  // Poți reseta după ce ai selectat ambele noduri
                }

                repaint();  // Reîmprospătează panoul
            }
        });

        // Calculează limitele pentru normalizare
        //valoarea minima a longitudinilor nodurilor
        this.minLongitude = graph.nodes.values().stream().mapToDouble(n -> n.longitude).min().orElse(0);
        this.maxLongitude = graph.nodes.values().stream().mapToDouble(n -> n.longitude).max().orElse(1);
        this.minLatitude = graph.nodes.values().stream().mapToDouble(n -> n.latitude).min().orElse(0);
        this.maxLatitude = graph.nodes.values().stream().mapToDouble(n -> n.latitude).max().orElse(1);
        //stream()-flux din valorile nodurilor

    }
    public void updateNearestNode(int clickX, int clickY) {
        double minDistance = Double.MAX_VALUE;
        nearestNode = null;

        for (Node node : graph.nodes.values()) {
            int nodeX = normalizeLongitude(node.longitude);
            int nodeY = normalizeLatitude(node.latitude);
            double distance = Math.sqrt(Math.pow(clickX - nodeX, 2) + Math.pow(clickY - nodeY, 2));

            if (distance < minDistance) {
                minDistance = distance;
                nearestNode = node; // Se actualizează variabila de instanță
            }
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Desenează arcele
        if (graph.edges.isEmpty()) {
            System.out.println("Lista de muchii este goala");
        }
        for (Edge edge : graph.edges) {
            Node from = graph.nodes.get(edge.from);
            Node to = graph.nodes.get(edge.to);

            if (from != null && to != null) {
                int x1 = normalizeLongitude(from.longitude);
                int y1 = normalizeLatitude(from.latitude);
                int x2 = normalizeLongitude(to.longitude);
                int y2 = normalizeLatitude(to.latitude);

                // Verifică dacă muchia face parte din drumul minim
                if(edgePath==null)
                {
                    System.out.println("lista de drumuri este goala");
                }
                if (edgePath.contains(edge)) {
                    //System.out.println("Muchia face parte din drum");
                    g2d.setColor(Color.BLUE);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawLine(x1, y1, x2, y2);
                      // Setează culoarea roșie pentru muchiile din drumul minim
                } else {
                    g2d.setColor(Color.BLACK); // Setează culoarea neagră pentru celelalte muchii
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawLine(x1, y1, x2, y2);
                }

            }
        }

        // Desenează nodurile
        for (Node node : graph.nodes.values()) {
            int x = normalizeLongitude(node.longitude);
            int y = normalizeLatitude(node.latitude);
            boolean isOnPath = edgePath.stream().anyMatch(edge -> edge.from == node.id || edge.to == node.id);
            if (startNode != null && node.id == startNode.id) {
                g2d.setColor(Color.BLUE); // Nodul de start va fi roșu
                g2d.fillOval(x - 1, y - 1, 6, 6);
            }
            // Verifică dacă nodul este de destinație
            else if (endNode != null && node.id == endNode.id) {
                g2d.setColor(Color.BLUE); // Nodul de destinație va fi, de asemenea, roșu
                g2d.fillOval(x - 1, y - 1, 6, 6);
            } else if (isOnPath) {
                g2d.setColor(Color.BLUE);  // Colorează nodul în albastru dacă face parte din drumul minim
                g2d.fillOval(x - 1, y - 1, 3, 3);  // Desenează nodul
            } else {
                g2d.setColor(Color.BLACK);  // Colorează nodul în negru dacă nu face parte din drumul minim
                g2d.fillOval(x - 1, y - 1, 1, 1);  // Desenează nodul
            }
        }
    }

    private int normalizeLongitude(double longitude) {
        double width = getWidth();
        return (int) ((longitude - minLongitude) / (maxLongitude - minLongitude) * width);
    }

    private int normalizeLatitude(double latitude) {
        double height = getHeight();
        return (int) ((maxLatitude - latitude) / (maxLatitude - minLatitude) * height); // Y inversat
    }
}
