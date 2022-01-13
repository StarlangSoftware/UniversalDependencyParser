package Parser.TransitionBasedParser;

import Classification.Instance.Instance;
import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;
import java.util.HashMap;

public class ArcStandardStepWiseOracle extends ArcStandardCombinedOracle {

    protected final Model relationModel;

    public ArcStandardStepWiseOracle(Model commandModel, Model relationModel) {
        super(commandModel);
        this.relationModel = relationModel;
    }

    protected String findClassInfo(HashMap<String, Double> probabilities, State state) {
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

    @Override
    public Decision makeDecision(State state, TransitionSystem transitionSystem) {
        InstanceGenerator instanceGenerator = new SimpleInstanceGenerator();
        Instance instance = instanceGenerator.generate(state, 2, "");
        String classInfo = findClassInfo(commandModel.predictProbability(instance), state);
        if (classInfo.equals("SHIFT")) {
            return new Decision(Command.SHIFT, null, 0.0);
        }
        instance.addAttribute(classInfo);
        String relationInfo = relationModel.predict(instance);
        return new Decision(Command.valueOf(classInfo), UniversalDependencyType.valueOf(relationInfo.replaceAll(":", "_")), 0.0);
    }

    @Override
    public ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
        return null;
    }
}
