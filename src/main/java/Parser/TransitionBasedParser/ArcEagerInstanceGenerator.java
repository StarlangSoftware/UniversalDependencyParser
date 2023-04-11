package Parser.TransitionBasedParser;

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteAttribute;
import Classification.Instance.Instance;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.ArrayList;

public class ArcEagerInstanceGenerator extends InstanceGenerator {

    private boolean suitable(UniversalDependencyTreeBankWord word) {
        try {
            word.getRelation().toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Instance generate(State state, int windowSize, String command) {
        Instance instance = new Instance(command);
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < windowSize; i++) {
            UniversalDependencyTreeBankWord word = state.getStackWord(i);
            if (word == null) {
                for (int j = 0; j < 15; j++) {
                    attributes.add(new DiscreteAttribute("null"));
                }
            } else {
                if (word.getName().equals("root")) {
                    attributes.add(new DiscreteAttribute("root"));
                    for (int j = 0; j < 14; j++) {
                        attributes.add(new DiscreteAttribute("null"));
                    }
                } else {
                    attributes.add(new DiscreteAttribute(word.getUpos().toString()));
                    attributes.add(new DiscreteAttribute(word.getXpos()));
                    attributes.add(new DiscreteAttribute(word.getMisc()));
                    addFeatureAttributes(word, attributes);
                    if (suitable(word)) {
                        attributes.add(new DiscreteAttribute(word.getRelation().toString()));
                    } else {
                        attributes.add(new DiscreteAttribute("null"));
                    }
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
}
