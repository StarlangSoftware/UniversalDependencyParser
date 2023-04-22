package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 17.12.2020 */

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteIndexedAttribute;
import Classification.Instance.Instance;
import DependencyParser.Universal.UniversalDependencyTreeBankFeatures;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.ArrayList;

public abstract class InstanceGenerator {

    public abstract Instance generate(State state, int windowSize, String command);

    private void addAttributeForFeatureType(UniversalDependencyTreeBankWord word, ArrayList<Attribute> attributes, String featureType){
        String feature = word.getFeatureValue(featureType);
        int numberOfValues = UniversalDependencyTreeBankFeatures.numberOfValues("tr", featureType) + 1;
        if (feature != null) {
            attributes.add(new DiscreteIndexedAttribute(feature, UniversalDependencyTreeBankFeatures.featureValueIndex("tr", featureType, feature) + 1, numberOfValues));
        } else {
            attributes.add(new DiscreteIndexedAttribute("null", 0, numberOfValues));
        }
    }

    protected void addEmptyAttributes(ArrayList<Attribute> attributes){
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "PronType") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "NumType") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Number") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Case") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Definite") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Degree") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "VerbForm") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Mood") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Tense") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Aspect") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Voice") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Evident") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Polarity") + 1));
        attributes.add(new DiscreteIndexedAttribute("null", 0, UniversalDependencyTreeBankFeatures.numberOfValues("tr", "Person") + 1));
    }

    protected void addFeatureAttributes(UniversalDependencyTreeBankWord word, ArrayList<Attribute> attributes) {
        addAttributeForFeatureType(word, attributes, "PronType");
        addAttributeForFeatureType(word, attributes, "NumType");
        addAttributeForFeatureType(word, attributes, "Number");
        addAttributeForFeatureType(word, attributes, "Case");
        addAttributeForFeatureType(word, attributes, "Definite");
        addAttributeForFeatureType(word, attributes, "Degree");
        addAttributeForFeatureType(word, attributes, "VerbForm");
        addAttributeForFeatureType(word, attributes, "Mood");
        addAttributeForFeatureType(word, attributes, "Tense");
        addAttributeForFeatureType(word, attributes, "Aspect");
        addAttributeForFeatureType(word, attributes, "Voice");
        addAttributeForFeatureType(word, attributes, "Evident");
        addAttributeForFeatureType(word, attributes, "Polarity");
        addAttributeForFeatureType(word, attributes, "Person");
    }
}