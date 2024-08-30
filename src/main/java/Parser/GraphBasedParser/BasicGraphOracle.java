package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;

public class BasicGraphOracle implements GraphOracle {

    /**
     * sets the value 0 to the edge.
     * @param sentence is a {@link UniversalDependencyTreeBankSentence}.
     * @param fromIndex start index of the edge.
     * @param toIndex end index of the edge.
     * @return 0.
     **/

    @Override
    public double findLength(UniversalDependencyTreeBankSentence sentence, int fromIndex, int toIndex) {
        return 0;
    }
}
