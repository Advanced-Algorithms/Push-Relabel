import java.util.*;

public class Node{
	private ArrayList<Edge> inArray;
	private ArrayList<Edge> outArray;
	private int preflow;
	private int outflow;
	private int height;

	public Node(){
		inArray = new ArrayList<>();
		outArray = new ArrayList<>();
		preflow = 0;
		outflow = 0;
		height = 0;
	}

	public ArrayList<Edge> getInArray(){
		return inArray;
	}

	public void addInEdge(Edge e){
		inArray.add(e);
	}

	public ArrayList<Edge> getOutArray(){
		return outArray;
	}

	public void addOutEdge(Edge e){
		outArray.add(e);
	}

	public int getPreflow(){
		return preflow;
	}

	public int calcPreflow(){
		int flow = 0;
		for (Edge e : inArray){
			flow += e.getFlow();
		}
		preflow = flow;
		return flow;
	}

	public void addToPreflow(int add){
		preflow += add;
	}

	public int getOutflow(){
		return outflow;
	}

	public int calcOutflow(){
		int flow = 0;
		for (Edge e : outArray){
			flow += e.getFlow();
		}
		outflow = flow;
		return flow;
	}

	public void addToOutflow(int add){
		outflow += add;
	}

	public int getHeight(){
		return height;
	}

	public void setHeight(int h){
		height = h;
	}

	public void incrementHeight(){
		height++;
	}

	public int calcExcess(){
		return preflow-outflow;
	}
}