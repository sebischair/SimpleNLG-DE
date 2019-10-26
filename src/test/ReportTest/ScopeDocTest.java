package test.ReportTest;

import org.junit.jupiter.api.Assertions;

import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

public class ScopeDocTest {
	
    private Lexicon lexicon;
    private NLGFactory nlgFactory;
    private Realiser realiser = null;
    
    public ScopeDocTest(Lexicon lexicon2, NLGFactory nlgFactory2, Realiser realiser2) {
		this.lexicon = lexicon2;
		this.nlgFactory = nlgFactory2;
		this.realiser = realiser2;
	}
	
    public void scopeDocB_4_2() {  
    	//System.out.println("\n---------------------------- SECTION B4.2 ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("wir");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("investieren");
        verb1.addModifier("schwerpunktmäßig");
        NPPhraseSpec object1_1 = nlgFactory.createNounPhrase("in Unternehmen");
        NPPhraseSpec object1_2 = nlgFactory.createNounPhrase("kanada");
        
        CoordinatedPhraseElement obj1 = nlgFactory.createCoordinatedPhrase(object1_1, object1_2);
        obj1.setFeature(Feature.CONJUNCTION, "aus");
        
        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);
        sentence1.setObject(obj1);
        sentence1.setFeature(Feature.TENSE, Tense.PAST);
        
        //sentence1.addFrontModifier("auf regionaler Ebene");
        NPPhraseSpec place = nlgFactory.createNounPhrase("Ebene");
        place.addModifier("regional");
        PPPhraseSpec pp = nlgFactory.createPrepositionPhrase();
        place.setFeature(InternalFeature.CASE, DiscourseFunction.INDIRECT_OBJECT);
        pp.addComplement(place);
        pp.setPreposition("auf");
        sentence1.addFrontModifier(pp);
        
    	String output1 = realiser.realiseSentence(sentence1);
    	Assertions.assertEquals("Auf regionaler Ebene investierten wir schwerpunktmäßig in Unternehmen aus Kanada.", output1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("titel");
        subject2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");
        NPPhraseSpec object2_1 = nlgFactory.createNounPhrase("kanada");
        NPPhraseSpec object2_2 = nlgFactory.createNounPhrase("Deutschland");
        NPPhraseSpec object2_3 = nlgFactory.createNounPhrase("Spanien");
        
        CoordinatedPhraseElement obj2 = nlgFactory.createCoordinatedPhrase();
        obj2.setFeature(Feature.CONJUNCTION, "sowie");
        obj2.addPreModifier("aus");
        obj2.addCoordinate(object2_1);  
        obj2.addCoordinate(object2_2);
        obj2.addCoordinate(object2_3);
        subject2.addPostModifier(obj2);
 
        subject2.addPostModifier("unterdurchschnittlich vertreten");
        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        sentence2.setFeature(Feature.TENSE, Tense.PAST);
        
        sentence2.addFrontModifier("auf regionaler Ebene");
        
    	String output2 = realiser.realiseSentence(sentence2);
    	Assertions.assertEquals("Auf regionaler Ebene waren Titel aus Kanada, Deutschland sowie Spanien unterdurchschnittlich vertreten.", output2);
    	
        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("der fonds");
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("sein");
        NPPhraseSpec object3_1 = nlgFactory.createNounPhrase("kanada");
        NPPhraseSpec object3_2 = nlgFactory.createNounPhrase("Deutschland");
        NPPhraseSpec object3_3 = nlgFactory.createNounPhrase("Spanien");
        
        CoordinatedPhraseElement obj3 = nlgFactory.createCoordinatedPhrase();
        obj3.addPreModifier("in Unternehmen aus");
        obj3.addCoordinate(object3_1);  
        obj3.addCoordinate(object3_2);
        obj3.addCoordinate(object3_3);
        subject3.addPostModifier(obj3);
 
        subject3.addPostModifier("übergewichtet");
        sentence3.setSubject(subject3);
        sentence3.setVerb(verb3);
        sentence3.setFeature(Feature.TENSE, Tense.PAST);
        
        sentence3.addFrontModifier("auf regionaler Ebene");
        
    	String output3 = realiser.realiseSentence(sentence3);
    	Assertions.assertEquals("Auf regionaler Ebene war der Fonds in Unternehmen aus Kanada, Deutschland und Spanien übergewichtet.", output3);

        SPhraseSpec sentence4 = nlgFactory.createClause();
        NPPhraseSpec subject4 = nlgFactory.createNounPhrase("Titel");
        subject4.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
        VPPhraseSpec verb4 = nlgFactory.createVerbPhrase("sein");
        NPPhraseSpec object4_1 = nlgFactory.createNounPhrase("Italien");
        NPPhraseSpec object4_2 = nlgFactory.createNounPhrase("die Schweiz");
        
        CoordinatedPhraseElement obj4 = nlgFactory.createCoordinatedPhrase();
        obj4.addPreModifier("aus");
        obj4.addCoordinate(object4_1);  
        obj4.addCoordinate(object4_2);
 
        sentence4.setSubject(subject4);
        sentence4.setVerb(verb4);
        sentence4.setIndirectObject(obj4);
        obj4.addPostModifier("unterdurchschnittlich vertreten");
        sentence4.setFeature(Feature.TENSE, Tense.PAST);
        
        sentence4.addFrontModifier("gleichzeitig");
        
    	String output4 = realiser.realiseSentence(sentence4);
    	Assertions.assertEquals("Gleichzeitig waren Titel aus Italien und der Schweiz unterdurchschnittlich vertreten.", output4);
    }
    	
    public void scopeDocB_4_3() {
    	//System.out.println("\n---------------------------- SECTION B4.3 ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("unternehmen");
        subject1.addComplement("aus");
        subject1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        NPPhraseSpec object1 = nlgFactory.createNounPhrase("der Sektor");
        NPPhraseSpec object1_1 = nlgFactory.createNounPhrase("Pharmazeutika");
        NPPhraseSpec object1_2 = nlgFactory.createNounPhrase("Biotechnologie");
        NPPhraseSpec object1_3 = nlgFactory.createNounPhrase("Spezialchemikalien");
        CoordinatedPhraseElement obj1 = nlgFactory.createCoordinatedPhrase(object1_1, object1_2);
        obj1.addCoordinate(object1_3);
        object1.addPostModifier(obj1);
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("sein");
        verb1.addModifier("übergewichtet");
        
        if(obj1.checkIfPlural()) {
        	object1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        }
        
        subject1.setIndirectObject(object1);
        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);
        sentence1.setFeature(Feature.TENSE, Tense.PAST);
                
    	String output1 = realiser.realiseSentence(sentence1);
    	Assertions.assertEquals("Unternehmen aus den Sektoren Pharmazeutika, Biotechnologie und Spezialchemikalien waren übergewichtet.", output1);

        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("wir");
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("legen");
        NPPhraseSpec object2_1 = nlgFactory.createNounPhrase("ein Schwerpunkt");
        object2_1.addComplement("auf Unternehmen aus");
        NPPhraseSpec object2_2 = nlgFactory.createNounPhrase("der bereich");
        NPPhraseSpec object2_3 = nlgFactory.createNounPhrase("Pharmazeutika");
        NPPhraseSpec object2_4 = nlgFactory.createNounPhrase("Biotechnologie");
        CoordinatedPhraseElement obj2 = nlgFactory.createCoordinatedPhrase(object2_3, object2_4);
        object2_2.addPostModifier(obj2);
        
        if(obj2.checkIfPlural()) {
        	object2_2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        }

        sentence2.setSubject(subject2);
        sentence2.setVerb(verb2);
        verb2.setObject(object2_1);
        object2_1.setIndirectObject(object2_2);
        sentence2.setFeature(Feature.TENSE, Tense.PAST);
                
    	String output2 = realiser.realiseSentence(sentence2);
    	Assertions.assertEquals("Wir legten einen Schwerpunkt auf Unternehmen aus den Bereichen Pharmazeutika und Biotechnologie.", output2);

    	SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("Unternehmen");
        subject3.addComplement("aus");
        subject3.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        NPPhraseSpec object3 = nlgFactory.createNounPhrase("die Branche");
        NPPhraseSpec object3_3 = nlgFactory.createNounPhrase("Regionbanken");
        object3.addPostModifier(object3_3);
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("sein");
        verb3.addPreModifier("untergewichtet");
        
        subject3.setIndirectObject(object3);
        sentence3.setSubject(subject3);
        sentence3.setVerb(verb3);
        sentence3.setFeature(Feature.TENSE, Tense.PAST);
        
    	sentence3.setFeature(Feature.COMPLEMENTISER, "während");
    	sentence1.addComplement(sentence3);
                
    	String output3 = realiser.realiseSentence(sentence1);
    	Assertions.assertEquals("Unternehmen aus den Sektoren Pharmazeutika, Biotechnologie und Spezialchemikalien waren übergewichtet, "
    			+ "während Unternehmen aus der Branche Regionbanken untergewichtet waren.", output3);

    	SPhraseSpec sentence5 = nlgFactory.createClause();
        NPPhraseSpec subject5 = nlgFactory.createNounPhrase("Unternehmen");
        subject5.addComplement("aus");
        subject5.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        NPPhraseSpec object5 = nlgFactory.createNounPhrase("die Branche");
        NPPhraseSpec object5_3 = nlgFactory.createNounPhrase("Regionalbanken");
        object5.addPostModifier(object5_3);
        VPPhraseSpec verb5 = nlgFactory.createVerbPhrase("sein");
        verb5.addPreModifier("untergewichtet");
    	
        subject5.setIndirectObject(object5);
        sentence5.setSubject(subject5);
        sentence5.setVerb(verb5);
        sentence5.setFeature(Feature.TENSE, Tense.PAST);
    	
        SPhraseSpec sentence4 = nlgFactory.createClause();
        NPPhraseSpec subject4 = nlgFactory.createNounPhrase("wir");
        VPPhraseSpec verb4 = nlgFactory.createVerbPhrase("legen");
        NPPhraseSpec object4_1 = nlgFactory.createNounPhrase("einen Schwerpunkt");
        verb4.addPostModifier(object4_1);
        object4_1.addComplement("auf Unternehmen aus");
        NPPhraseSpec object4_2 = nlgFactory.createNounPhrase("der bereich");
        NPPhraseSpec object4_3 = nlgFactory.createNounPhrase("Pharmazeutika");
        object4_2.addPostModifier(object4_3);

        sentence4.setSubject(subject4);
        sentence4.setVerb(verb4);
        object4_1.setIndirectObject(object4_2);
        object4_1.setFeature(InternalFeature.CASE, DiscourseFunction.OBJECT);
        sentence4.setFeature(Feature.TENSE, Tense.PAST);

        sentence5.setFeature(Feature.COMPLEMENTISER, "wohingegen");
        sentence4.addComplement(sentence5);
        String output4 = realiser.realiseSentence(sentence4);
    	Assertions.assertEquals("Wir legten einen Schwerpunkt auf Unternehmen aus dem Bereich Pharmazeutika, wohingegen Unternehmen aus der Branche Regionalbanken untergewichtet waren.", output4);
    }  
    
    public void reportSampeleSentences() {
    	//System.out.println("\n---------------------------- FURTHER TEST SENTENCES FROM REPORTS ---------------------------\n");
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("der fonds");
        VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("bleiben");
        verb1.addModifier("schwerpunktmäßig");

        NPPhraseSpec object1_1 = nlgFactory.createNounPhrase("Standardwert");
        object1_1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        object1_1.addPreModifier("in");
        object1_1.addModifier("deutsch");
        NPPhraseSpec object1_2 = nlgFactory.createNounPhrase("Unternehmen");
        object1_2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        object1_2.addModifier("klein");
        object1_2.addPostModifier("investiert");
        CoordinatedPhraseElement obj1 = nlgFactory.createCoordinatedPhrase(object1_1, object1_2);
        obj1.setFeature(Feature.CONJUNCTION, "sowie");
        
        sentence1.setSubject(subject1);
        sentence1.setVerb(verb1);
        sentence1.setObject(obj1);
        sentence1.setFeature(Feature.TENSE, Tense.PAST);
                
    	String output1 = realiser.realiseSentence(sentence1);
    	Assertions.assertEquals("Der Fonds blieb schwerpunktmäßig in deutsche Standardwerte sowie kleine Unternehmen investiert.", output1);
    	
        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec object2 = nlgFactory.createNounPhrase("die drei größten Positionen");
        NPPhraseSpec noun2 = nlgFactory.createNounPhrase("der Fonds");
        noun2.setFeature(InternalFeature.CASE, DiscourseFunction.GENITIVE);
        object2.setComplement(noun2);
        object2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");

        NPPhraseSpec coord2_1 = nlgFactory.createNounPhrase("Bayer");
        coord2_1.setFeature(LexicalFeature.PROPER, true);
        NPPhraseSpec modifier2_1 = nlgFactory.createNounPhrase("eine Firma");
        modifier2_1.addPostModifier("aus dem Pharmasektor");
        modifier2_1.setFeature(Feature.APPOSITIVE, true);
        coord2_1.addPostModifier(modifier2_1);
        NPPhraseSpec coord2_2 = nlgFactory.createNounPhrase("SAP");
        coord2_2.setFeature(LexicalFeature.PROPER, true);
        NPPhraseSpec modifier2_2 = nlgFactory.createNounPhrase("ein Betreiber von Fahrerassistenzsystemen");
        modifier2_2.setFeature(Feature.APPOSITIVE, true);
        coord2_2.addPostModifier(modifier2_2);
        NPPhraseSpec coord2_3 = nlgFactory.createNounPhrase("Infineon Technologies");
        coord2_3.setFeature(LexicalFeature.PROPER, true);
        CoordinatedPhraseElement subject2 = nlgFactory.createCoordinatedPhrase();
        subject2.addCoordinate(coord2_1);
        subject2.addCoordinate(coord2_2);
        subject2.addCoordinate(coord2_3);
        subject2.addPreModifier("Ende März");
        
        sentence2.setFrontModifier(object2);
        sentence2.setVerb(verb2);
        sentence2.setSubject(subject2);
        sentence2.setFeature(Feature.TENSE, Tense.PAST);
                
    	String output2 = realiser.realiseSentence(sentence2);
    	Assertions.assertEquals("Die drei größten Positionen des Fonds waren Ende März Bayer, eine Firma aus dem Pharmasektor, "
    			+ "SAP, ein Betreiber von Fahrerassistenzsystemen, und Infineon Technologies.", output2);
    	
        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("der fonds");
        VPPhraseSpec verb3 = nlgFactory.createVerbPhrase("sein");
        NPPhraseSpec object3_1 = nlgFactory.createNounPhrase("die USA");
        NPPhraseSpec object3_2 = nlgFactory.createNounPhrase("die schweiz");
        NPPhraseSpec object3_3 = nlgFactory.createNounPhrase("die niederlande");
        NPPhraseSpec object3_4 = nlgFactory.createNounPhrase("die Russische Föderation");
        NPPhraseSpec object3_5 = nlgFactory.createNounPhrase("die Tschechische Republik");
        
        CoordinatedPhraseElement obj3 = nlgFactory.createCoordinatedPhrase();
        obj3.addPreModifier("in Unternehmen aus");
        obj3.addCoordinate(object3_1);  
        obj3.addCoordinate(object3_2);
        obj3.addCoordinate(object3_3);
        obj3.addCoordinate(object3_4);
        obj3.addCoordinate(object3_5);
 
        subject3.addPostModifier("übergewichtet");
        subject3.setIndirectObject(obj3);
        sentence3.setSubject(subject3);
        sentence3.setVerb(verb3);
        sentence3.setFeature(Feature.TENSE, Tense.PAST);
        sentence3.addFrontModifier("auf regionaler Ebene");
        
    	String output3 = realiser.realiseSentence(sentence3);
    	Assertions.assertEquals("Auf regionaler Ebene war der Fonds in Unternehmen aus den USA, der Schweiz, den Niederlanden, "
    			+ "der Russischen Föderation und der Tschechischen Republik übergewichtet.", output3);
    }
    
    public void sectorAndCountryAttributes() {
    	//System.out.println("\n---------------------------- TEST SECTOR AND COUNTRY ATTRIBUTES ---------------------------\n");    	
        SPhraseSpec sentence1 = nlgFactory.createClause();
        NPPhraseSpec subject1 = nlgFactory.createNounPhrase("ein unternehmen");
        AdjPhraseSpec adjective1 = nlgFactory.createAdjectivePhrase("deutsch");
        subject1.addModifier(adjective1);
        sentence1.setSubject(subject1);
    	String output1 = realiser.realiseSentence(sentence1);
    	Assertions.assertEquals("Ein deutsches Unternehmen.", output1);
    	
        SPhraseSpec sentence2 = nlgFactory.createClause();
        NPPhraseSpec subject2 = nlgFactory.createNounPhrase("ein firma");
        subject2.addModifier("deutsch");
        sentence2.setSubject(subject2);
    	String output2 = realiser.realiseSentence(sentence2);
    	Assertions.assertEquals("Eine deutsche Firma.", output2);
    	
        SPhraseSpec sentence3 = nlgFactory.createClause();
        NPPhraseSpec subject3 = nlgFactory.createNounPhrase("ein unternehmen");
        subject3.addModifier("in Deutschland");
        subject3.addModifier("ansässig");
        sentence3.setSubject(subject3);
    	String output3 = realiser.realiseSentence(sentence3);
    	Assertions.assertEquals("Ein in Deutschland ansässiges Unternehmen.", output3);
    	
        SPhraseSpec sentence4 = nlgFactory.createClause();
        NPPhraseSpec subject4 = nlgFactory.createNounPhrase("SAP");
        NPPhraseSpec addition4 = nlgFactory.createNounPhrase("ein unternehmen");
        addition4.addModifier("deutsch");
        subject4.addPostModifier(addition4);
        sentence4.setSubject(subject4);
    	String output4 = realiser.realiseSentence(sentence4);
    	Assertions.assertEquals("SAP ein deutsches Unternehmen.", output4);
    	
        SPhraseSpec sentence4_1 = nlgFactory.createClause();
        NPPhraseSpec object4_1 = nlgFactory.createNounPhrase("SAP");
        NPPhraseSpec addition4_1 = nlgFactory.createNounPhrase("ein unternehmen");
        addition4_1.addModifier("deutsch");
        object4_1.addPreModifier("von");
        object4_1.addPostModifier(addition4_1);
        sentence4_1.setIndirectObject(object4_1);
    	String output4_1 = realiser.realiseSentence(sentence4_1);
    	Assertions.assertEquals("Von SAP einem deutschen Unternehmen.", output4_1);
    	
        SPhraseSpec sentence5 = nlgFactory.createClause();
        VPPhraseSpec verb5 = nlgFactory.createVerbPhrase("sein");
        NPPhraseSpec company1 = nlgFactory.createNounPhrase("SAP");
        NPPhraseSpec company2 = nlgFactory.createNounPhrase("Siemens");
        CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
        coord.addCoordinate(company1);
        coord.addCoordinate(company2);
        NPPhraseSpec addition5 = nlgFactory.createNounPhrase("die gesellschaft");
        addition5.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
        addition5.addModifier("beide");
        addition5.addModifier("australisch");
        addition5.setFeature(Feature.APPOSITIVE, true);
        coord.addPostModifier(addition5);
        verb5.addPostModifier("groß");
        sentence5.setSubject(coord);
        sentence5.setVerb(verb5);
    	String output5 = realiser.realiseSentence(sentence5);
    	Assertions.assertEquals("SAP und Siemens, die beiden australischen Gesellschaften, sind groß.", output5);
    	
        SPhraseSpec sentence6 = nlgFactory.createClause();
        NPPhraseSpec subject6 = nlgFactory.createNounPhrase("McMoRan");
        NPPhraseSpec addition6 = nlgFactory.createNounPhrase("ein unternehmen");
        NPPhraseSpec object6 = nlgFactory.createNounPhrase("die USA");
        object6.setFeature(InternalFeature.CASE, DiscourseFunction.INDIRECT_OBJECT);
        addition6.addModifier("in");
        addition6.addModifier(object6);
        addition6.addModifier("ansässig");
        addition6.setFeature(Feature.APPOSITIVE, true);
        subject6.addPostModifier(addition6);
        sentence6.setSubject(subject6);
        sentence6.setVerb(verb5);
    	String output6 = realiser.realiseSentence(sentence6);
    	Assertions.assertEquals("McMoRan, ein in den USA ansässiges Unternehmen, ist groß.", output6);
    }
    
    public void scopeDoc4_2_new() {  
		SPhraseSpec sentence1 = nlgFactory.createClause();
		sentence1.setFrontModifier("Auf regionaler Ebene");
		NPPhraseSpec subject1 = nlgFactory.createNounPhrase("wir");
		subject1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
		VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("investieren");      

		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
        NPPhraseSpec object3_1 = nlgFactory.createNounPhrase("Deutschland");
        NPPhraseSpec object3_2 = nlgFactory.createNounPhrase("Irland");
        NPPhraseSpec object3_3 = nlgFactory.createNounPhrase("Frankreich");
        coord.addCoordinate(object3_1);  
       	coord.addCoordinate(object3_2);
        coord.addCoordinate(object3_3);
        
		coord.addPreModifier("schwerpunktmäßig in Unternehmen aus");
		sentence1.setSubject(subject1);
		sentence1.setIndirectObject(coord);
		sentence1.setVerb(verb1);
		sentence1.setFeature(Feature.TENSE, Tense.PAST);
		
		String output1 = realiser.realiseSentence(sentence1);
		Assertions.assertEquals("Auf regionaler Ebene investierten wir schwerpunktmäßig in Unternehmen aus Deutschland, Irland und Frankreich.", output1);


		SPhraseSpec sentence2 = nlgFactory.createClause();
		NPPhraseSpec subject2 = nlgFactory.createNounPhrase("Titel");
		subject2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
		VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");

		CoordinatedPhraseElement coord2 = nlgFactory.createCoordinatedPhrase();
        NPPhraseSpec object2_1 = nlgFactory.createNounPhrase("Spanien");
        NPPhraseSpec object2_2 = nlgFactory.createNounPhrase("die Schweiz");
        NPPhraseSpec object2_3 = nlgFactory.createNounPhrase("Grossbritannien");
        coord2.addCoordinate(object2_1);  
       	coord2.addCoordinate(object2_2);
        coord2.addCoordinate(object2_3);

		coord2.addPreModifier("aus");
		sentence2.setSubject(subject2);
		sentence2.setVerb(verb2);
        sentence2.setIndirectObject(coord2);
        coord2.addPostModifier("unterdurchschnittlich vertreten");
		sentence2.setFeature(Feature.TENSE, Tense.PAST);  
		sentence2.addFrontModifier("gleichzeitig");

		String output2 = realiser.realiseSentence(sentence2);
		Assertions.assertEquals("Gleichzeitig waren Titel aus Spanien, der Schweiz und Grossbritannien unterdurchschnittlich vertreten.", output2);
    }
    
    public void scopeDoc4_2_new2() { 
		SPhraseSpec sentence1 = nlgFactory.createClause();
		sentence1.setFrontModifier("Auf regionaler Ebene");
		NPPhraseSpec subject1 = nlgFactory.createNounPhrase("wir");
		subject1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
		VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("investieren");      

		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
        NPPhraseSpec object3_1 = nlgFactory.createNounPhrase("Deutschland");
        NPPhraseSpec object3_2 = nlgFactory.createNounPhrase("Irland");
        NPPhraseSpec object3_3 = nlgFactory.createNounPhrase("Frankreich");
        coord.addCoordinate(object3_1);  
       	coord.addCoordinate(object3_2);
        coord.addCoordinate(object3_3);
        
		coord.addPreModifier("schwerpunktmäßig in Unternehmen aus");
		sentence1.setSubject(subject1);
		sentence1.setIndirectObject(coord);
		sentence1.setVerb(verb1);
		sentence1.setFeature(Feature.TENSE, Tense.PAST);
		
		SPhraseSpec sentence2 = nlgFactory.createClause();
		NPPhraseSpec subject2 = nlgFactory.createNounPhrase("Titel");
		subject2.addComplement("aus");
		subject2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
		VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");
		verb2.addPreModifier("untergewichtet");
		
		CoordinatedPhraseElement coord2 = nlgFactory.createCoordinatedPhrase();
        NPPhraseSpec object2_1 = nlgFactory.createNounPhrase("Spanien");
        NPPhraseSpec object2_2 = nlgFactory.createNounPhrase("die Schweiz");
        NPPhraseSpec object2_3 = nlgFactory.createNounPhrase("Grossbritannien");
        coord2.addCoordinate(object2_1);  
       	coord2.addCoordinate(object2_2);
        coord2.addCoordinate(object2_3);
		
		subject2.setIndirectObject(coord2);
		sentence2.setSubject(subject2);
		sentence2.setVerb(verb2);
		
		sentence2.setFeature(Feature.TENSE, Tense.PAST);
		sentence2.setFeature(Feature.COMPLEMENTISER, "wohingegen");
		sentence1.addComplement(sentence2);

		String output1 = realiser.realiseSentence(sentence1);
		Assertions.assertEquals("Auf regionaler Ebene investierten wir schwerpunktmäßig in Unternehmen aus Deutschland, Irland und Frankreich, "
				+ "wohingegen Titel aus Spanien, der Schweiz und Grossbritannien untergewichtet waren.", output1);

    }
    
    public void scopeDoc4_3_new() {
		CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
		SPhraseSpec sentence1 = nlgFactory.createClause();
		NPPhraseSpec subject1 = nlgFactory.createNounPhrase("unternehmen");
		subject1.addComplement("aus");
		subject1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
		NPPhraseSpec object1 = nlgFactory.createNounPhrase("der Sektor");
		VPPhraseSpec verb1 = nlgFactory.createVerbPhrase("sein");
		verb1.addModifier("übergewichtet");

        NPPhraseSpec object2_1 = nlgFactory.createNounPhrase("IT");
        NPPhraseSpec object2_2 = nlgFactory.createNounPhrase("Roh-, Hilfs- & Betriebsstoffe");
        NPPhraseSpec object2_3 = nlgFactory.createNounPhrase("Immobilien");
        coord.addCoordinate(object2_1);  
       	coord.addCoordinate(object2_2);
        coord.addCoordinate(object2_3);

		if(coord.checkIfPlural()) {
			object1.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
		}

		object1.addPostModifier(coord);
		subject1.setIndirectObject(object1);
		sentence1.setSubject(subject1);
		sentence1.setVerb(verb1);
		sentence1.setFeature(Feature.TENSE, Tense.PAST);

		/**
		 * For underweight list
		 */
		SPhraseSpec sentence2 = nlgFactory.createClause();
		NPPhraseSpec subject2 = nlgFactory.createNounPhrase("Unternehmen");
		subject2.addComplement("aus");
		subject2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
		NPPhraseSpec object2 = nlgFactory.createNounPhrase("die Branche");
		VPPhraseSpec verb2 = nlgFactory.createVerbPhrase("sein");
		verb2.addPreModifier("untergewichtet");

		CoordinatedPhraseElement coord2 = nlgFactory.createCoordinatedPhrase();

        NPPhraseSpec object3_1 = nlgFactory.createNounPhrase("Versorger");
        NPPhraseSpec object3_2 = nlgFactory.createNounPhrase("Finanzen");
        NPPhraseSpec object3_3 = nlgFactory.createNounPhrase("Nicht-Basiskonsumgüter");
        coord2.addCoordinate(object3_1);  
       	coord2.addCoordinate(object3_2);
        coord2.addCoordinate(object3_3);

		object2.addPostModifier(coord2);

		if(coord2.checkIfPlural()) {
			object2.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
		}
		
		subject2.setIndirectObject(object2);
		sentence2.setSubject(subject2);
		sentence2.setVerb(verb2);
		sentence2.setFeature(Feature.TENSE, Tense.PAST);
		sentence2.setFeature(Feature.COMPLEMENTISER, "während");
		sentence1.addComplement(sentence2);

		String output1 = realiser.realiseSentence(sentence1);
		Assertions.assertEquals("Unternehmen aus den Sektoren IT, Roh-, Hilfs- & Betriebsstoffe und Immobilien waren übergewichtet, "
				+ "während Unternehmen aus den Branchen Versorger, Finanzen und Nicht-Basiskonsumgüter untergewichtet waren.", output1);
    }

    public void additionalTest() {
        NPPhraseSpec companyNoun = nlgFactory.createNounPhrase("der Chemiekonzern");
        AdjPhraseSpec adjective = nlgFactory.createAdjectivePhrase("diversifiziert");
        companyNoun.addModifier(adjective);

        NPPhraseSpec companyNoun2 = nlgFactory.createNounPhrase("der Titel");
        companyNoun2.addModifier(adjective);

        NPPhraseSpec companyNoun3 = nlgFactory.createNounPhrase("der dasfdafsafa");
        companyNoun3.addModifier(adjective);

        CoordinatedPhraseElement coord = nlgFactory.createCoordinatedPhrase();
        coord.addCoordinate(companyNoun);
        coord.addCoordinate(companyNoun2);
        coord.addCoordinate(companyNoun3);

        SPhraseSpec sentence = nlgFactory.createClause();
        sentence.setIndirectObject(coord);
        System.out.println(realiser.realise(sentence));
    }
}
