package Parser.TransitionBasedParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteAttribute;
import Classification.DistanceMetric.EuclidianDistance;
import Classification.Instance.Instance;
import Classification.InstanceList.InstanceList;
import Classification.Model.KnnModel;
import Classification.Model.Model;
import Corpus.Sentence;
import DependencyParser.Universal.UniversalDependencyPosType;
import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankFeatures;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;
import DependencyParser.Universal.UniversalDependencyType;

public class ArcStandardOracle implements Oracle {
	protected Model arcStandardModel;
	protected InstanceList instances = new InstanceList();
	
//	public ArrayList<UniversalDependencyTreeBankSentence> sentences = new ArrayList<UniversalDependencyTreeBankSentence>();
	
	public ArcStandardOracle() {
        
    }
	
	public ArcStandardOracle(Model model) {
		arcStandardModel = model;
    }
	
	public Model getModel() {
		return arcStandardModel;
	}
	
	private String[] findClassInfo(HashMap<String, Double> probabilities, State state) {
		String decision[] = new String[2];
		
		return decision;
	}
	
	@Override
	public Decision makeDecision(State state, TransitionSystem transitionSystem) {
		Attribute stack0POS = new DiscreteAttribute(state.getStackWord(0).getUpos().toString());
		Attribute stack1POS = new DiscreteAttribute(state.getStackWord(1).getUpos().toString());
		Instance instance = new Instance(null);
		instance.addAttribute(stack0POS);
		instance.addAttribute(stack1POS);
		
		
		String[] classInfo = findClassInfo(arcStandardModel.predictProbability(instance), state);
        if(classInfo[0].equals("LEFTARC")) {
        	return new Decision(Command.LEFTARC, null, 0.0);
        }else if (classInfo[0].equals("RIGHTARC")) {
            return new Decision(Command.RIGHTARC, null, 0.0);
        }else {
        	return new Decision(Command.SHIFT, null, 0.0);
        }
        
	}

	@Override
	public ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
		
		return null;
	}
	
	
	// Learn
	public void createModel() {
		arcStandardModel = new KnnModel(instances, 1, new EuclidianDistance());
	}
	
	public UniversalDependencyTreeBankSentence readSentence(String fileName) throws IOException {
//		System.out.println(fileName);
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName), StandardCharsets.UTF_16));
		Sentence sentence = new UniversalDependencyTreeBankSentence();
		
		String line = "";
		
		while((line = reader.readLine()) != null) {
			StringTokenizer tokenizer = new StringTokenizer(line);
			String[] fields = new String[10];
			int i = 0;

			// Read input file into fields[]
			while(tokenizer.hasMoreTokens()) {
				fields[i++] = tokenizer.nextToken();
			}
			
			if(fields[0] != null) {
				UniversalDependencyTreeBankWord word = new UniversalDependencyTreeBankWord(
						Integer.parseInt(fields[0]),
						fields[1], fields[2], UniversalDependencyPosType.valueOf(fields[3]),
						fields[4], new UniversalDependencyTreeBankFeatures(fields[5]), 
						new UniversalDependencyRelation(Integer.parseInt(fields[6]), fields[7]),
						fields[8], fields[9]);
				
				sentence.addWord(word);
//				System.out.println(word);
			}
		}
//		sentences.add((UniversalDependencyTreeBankSentence) sentence);
//		System.out.println(sentence);
		reader.close();
		return (UniversalDependencyTreeBankSentence) sentence;
	}
	
	public void train(String path) throws IOException{
		for(File file : (new File(path)).listFiles()) {
			if(file.getName().endsWith("train")) {
				extractConfigurations(readSentence(file.getAbsolutePath()));
//				readSentence(file.getAbsolutePath());
			}
		}
	}
	
	
	public void extractConfigurations(UniversalDependencyTreeBankSentence sentence) {
		SimpleInstanceGenerator instanceGenerator = new SimpleInstanceGenerator();
		ArrayList<UniversalDependencyTreeBankWord> words = new ArrayList<UniversalDependencyTreeBankWord>();
		
		for(int i = 0; i < sentence.wordCount(); i++) {
			words.add((UniversalDependencyTreeBankWord) sentence.getWord(i));
		}
		
		State state = initialState(sentence);
		boolean complete = false;
		
		while(!complete) {
			printConfiguration(state, sentence);
			

			if(state.stackSize() >= 2) {
				UniversalDependencyTreeBankWord top =  state.getStackWord(0);
				UniversalDependencyTreeBankWord belowTop = state.getStackWord(1);
				
				
				if(belowTop.getRelation().to() == top.getId()) {
					// LEFT ARC
										
					instances.add(instanceGenerator.generate(state, 2, "LEFTARC"));
					state.applyLeftArc(UniversalDependencyType.valueOf(belowTop.getRelation().toString().replace(':', '_')));
				}else if(top.getRelation().to() == belowTop.getId()) {
					// RIGHT ARC
					

					boolean childrenRemaining = false;
					for(int i = 0; i < state.wordListSize(); i++) {
						if(state.getWordListWord(i).getRelation().to() == top.getId()) {
							childrenRemaining = true;
						}
					}
					
					if(childrenRemaining) {
						instances.add(instanceGenerator.generate(state, 2, "SHIFT"));
						state.applyShift();
					}else {
						instances.add(instanceGenerator.generate(state, 2, "RIGHTARC"));
						state.applyRightArc(UniversalDependencyType.valueOf(top.getRelation().toString().replace(':', '_')));
					}
				}else {
					// SHIFT
					
					instances.add(instanceGenerator.generate(state, 2, "SHIFT"));
					state.applyShift();
				}
			}else {
				// SHIFT
				
				instances.add(instanceGenerator.generate(state, 2, "SHIFT"));
				state.applyShift();
			}
			
			if(state.stackSize() == 1 && 
					state.getStackWord(0).getName().equalsIgnoreCase("root") && 
					state.wordListSize() == 0){
				complete = true;
				
				printConfiguration(state, sentence);
			}
		} // End while	
	}
	
	
	private State initialState(UniversalDependencyTreeBankSentence sentence) {
        ArrayList<AbstractMap.SimpleEntry<UniversalDependencyTreeBankWord, Integer>> wordList = new ArrayList<>();
        for (int i = 0; i < sentence.wordCount(); i++) {
            wordList.add(new AbstractMap.SimpleEntry<>((UniversalDependencyTreeBankWord) sentence.getWord(i), i + 1));
        }
        Stack<AbstractMap.SimpleEntry<UniversalDependencyTreeBankWord, Integer>> stack = new Stack<>();
        stack.add(new AbstractMap.SimpleEntry<>(new UniversalDependencyTreeBankWord(0, "root", "", null, "", null, new UniversalDependencyRelation(-1, ""), "", ""), 0));
        return new State(stack, wordList, new ArrayList<>());
    }
	
	public void printConfiguration(State state, UniversalDependencyTreeBankSentence sentence) {
		System.out.println("Stack content:");
		for(int i = 0; i < state.stackSize(); i++) {
			System.out.println(i + ": " + state.getStackWord(i).getName());
		}
		System.out.println();
		
		System.out.println("Buffer content:");
		for(int i = 0; i < state.wordListSize(); i++) {
			System.out.println(i + ": " + state.getWordListWord(i).getName());
		}
		System.out.println();
		
		System.out.println("Relations added:");
		for(int i = 0; i < state.relationSize(); i++) {
			int dependentIndex = state.getRelation(i).getKey().getId() - 1;
			int headIndex = state.getRelation(i).getValue().to() - 1;
			String relation = state.getRelation(i).getValue().toString();
			
			if(headIndex > 0) {
				System.out.print(sentence.getWord(headIndex).getName());
			}else {
				System.out.print("root");
			}
			
			System.out.println(" -- " + relation + " -> " + sentence.getWord(dependentIndex).getName());
		}
		System.out.println("----- End of phase ------\n");
	}
	
	public void printInstances() {
		for(int i = 0; i < instances.size(); i++) {
			System.out.println("Instance class label: " + instances.get(i).getClassLabel());
			for(int j = 0; j < instances.get(i).attributeSize(); j++) {
				System.out.println(instances.get(i).getAttribute(j));
			}
			System.out.println();
		}
	}
}