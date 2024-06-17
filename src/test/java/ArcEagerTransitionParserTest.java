import Classification.DataSet.DataSet;
import Classification.Model.DecisionTree.DecisionTree;
import Classification.Parameter.C45Parameter;
import DependencyParser.ParserEvaluationScore;
import DependencyParser.Universal.UniversalDependencyTreeBankCorpus;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import Parser.TransitionBasedParser.ArcEagerOracle;
import Parser.TransitionBasedParser.ArcEagerTransitionParser;
import Parser.TransitionBasedParser.TransitionParser;
import org.junit.Assert;

public class ArcEagerTransitionParserTest {

    private void generateEagerModelC45(String language, String dataSetName, int windowSize){
        UniversalDependencyTreeBankCorpus corpus = new UniversalDependencyTreeBankCorpus(language + "_" + dataSetName + "-ud-train.conllu");
        TransitionParser transitionParser = new ArcEagerTransitionParser();
        DataSet dataSet = transitionParser.simulateParseOnCorpus(corpus, windowSize);
        DecisionTree c45 = new DecisionTree();
        c45.train(dataSet.getInstanceList(), new C45Parameter(1, true, 0.2));
        c45.saveTxt(language + "_" + dataSetName + "_eager_c45_" + windowSize + ".txt");
    }

    public void test(){
        generateEagerModelC45("en", "atis", 2);
        generateEagerModelC45("tr", "atis", 2);
        generateEagerModelC45("tr", "penn", 2);
        generateEagerModelC45("tr", "framenet", 2);
        generateEagerModelC45("tr", "kenet", 2);
        generateEagerModelC45("tr", "tourism", 2);
        generateEagerModelC45("tr", "boun", 2);
        generateEagerModelC45("en", "atis", 3);
        generateEagerModelC45("tr", "atis", 3);
        generateEagerModelC45("tr", "penn", 3);
        generateEagerModelC45("tr", "framenet", 3);
        generateEagerModelC45("tr", "kenet", 3);
        generateEagerModelC45("tr", "tourism", 3);
        generateEagerModelC45("tr", "boun", 3);
    }

    public void testOracle() {
        ParserEvaluationScore scores = new ParserEvaluationScore();
        TransitionParser transitionParser = new ArcEagerTransitionParser();
        UniversalDependencyTreeBankCorpus corpus = new UniversalDependencyTreeBankCorpus("tr_boun-ud-test.conllu");
        DecisionTree c45 = new DecisionTree();
        int windowSize = 3;
        c45.loadModel("models/tr_boun_eager_c45_" + windowSize + ".txt");
        for (int i = 0; i < corpus.sentenceCount(); i++) {
            UniversalDependencyTreeBankSentence actual = (UniversalDependencyTreeBankSentence) corpus.getSentence(i);
            UniversalDependencyTreeBankSentence expected = transitionParser.dependencyParse(actual, new ArcEagerOracle(c45, windowSize));
            scores.add(actual.compareParses(expected));
        }
        Assert.assertEquals(64.89458453906572, 100 * scores.getLS(), 0.01);
        Assert.assertEquals(47.110376188507644, 100 * scores.getLAS(), 0.01);
        Assert.assertEquals(54.91525423728817, 100 * scores.getUAS(), 0.01);
    }

}
