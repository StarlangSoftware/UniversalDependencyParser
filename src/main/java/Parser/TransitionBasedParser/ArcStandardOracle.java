package Parser.TransitionBasedParser;

import Classification.Instance.Instance;
import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;
import java.util.HashMap;

public class ArcStandardOracle extends Oracle {

    public ArcStandardOracle(Model model, int windowSize) {
        super(model, windowSize);
    }

    private String[] findClassInfo(HashMap<String, Double> probabilities, State state) {
        String best = findStandardClassInfo(probabilities, state);
        String[] decision = new String[2];
        decision[0] = best.substring(0, best.indexOf('('));
        decision[1] = best.substring(best.indexOf('(') + 1, best.indexOf(')'));
        return decision;
    }

    @Override
    public Decision makeDecision(State state) {
        InstanceGenerator instanceGenerator = new SimpleInstanceGenerator();
        Instance instance = instanceGenerator.generate(state, this.windowSize, "");
        String[] classInfo = findClassInfo(commandModel.predictProbability(instance), state);
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
