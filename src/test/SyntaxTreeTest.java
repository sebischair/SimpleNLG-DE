package test;

import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

public class SyntaxTreeTest {

    private Lexicon lexicon;
    private NLGFactory nlgFactory;
    private Realiser realiser;

    public SyntaxTreeTest(Lexicon lexicon, NLGFactory nlgFactory, Realiser realiser) {
        this.lexicon = lexicon;
        this.nlgFactory = nlgFactory;
        this.realiser = realiser;
    }

    public void buildSyntaxTree() {
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("Otto");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("fegen");
        sentence.setSubject(subject);
        sentence.setVerb(verb);
        sentence.setObject("das auto");
        sentence.addComplement("und tanzt");
        System.out.println(realiser.realiseSentence(sentence));
    }
}
