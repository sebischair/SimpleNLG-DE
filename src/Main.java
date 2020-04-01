import test.*;

import java.io.IOException;

import simplenlgde.framework.NLGFactory;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;

public class Main {

	public static void main(String[] args) throws IOException {		
	    Lexicon lexicon;
	    NLGFactory nlgFactory;
	    Realiser realiser;
	    
	    lexicon = Lexicon.getDefaultLexicon();
	    nlgFactory = new NLGFactory(lexicon);
	    realiser = new Realiser(lexicon);
	}
}