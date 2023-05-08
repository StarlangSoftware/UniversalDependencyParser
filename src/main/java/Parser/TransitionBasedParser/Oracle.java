package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 5.12.2020 */

import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Oracle {

    protected final Model commandModel;
    protected final int windowSize;

    public Oracle(Model model, int windowSize) {
        this.commandModel = model;
        this.windowSize = windowSize;
    }

    protected abstract Decision makeDecision(State state);
    protected abstract ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem);

    protected String findBestValidEagerClassInfo(HashMap<String, Double> probabilities, State state) {
        double bestValue = 0.0;
        String best = "";
        for (String key : probabilities.keySet()) {
            if (probabilities.get(key) > bestValue) {
                if (key.equals("SHIFT") || key.equals("RIGHTARC")) {
                    if (state.wordListSize() > 0) {
                        best = key;
                        bestValue = probabilities.get(key);
                    }
                } else if (state.stackSize() > 1) {
                    if (!(key.equals("REDUCE") && state.getPeek().getRelation() == null)) {
                        best = key;
                        bestValue = probabilities.get(key);
                    }
                }
            }
        }
        return best;
    }

    protected String findBestValidStandardClassInfo(HashMap<String, Double> probabilities, State state) {
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

    protected Candidate getDecisionCandidate(String best) {
        String command, relation;
        UniversalDependencyType type;
        if (best.contains("(")){
            command = best.substring(0, best.indexOf('('));
            relation = best.substring(best.indexOf('(') + 1, best.indexOf(')'));
            type = UniversalDependencyRelation.getDependencyTag(relation);
        } else {
            command = best;
            type = UniversalDependencyType.DEP;
        }
        switch (command){
            case "SHIFT":
                return new Candidate(Command.SHIFT, type);
            case "REDUCE":
                return new Candidate(Command.REDUCE, type);
            case "LEFTARC":
                return new Candidate(Command.LEFTARC, type);
            case "RIGHTARC":
                return new Candidate(Command.RIGHTARC, type);
        }
        return null;
    }

}
