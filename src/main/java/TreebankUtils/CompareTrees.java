package TreebankUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import DependencyParser.Universal.UniversalDependencyTreeBankSentence;

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
		ArrayList<UniversalDependencyTreeBankSentence> sentences = new ArrayList<>();
		
		Scanner scanner = new Scanner(new File(fileName));
		
		StringBuilder sentence = new StringBuilder();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			if(line.isEmpty()) {
				sentences.add(new UniversalDependencyTreeBankSentence(lang, sentence.toString()));
				sentence = new StringBuilder();
			}else {
				sentence.append(System.lineSeparator()).append(line);
			}
		}
		return sentences;
	}
	
	public static void main(String[] args) throws IOException {
		CompareTrees compare = new CompareTrees();
		compare.loadCorpora();
		
		
		Tree enTree = new Tree(compare.enCorpus.get(0));
		Tree trTree = new Tree(compare.trCorpus.get(0));
		
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
