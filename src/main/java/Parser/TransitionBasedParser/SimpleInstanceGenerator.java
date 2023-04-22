package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 17.12.2020 */

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteIndexedAttribute;
import Classification.Instance.Instance;
import DependencyParser.Universal.UniversalDependencyTreeBankFeatures;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.ArrayList;

public class SimpleInstanceGenerator extends InstanceGenerator {

    public SimpleInstanceGenerator() {
    }

    @Override
    public Instance generate(State state, int windowSize, String command) {
        Instance instance = new Instance(command);
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < windowSize; i++) {
            UniversalDependencyTreeBankWord word = state.getStackWord(i);
            if (word == null) {
                attributes.add(new DiscreteIndexedAttribute("null", 0, 18));
                addEmptyAttributes(attributes);
            } else {
                if (word.getName().equals("root")) {
                    attributes.add(new DiscreteIndexedAttribute("root", 0, 18));
                    addEmptyAttributes(attributes);
                } else {
                    attributes.add(new DiscreteIndexedAttribute(word.getUpos().toString(), UniversalDependencyTreeBankFeatures.posIndex(word.getUpos().toString()) + 1, 18));
                    addFeatureAttributes(word, attributes);
                }
            }
        }
        for (int i = 0; i < windowSize; i++) {
            UniversalDependencyTreeBankWord word = state.getWordListWord(i);
            if (word != null) {
                attributes.add(new DiscreteIndexedAttribute(word.getUpos().toString(), UniversalDependencyTreeBankFeatures.posIndex(word.getUpos().toString()) + 1, 18));
                addFeatureAttributes(word, attributes);
            } else {
                attributes.add(new DiscreteIndexedAttribute("root", 0, 18));
                addEmptyAttributes(attributes);
            }
        }
        for (Attribute attribute : attributes) {
            instance.addAttribute(attribute);
        }
        return instance;
    }
}