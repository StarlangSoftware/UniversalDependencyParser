package Parser.TransitionBasedParser;

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteIndexedAttribute;
import Classification.Instance.Instance;
import DependencyParser.Universal.UniversalDependencyTreeBankFeatures;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.ArrayList;

public class ArcEagerInstanceGenerator extends InstanceGenerator {

    /**
     * Checks if the given word has a valid relation.
     * @param word The UniversalDependencyTreeBankWord to check.
     * @return true if the relation is valid, false otherwise.
     */

    private boolean suitable(UniversalDependencyTreeBankWord word) {
        try {
            word.getRelation().toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Generates an Instance object based on the provided state, window size, and command.
     * The Instance is populated with attributes derived from the words in the state.
     * @param state The state used to generate the instance.
     * @param windowSize The size of the window used to extract words from the state.
     * @param command The command associated with the instance.
     * @return The generated Instance object.
     */

    @Override
    public Instance generate(State state, int windowSize, String command) {
        Instance instance = new Instance(command);
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < windowSize; i++) {
            UniversalDependencyTreeBankWord word = state.getStackWord(i);
            if (word == null) {
                attributes.add(new DiscreteIndexedAttribute("null", 0, 18));
                addEmptyAttributes(attributes);
                attributes.add(new DiscreteIndexedAttribute("null", 0, 59));
            } else {
                if (word.getName().equals("root")) {
                    attributes.add(new DiscreteIndexedAttribute("root", 0, 18));
                    addEmptyAttributes(attributes);
                    attributes.add(new DiscreteIndexedAttribute("null", 0, 59));
                } else {
                    attributes.add(new DiscreteIndexedAttribute(word.getUpos().toString(), UniversalDependencyTreeBankFeatures.posIndex(word.getUpos().toString()) + 1, 18));
                    addFeatureAttributes(word, attributes);
                    if (suitable(word)) {
                        attributes.add(new DiscreteIndexedAttribute(word.getRelation().toString(), UniversalDependencyTreeBankFeatures.dependencyIndex(word.getRelation().toString()) + 1, 59));
                    } else {
                        attributes.add(new DiscreteIndexedAttribute("null", 0, 59));
                    }
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
