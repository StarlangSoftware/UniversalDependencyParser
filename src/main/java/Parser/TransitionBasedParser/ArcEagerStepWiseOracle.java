package Parser.TransitionBasedParser;
import Classification.Instance.Instance;
import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.HashMap;

public class ArcEagerStepWiseOracle extends ArcStandardStepWiseOracle {

    public ArcEagerStepWiseOracle(Model model1, Model model2, int windowSize) {
        super(model1, model2, windowSize);
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
    public Decision makeDecision(State state) {
        InstanceGenerator instanceGenerator = new ArcEagerInstanceGenerator();
        Instance instance = instanceGenerator.generate(state, this.windowSize, "");
        String classInfo = findClassInfo(commandModel.predictProbability(instance), state);
        if (classInfo.equals("SHIFT")) {
            return new Decision(Command.SHIFT, null, 0.0);
        } else if (classInfo.equals("REDUCE")) {
            return new Decision(Command.REDUCE, null, 0.0);
        }
        instance.addAttribute(classInfo);
        String relationInfo = relationModel.predict(instance);
        return new Decision(Command.valueOf(classInfo), UniversalDependencyType.valueOf(relationInfo.replaceAll(":", "_")), 0.0);
    }
}
