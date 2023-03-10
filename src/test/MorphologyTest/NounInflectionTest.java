package MorphologyTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

import org.junit.jupiter.api.Assertions;

public class NounInflectionTest {

    private static Lexicon lexicon;
    private static NLGFactory nlgFactory;
    private static Realiser realiser = null;

    int sentenceCounter = 0;

    String[] irregularNounsDirect = {"der Ball", "das Blatt", "die Bank", "das Dach", "der Gesang", "das Glas",
            "das Grab", "der Bauch", "der Block", "der Bruch", "der Bruder", "der Schwur", "der Zug", "der Korb",
            "das Korn", "der Saft", "das Kalb", "die Macht", "die Magd", "das Lamm"};

    String[] irregularNounsIndirect = {"ein Ball", "ein Blatt", "eine Bank", "ein Dach", "ein Gesang", "ein Glas",
            "ein Grab", "ein Bauch", "ein Block", "ein Bruch", "ein Bruder", "ein Schwur", "ein Zug", "ein Korb",
            "ein Korn", "ein Saft", "ein Kalb", "eine Macht", "eine Magd", "ein Lamm"};

    String[] regularNounsDirect = {"der Mensch", "die Frau", "der Redner", "die Katze", "das Hemd", "die Straßenbahn",
    "die Erziehung", "die Entfernung", "die Tulpe", "die Amöbe"};

    String[] regularNounsIndirect = {"ein Mensch", "eine Frau", "ein Redner", "eine Katze", "ein Hemd", "eine Straßenbahn",
            "eine Erziehung", "eine Entfernung", "eine Tulpe", "eine Amöbe"};

    DiscourseFunction[] cases = {DiscourseFunction.SUBJECT, DiscourseFunction.GENITIVE, DiscourseFunction.OBJECT,
            DiscourseFunction.INDIRECT_OBJECT};

    //Compound nouns
    String[] nouns_compound = {"die russische föderation", "die Quantitative Linguistik", "die vereinten nationen",
            "die Theoretische Informatik", "die juristische Person", "das internes rechnungswesen",
            "die tschechische republik", "die vereinigten arabischen emirate", "das Altenburger Land",
            "die Argentinische Republik"};

    String[] nouns_compound_dat_correct = {
            "Aktien aus der Russischen Föderation.",
            "Aktien aus der Quantitativen Linguistik.",
            "Aktien aus den Vereinten Nationen.",
            "Aktien aus der Theoretischen Informatik.",
            "Aktien aus der Juristischen Person.",
            "Aktien aus dem Internen Rechnungswesen.",
            "Aktien aus der Tschechischen Republik.",
            "Aktien aus den Vereinigten Arabischen Emiraten.",
            "Aktien aus dem Altenburger Land.",
            "Aktien aus der Argentinischen Republik."};

    String[] nouns_compound_acc_correct = {
            "Ich mag die Russische Föderation.",
            "Ich mag die Quantitative Linguistik.",
            "Ich mag die Vereinten Nationen.",
            "Ich mag die Theoretische Informatik.",
            "Ich mag die Juristische Person.",
            "Ich mag das Interne Rechnungswesen.",
            "Ich mag die Tschechische Republik.",
            "Ich mag die Vereinigten Arabischen Emirate.",
            "Ich mag das Altenburger Land.",
            "Ich mag die Argentinische Republik."};

    String[] nouns_compound_gen_correct = {
            "Aktien der Russischen Föderation.",
            "Aktien der Quantitativen Linguistik.",
            "Aktien der Vereinten Nationen.",
            "Aktien der Theoretischen Informatik.",
            "Aktien der Juristischen Person.",
            "Aktien des Internen Rechnungswesens.",
            "Aktien der Tschechischen Republik.",
            "Aktien der Vereinigten Arabischen Emirate.",
            "Aktien des Altenburger Lands.",
            "Aktien der Argentinischen Republik."};


    @BeforeAll
    public static void setup() {
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }

    @Test
    private void doNounInflection(String[] nouns, Boolean testPlural) {
        for (String noun : nouns) {
                for (DiscourseFunction disc : cases) {
                    NPPhraseSpec nounInfl = nlgFactory.createNounPhrase(noun);
                    nounInfl.setFeature(InternalFeature.CASE, disc);
                    String output1 = realiser.realise(nounInfl).toString();
                    sentenceCounter += 1;
                    System.out.println(output1);

                    if(testPlural) {
                        NPPhraseSpec nounInflPl = nlgFactory.createNounPhrase(noun);
                        nounInflPl.setFeature(InternalFeature.CASE, disc);
                        nounInflPl.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                        String output2 = realiser.realise(nounInflPl).toString();
                        sentenceCounter += 1;
                        System.out.println(output2);
                    }
                }
        }
    }

    @Test
    public void testRegularNounInflecionDir() {
        System.out.println("\n---------------------------- Regular noun inflection with direct article ---------------------------\n");
        doNounInflection(regularNounsDirect, true);
    }

    @Test
    public void testIrregularNounInflecionDir() {
        System.out.println("\n---------------------------- Irregular noun inflection with direct article ---------------------------\n");
        doNounInflection(irregularNounsDirect, true);
    }

    @Test
    public void testRegularNounInflecionIndir() {
        System.out.println("\n---------------------------- Regular noun inflection with indirect article ---------------------------\n");
        // no plural for indirect article
        doNounInflection(regularNounsIndirect, false);
    }

    @Test
    public void testIrregularNounInflecionIndir() {
        System.out.println("\n---------------------------- Irregular noun inflection with indirect article ---------------------------\n");
        // no plural for indirect article
        doNounInflection(irregularNounsIndirect, false);
    }

    @Test
    public void testCompoundWordInflection() {
        //System.out.println("\n---------------------------- Test inflection of compound words ---------------------------\n");
        for (int i = 0; i < nouns_compound.length; i++) {
            SPhraseSpec sentence = nlgFactory.createClause();
            NPPhraseSpec subject = nlgFactory.createNounPhrase("Aktien");
            subject.setPlural(true);
            subject.addComplement("aus");
            NPPhraseSpec noun = nlgFactory.createNounPhrase(nouns_compound[i]);
            sentence.setSubject(subject);
            sentence.setIndirectObject(noun);
            String output = realiser.realiseSentence(sentence);
            sentenceCounter += 1;
            Assertions.assertEquals(nouns_compound_dat_correct[i], output);

            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("Ich");
            NPPhraseSpec noun2 = nlgFactory.createNounPhrase(nouns_compound[i]);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("mögen");
            sentence2.setSubject(subject2);
            sentence2.setObject(noun2);
            sentence2.setVerb(verb2);
            String output2 = realiser.realiseSentence(sentence2);
            sentenceCounter += 1;
            Assertions.assertEquals(nouns_compound_acc_correct[i], output2);

            SPhraseSpec sentence3 = nlgFactory.createClause();
            NPPhraseSpec subject3 = nlgFactory.createNounPhrase("Aktien");
            subject3.setPlural(true);
            NPPhraseSpec noun3 = nlgFactory.createNounPhrase(nouns_compound[i]);
            noun3.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
            subject3.addComplement(noun3);
            sentence3.setSubject(subject3);
            String output3 = realiser.realiseSentence(sentence3);
            sentenceCounter += 1;
            Assertions.assertEquals(nouns_compound_gen_correct[i], output3);
        }
        System.out.println("\nNoun sentences generated: " + sentenceCounter);
    }
}
