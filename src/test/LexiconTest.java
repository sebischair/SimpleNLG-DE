package test;


import org.junit.jupiter.api.Assertions;

import simplenlgde.framework.*;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;
import simplenlgde.lexicon.*;


public class LexiconTest {
	
    private Lexicon lexicon;
    private NLGFactory nlgFactory;
    private Realiser realiser;
    
    public LexiconTest() {
    	Lexicon lexicon1 = Lexicon.getDefaultLexicon();
        Lexicon lexicon2 = new XMLLexicon("./src/main/java/simplenlgger/lexicon/additional_lexicon.xml");
        this.lexicon = new MultipleLexicon(lexicon1, lexicon2);
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
	}

	public void textMultipleLexicons() {
		//test with a word which is only in 2nd lexicon
        NPPhraseSpec noun = nlgFactory.createNounPhrase("TestKompositum");
        noun.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        String output = realiser.realise(noun).toString();
        Assertions.assertEquals("TestKompositaaa", output);
    }
}
