package Parser.TransitionBasedParser;

import Classification.Instance.Instance;
import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class C1Oracle implements Oracle {

    protected final Model model1;

    public C1Oracle(Model model) {
        this.model1 = model;
    }

    private String[] findClassInfo(HashMap<String, Double> probabilities, State state) {
        String[] decision = new String[2];
        Arrays.fill(decision, "");
        double bestValue = 0.0;
        String best = "";
        for (String key : probabilities.keySet()) {
            if (probabilities.get(key) > bestValue) {
                if (key.contains("SHIFT")) {
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
        int index = Integer.MAX_VALUE - 1;
        for (int i = 0; i < best.length(); i++) {
            if (best.charAt(i) == '(') {
                index = i + 1;
                break;
            }
            decision[0] += best.charAt(i);
        }
        for (int i = index; i < best.length() - 1; i++) {
            decision[1] += best.charAt(i);
        }
        return decision;
    }

    @Override
    public Decision makeDecision(State state, TransitionSystem transitionSystem) {
        InstanceGenerator instanceGenerator = new SimpleInstanceGenerator();
        Instance instance = instanceGenerator.generate(state, 2, "");
        String[] classInfo = findClassInfo(model1.predictProbability(instance), state);
        if (classInfo[0].equals("SHIFT")) {
            return new Decision(Command.SHIFT, null, 0.0);
        }
        return new Decision(Command.valueOf(classInfo[0]), UniversalDependencyType.valueOf(classInfo[1].replaceAll(":", "_")), 0.0);
    }

    @Override
    public ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
        return null;
    }
}
