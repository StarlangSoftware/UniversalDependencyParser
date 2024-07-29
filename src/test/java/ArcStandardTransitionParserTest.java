import Classification.DataSet.DataSet;
import Classification.Model.DecisionTree.DecisionTree;
import Classification.Parameter.C45Parameter;
import DependencyParser.ParserEvaluationScore;
import DependencyParser.Universal.UniversalDependencyTreeBankCorpus;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import Parser.TransitionBasedParser.ArcStandardCombinedOracle;
import Parser.TransitionBasedParser.ArcStandardTransitionParser;
import Parser.TransitionBasedParser.TransitionParser;
import org.junit.Assert;
import org.junit.Test;

public class ArcStandardTransitionParserTest {

    private void generateStandardModelC45(String language, String dataSetName, int windowSize){
        UniversalDependencyTreeBankCorpus corpus = new UniversalDependencyTreeBankCorpus(language + "_" + dataSetName + "-ud-train.conllu");
        TransitionParser transitionParser = new ArcStandardTransitionParser();
        DataSet dataSet = transitionParser.simulateParseOnCorpus(corpus, windowSize);
        DecisionTree c45 = new DecisionTree();
        c45.train(dataSet.getInstanceList(), new C45Parameter(1, true, 0.2));
        c45.saveTxt(language + "_" + dataSetName + "_standard_c45_" + windowSize + ".txt");
    }

    public void testc45(){
        generateStandardModelC45("en", "atis", 2);
        generateStandardModelC45("tr", "atis", 2);
        generateStandardModelC45("tr", "penn", 2);
        generateStandardModelC45("tr", "framenet", 2);
        generateStandardModelC45("tr", "kenet", 2);
        generateStandardModelC45("tr", "tourism", 2);
        generateStandardModelC45("tr", "boun", 2);
        generateStandardModelC45("en", "atis", 3);
        generateStandardModelC45("tr", "atis", 3);
        generateStandardModelC45("tr", "penn", 3);
        generateStandardModelC45("tr", "framenet", 3);
        generateStandardModelC45("tr", "kenet", 3);
        generateStandardModelC45("tr", "tourism", 3);
        generateStandardModelC45("tr", "boun", 3);
    }

    @Test
    public void testOracle() {
        ParserEvaluationScore scores = new ParserEvaluationScore();
        TransitionParser transitionParser = new ArcStandardTransitionParser();
        UniversalDependencyTreeBankCorpus corpus = new UniversalDependencyTreeBankCorpus("tr_atis-ud-test.conllu");
        DecisionTree c45 = new DecisionTree();
        int windowSize = 3;
        c45.loadModel("models/tr_atis_standard_c45_" + windowSize + ".txt");
        for (int i = 0; i < corpus.sentenceCount(); i++) {
            UniversalDependencyTreeBankSentence actual = (UniversalDependencyTreeBankSentence) corpus.getSentence(i);
            UniversalDependencyTreeBankSentence expected = transitionParser.dependencyParse(actual, new ArcStandardCombinedOracle(c45, windowSize));
            scores.add(actual.compareParses(expected));
        }
    }

}
