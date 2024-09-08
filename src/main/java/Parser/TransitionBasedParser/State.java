package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 14.12.2020 */

import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;
import java.util.Stack;

public class State {

    private final Stack<StackWord> stack;
    private final ArrayList<StackWord> wordList;
    private final ArrayList<StackRelation> relations;

    /**
     * Constructs a State object with given stack, wordList, and relations.
     *
     * @param stack     The stack of words in the parser state.
     * @param wordList  The list of words to be processed.
     * @param relations The relations established between words.
     */
    public State(Stack<StackWord> stack, ArrayList<StackWord> wordList, ArrayList<StackRelation> relations) {
        this.stack = stack;
        this.wordList = wordList;
        this.relations = relations;
    }
    
    public void print() {
    	for(int i = 0; i < this.relationSize(); i++) {
//    		this.relations.get(i).getKey()
    	}
    }

    /**
     * Applies the SHIFT operation to the parser state.
     * Moves the first word from the wordList to the stack.
     */
    public void applyShift() {
        if (!wordList.isEmpty()) {
            stack.add(wordList.remove(0));
        }
    }

    /**
     * Applies the LEFTARC operation to the parser state.
     * Creates a relation from the second-to-top stack element to the top stack element
     * and then removes the second-to-top element from the stack.
     * @param type The type of the dependency relation.
     */
	public void applyLeftArc(UniversalDependencyType type) {
        if (stack.size() > 1) {
            UniversalDependencyTreeBankWord beforeLast = stack.get(stack.size() - 2).getWord();
            int index = stack.get(stack.size() - 1).getToWord();
            beforeLast.setRelation(new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":")));
            stack.remove(stack.size() - 2);
            relations.add(new StackRelation(beforeLast, new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":"))));
        }
    }

    /**
     * Applies the RIGHTARC operation to the parser state.
     * Creates a relation from the top stack element to the second-to-top stack element
     * and then removes the top element from the stack.
     *
     * @param type The type of the dependency relation.
     */
    public void applyRightArc(UniversalDependencyType type) {
        if (stack.size() > 1) {
            UniversalDependencyTreeBankWord last = stack.get(stack.size() - 1).getWord();
            int index = stack.get(stack.size() - 2).getToWord();
            last.setRelation(new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":")));
            stack.pop();
            relations.add(new StackRelation(last, new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":"))));
        }
    }

    /**
     * Applies the ARC_EAGER_LEFTARC operation to the parser state.
     * Creates a relation from the last element of the stack to the first element of the wordList
     * and then removes the top element from the stack.
     *
     * @param type The type of the dependency relation.
     */
    public void applyArcEagerLeftArc(UniversalDependencyType type) {
        if (!stack.isEmpty() && !wordList.isEmpty()) {
            UniversalDependencyTreeBankWord lastElementOfStack = stack.peek().getWord();
            int index = wordList.get(0).getToWord();
            lastElementOfStack.setRelation(new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":")));
            stack.pop();
            relations.add(new StackRelation(lastElementOfStack, new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":"))));
        }
    }

    /**
     * Applies the ARC_EAGER_RIGHTARC operation to the parser state.
     * Creates a relation from the first element of the wordList to the top element of the stack
     * and then performs a SHIFT operation.
     *
     * @param type The type of the dependency relation.
     */
    public void applyArcEagerRightArc(UniversalDependencyType type) {
        if (!stack.isEmpty() && !wordList.isEmpty()) {
            UniversalDependencyTreeBankWord firstElementOfWordList = wordList.get(0).getWord();
            int index = stack.peek().getToWord();
            firstElementOfWordList.setRelation(new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":")));
            applyShift();
            relations.add(new StackRelation(firstElementOfWordList, new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":"))));
        }
    }

    /**
     * Applies the REDUCE operation to the parser state.
     * Removes the top element from the stack.
     */
    public void applyReduce() {
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    /**
     * Applies a specific command based on the transition system.
     *
     * @param command The command to be applied (e.g., SHIFT, LEFTARC, RIGHTARC, REDUCE).
     * @param type The type of dependency relation, relevant for ARC operations.
     * @param transitionSystem The transition system (e.g., ARC_STANDARD, ARC_EAGER) that determines which command to apply.
     */
    public void apply(Command command, UniversalDependencyType type, TransitionSystem transitionSystem) {
        switch (transitionSystem) {
            case ARC_STANDARD:
                switch (command) {
                    case LEFTARC:
                        applyLeftArc(type);
                        break;
                    case RIGHTARC:
                        applyRightArc(type);
                        break;
                    case SHIFT:
                        applyShift();
                        break;
                    default:
                        break;
                }
                break;
            case ARC_EAGER:
                switch (command) {
                    case LEFTARC:
                        applyArcEagerLeftArc(type);
                        break;
                    case RIGHTARC:
                        applyArcEagerRightArc(type);
                        break;
                    case SHIFT:
                        applyShift();
                        break;
                    case REDUCE:
                        applyReduce();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Returns the number of relations established in the current state.
     * @return The size of the relations list.
     */
    public int relationSize() {
        return relations.size();
    }

    /**
     * Returns the number of words remaining in the wordList.
     * @return The size of the wordList.
     */
    public int wordListSize() {
        return wordList.size();
    }

    /**
     * Returns the number of words currently in the stack.
     * @return The size of the stack.
     */
    public int stackSize() {
        return stack.size();
    }

    /**
     * Retrieves a specific word from the stack based on its position.
     * @param index The position of the word in the stack.
     * @return The word at the specified position, or null if the index is out of bounds.
     */
    public UniversalDependencyTreeBankWord getStackWord(int index) {
        int size = stack.size() - 1;
        if (size - index < 0) {
            return null;
        }
        return stack.get(size - index).getWord();
    }

    /**
     * Retrieves the top word from the stack.
     * @return The top word of the stack, or null if the stack is empty.
     */
    public UniversalDependencyTreeBankWord getPeek() {
        if (!stack.isEmpty()) {
            return stack.peek().getWord();
        }
        return null;
    }

    /**
     * Retrieves a specific word from the wordList based on its position.
     * @param index The position of the word in the wordList.
     * @return The word at the specified position, or null if the index is out of bounds.
     */
    public UniversalDependencyTreeBankWord getWordListWord(int index) {
        if (index > wordList.size() - 1) {
            return null;
        }
        return wordList.get(index).getWord();
    }

    /**
     * Retrieves a specific relation based on its index.
     * @param index The position of the relation in the relations list.
     * @return The relation at the specified position, or null if the index is out of bounds.
     */
    public StackRelation getRelation(int index) {
        if (index < relations.size()) {
            return relations.get(index);
        }
        return null;
    }

    @Override
    public int hashCode() {
        return this.stack.hashCode() ^ this.wordList.hashCode() ^ this.relations.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) {
            return false;
        }
        State state = (State) obj;
        return this.stack.equals(state.stack) && this.wordList.equals(state.wordList) && this.relations.equals(state.relations);
    }

    @Override
    public Object clone() {
        State o = new State(new Stack<>(), new ArrayList<>(), new ArrayList<>());
        for (StackWord element : stack) {
            if (!element.getWord().getName().equals("root")) {
                o.stack.add(element.clone());
            } else {
                o.stack.add(new StackWord(new UniversalDependencyTreeBankWord(), element.getToWord()));
            }
        }
        for (StackWord word : wordList) {
            o.wordList.add(word.clone());
        }
        for (StackRelation relation : relations) {
            if (!relation.getWord().getName().equals("root")) {
                o.relations.add(relation.clone());
            } else {
                o.relations.add(new StackRelation(new UniversalDependencyTreeBankWord(), relation.getRelation()));
            }
        }
        return o;
    }
}
