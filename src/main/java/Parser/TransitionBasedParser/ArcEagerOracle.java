package Parser.TransitionBasedParser;

import Classification.Instance.Instance;
import Classification.Model.Model;

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
        best = findBestValidEagerClassInfo(commandModel.predictProbability(instance), state);
        Candidate decisionCandidate = getDecisionCandidate(best);
        if (decisionCandidate.getCommand().equals(Command.SHIFT)) {
            return new Decision(Command.SHIFT, null, 0.0);
        } else if (decisionCandidate.getCommand().equals(Command.REDUCE)) {
            return new Decision(Command.REDUCE, null, 0.0);
        }
        return new Decision(decisionCandidate.getCommand(), decisionCandidate.getUniversalDependencyType(), 0.0);
    }

    @Override
    protected ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
        return null;
    }
}
