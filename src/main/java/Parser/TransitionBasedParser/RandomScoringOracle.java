package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 27.12.2020 */

import java.util.Random;

public class RandomScoringOracle implements ScoringOracle {

    @Override
    public double score(State state) {
        Random random = new Random();
        return random.nextDouble();
    }
}
