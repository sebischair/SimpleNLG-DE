package MorphologyTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

import org.junit.jupiter.api.Assertions;

public class AdjectiveInflectionTest {

    private Lexicon lexicon;
    private NLGFactory nlgFactory;
    private Realiser realiser = null;

    int sentenceCounter = 0;

    String[] regularAdjectives = {"gut", "schwer", "alt", "leicht", "nett"};

    String[] irregularAdjectives = {"leise", "nahe", "müde", "weise", "dunkel", "flexibel", "irreparabel", "teuer",
            "makaber", "hoch"};

    String[] articleNouns = {"der Hund", "die Frau", "das Auto", "ein Haus", "eine Katze"};

    DiscourseFunction[] cases = {DiscourseFunction.SUBJECT, DiscourseFunction.GENITIVE, DiscourseFunction.OBJECT,
            DiscourseFunction.INDIRECT_OBJECT};

    String[] adj_conj_special = {"ein", "derselbe", "dieser", "jener", "jeder", "mancher", "solcher", "welcher"};


    //correct forms
    String[] adj_conj_special_correct = {"Ein guter Schüler.", "Derselbe gute Schüler.", "Dieser gute Schüler.",
            "Jener gute Schüler.", "Jeder gute Schüler.", "Mancher gute Schüler.", "Solcher gute Schüler.",
            "Welcher gute Schüler."};


    @BeforeEach
    public void setup() {
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }

    @Test
    private void doAdjectiveInflection(String[] adjectives, Boolean comp) {
        for (String adjective : adjectives) {
            for (String articleNoun : articleNouns) {
                for (DiscourseFunction disc : cases) {
                    NPPhraseSpec noun = nlgFactory.createNounPhrase(articleNoun);
                    noun.setFeature(InternalFeature.CASE, disc);
                    AdjPhraseSpec adjective1 = nlgFactory.createAdjectivePhrase(adjective);
                    if(comp) {
                        adjective1.setFeature(Feature.IS_COMPARATIVE, true);
                    }
                    noun.addModifier(adjective1);
                    String output1 = realiser.realise(noun).toString();
                    System.out.println(output1);
                    sentenceCounter += 1;

                    if(articleNoun != "ein Haus" && articleNoun != "eine Katze") {
                        NPPhraseSpec nounPl = nlgFactory.createNounPhrase(articleNoun);
                        nounPl.setFeature(InternalFeature.CASE, disc);
                        nounPl.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                        AdjPhraseSpec adjective2 = nlgFactory.createAdjectivePhrase(adjective);
                        if(comp) {
                            adjective2.setFeature(Feature.IS_COMPARATIVE, true);
                        }
                        nounPl.addModifier(adjective2);
                        String output2 = realiser.realise(nounPl).toString();
                        System.out.println(output2);
                        sentenceCounter += 1;
                    }
                }
            }
        }
    }

    @Test
    public void testRegularAdjectiveInflecion() {
        System.out.println("\n---------------------------- IRREGULAR ADJECTIVE INFLECTION ---------------------------\n");
        doAdjectiveInflection(regularAdjectives, false);
    }

    @Test
    public void testIrregularAdjectiveInflecion() {
        System.out.println("\n---------------------------- IRREGULAR ADJECTIVE INFLECTION ---------------------------\n");
        doAdjectiveInflection(irregularAdjectives, false);
    }

    @Test
    public void testAdjectiveInflecionSpecialWords() {
        //System.out.println("\n---------------------------- ADJECTIVE INFLECTION AFTER SPECIAL WORDS ---------------------------\n");
        for (int i = 0; i < adj_conj_special.length; i++) {
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase(adj_conj_special[i] + " " + "Schüler");
            subject2.addModifier("gut");
            sentence2.setSubject(subject2);
            String output2 = realiser.realiseSentence(sentence2);
            sentenceCounter += 1;
            Assertions.assertEquals(adj_conj_special_correct[i], output2);
        }

        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("alle Schüler");
        subject3.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        subject3.addModifier("gut");
        sentence3.setSubject(subject3);
        String output3 = realiser.realiseSentence(sentence3);
        sentenceCounter += 1;
        Assertions.assertEquals("Alle guten Schüler.", output3);
    }

    @Test
    public void testAdjComparative() {
        //System.out.println("\n---------------------------- Test adjective's comparative ---------------------------\n");
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("eine Schülerin");
        AdjPhraseSpec adjevtive1 = nlgFactory.createAdjectivePhrase("fleißig");
        adjevtive1.setFeature(Feature.IS_COMPARATIVE, true);
        subject.addModifier(adjevtive1);
        NPPhraseSpec object1 = nlgFactory.createNounPhrase("das Haus");
        object1.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        AdjPhraseSpec adjevtive1_2 = nlgFactory.createAdjectivePhrase("schön");
        adjevtive1_2.setFeature(Feature.IS_COMPARATIVE, true);
        object1.addModifier(adjevtive1_2);
        subject.addComplement(object1);
        sentence.setSubject(subject);
        String output = realiser.realiseSentence(sentence);
        sentenceCounter += 1;
        Assertions.assertEquals("Eine fleißigere Schülerin des schöneren Hauses.", output);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("die Schülerin");
        AdjPhraseSpec adjective2 = nlgFactory.createAdjectivePhrase("fleißig");
        adjective2.setFeature(Feature.IS_COMPARATIVE, true);
        subject2.addModifier(adjective2);
        sentence2.setIndirectObject(subject2);
        String output2 = realiser.realiseSentence(sentence2);
        sentenceCounter += 1;
        Assertions.assertEquals("Der fleißigeren Schülerin.", output2);

        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("die Schülerin");
        AdjPhraseSpec adjective3 = nlgFactory.createAdjectivePhrase("leise");
        adjective3.setFeature(Feature.IS_COMPARATIVE, true);
        subject3.addModifier(adjective3);
        sentence3.setObject(subject3);
        String output3 = realiser.realiseSentence(sentence3);
        sentenceCounter += 1;
        Assertions.assertEquals("Die leisere Schülerin.", output3);
    }

    @Test
    public void testIrregularAdjectivesComparative() {
        System.out.println("\n---------------------------- ADJECTIVE INFLECTION IN COMPARATIVE ---------------------------\n");
        doAdjectiveInflection(regularAdjectives, true);
        doAdjectiveInflection(irregularAdjectives, true);
    }

    @Test
    public void testAdjSuperlative() {
        System.out.println("\n---------------------------- Test adjective's superlative ---------------------------\n");
        for (String adjective : irregularAdjectives) {
            SPhraseSpec sentence = nlgFactory.createClause();
            NPPhraseSpec subject = nlgFactory.createNounPhrase("die Schülerin");
            AdjPhraseSpec adjevtive1 = nlgFactory.createAdjectivePhrase(adjective);
            adjevtive1.setFeature(Feature.IS_SUPERLATIVE, true);
            subject.addModifier(adjevtive1);
            sentence.setSubject(subject);
            String output = realiser.realiseSentence(sentence);
            System.out.println(output);
            sentenceCounter += 1;

            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("die Schülerin");
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");
            verb2.addPostModifier("am");
            AdjPhraseSpec adjective2 = nlgFactory.createAdjectivePhrase(adjective);
            adjective2.setFeature(Feature.IS_SUPERLATIVE, true);
            verb2.addPostModifier(adjective2);
            sentence2.setVerb(verb2);
            sentence2.setSubject(subject2);
            String output2 = realiser.realiseSentence(sentence2);
            System.out.println(output2);
            sentenceCounter += 1;
        }

        for (String adjective : regularAdjectives) {
            SPhraseSpec sentence = nlgFactory.createClause();
            NPPhraseSpec subject = nlgFactory.createNounPhrase("die Schülerin");
            AdjPhraseSpec adjevtive1 = nlgFactory.createAdjectivePhrase(adjective);
            adjevtive1.setFeature(Feature.IS_SUPERLATIVE, true);
            subject.addModifier(adjevtive1);
            sentence.setSubject(subject);
            String output = realiser.realiseSentence(sentence);
            System.out.println(output);
            sentenceCounter += 1;

            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("die Schülerin");
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");
            verb2.addPostModifier("am");
            AdjPhraseSpec adjective2 = nlgFactory.createAdjectivePhrase(adjective);
            adjective2.setFeature(Feature.IS_SUPERLATIVE, true);
            verb2.addPostModifier(adjective2);
            sentence2.setVerb(verb2);
            sentence2.setSubject(subject2);
            String output2 = realiser.realiseSentence(sentence2);
            System.out.println(output2);
            sentenceCounter += 1;
        }
        System.out.println("\nAdjective sentences generated: " + sentenceCounter);
    }
}
