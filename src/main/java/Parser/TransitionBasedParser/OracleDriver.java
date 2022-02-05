package Parser.TransitionBasedParser;
import java.io.IOException;



public class OracleDriver {
	public static void main(String args[]) throws IOException {
		ArcStandardOracle oracle = new ArcStandardOracle();
		
		oracle.train("C:\\test");
		oracle.createModel();
		oracle.getModel().save("C:\\test\\model");
//		oracle.printInstances();
//		ArcStandardTransitionParser parser = new ArcStandardTransitionParser();

//		parser.initialState(oracle.sentences.get(0));
//		parser.dependencyParse(oracle.sentences.get(0), oracle);
//		oracle.listArcs();
		
		
		
	}
}
