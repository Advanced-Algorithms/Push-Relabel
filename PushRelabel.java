import java.util.List;
import java.util.ArrayList;

public class PushRelabelReformat {

  List<Node> nodes;

  public PushRelabelReformat(int nodeCount) {
    nodes = new ArrayList<>();

    // initialize height and excess flow of nodes to 0
    for (int i = 0; i < nodeCount; i++) {
      nodes.add(new Node(0, 0));
    }
  }

  // Create edge and initialize its flow to zero
  public void addEdge(int source, int dest, int capacity) {
    nodes.get(source).addEdge(nodes.get(dest), 0, capacity);
  }

  /*
  * Max Flow:
  * - Maximum flow from source (first node) to sink (last node)
  */
  public int getMaxFlow() {
    preflow(nodes.get(0));
    // While there are active nodes, push or relabel
    Node activeNode = getActiveNode();
    while (activeNode != null) {
      if (!push(activeNode)) {
        relabel(activeNode);
      }
      activeNode = getActiveNode();
    }

    return nodes.get(nodes.size()-1).excessFlow;
  }

  /*
  * Preflow:
  * - Sets height of source to |V|
  * - Saturizes flow of edges adjacent to source
  * - Adds backward edges to residual graph
  */
  private void preflow(Node s) {
    s.height = nodes.size();

    for (Edge e : s.edges) {
      e.flow = e.capacity;
      e.dest.excessFlow += e.flow;
      e.dest.addEdge(s, -e.flow, 0); // Residual backwards edge
    }
  }

  /*
  * Push:
  * - Push flow from node with excess flow to those below it
  * - Decreases excess flow of pushing node
  * - Increases excess flow of pushed node
  * - Increases flow along the edge connecting them
  * - Updates (or creates) reverse edge along nodes
  */
  private boolean push(Node n) {
    for (Edge e : n.edges) {
      if ((n.height > e.dest.height) && (e.flow != e.capacity)) {
        int flow = Math.min(e.capacity - e.flow, n.excessFlow);
        n.excessFlow -= flow;
        e.dest.excessFlow += flow;
        e.flow += flow;
        updateReverseEdge(e, flow);
        return true;
      }
    }
    return false;
  }

  /*
  * Relabel:
  * - Finds adjacent node with lowest height
  * - Sets height of node to one greater than that
  */
  private void relabel(Node n) {
    int minAdjHeight = Integer.MAX_VALUE;
    for (Edge e : n.edges) {
      if ((e.flow != e.capacity) && (e.dest.height < minAdjHeight)) {
        minAdjHeight = e.dest.height;
        n.height = minAdjHeight + 1;
      }
    }
  }

  /*
  * Active Node:
  * - A node (excluding source and sink) with excess flow
  */
  private Node getActiveNode() {
    for (int i = 1; i < nodes.size()-1; i++) {
      if (nodes.get(i).excessFlow > 0) {
        return nodes.get(i);
      }
    }
    return null;
  }

  /*
  * Reverse Edge
  * - Used by the residual graph to allocate flow
  * - Represented here by negative flow and zero capacity
  */
  private void updateReverseEdge(Edge edge, int flow) {
    for (Edge e : edge.dest.edges) {
      if (e.dest.equals(edge.source)) {
        e.flow -= flow;
        return;
      }
    }

    edge.dest.addEdge(edge.source, -flow, 0);
  }

  /* Inner Classes */

  class Node {
    int height;
    int excessFlow;
    List<Edge> edges;

    Node(int height, int excessFlow) {
      this.height = height;
      this.excessFlow = excessFlow;
      edges = new ArrayList<>();
    }

    void addEdge(Node dest, int flow, int capacity) {
      Edge edge = new Edge(this, dest, flow, capacity);
      edges.add(edge);
    }

  }

  class Edge {
    Node source;
    Node dest;
    int flow;
    int capacity;

    Edge(Node source, Node dest, int flow, int capacity) {
      this.source = source;
      this.dest = dest;
      this.flow = flow;
      this.capacity = capacity;
    }

  }

}
