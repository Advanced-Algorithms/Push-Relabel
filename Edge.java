public class Edge{
	private int capacity;
	private int flow;
	private Node source;
	private Node dest;
	private Edge reverse;
	private boolean isReverse = false;

	public Edge(Node s, Node d, int cap){
		source = s;
		dest = d;
		capacity = cap;
		flow = 0;
	}

	public int calcResCap(){
		return capacity - flow;
	}

	public int getCapacity(){
		return capacity;
	}

	public void setCapacity(int cap){
		capacity = cap;
	}

	public int getFlow(){
		return flow;
	}

	public void setFlow(int amount){
		flow = amount;
	}

	public Node getSource(){
		return source;
	}

	public Node getDest(){
		return dest;
	}

	public Edge getReverse(){
		return reverse;
	}

	public void setReverse(Edge e){
		reverse = e;
	}

	public boolean isReverse(){
		return isReverse;
	}

	public void setIsReverse(boolean b){
		isReverse = b;
	}
}
