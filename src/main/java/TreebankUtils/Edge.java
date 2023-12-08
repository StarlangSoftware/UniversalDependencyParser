package TreebankUtils;

import DependencyParser.Universal.UniversalDependencyRelation;

public class Edge {
	private Node fromNode;
	private Node toNode;
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
