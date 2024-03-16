package TreebankUtils;

import DependencyParser.Universal.UniversalDependencyTreeBankWord;

public class Node {
	private final UniversalDependencyTreeBankWord word;
	
	public Node(UniversalDependencyTreeBankWord word) {
		this.word = word;
	}
	
	public UniversalDependencyTreeBankWord getWord() {
		return word;
	}
}
