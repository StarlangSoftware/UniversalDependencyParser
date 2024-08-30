package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.UniversalDependencyTreeBankWord;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class WeightedGraph {

    public final HashMap<UniversalDependencyTreeBankWord, ArrayList<SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>>>> edgeList;

    /** A map storing the edges of the graph. Each node maps to a list of edges where each edge is represented
    * by a destination node and a weight-entry pair.
    */

    public WeightedGraph() {
        edgeList = new HashMap<>();
    }

    /**
     * Clears all the edges from the graph.
     */

    public void clear() {
        edgeList.clear();
    }

    /**
     * Checks if the graph contains a given node.
     * @param word The node to check for.
     * @return true if the graph contains the node, false otherwise.
     */

    public boolean containsKey(UniversalDependencyTreeBankWord word) {
        return edgeList.containsKey(word);
    }

    /**
     * Checks if there is a connection (edge) from one node to another.
     * @param from The starting node.
     * @param to   The destination node.
     * @return true if there is an edge from 'from' to 'to', false otherwise.
     */

    private boolean containsConnection(UniversalDependencyTreeBankWord from, UniversalDependencyTreeBankWord to) {
        for (int i = 0; i < edgeList.get(from).size(); i++) {
            if (edgeList.get(from).get(i).getKey().equals(to)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a node and all its edges from the graph.
     * @param word The node to remove.
     */

    public void remove(UniversalDependencyTreeBankWord word) {
        edgeList.remove(word);
    }

    /**
     * Removes a specific edge from a given node.
     * @param word  The node from which to remove the edge.
     * @param index The index of the edge to remove.
     */

    public void remove(UniversalDependencyTreeBankWord word, int index) {
        edgeList.get(word).remove(index);
    }

    /**
     * Retrieves the set of nodes in the graph.
     * @return A set of all nodes in the graph.
     */

    public Set<UniversalDependencyTreeBankWord> getKeySet() {
        return edgeList.keySet();
    }

    /**
     * Retrieves the list of edges for a given node.
     * @param word The node whose edges to retrieve.
     * @return A list of edges where each edge is represented by a destination node and a weight-entry pair.
     */

    public ArrayList<SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>>> get(UniversalDependencyTreeBankWord word) {
        return edgeList.get(word);
    }

    /**
     * Retrieves a specific edge from a given node.
     * @param word  The node from which to retrieve the edge.
     * @param index The index of the edge to retrieve.
     * @return The edge at the specified index, represented by a destination node and a weight-entry pair.
     */

    public SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>> get(UniversalDependencyTreeBankWord word, int index) {
        return edgeList.get(word).get(index);
    }

    /**
     * Adds a directed edge from one node to another with a specific weight and index.
     * @param from  The starting node.
     * @param to    The destination node.
     * @param entry A pair consisting of the edge's weight and its number.
     */

    public void addDirectedEdge(UniversalDependencyTreeBankWord from, UniversalDependencyTreeBankWord to, SimpleEntry<Double, Integer> entry) {
        if (!edgeList.containsKey(from)) {
            edgeList.put(from, new ArrayList<>());
        }
        if (!containsConnection(from, to)) {
            edgeList.get(from).add(new SimpleEntry<>(to, entry));
        }
    }

    /**
     * Adds a directed edge from a given node to another, represented by a SimpleEntry.
     * @param word  The starting node.
     * @param entry A SimpleEntry representing the destination node and the weight-entry pair.
     */

    public void addDirectedEdge(UniversalDependencyTreeBankWord word, SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>> entry) {
        if (!edgeList.containsKey(word)) {
            edgeList.put(word, new ArrayList<>());
        }
        edgeList.get(word).add(entry);
    }

    /**
     * Sets a new weight for a specific edge of a given node.
     * @param word      The node whose edge weight to update.
     * @param index     The index of the edge to update.
     * @param newWeight The new weight for the edge.
     */

    public void setWeight(UniversalDependencyTreeBankWord word, int index, double newWeight) {
        edgeList.get(word).get(index).setValue(new SimpleEntry<>(newWeight, edgeList.get(word).get(index).getValue().getValue()));
    }

    /**
     * Creates a clone of the current WeightedGraph instance.
     * @return A new {@link WeightedGraph} instance that is a copy of the current graph.
     */

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
