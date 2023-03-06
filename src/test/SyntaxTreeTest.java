import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

public class SyntaxTreeTest {

    private Lexicon lexicon;
    private NLGFactory nlgFactory;
    private Realiser realiser;

    @BeforeEach
    public void setup() {
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
