package simplenlgde.syntax;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

public class MainClausesTest {
    private static Lexicon lexicon;
    private static NLGFactory nlgFactory;
    private static Realiser realiser = null;

    @BeforeAll
    public static void setup() {
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }

    @Test
    public void testMainClauses() {
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("mary");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("machen");
        NPPhraseSpec object1_1 = nlgFactory.createNounPhrase("hausaufgaben");
        object1_1.setPlural(true);
        NPPhraseSpec object1_2 = nlgFactory.createNounPhrase("in der Küche");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);
        sentence1.setObject(object1_1);
        sentence1.setIndirectObject(object1_2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Mary macht Hausaufgaben in der Küche.", output1);
    }

    @Test
    public void testSeparableMainClauses() {
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("mary");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("abarbeiten");
        NPPhraseSpec object1_1 = nlgFactory.createNounPhrase("hausaufgaben");
        object1_1.setPlural(true);
        NPPhraseSpec object1_2 = nlgFactory.createNounPhrase("in der Küche");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);
        sentence1.setObject(object1_1);
        sentence1.setIndirectObject(object1_2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Mary arbeitet Hausaufgaben in der Küche ab.", output1);
    }

    @Test
    public void testCompoundMainClausesComma() {
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("der hund");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("bellen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("die katze");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("miauen");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);

        sentence1.addComplement(sentence2);

        String output = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Der Hund bellt, die Katze miaut.", output);
    }

    @Test
    public void testCompoundMainClausesUnd() {
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("der hund");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("bellen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("die katze");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("miauen");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "und");

        sentence1.addComplement(sentence2);

        String output = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Der Hund bellt und die Katze miaut.", output);
    }
}
