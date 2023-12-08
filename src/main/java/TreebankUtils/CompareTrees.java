package TreebankUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

public class CompareTrees {

	public ArrayList<UniversalDependencyTreeBankSentence> enCorpus;
	public ArrayList<UniversalDependencyTreeBankSentence> trCorpus;
	
	public CompareTrees() {
		
	}
	
	public void loadCorpora() throws IOException {
		enCorpus = loadCorpus("C:\\Source\\TurkishDependencyParser\\src\\main\\resources\\en_atis-ud-train.conllu", "en");
		trCorpus = loadCorpus("C:\\Source\\TurkishDependencyParser\\src\\main\\resources\\tr_atis-ud-train.conllu", "tr");
		
	}
	
	private ArrayList<UniversalDependencyTreeBankSentence> loadCorpus(String fileName, String lang) throws IOException {
		ArrayList<UniversalDependencyTreeBankSentence> sentences = new ArrayList<UniversalDependencyTreeBankSentence>();
		
		Scanner scanner = new Scanner(new File(fileName));
		
		StringBuilder sentence = new StringBuilder();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			if(line.isEmpty()) {
				sentences.add(new UniversalDependencyTreeBankSentence(lang, sentence.toString()));
				sentence = new StringBuilder();
			}else {
				sentence.append(System.lineSeparator() + line);
			}
		}
		return sentences;
	}
	
	public Tree buildTree(UniversalDependencyTreeBankSentence sentence) {
		Tree tree = new Tree();
		
		for(int i = 0; i < sentence.wordCount(); i++) {
			UniversalDependencyTreeBankWord word = (UniversalDependencyTreeBankWord) sentence.getWord(i);
			Node node = new Node(word);
			tree.addNode(node);
			
			if(word.getRelation().to() > 0) {
				UniversalDependencyTreeBankWord toWord = (UniversalDependencyTreeBankWord) sentence.getWord(word.getRelation().to() - 1);
				Node toNode = new Node(toWord);
				Edge edge = new Edge(node, toNode, word.getRelation().toString());
				tree.addEdge(edge);
			}
		}
		
		return tree;
	}

	public static void main(String args[]) throws IOException {
		CompareTrees compare = new CompareTrees();
		compare.loadCorpora();
		
		
		Tree enTree = compare.buildTree(compare.enCorpus.get(0));
		Tree trTree = compare.buildTree(compare.trCorpus.get(0));
		
		enTree.getDifference(trTree);
		
//		ArrayList<Set<UniversalDependencyRelation>> enSents = new ArrayList<Set<UniversalDependencyRelation>>();
//		ArrayList<Set<UniversalDependencyRelation>> trSents = new ArrayList<Set<UniversalDependencyRelation>>();
//		
//		for(int i = 0; i < compare.enCorpus.size(); i++) {
//			HashSet<UniversalDependencyRelation> enRels = new HashSet<UniversalDependencyRelation>();
//			HashSet<UniversalDependencyRelation> trRels = new HashSet<UniversalDependencyRelation>();
//			
//			for(int j = 0; j < compare.enCorpus.get(i).wordCount(); j++) {
//				UniversalDependencyTreeBankWord enWord = (UniversalDependencyTreeBankWord) compare.enCorpus.get(i).getWord(j);
//				enRels.add(enWord.getRelation());
//			}
//			
//			for(int j = 0; j < compare.trCorpus.get(i).wordCount(); j++) {		
//				UniversalDependencyTreeBankWord trWord = (UniversalDependencyTreeBankWord) compare.trCorpus.get(i).getWord(j);
//				trRels.add(trWord.getRelation());
//			}
//			
//			enSents.add(enRels);
//			trSents.add(trRels);
//		}
//		
//		System.out.println(enSents.get(0));
//		System.out.println(trSents.get(0));
	}
}
