package TreebankUtils;

public class Edge {
	private final Node fromNode;
	private final Node toNode;
	private String edgeLabel;
	
	public Edge(Node from, Node to) {
		toNode = to;
		fromNode = from;
	}
	
	public Edge(Node from, Node to, String label) {
		toNode = to;
		fromNode = from;
		edgeLabel = label;
	}
	
	public Node getFromNode() {
		return fromNode;
	}
	
	public Node getToNode() {
		return toNode;
	}
	
	public String getLabel() {
		return edgeLabel;
	}
}
