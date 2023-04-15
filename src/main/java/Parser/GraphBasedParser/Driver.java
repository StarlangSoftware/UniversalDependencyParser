package Parser.GraphBasedParser;

import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws IOException {
    	ArcFactoredGraphOracle oracle = new ArcFactoredGraphOracle();
    	
    	oracle.train("C:\\test");
    	
    }
}
