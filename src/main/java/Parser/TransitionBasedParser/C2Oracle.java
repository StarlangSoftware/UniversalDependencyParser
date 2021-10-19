package Parser.TransitionBasedParser;

import Classification.Attribute.Attribute;
import Classification.Attribute.DiscreteAttribute;
import Classification.Instance.Instance;
import Classification.Model.Model;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;
import DependencyParser.Universal.UniversalDependencyType;

import java.util.ArrayList;
import java.util.HashMap;

public class C2Oracle implements Oracle {

    private final Model model1;
    private final Model model2;

    public C2Oracle(Model model1, Model model2) {
        this.model1 = model1;
        this.model2 = model2;
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

    private ArrayList<Attribute> setAttributes(State state) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
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
        for (int i = 0; i < 2; i++) {
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
        return attributes;
    }

    private String findClassInfo(HashMap<String, Double> probabilities, State state) {
        double bestValue = 0.0;
        String best = "";
        for (String key : probabilities.keySet()) {
            if (probabilities.get(key) > bestValue) {
                if (key.equals("SHIFT")) {
                    if (state.wordListSize() > 0) {
                        best = key;
                        bestValue = probabilities.get(key);
                    }
                } else if (state.stackSize() > 1) {
                    best = key;
                    bestValue = probabilities.get(key);
                }
            }
        }
        return best;
    }

    @Override
    public Decision makeDecision(State state, TransitionSystem transitionSystem) {
        ArrayList<Attribute> attributes = setAttributes(state);
        String classInfo = findClassInfo(model1.predictProbability(new Instance("", attributes)), state);
        if (classInfo.equals("SHIFT")) {
            return new Decision(Command.SHIFT, null, 0.0);
        }
        attributes.add(new DiscreteAttribute(classInfo));
        String relationInfo = model2.predict(new Instance("", attributes));
        return new Decision(Command.valueOf(classInfo), UniversalDependencyType.valueOf(relationInfo.replaceAll(":", "_")), 0.0);
    }

    @Override
    public ArrayList<Decision> scoreDecisions(State state, TransitionSystem transitionSystem) {
        return null;
    }
}
