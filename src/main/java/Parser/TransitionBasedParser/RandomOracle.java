package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 5.12.2020 */

import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;
import java.util.Random;

public class RandomOracle extends Oracle {

    public RandomOracle(Model model, int windowSize) {
        super(model, windowSize);
    }

    /**
     * Makes a random decision based on a uniform distribution over possible actions.
     * @param state The current state of the parser.
     * @return A Decision object representing the randomly chosen action.
     */

    @Override
    public Decision makeDecision(State state) {
        Random random = new Random();
        int command = random.nextInt(3);
        int relation = random.nextInt(UniversalDependencyRelation.universalDependencyTags.length);
        switch (command) {
            case 0:
                return new Decision(Command.LEFTARC, UniversalDependencyRelation.universalDependencyTags[relation], 0);
            case 1:
                return new Decision(Command.RIGHTARC, UniversalDependencyRelation.universalDependencyTags[relation], 0);
            case 2:
                return new Decision(Command.SHIFT, UniversalDependencyType.DEP, 0);
        }
        return null;
    }

    @Override
    public ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
        return null;
    }
}
