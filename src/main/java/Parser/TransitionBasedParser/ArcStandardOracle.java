package Parser.TransitionBasedParser;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


import Classification.DistanceMetric.EuclidianDistance;
import Classification.InstanceList.InstanceList;
import Classification.Model.KnnModel;

import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;
import DependencyParser.Universal.UniversalDependencyType;
import tools.FileIO;

public class ArcStandardOracle implements Oracle {
	protected KnnModel commandModel;
	protected KnnModel relationModel;
	protected InstanceList commandInstances = new InstanceList();
	protected InstanceList relationInstances = new InstanceList();
		
	public ArcStandardOracle() {
        
    }
	
	public ArcStandardOracle(KnnModel commandModel, KnnModel relationModel) {
		this.commandModel = commandModel;
		this.relationModel = relationModel;
    }
	
//	public Model getModel() {
//		return arcStandardModel;
//	}
	
	protected String findClassInfo(HashMap<String, Double> probabilities, State state) {
        double bestValue = 0.0;
        String best = "";
        for (String key : probabilities.keySet()) {
            if (probabilities.get(key) > bestValue) {
                if (key.equals("SHIFT")) {
                    if (state.wordListSize() > 0) {
                        best = key;
                        bestValue = probabilities.get(key);
                    }
                } else if (state.stackSize() > 1) {
                    best = key;
                    bestValue = probabilities.get(key);
                }
            }
        }
        return best;
    }
	
	@Override
	public Decision makeDecision(State state, TransitionSystem transitionSystem) {
		SimpleInstanceGenerator instanceGenerator = new SimpleInstanceGenerator();
		 
		String commandClass = findClassInfo(commandModel.predictProbability(instanceGenerator.generate(state, 2, null)), state);
        
		if(commandClass.equals("SHIFT")) {
			return new Decision(Command.SHIFT, null, 0.0);
        }else if (commandClass.equals("LEFTARC")) {
        	String relationClass = findClassInfo(relationModel.predictProbability(instanceGenerator.generate(state, 2, null)), state);
        	return new Decision(Command.LEFTARC, UniversalDependencyType.valueOf(relationClass), 0.0);
        }else {
        	String relationClass = findClassInfo(relationModel.predictProbability(instanceGenerator.generate(state, 2, null)), state);
        	return new Decision(Command.RIGHTARC, UniversalDependencyType.valueOf(relationClass), 0.0);
        }
	}

	@Override
	public ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
		
		return null;
	}
	
	
	// Learn
	public void createModel() {
		commandModel = new KnnModel(commandInstances, 1, new EuclidianDistance());
		relationModel = new KnnModel(relationInstances, 1, new EuclidianDistance());
	}
	
	
	
	public void train(String path) throws IOException{
		for(File file : (new File(path)).listFiles()) {
			if(file.getName().endsWith("train")) {
				extractConfigurations(FileIO.readSentence(file.getAbsolutePath()));
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
										
					commandInstances.add(instanceGenerator.generate(state, 2, "LEFTARC"));
					relationInstances.add(instanceGenerator.generate(state, 2, belowTop.getRelation().toString()));
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
						commandInstances.add(instanceGenerator.generate(state, 2, "SHIFT"));
						state.applyShift();
					}else {
						commandInstances.add(instanceGenerator.generate(state, 2, "RIGHTARC"));
						relationInstances.add(instanceGenerator.generate(state, 2, top.getRelation().toString()));
						state.applyRightArc(UniversalDependencyType.valueOf(top.getRelation().toString().replace(':', '_')));
					}
				}else {
					// SHIFT
					
					commandInstances.add(instanceGenerator.generate(state, 2, "SHIFT"));
					state.applyShift();
				}
			}else {
				// SHIFT
				
				commandInstances.add(instanceGenerator.generate(state, 2, "SHIFT"));
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
	
//	public void printInstances() {
//		for(int i = 0; i < instances.size(); i++) {
//			System.out.println("Instance class label: " + instances.get(i).getClassLabel());
//			for(int j = 0; j < instances.get(i).attributeSize(); j++) {
//				System.out.println(instances.get(i).getAttribute(j));
//			}
//			System.out.println();
//		}
//	}
}
