package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;
import tools.FileIO;

public class ArcFactoredGraphOracle implements GraphOracle {
    private ArrayList<WeightedGraph> graphList = new ArrayList<WeightedGraph>();
	private HashMap<Feature, Double> FeatureVector = new HashMap<Feature, Double>();
	private ArrayList<String> bagOfWords = new ArrayList<String>();
	private ArrayList<String> bagOfPOS = new ArrayList<String>();
	
	@Override
    public double computeScore(UniversalDependencyTreeBankSentence sentence, int fromIndex, int toIndex) {
        return 0;
    }
    
    
    public void train(String path) throws IOException {
    	extractGraphs(path);
    	extractFeatures();

    	Set<String> set = new HashSet<>(bagOfWords);
    	bagOfWords.clear();
    	bagOfWords.addAll(set);

    	set = new HashSet<>(bagOfPOS);
    	bagOfPOS.clear();
    	bagOfPOS.addAll(set);
    }

    
    private void extractFeatures() {
		for(int i = 0; i < graphList.size(); i++) {
			WeightedGraph graph = graphList.get(i);
			for(int j = 0; j < graph.edgeList.size(); j++) {
				
			}
		}
	}

	private void extractGraphs(String path) throws IOException {
    	for (File file : (new File(path)).listFiles()) {
			if (file.getName().endsWith("train")) {
				UniversalDependencyTreeBankSentence sentence = FileIO.readSentence(file.getAbsolutePath());
				WeightedGraph graph = new WeightedGraph();
				ArrayList<UniversalDependencyTreeBankWord> words = new ArrayList<UniversalDependencyTreeBankWord>();
				
				for(int i = 0; i < sentence.wordCount(); i++) {
					UniversalDependencyTreeBankWord word = (UniversalDependencyTreeBankWord) sentence.getWord(i);
					words.add(word);
					
					bagOfWords.add(word.getName());
					bagOfPOS.add(word.getUpos().toString());
					
					UniversalDependencyTreeBankWord head;
					if(word.getRelation().to() == 0) {
						head = new UniversalDependencyTreeBankWord(0, "ROOT", null, null, null, null, null, null, null);
		
					} else {
						head = (UniversalDependencyTreeBankWord) sentence.getWord(word.getRelation().to() - 1);
					}
					graph.addDirectedEdge(word, head, null);
				}
				
				graphList.add(graph);
			}
		}
    }
        
    // Perceptron ile training setten feature weightlerini çıkar
    // Feature set için mcdonalda bak: 
    // Online Large-Margin Training of Dependency Parsers
}
