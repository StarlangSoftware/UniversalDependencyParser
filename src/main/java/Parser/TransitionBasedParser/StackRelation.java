package Parser.TransitionBasedParser;

import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

public class StackRelation {

    private final UniversalDependencyTreeBankWord word;
    private final UniversalDependencyRelation relation;

    public StackRelation(UniversalDependencyTreeBankWord word, UniversalDependencyRelation relation){
        this.word = word;
        this.relation = relation;
    }

    public StackRelation clone(){
        return new StackRelation(word.clone(), relation);
    }

    public UniversalDependencyTreeBankWord getWord() {
        return word;
    }

    public UniversalDependencyRelation getRelation() {
        return relation;
    }

}
