//***STILL A WIP.  Currently Debugging***

import java.io.*;
import java.util.*;

public class PushRelabel{

	//The original graph
	private ArrayList<Edge> edges;
	private ArrayList<Node> verts;
	//The residual graph.  All the operations work on the residual graph.
	private ArrayList<Edge> residualEdges;
	private ArrayList<Edge> backEdges;
	private ArrayList<Node> resVerts;

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
			//Have copies of all vertices in both graphs
			verts.add(new Node());
			resVerts.add(new Node());
		}
		int numEs = sc.nextInt();
		for (int j = 0; j < numEs; j++){
			//From
			int v1 = sc.nextInt();
			//To
			int v2 = sc.nextInt();
			//Capacity
			int cap = sc.nextInt();
			Edge e = new Edge(verts.get(v1), verts.get(v2), cap);
			edges.add(e);
			verts.get(v1).addOutEdge(e);
			verts.get(v2).addInEdge(e);
			//Edge in residual graph
			Edge resE = new Edge(resVerts.get(v1), resVerts.get(v2), cap);
			residualEdges.add(resE);
			resVerts.get(v1).addOutEdge(resE);
			resVerts.get(v2).addInEdge(resE);
			//The reverse edge
			Edge be = new Edge(resVerts.get(v2), resVerts.get(v1), 0);
			backEdges.add(be);
			resVerts.get(v2).addOutEdge(be);
			resVerts.get(v1).addInEdge(be);
			//For later might not be needed
			be.setIsReverse(true);
			resE.setReverse(be);
		}
		//Set source height
		Node source = verts.get(0);
		source.setHeight(numVs);
		resVerts.get(0).setHeight(numVs);
		//Set all outflow edges from Source to be their capacities
		ArrayList<Edge> sourceOut = source.getOutArray();
		for (int k = 0; k < sourceOut.size(); k++){
			Edge ed = sourceOut.get(k);
			ed.setFlow(ed.getCapacity());
			ed.getDest().addToPreflow(ed.getFlow());
			int index = findIndex(ed);
			//Replicate in residual graph
			residualEdges.get(index).setFlow(ed.getFlow());
			residualEdges.get(index).getDest().addToPreflow(ed.getFlow());
			//Capacity of reverse/backedge should be the flow of front edge i think
			//Source: http://www.keithschwarz.com/interesting/code/ford-fulkerson/ResidualGraph.java.html
			residualEdges.get(index).getReverse().setCapacity(residualEdges.get(index).getFlow());
		}
	}

	//Really should be using a hashmap instead of this or at least better organization
	private int findIndex(Edge e){
		for (int i = 0; i < edges.size(); i++){
			if (e.equals(edges.get(i)))
				return i;
		}
		return -1;
	}

	//Poor translation of alg from wikipedia
	//Source: https://en.wikipedia.org/wiki/Push%E2%80%93relabel_maximum_flow_algorithm
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

	//Wasn't too sure how to ensure algorithm continues running on active nodes.
	//Can probably be optimized via "what operations can activate nodes"
	public int getMaxFlow(){
		boolean cont = true;
		while (cont == true){
			//printNodes();
			//printEdges(residualEdges);
			//printEdges(backEdges);
			cont = false;
			for (Node n : resVerts){
				if (n.calcExcess() > 0){
					push(n);
					relabel(n);
					//Did things should probably check again.
					cont = true;
				}
			}
		}
		//Ideally the maxflow would be the flow into the last node.
		return resVerts.get(resVerts.size()-1).calcPreflow();
	}

	//Classic 112esque code
	public static void main(String[] args){
		PushRelabel pl = new PushRelabel();
		pl.go(args);
	}

	public void go(String[] args){
		init();
		System.out.println(getMaxFlow());
	}

	public void printNodes(){
		for (Node n : resVerts){
			System.out.println("Preflow: " + n.getPreflow() + " Outflow: " + n.getOutflow() + " Height: " + n.getHeight());
		}
	}

	public void printEdges(ArrayList<Edge> edge){
		for (Edge e : edge){
			System.out.println("Coords: " + e.getSource() + " " + e.getDest() + " Cap:" + e.getCapacity() + " Flow" + e.getFlow());
		}
	}

}