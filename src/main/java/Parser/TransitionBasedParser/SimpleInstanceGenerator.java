package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 17.12.2020 */

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteAttribute;
import Classification.Instance.Instance;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.ArrayList;

public class SimpleInstanceGenerator implements InstanceGenerator {

    public SimpleInstanceGenerator() {
    }

    @Override
    public Instance generate(State state, int windowSize, String command) {
        Instance instance = new Instance(command);
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < windowSize; i++) {
            UniversalDependencyTreeBankWord word = state.getStackWord(i);
            if (word == null) {
                for (int j = 0; j < 14; j++) {
                    attributes.add(new DiscreteAttribute("null"));
                }
            } else {
                if (word.getName().equals("root")) {
                    attributes.add(new DiscreteAttribute("root"));
                    for (int j = 0; j < 13; j++) {
                        attributes.add(new DiscreteAttribute("null"));
                    }
                } else {
                    attributes.add(new DiscreteAttribute(word.getUpos().toString()));
                    attributes.add(new DiscreteAttribute(word.getXpos()));
                    attributes.add(new DiscreteAttribute(word.getMisc()));
                    addFeatureAttributes(word, attributes);
                }
            }
        }
        for (int i = 0; i < windowSize; i++) {
            UniversalDependencyTreeBankWord word = state.getWordListWord(i);
            if (word != null) {
                attributes.add(new DiscreteAttribute(word.getUpos().toString()));
                attributes.add(new DiscreteAttribute(word.getXpos()));
                attributes.add(new DiscreteAttribute(word.getMisc()));
                addFeatureAttributes(word, attributes);
            } else {
                for (int j = 0; j < 14; j++) {
                    attributes.add(new DiscreteAttribute("null"));
                }
            }
        }
        for (Attribute attribute : attributes) {
            instance.addAttribute(attribute);
        }
        return instance;
    }

    private void addFeatureAttributes(UniversalDependencyTreeBankWord word, ArrayList<Attribute> attributes) {
        String feature = word.getFeatureValue("NumType");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("Number");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("Case");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("VerbForm");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("Mood");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("Tense");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("Aspect");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("Voice");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("Evident");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("Polarity");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
        feature = word.getFeatureValue("Person");
        if (feature != null) {
            attributes.add(new DiscreteAttribute(feature));
        } else {
            attributes.add(new DiscreteAttribute("null"));
        }
    }
}