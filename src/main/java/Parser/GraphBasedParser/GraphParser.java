package Parser.GraphBasedParser;/* Created by oguzkeremyildiz on 11.02.2021 */

import DependencyParser.Universal.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GraphParser {

    public GraphParser() {
    }

    private WeightedGraph generateGraph(UniversalDependencyTreeBankSentence sentence, GraphOracle oracle, UniversalDependencyTreeBankWord root) {
        WeightedGraph graph = new WeightedGraph();
        for (int i = 0; i < sentence.wordCount(); i++) {
            double score = oracle.computeScore(sentence, -1, i);
            graph.addDirectedEdge(root, (UniversalDependencyTreeBankWord) sentence.getWord(i), new SimpleEntry<>(score, -1 * (i + 1)));
            for (int j = 0; j < sentence.wordCount(); j++) {
                if (i != j) {
                    score = oracle.computeScore(sentence, i, j);
                    graph.addDirectedEdge((UniversalDependencyTreeBankWord) sentence.getWord(i),(UniversalDependencyTreeBankWord) sentence.getWord(j), new SimpleEntry<>(score, (i * sentence.wordCount()) + j + 1));
                }
            }
        }
        return graph;
    }

    private boolean isBestConnection(SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>> entry, ArrayList<SimpleEntry<Connection, Integer>> connections) {
        double listBest = Integer.MIN_VALUE;
        int index = -1;
        if (connections.isEmpty()) {
            return true;
        } else {
            for (int i = 0; i < connections.size(); i++) {
                if (connections.get(i).getKey().getTo().equals(entry.getKey()) && listBest < connections.get(i).getKey().getScore()) {
                    listBest = connections.get(i).getKey().getScore();
                    index = i;
                    break;
                }
            }
        }
        if (entry.getValue().getKey() > listBest) {
            if (index > -1) {
                connections.remove(index);
            }
            return true;
        }
        return false;
    }

    private ArrayList<SimpleEntry<Connection, Integer>> findConnections(WeightedGraph graph) {
        ArrayList<SimpleEntry<Connection, Integer>> connections = new ArrayList<>();
        for (UniversalDependencyTreeBankWord word : graph.getKeySet()) {
            for (int i = 0; i < graph.get(word).size(); i++) {
                SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>> entry = graph.get(word).get(i);
                if (isBestConnection(entry, connections)) {
                    connections.add(new SimpleEntry<>(new Connection(word, entry.getKey(), entry.getValue().getKey()), entry.getValue().getValue()));
                }
            }
        }
        return connections;
    }

    private boolean cycleContains(UniversalDependencyTreeBankWord word, ArrayList<SimpleEntry<Connection, Integer>> cycle) {
        for (SimpleEntry<Connection, Integer> entry : cycle) {
            if (entry.getKey().getFrom() == word || entry.getKey().getTo() == word) {
                return true;
            }
        }
        return false;
    }

    private SimpleEntry<Integer, Boolean> containsConnection(ArrayList<SimpleEntry<Connection, Integer>> temporaryConnections, Connection current) {
        for (SimpleEntry<Connection, Integer> temporaryConnection : temporaryConnections) {
            if (temporaryConnection.getKey().equals(current)) {
                return new SimpleEntry<>(temporaryConnection.getValue(), true);
            }
        }
        return new SimpleEntry<>(Integer.MIN_VALUE, false);
    }

    private boolean findCycle(WeightedGraph graph, ArrayList<SimpleEntry<Connection, Integer>> temporaryConnections, ArrayList<SimpleEntry<Connection, Integer>> cycle, UniversalDependencyTreeBankWord currentWord) {
        for (int i = 0; i < graph.get(currentWord).size(); i++) {
            Connection current = new Connection(currentWord, graph.get(currentWord).get(i).getKey(), graph.get(currentWord).get(i).getValue().getKey());
            SimpleEntry<Integer, Boolean> entry = containsConnection(temporaryConnections, current);
            if (entry.getValue()) {
                if (cycleContains(current.getTo(), cycle)) {
                    cycle.add(new SimpleEntry<>(current, entry.getKey()));
                    return true;
                } else {
                    cycle.add(new SimpleEntry<>(current, entry.getKey()));
                    if (graph.containsKey(graph.get(currentWord, i).getKey()) && findCycle(graph, temporaryConnections, cycle, graph.get(currentWord).get(i).getKey())) {
                        return true;
                    }
                    cycle.remove(cycle.size() - 1);
                }
            }
        }
        return false;
    }

    private void expandGraph(WeightedGraph graph, WeightedGraph clone, ArrayList<Integer> bestInEdge, HashMap<Integer, ArrayList<Integer>> kicksOut) {
        for (int i = bestInEdge.size() - 1; i > -1; i--) {
            if (kicksOut.containsKey(bestInEdge.get(i))) {
                for (int j = 0; j < bestInEdge.size(); j++) {
                    if (kicksOut.get(bestInEdge.get(i)).contains(bestInEdge.get(j))) {
                        bestInEdge.set(j, bestInEdge.get(i));
                    }
                }
                kicksOut.remove(bestInEdge.get(i));
                break;
            }
        }
        graph.clear();
        for (UniversalDependencyTreeBankWord word : clone.getKeySet()) {
            for (int i = 0; i < clone.get(word).size(); i++) {
                graph.addDirectedEdge(word, clone.get(word, i).getKey(), clone.get(word, i).getValue());
            }
        }
    }

    private void contractGraph(WeightedGraph graph, ArrayList<Integer> bestInEdge, HashMap<Integer, ArrayList<Integer>> kicksOut, ArrayList<SimpleEntry<Connection, Integer>> cycle) {
        for (SimpleEntry<Connection, Integer> entry : cycle) {
            bestInEdge.add(entry.getValue());
        }
        HashMap<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>> map = new HashMap<>();
        for (SimpleEntry<Connection, Integer> entry : cycle) {
            map.put(entry.getKey().getTo(), new SimpleEntry<>(entry.getKey().getScore(), entry.getValue()));
        }
        for (UniversalDependencyTreeBankWord word : graph.getKeySet()) {
            if (!map.containsKey(word)) {
                for (int i = 0; i < graph.get(word).size(); i++) {
                    if (map.containsKey(graph.get(word, i).getKey())) {
                        if (!kicksOut.containsKey(graph.get(word, i).getValue().getValue())) {
                            kicksOut.put(graph.get(word, i).getValue().getValue(), new ArrayList<>());
                        }
                        kicksOut.get(graph.get(word, i).getValue().getValue()).add(map.get(graph.get(word, i).getKey()).getValue());
                        graph.setWeight(word, i, graph.get(word, i).getValue().getKey() - map.get(graph.get(word, i).getKey()).getKey());
                    }
                }
            }
        }
        String name = "";
        for (UniversalDependencyTreeBankWord word : map.keySet()) {
            name += "-" + word.getName();
        }
        name += "-";
        UniversalDependencyTreeBankWord node = new UniversalDependencyTreeBankWord(Integer.MIN_VALUE, name, "_", UniversalDependencyPosType.DET,"_", new UniversalDependencyTreeBankFeatures("_"), new UniversalDependencyRelation(Integer.MAX_VALUE, "root"),"_","_");
        for (UniversalDependencyTreeBankWord word : map.keySet()) {
            for (int i = 0; i < graph.get(word).size(); i++) {
                if (!cycleContains(graph.get(word, i).getKey(), cycle)) {
                    graph.addDirectedEdge(node, graph.get(word, i));
                }
            }
            graph.remove(word);
        }
        Set<UniversalDependencyTreeBankWord> keySet = graph.getKeySet();
        for (UniversalDependencyTreeBankWord word : keySet) {
            ArrayList<SimpleEntry<UniversalDependencyTreeBankWord, SimpleEntry<Double, Integer>>> list = graph.get(word);
            for (int i = 0; i < list.size(); i++) {
                if (map.containsKey(graph.get(word, i).getKey())) {
                    SimpleEntry<Double, Integer> entry = graph.get(word, i).getValue();
                    graph.remove(word, i);
                    graph.get(word).add(i, new SimpleEntry<>(node, entry));
                }
            }
        }
    }

    public void chuLiuEdmonds(WeightedGraph graph, ArrayList<Integer> bestInEdge, HashMap<Integer, ArrayList<Integer>> kicksOut) {
        ArrayList<SimpleEntry<Connection, Integer>> temporaryConnections = findConnections(graph);
        ArrayList<SimpleEntry<Connection, Integer>> cycle = new ArrayList<>();
        boolean isCycle = false;
        for (int i = 0; i < temporaryConnections.size(); i++) {
            cycle.add(temporaryConnections.get(i));
            if (graph.containsKey(temporaryConnections.get(i).getKey().getTo())) {
                isCycle = findCycle(graph, temporaryConnections, cycle, temporaryConnections.get(i).getKey().getTo());
                if (isCycle) {
                    break;
                }
            }
            cycle.clear();
        }
        if (!isCycle) {
            for (SimpleEntry<Connection, Integer> temporaryConnection : temporaryConnections) {
                bestInEdge.add(temporaryConnection.getValue());
            }
        } else {
            WeightedGraph clone = (WeightedGraph) graph.clone();
            contractGraph(graph, bestInEdge, kicksOut, cycle);
            chuLiuEdmonds(graph, bestInEdge, kicksOut);
            expandGraph(graph, clone, bestInEdge, kicksOut);
        }
    }

    public ArrayList<Connection> findMaximumSpanningTree(UniversalDependencyTreeBankSentence sentence, GraphSystem system) {
        GraphOracle oracle;
        if (system.equals(GraphSystem.RANDOM_ORACLE)) {
            oracle = new RandomGraphOracle();
        } else {
            oracle = new ArcFactoredGraphOracle();
        }
        ArrayList<Connection> list = new ArrayList<>();
        UniversalDependencyTreeBankWord root = new UniversalDependencyTreeBankWord(0, "root", "", UniversalDependencyPosType.DET, "", new UniversalDependencyTreeBankFeatures("_"), null, "", "");
        WeightedGraph graph = generateGraph(sentence, oracle, root);
        ArrayList<Integer> bestInEdge = new ArrayList<>();
        HashMap<Integer, ArrayList<Integer>> kicksOut = new HashMap<>();
        chuLiuEdmonds(graph, bestInEdge, kicksOut);
        HashSet<Integer> set = new HashSet<>(bestInEdge);
        for (UniversalDependencyTreeBankWord word : graph.getKeySet()) {
            for (int i = 0; i < graph.get(word).size(); i++) {
                if (set.contains(graph.get(word).get(i).getValue().getValue())) {
                    list.add(new Connection(word, graph.get(word).get(i).getKey(), graph.get(word).get(i).getValue().getKey()));
                }
            }
        }
        return list;
    }
}
