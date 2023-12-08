package Parser.TransitionBasedParser;

<<<<<<< HEAD
import Classification.Instance.Instance;
import Classification.Model.Model;
=======
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
>>>>>>> bee5aa9b3dacd37acd20c4d9fe15d8bd81f7df65

import java.util.ArrayList;

public class ArcStandardOracle extends Oracle {

    public ArcStandardOracle(Model model, int windowSize) {
        super(model, windowSize);
    }
    @Override
    public Decision makeDecision(State state) {
        String best;
        InstanceGenerator instanceGenerator = new SimpleInstanceGenerator();
        Instance instance = instanceGenerator.generate(state, this.windowSize, "");
        best = findBestValidStandardClassInfo(commandModel.predictProbability(instance), state);
        Candidate decisionCandidate = getDecisionCandidate(best);
        if (decisionCandidate.getCommand().equals(Command.SHIFT)) {
            return new Decision(Command.SHIFT, null, 0.0);
        }
        return new Decision(decisionCandidate.getCommand(), decisionCandidate.getUniversalDependencyType(), 0.0);
    }

<<<<<<< HEAD
    @Override
    public ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
        return null;
=======
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
>>>>>>> bee5aa9b3dacd37acd20c4d9fe15d8bd81f7df65
    }
}
