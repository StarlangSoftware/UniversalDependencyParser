package Parser.TransitionBasedParser;
import java.io.IOException;

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;



public class OracleDriver {
	public static void main(String args[]) throws IOException {
		ArcStandardOracle oracle = new ArcStandardOracle();
		
		oracle.train("C:\\test");
		oracle.createModel();
		oracle.getModel().save("C:\\test\\model");
		
		ArcStandardTransitionParser parser = new ArcStandardTransitionParser();
		UniversalDependencyTreeBankSentence sentence = oracle.readSentence("C:\\test\\0000.dev");
		UniversalDependencyTreeBankSentence parsedSentence = parser.dependencyParse(sentence, oracle);
		
		System.out.println(parsedSentence);
		
		
		
	}
}
