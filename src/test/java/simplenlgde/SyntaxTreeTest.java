package simplenlgde;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.phrasespec.*;

public class SyntaxTreeTest {
    private static Lexicon lexicon;
    private static NLGFactory nlgFactory;
    private static Realiser realiser;

    @BeforeAll
    public static void setup() {
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }

    @Test
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
