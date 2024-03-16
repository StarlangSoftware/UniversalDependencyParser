package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 14.12.2020 */

import Classification.Instance.Instance;
import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.ArrayList;
import java.util.Stack;

public class ArcStandardTransitionParser extends TransitionParser {

    public ArcStandardTransitionParser() {
        super();
    }

    private boolean checkForMoreRelation(ArrayList<StackWord> wordList, int id) {
        for (StackWord word : wordList) {
            if (word.getWord().getRelation().to() == id) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Instance> simulateParse(UniversalDependencyTreeBankSentence sentence, int windowSize) {
        UniversalDependencyTreeBankWord top, beforeTop;
        UniversalDependencyRelation topRelation, beforeTopRelation;
        InstanceGenerator instanceGenerator = new SimpleInstanceGenerator();
        ArrayList<Instance> instanceList = new ArrayList<>();
        ArrayList<StackWord> wordList = new ArrayList<>();
        Stack<StackWord> stack = new Stack<>();
        for (int j = 0; j < sentence.wordCount(); j++) {
            wordList.add(new StackWord((UniversalDependencyTreeBankWord) sentence.getWord(j), j + 1));
        }
        stack.add(new StackWord());
        State state = new State(stack, wordList, new ArrayList<>());
        if (!wordList.isEmpty()) {
            instanceList.add(instanceGenerator.generate(state, windowSize, "SHIFT"));
            stack.add(wordList.remove(0));
            if (wordList.size() > 1) {
                instanceList.add(instanceGenerator.generate(state, windowSize, "SHIFT"));
                stack.add(wordList.remove(0));
            }
            while (!wordList.isEmpty() || stack.size() > 1) {
                top = stack.peek().getWord();
                topRelation = top.getRelation();
                if (stack.size() > 1) {
                    beforeTop = stack.get(stack.size() - 2).getWord();
                    beforeTopRelation = beforeTop.getRelation();
                    if (beforeTop.getId() == topRelation.to() && checkForMoreRelation(wordList, top.getId())) {
                        instanceList.add(instanceGenerator.generate(state, windowSize, "RIGHTARC(" + topRelation + ")"));
                        stack.pop();
                    } else if (top.getId() == beforeTopRelation.to()) {
                        instanceList.add(instanceGenerator.generate(state, windowSize, "LEFTARC(" + beforeTopRelation + ")"));
                        stack.remove(stack.size() - 2);
                    } else {
                        if (!wordList.isEmpty()) {
                            instanceList.add(instanceGenerator.generate(state, windowSize, "SHIFT"));
                            stack.add(wordList.remove(0));
                        } else {
                            break;
                        }
                    }
                } else {
                    if (!wordList.isEmpty()) {
                        instanceList.add(instanceGenerator.generate(state, windowSize, "SHIFT"));
                        stack.add(wordList.remove(0));
                    } else {
                        break;
                    }
                }
            }
        }
        return instanceList;
    }

    public UniversalDependencyTreeBankSentence dependencyParse(UniversalDependencyTreeBankSentence universalDependencyTreeBankSentence, Oracle oracle) {
        UniversalDependencyTreeBankSentence sentence = createResultSentence(universalDependencyTreeBankSentence);
        State state = initialState(sentence);
        while (state.wordListSize() > 0 || state.stackSize() > 1) {
            Decision decision = oracle.makeDecision(state);
            switch (decision.getCommand()) {
                case SHIFT:
                    state.applyShift();
                    break;
                case LEFTARC:
                    state.applyLeftArc(decision.getUniversalDependencyType());
                    break;
                case RIGHTARC:
                    state.applyRightArc(decision.getUniversalDependencyType());
                    break;
                default:
                    break;
            }
        }
        return sentence;
    }
}
