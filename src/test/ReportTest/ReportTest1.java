package test.ReportTest;
import org.junit.jupiter.api.Assertions;

import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

public class ReportTest1 {
	
    private Lexicon lexicon;
    private NLGFactory nlgFactory;
    private Realiser realiser = null;
    
    public ReportTest1(Lexicon lexicon2, NLGFactory nlgFactory2, Realiser realiser2) {
    	//System.out.println("\n---------------------------- Fondis A EUR (DE0008471020), März 2017 ---------------------------\n");
		this.lexicon = lexicon2;
		this.nlgFactory = nlgFactory2;
		this.realiser = realiser2;	
		}

	public void wertentwicklung() { 
        SPhraseSpec sentence1_1 = nlgFactory.createClause();
        NPPhraseSpec subject1_1 = nlgFactory.createNounPhrase("der Fonds");
        VPPhraseSpec verb1_1 = nlgFactory.createVerbPhrase("sich entwickeln");
        verb1_1.addPostModifier("leicht positiv");
        
        sentence1_1.setSubject(subject1_1);
        sentence1_1.setVerb(verb1_1);
        sentence1_1.setFeature(Feature.TENSE, Tense.PAST);
        sentence1_1.addFrontModifier("im März");
        
        SPhraseSpec sentence1_2 = nlgFactory.createClause();
        NPPhraseSpec subject1_2 = nlgFactory.createNounPhrase("sein Marktsegment");
        VPPhraseSpec verb1_2 = nlgFactory.createVerbPhrase("abschneiden");
        subject1_2.addFrontModifier("als");
        verb1_2.addModifier("auch");
        verb1_2.addModifier("besser");
        verb1_2.addComplement(subject1_2);
        sentence1_2.setVerb(verb1_2);
        sentence1_2.setFeature(Feature.COMPLEMENTISER, "und");
        sentence1_2.setFeature(Feature.TENSE, Tense.PAST);
       
        sentence1_1.addComplement(sentence1_2);
    	String output1 = realiser.realiseSentence(sentence1_1);
    	Assertions.assertEquals("Im März entwickelte sich der Fonds leicht positiv und schnitt auch besser ab als sein Marktsegment.", output1);
    	
        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("das ergebnis");
        subject2.addModifier("überdurchschnittlich");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");
        verb2.addComplement("hauptsächlich");
        verb2.addComplement("auf");
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("die Sektorstruktur");
        object2.addModifier("günstig");
        object2.addPostModifier("zurückzuführen");
        
        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setObject(object2);
        sentence2.setFeature(Feature.TENSE, Tense.PAST);
        
    	String output2 = realiser.realiseSentence(sentence2);
    	Assertions.assertEquals("Das überdurchschnittliche Ergebnis war hauptsächlich auf die günstige Sektorstruktur zurückzuführen.", output2);
    	
    	
        SPhraseSpec sentence3_1 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("unsere gewichtung");
        AdjPhraseSpec adjective3 = nlgFactory.createAdjectivePhrase("hoch");
        adjective3.addPreModifier("relativ");
        subject3.addModifier(adjective3);
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("überzeugen");
        verb3.addComplement("insbesondere");
        NPPhraseSpec object3_1_1 = nlgFactory.createNounPhrase("unternehmen");
        object3_1_1.addPreModifier("von");
        NPPhraseSpec object3_1_2 = nlgFactory.createNounPhrase("das IT-Segment");
        object3_1_2.addPreModifier("aus");
        object3_1_1.addComplement(object3_1_2);
        
        sentence3_1.addFrontModifier("auf Branchenebene");
        sentence3_1.setSubject(subject3);
        sentence3_1.setVerb(verb3);
        sentence3_1.setIndirectObject(object3_1_1);
        sentence3_1.setFeature(Feature.TENSE, Tense.PAST);
        
        SPhraseSpec sentence3_2 = nlgFactory.createClause();
        NPPhraseSpec subject3_2 = nlgFactory.createNounPhrase("die positionierung");
        subject3_2.addModifier("unterdurchschnittlich");
        NPPhraseSpec object3_2_1 = nlgFactory.createNounPhrase("Aktien");
        object3_2_1.addPreModifier("in");
        object3_2_1.addPostModifier("aus");
        NPPhraseSpec object3_2_2 = nlgFactory.createNounPhrase("der Bereich");
        NPPhraseSpec object3_2_3 = nlgFactory.createNounPhrase("Immobilien");
        NPPhraseSpec object3_2_4 = nlgFactory.createNounPhrase("Energie");
        CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase(object3_2_3, object3_2_4);
        object3_2_2.addComplement(coord);
        
        if(coord.checkIfPlural()) {
        	object3_2_2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        }
        
        sentence3_2.setSubject(subject3_2);
        sentence3_2.setObject(object3_2_1);
        sentence3_2.setIndirectObject(object3_2_2);
        sentence3_2.setFeature(Feature.COMPLEMENTISER, "und");
        sentence3_2.setFeature(Feature.TENSE, Tense.PAST);
       
        sentence3_1.addComplement(sentence3_2);
        
    	String output3 = realiser.realiseSentence(sentence3_1);
    	Assertions.assertEquals("Auf Branchenebene überzeugte insbesondere unsere relativ hohe Gewichtung von Unternehmen aus dem IT-Segment "
    			+ "und die unterdurchschnittliche Positionierung in Aktien aus den Bereichen Immobilien und Energie.", output3);
    	
    	
        SPhraseSpec sentence4 = nlgFactory.createClause();
        NPPhraseSpec subject4 = nlgFactory.createNounPhrase("ein Beitrag");
        subject4.addPreModifier("jedoch");
        subject4.addPostModifier("aus");
        subject4.addModifier("negativ");
        VPPhraseSpec verb4 = nlgFactory.createVerbPhrase("gegenüberstehen");
        NPPhraseSpec object4 = nlgFactory.createNounPhrase("die Gewichtung");
        AdjPhraseSpec adjective4 = nlgFactory.createAdjectivePhrase("hoch");
        adjective4.addPreModifier("relativ");
        object4.addModifier(adjective4);
        NPPhraseSpec genitive4 = nlgFactory.createNounPhrase("der Sektor");
        genitive4.addComplement("Gesundheitswesen");
        genitive4.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        object4.addComplement(genitive4);
        
        sentence4.setSubject(subject4);
        sentence4.setVerb(verb4);
        sentence4.setIndirectObject(object4);
        sentence4.addFrontModifier("dem");
        sentence4.setFeature(Feature.TENSE, Tense.PAST);
        
    	String output4 = realiser.realiseSentence(sentence4);
    	Assertions.assertEquals("Dem stand jedoch ein negativer Beitrag aus der relativ hohen Gewichtung des Sektors Gesundheitswesen gegenüber.", output4);
    	
        SPhraseSpec sentence5 = nlgFactory.createClause();
        NPPhraseSpec subject5 = nlgFactory.createNounPhrase("die einzeltitelauswahl");
        VPPhraseSpec verb5 = nlgFactory.createVerbPhrase("sein");
        verb5.addModifier("hauptsächlich");
        verb5.addModifier("in");
        NPPhraseSpec object5_1 = nlgFactory.createNounPhrase("das segment");
        NPPhraseSpec object5_2 = nlgFactory.createNounPhrase("basiskonsumgüter");
        NPPhraseSpec object5_3 = nlgFactory.createNounPhrase("gesundheitswesen");
        NPPhraseSpec object5_4 = nlgFactory.createNounPhrase("Roh-, Hilfs- & Betriebsstoffe");
        CoordinatedPhraseElement coord5 = nlgFactory.createCoordinatedPhrase();
        coord5.addCoordinate(object5_2);  
        coord5.addCoordinate(object5_3);
        coord5.addCoordinate(object5_4);
        coord5.setConjunction("sowie");
        
        if(coord5.checkIfPlural()) {
        	object5_1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        }
        object5_1.addComplement(coord5);
        
        sentence5.setSubject(subject5);
        sentence5.setVerb(verb5);
        sentence5.setIndirectObject(object5_1);
        sentence5.addFrontModifier("ungünstig");
        sentence5.setFeature(Feature.TENSE, Tense.PAST);
        
    	String output5 = realiser.realiseSentence(sentence5);
    	Assertions.assertEquals("Ungünstig war die Einzeltitelauswahl hauptsächlich in den Segmenten Basiskonsumgüter, Gesundheitswesen sowie Roh-, Hilfs- & Betriebsstoffe.", output5);
    	
        SPhraseSpec sentence6 = nlgFactory.createClause();
        NPPhraseSpec subject6 = nlgFactory.createNounPhrase("die aktienauswahl");
        VPPhraseSpec verb6 = nlgFactory.createVerbPhrase("sich erweisen");
        verb6.addComplement("hingegen");
        NPPhraseSpec object6_1 = nlgFactory.createNounPhrase("der bereich");
        object6_1.addPreModifier("in");
        NPPhraseSpec object6_2 = nlgFactory.createNounPhrase("energie");
        NPPhraseSpec object6_3 = nlgFactory.createNounPhrase("immobilien");
        CoordinatedPhraseElement coord6 = nlgFactory.createCoordinatedPhrase();
        coord6.addCoordinate(object6_2);  
        coord6.addCoordinate(object6_3);
        
        if(coord6.checkIfPlural()) {
        	object6_1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        }
        object6_1.addComplement(coord6);
        
        sentence6.setSubject(subject6);
        sentence6.setVerb(verb6);
        sentence6.setIndirectObject(object6_1);
        sentence6.addFrontModifier("als positiv");
        sentence6.setFeature(Feature.TENSE, Tense.PAST);
        
    	String output6 = realiser.realiseSentence(sentence6);
    	Assertions.assertEquals("Als positiv erwies sich hingegen die Aktienauswahl in den Bereichen Energie und Immobilien.", output6);
    	
        SPhraseSpec sentence7 = nlgFactory.createClause();
        NPPhraseSpec subject7 = nlgFactory.createNounPhrase("das engagement");
        subject7.addModifier("vergleichsweise");
        subject7.addModifier("stark");
        subject7.addPostModifier("in");
        VPPhraseSpec verb7 = nlgFactory.createVerbPhrase("erbringen");
        
        CoordinatedPhraseElement coord7 = nlgFactory.createCoordinatedPhrase();
        NPPhraseSpec object7_1 = nlgFactory.createNounPhrase("Mediobanca");
        NPPhraseSpec addition7_1 = nlgFactory.createNounPhrase("eine bank");
        AdjPhraseSpec adjective7_1 = nlgFactory.createAdjectivePhrase("diversifiziert");
        addition7_1.addModifier(adjective7_1);
        addition7_1.addComplement("aus Italien");
        addition7_1.setFeature(Feature.APPOSITIVE, true);
        object7_1.addPostModifier(addition7_1);
        coord7.addCoordinate(object7_1);
        
        NPPhraseSpec object7_2 = nlgFactory.createNounPhrase("die firma");
        object7_2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        object7_2.addModifier("beide");
        object7_2.addModifier("amerikanisch");
        CoordinatedPhraseElement coord7_2 = nlgFactory.createCoordinatedPhrase();
        NPPhraseSpec object7_2_1 = nlgFactory.createNounPhrase("Best Buy");
        object7_2_1.addComplement("(Einzelhandel: Computer & Elektronik)");
        NPPhraseSpec object7_2_2 = nlgFactory.createNounPhrase("Cabot Microelectronics");
        object7_2_2.addComplement("(Geräte zur Halbleiterproduktion)");
        coord7_2.addCoordinate(object7_2_1);
        coord7_2.addCoordinate(object7_2_2);
        coord7_2.addPreModifier(object7_2);
        coord7.addCoordinate(coord7_2);
        
        NPPhraseSpec object7_3 = nlgFactory.createNounPhrase("der beitrag");
        object7_3.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        object7_3.setFeature(InternalFeature.CASE, DiscourseFunction.OBJECT);
        object7_3.addModifier("größten");
        
        NPPhraseSpec object7_4 = nlgFactory.createNounPhrase("zum ergebnis");
        object7_4.setFeature(InternalFeature.CASE, DiscourseFunction.INDIRECT_OBJECT);
        object7_4.addModifier("relativ");
        
        object7_3.addComplement(object7_4);

        sentence7.setSubject(subject7);
        sentence7.setVerb(verb7);
        sentence7.setIndirectObject(coord7);
        sentence7.addComplement(object7_3);
        sentence7.addFrontModifier("auf Einzelwertebene");
        sentence7.setFeature(Feature.TENSE, Tense.PAST);
        
    	String output7 = realiser.realiseSentence(sentence7);
    	Assertions.assertEquals("Auf Einzelwertebene erbrachte das vergleichsweise starke Engagement in Mediobanca, einer diversifizierten Bank aus Italien, "
    			+ "und den beiden amerikanischen Firmen Best Buy (Einzelhandel: Computer & Elektronik) und Cabot Microelectronics (Geräte zur Halbleiterproduktion) "
    			+ "die größten Beiträge zum relativen Ergebnis.", output7);
    	
        SPhraseSpec sentence8 = nlgFactory.createClause();
        NPPhraseSpec subject8 = nlgFactory.createNounPhrase("der akzent");
        subject8.addComplement("auf");
        VPPhraseSpec verb8 = nlgFactory.createVerbPhrase("sich machen");
        verb8.addComplement("außerdem");
        NPPhraseSpec object8 = nlgFactory.createNounPhrase("die firma");
        object8.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        object8.addModifier("drei");
        object8.addModifier("amerikanisch");
        CoordinatedPhraseElement coord8 = nlgFactory.createCoordinatedPhrase();
        coord8.addCoordinate("Ally Financial");
        coord8.addCoordinate("Amgen (Biotechnologie)");
        coord8.addCoordinate("Bank of America");
        coord8.addComplement("positiv bemerkbar");
        object8.addComplement(coord8);
        
        sentence8.setSubject(subject8);
        sentence8.setVerb(verb8);
        sentence8.setObject(object8);
        sentence8.setFrontModifier("mit Blick auf Einzeltitel");
        sentence8.setFeature(Feature.TENSE, Tense.PAST);
        
    	String output8 = realiser.realiseSentence(sentence8);
    	Assertions.assertEquals("Mit Blick auf Einzeltitel machte sich außerdem "
    			+ "der Akzent auf die drei amerikanischen Firmen Ally Financial, Amgen (Biotechnologie) und Bank of America positiv bemerkbar.", output8);
    	
        SPhraseSpec sentence9 = nlgFactory.createClause();
        NPPhraseSpec subject9 = nlgFactory.createNounPhrase("die übergewichtung");
        subject9.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        subject9.addPostModifier("von");
        VPPhraseSpec verb9 = nlgFactory.createVerbPhrase("leisten");
        
        CoordinatedPhraseElement coord9 = nlgFactory.createCoordinatedPhrase();
        NPPhraseSpec object9_1 = nlgFactory.createNounPhrase("Walmart");
        coord9.addCoordinate(object9_1);
        
        NPPhraseSpec object9_2 = nlgFactory.createNounPhrase("SCOR");
        coord9.addCoordinate(object9_2);
        
        NPPhraseSpec object9_3 = nlgFactory.createNounPhrase("der beitrag");
        object9_3.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        object9_3.setFeature(InternalFeature.CASE, DiscourseFunction.OBJECT);
        object9_3.addModifier("größten");
        
        NPPhraseSpec object9_4 = nlgFactory.createNounPhrase("zum ergebnis");
        object9_4.setFeature(InternalFeature.CASE, DiscourseFunction.INDIRECT_OBJECT);
        object9_4.addModifier("relativ");
        
        object9_3.addComplement(object9_4);

        sentence9.setSubject(subject9);
        sentence9.setVerb(verb9);
        sentence9.setIndirectObject(coord9);
        sentence9.addComplement(object9_3);
        sentence9.addFrontModifier("auf Einzelwertebene");
        sentence9.setFeature(Feature.TENSE, Tense.PAST);
        
    	String output9 = realiser.realiseSentence(sentence9);
    	Assertions.assertEquals("Auf Einzelwertebene leisteten die Übergewichtungen von Walmart und SCOR die größten Beiträge zum relativen Ergebnis.", output9);

    }
    
    public void anlagestrategie() {
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("der anlageschwerpunkt");
        NPPhraseSpec genitive1 = nlgFactory.createNounPhrase("der fonds");
        genitive1.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        subject1.addComplement(genitive1);
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("liegen");
        verb1.addPostModifier("nach wie vor auf");
        NPPhraseSpec object1 = nlgFactory.createNounPhrase("unternehmen");
        object1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        object1.addModifier("international");
        object1.addPostModifier("aus entwickelten Staaten");
        
        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);
        sentence1.setIndirectObject(object1);
        sentence1.setFeature(Feature.TENSE, Tense.PAST);
      
    	String output1 = realiser.realiseSentence(sentence1);
    	Assertions.assertEquals("Der Anlageschwerpunkt des Fonds lag nach wie vor auf internationalen Unternehmen aus entwickelten Staaten.", output1);
    	
        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("der \"Active Share\"");
        NPPhraseSpec genitive2 = nlgFactory.createNounPhrase("der fonds");
        genitive2.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        subject2.addComplement(genitive2);
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("liegen");
        verb2.addPostModifier("zum Monatsende bei 72,13 %");
        
        SPhraseSpec sentence2_1 = nlgFactory.createClause();
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("das management");
        AdjPhraseSpec adjective2 = nlgFactory.createAdjectivePhrase("aktiv");
        adjective2.addPreModifier("ausgeprägt");
        object2.addModifier(adjective2);
        VPPhraseSpec verb2_1 = nlgFactory.createVerbPhrase("unterstreichen");
        verb2_1.addPreModifier(object2);
        sentence2_1.setVerb(verb2_1);
        sentence2_1.addFrontModifier(", was");
        
        sentence2.addComplement(sentence2_1);
        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.TENSE, Tense.PAST);
      
    	String output2 = realiser.realiseSentence(sentence2);
    	Assertions.assertEquals("Der \"Active Share\" des Fonds lag zum Monatsende bei 72,13 %, was "
    			+ "das ausgeprägt aktive Management unterstreicht.", output2);
    	
        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("die kennzahl");
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("ausdrücken");
        
        SPhraseSpec sentence3_1 = nlgFactory.createClause();
        NPPhraseSpec subject3_1 = nlgFactory.createNounPhrase("das fondsmanagement");
        VPPhraseSpec verb3_1 = nlgFactory.createVerbPhrase("abweichen");
        NPPhraseSpec object3_1 = nlgFactory.createNounPhrase("zur erzielung");
        NPPhraseSpec object3_2 = nlgFactory.createNounPhrase("eine rendite");
        object3_2.addModifier("überdurchschnittlich");
        NPPhraseSpec object3_3 = nlgFactory.createNounPhrase("vom vergleichsindex");
        object3_2.addComplement(object3_3);
        object3_1.addComplement(object3_2);
        
        sentence3_1.setSubject(subject3_1);
        sentence3_1.setVerb(verb3_1);
        sentence3_1.setIndirectObject(object3_1);
        sentence3_1.setFeature(Feature.COMPLEMENTISER, "inwieweit");
        
        sentence3.addComplement(sentence3_1);
        sentence3.setSubject(subject3);
        sentence3.setVerb(verb3);
      
    	String output3 = realiser.realiseSentence(sentence3);
    	//System.out.println(output3 + "\n");
    	/*Assertions.assertEquals("Die Kennzahl drückt aus, inwieweit das Fondsmanagement zur Erzielung einer überdurchschnittlichen "
    			+ "Rendite vom Vergleichsindex abweicht.", output3);*/
    	
        SPhraseSpec sentence4 = nlgFactory.createClause();
        NPPhraseSpec subject4 = nlgFactory.createNounPhrase("das engagement");
        subject4.addComplement("in Informa");
        VPPhraseSpec verb4 = nlgFactory.createVerbPhrase("beenden");
		verb4.setFeature(Feature.PASSIVE, true);
		verb4.setFeature(Feature.PROGRESSIVE, true);

        sentence4.setSubject(subject4);
        sentence4.setVerb(verb4);
        sentence4.setFeature(Feature.TENSE, Tense.PAST);
      
    	String output4 = realiser.realiseSentence(sentence4);
    	//System.out.println(output4 + "\n");
    	/*Assertions.assertEquals("Das Engagement in Informa, und den beiden amerikanischen Gesellschaften Broadridge Financial Solution (Datenverarbeitungs- & Outsourcing-Dienste) und "
    			+ "The Hartford Financial Services Group (Diversifizierte Versicherungen) wurde beendet.", output4);*/
    }
}
