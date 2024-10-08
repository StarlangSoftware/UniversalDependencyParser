package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;

import java.util.Random;

public class RandomGraphOracle implements GraphOracle {

    /**
     * sets the edge to a value between 0 and 1.
     * @param sentence is a {@link UniversalDependencyTreeBankSentence}.
     * @param fromIndex start index of the edge.
     * @param toIndex end index of the edge.
     * @return a {@link Double} random value.
     **/

    @Override
    public double findLength(UniversalDependencyTreeBankSentence sentence, int fromIndex, int toIndex) {
        Random random = new Random();
        return random.nextDouble();
    }
}
