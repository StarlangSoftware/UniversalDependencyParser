import Classification.Classifier.C45;
import Classification.DataSet.DataSet;
import Classification.Parameter.C45Parameter;
import DependencyParser.ParserEvaluationScore;
import DependencyParser.Universal.UniversalDependencyTreeBankCorpus;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import Parser.TransitionBasedParser.ArcStandardOracle;
import Parser.TransitionBasedParser.ArcStandardTransitionParser;
import Parser.TransitionBasedParser.TransitionParser;
import org.junit.Assert;
import org.junit.Test;

public class ArcStandardTransitionParserTest {

    private void generateStandardModelC45(String language, String dataSetName, int windowSize){
        UniversalDependencyTreeBankCorpus corpus = new UniversalDependencyTreeBankCorpus(language + "_" + dataSetName + "-ud-train.conllu");
        TransitionParser transitionParser = new ArcStandardTransitionParser();
        DataSet dataSet = transitionParser.simulateParseOnCorpus(corpus, windowSize);
        C45 c45 = new C45();
        c45.train(dataSet.getInstanceList(), new C45Parameter(1, true, 0.2));
        c45.getModel().saveTxt(language + "_" + dataSetName + "_standard_c45_" + windowSize + ".txt");
    }

    @Test
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
        C45 c45 = new C45();
        int windowSize = 3;
        c45.loadModel("models/tr_atis_standard_c45_" + windowSize + ".txt");
        for (int i = 0; i < corpus.sentenceCount(); i++) {
            UniversalDependencyTreeBankSentence actual = (UniversalDependencyTreeBankSentence) corpus.getSentence(i);
            UniversalDependencyTreeBankSentence expected = transitionParser.dependencyParse(actual, new ArcStandardOracle(c45.getModel(), windowSize));
            scores.add(actual.compareParses(expected));
        }
        Assert.assertEquals(75.27529607313518, 100 * scores.getLS(), 0.01);
        Assert.assertEquals(66.86058591315187, 100 * scores.getLAS(), 0.01);
        Assert.assertEquals(74.25722002908796, 100 * scores.getUAS(), 0.01);
    }

}
