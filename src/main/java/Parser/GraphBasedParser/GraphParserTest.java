package Parser.GraphBasedParser;

import DependencyParser.Universal.UniversalDependencyPosType;
import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankFeatures;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;
import org.junit.Assert;
import org.junit.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/* Created by oguzkeremyildiz on 13.02.2021 */

public class GraphParserTest {

    @Test
    public void testChuLiuEdmonds() {
        GraphParser parser = new GraphParser();
        WeightedGraph graph = new WeightedGraph();
        UniversalDependencyTreeBankWord root = new UniversalDependencyTreeBankWord(-1, "root", "_", UniversalDependencyPosType.DET, "_", new UniversalDependencyTreeBankFeatures("tr", "_"), new UniversalDependencyRelation(0, "root"), "_", "_");
        UniversalDependencyTreeBankWord v1 = new UniversalDependencyTreeBankWord(1, "V1", "_", UniversalDependencyPosType.DET, "_", new UniversalDependencyTreeBankFeatures("tr", "_"), new UniversalDependencyRelation(1, "root"), "_", "_");
        UniversalDependencyTreeBankWord v2 = new UniversalDependencyTreeBankWord(2, "V2", "_", UniversalDependencyPosType.DET, "_", new UniversalDependencyTreeBankFeatures("tr", "_"), new UniversalDependencyRelation(2, "root"), "_", "_");
        UniversalDependencyTreeBankWord v3 = new UniversalDependencyTreeBankWord(3, "V3", "_", UniversalDependencyPosType.DET, "_", new UniversalDependencyTreeBankFeatures("tr", "_"), new UniversalDependencyRelation(3, "root"), "_", "_");
        graph.addDirectedEdge(root, v1, new SimpleEntry<>(5.0, 1));
        graph.addDirectedEdge(root, v2, new SimpleEntry<>(1.0, 2));
        graph.addDirectedEdge(root, v3, new SimpleEntry<>(1.0, 3));
        graph.addDirectedEdge(v1, v2, new SimpleEntry<>(11.0, 4));
        graph.addDirectedEdge(v2, v1, new SimpleEntry<>(10.0, 5));
        graph.addDirectedEdge(v3, v1, new SimpleEntry<>(9.0, 6));
        graph.addDirectedEdge(v1, v3, new SimpleEntry<>(4.0, 7));
        graph.addDirectedEdge(v2, v3, new SimpleEntry<>(5.0, 8));
        graph.addDirectedEdge(v3, v2, new SimpleEntry<>(8.0, 9));
        HashSet<Integer> expectedSet = new HashSet<>();
        expectedSet.add(1);
        expectedSet.add(4);
        expectedSet.add(8);
        ArrayList<Integer> bestInEdge = new ArrayList<>();
        parser.chuLiuEdmonds(graph, bestInEdge, new HashMap<>());
        HashSet<Integer> set = new HashSet<>(bestInEdge);
        Assert.assertEquals(expectedSet, set);
        graph.clear();
        expectedSet.clear();
        UniversalDependencyTreeBankWord word1 = new UniversalDependencyTreeBankWord(2, "word1", "_", UniversalDependencyPosType.DET, "_", new UniversalDependencyTreeBankFeatures("tr", "_"), new UniversalDependencyRelation(2, "root"), "_", "_");
        UniversalDependencyTreeBankWord word2 = new UniversalDependencyTreeBankWord(3, "word2", "_", UniversalDependencyPosType.DET, "_", new UniversalDependencyTreeBankFeatures("tr", "_"), new UniversalDependencyRelation(3, "root"), "_", "_");
        UniversalDependencyTreeBankWord word3 = new UniversalDependencyTreeBankWord(4, "word3", "_", UniversalDependencyPosType.DET, "_", new UniversalDependencyTreeBankFeatures("tr", "_"), new UniversalDependencyRelation(4, "root"), "_", "_");
        graph.addDirectedEdge(root, word1, new SimpleEntry<>(10.0, 1));
        graph.addDirectedEdge(root, word2, new SimpleEntry<>(4.0, 2));
        graph.addDirectedEdge(word1, word2, new SimpleEntry<>(10.0, 3));
        graph.addDirectedEdge(word2, word1, new SimpleEntry<>(12.0, 4));
        graph.addDirectedEdge(root, word3, new SimpleEntry<>(9.0, 5));
        graph.addDirectedEdge(word2, word3,  new SimpleEntry<>(7.0, 6));
        graph.addDirectedEdge(word3, word2, new SimpleEntry<>(6.0, 7));
        graph.addDirectedEdge(word1, word3, new SimpleEntry<>(3.0, 8));
        graph.addDirectedEdge(word3, word1, new SimpleEntry<>(2.0, 9));
        expectedSet.add(1);
        expectedSet.add(3);
        expectedSet.add(5);
        bestInEdge = new ArrayList<>();
        parser.chuLiuEdmonds(graph, bestInEdge, new HashMap<>());
        set = new HashSet<>(bestInEdge);
        Assert.assertEquals(expectedSet, set);
        graph.clear();
        expectedSet.clear();
        UniversalDependencyTreeBankWord word4 = new UniversalDependencyTreeBankWord(5, "word4", "_", UniversalDependencyPosType.DET, "_", new UniversalDependencyTreeBankFeatures("tr", "_"), new UniversalDependencyRelation(5, "root"), "_", "_");
        UniversalDependencyTreeBankWord word5 = new UniversalDependencyTreeBankWord(6, "word5", "_", UniversalDependencyPosType.DET, "_", new UniversalDependencyTreeBankFeatures("tr", "_"), new UniversalDependencyRelation(6, "root"), "_", "_");
        graph.addDirectedEdge(root, word1, new SimpleEntry<>(5.0, 1));
        graph.addDirectedEdge(root, word2, new SimpleEntry<>(7.0, 2));
        graph.addDirectedEdge(root, word3, new SimpleEntry<>(4.0, 3));
        graph.addDirectedEdge(word1, word2, new SimpleEntry<>(6.0, 6));
        graph.addDirectedEdge(word2, word1, new SimpleEntry<>(3.0, 5));
        graph.addDirectedEdge(word3, word2, new SimpleEntry<>(12.0, 4));
        graph.addDirectedEdge(word1, word4, new SimpleEntry<>(2.0, 7));
        graph.addDirectedEdge(word4, word1, new SimpleEntry<>(7.0, 11));
        graph.addDirectedEdge(word4, word2, new SimpleEntry<>(5.6, 10));
        graph.addDirectedEdge(word5, word4, new SimpleEntry<>(9.0, 8));
        graph.addDirectedEdge(word4, word5, new SimpleEntry<>(6.0, 9));
        graph.addDirectedEdge(word3, word5, new SimpleEntry<>(6.0, 13));
        graph.addDirectedEdge(word5, word3, new SimpleEntry<>(8.0, 12));
        expectedSet.add(3);
        expectedSet.add(4);
        expectedSet.add(8);
        expectedSet.add(11);
        expectedSet.add(13);
        bestInEdge = new ArrayList<>();
        parser.chuLiuEdmonds(graph, bestInEdge, new HashMap<>());
        set = new HashSet<>(bestInEdge);
        Assert.assertEquals(expectedSet, set);
        graph.clear();
        expectedSet.clear();
        graph.addDirectedEdge(root, v1, new SimpleEntry<>(1.0, 1));
        graph.addDirectedEdge(root, v2, new SimpleEntry<>(2.0, 5));
        graph.addDirectedEdge(root, v3, new SimpleEntry<>(1.0, 4));
        graph.addDirectedEdge(v1, v2, new SimpleEntry<>(12.7, 2));
        graph.addDirectedEdge(v2, v1, new SimpleEntry<>(8.0, 8));
        graph.addDirectedEdge(v2, v3, new SimpleEntry<>(2.0, 3));
        graph.addDirectedEdge(v3, v2, new SimpleEntry<>(16.0, 6));
        graph.addDirectedEdge(v3, v1, new SimpleEntry<>(3.0, 7));
        expectedSet.add(4);
        expectedSet.add(6);
        expectedSet.add(8);
        bestInEdge = new ArrayList<>();
        parser.chuLiuEdmonds(graph, bestInEdge, new HashMap<>());
        set = new HashSet<>(bestInEdge);
        Assert.assertEquals(expectedSet, set);
    }
}