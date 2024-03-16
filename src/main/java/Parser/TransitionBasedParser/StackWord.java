package Parser.TransitionBasedParser;

import DependencyParser.Universal.UniversalDependencyTreeBankWord;

public class StackWord {
    private final UniversalDependencyTreeBankWord word;
    private final int toWord;

    public StackWord(){
        word = new UniversalDependencyTreeBankWord();
        toWord = 0;
    }

    public StackWord clone(){
        return new StackWord(word.clone(), toWord);
    }
    public StackWord(UniversalDependencyTreeBankWord word, int toWord){
        this.word = word;
        this.toWord = toWord;
    }

    public UniversalDependencyTreeBankWord getWord() {
        return word;
    }

    public int getToWord() {
        return toWord;
    }

}
