package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 5.12.2020 */

import Classification.Model.Model;

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

    protected String[] divideClassInfo(String best) {
        String[] decision = new String[2];
        if (best.contains("(")){
            decision[0] = best.substring(0, best.indexOf('('));
            decision[1] = best.substring(best.indexOf('(') + 1, best.indexOf(')'));
        } else {
            decision[0] = best;
        }
        return decision;
    }

}
