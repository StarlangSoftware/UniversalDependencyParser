package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;

public interface GraphOracle {
    double computeScore(UniversalDependencyTreeBankSentence sentence, int fromIndex, int toIndex);
}
