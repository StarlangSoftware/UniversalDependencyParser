package Parser.TransitionBasedParser;

import Classification.Classifier.C45;
import Classification.DataSet.DataSet;
import Classification.Parameter.C45Parameter;
import DependencyParser.ParserEvaluationScore;
import DependencyParser.Universal.UniversalDependencyTreeBankCorpus;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import org.junit.Assert;
import org.junit.Test;

public class ArcStandardTransitionParserTest {

    private void generateStandardModelC45(String language, String dataSetName, int windowSize){
        UniversalDependencyTreeBankCorpus corpus = new UniversalDependencyTreeBankCorpus(language + "_" + dataSetName + "-ud-train.conllu");
        TransitionParser transitionParser = new ArcStandardTransitionParser();
        DataSet dataSet = transitionParser.simulateParse(corpus, windowSize);
        C45 c45 = new C45();
        c45.train(dataSet.getInstanceList(), new C45Parameter(1, true, 0.2));
        c45.getModel().saveTxt(language + "_" + dataSetName + "_standard_c45_" + windowSize + ".txt");
    }

    @Test
    public void test(){
        generateStandardModelC45("tr", "boun", 2);
    }

    @Test
    public void testOracle() {
        ParserEvaluationScore scores = new ParserEvaluationScore();
        TransitionParser transitionParser = new ArcStandardTransitionParser();
        UniversalDependencyTreeBankCorpus corpus = new UniversalDependencyTreeBankCorpus("tr_boun-ud-test.conllu");
        C45 c45 = new C45();
        int windowSize = 3;
        c45.loadModel("models/tr_boun_standard_c45_" + windowSize + ".txt");
        for (int i = 0; i < corpus.sentenceCount(); i++) {
            UniversalDependencyTreeBankSentence actual = (UniversalDependencyTreeBankSentence) corpus.getSentence(i);
            UniversalDependencyTreeBankSentence expected = transitionParser.dependencyParse(actual, new ArcStandardOracle(c45.getModel(), windowSize));
            scores.add(actual.compareParses(expected));
        }
        Assert.assertEquals(60.11575031004547, 100 * scores.getLS(), 0.01);
        Assert.assertEquals(42.97643654402647, 100 * scores.getLAS(), 0.01);
        Assert.assertEquals(51.27738735014466, 100 * scores.getUAS(), 0.01);
    }
}
