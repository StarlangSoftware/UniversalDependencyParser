package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 5.12.2020 */

import Classification.DataSet.DataSet;
import Classification.Instance.Instance;
import DependencyParser.Universal.*;

import java.util.*;

public abstract class TransitionParser {

    public TransitionParser() {
    }

    /**
     * Creates a new {@link UniversalDependencyTreeBankSentence} with the same words as the input sentence,
     * but with null heads, effectively cloning the sentence structure without dependencies.
     * @param universalDependencyTreeBankSentence the sentence to be cloned
     * @return a new {@link UniversalDependencyTreeBankSentence} with copied words but no dependencies
     */
    protected UniversalDependencyTreeBankSentence createResultSentence(UniversalDependencyTreeBankSentence universalDependencyTreeBankSentence) {
        UniversalDependencyTreeBankSentence sentence = new UniversalDependencyTreeBankSentence();
        for (int i = 0; i < universalDependencyTreeBankSentence.wordCount(); i++) {
            UniversalDependencyTreeBankWord word = (UniversalDependencyTreeBankWord) universalDependencyTreeBankSentence.getWord(i);
            sentence.addWord(new UniversalDependencyTreeBankWord(word.getId(), word.getName(), word.getLemma(), word.getUpos(), word.getXpos(), word.getFeatures(), null, word.getDeps(), word.getMisc()));
        }
        return sentence;
    }

    /**
     * Simulates parsing a corpus of sentences, returning a dataset of instances created by parsing each sentence.
     * @param corpus the corpus to be parsed
     * @param windowSize the size of the window used in parsing
     * @return a {@link DataSet} containing instances from parsing each sentence in the corpus
     */
    public DataSet simulateParseOnCorpus(UniversalDependencyTreeBankCorpus corpus, int windowSize) {
        DataSet dataSet = new DataSet();
        for (int i = 0; i < corpus.sentenceCount(); i++) {
            dataSet.addInstanceList(simulateParse((UniversalDependencyTreeBankSentence) corpus.getSentence(i), windowSize));
        }
        return dataSet;
    }

    /**
     * Parses a single sentence and returns a list of instances that represent the parsing process.
     * @param sentence the sentence to be parsed
     * @param windowSize the size of the window used in parsing
     * @return a list of {@link Instance} objects representing the parsing process
     */
    public abstract ArrayList<Instance> simulateParse(UniversalDependencyTreeBankSentence sentence, int windowSize);

    /**
     * Parses a single sentence using a specified oracle and returns the parsed sentence with dependencies.
     * @param universalDependencyTreeBankSentence the sentence to be parsed
     * @param oracle the oracle used for guiding the parsing process
     * @return a {@link UniversalDependencyTreeBankSentence} with dependencies parsed
     */
    public abstract UniversalDependencyTreeBankSentence dependencyParse(UniversalDependencyTreeBankSentence universalDependencyTreeBankSentence, Oracle oracle);

    /**
     * Checks if there are any states in the agenda that still have words to process or have more than one item in the stack.
     * @param agenda the agenda containing the states
     * @return true if there are states to process, false otherwise
     */
    private boolean checkStates(Agenda agenda) {
        for (State state : agenda.getKeySet()) {
            if (state.wordListSize() > 0 || state.stackSize() > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes the parsing state with a stack containing one empty {@link StackWord} and a word list containing all words in the sentence.
     * @param sentence the sentence to initialize the state with
     * @return a {@link State} representing the starting point for parsing
     */
    protected State initialState(UniversalDependencyTreeBankSentence sentence) {
        ArrayList<StackWord> wordList = new ArrayList<>();
        for (int i = 0; i < sentence.wordCount(); i++) {
            wordList.add(new StackWord((UniversalDependencyTreeBankWord) sentence.getWord(i), i + 1));
        }
        Stack<StackWord> stack = new Stack<>();
        stack.add(new StackWord());
        return new State(stack, wordList, new ArrayList<>());
    }

    /**
     * Constructs possible parsing candidates based on the current state and transition system.
     * @param transitionSystem the transition system used (ARC_STANDARD or ARC_EAGER)
     * @param state the current parsing state
     * @return a list of possible {@link Candidate} actions to be applied
     */
    private ArrayList<Candidate> constructCandidates(TransitionSystem transitionSystem, State state) {
        if (state.stackSize() == 1 && state.wordListSize() == 0) {
            return new ArrayList<>();
        }
        ArrayList<Candidate> subsets = new ArrayList<>();
        if (state.wordListSize() > 0) {
            subsets.add(new Candidate(Command.SHIFT, UniversalDependencyType.DEP));
        }
        if (transitionSystem == TransitionSystem.ARC_EAGER && state.stackSize() > 0) {
            subsets.add(new Candidate(Command.REDUCE, UniversalDependencyType.DEP));
        }
        for (int i = 0; i < UniversalDependencyRelation.universalDependencyTypes.length; i++) {
            UniversalDependencyType type = UniversalDependencyRelation.getDependencyTag(UniversalDependencyRelation.universalDependencyTypes[i]);
            if (transitionSystem == TransitionSystem.ARC_STANDARD && state.stackSize() > 1) {
                subsets.add(new Candidate(Command.LEFTARC, type));
                subsets.add(new Candidate(Command.RIGHTARC, type));
            } else if (transitionSystem == TransitionSystem.ARC_EAGER && state.stackSize() > 0 && state.wordListSize() > 0) {
                subsets.add(new Candidate(Command.LEFTARC, type));
                subsets.add(new Candidate(Command.RIGHTARC, type));
            }
        }
        return subsets;
    }

    /**
     * Performs dependency parsing with beam search to find the best parse for a given sentence.
     * @param oracle the scoring oracle used for guiding the search
     * @param beamSize the size of the beam for beam search
     * @param universalDependencyTreeBankSentence the sentence to be parsed
     * @param transitionSystem the transition system used (ARC_STANDARD or ARC_EAGER)
     * @return the best parsing state from the beam search
     */
    public State dependencyParseWithBeamSearch(ScoringOracle oracle, int beamSize, UniversalDependencyTreeBankSentence universalDependencyTreeBankSentence, TransitionSystem transitionSystem) {
        UniversalDependencyTreeBankSentence sentence = createResultSentence(universalDependencyTreeBankSentence);
        State initialState = initialState(sentence);
        Agenda agenda = new Agenda(beamSize);
        agenda.updateAgenda(oracle, (State) initialState.clone());
        while (checkStates(agenda)) {
            for (State state : agenda.getKeySet()) {
                ArrayList<Candidate> subsets = constructCandidates(transitionSystem, state);
                for (Candidate subset : subsets) {
                    Command command = subset.getCommand();
                    UniversalDependencyType type = subset.getUniversalDependencyType();
                    State cloneState = (State) state.clone();
                    cloneState.apply(command, type, transitionSystem);
                    agenda.updateAgenda(oracle,(State) cloneState.clone());
                }
            }
        }
        return agenda.best();
    }

    /**
     * Parses a corpus of sentences using the given oracle and returns a new corpus with the parsed sentences.
     * @param universalDependencyTreeBankCorpus the corpus to be parsed
     * @param oracle the oracle used for guiding the parsing process
     * @return a {@link UniversalDependencyTreeBankCorpus} containing the parsed sentences
     */
    public UniversalDependencyTreeBankCorpus dependencyParseCorpus(UniversalDependencyTreeBankCorpus universalDependencyTreeBankCorpus, Oracle oracle) {
        UniversalDependencyTreeBankCorpus corpus = new UniversalDependencyTreeBankCorpus();
        for (int i = 0; i < universalDependencyTreeBankCorpus.sentenceCount(); i++) {
            UniversalDependencyTreeBankSentence sentence = (UniversalDependencyTreeBankSentence) universalDependencyTreeBankCorpus.getSentence(i);
            corpus.addSentence(dependencyParse(sentence, oracle));
        }
        return corpus;
    }
}
