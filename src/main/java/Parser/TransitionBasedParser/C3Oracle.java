package Parser.TransitionBasedParser;
import Classification.Instance.Instance;
import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.HashMap;

public class C3Oracle extends C2Oracle {

    public C3Oracle(Model model1, Model model2) {
        super(model1, model2);
    }

    protected String findClassInfo(HashMap<String, Double> probabilities, State state) {
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

    @Override
    public Decision makeDecision(State state, TransitionSystem transitionSystem) {
        InstanceGenerator instanceGenerator = new ArcEagerInstanceGenerator();
        Instance instance = instanceGenerator.generate(state, 2, "");
        String classInfo = findClassInfo(model1.predictProbability(instance), state);
        if (classInfo.equals("SHIFT")) {
            return new Decision(Command.SHIFT, null, 0.0);
        } else if (classInfo.equals("REDUCE")) {
            return new Decision(Command.REDUCE, null, 0.0);
        }
        instance.addAttribute(classInfo);
        String relationInfo = model2.predict(instance);
        return new Decision(Command.valueOf(classInfo), UniversalDependencyType.valueOf(relationInfo.replaceAll(":", "_")), 0.0);
    }
}
