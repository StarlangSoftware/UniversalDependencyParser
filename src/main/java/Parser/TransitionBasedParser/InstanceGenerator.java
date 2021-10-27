package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 17.12.2020 */

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteAttribute;
import Classification.Instance.Instance;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.ArrayList;

public abstract class InstanceGenerator {

    public abstract Instance generate(State state, int windowSize, String command);

    protected void addFeatureAttributes(UniversalDependencyTreeBankWord word, ArrayList<Attribute> attributes) {
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