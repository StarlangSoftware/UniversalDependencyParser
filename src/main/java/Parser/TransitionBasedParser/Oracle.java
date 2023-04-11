package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 5.12.2020 */

import Classification.Model.Model;

import java.util.ArrayList;

public abstract class Oracle {

    protected final Model commandModel;
    protected final int windowSize;

    public Oracle(Model model, int windowSize) {
        this.commandModel = model;
        this.windowSize = windowSize;
    }

    protected abstract Decision makeDecision(State state);
    protected abstract ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem);
}
