package TreebankUtils;

import java.util.ArrayList;

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

public class Tree {
	private final ArrayList<Node> nodes = new ArrayList<>();
	private final ArrayList<Edge> edges = new ArrayList<>();
	
	public Tree() {
		
	}
	
	public Tree(UniversalDependencyTreeBankSentence sentence) {
		for(int i = 0; i < sentence.wordCount(); i++) {
			UniversalDependencyTreeBankWord word = (UniversalDependencyTreeBankWord) sentence.getWord(i);
			Node node = new Node(word);
			addNode(node);
			
			if(word.getRelation().to() > 0) {
				UniversalDependencyTreeBankWord toWord = (UniversalDependencyTreeBankWord) sentence.getWord(word.getRelation().to() - 1);
				Node toNode = new Node(toWord);
				Edge edge = new Edge(node, toNode, word.getRelation().toString());
				addEdge(edge);
			}
		}
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public void addEdge(Edge edge) {
		this.edges.add(edge);
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();

        for (Edge edge : edges) {
            str.append(edge.getFromNode().getWord().getUpos().toString()).append(" -- ").append(edge.getLabel()).append(" -> ").append(edge.getToNode().getWord().getUpos().toString()).append(System.lineSeparator());
        }
		
		return str.toString();
	}
	
	public ArrayList<Edge> getDifference(Tree difTree){
		ArrayList<Edge> mismatch = new ArrayList<>();
		
		for(int i = 0; i < edges.size(); i++) {
			for(int j = 0; j < difTree.edges.size(); j++) {
				if(edges.get(i).getFromNode().getWord().getUpos() == difTree.edges.get(j).getFromNode().getWord().getUpos() &&
						edges.get(i).getToNode().getWord().getUpos() == difTree.edges.get(j).getToNode().getWord().getUpos() &&
						edges.get(i).getLabel().equals(difTree.edges.get(j).getLabel())) {
					System.out.println("It's a match! " + i + "\t" + j);
				}
			}
		}
		System.out.print("done");
		return mismatch;
	}
}
