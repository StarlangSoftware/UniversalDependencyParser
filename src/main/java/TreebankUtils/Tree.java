package TreebankUtils;

import java.util.ArrayList;

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

public class Tree {
	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	
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
					
					System.out.println("It's a match! " + i + "\t" + j);
					continue;
				}
			}
		}
		System.out.print("done");
		return mismatch;
	}
}
