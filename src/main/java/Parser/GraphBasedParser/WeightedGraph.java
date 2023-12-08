package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class WeightedGraph {

    public final HashMap<UniversalDependencyTreeBankWord, ArrayList<SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>>>> edgeList;

    public WeightedGraph() {
        edgeList = new HashMap<>();
    }

    public void clear() {
        edgeList.clear();
    }

    public boolean containsKey(UniversalDependencyTreeBankWord word) {
        return edgeList.containsKey(word);
    }

    private boolean containsConnection(UniversalDependencyTreeBankWord from, UniversalDependencyTreeBankWord to) {
        for (int i = 0; i < edgeList.get(from).size(); i++) {
            if (edgeList.get(from).get(i).getKey().equals(to)) {
                return true;
            }
        }
        return false;
    }

    public void remove(UniversalDependencyTreeBankWord word) {
        edgeList.remove(word);
    }

    public void remove(UniversalDependencyTreeBankWord word, int index) {
        edgeList.get(word).remove(index);
    }

    public Set<UniversalDependencyTreeBankWord> getKeySet() {
        return edgeList.keySet();
    }

    public ArrayList<SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>>> get(UniversalDependencyTreeBankWord word) {
        return edgeList.get(word);
    }

    public SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>> get(UniversalDependencyTreeBankWord word, int index) {
        return edgeList.get(word).get(index);
    }

    public void addDirectedEdge(UniversalDependencyTreeBankWord from, UniversalDependencyTreeBankWord to, SimpleEntry<Double, Integer> entry) {
        if (!edgeList.containsKey(from)) {
            edgeList.put(from, new ArrayList<>());
        }
        if (!containsConnection(from, to)) {
            edgeList.get(from).add(new SimpleEntry<>(to, entry));
        }
    }

    public void addDirectedEdge(UniversalDependencyTreeBankWord word, SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>> entry) {
        if (!edgeList.containsKey(word)) {
            edgeList.put(word, new ArrayList<>());
        }
        edgeList.get(word).add(entry);
    }

    public void setWeight(UniversalDependencyTreeBankWord word, int index, double newWeight) {
        edgeList.get(word).get(index).setValue(new SimpleEntry<>(newWeight, edgeList.get(word).get(index).getValue().getValue()));
    }

    @Override
    public Object clone() {
        WeightedGraph graph = new WeightedGraph();
        for (UniversalDependencyTreeBankWord word : edgeList.keySet()) {
            for (int i = 0; i < edgeList.get(word).size(); i++) {
                graph.addDirectedEdge(word.clone(), edgeList.get(word).get(i).getKey().clone(), edgeList.get(word).get(i).getValue());
            }
        }
        return graph;
    }
}
