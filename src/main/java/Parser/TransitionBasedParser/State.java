package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 14.12.2020 */

import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;
import java.util.Stack;

public class State {

    private Stack<StackWord> stack;
    private ArrayList<StackWord> wordList;
    private ArrayList<StackRelation> relations;

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
    public void applyShift() {
        if (wordList.size() > 0) {
            stack.add(wordList.remove(0));
        }
    }

	public void applyLeftArc(UniversalDependencyType type) {
        if (stack.size() > 1) {
            UniversalDependencyTreeBankWord beforeLast = stack.get(stack.size() - 2).getWord();
            int index = stack.get(stack.size() - 1).getToWord();
            beforeLast.setRelation(new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":")));
            stack.remove(stack.size() - 2);
            relations.add(new StackRelation(beforeLast, new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":"))));
        }
    }

    public void applyRightArc(UniversalDependencyType type) {
        if (stack.size() > 1) {
            UniversalDependencyTreeBankWord last = stack.get(stack.size() - 1).getWord();
            int index = stack.get(stack.size() - 2).getToWord();
            last.setRelation(new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":")));
            stack.pop();
            relations.add(new StackRelation(last, new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":"))));
        }
    }

    public void applyArcEagerLeftArc(UniversalDependencyType type) {
        if (stack.size() > 0 && wordList.size() > 0) {
            UniversalDependencyTreeBankWord lastElementOfStack = stack.peek().getWord();
            int index = wordList.get(0).getToWord();
            lastElementOfStack.setRelation(new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":")));
            stack.pop();
            relations.add(new StackRelation(lastElementOfStack, new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":"))));
        }
    }

    public void applyArcEagerRightArc(UniversalDependencyType type) {
        if (stack.size() > 0 && wordList.size() > 0) {
            UniversalDependencyTreeBankWord firstElementOfWordList = wordList.get(0).getWord();
            int index = stack.peek().getToWord();
            firstElementOfWordList.setRelation(new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":")));
            applyShift();
            relations.add(new StackRelation(firstElementOfWordList, new UniversalDependencyRelation(index, type.toString().replaceAll("_", ":"))));
        }
    }

    public void applyReduce() {
        if (stack.size() > 0) {
            stack.pop();
        }
    }

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

    public int relationSize() {
        return relations.size();
    }

    public int wordListSize() {
        return wordList.size();
    }

    public int stackSize() {
        return stack.size();
    }

    public UniversalDependencyTreeBankWord getStackWord(int index) {
        int size = stack.size() - 1;
        if (size - index < 0) {
            return null;
        }
        return stack.get(size - index).getWord();
    }

    public UniversalDependencyTreeBankWord getPeek() {
        if (stack.size() > 0) {
            return stack.peek().getWord();
        }
        return null;
    }

    public UniversalDependencyTreeBankWord getWordListWord(int index) {
        if (index > wordList.size() - 1) {
            return null;
        }
        return wordList.get(index).getWord();
    }

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
    public Object clone() throws CloneNotSupportedException {
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
