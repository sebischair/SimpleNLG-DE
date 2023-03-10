import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

import org.junit.jupiter.api.Assertions;

public class RealiserTest {

    private static Lexicon lexicon;
    private static NLGFactory nlgFactory;
    private static Realiser realiser = null;

    String[] persons = {"Ich", "Du", "Er", "Wir", "Ihr"};

    String[] werden = {"werde", "wirst", "wird", "werden", "werdet"};
    String[] werden_pret = {"wurde", "wurdest", "wurde", "wurden", "wurdet"};
    String[] sein = {"bin", "bist", "ist", "sind", "seid"};
    String[] sein_pret = {"war", "warst", "war", "waren", "wart"};
    String[] haben = {"habe", "hast", "hat", "haben", "habt"};

    //Regular verbs
    String[] regularVerbs = {"kaufen", "lernen", "machen", "sagen", "folgen",
            "retten", "reden", "weiden", "baden", "beten", "atmen", "rechnen", "widmen", "wappnen",
            "segeln", "handeln", "ändern", "stolpern", "klingeln", "feiern",
            "hetzen",
            "widerspiegeln", "ausdrücken", "anleinen"};
    String[] regularVerbs_pres_correct = {"kaufe", "kaufst", "kauft", "kaufen", "kauft", "kaufen", "lerne", "lernst",
            "lernt", "lernen", "lernt", "lernen", "mache", "machst", "macht", "machen", "macht", "machen", "sage",
            "sagst", "sagt", "sagen", "sagt", "sagen", "folge", "folgst", "folgt", "folgen", "folgt", "folgen", "rette",
            "rettest", "rettet", "retten", "rettet", "retten", "rede", "redest", "redet", "reden", "redet", "reden",
            "weide", "weidest", "weidet", "weiden", "weidet", "weiden", "bade", "badest", "badet", "baden", "badet",
            "baden", "bete", "betest", "betet", "beten", "betet", "beten", "atme", "atmest", "atmet", "atmen", "atmet",
            "atmen", "rechne", "rechnest", "rechnet", "rechnen", "rechnet", "rechnen", "widme", "widmest", "widmet",
            "widmen", "widmet", "widmen", "wappne", "wappnest", "wappnet", "wappnen", "wappnet", "wappnen", "segele",
            "segelst", "segelt", "segeln", "segelt", "segeln", "handele", "handelst", "handelt", "handeln", "handelt",
            "handeln", "ändere", "änderst", "ändert", "ändern", "ändert", "ändern", "stolpere", "stolperst", "stolpert",
            "stolpern", "stolpert", "stolpern", "klingele", "klingelst", "klingelt", "klingeln", "klingelt", "klingeln",
            "feiere", "feierst", "feiert", "feiern", "feiert", "feiern", "hetze", "hetzt", "hetzt", "hetzen", "hetzt",
            "hetzen", "spiegele wider", "spiegelst wider", "spiegelt wider", "spiegeln wider", "spiegelt wider",
            "spiegeln wider", "drücke aus", "drückst aus", "drückt aus", "drücken aus", "drückt aus", "drücken aus",
            "leine an", "leinst an", "leint an", "leinen an", "leint an", "leinen an"};
    String[] regularVerbs_participleII = {"gekauft", "gelernt", "gemacht", "gesagt", "gefolgt",
            "gerettet", "geredet", "geweidet", "gebadet", "gebetet", "geatmet", "gerechnet", "gewidmet", "gewappnet",
            "gesegelt", "gehandelt", "geändert", "gestolpert", "geklingelt", "gefeiert",
            "gehetzt",
            "widergespiegelt", "ausgedrückt", "angeleint"};

    //Irregular verbs
    String[] irregularVerbs = {"sein", "laden", "heißen", "haben", "werden", "wissen", "denken", "gehen", "fahren", "bringen", "lassen"};
    String[] irregularSeperatableVerbs = {"aufweisen", "abweichen", "stattfinden", "weggehen", "abschließen"};
    String[] irregularModalVerbs = {"dürfen", "können", "mögen", "sollen", "wollen"};

    //Nouns with different genders and articles
    String[] nouns_artcl1 = {"der Fonds", "die Anleihe", "das Wertpapier", "ein Fonds", "eine Anleihe", "ein Wertpapier", "Fonds", "Anleihe", "Wertpapier"};
    String[] nouns_artcl2 = {"der Aktionär", "die Firma", "das Unternehmen", "ein Aktionär", "eine Firma", "ein Unternehmen", "Aktionär", "Firma", "Unternehmen"};

    //Nouns with special inflection forms
    String[] nouns = {"der fonds", "der vater", "der einfluss", "der fuß", "der index", "das suffix", "der Pilz", "das Ergebnis", "das jahr", "das Geschenk", "der junge",
            "der assistent", "der mensch", "der herr", "die frau", "die eltern", "das kind", "der löffel", "die niederlande", "die USA"};
    String[] nouns_gen_correct = {"Des Fonds.", "Des Vaters.", "Des Einflusses.", "Des Fußes.", "Des Indexes.",
            "Des Suffixes.", "Des Pilzes.", "Des Ergebnisses.", "Des Jahres.", "Des Geschenkes.", "Des Jungen.",
            "Des Assistenten.", "Des Menschen.", "Des Herrn.", "Der Frau.", "Der Eltern.", "Des Kindes.",
            "Des Löffels.", "Der Niederlande.", "Der USA."};
    String[] nouns_dat_correct = {"Aus dem Fonds.", "Aus dem Vater.", "Aus dem Einfluss.", "Aus dem Fuß.",
            "Aus dem Index.", "Aus dem Suffix.", "Aus dem Pilz.", "Aus dem Ergebnis.", "Aus dem Jahr.",
            "Aus dem Geschenk.", "Aus dem Jungen.", "Aus dem Assistenten.", "Aus dem Menschen.", "Aus dem Herrn.",
            "Aus der Frau.", "Aus den Eltern.", "Aus dem Kind.", "Aus dem Löffel.", "Aus den Niederlanden.",
            "Aus den USA."};
    String[] nouns_acc_correct = {"Fonds", "Väter", "Einflüsse", "Füße", "Indizes", "Suffixe", "Pilze", "Ergebnisse", "Jahre", "Geschenke", "Jungen",
            "Assistenten", "Menschen", "Herren", "Frauen", "Eltern", "Kinder", "Löffel", "Niederlande", "USA"};


    @BeforeAll
    public static void setup() {
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }

    @Test
    public void testPluralizationLexicon() {
        //Sentence1
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("die bewertung");
        subject1.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        sentence1.setSubject(subject1);
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("erfolgen");
        sentence1.setVerb(verb1);
        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Die Bewertungen erfolgen.", output1);


        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2_1 = nlgFactory.createNounPhrase("aktie");
        subject2_1.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        NPPhraseSpec subject2_2 = nlgFactory.createNounPhrase("rentenpapier");
        subject2_2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        NPPhraseSpec subject2_3 = nlgFactory.createNounPhrase("genussschein");
        subject2_3.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);

        CoordinatedPhraseElement subject2 = nlgFactory.createCoordinatedPhrase();
        subject2.addCoordinate(subject2_1);
        subject2.addCoordinate(subject2_2);
        subject2.addCoordinate(subject2_3);
        sentence2.setSubject(subject2);

        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");
        sentence2.setVerb(verb2);

        sentence2.addComplement("verfügbar");
        String output2 = realiser.realiseSentence(sentence2);
        Assertions.assertEquals("Aktien, Rentenpapiere und Genussscheine sind verfügbar.", output2);


        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("das arrangement");
        subject3.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        sentence3.setSubject(subject3);
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("bestehen");
        sentence3.setVerb(verb3);
        String output3 = realiser.realiseSentence(sentence3);
        Assertions.assertEquals("Die Arrangements bestehen.", output3);


        SPhraseSpec sentence4 = nlgFactory.createClause();
        NPPhraseSpec subject4 = nlgFactory.createNounPhrase("das risiko");
        sentence4.setSubject(subject4);
        VPPhraseSpec verb4 = nlgFactory.createVerbPhrase("sein");
        sentence4.setVerb(verb4);
        sentence4.addComplement("hoch");
        String output4 = realiser.realiseSentence(sentence4);
        Assertions.assertEquals("Das Risiko ist hoch.", output4);


        SPhraseSpec sentence5 = nlgFactory.createClause();
        NPPhraseSpec subject5 = nlgFactory.createNounPhrase("der fonds");
        subject5.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        sentence5.setSubject(subject5);
        VPPhraseSpec verb5 = nlgFactory.createVerbPhrase("sein");
        sentence5.setVerb(verb5);
        NPPhraseSpec object5 = nlgFactory.createNounPhrase("mischung");
        object5.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        sentence5.setObject(object5);
        String output5 = realiser.realiseSentence(sentence5);
        Assertions.assertEquals("Die Fonds sind Mischungen.", output5);


        SPhraseSpec sentence6 = nlgFactory.createClause();
        NPPhraseSpec subject6 = nlgFactory.createNounPhrase("die maßnahme");
        sentence6.setSubject(subject6);
        VPPhraseSpec verb6 = nlgFactory.createVerbPhrase("steigern");
        sentence6.setVerb(verb6);
        NPPhraseSpec object6 = nlgFactory.createNounPhrase("den kurs");
        object6.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        sentence6.setObject(object6);
        String output6 = realiser.realiseSentence(sentence6);
        Assertions.assertEquals("Die Maßnahme steigert die Kurse.", output6);
    }

    @Test
    public void testregularVerbInflection() {
        //System.out.println("\n---------------------------- Test regular verb inflection ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(persons[j]);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(regularVerbs[i]);
                sentence.setVerb(verb);
                String output = realiser.realiseSentence(sentence);
                Assertions.assertEquals(persons[j] + " " + regularVerbs_pres_correct[j + loop] + ".", output);
            }
            loop += 5;
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(regularVerbs[i]);
            sentence2.setVerb(verb2);
            String output2 = realiser.realiseSentence(sentence2);
            Assertions.assertEquals("Sie" + " " + regularVerbs_pres_correct[loop] + ".", output2);
            loop += 1;
        }
    }

    @Test
    public void testirregularVerbInflection() {
        System.out.println("\n---------------------------- Test irregular verb inflection ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(person);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(irrregularVerb);
                sentence.setVerb(verb);
                String output = realiser.realiseSentence(sentence);
                System.out.println(output);
            }

            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(irrregularVerb);
            sentence2.setVerb(verb2);
            String output2 = realiser.realiseSentence(sentence2);
            System.out.println(output2 + "\n");
        }
        System.out.println("\nIrregular verbs:\n");
        for (String irrregularSepVerb : irregularSeperatableVerbs) {
            for (String person : persons) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(person);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(irrregularSepVerb);
                sentence.setVerb(verb);
                String output = realiser.realiseSentence(sentence);
                System.out.println(output);
            }

            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(irrregularSepVerb);
            sentence2.setVerb(verb2);
            String output2 = realiser.realiseSentence(sentence2);
            System.out.println(output2 + "\n");
        }
    }

    @Test
    public void testregularVerbInflectionPreterite() {
        System.out.println("\n---------------------------- Test regular verb inflection in preterite ---------------------------\n");
        for (String regularVerb : regularVerbs) {
            for (String person : persons) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(person);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(regularVerb);
                sentence.setVerb(verb);
                sentence.setFeature(Feature.TENSE, Tense.PAST);
                String output = realiser.realiseSentence(sentence);
                System.out.println(output);
            }

            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(regularVerb);
            verb2.setFeature(Feature.TENSE, Tense.PAST);
            sentence2.setVerb(verb2);
            sentence2.setFeature(Feature.TENSE, Tense.PAST);
            String output2 = realiser.realiseSentence(sentence2);
            System.out.println(output2 + "\n");
        }
    }

    @Test
    public void testirregularVerbInflectionPreterite() {
        System.out.println("\n---------------------------- Test irregular verb inflection in preterite ---------------------------\n");
        for (String irrregularVerb : irregularVerbs) {
            for (String person : persons) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(person);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(irrregularVerb);
                sentence.setVerb(verb);
                sentence.setFeature(Feature.TENSE, Tense.PAST);
                String output = realiser.realiseSentence(sentence);
                System.out.println(output);
            }
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(irrregularVerb);
            sentence2.setVerb(verb2);
            sentence2.setFeature(Feature.TENSE, Tense.PAST);
            String output2 = realiser.realiseSentence(sentence2);
            System.out.println(output2 + "\n");
        }
    }

    @Test
    public void testAdjectiveArticleInflecion() {
        System.out.println("\n---------------------------- ADJECTIVE & ARTICLE INFLECTION ---------------------------\n");
        System.out.println("NOMINATIVE & DATIVE\n");
        // Singular
        System.out.println("Singular:");
        for (int i = 0; i < nouns_artcl1.length; i++) {
            // Singular
            SPhraseSpec sentence1 = nlgFactory.createClause();
            NPPhraseSpec subject1 = nlgFactory.createNounPhrase(nouns_artcl1[i]);
            VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("gehören");
            NPPhraseSpec object1 = nlgFactory.createNounPhrase(nouns_artcl2[i]);

            subject1.addModifier("wertvoll");
            object1.addModifier("groß");

            sentence1.setSubject(subject1);
            verb1.setIndirectObject(object1);
            sentence1.setVerb(verb1);

            String output1 = realiser.realiseSentence(sentence1);
            System.out.println(output1);
        }

        // Plural
        System.out.println("\nPlural:");
        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("das Wertpapier");
        subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("gehören");
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("der Aktionär");
        object2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);

        subject2.addModifier("wertvoll");
        object2.addModifier("groß");

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        verb2.setIndirectObject(object2);

        String output2 = realiser.realiseSentence(sentence2);
        System.out.println(output2);

        SPhraseSpec sentence2_2 = nlgFactory.createClause();
        NPPhraseSpec subject2_2 = nlgFactory.createNounPhrase("Wertpapier");
        subject2_2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        VPPhraseSpec verb2_2 = nlgFactory.createVerbPhrase("gehören");
        NPPhraseSpec object2_2 = nlgFactory.createNounPhrase("Aktionär");
        object2_2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);

        subject2_2.addModifier("wertvoll");
        object2_2.addModifier("groß");

        sentence2_2.setSubject(subject2_2);
        sentence2_2.setVerb(verb2_2);
        verb2_2.setIndirectObject(object2_2);

        String output2_2 = realiser.realiseSentence(sentence2_2);
        System.out.println(output2_2);


        System.out.println("\n\nNOMINATIVE & ACCUSATIVE\n");
        System.out.println("Singular:");
        for (int i = 0; i < nouns_artcl1.length; i++) {
            // Singular
            SPhraseSpec sentence1 = nlgFactory.createClause();
            NPPhraseSpec subject1 = nlgFactory.createNounPhrase(nouns_artcl1[i]);
            VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("beeinflussen");
            NPPhraseSpec object1 = nlgFactory.createNounPhrase(nouns_artcl2[i]);

            subject1.addModifier("wertvoll");
            object1.addModifier("groß");

            sentence1.setSubject(subject1);
            verb1.setObject(object1);
            sentence1.setVerb(verb1);

            String output1 = realiser.realiseSentence(sentence1);
            System.out.println(output1);
        }

        // Plural
        System.out.println("\nPlural:");
        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("das Wertpapier");
        subject3.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("beeinflussen");
        NPPhraseSpec object3 = nlgFactory.createNounPhrase("der Aktionär");
        object3.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);

        subject3.addModifier("wertvoll");
        object3.addModifier("groß");

        sentence3.setSubject(subject3);
        sentence3.setVerb(verb3);
        verb3.setObject(object3);

        String output3 = realiser.realiseSentence(sentence3);
        System.out.println(output3);


        SPhraseSpec sentence3_2 = nlgFactory.createClause();
        NPPhraseSpec subject3_2 = nlgFactory.createNounPhrase("Wertpapier");
        subject3_2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        VPPhraseSpec verb3_2 = nlgFactory.createVerbPhrase("beeinflussen");
        NPPhraseSpec object3_2 = nlgFactory.createNounPhrase("Aktionär");
        object3_2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);

        subject3_2.addModifier("wertvoll");
        object3_2.addModifier("groß");

        sentence3_2.setSubject(subject3_2);
        sentence3_2.setVerb(verb3_2);
        verb3_2.setObject(object3_2);

        String output3_2 = realiser.realiseSentence(sentence3_2);
        System.out.println(output3_2);


        System.out.println("\n\nNOMINATIVE & GENITIVE\n");
        System.out.println("Singular:");
        for (int i = 0; i < nouns_artcl1.length; i++) {
            // Singular
            SPhraseSpec sentence1 = nlgFactory.createClause();
            NPPhraseSpec subject1 = nlgFactory.createNounPhrase(nouns_artcl1[i]);
            NPPhraseSpec object1 = nlgFactory.createNounPhrase(nouns_artcl2[i]);
            object1.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
            subject1.setComplement(object1);

            subject1.addModifier("wertvoll");
            object1.addModifier("groß");

            sentence1.setSubject(subject1);

            String output1 = realiser.realiseSentence(sentence1);
            System.out.println(output1);
        }

        // Plural
        System.out.println("\nPlural:");
        SPhraseSpec sentence4 = nlgFactory.createClause();
        NPPhraseSpec subject4 = nlgFactory.createNounPhrase("das Wertpapier");
        subject4.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        NPPhraseSpec object4 = nlgFactory.createNounPhrase("der Aktionär");
        object4.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        object4.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        subject4.setComplement(object4);

        subject4.addModifier("wertvoll");
        object4.addModifier("groß");

        sentence4.setSubject(subject4);

        String output4 = realiser.realiseSentence(sentence4);
        System.out.println(output4);


        SPhraseSpec sentence4_2 = nlgFactory.createClause();
        NPPhraseSpec subject4_2 = nlgFactory.createNounPhrase("Wertpapier");
        subject4_2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        NPPhraseSpec object4_2 = nlgFactory.createNounPhrase("Aktionär");
        object4_2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        object4_2.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.GENITIVE);
        subject4_2.setComplement(object4_2);

        subject4_2.addModifier("wertvoll");
        object4_2.addModifier("groß");

        sentence4_2.setSubject(subject4_2);

        String output4_2 = realiser.realiseSentence(sentence4_2);
        System.out.println(output4_2);
    }


    @Test
    public void testCommaRules() {
        //System.out.println("\n---------------------------- Test comma placement with postposed additions ---------------------------\n");
        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec companyNoun3_1 = nlgFactory.createNounPhrase("BMW");
        NPPhraseSpec companyNoun3_2 = nlgFactory.createNounPhrase("IBM");
        NPPhraseSpec companyNoun3_3 = nlgFactory.createNounPhrase("Siemens");
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("expandieren");

        NPPhraseSpec modifier3_1 = nlgFactory.createNounPhrase("ein Unternehmen aus dem Bereich Automobilbau");
        NPPhraseSpec modifier3_2 = nlgFactory.createNounPhrase("ein Unternehmen aus dem Bereich IT");
        NPPhraseSpec modifier3_3 = nlgFactory.createNounPhrase("ein Technologiekonzern");
        modifier3_1.setFeature(Feature.APPOSITIVE, true);
        modifier3_2.setFeature(Feature.APPOSITIVE, true);
        modifier3_3.setFeature(Feature.APPOSITIVE, true);
        companyNoun3_1.addPostModifier(modifier3_1);
        companyNoun3_2.addPostModifier(modifier3_2);
        companyNoun3_3.addPostModifier(modifier3_3);

        CoordinatedPhraseElement subject3 = nlgFactory.createCoordinatedPhrase();
        subject3.addCoordinate(companyNoun3_1);
        subject3.addCoordinate(companyNoun3_2);
        subject3.addCoordinate(companyNoun3_3);

        subject3.setFeature(Feature.CONJUNCTION, "sowie");
        sentence3.setSubject(subject3);
        sentence3.setVerb(verb3);

        String output3 = realiser.realiseSentence(sentence3);
        Assertions.assertEquals("BMW, ein Unternehmen aus dem Bereich Automobilbau, IBM, ein Unternehmen aus dem Bereich IT, sowie Siemens, ein Technologiekonzern, expandieren.", output3);
    }

    @Test
    public void testGenitiveNouns() {
        //System.out.println("\n---------------------------- Test genitive inflection of nouns ---------------------------\n");
        for (int i = 0; i < nouns.length; i++) {
            NPPhraseSpec noun = nlgFactory.createNounPhrase(nouns[i]);
            noun.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
            String output = realiser.realiseSentence(noun);
            Assertions.assertEquals(nouns_gen_correct[i], output);
        }
    }

    @Test
    public void testDativeNouns() {
        //System.out.println("\n---------------------------- Test dative inflection of nouns ---------------------------\n");
        for (int i = 0; i < nouns.length; i++) {
            SPhraseSpec sentence = nlgFactory.createClause();
            NPPhraseSpec subject = nlgFactory.createNounPhrase("aus");
            NPPhraseSpec noun = nlgFactory.createNounPhrase(nouns[i]);
            sentence.setSubject(subject);
            sentence.setIndirectObject(noun);
            String output = realiser.realiseSentence(sentence);
            Assertions.assertEquals(nouns_dat_correct[i], output);
        }
    }

    @Test
    public void testAccusativeNouns() {
        //System.out.println("\n---------------------------- Test accusative inflection of nouns ---------------------------\n");
        for (int i = 0; i < nouns.length; i++) {
            SPhraseSpec sentence = nlgFactory.createClause();
            NPPhraseSpec subject = nlgFactory.createNounPhrase("Ich");
            VPPhraseSpec verb = nlgFactory.createVerbPhrase("mögen");
            NPPhraseSpec noun = nlgFactory.createNounPhrase(nouns[i]);
            noun.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence.setSubject(subject);
            sentence.setVerb(verb);
            sentence.setObject(noun);
            String output = realiser.realiseSentence(sentence);
            Assertions.assertEquals("Ich mag die " + nouns_acc_correct[i] + ".", output);
        }
    }

    @Test
    public void testModifiers() {
        //System.out.println("\n---------------------------- Test placement of modifiers ---------------------------\n");
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("der Hund");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("laufen");
        verb.addModifier("schnell");
        sentence.setSubject(subject);
        sentence.setVerb(verb);
        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Der Hund läuft schnell.", output);

        SPhraseSpec sentence1 = nlgFactory.createClause();
        sentence1.setSubject(subject);
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("laufen");
        verb1.addPreModifier("des Mannes");
        verb1.addPostModifier("schnell");
        sentence1.setVerb(verb1);
        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Der Hund des Mannes läuft schnell.", output1);

        SPhraseSpec sentence1_1 = nlgFactory.createClause();
        NPPhraseSpec subject1_1 = nlgFactory.createNounPhrase("wir");
        VPPhraseSpec verb1_1 = nlgFactory.createVerbPhrase("expandieren");
        sentence1_1.setFrontModifier("heute");
        sentence1_1.setSubject(subject1_1);
        sentence1_1.setVerb(verb1_1);
        String output1_1 = realiser.realiseSentence(sentence1_1);
        Assertions.assertEquals("Heute expandieren wir.", output1_1);

        SPhraseSpec sentence1_2 = nlgFactory.createClause();
        NPPhraseSpec subject1_2 = nlgFactory.createNounPhrase("wir");
        sentence1_2.addFrontModifier("heute");
        sentence1_2.setSubject(subject1_2);
        sentence1_2.setVerb(verb1_1);
        String output1_2 = realiser.realiseSentence(sentence1_2);
        Assertions.assertEquals("Heute expandieren wir.", output1_2);

        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("der hund");
        subject3.addModifier("groß");
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("laufen");
        sentence3.setSubject(subject3);
        sentence3.setVerb(verb3);
        String output3 = realiser.realiseSentence(sentence3);
        Assertions.assertEquals("Der große Hund läuft.", output3);

        SPhraseSpec sentence4 = nlgFactory.createClause();
        NPPhraseSpec subject4 = nlgFactory.createNounPhrase("der hund");
        subject4.addModifier("groß");
        sentence4.setSubject(subject4);
        sentence4.setVerb(verb3);
        String output4 = realiser.realiseSentence(sentence4);
        Assertions.assertEquals("Der große Hund läuft.", output4);

        SPhraseSpec sentence5 = nlgFactory.createClause();
        NPPhraseSpec subject5 = nlgFactory.createNounPhrase("der hund");
        subject5.addModifier("groß");
        sentence5.setSubject(subject5);
        sentence5.setVerb(verb3);
        sentence5.addFrontModifier("Heute");
        String output5 = realiser.realiseSentence(sentence5);
        Assertions.assertEquals("Heute läuft der große Hund.", output5);

        SPhraseSpec sentence6 = nlgFactory.createClause();
        NPPhraseSpec subject6 = nlgFactory.createNounPhrase("der zug");
        VPPhraseSpec verb6 = nlgFactory.createVerbPhrase("abfahren");
        verb6.addModifier("früher");
        verb6.addComplement("als der Bus");
        sentence6.setSubject(subject6);
        sentence6.setVerb(verb6);
        String output6 = realiser.realiseSentence(sentence6);
        Assertions.assertEquals("Der Zug fährt früher ab als der Bus.", output6);
    }

    @Test
    public void separableVerbsPositioning() {
        //System.out.println("\n---------------------------- TEST POSITIONING OF SEPARABLE VERBS ---------------------------\n");

        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("der fonds");
        AdvPhraseSpec adverb1 = nlgFactory.createAdverbPhrase("geringfügig");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("übertreffen");
        NPPhraseSpec object1 = nlgFactory.createNounPhrase("sein Marktsegment");
        object1.addPostModifier(adverb1);

        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);
        verb1.setComplement(object1);
        sentence1.setFeature(Feature.TENSE, Tense.PAST);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Der Fonds übertraf sein Marktsegment geringfügig.", output1);

        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("abschneiden");
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("sein Marktsegment");
        object2.addFrontModifier("als");
        verb2.addModifier(adverb1);
        verb2.addModifier("besser");
        verb2.addComplement(object2);
        sentence1.setVerb(verb2);

        String output2 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Der Fonds schnitt geringfügig besser ab als sein Marktsegment.", output2);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("bob");
        sentence2.setSubject(subject2);
        NPPhraseSpec object3 = nlgFactory.createNounPhrase("das fahrrad");
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("abschließen");
        verb3.addModifier("schnell");
        sentence2.setVerb(verb3);
        sentence2.setObject(object3);
        String output3 = realiser.realiseSentence(sentence2);
        Assertions.assertEquals("Bob schließt schnell das Fahrrad ab.", output3);

        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("das");
        sentence3.setSubject(subject3);
        NPPhraseSpec object3_1 = nlgFactory.createNounPhrase("Bob");
        VPPhraseSpec verb3_1 = nlgFactory.createVerbPhrase("ausmachen");
        object3_1.addPostModifier("nichts");
        sentence3.setVerb(verb3_1);
        sentence3.setIndirectObject(object3_1);
        String output3_1 = realiser.realiseSentence(sentence3);
        Assertions.assertEquals("Das macht Bob nichts aus.", output3_1);

        SPhraseSpec sentence4 = nlgFactory.createClause();
        NPPhraseSpec subject4 = nlgFactory.createNounPhrase("wir");
        subject4.addComplement("alle");
        sentence4.setSubject(subject4);
        VPPhraseSpec verb4 = nlgFactory.createVerbPhrase("aufwachen");
        verb4.addModifier("sehr früh");
        sentence4.setVerb(verb4);
        String output4 = realiser.realiseSentence(sentence4);
        Assertions.assertEquals("Wir alle wachen sehr früh auf.", output4);

        SPhraseSpec sentence5 = nlgFactory.createClause();
        NPPhraseSpec subject5 = nlgFactory.createNounPhrase("wir");
        subject5.addComplement("alle");
        sentence5.setSubject(subject5);
        VPPhraseSpec verb5 = nlgFactory.createVerbPhrase("aufwachen");
        verb5.addModifier("sehr früh");
        sentence5.setVerb(verb4);
        sentence5.addFrontModifier("Morgen");
        String output5 = realiser.realiseSentence(sentence5);
        Assertions.assertEquals("Morgen wachen wir alle sehr früh auf.", output5);

        SPhraseSpec sentence6 = nlgFactory.createClause();
        NPPhraseSpec subject6 = nlgFactory.createNounPhrase("bob");
        sentence6.setSubject(subject6);
        NPPhraseSpec object6 = nlgFactory.createNounPhrase("das fahrrad");
        object6.addModifier("rot");
        object6.addPostModifier("schnell");
        VPPhraseSpec verb6 = nlgFactory.createVerbPhrase("abschließen");
        sentence6.setVerb(verb6);
        sentence6.setObject(object6);
        sentence6.addFrontModifier("Morgen");
        String output6 = realiser.realiseSentence(sentence6);
        Assertions.assertEquals("Morgen schließt Bob das rote Fahrrad schnell ab.", output6);
    }

    @Test
    public void basicSyntaxTest() {
        //System.out.println("\n---------------------------- TEST SYNTAX ---------------------------\n");
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("tom");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("mögen");
        NPPhraseSpec object1 = nlgFactory.createNounPhrase("Hund");
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("Katze");
        NPPhraseSpec object3 = nlgFactory.createNounPhrase("Fisch");

        CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
        coord.addCoordinate(object1);
        coord.addCoordinate(object2);
        coord.addCoordinate(object3);

        sentence.setSubject(subject);
        sentence.setVerb(verb);
        sentence.setObject(coord);

        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Tom mag Hund, Katze und Fisch.", output);

        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("der Fonds");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("gehören");
        NPPhraseSpec object1_1 = nlgFactory.createNounPhrase("der Aktionär");

        subject1.addModifier("wertvoll");
        object1_1.addModifier("groß");

        sentence1.setSubject(subject1);
        sentence1.setIndirectObject(object1_1);
        sentence1.setVerb(verb1);

        String output1 = realiser.realiseSentence(sentence1);
        Assertions.assertEquals("Der wertvolle Fonds gehört dem großen Aktionär.", output1);
    }

    @Test
    public void testPassivePresVerbInflection() {
        //System.out.println("\n---------------------------- Test passive present verb inflection ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(persons[j]);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(regularVerbs[i]);
                verb.setFeature(Feature.PASSIVE, true);
                sentence.setVerb(verb);
                String output = realiser.realiseSentence(sentence);
                Assertions.assertEquals(persons[j] + " " + sein[j] + " " + regularVerbs_participleII[i] + ".", output);
            }
            loop += 5;
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(regularVerbs[i]);
            verb2.setFeature(Feature.PASSIVE, true);
            sentence2.setVerb(verb2);
            String output2 = realiser.realiseSentence(sentence2);
            Assertions.assertEquals("Sie" + " sind " + regularVerbs_participleII[i] + ".", output2);
            loop += 1;
        }
    }

    @Test
    public void testPassiveProgressivePresVerbInflection() {
        //System.out.println("\n---------------------------- Test passive progressive present verb inflection ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(persons[j]);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(regularVerbs[i]);
                verb.setFeature(Feature.PASSIVE, true);
                verb.setFeature(Feature.PROGRESSIVE, true);
                sentence.setVerb(verb);
                String output = realiser.realiseSentence(sentence);
                Assertions.assertEquals(persons[j] + " " + werden[j] + " " + regularVerbs_participleII[i] + ".", output);
            }
            loop += 5;
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(regularVerbs[i]);
            verb2.setFeature(Feature.PASSIVE, true);
            verb2.setFeature(Feature.PROGRESSIVE, true);
            sentence2.setVerb(verb2);
            String output2 = realiser.realiseSentence(sentence2);
            Assertions.assertEquals("Sie" + " werden " + regularVerbs_participleII[i] + ".", output2);
            loop += 1;
        }
    }

    @Test
    public void testPassivePretVerbInflection() {
        //System.out.println("\n---------------------------- Test passive preterite verb inflection ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(persons[j]);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(regularVerbs[i]);
                verb.setFeature(Feature.PASSIVE, true);
                sentence.setVerb(verb);
                sentence.setFeature(Feature.TENSE, Tense.PAST);
                String output = realiser.realiseSentence(sentence);
                Assertions.assertEquals(persons[j] + " " + werden_pret[j] + " " + regularVerbs_participleII[i] + ".", output);
            }
            loop += 5;
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(regularVerbs[i]);
            verb2.setFeature(Feature.PASSIVE, true);
            sentence2.setVerb(verb2);
            sentence2.setFeature(Feature.TENSE, Tense.PAST);
            String output2 = realiser.realiseSentence(sentence2);
            Assertions.assertEquals("Sie" + " wurden " + regularVerbs_participleII[i] + ".", output2);
            loop += 1;
        }
    }

    @Test
    public void adjAdvTest() {
        //System.out.println("\n---------------------------- TEST ADJECTIVES VS. ADVERBS ---------------------------\n");
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("er");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("sein");
        verb.addPostModifier("relativ");
        sentence.setSubject(subject);
        sentence.setVerb(verb);
        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Er ist relativ.", output);

        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("das auto");
        subject1.addModifier("schwer");
        subject1.addModifier("beladen");
        sentence1.setSubject(subject1);
        String output1 = realiser.realiseSentence(sentence1);
        //System.out.println(output1);
        Assertions.assertEquals("Das schwere und beladene Auto.", output1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("das auto");
        AdjPhraseSpec adjective2 = nlgFactory.createAdjectivePhrase("beladen");
        adjective2.addPreModifier("schwer");
        subject2.addModifier(adjective2);
        sentence2.setSubject(subject2);
        String output2 = realiser.realiseSentence(sentence2);
        Assertions.assertEquals("Das schwer beladene Auto.", output2);
    }

    @Test
    public void adjEnumerationTest() {
        //System.out.println("\n---------------------------- TEST ENUMERATION OF ADJECTIVES ---------------------------\n");
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("unternehmen");
        subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        AdjPhraseSpec adjective1 = nlgFactory.createAdjectivePhrase("deutsch");
        AdjPhraseSpec adjective2 = nlgFactory.createAdjectivePhrase("US-amerikanisch");
        AdjPhraseSpec adjective3 = nlgFactory.createAdjectivePhrase("englisch");
        subject2.addModifier(adjective1);
        subject2.addModifier(adjective2);
        subject2.addModifier(adjective3);
        Assertions.assertEquals("deutsche, US-amerikanische und englische Unternehmen", realiser.realise(subject2).toString());

        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("unternehmen");
        subject3.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        AdjPhraseSpec adjective3_1 = nlgFactory.createAdjectivePhrase("deutsch");
        AdjPhraseSpec adjective3_2 = nlgFactory.createAdjectivePhrase("US-amerikanisch");
        subject3.addModifier(adjective3_1);
        subject3.addModifier(adjective3_2);
        String output3 = realiser.realise(subject3).toString();
        //System.out.println(output3);
        Assertions.assertEquals("deutsche und US-amerikanische Unternehmen", output3);
    }

    @Test
    public void testPerfectVerbInflection() {
        //System.out.println("\n---------------------------- Test perfect verb inflection ---------------------------\n");

        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(persons[j]);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(regularVerbs[i]);
                sentence.setVerb(verb);
                sentence.setFeature(Feature.TENSE, Tense.PERFECT);
                String output = realiser.realiseSentence(sentence);
                Assertions.assertEquals(persons[j] + " " + sein[j] + " " + regularVerbs_participleII[i] + ".", output);
            }
            loop += 5;
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(regularVerbs[i]);
            sentence2.setVerb(verb2);
            sentence2.setFeature(Feature.TENSE, Tense.PERFECT);
            String output2 = realiser.realiseSentence(sentence2);
            Assertions.assertEquals("Sie" + " sind " + regularVerbs_participleII[i] + ".", output2);
            loop += 1;
        }
    }

    @Test
    public void testPerfectProgressiveVerbInflection() {
        System.out.println("\n---------------------------- Test perfect progressive verb inflection ---------------------------\n");

        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(persons[j]);
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(regularVerbs[i]);
                verb.setFeature(Feature.PROGRESSIVE, true);
                sentence.setVerb(verb);
                sentence.setFeature(Feature.TENSE, Tense.PERFECT);
                String output = realiser.realiseSentence(sentence);
                System.out.println(output);
                Assertions.assertEquals(persons[j] + " " + haben[j] + " " + regularVerbs_participleII[i] + ".", output);
            }
            loop += 5;
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(regularVerbs[i]);
            verb2.setFeature(Feature.PROGRESSIVE, true);
            sentence2.setVerb(verb2);
            sentence2.setFeature(Feature.TENSE, Tense.PERFECT);
            String output2 = realiser.realiseSentence(sentence2);
            Assertions.assertEquals("Sie" + " haben " + regularVerbs_participleII[i] + ".", output2);
            loop += 1;
        }
    }

    @Test
    public void testFutureVerbInflection() {
        //System.out.println("\n---------------------------- Test future verb inflection ---------------------------\n");
        int loop = 0;
        for (int i = 0; i < regularVerbs.length; i++) {
            for (int j = 0; j < persons.length; j++) {
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase(persons[j]);
                if(persons[j].equals("ihr")) {
                    subject.setPlural(true);
                }
                sentence.setSubject(subject);
                VPPhraseSpec verb = nlgFactory.createVerbPhrase(regularVerbs[i]);
                sentence.setVerb(verb);
                sentence.setFeature(Feature.TENSE, Tense.FUTURE);
                String output = realiser.realiseSentence(sentence);
                Assertions.assertEquals(persons[j] + " " + werden[j] + " " + regularVerbs[i] + ".", output);
            }
            loop += 5;
            SPhraseSpec sentence2 = nlgFactory.createClause();
            NPPhraseSpec subject2 = nlgFactory.createNounPhrase("sie");
            subject2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
            sentence2.setSubject(subject2);
            VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(regularVerbs[i]);
            sentence2.setVerb(verb2);
            sentence2.setFeature(Feature.TENSE, Tense.FUTURE);
            String output2 = realiser.realiseSentence(sentence2);
            Assertions.assertEquals("Sie" + " werden " + regularVerbs[i] + ".", output2);
            loop += 1;
        }
    }

    @Test
    public void testSpecialCharacters() {
        NLGElement test = nlgFactory.createNounPhrase("Immobilien & Bau");
    }

    @Test
    public void testVerbPreModifiers() {
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("Nachhause");
        sentence.setSubject(subject);
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("laufen");
        verb.addPreModifier("zu");
        sentence.setVerb(verb);
        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Nachhause zu laufen.", output);
    }

    @Test
    public void testPostModsVsComplements() {
        //System.out.println("\n---------------------------- Test the positioning of PostModifiers vs. Complements ---------------------------\n");
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("Tom");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("laufen");
        verb.addPostModifier("schnell");
        verb.addPostModifier("nach Hause");
        sentence.setVerb(verb);
        sentence.setSubject(subject);
        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Tom läuft schnell nach Hause.", output);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("Tom");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("laufen");
        verb2.addComplement("insbesondere");
        verb2.addPostModifier("schnell");
        verb2.addPostModifier("nach Hause");
        sentence2.setVerb(verb2);
        sentence2.setSubject(subject2);
        //sentence2.setIndirectObject("mit Mary");
        sentence2.setFrontModifier("Heute");
        String output2 = realiser.realiseSentence(sentence2);
        //System.out.println(output2);
    }

    @Test
    public void testModifierPositioning() {
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("er");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("liegen");
        verb.addModifier("gut");
        NPPhraseSpec object = nlgFactory.createNounPhrase("im Rennen");
        sentence.setVerb(verb);
        sentence.setSubject(subject);
        sentence.setIndirectObject(object);
        String output = realiser.realiseSentence(sentence);
        System.out.println(output);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("er");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("liegen");
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("im Rennen");
        object2.addPreModifier("gut");
        sentence2.setVerb(verb2);
        sentence2.setSubject(subject2);
        sentence2.setIndirectObject(object2);
        String output2 = realiser.realiseSentence(sentence2);
        System.out.println(output2);

        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("er");
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("liegen");
        NPPhraseSpec object3 = nlgFactory.createNounPhrase("im Rennen");
        object3.addModifier("gut");
        sentence3.setVerb(verb3);
        sentence3.setSubject(subject3);
        sentence3.setIndirectObject(object3);
        String output3 = realiser.realiseSentence(sentence3);
        System.out.println(output3);
    }

    @Test
    public void testPerfectWordOrder() {
        System.out.println("\n---------------------------- Test perfect verb inflection ---------------------------\n");
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase("er");
                VPPhraseSpec verb = nlgFactory.createVerbPhrase("mögen");
                verb.setFeature(Feature.PROGRESSIVE, true);
                NPPhraseSpec object = nlgFactory.createNounPhrase("sie");
                sentence.setSubject(subject);
                sentence.setVerb(verb);
                sentence.setIndirectObject(object);
                sentence.setFeature(Feature.TENSE, Tense.PERFECT);
                String output = realiser.realiseSentence(sentence);
                System.out.println(output);
                //Assertions.assertEquals("Er hat sie gemocht.", output);
                
                SPhraseSpec sentence2 = nlgFactory.createClause();
                NPPhraseSpec subject2 = nlgFactory.createNounPhrase("er");
                VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("mögen");
                NPPhraseSpec object2 = nlgFactory.createNounPhrase("sie");
                sentence2.setSubject(subject2);
                sentence2.setVerb(verb2);
                sentence2.setIndirectObject(object2);
                sentence2.setFeature(Feature.TENSE, Tense.FUTURE);
                String output2 = realiser.realiseSentence(sentence2);
                System.out.println(output2);
                //Assertions.assertEquals("Er wird sie mögen.", output);

    }

    @Test
    public void testApposition() {
        //System.out.println("\n---------------------------- Test commas with appositions ---------------------------\n");
                SPhraseSpec sentence = nlgFactory.createClause();
                NPPhraseSpec subject = nlgFactory.createNounPhrase("die überproportionalen Positionen in");
                NPPhraseSpec noun1 = nlgFactory.createNounPhrase("Bank of China (HK)");
                NPPhraseSpec addition = nlgFactory.createNounPhrase("ein Titel mit Sitz in Hongkong");
                addition.setFeature(Feature.APPOSITIVE, true);
                noun1.addPostModifier(addition);
                NPPhraseSpec noun2 = nlgFactory.createNounPhrase("American International Group (AIG)");
                CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
                coord.addCoordinate(noun1);
                coord.addCoordinate(noun2);
                sentence.setSubject(subject);
                sentence.setIndirectObject(coord);
                String output = realiser.realiseSentence(sentence);
                Assertions.assertEquals("Die überproportionalen Positionen in Bank of China (HK), einem Titel mit Sitz in Hongkong, und American International Group (AIG).", output);
    }

    @Test
    public void testUserSetGender() {
        //System.out.println("\n---------------------------- Test user set gender ---------------------------\n");
    	SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("ein Fußballmatch");
        subject.setFeature(LexicalFeature.GENDER, Gender.NEUTER);
        subject.addModifier("klein");
        sentence.setSubject(subject);
        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Ein kleines Fußballmatch.", output);
    }

    @Test
    public void helpTest() {
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("du");
        sentence.setSubject(subject);
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("naheliegen");
        sentence.setVerb(verb);
        sentence.setFeature(Feature.TENSE, Tense.PAST);
        String output = realiser.realiseSentence(sentence);
        System.out.println(output);
    }
}

