package Parser.TransitionBasedParser;

import Classification.Model.Model;
import DependencyParser.ParserEvaluationScore;
import DependencyParser.Universal.UniversalDependencyTreeBankCorpus;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import Util.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;

public class ArcEagerTransitionParserTest {

    private static Model loadModel(String fileName) {
        ObjectInputStream outObject;
        Model model = null;
        try {
            outObject = new ObjectInputStream(FileUtils.getInputStream(fileName));
            model = (Model) outObject.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return model;
    }

}
