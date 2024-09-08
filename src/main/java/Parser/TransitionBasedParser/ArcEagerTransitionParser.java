package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 14.12.2020 */

import Classification.Instance.Instance;
import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class ArcEagerTransitionParser extends TransitionParser {

    public ArcEagerTransitionParser() {
        super();
    }

    /**
     * Simulates the parsing process for a given sentence using the Arc Eager parsing algorithm.
     * @param sentence The sentence to be parsed.
     * @param windowSize The size of the window used for feature generation.
     * @return An ArrayList of {@link Instance} objects representing the parsed actions.
     */
    @Override
    public ArrayList<Instance> simulateParse(UniversalDependencyTreeBankSentence sentence, int windowSize) {
        UniversalDependencyTreeBankWord top, first;
        UniversalDependencyRelation topRelation = null, firstRelation;
        InstanceGenerator instanceGenerator = new ArcEagerInstanceGenerator();
        ArrayList<Instance> instanceList = new ArrayList<>();
        HashMap<Integer, UniversalDependencyTreeBankWord> wordMap = new HashMap<>();
        ArrayList<StackWord> wordList = new ArrayList<>();
        Stack<StackWord> stack = new Stack<>();
        for (int j = 0; j < sentence.wordCount(); j++) {
            UniversalDependencyTreeBankWord word = (UniversalDependencyTreeBankWord) sentence.getWord(j);
            UniversalDependencyTreeBankWord clone = word.clone();
            clone.setRelation(null);
            wordMap.put(j + 1, word);
            wordList.add(new StackWord(clone, j + 1));
        }
        stack.add(new StackWord());
        State state = new State(stack, wordList, new ArrayList<>());
        while (!wordList.isEmpty() || stack.size() > 1) {
            if (!wordList.isEmpty()) {
                first = wordList.get(0).getWord();
                firstRelation = wordMap.get(wordList.get(0).getToWord()).getRelation();
            } else {
                first = null;
                firstRelation = null;
            }
            top = stack.peek().getWord();
            if (!top.getName().equals("root")) {
                topRelation = wordMap.get(stack.peek().getToWord()).getRelation();
            }
            if (stack.size() > 1) {
                if (firstRelation != null && firstRelation.to() == top.getId()) {
                    instanceList.add(instanceGenerator.generate(state, windowSize, "RIGHTARC(" + firstRelation + ")"));
                    StackWord word = wordList.remove(0);
                    stack.add(new StackWord(wordMap.get(word.getToWord()), word.getToWord()));
                } else if (first != null && topRelation != null && topRelation.to() == first.getId()) {
                    instanceList.add(instanceGenerator.generate(state, windowSize, "LEFTARC(" + topRelation + ")"));
                    stack.pop();
                } else if (!wordList.isEmpty()) {
                    instanceList.add(instanceGenerator.generate(state, windowSize, "SHIFT"));
                    stack.add(wordList.remove(0));
                } else {
                    instanceList.add(instanceGenerator.generate(state, windowSize, "REDUCE"));
                    stack.pop();
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
        return instanceList;
    }

    /**
     * Performs dependency parsing on the given sentence using the provided oracle.
     * @param universalDependencyTreeBankSentence The sentence to be parsed.
     * @param oracle The oracle used to make parsing decisions.
     * @return The parsed sentence with dependency relations established.
     */
    @Override
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
                    state.applyArcEagerLeftArc(decision.getUniversalDependencyType());
                    break;
                case RIGHTARC:
                    state.applyArcEagerRightArc(decision.getUniversalDependencyType());
                    break;
                case REDUCE:
                    state.applyReduce();
                    break;
                default:
                    break;
            }
        }
        return sentence;
    }
}
