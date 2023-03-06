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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

import org.junit.jupiter.api.Assertions;

public class QuestionTest {
    private Lexicon lexicon;
    private NLGFactory nlgFactory;
    private Realiser realiser;

    @BeforeEach
    public void setup() {
        System.out.println("RUN");
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }

    @Test
    public void questionTest1(){
        //statement
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("Klaus");
        sentence.setSubject(subject);
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("verlieren");
        sentence.setVerb(verb);
        NPPhraseSpec object = nlgFactory.createNounPhrase("das Spiel");
        sentence.setObject(object);

        String output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Klaus verliert das Spiel.", output);

        //question yes_no
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("Klaus");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("verlieren");
        sentence.setVerb(verb);
        object = nlgFactory.createNounPhrase("das Spiel");
        sentence.setObject(object);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Verliert Klaus das Spiel?", output);

        //question how
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("Klaus");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("verlieren");
        sentence.setVerb(verb);
        object = nlgFactory.createNounPhrase("das Spiel");
        sentence.setObject(object);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Wie verliert Klaus das Spiel?", output);

        //question subject
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("Klaus");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("verlieren");
        sentence.setVerb(verb);
        object = nlgFactory.createNounPhrase("das Spiel");
        sentence.setObject(object);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHO_SUBJECT);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Wer verliert das Spiel?", output);

        //question object
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("Klaus");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("verlieren");
        sentence.setVerb(verb);
        object = nlgFactory.createNounPhrase("das Spiel");
        sentence.setObject(object);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Was verliert Klaus?", output);
    }

    @Test
    public void questionTest2(){
        //statement
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("das Auto");
        sentence.setSubject(subject);
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("sein");
        verb.addModifier("rot");
        sentence.setVerb(verb);

        String output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Das Auto ist rot.", output);

        //question yes_no
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("das Auto");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("sein");
        verb.addModifier("rot");
        sentence.setVerb(verb);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Ist das Auto rot?", output);

        //question how
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("das Auto");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("sein");
        verb.addModifier("rot");
        sentence.setVerb(verb);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Wie ist das Auto rot?", output);

        //question subject
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("das Auto");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("sein");
        verb.addModifier("rot");
        sentence.setVerb(verb);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_SUBJECT);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Was ist rot?", output);
    }

    @Test
    public void questionTest3(){
        //statement
        SPhraseSpec sentence = nlgFactory.createClause();
        NPPhraseSpec subject = nlgFactory.createNounPhrase("es");
        sentence.setSubject(subject);
        VPPhraseSpec verb = nlgFactory.createVerbPhrase("geben");
        verb.addModifier("heute");
        sentence.setVerb(verb);
        NPPhraseSpec object = nlgFactory.createNounPhrase("Fisch");
        sentence.setObject(object);
        PPPhraseSpec preposition = nlgFactory.createPrepositionPhrase("in");
        preposition.addComplement("der Mensa");
        sentence.addComplement(preposition);

        String output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Es gibt heute Fisch in der Mensa.", output);

        //yes/no question
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("es");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("geben");
        verb.addModifier("heute");
        sentence.setVerb(verb);
        object = nlgFactory.createNounPhrase("Fisch");
        sentence.setObject(object);
        preposition = nlgFactory.createPrepositionPhrase("in");
        preposition.addComplement("der Mensa");
        sentence.addComplement(preposition);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.YES_NO);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Gibt es heute Fisch in der Mensa?", output);

        //how question
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("es");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("geben");
        verb.addModifier("heute");
        sentence.setVerb(verb);
        object = nlgFactory.createNounPhrase("Fisch");
        sentence.setObject(object);
        preposition = nlgFactory.createPrepositionPhrase("in");
        preposition.addComplement("der Mensa");
        sentence.addComplement(preposition);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.HOW);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Wie gibt es heute Fisch in der Mensa?", output);

        //question object
        sentence = nlgFactory.createClause();
        subject = nlgFactory.createNounPhrase("es");
        sentence.setSubject(subject);
        verb = nlgFactory.createVerbPhrase("geben");



        sentence.setVerb(verb);

        NLGElement heute = nlgFactory.createAdverbPhrase("heute");
        sentence.addComplement(heute);

        object = nlgFactory.createNounPhrase("Fisch");
        sentence.setObject(object);
        preposition = nlgFactory.createPrepositionPhrase("in");
        preposition.addComplement("der Mensa");
        sentence.addComplement(preposition);

        sentence.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
        output = realiser.realiseSentence(sentence);
        System.out.println(output);
        Assertions.assertEquals("Was gibt es heute in der Mensa?", output);
    }
}