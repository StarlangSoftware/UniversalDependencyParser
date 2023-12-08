package Parser.GraphBasedParser;

import gnu.trove.TLinkedList;

public class FeatureVector extends TLinkedList{
	
	public FeatureVector() {
		
	}
	
	public FeatureVector(int[] keys) {
		for(int i = 0; i < keys.length; i++) {
			add(new Feature(keys[i], 1.0));
		}
	}
	
	public void add(int key, double value) {
		add(new Feature(key, value));
	}
	
	public void update() {
		
	}
	

}
