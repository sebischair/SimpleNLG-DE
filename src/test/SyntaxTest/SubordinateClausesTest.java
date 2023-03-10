package SyntaxTest;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

public class SubordinateClausesTest {
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
    public void testTemporalSubordinates() {
        //System.out.println("\n---------------------------- Test temporal subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("die sonne");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("scheinen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("es");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("regnen");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "während");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Die Sonne scheint, während es regnet.", output1);
    }

    @Test
    public void testCausalSubordinates() {
        //System.out.println("\n---------------------------- Test causal subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("die sonne");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("scheinen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("es sommer");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "weil");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Die Sonne scheint, weil es Sommer ist.", output1);
    }

    @Test
    public void testConditionalSubordinates() {
        //System.out.println("\n---------------------------- Test conditional subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("die sonne");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("scheinen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("du");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");
        verb2.addPreModifier("brav");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "wenn");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Die Sonne scheint, wenn du brav bist.", output1);
    }

    @Test
    public void testConsecutiveSubordinates() {
        //System.out.println("\n---------------------------- Test consecutive subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("die sonne");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("scheinen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("alle");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("kommen");
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("ins Schwitzen");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setObject(object2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "sodass");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Die Sonne scheint, sodass alle ins Schwitzen kommen.", output1);
    }

    @Test
    public void testConcessiveSubordinates() {
        //System.out.println("\n---------------------------- Test conecssive subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("die sonne");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("scheinen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("es");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("regnen");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "obwohl");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Die Sonne scheint, obwohl es regnet.", output1);
    }

    @Test
    public void testModalSubordinates() {
        //System.out.println("\n---------------------------- Test modal subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("die sonne");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("scheinen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("betreiben");
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("Kernfusion");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setObject(object2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "indem");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Die Sonne scheint, indem sie Kernfusion betreibt.", output1);
    }

    @Test
    public void testComparativeSubordinates() {
        //System.out.println("\n---------------------------- Test comparative subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("die sonne");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("scheinen");
        AdvPhraseSpec adverb1 = nlgFactory.createAdverbPhrase("hell");
        adverb1.setFeature(Feature.IS_COMPARATIVE, true);
        verb1.addModifier(adverb1);

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("die Lampe");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("brennen");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "als");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Die Sonne scheint heller, als die Lampe brennt.", output1);
    }

    @Test
    public void testFinalSubordinates() {
        //System.out.println("\n---------------------------- Test final subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("die sonne");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("scheinen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("wir");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("frieren");
        verb2.addPreModifier("nicht");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "damit");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Die Sonne scheint, damit wir nicht frieren.", output1);
    }

    @Test
    public void testAdversativeSubordinates() {
        //System.out.println("\n---------------------------- Test adversative subordinate clauses ---------------------------\n");
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
        sentence2.setFeature(Feature.COMPLEMENTISER, "wohingegen");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Der Hund bellt, wohingegen die Katze miaut.", output1);
    }

    @Test
    public void testRelativeSubordinates() {
        //System.out.println("\n---------------------------- Test relativ subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("der hund");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("sein");
        verb1.addPostModifier("dort drüben");
        sentence1.setVerb(verb1);
        sentence1.setSubject(subject1);

        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("bellen");
        verb2.addPreModifier("der");
        verb2.setFeature(Feature.APPOSITIVE, true);
        subject1.addPostModifier(verb2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Der Hund, der bellt, ist dort drüben.", output1);
    }

    @Test
    public void testDassSubordinates() {
        //System.out.println("\n---------------------------- Test dass subordinate clauses ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("er");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("sagen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("er");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("kommen");
        verb2.addPreModifier("morgen");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "dass");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Er sagt, dass er morgen kommt.", output1);
    }

    @Test
    public void testSubordinatesSepVerb() {
        //System.out.println("\n---------------------------- Test subordinate clauses with separable verb ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("Florian");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("einkaufen");

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("Alex");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("aufräumen");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.COMPLEMENTISER, "während");

        sentence1.addComplement(sentence2);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Florian kauft ein, während Alex aufräumt.", output1);

        sentence1.setFeature(Feature.TENSE, Tense.PAST);
        sentence2.setFeature(Feature.TENSE, Tense.PAST);
        String output2 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Florian kaufte ein, während Alex aufräumte.", output2);
    }
}
