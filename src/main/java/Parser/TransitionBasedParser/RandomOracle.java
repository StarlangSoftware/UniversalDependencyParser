package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 5.12.2020 */

import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;
import java.util.Random;

public class RandomOracle extends Oracle {

    public RandomOracle(Model model, int windowSize) {
        super(model, windowSize);
    }

    @Override
    public Decision makeDecision(State state) {
        Random random = new Random();
        int command = 4;
        TransitionSystem transitionSystem = TransitionSystem.ARC_EAGER;
        switch (transitionSystem){
            case ARC_EAGER:
                command = random.nextInt(4);
                break;
            case ARC_STANDARD:
                command = random.nextInt(3);
                break;
        }
        int relation = random.nextInt(UniversalDependencyType.values().length);
        switch (command) {
            case 0:
                return new Decision(Command.LEFTARC, UniversalDependencyType.values()[relation], 0);
            case 1:
                return new Decision(Command.RIGHTARC, UniversalDependencyType.values()[relation], 0);
            case 2:
                return new Decision(Command.SHIFT, UniversalDependencyType.DEP, 0);
            case 3:
                return new Decision(Command.REDUCE, UniversalDependencyType.DEP, 0);
        }
        return null;
    }

    @Override
    public ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
        return null;
    }
}
