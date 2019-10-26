import test.*;

import java.io.IOException;

import simplenlgde.framework.NLGFactory;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;

public class Main {

	public static void main(String[] args) throws IOException {
		
	    Lexicon lexicon;
	    NLGFactory nlgFactory;
	    Realiser realiser;
	    
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);

        /*RealiserTest test1 = new RealiserTest(lexicon, nlgFactory, realiser);

		ScopeDocTest test2 = new ScopeDocTest(lexicon, nlgFactory, realiser);
		test2.scopeDocB_4_2();
		test2.scopeDocB_4_3();
		test2.reportSampeleSentences();
		test2.sectorAndCountryAttributes();
		test2.scopeDoc4_2_new();
		test2.scopeDoc4_2_new2();
		test2.scopeDoc4_3_new();

		test1.testPluralizationLexicon();
		test1.testregularVerbInflection();
		test1.testirregularVerbInflection();
		test1.testregularVerbInflectionPreterite();
		test1.testirregularVerbInflectionPreterite();
		test1.testAdjectiveArticleInflecion();
		test1.testCommaRules();
		test1.testGenitiveNouns();
		test1.testDativeNouns();
		test1.testAccusativeNouns();
		test1.testModifiers();
		test1.separableVerbsPositioning();
		test1.basicSyntaxTest();
		test1.testPassivePresVerbInflection();
		test1.testPassiveProgressivePresVerbInflection();
		test1.testPassivePretVerbInflection();
		test1.adjEnumerationTest();
		test1.testVerbPreModifiers();
		test1.adjAdvTest();
		test1.testSpecialCharacters();
		test1.testFutureVerbInflection();
		test1.testPerfectVerbInflection();
		test1.testPerfectProgressiveVerbInflection();
		test1.testModifierPositioning();
		test1.testPostModsVsComplements();

		ReportTest1 reportTest1 = new ReportTest1(lexicon, nlgFactory, realiser);
		reportTest1.wertentwicklung();
		reportTest1.anlagestrategie();

		SaToSTest satosTest = new SaToSTest(lexicon, nlgFactory, realiser);
		satosTest.testToS1();
		satosTest.testToS2();
		satosTest.testToS3();
		satosTest.testToS4();
		satosTest.testToS5();
		satosTest.testToS6();
		satosTest.testToS7();

		test1.testPerfectWordOrder();
		test1.testApposition();
		test1.testUserSetGender();
		
		QuestionTest test = new QuestionTest(lexicon, nlgFactory, realiser);
        test.yesNoTest();
        test.yesNoTest2();
        test.yesNoTest3();
	}
}