package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;

public class BasicGraphOracle implements GraphOracle {
    @Override
    public double findLength(UniversalDependencyTreeBankSentence sentence, int fromIndex, int toIndex) {
        return 0;
    }
}
