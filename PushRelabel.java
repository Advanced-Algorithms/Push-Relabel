import java.io.*;
import java.util.*;

public class PushRelabel{

	private ArrayList<Edge> edges;
	private ArrayList<Edge> residualEdges;
	private ArrayList<Edge> backEdges;
	private ArrayList<Node> verts;
	private ArrayList<Node> resVerts;
	private Stack<Node> stack = new Stack<>();
	/*
		Assuming input is formatted as follwos:
		# of Vertices
		# of Edges
		Edges in order with format (V1, V2, Capacity)
		Where vertices are labeled with index in Vertex array 
		Source is index 0 and sink is index size-1
	*/
	public void init(){
		edges = new ArrayList<>();
		residualEdges = new ArrayList<>();
		backEdges = new ArrayList<>();
		verts = new ArrayList<>();
		resVerts = new ArrayList<>();
		Scanner sc= new Scanner(System.in);
		int numVs = sc.nextInt();
		for (int i = 0; i < numVs; i++){
			verts.add(new Node());
			resVerts.add(new Node());
		}
		int numEs = sc.nextInt();
		for (int j = 0; j < numEs; j++){
			int v1 = sc.nextInt();
			int v2 = sc.nextInt();
			int cap = sc.nextInt();
			Edge e = new Edge(verts.get(v1), verts.get(v2), cap);
			edges.add(e);
			verts.get(v1).addOutEdge(e);
			verts.get(v2).addInEdge(e);
			Edge resE = new Edge(verts.get(v1), verts.get(v2), cap);
			residualEdges.add(resE);
			resVerts.get(v1).addOutEdge(resE);
			resVerts.get(v2).addInEdge(resE);
			Edge be = new Edge(verts.get(v2), verts.get(v1), 0);
			backEdges.add(be);
			resVerts.get(v2).addOutEdge(be);
			resVerts.get(v1).addInEdge(be);
			be.setIsReverse(true);
			resE.setReverse(be);
		}
		Node source = verts.get(0);
		source.setHeight(numVs);
		resVerts.get(0).setHeight(numVs);
		ArrayList<Edge> sourceOut = source.getOutArray();
		for (int k = 0; k < sourceOut.size(); k++){
			Edge ed = sourceOut.get(k);
			ed.setFlow(ed.getCapacity());
			ed.getDest().addToPreflow(ed.getFlow());
			int index = findIndex(ed);
			residualEdges.get(index).setFlow(ed.getFlow());
			residualEdges.get(index).getReverse().setCapacity(residualEdges.get(index).getFlow());
		}
	}

	private int findIndex(Edge e){
		for (int i = 0; i < edges.size(); i++){
			if (e.equals(edges.get(i)))
				return i;
		}
		return -1;
	}

	public void push (Node n){
		if (n.getPreflow() > 1){
			ArrayList<Edge> out = n.getOutArray();
			for (int i = 0; i < out.size(); i++){
				Edge e = out.get(i);
				Edge be = n.getInArray().get(i);
				if (n.getHeight() == e.getDest().getHeight() + 1){
					int delta = Math.min(n.calcExcess(), e.getCapacity() - e.getFlow());
					e.setFlow(e.getFlow() + delta);
					be.setFlow(be.getFlow() - delta);
					if (e.isReverse()){
						e.setCapacity(be.getFlow());
					}
					else{
						be.setCapacity(e.getFlow());	
					}
					n.addToOutflow(delta);
					e.getDest().addToPreflow(delta);
				}
			}
		}
	}

	public boolean relabel (Node n){
		if (n.calcExcess() > 0){
			boolean relabel = true;
			ArrayList<Edge> out = n.getOutArray();
			int min = Integer.MAX_VALUE;
			for (Edge e : out){
				if (!((e.getCapacity() - e.getFlow() > 0) && (e.getDest().getHeight()>=n.getHeight()))){
					relabel = false;
				}
				else{
					if (e.getCapacity() - e.getFlow() > 0){
						if (e.getDest().getHeight() < min){
							min = e.getDest().getHeight();
						}
					}
				}
			}
			if (relabel){
				n.setHeight(min + 1);
				return true;
			}
		}
		return false;
	}

	public int getMaxFlow(){
		boolean cont = true;
		int counter = 0;
		while (cont == true){
			System.out.println(counter++);
			cont = false;
			for (Node n : resVerts){
				if (n.calcExcess() > 0){
					push(n);
					relabel(n);
					cont = true;
				}
			}
		}
		return resVerts.get(resVerts.size()-1).calcPreflow();
	}

	public static void main(String[] args){
		PushRelabel pl = new PushRelabel();
		pl.go(args);
	}

	public void go(String[] args){
		init();
		System.out.println(getMaxFlow());
	}

}