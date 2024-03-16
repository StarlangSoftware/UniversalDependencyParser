package Parser.GraphBasedParser;

import gnu.trove.TLinkableAdaptor;

public class Feature extends TLinkableAdaptor{
	private final int index;
	private final double value;
	
	public Feature(int i, double v) {
		index = i;
		value = v;
	}
	
	public final Feature clone() {
		return new Feature(index, value);
	}
	
	public final Feature negate() {
		return new Feature(index, -value);
	}

	public final String toString() {
		return index + " = " + value;
	}
}
