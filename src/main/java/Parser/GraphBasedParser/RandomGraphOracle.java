package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;

import java.util.Random;

public class RandomGraphOracle implements GraphOracle {
    @Override
    public double computeScore(UniversalDependencyTreeBankSentence sentence, int fromIndex, int toIndex) {
        Random random = new Random();
        return random.nextDouble();
    }
}
