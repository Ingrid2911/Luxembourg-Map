import java.util.*;

class Dijkstra {
    public static List<Integer> dijkstraWithPath(Graph graph, int startNode, int endNode) {
        SAXGraphParser mapParser = new SAXGraphParser();
        Map<Integer, Vector<Edge>> mapArcList=mapParser.getArcList();
        // Harta de distanțe (se va inițializa pe măsură ce sunt vizitate nodurile)
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>(); // Predecesorii nodurilor
        distances.put(startNode, 0); // Distanta nodului de start este 0

        // Coada de priorități pentru procesarea nodurilor (prioritizează nodurile cu distanțe mici)
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.distance));
        pq.add(new NodeDistance(startNode, 0));

        // Set pentru nodurile deja procesate
        Set<Integer> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            int currentNode = current.node;

            // Dacă nodul curent a fost deja procesat, continuăm
            if (visited.contains(currentNode)) continue;

            // Marcăm nodul ca vizitat
            visited.add(currentNode);

            // Dacă am ajuns la nodul țintă, reconstruim drumul și returnăm
            if (currentNode == endNode) {
                return reconstructPath(predecessors, startNode, endNode);
            }

            // Actualizăm distanțele pentru vecini
            for (Edge edge : mapArcList.getOrDefault(currentNode, new Vector<>())) {
                int neighbor = edge.to;
                // Inițializăm distanța nodului vecin doar când este necesar
                int currentDist = distances.getOrDefault(currentNode, Integer.MAX_VALUE);
                int newDist = currentDist + edge.length;

                // Verificăm și actualizăm distanța doar dacă găsim o cale mai scurtă
                if (newDist < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, currentNode); // Setăm predecesorul
                    pq.add(new NodeDistance(neighbor, newDist));
                }
            }
        }

        // Dacă nodul țintă nu poate fi atins, returnăm o listă goală
        return new ArrayList<>();
    }

    // Reconstruirea drumului de la startNode la endNode
    private static List<Integer> reconstructPath(Map<Integer, Integer> predecessors, int startNode, int endNode) {
        List<Integer> path = new ArrayList<>();
        int currentNode = endNode;

        while (currentNode != startNode) {
            path.add(currentNode);
            currentNode = predecessors.getOrDefault(currentNode, -1);
            if (currentNode == -1) {
                return new ArrayList<>(); // Nu există drum
            }
        }

        path.add(startNode); // Adăugăm nodul de start
        Collections.reverse(path); // Inversăm drumul pentru a fi în ordinea corectă
        return path;
    }
}

class NodeDistance {
    int node;
    int distance;

    public NodeDistance(int node, int distance) {
        this.node = node;
        this.distance = distance;
    }
}

