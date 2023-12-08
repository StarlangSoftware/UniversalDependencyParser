package Parser.GraphBasedParser;

import DependencyParser.Universal.UniversalDependencyTreeBankWord;

public class Arc {
	private UniversalDependencyTreeBankWord from;
	private UniversalDependencyTreeBankWord to;
	
	public Arc() {
		
	}
	
	public Arc(UniversalDependencyTreeBankWord from, UniversalDependencyTreeBankWord to) {
		this.from = from;
		this.to = to;
	}
}
