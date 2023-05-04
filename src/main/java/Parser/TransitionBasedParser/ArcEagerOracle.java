package Parser.TransitionBasedParser;

import Classification.Instance.Instance;
import Classification.Model.DecisionTree.DecisionTree;
import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;

public class ArcEagerOracle extends Oracle{

    public ArcEagerOracle(Model model, int windowSize) {
        super(model, windowSize);
    }

    @Override
    protected Decision makeDecision(State state) {
        String best;
        InstanceGenerator instanceGenerator = new SimpleInstanceGenerator();
        Instance instance = instanceGenerator.generate(state, this.windowSize, "");
        if (commandModel instanceof DecisionTree){
            best = commandModel.predict(instance);
        } else {
            best = findBestValidEagerClassInfo(commandModel.predictProbability(instance), state);
        }
        String[] classInfo = divideClassInfo(best);
        if (classInfo[0].equals("SHIFT")) {
            return new Decision(Command.SHIFT, null, 0.0);
        } else if (classInfo[0].equals("REDUCE")) {
            return new Decision(Command.REDUCE, null, 0.0);
        }
        return new Decision(Command.valueOf(classInfo[0]), UniversalDependencyType.valueOf(classInfo[1].replaceAll(":", "_")), 0.0);
    }

    @Override
    protected ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
        return null;
    }
}
