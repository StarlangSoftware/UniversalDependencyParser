package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;

public interface GraphOracle {

    /**
     * @param sentence is a {@link UniversalDependencyTreeBankSentence}.
     * @param fromIndex start index of the edge.
     * @param toIndex end index of the edge.
     * @return a {@link Double} value.
     **/

    double findLength(UniversalDependencyTreeBankSentence sentence, int fromIndex, int toIndex);
}
