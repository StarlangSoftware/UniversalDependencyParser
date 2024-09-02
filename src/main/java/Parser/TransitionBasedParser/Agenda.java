package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 23.12.2020 */

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Agenda {

    private final ConcurrentHashMap<State, Double> agenda;
    private final int beamSize;

    public Agenda(int beamSize) {
        agenda = new ConcurrentHashMap<>();
        this.beamSize = beamSize;
    }

    /**
     * Retrieves the set of states currently in the agenda.
     * @return A set of states that are currently in the agenda.
     */

    public Set<State> getKeySet() {
        return agenda.keySet();
    }

    /**
     * Updates the agenda with a new state if it is better than the worst state
     * currently in the agenda or if there is room in the agenda.
     * @param oracle The ScoringOracle used to score the state.
     * @param current The state to be added to the agenda.
     */

    public void updateAgenda(ScoringOracle oracle, State current) {
        if (agenda.containsKey(current)) {
            return;
        }
        double point = oracle.score(current);
        if (agenda.size() < beamSize) {
            agenda.put(current, point);
        } else {
            State worst = null;
            double worstValue = Integer.MAX_VALUE;
            for (State key : agenda.keySet()) {
                if (agenda.get(key) < worstValue) {
                    worstValue = agenda.get(key);
                    worst = key;
                }
            }
            if (point > worstValue) {
                agenda.remove(worst);
                agenda.put(current, point);
            }
        }
    }

    /**
     * Retrieves the best state from the agenda based on the highest score.
     * @return The state with the highest score in the agenda.
     */

    public State best() {
        State best = null;
        double bestValue = Integer.MIN_VALUE;
        for (State key : agenda.keySet()) {
            if (agenda.get(key) > bestValue) {
                bestValue = agenda.get(key);
                best = key;
            }
        }
        return best;
    }
}
