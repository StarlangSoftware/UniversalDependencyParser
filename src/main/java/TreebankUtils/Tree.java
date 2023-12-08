package TreebankUtils;

import java.util.ArrayList;

public class Tree {
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	
	public Tree() {
		
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public void addEdge(Edge edge) {
		this.edges.add(edge);
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < edges.size(); i++) {
			str.append(edges.get(i).getFromNode().getWord().getUpos().toString() + 
				" -- " + edges.get(i).getLabel() + " -> " + 
				edges.get(i).getToNode().getWord().getUpos().toString() + 
				System.lineSeparator());
		}
		
		return str.toString();
	}
	
	public ArrayList<Edge> getDifference(Tree difTree){
		ArrayList<Edge> mismatch = new ArrayList<Edge>();
		
		for(int i = 0; i < edges.size(); i++) {
			for(int j = 0; j < difTree.edges.size(); j++) {
				if(edges.get(i).getFromNode().getWord().getUpos() == difTree.edges.get(j).getFromNode().getWord().getUpos() &&
						edges.get(i).getToNode().getWord().getUpos() == difTree.edges.get(j).getToNode().getWord().getUpos() &&
						edges.get(i).getLabel() == difTree.edges.get(j).getLabel()) {
//					edges.remove(i);
//					edges.remove(j);
					System.out.println("It's a match! " + i + "\t" + j);
					continue;
				}
			}
		}
		System.out.print("done");
		return mismatch;
	}
}
