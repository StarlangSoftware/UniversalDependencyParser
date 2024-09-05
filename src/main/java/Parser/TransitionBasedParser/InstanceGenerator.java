package Parser.TransitionBasedParser;/* Created by oguzkeremyildiz on 17.12.2020 */

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteIndexedAttribute;
import Classification.Instance.Instance;
import DependencyParser.Universal.UniversalDependencyTreeBankFeatures;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.ArrayList;

public abstract class InstanceGenerator {

    /**
     * Abstract method for generating an instance based on the current state, window size, and command.
     * @param state The current state of the parser.
     * @param windowSize The size of the window used for feature extraction.
     * @param command The command to be used for generating the instance.
     * @return The generated {@link Instance} object.
     */

    public abstract Instance generate(State state, int windowSize, String command);

    /**
     * Adds an attribute for a specific feature type of a given word to the list of attributes.
     * @param word The word whose feature value is used to create the attribute.
     * @param attributes The list of attributes to which the new attribute will be added.
     * @param featureType The type of the feature to be extracted from the word.
     */

    private void addAttributeForFeatureType(UniversalDependencyTreeBankWord word, ArrayList<Attribute> attributes, String featureType){
        String feature = word.getFeatureValue(featureType);
        int numberOfValues = UniversalDependencyTreeBankFeatures.numberOfValues("tr", featureType) + 1;
        if (feature != null) {
            attributes.add(new DiscreteIndexedAttribute(feature, UniversalDependencyTreeBankFeatures.featureValueIndex("tr", featureType, feature) + 1, numberOfValues));
        } else {
            attributes.add(new DiscreteIndexedAttribute("null", 0, numberOfValues));
        }
    }

    /**
     * Adds a set of default (empty) attributes to the list of attributes. These attributes represent
     * various feature types with default "null" values.
     * @param attributes The list of attributes to which the default attributes will be added.
     */

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

    /**
     * Adds attributes for various feature types of a given word to the list of attributes.
     * @param word The word whose feature values are used to create the attributes.
     * @param attributes The list of attributes to which the new attributes will be added.
     */

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