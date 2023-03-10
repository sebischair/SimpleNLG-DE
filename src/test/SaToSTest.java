/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Contributor(s): Daniel Braun, Technical University of Munich.
 */

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.phrasespec.*;

import org.junit.jupiter.api.Assertions;

public class SaToSTest {
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
	public void testToS1() {
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("Betreiber");
        subject.setDeterminer("der");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("einräumen");
        NPPhraseSpec days = nlgFactory.createNounPhrase("tag");
        days.setPlural(true);
        days.addFrontModifier("vierzehn");
        NPPhraseSpec object = nlgFactory.createNounPhrase("Rückgaberecht");
        object.addFrontModifier(days);

        sentence.setSubject(subject);
        sentence.setVerb(verb);
        sentence.setObject(object);

        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Der Betreiber räumt vierzehn Tage Rückgaberecht ein.", output);
    }

    @Test
    public void testToS2() {
        SPhraseSpec sentence = nlgFactory.createClause();

        NPPhraseSpec subject = nlgFactory.createNounPhrase("Kündigung");
        subject.setPlural(true);

        VPPhraseSpec verb = nlgFactory.createVerbPhrase("sein");
        verb.addModifier("zulässig");

        PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
        pp.setPreposition("per");
        NPPhraseSpec o1 = nlgFactory.createNounPhrase("Brief");
        NPPhraseSpec o2 = nlgFactory.createNounPhrase("E-Mail");
        NPPhraseSpec o3 = nlgFactory.createNounPhrase("Fax");
        CoordinatedPhraseElement coo = nlgFactory.createCoordinatedPhrase(o1, o2);
        coo.addCoordinate(o3);
        pp.addComplement(coo);

        sentence.setSubject(subject);
        sentence.setVerb(verb);
        sentence.addComplement(pp);

        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Kündigungen sind zulässig per Brief, E-Mail und Fax.", output);
    }

    @Test
    public void testToS3() {
    	SPhraseSpec sentence = nlgFactory.createClause();

    	NPPhraseSpec subject = nlgFactory.createNounPhrase("Betreiber");
        subject.setDeterminer("der");

        VPPhraseSpec verb = nlgFactory.createVerbPhrase("gewähren");

        NPPhraseSpec object = nlgFactory.createNounPhrase("Gewährleistung");
        NPPhraseSpec time = nlgFactory.createNounPhrase("Jahr");
        time.setPlural(false);
        time.setDeterminer("ein");
        object.addFrontModifier(time);

        PPPhraseSpec prep = nlgFactory.createPrepositionPhrase("auf");
        NPPhraseSpec item = nlgFactory.createNounPhrase("Gegenstand");
        item.setPlural(true);
        item.addModifier("gebraucht");
        prep.addComplement(item);

        sentence.setSubject(subject);
        sentence.setVerb(verb);
        sentence.setObject(object);
        sentence.addComplement(prep);

        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Der Betreiber gewährt ein Jahr Gewährleistung auf gebrauchte Gegenstände.", output);
    }

    @Test
    public void testToS4() {
        SPhraseSpec sentence = nlgFactory.createClause();

        NPPhraseSpec subject = nlgFactory.createNounPhrase("Betreiber");
        subject.setDeterminer("der");

        VPPhraseSpec verb = nlgFactory.createVerbPhrase("gewähren");

        NPPhraseSpec object = nlgFactory.createNounPhrase("Gewährleistung");
        NPPhraseSpec time = nlgFactory.createNounPhrase("Jahr");
        time.setPlural(true);
        time.setDeterminer("zwei");
        object.addFrontModifier(time);

        PPPhraseSpec prep = nlgFactory.createPrepositionPhrase("auf");
        NPPhraseSpec item = nlgFactory.createNounPhrase("Gegenstand");
        item.addModifier("neu");
        item.setPlural(true);
        prep.addComplement(item);

        sentence.setSubject(subject);
        sentence.setVerb(verb);
        sentence.setObject(object);
        sentence.addComplement(prep);

        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Der Betreiber gewährt zwei Jahre Gewährleistung auf neue Gegenstände.", output);
    }

    @Test
    public void testToS5() {
        SPhraseSpec sentence = nlgFactory.createClause();

        NPPhraseSpec subject = nlgFactory.createNounPhrase("Kündigungsfrist");
        subject.setDeterminer("die");

        VPPhraseSpec verb = nlgFactory.createVerbPhrase("betragen");

        NPPhraseSpec object = nlgFactory.createNounPhrase("Monat");
        object.addPreModifier("drei");
        object.setPlural(true);

        sentence.setSubject(subject);
        sentence.setVerb(verb);
        sentence.setObject(object);

        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Die Kündigungsfrist beträgt drei Monate.", output);
    }

    @Test
    public void testToS6(){
        SPhraseSpec sentence = nlgFactory.createClause();

        NPPhraseSpec subject = nlgFactory.createNounPhrase("Betreiber");
        subject.setDeterminer("der");

        VPPhraseSpec verb = nlgFactory.createVerbPhrase("einschränken");
        verb.addPostModifier("gesetzwidrig");

        NPPhraseSpec object = nlgFactory.createNounPhrase("Rückgaberecht");
        object.setDeterminer("das");

        sentence.setSubject(subject);
        sentence.setVerb(verb);
        sentence.setObject(object);

        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Der Betreiber schränkt gesetzwidrig das Rückgaberecht ein.", output);
    }

    @Test
    public void testToS7() {
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("Betreiber");
        subject.setDeterminer("der");
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("gewähren");
        NPPhraseSpec weeks = nlgFactory.createNounPhrase("woche");
        weeks.setPlural(true);
        weeks.addFrontModifier("zwei");
        NPPhraseSpec object = nlgFactory.createNounPhrase("Rückgaberecht");
        object.addFrontModifier(weeks);


        sentence.setSubject(subject);
        sentence.setVerb(verb);
        sentence.setObject(object);


        String output = realiser.realiseSentence(sentence);
        Assertions.assertEquals("Der Betreiber gewährt zwei Wochen Rückgaberecht.", output);
    }
}
