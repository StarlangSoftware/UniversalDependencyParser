package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import Corpus.Sentence;
import DependencyParser.Universal.UniversalDependencyPosType;
import DependencyParser.Universal.UniversalDependencyRelation;
import DependencyParser.Universal.UniversalDependencyTreeBankFeatures;
import DependencyParser.Universal.UniversalDependencyTreeBankSentence;
import DependencyParser.Universal.UniversalDependencyTreeBankWord;

public class FileIO {
	public static UniversalDependencyTreeBankSentence readSentence(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName), StandardCharsets.UTF_16));
		Sentence sentence = new UniversalDependencyTreeBankSentence();
		
		String line = "";
		
		while((line = reader.readLine()) != null) {
			StringTokenizer tokenizer = new StringTokenizer(line);
			String[] fields = new String[10];
			int i = 0;

			// Read input file into fields[]
			while(tokenizer.hasMoreTokens()) {
				fields[i++] = tokenizer.nextToken();
			}
			
			if(fields[0] != null) {
				UniversalDependencyTreeBankWord word = new UniversalDependencyTreeBankWord(
						Integer.parseInt(fields[0]),
						fields[1], fields[2], UniversalDependencyPosType.valueOf(fields[3]),
						fields[4], new UniversalDependencyTreeBankFeatures(fields[5]), 
						new UniversalDependencyRelation(Integer.parseInt(fields[6]), fields[7]),
						fields[8], fields[9]);
				
				sentence.addWord(word);
			}
		}

		reader.close();
		return (UniversalDependencyTreeBankSentence) sentence;
	}
}
