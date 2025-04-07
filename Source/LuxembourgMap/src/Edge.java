import java.util.Comparator;
import java.util.Objects;

class Edge {
    int from, to;
    int length;

    public Edge(int from, int to, int length) {
        this.from = from;
        this.to = to;
        this.length = length;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Verifică dacă sunt același obiect
        if (obj == null || getClass() != obj.getClass()) return false; // Verifică tipul obiectului
        Edge edge = (Edge) obj;
        return from == edge.from && to == edge.to && Double.compare(edge.length, length) == 0; // Compară valorile
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, length); // Hashing pe baza valorilor
    }

}
class EdgeComparator implements Comparator<Edge> {
    public int compare(Edge e1, Edge e2) {
        return Integer.compare(e1.length, e2.length);  // Comparăm lungimea
    }
}
