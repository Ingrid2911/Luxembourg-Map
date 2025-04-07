import java.util.List;
import java.util.Stack;

class KDTree {
    //structura de date de date pentru a organiza punctele intr un spatiu k dimensional
    private KDNode root;

    public KDTree(List<Node> nodes) {
        this.root = buildKDTree(nodes);
    }

    private KDNode buildKDTree(List<Node> nodes) {
        if (nodes.isEmpty()) {
            return null;
        }

        Stack<IterationState> stack = new Stack<>();
        stack.push(new IterationState(nodes, 0, null));

        KDNode root = null;
        KDNode currentNode = null;//nodul curent in constructia arborelui

        while (!stack.isEmpty()) {
            IterationState state = stack.pop();
            List<Node> currentNodes = state.nodes;
            int depth = state.depth;

            if (currentNodes.isEmpty()) {
                continue;
            }

            int axis = depth % 2; // 0 pentru longitude, 1 pentru latitude

            // Sortăm nodurile pe baza axei curente
            currentNodes.sort((node1, node2) -> {
                if (axis == 0) {
                    return Double.compare(node1.longitude, node2.longitude);
                } else {
                    return Double.compare(node1.latitude, node2.latitude);
                }
            });

            int medianIndex = currentNodes.size() / 2;//mijlocul listei de noduri
            Node medianNode = currentNodes.get(medianIndex);

            // Creăm nodul curent
            KDNode node = new KDNode(medianNode);

            // Dacă este rădăcina arborelui, o setăm
            if (currentNode == null) {
                root = node;
            } else {
                if (state.isLeft) {
                    currentNode.left = node;
                } else {
                    currentNode.right = node;
                }
            }

            // Adăugăm subarborii la stivă pentru a fi procesati
            List<Node> leftNodes = currentNodes.subList(0, medianIndex);
            List<Node> rightNodes = currentNodes.subList(medianIndex + 1, currentNodes.size());

            stack.push(new IterationState(leftNodes, depth + 1, node, true));  // Stiva pentru subarborele stâng
            stack.push(new IterationState(rightNodes, depth + 1, node, false)); // Stiva pentru subarborele drept

            // Setăm nodul curent pentru a lega subarborii
            currentNode = node;
        }

        return root;
    }

    private static class IterationState {
        List<Node> nodes;
        int depth;
        KDNode parentNode;
        boolean isLeft;

        IterationState(List<Node> nodes, int depth, KDNode parentNode, boolean isLeft) {
            this.nodes = nodes;
            this.depth = depth;
            this.parentNode = parentNode;
            this.isLeft = isLeft;
        }

        IterationState(List<Node> nodes, int depth, KDNode parentNode) {
            this(nodes, depth, parentNode, true); // default isLeft = true for root
        }
    }

    private NearestNeighbor findNearestNeighborIterative(KDNode root, Node target) {
        if (root == null) {
            return null;
        }

        Stack<NeighborSearchState> stack = new Stack<>();
        stack.push(new NeighborSearchState(root, 0, null));

        NearestNeighbor best = null;

        while (!stack.isEmpty()) {
            NeighborSearchState state = stack.pop();
            KDNode node = state.node;
            int depth = state.depth;

            if (node == null) {
                continue;
            }

            double distance = calculateDistance(target, node.node);
            if (best == null || distance < best.distance) {
                best = new NearestNeighbor(node.node, distance);
            }

            int axis = depth % 2;  // Axa de comparare
            KDNode nextNode = null;
            KDNode oppositeNode = null;

            if (axis == 0) {
                if (target.longitude < node.node.longitude) {
                    nextNode = node.left;
                    oppositeNode = node.right;
                } else {
                    nextNode = node.right;
                    oppositeNode = node.left;
                }
            } else {
                if (target.latitude < node.node.latitude) {
                    nextNode = node.left;
                    oppositeNode = node.right;
                } else {
                    nextNode = node.right;
                    oppositeNode = node.left;
                }
            }

            // Explorăm nodul următor
            stack.push(new NeighborSearchState(nextNode, depth + 1, best));

            // Verificăm dacă trebuie să explorăm și subarborele opus
            double axisDistance = (axis == 0) ? Math.abs(target.longitude - node.node.longitude) : Math.abs(target.latitude - node.node.latitude);
            if (best != null && axisDistance < best.distance) {
                stack.push(new NeighborSearchState(oppositeNode, depth + 1, best));
            }
        }

        return best;
    }

    private static class NeighborSearchState {
        KDNode node;
        int depth;
        NearestNeighbor best;

        NeighborSearchState(KDNode node, int depth, NearestNeighbor best) {
            this.node = node;
            this.depth = depth;
            this.best = best;
        }
    }

    private double calculateDistance(Node n1, Node n2) {
        double dx = n1.longitude - n2.longitude;
        double dy = n1.latitude - n2.latitude;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static class KDNode {
        Node node;
        KDNode left;
        KDNode right;

        KDNode(Node node) {
            this.node = node;
        }
    }

    private static class NearestNeighbor {
        Node node;
        double distance;

        NearestNeighbor(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}

