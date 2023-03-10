package MorphologyTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.phrasespec.*;
import simplenlgde.features.*;

public class VerbInflectionTest {

    private static Lexicon lexicon;
    private static NLGFactory nlgFactory;
    private static Realiser realiser = null;

    int sentenceCounter = 0;

    String[] persons = {"Ich", "Du", "Er", "Wir", "Ihr"};

    String[] regularVerbs = {"arbeiten", "benötigen", "charakterisieren", "drosseln", "entwaffnen", "fassen", "gehören",
    "hinken", "isolieren", "jagen", "kompensieren", "lernen", "machen", "nähen", "opfern", "parken", "quadrieren",
    "radeln", "sabotieren", "tagen", "überraschen", "vernetzen", "wachen", "xerographieren", "zählen"};

    String[] irregularVerbs = {"befragen", "denken", "empfehlen", "finden", "geben", "heißen", "können", "lassen", "messen",
    "nennen", "obliegen", "pfeifen", "quellen", "raten", "scheinen", "tragen", "unterliegen", "verlieren", "wachsen", "ziehen"};

    String[] separableVerbs = {"abfließen", "herkommen", "mitbringen", "naheliegen", "preisgeben"};

    String[] modalVerbs = {"dürfen", "können", "mögen", "müssen", "sollen", "wollen"};


    @BeforeAll
    public static void setup() {
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }


    @Test
    private void doInflection(String person, String regularVerb, Tense tense, Boolean progressive, Boolean passive) {
        // help method to create inflected form
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase(person);
        sentence.setSubject(subject);
        VPPhraseSpec verb = nlgFactory.createVerbPhrase(regularVerb);
        if(progressive) {
            verb.setFeature(Feature.PROGRESSIVE, true);
        }
        if(passive) {
            verb.setFeature(Feature.PASSIVE, true);
        }
        sentence.setVerb(verb);
        sentence.setFeature(Feature.TENSE, tense);
        String output = realiser.realiseSentence(sentence);
        System.out.println(output);
        sentenceCounter += 1;
    }

    @Test
    private void doInflectionSiePlural(String regularVerb, Tense tense, Boolean progressive, Boolean passive) {
        // help method to create inflected form for "sie" in plural
        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
        subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        sentence2.setSubject(subject2);
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(regularVerb);
        if(progressive) {
            verb2.setFeature(Feature.PROGRESSIVE, true);
        }
        if(passive) {
            verb2.setFeature(Feature.PASSIVE, true);
        }
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.TENSE, tense);
        String output2 = realiser.realiseSentence(sentence2);
        System.out.println(output2);
        sentenceCounter += 1;
    }

    @Test
    public void testregularVerbInflection() {
        System.out.println("\n---------------------------- Test regular verb inflection ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                doInflection(persons[j], regularVerbs[i], Tense.PRESENT, false, false);
            }
            loop += 5;
            doInflectionSiePlural(regularVerbs[i], Tense.PRESENT, false, false);
            loop += 1;
        }
    }

    @Test
    public void testirregularVerbInflection() {
        System.out.println("\n---------------------------- Test irregular verb inflection ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PRESENT, false, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PRESENT, false, false);
        }
    }

    @Test
    public void separableVerbInflection() {
        System.out.println("\n---------------------------- Test separable verb inflection ---------------------------\n");
        for (String irrregularVerb : separableVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PRESENT, false, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PRESENT, false, false);
        }
    }

    @Test
    public void testregularVerbInflectionPassive() {
        System.out.println("\n---------------------------- Test regular verb inflection passive ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                doInflection(persons[j], regularVerbs[i], Tense.PRESENT, false, true);
            }
            loop += 5;
            doInflectionSiePlural(regularVerbs[i], Tense.PRESENT, false, true);
            loop += 1;
        }
    }

    @Test
    public void testirregularVerbInflectionPassive() {
        System.out.println("\n---------------------------- Test irregular verb inflection passive ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PRESENT, false, true);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PRESENT, false, true);
        }
    }

    @Test
    public void separableVerbInflectionPassive() {
        System.out.println("\n---------------------------- Test separable verb inflection passive ---------------------------\n");
        for (String irrregularVerb : separableVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PRESENT, false, true);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PRESENT, false, true);
        }
    }

    @Test
    public void testregularVerbInflectionPassiveProg() {
        System.out.println("\n---------------------------- Test regular verb inflection passive progressive ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                doInflection(persons[j], regularVerbs[i], Tense.PRESENT, true, true);
            }
            loop += 5;
            doInflectionSiePlural(regularVerbs[i], Tense.PRESENT, true, true);
            loop += 1;
        }
    }

    @Test
    public void testirregularVerbInflectionPassiveProg() {
        System.out.println("\n---------------------------- Test irregular verb inflection passive progressive ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PRESENT, true, true);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PRESENT, true, true);
        }
    }

    @Test
    public void separableVerbInflectionPassiveProg() {
        System.out.println("\n---------------------------- Test separable verb inflection passive progressive ---------------------------\n");
        for (String irrregularVerb : separableVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PRESENT, true, true);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PRESENT, true, true);
        }
    }

    @Test
    public void testregularVerbInflectionPassivePret() {
        System.out.println("\n---------------------------- Test regular verb inflection passive past ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                doInflection(persons[j], regularVerbs[i], Tense.PAST, false, true);
            }
            loop += 5;
            doInflectionSiePlural(regularVerbs[i], Tense.PAST, false, true);
            loop += 1;
        }
    }

    @Test
    public void testirregularVerbInflectionPassivePret() {
        System.out.println("\n---------------------------- Test irregular verb inflection passive past ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PAST, false, true);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PAST, false, true);
        }
    }

    @Test
    public void separableVerbInflectionPassivePret() {
        System.out.println("\n---------------------------- Test separable verb inflection passive past---------------------------\n");
        for (String irrregularVerb : separableVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PAST, false, true);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PAST, false, true);
        }
    }

    @Test
    public void testregularVerbInflectionPreterite() {
        System.out.println("\n---------------------------- Test regular verb inflection in preterite ---------------------------\n");
        for (String regularVerb : regularVerbs) {
            for (String person : persons) {
                doInflection(person, regularVerb, Tense.PAST, false, false);
            }
            doInflectionSiePlural(regularVerb, Tense.PAST, false, false);
        }
    }

    @Test
    public void testirregularVerbInflectionPreterite() {
        System.out.println("\n---------------------------- Test irregular verb inflection in preterite ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PAST, false, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PAST, false, false);
        }
    }

    @Test
    public void separableVerbInflectionPreterite() {
        System.out.println("\n---------------------------- Test separable verb inflection in preterite ---------------------------\n");
        for (String irrregularVerb : separableVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PAST, false, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PAST, false, false);
        }
    }

    @Test
    public void testregularVerbInflectionPerfect() {
        System.out.println("\n---------------------------- Test regular verb inflection in perfect ---------------------------\n");
        for (String regularVerb : regularVerbs) {
            for (String person : persons) {
                doInflection(person, regularVerb, Tense.PERFECT, false, false);
            }
            doInflectionSiePlural(regularVerb, Tense.PERFECT, false, false);
        }
    }

    @Test
    public void testirregularVerbInflectionPerfect() {
        System.out.println("\n---------------------------- Test irregular verb inflection in perfect ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PERFECT, false, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PERFECT, false, false);
        }
    }

    @Test
    public void separableVerbInflectionPerfect() {
        System.out.println("\n---------------------------- Test separable verb inflection in perfect ---------------------------\n");
        for (String irrregularVerb : separableVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PERFECT, false, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PERFECT, false, false);
        }
    }

    @Test
    public void testregularVerbInflectionPerfectProg() {
        System.out.println("\n---------------------------- Test regular verb inflection in perfect progressive ---------------------------\n");
        for (String regularVerb : regularVerbs) {
            for (String person : persons) {
                doInflection(person, regularVerb, Tense.PERFECT, true, false);
            }
            doInflectionSiePlural(regularVerb, Tense.PERFECT, true, false);
        }
    }

    @Test
    public void testirregularVerbInflectionPerfectProg() {
        System.out.println("\n---------------------------- Test irregular verb inflection in perfect progressive ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PERFECT, true, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PERFECT, true, false);
        }
    }

    @Test
    public void separableVerbInflectionPerfectPro() {
        System.out.println("\n---------------------------- Test separable verb inflection in perfect progressive ---------------------------\n");
        for (String irrregularVerb : separableVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.PERFECT, true, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.PERFECT, true, false);
        }
    }

    @Test
    public void testregularVerbInflectionFuture() {
        System.out.println("\n---------------------------- Test regular verb inflection in future ---------------------------\n");
        for (String regularVerb : regularVerbs) {
            for (String person : persons) {
                doInflection(person, regularVerb, Tense.FUTURE, false, false);
            }
            doInflectionSiePlural(regularVerb, Tense.FUTURE, false, false);
        }
    }

    @Test
    public void testirregularVerbInflectionFuture() {
        System.out.println("\n---------------------------- Test irregular verb inflection in future ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.FUTURE, false, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.FUTURE, false, false);
        }
    }

    @Test
    public void separableVerbInflectionFuture() {
        System.out.println("\n---------------------------- Test separable verb inflection in future ---------------------------\n");
        for (String irrregularVerb : separableVerbs) {
            for (String person : persons) {
                doInflection(person, irrregularVerb, Tense.FUTURE, false, false);
            }
            doInflectionSiePlural(irrregularVerb, Tense.FUTURE, false, false);
        }
    }

    @Test
    public void testModalVerbInflection() {
        System.out.println("\n---------------------------- Test modal verbs present ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < modalVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(persons[j]);
                sentence.setSubject(subject);
                VPPhraseSpec modal = nlgFactory.createVerbPhrase(modalVerbs[i]);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(irregularVerbs[i]);
                modal.addPostModifier(verb);
                sentence.setVerb(modal);
                String output = realiser.realiseSentence(sentence);
                System.out.println(output);
                sentenceCounter += 1;
            }
            loop += 5;
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec modal2 = nlgFactory.createVerbPhrase(modalVerbs[i]);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(irregularVerbs[i]);
            modal2.addPostModifier(verb2);
            sentence2.setVerb(modal2);
            String output2 = realiser.realiseSentence(sentence2);
            System.out.println(output2);
            sentenceCounter += 1;
            loop += 1;
        }
        System.out.println("\nVerb sentences generated: " + sentenceCounter);
    }
}
