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
 * The Original Code is "Simplenlg".
 *
 * The Initial Developer of the Original Code is Ehud Reiter, Albert Gatt and Dave Westwater.
 * Portions created by Ehud Reiter, Albert Gatt and Dave Westwater are Copyright (C) 2010-11 The University of Aberdeen. All Rights Reserved.
 *
 * Contributor(s): Ehud Reiter, Albert Gatt, Dave Wewstwater, Roman Kutlak, Margaret Mitchell.
 *
 * Contributor(s) German version: Kira Klimt, Daniel Braun, Technical University of Munich
 *
 */

package simplenlgde.framework;

import java.util.Arrays;
import java.util.List;
import java.lang.String;

import simplenlgde.lexicon.Lexicon;
import simplenlgde.phrasespec.*;
import simplenlgde.features.*;


/**
 * <p>
 * This class contains methods for creating syntactic phrases. These methods
 * should be used instead of directly invoking new on SPhraseSpec, etc.
 * <p>
 * The phrase factory should be linked to s lexicon if possible (although it
 * will work without one)
 * </p>
 *
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 */

public class NLGFactory {

	/**
	 * The lexicon to be used with this factory.
	 */
	private Lexicon lexicon;

	/**
	 * The list of German pronouns.
	 */
	private static final List<String> PRONOUNS = Arrays.asList(
			"ich", "du", "er", "sie", "es", "wir", "ihr", "sie",
			"sich", "mich", "dich", "sich", "uns", "euch",
			"mein", "dein", "unser", "ihr",
			"der", "die", "das", "jener", "welcher",
			"dieser", "dieses", "diese", "derselbe", "der", "jener", "deren",
			"wer", "was", "wessen", "welcher", "wem",
			"jemand", "alle", "einer", "manche", "man", "wer", "etwas", "einige", "andere", "jeder", "jedermann",
			"keiner", "niemand", "nichts",
			"derjenige", "diejenige", "dasjenige", "derselbe", "dieselbe", "dasselbe",
			"dessen", "dem", "welcher", "deren", "denen", "auf den", "ihnen", "ihm", "ihr"
			);

	/**
	 * The list of first-person German pronouns.
	 */
	private static final List<String> FIRST_PRONOUNS = Arrays.asList("ich", "wir");

	/**
	 * The list of second person German pronouns.
	 */
	private static final List<String> SECOND_PRONOUNS = Arrays.asList("du", "ihr");

	/**
	 * The list of plural German pronouns. "sie" can be both plural and singular
	 */
	private static final List<String> PLURAL_PRONOUNS = Arrays.asList("wir", "ihr", "alle");

	/**
	 * The list of German indefinite pronouns, which have to be conjugated.
	 */
	private static final List<String> INDEFINITE_PRONOUNS = Arrays.asList(
			"beide", "beides", "beiden", "beider", "beidem",
			"einer", "eines", "einem", "einen",
			"jemand", "jemands", "jemandes", "jemanden", "jemandem,",
			"niemand", "niemands", "niemenades", "niemanden", "niemandem",
			"alle", "alles", "allen", "allem", "aller",
			"einige", "einiger", "einigen", "einigem", "einiges",
			"manche", "mancher", "manchen", "manchem", "manches",
			"irgendein", "irgendeines", "irgendeine", "irgendeinem", "irgendeinen", "irgendeiner",
			"jeder", "jede", "jedes", "jedem", "jeden");

	/**
	 * The list of German posessive pronouns, which have to be conjugated.
	 */
	private static final List<String> POSSESSIVE_PRONOUNS = Arrays.asList("sein", "seines", "seinem", "seinen", "seines");
	/**
	 * The list of definite articles.
	 */
	private static final List<String> ARTICLES_DEF = Arrays.asList("der", "die", "das", "den", "dem", "des");

	/**
	 * The list of definite articles merged with a preposition.
	 */
	private static final List<String> MERGED_ARTICLES_DEF = Arrays.asList(
			"am", "beim", "im", "vom", "zum",
			"zur", "ans", "ins", "aufs", "durchs",
			"fürs", "hintern", "hinterm", "hinters",
			"übern", "überm", "übers", "ums",
			"untern", "unterm", "unters", "vorm", "vors");

	/**
	 * The list of indefinite articles.
	 */
	private static final List<String> ARTICLES_INDEF = Arrays.asList("ein", "eine", "eines", "einer", "einem", "einen");

	/**
	 * The list of modal verbs.
	 */
	private static final List<String> VERBS_MODAL = Arrays.asList("dürfen", "können", "mögen", "müssen",
			"sollen", "wollen");

	/**
	 * regex for determining if a string is a single word or not, suporting also latin characters like German umlaute
	 **/
	private static final String WORD_REGEX = "\\w*||\\p{IsLatin}+";
	private static final String WORD_BLANK_REGEX = "\\w*||\\p{IsLatin}+||\\s";

	/**
	 * The list of common adjective suffixes
	 */
	private static final String ADJ_REGEX = ".*(bar|ig|isch|lich|los|sam|voll)$";

	/**
	 * Creates a new phrase factory with no associated lexicon.
	 */
	public NLGFactory() {
		this(null);
	}

	/**
	 * Creates a new phrase factory with the associated lexicon.
	 *
	 * @param newLexicon the <code>Lexicon</code> to be used with this factory.
	 */
	public NLGFactory(Lexicon newLexicon) {
		setLexicon(newLexicon);
	}

	/**
	 * Sets the lexicon to be used by this factory. Passing a parameter of
	 * <code>null</code> will remove any existing lexicon from the factory.
	 *
	 * @param newLexicon the new <code>Lexicon</code> to be used.
	 */
	public void setLexicon(Lexicon newLexicon) {
		this.lexicon = newLexicon;
	}

	/**
	 * Creates a blank canned text phrase with no text.
	 *
	 * @return a <code>PhraseElement</code> representing this phrase.
	 */
	public NLGElement createStringElement() {
		return createStringElement(null);
	}

	/**
	 * Creates a canned text phrase with the given text.
	 *
	 * @param text the canned text.
	 * @return a <code>PhraseElement</code> representing this phrase.
	 */
	public NLGElement createStringElement(String text) {
		return new StringElement(text);
	}

	/**
	 * Creates a blank clause with no subject, verb or objects.
	 *
	 * @return a <code>SPhraseSpec</code> representing this phrase.
	 */
	public SPhraseSpec createClause() {
		return createClause(null, null, null);
	}

	/**
	 * Creates a clause with the given subject and verb but no objects.
	 *
	 * @param subject the subject for the clause as a <code>NLGElement</code> or
	 *                <code>String</code>. This forms a noun phrase.
	 * @param verb    the verb for the clause as a <code>NLGElement</code> or
	 *                <code>String</code>. This forms a verb phrase.
	 * @return a <code>SPhraseSpec</code> representing this phrase.
	 */
	public SPhraseSpec createClause(Object subject, Object verb) {
		return createClause(subject, verb, null);
	}

	/**
	 * Creates a clause with the given subject, verb or verb phrase and direct
	 * object but no indirect object.
	 *
	 * @param subject      the subject for the clause as a <code>NLGElement</code> or
	 *                     <code>String</code>. This forms a noun phrase.
	 * @param verb         the verb for the clause as a <code>NLGElement</code> or
	 *                     <code>String</code>. This forms a verb phrase.
	 * @param directObject the direct object for the clause as a <code>NLGElement</code>
	 *                     or <code>String</code>. This forms a complement for the
	 *                     clause.
	 * @return a <code>SPhraseSpec</code> representing this phrase.
	 */
	public SPhraseSpec createClause(Object subject, Object verb, Object directObject) {

		SPhraseSpec phraseElement = new SPhraseSpec(this);

		if (verb != null) {
			// AG: fix here: check if "verb" is a VPPhraseSpec or a Verb
			if (verb instanceof PhraseElement) {
				phraseElement.setVerbPhrase((PhraseElement) verb);
			} else {
				phraseElement.setVerb(verb);
			}
		}

		if (subject != null)
			phraseElement.setSubject(subject);

		if (directObject != null) {
			phraseElement.setObject(directObject);
		}

		return phraseElement;
	}

	/**
	 * Creates a blank verb phrase with no main verb.
	 *
	 * @return a <code>VPPhraseSpec</code> representing this phrase.
	 */
	public VPPhraseSpec createVerbPhrase() {
		return createVerbPhrase(null);
	}

	/**
	 * Creates a verb phrase wrapping the main verb given. If a
	 * <code>String</code> is passed in then some parsing is done to see if the
	 * verb contains a particle, for example <em>fall down</em>. The first word
	 * is taken to be the verb while all other words are assumed to form the
	 * particle.
	 *
	 * @param verb the verb to be wrapped.
	 * @return a <code>VPPhraseSpec</code> representing this phrase.
	 */
	public VPPhraseSpec createVerbPhrase(Object verb) {
		VPPhraseSpec phraseElement = new VPPhraseSpec(this);
		String split_verb = "";
		if(verb instanceof String && ((String) verb).contains(" ")) {
			String[] split = ((String) verb).split(" ");
			split_verb = split[split.length-1];
		}
		if(verb != null && VERBS_MODAL.contains(verb.toString().toLowerCase()) || VERBS_MODAL.contains(split_verb.toString().toLowerCase())) {
			phraseElement.setFeature(Feature.CONTAINS_MODAL, true);
		}
		phraseElement.setVerb(verb);
		setPhraseHead(phraseElement, phraseElement.getVerb());
		return phraseElement;
	}

	/**
	 * A helper method to set the head feature of the phrase.
	 *
	 * @param phraseElement the phrase element.
	 * @param headElement   the head element.
	 */
	private void setPhraseHead(PhraseElement phraseElement, NLGElement headElement) {
		if (headElement != null) {
			phraseElement.setHead(headElement);
			headElement.setParent(phraseElement);
		}
	}

	/**
	 * this method creates an NLGElement from an object If object is null,
	 * return null If the object is already an NLGElement, it is returned
	 * unchanged Exception: if it is an InflectedWordElement, return underlying
	 * WordElement If it is a String which matches a lexicon entry or pronoun,
	 * the relevant WordElement is returned If it is a different String, a
	 * wordElement is created if the string is a single word Otherwise a
	 * StringElement is returned Otherwise throw an exception
	 *
	 * @param element  - object to look up
	 * @param category - default lexical category of object
	 * @return NLGelement
	 */
	public NLGElement createNLGElement(Object element, LexicalCategory category) {
		if (element == null)
			return null;

		// InflectedWordElement - return underlying word
		else if (element instanceof InflectedWordElement)
			return ((InflectedWordElement) element).getBaseWord();

		// StringElement - look up in lexicon if it is a word
		// otherwise return element
		if (element instanceof StringElement) {
			if (stringIsWord(((StringElement) element).getRealisation(), category))
				return createWord(((StringElement) element).getRealisation(), category);
			else
				return (StringElement) element;
		}

		// other NLGElement - return element
		else if (element instanceof NLGElement)
			return (NLGElement) element;

		// String - look up in lexicon if a word, otherwise return StringElement
		else if (element instanceof String) {
			NLGElement newWord = createWord(element, category);
			if (stringIsWord((String) element, category)) {
				// In case it's a noun (& not a pronoun), capitalize 1st letter
				String helpElement = (String) element;
				if (category == LexicalCategory.NOUN && !PRONOUNS.contains(helpElement.toLowerCase())) {
					element = helpElement.substring(0, 1).toUpperCase() + helpElement.substring(1);
				}
				if (((String) element).contains("-")) {
					String[] split = ((String) element).split("-");
					String lastOne = split[split.length-1];
					NLGElement lastWord = createWord(lastOne, category);
					if(lastWord.hasFeature(LexicalFeature.GENDER)) {
						newWord = createWord(element, category);
						newWord.setFeature(LexicalFeature.GENDER, lastWord.getFeature(LexicalFeature.GENDER));
						return newWord;
					}
				}
				return createWord(element, category);
			}
			else if (((String) element).contains(" ")) {
				newWord = checkForWordGroupLexeme(element, newWord);
			}

			else if (((String) element).contains("-") && category.equals(LexicalCategory.ADJECTIVE)) {
				return createWord(element, LexicalCategory.ADJECTIVE);
			}
			return newWord;
		}
		throw new IllegalArgumentException(element.toString() + " is not a valid type");
	}

	/**
	 * This method checks if a word is a word groups lexeme (e.g. "Russische Föderation",
	 * "Eulersche Zahl") and creates the NLGElement with category NOUN accordingly.
	 * If the word is not a word group lexeme, a plain String is created.
	 *
	 * @param element  - object to look up
	 * @param newWord - default object to store modified element
	 * @return NLGelement
	 */
	private NLGElement checkForWordGroupLexeme(Object element, NLGElement newWord) {
		// count how many words the string contains
		int countBlanks = ((String) element).length() - ((String) element).replace(" ", "").length();
		if (countBlanks == 1) {
			String part1 = ((String) element).split(" ")[0];
			String part2 = ((String) element).split(" ")[1];
			if (stringIsBaseForm(part1, LexicalCategory.ADJECTIVE) >= 0 && stringIsWord(part2, LexicalCategory.NOUN)) {
				newWord = createWord(element, LexicalCategory.NOUN);
			}
			// nouns consisting of 2 adjectives + noun, e.g. "Vereinigte Arabische Emirate"
		} else if (countBlanks == 2) {
			String part1 = ((String) element).split(" ")[0];
			String part2 = ((String) element).split(" ")[1];
			String part3 = ((String) element).split(" ")[2];
			if (stringIsBaseForm(part1, LexicalCategory.ADJECTIVE) >= 0 && stringIsBaseForm(part2, LexicalCategory.ADJECTIVE) >= 0 && stringIsWord(part3, LexicalCategory.NOUN)) {
				newWord = createWord(element, LexicalCategory.NOUN);
			} else {
				// create a plain String
				newWord = new StringElement((String) element);
			}
		} else {
			// create a plain String
			newWord = new StringElement((String) element);
		}
		return newWord;
	}

	/**
	 * this method checks if a word, the word without its last character, or the word without its last two characters
	 * is an adjective according to the lexicon. The check with removal of last characters is done in order to recognize inflected adjective forms,
	 * e.g. "russisch" (base form), but "RussischE Foederation" (inflected form in base form of noun)
	 *
	 * @param word     - word to look up
	 * @param category - lexical category (e.g. adjective) to look up
	 * @return an int, -1 if the word and its inflected forms are no entries in the dictionary with the given category, 0, if the word itself is an entry,
	 * 1, if the word the word without its last character is an entry, or 2, if the word without its last two characters is an entry
	 */
	protected int stringIsBaseForm(String word, LexicalCategory category) {
		if (!category.equals(LexicalCategory.NOUN)) {
			word = word.toLowerCase();
		}
		int whichIsAdjective = -1;
		if (word.length() > 2) {
			if (lexicon.hasWord(word, category))
				whichIsAdjective = 0;
			else if (lexicon.hasWord((word).substring(0, (word).length() - 1), category))
				whichIsAdjective = 1;
			else if (lexicon.hasWord((word).substring(0, (word).length() - 2), category))
				whichIsAdjective = 2;
		}
		return whichIsAdjective;
	}

	/**
	 * Returns true if string is a word.
	 * Looks for the word as it is and for the word with an upper case 1st letter,
	 * as German nouns usually begin with upper case letters.
	 *
	 * @param string
	 * @param category
	 * @return
	 */
	private boolean stringIsWord(String string, LexicalCategory category) {
		return lexicon != null
				&& (lexicon.hasWord(string, category)
						|| (lexicon.hasWord((string.substring(0, 1).toUpperCase() + string.substring(1)), category))
						|| PRONOUNS.contains(string.toLowerCase()) || (string.matches(WORD_REGEX)) || string.contains("-"));
	}

	/**
	 * Returns true if string is an adjective.
	 * Looks for the word as it is and for the word with an upper case 1st letter,
	 * as German nouns usually begin with upper case letters.
	 *
	 * @param string
	 * @return
	 */
	private boolean stringIsAdjective(String string) {
		return lexicon != null
				&& ((string.length() > 2) &&
						(lexicon.hasWord(string, LexicalCategory.ADJECTIVE)
								|| (lexicon.hasWord((string.substring(0, 1).toUpperCase() + string.substring(1)), LexicalCategory.ADJECTIVE))));
	}

	/**
	 * Creates a new element representing a word. If the word passed is already
	 * an <code>NLGElement</code> then that is returned unchanged. If a
	 * <code>String</code> is passed as the word then the factory will look up
	 * the <code>Lexicon</code> if one exists and use the details found to
	 * create a new <code>WordElement</code>.
	 *
	 * @param word     the base word for the new element. This can be a
	 *                 <code>NLGElement</code>, which is returned unchanged, or a
	 *                 <code>String</code>, which is used to construct a new
	 *                 <code>WordElement</code>.
	 * @param category the <code>LexicalCategory</code> for the word.
	 * @return an <code>NLGElement</code> representing the word.
	 */
	public NLGElement createWord(Object word, LexicalCategory category) {
		NLGElement wordElement = null;
		if (word instanceof NLGElement) {
			wordElement = (NLGElement) word;
		} else if (word instanceof String && this.lexicon != null) {
			wordElement = lexicon.lookupWord((String) word, category);
			if (PRONOUNS.contains(((String) word).toLowerCase())) {
				wordElement = new WordElement(((String) word).toLowerCase(), LexicalCategory.PRONOUN); //ignore pronouns in lexicon
				setPronounFeatures(wordElement, (String) word);
			}
			if (MERGED_ARTICLES_DEF.contains(word.toString().toLowerCase())) {
				wordElement.setCategory(LexicalCategory.ARTICLE_DEFINITE);
				wordElement.setFeature(InternalFeature.MERGED_ARTICLE, true);
			}
			if (ARTICLES_DEF.contains(word.toString().toLowerCase())) {
				wordElement.setCategory(LexicalCategory.ARTICLE_DEFINITE);
			} else if (ARTICLES_INDEF.contains(word.toString().toLowerCase())) {
				wordElement.setCategory(LexicalCategory.ARTICLE_INDEFINITE);
			} else if (INDEFINITE_PRONOUNS.contains(word.toString().toLowerCase())) {
				wordElement.setCategory(LexicalCategory.INDEFINITE_PRONOUN);
			} else if (word.toString().matches(ADJ_REGEX) && category == LexicalCategory.ANY) {
				wordElement.setCategory(LexicalCategory.ADJECTIVE);
			}
		}
		return wordElement;
	}

	public NPPhraseSpec createNounPhrase() {
		return createNounPhrase(null, null);
	}

	/**
	 * Creates a noun phrase with the given subject but no specifier.
	 *
	 * @param noun the subject of the phrase.
	 * @return a <code>NPPhraseSpec</code> representing this phrase.
	 */
	public NPPhraseSpec createNounPhrase(Object noun) {
		if (noun instanceof String) {
			/* if article is specified before noun, e.g. "Der Mann" */
			if (((String) noun).indexOf(" ") > -1 && ((String) noun).indexOf("&") == -1) {
				String spec = ((String) noun).substring(0, ((String) noun).indexOf(" "));
				String head = ((String) noun).substring(((String) noun).indexOf(" ") + 1);
				return createNounPhrase(spec, head);
			} else {
				return createNounPhrase(null, noun);
			}
		} else {
			if (noun instanceof NPPhraseSpec)
				return (NPPhraseSpec) noun;
			else
				return createNounPhrase(null, noun);
		}
	}

	/**
	 * Creates a noun phrase with the given specifier and subject.
	 *
	 * @param specifier the specifier or determiner for the noun phrase.
	 * @param noun      the subject of the phrase.
	 * @return a <code>NPPhraseSpec</code> representing this phrase.
	 */
	public NPPhraseSpec createNounPhrase(Object specifier, Object noun) {
		if (noun instanceof NPPhraseSpec)
			return (NPPhraseSpec) noun;
		NLGElement nounElement = null;
		NPPhraseSpec phraseElement = new NPPhraseSpec(this);
		// nouns consisting of an adjective + noun, e.g. "die Russische Föderaion"
		if (((String) noun).contains(" ") && stringIsWord(noun.toString().replaceAll(" ", ""),
				LexicalCategory.NOUN)) {
			nounElement = createWordGroupLexeme(specifier, noun, nounElement, phraseElement);
		} else {
			nounElement = createNLGElement(noun, LexicalCategory.NOUN);
		}
		if (nounElement == null) {
			nounElement = createNLGElement(noun, LexicalCategory.NOUN);
		}
		if (specifier != null) {
			if (phraseElement.getSpecifier() == null) {
				phraseElement.setSpecifier(specifier);
			}
			if (ARTICLES_DEF.contains(specifier.toString().toLowerCase()) || ARTICLES_INDEF.contains(specifier.toString().toLowerCase())
					|| MERGED_ARTICLES_DEF.contains(specifier.toString().toLowerCase())) {
				if (nounElement != null && ((nounElement.hasFeature(LexicalFeature.GENDER) && nounElement.getFeature(LexicalFeature.GENDER) == null)
						|| !nounElement.hasFeature(LexicalFeature.GENDER))) {
					setArticleGender(specifier, nounElement);
				}
			}
		}
		setPhraseHead(phraseElement, nounElement);
		return phraseElement;
	}

	/**
	 * This method creates a NounElement for a word groups lexeme (e.g. "Russische Föderation",
	 * "Eulersche Zahl").
	 *
	 * @param specifier the noun's specifier
	 * @param noun the word group lexeme
	 * @param nounElement the resulting NounElement
	 * @param phraseElement the resulting PhraseElement
	 * @return NLGelement
	 */
	private NLGElement createWordGroupLexeme(Object specifier, Object noun, NLGElement nounElement,
											 NPPhraseSpec phraseElement) {
		// count how many words the string contains
		int countBlanks = ((String) noun).length() - ((String) noun).replace(" ", "").length();

		if (countBlanks == 1) {
			String part1 = ((String) noun).split(" ")[0];
			String part2 = ((String) noun).split(" ")[1];
			if(part1 != null & part2 != null) {
				NLGElement adjectiveElement = null;
				if (!lexicon.hasWord(part1, LexicalCategory.NOUN) && (stringIsAdjective(part1.toLowerCase()) || stringIsAdjective(part1.toLowerCase().substring(0, (part1).length() - 1))
						|| stringIsAdjective(part1.toLowerCase().substring(0, (part1).length() - 2)))) {
					adjectiveElement = createWordFromBaseform(part1.toLowerCase(), LexicalCategory.ADJECTIVE);
				}
				if (stringIsWord(part2, LexicalCategory.NOUN) && adjectiveElement != null) {
					nounElement = createWordFromBaseform((part2.substring(0, 1).toUpperCase()
							+ part2.substring(1)), LexicalCategory.NOUN);
					if (MERGED_ARTICLES_DEF.contains(part1.toLowerCase())) {
						adjectiveElement = createWord(part1, LexicalCategory.ANY);
						if(specifier != null) {
							phraseElement.setSpecifier(specifier);
							phraseElement.getSpecifier().setFeature(InternalFeature.MERGED_ARTICLE, true);
						}
					} else {
						adjectiveElement.setFeature(InternalFeature.COMPOSITE, true);
					}
					if (part2.equalsIgnoreCase(nounElement.getFeatureAsString(LexicalFeature.PLURAL))) {
						adjectiveElement.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
						nounElement.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
					}
					phraseElement.addModifier(adjectiveElement);
				}
			}

		} else if (countBlanks == 2) {
			String part1 = ((String) noun).split(" ")[0];
			String part2 = ((String) noun).split(" ")[1];
			String part3 = ((String) noun).split(" ")[2];
			NLGElement adjectiveElement1 = null;
			NLGElement adjectiveElement2 = null;
			if (stringIsWord(part1, LexicalCategory.ANY) && stringIsWord(part2, LexicalCategory.ANY)
					&& stringIsWord(part3, LexicalCategory.ANY)) {
				if (stringIsAdjective(part1.toLowerCase()) || stringIsAdjective(part1.toLowerCase().substring(0, (part1).length() - 1))
						|| stringIsAdjective(part1.toLowerCase().substring(0, (part1).length() - 2))) {
					adjectiveElement1 = createWordFromBaseform(part1.toLowerCase(), LexicalCategory.ADJECTIVE);
				}
				if (stringIsAdjective(part2.toLowerCase()) || stringIsAdjective(part2.toLowerCase().substring(0, (part2).length() - 1))
						|| stringIsAdjective(part2.toLowerCase().substring(0, (part2).length() - 2))) {
					adjectiveElement2 = createWordFromBaseform(part2.toLowerCase(), LexicalCategory.ADJECTIVE);
				}
				if (stringIsWord(part3, LexicalCategory.NOUN) && adjectiveElement1 != null && adjectiveElement2 != null) {
					nounElement = createWordFromBaseform((part3.substring(0, 1).toUpperCase() + part3.substring(1)), LexicalCategory.NOUN);
					adjectiveElement1.setFeature(InternalFeature.COMPOSITE, true);
					phraseElement.addModifier(adjectiveElement1);
					adjectiveElement2.setFeature(InternalFeature.COMPOSITE, true);
					if (part3.equalsIgnoreCase(nounElement.getFeatureAsString(LexicalFeature.PLURAL))) {
						adjectiveElement1.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
						adjectiveElement2.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
						nounElement.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
					}
					phraseElement.addModifier(adjectiveElement2);
				} else {
					nounElement = createNLGElement(noun, LexicalCategory.NOUN);
				}
			} else {
				nounElement = createNLGElement(noun, LexicalCategory.NOUN);
			}
		} else {
			nounElement = createNLGElement(noun, LexicalCategory.NOUN);
		}
		return nounElement;
	}

	/**
	 * Sets the gender of an article, if no gender was specified with the corresponding noun.
	 *
	 * @param specifier the specifier or determiner for the noun phrase.
	 * @param nounElement the corresponding noun.
	 */
	private void setArticleGender(Object specifier, NLGElement nounElement) {
		// if no gender was retreived from the lexicon, but noun has definite article which specifies gender
		switch (specifier.toString()) {
			case "der":
			case "am":
			case "im":
			case "beim":
			case "vom":
			case "zum":
				nounElement.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
				break;
			case "eine":
			case "die":
			case "zur":
				nounElement.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
				break;
			case "das":
			case "ans":
			case "ins":
				nounElement.setFeature(LexicalFeature.GENDER, Gender.NEUTER);
				break;
			default:
				break;
		}
	}

	/**
	 * Tries to find the base form from an inflected word appearing in a word group lexeme.
	 *
	 * @param word the word.
	 * @param category the LexicalCategory.
	 */
	protected NLGElement createWordFromBaseform(String word, LexicalCategory category) {
		NLGElement newElement;
		int stringIsBaseform = stringIsBaseForm(word, category);
		if (word.length() > 2) {
			// check if 1st part is an adjective (but can be also given in an inflected form, e.g. "russische" instead of "russisch"
			if (stringIsBaseform == 0) {
				newElement = createNLGElement(word.toLowerCase(), category);
			} else if (stringIsBaseform == 1) {
				newElement = createNLGElement((word.toLowerCase()).substring(0, (word).length() - 1), category);
			} else if (stringIsBaseform == 2) {
				newElement = createNLGElement((word.toLowerCase()).substring(0, (word).length() - 2), category);
			} else {
				//default - create entry with word type "any"
				newElement = createNLGElement(word, category);
			}
		} else {
			newElement = createNLGElement(word, category);
		}
		return newElement;
	}

	/**
	 * A helper method to set the features on newly created pronoun words.
	 *
	 * @param wordElement the created element representing the pronoun.
	 * @param word        the base word for the pronoun.
	 */
	private void setPronounFeatures(NLGElement wordElement, String word) {
		wordElement.setCategory(LexicalCategory.PRONOUN);
		if (FIRST_PRONOUNS.contains(word.toLowerCase())) {
			wordElement.setFeature(Feature.PERSON, Person.FIRST);
		} else if (SECOND_PRONOUNS.contains(word.toLowerCase())) {
			wordElement.setFeature(Feature.PERSON, Person.SECOND);
		} else {
			wordElement.setFeature(Feature.PERSON, Person.THIRD);
		}
		if (PLURAL_PRONOUNS.contains(word.toLowerCase())) {
			wordElement.setPlural(true);
		}
	}

	/**
	 * Creates a new (empty) coordinated phrase
	 *
	 * @return empty <code>CoordinatedPhraseElement</code>
	 */
	public CoordinatedPhraseElement createCoordinatedPhrase() {
		return new CoordinatedPhraseElement();
	}

	/**
	 * Creates a new coordinated phrase with two elements (initially)
	 *
	 * @param coord1 - first phrase to be coordinated
	 * @param coord2 = second phrase to be coordinated
	 * @return <code>CoordinatedPhraseElement</code> for the two given elements
	 */
	public CoordinatedPhraseElement createCoordinatedPhrase(Object coord1, Object coord2) {
		return new CoordinatedPhraseElement(coord1, coord2);
	}

	/**
	 * Creates a blank adjective phrase with no base adjective set.
	 *
	 * @return a <code>AdjPhraseSpec</code> representing this phrase.
	 */
	public AdjPhraseSpec createAdjectivePhrase() {
		return createAdjectivePhrase(null);
	}

	/**
	 * Creates an adjective phrase wrapping the given adjective.
	 *
	 * @param adjective the main adjective for this phrase.
	 * @return a <code>AdjPhraseSpec</code> representing this phrase.
	 */
	public AdjPhraseSpec createAdjectivePhrase(Object adjective) {
		if (adjective instanceof String) {
			if (stringIsWord((String) adjective, LexicalCategory.ADJECTIVE)) {
				AdjPhraseSpec phraseElement = new AdjPhraseSpec(this);
				NLGElement adjectiveElement = createNLGElement(adjective, LexicalCategory.ADJECTIVE);
				setPhraseHead(phraseElement, adjectiveElement);
				return phraseElement;
			} else {
				AdjPhraseSpec phraseElement = new AdjPhraseSpec(this);
				NLGElement adjectiveElement = createNLGElement(adjective, LexicalCategory.ANY);
				setPhraseHead(phraseElement, adjectiveElement);
				return phraseElement;
			}
		} else {
			AdjPhraseSpec phraseElement = new AdjPhraseSpec(this);
			NLGElement adjectiveElement = createNLGElement(adjective, LexicalCategory.ADJECTIVE);
			setPhraseHead(phraseElement, adjectiveElement);
			return phraseElement;
		}
	}

	/**
	 * Creates a blank adverb phrase that has no adverb.
	 *
	 * @return a <code>AdvPhraseSpec</code> representing this phrase.
	 */
	public AdvPhraseSpec createAdverbPhrase() {
		return createAdverbPhrase(null);
	}

	/**
	 * Creates an adverb phrase wrapping the given adverb.
	 *
	 * @param adverb the adverb for this phrase.
	 * @return a <code>AdvPhraseSpec</code> representing this phrase.
	 */
	public AdvPhraseSpec createAdverbPhrase(String adverb) {
		AdvPhraseSpec phraseElement = new AdvPhraseSpec(this);

		NLGElement adverbElement = createNLGElement(adverb, LexicalCategory.ADVERB);
		
		// Adverbial adjective, therefore get features of corresponding adjective from Wiktionary, e.g. comparative
		if(!adverbElement.hasFeature(LexicalFeature.COMPARATIVE)) {
			adverbElement = createNLGElement(adverb, LexicalCategory.ADJECTIVE);
			adverbElement.setCategory(LexicalCategory.ADVERB);
		}
		setPhraseHead(phraseElement, adverbElement);
		return phraseElement;
	}

	/**
	 * Creates a blank preposition phrase with no preposition or complements.
	 *
	 * @return a <code>PPPhraseSpec</code> representing this phrase.
	 */
	public PPPhraseSpec createPrepositionPhrase() {
		return createPrepositionPhrase(null, null);
	}

	/**
	 * Creates a preposition phrase with the given preposition.
	 *
	 * @param preposition the preposition to be used.
	 * @return a <code>PPPhraseSpec</code> representing this phrase.
	 */
	public PPPhraseSpec createPrepositionPhrase(Object preposition) {
		return createPrepositionPhrase(preposition, null);
	}

	/**
	 * Creates a preposition phrase with the given preposition and complement.
	 * An <code>NLGElement</code> representing the preposition is added as the
	 * head feature of this phrase while the complement is added as a normal
	 * phrase complement.
	 *
	 * @param preposition the preposition to be used.
	 * @param complement  the complement of the phrase.
	 * @return a <code>PPPhraseSpec</code> representing this phrase.
	 */
	public PPPhraseSpec createPrepositionPhrase(Object preposition, Object complement) {

		PPPhraseSpec phraseElement = new PPPhraseSpec(this);

		NLGElement prepositionalElement = createNLGElement(preposition, LexicalCategory.PREPOSITION);
		setPhraseHead(phraseElement, prepositionalElement);

		if (complement != null) {
			setComplement(phraseElement, complement);
		}
		return phraseElement;
	}

	/**
	 * A helper method for setting the complement of a phrase.
	 *
	 * @param phraseElement the created element representing this phrase.
	 * @param complement    the complement to be added.
	 */
	private void setComplement(PhraseElement phraseElement, Object complement) {
		NLGElement complementElement = createNLGElement(complement);
		phraseElement.addComplement(complementElement);
	}

	/**
	 * create an NLGElement from the element, no default lexical category
	 *
	 * @param element
	 * @return NLGelement
	 */
	public NLGElement createNLGElement(Object element) {
		return createNLGElement(element, LexicalCategory.ANY);
	}

	/**
	 * Create an inflected word element. InflectedWordElement represents a word
	 * that already specifies the morphological and other features that it
	 * should exhibit in a realisation. While normally, phrases are constructed
	 * using <code>WordElement</code>s, and features are set on phrases, it is
	 * sometimes desirable to set features directly on words (for example, when
	 * one wants to elide a specific word, but not its parent phrase).
	 *
	 * <p>
	 * If the object passed is already a <code>WordElement</code>, then a new
	 *
	 * <code>InflectedWordElement<code> is returned which wraps this <code>WordElement</code>
	 * . If the object is a <code>String</code>, then the
	 * <code>WordElement</code> representing this <code>String</code> is looked
	 * up, and a new
	 * <code>InflectedWordElement<code> wrapping this is returned. If no such <code>WordElement</code>
	 * is found, the element returned is an <code>InflectedWordElement</code>
	 * with the supplied string as baseform and no base <code>WordElement</code>
	 * . If an <code>NLGElement</code> is passed, this is returned unchanged.
	 *
	 * @param word     the word
	 * @param category the category
	 * @return an <code>InflectedWordElement</code>, or the original supplied
	 * object if it is an <code>NLGElement</code>.
	 */
	public NLGElement createInflectedWord(Object word, LexicalCategory category) {
		// first get the word element
		NLGElement inflElement = null;

		if (word instanceof WordElement) {
			inflElement = new InflectedWordElement((WordElement) word);

		} else if (word instanceof String) {
			NLGElement baseword = createWord((String) word, category);

			if (baseword != null && baseword instanceof WordElement) {
				inflElement = new InflectedWordElement((WordElement) baseword);
			} else {
				inflElement = new InflectedWordElement((String) word, category);
			}

		} else if (word instanceof NLGElement) {
			inflElement = (NLGElement) word;
		}

		return inflElement;
	}
/***********************************************************************************
 * Document level stuff
 ***********************************************************************************/

	/**
	 * Creates a new document element with no title.
	 *
	 * @return a <code>DocumentElement</code>
	 */
	public DocumentElement createDocument() {
		return createDocument(null);
	}

	/**
	 * Creates a new document element with the given title.
	 *
	 * @param title
	 *            the title for this element.
	 * @return a <code>DocumentElement</code>.
	 */
	public DocumentElement createDocument(String title) {
		return new DocumentElement(DocumentCategory.DOCUMENT, title);
	}

	/**
	 * Creates a new document element with the given title and adds all of the
	 * given components in the list
	 *
	 * @param title
	 *            the title of this element.
	 * @param components
	 *            a <code>List</code> of <code>NLGElement</code>s that form the
	 *            components of this element.
	 * @return a <code>DocumentElement</code>
	 */
	public DocumentElement createDocument(String title, List<DocumentElement> components) {

		DocumentElement document = new DocumentElement(DocumentCategory.DOCUMENT, title);
		if(components != null) {
			document.addComponents(components);
		}
		return document;
	}

	/**
	 * Creates a new document element with the given title and adds the given
	 * component.
	 *
	 * @param title
	 *            the title for this element.
	 * @param component
	 *            an <code>NLGElement</code> that becomes the first component of
	 *            this document element.
	 * @return a <code>DocumentElement</code>
	 */
	public DocumentElement createDocument(String title, NLGElement component) {
		DocumentElement element = new DocumentElement(DocumentCategory.DOCUMENT, title);

		if(component != null) {
			element.addComponent(component);
		}
		return element;
	}

	/**
	 * Creates a new list element with no components.
	 *
	 * @return a <code>DocumentElement</code> representing the list.
	 */
	public DocumentElement createList() {
		return new DocumentElement(DocumentCategory.LIST, null);
	}

	/**
	 * Creates a new list element and adds all of the given components in the
	 * list
	 *
	 * @param textComponents
	 *            a <code>List</code> of <code>NLGElement</code>s that form the
	 *            components of this element.
	 * @return a <code>DocumentElement</code> representing the list.
	 */
	public DocumentElement createList(List<DocumentElement> textComponents) {
		DocumentElement list = new DocumentElement(DocumentCategory.LIST, null);
		list.addComponents(textComponents);
		return list;
	}

	/**
	 * Creates a new section element with the given title and adds the given
	 * component.
	 *
	 * @param component
	 *            an <code>NLGElement</code> that becomes the first component of
	 *            this document element.
	 * @return a <code>DocumentElement</code> representing the section.
	 */
	public DocumentElement createList(NLGElement component) {
		DocumentElement list = new DocumentElement(DocumentCategory.LIST, null);
		list.addComponent(component);
		return list;
	}

	/**
	 * Creates a new enumerated list element with no components.
	 *
	 * @return a <code>DocumentElement</code> representing the list.
	 */
	public DocumentElement createEnumeratedList() {
		return new DocumentElement(DocumentCategory.ENUMERATED_LIST, null);
	}

	/**
	 * Creates a new enumerated list element and adds all of the given components in the
	 * list
	 *
	 * @param textComponents
	 *            a <code>List</code> of <code>NLGElement</code>s that form the
	 *            components of this element.
	 * @return a <code>DocumentElement</code> representing the list.
	 */
	public DocumentElement createEnumeratedList(List<DocumentElement> textComponents) {
		DocumentElement list = new DocumentElement(DocumentCategory.ENUMERATED_LIST, null);
		list.addComponents(textComponents);
		return list;
	}

	/**
	 * Creates a new section element with the given title and adds the given
	 * component.
	 *
	 * @param component
	 *            an <code>NLGElement</code> that becomes the first component of
	 *            this document element.
	 * @return a <code>DocumentElement</code> representing the section.
	 */
	public DocumentElement createEnumeratedList(NLGElement component) {
		DocumentElement list = new DocumentElement(DocumentCategory.ENUMERATED_LIST, null);
		list.addComponent(component);
		return list;
	}

	/**
	 * Creates a list item for adding to a list element.
	 *
	 * @return a <code>DocumentElement</code> representing the list item.
	 */
	public DocumentElement createListItem() {
		return new DocumentElement(DocumentCategory.LIST_ITEM, null);
	}

	/**
	 * Creates a list item for adding to a list element. The list item has the
	 * given component.
	 *
	 * @return a <code>DocumentElement</code> representing the list item.
	 */
	public DocumentElement createListItem(NLGElement component) {
		DocumentElement listItem = new DocumentElement(DocumentCategory.LIST_ITEM, null);
		listItem.addComponent(component);
		return listItem;
	}

	/**
	 * Creates a new paragraph element with no components.
	 *
	 * @return a <code>DocumentElement</code> representing this paragraph
	 */
	public DocumentElement createParagraph() {
		return new DocumentElement(DocumentCategory.PARAGRAPH, null);
	}

	/**
	 * Creates a new paragraph element and adds all of the given components in
	 * the list
	 *
	 * @param components
	 *            a <code>List</code> of <code>NLGElement</code>s that form the
	 *            components of this element.
	 * @return a <code>DocumentElement</code> representing this paragraph
	 */
	public DocumentElement createParagraph(List<DocumentElement> components) {
		DocumentElement paragraph = new DocumentElement(DocumentCategory.PARAGRAPH, null);
		if(components != null) {
			paragraph.addComponents(components);
		}
		return paragraph;
	}

	/**
	 * Creates a new paragraph element and adds the given component
	 *
	 * @param component
	 *            an <code>NLGElement</code> that becomes the first component of
	 *            this document element.
	 * @return a <code>DocumentElement</code> representing this paragraph
	 */
	public DocumentElement createParagraph(NLGElement component) {
		DocumentElement paragraph = new DocumentElement(DocumentCategory.PARAGRAPH, null);
		if(component != null) {
			paragraph.addComponent(component);
		}
		return paragraph;
	}

	/**
	 * Creates a new section element.
	 *
	 * @return a <code>DocumentElement</code> representing the section.
	 */
	public DocumentElement createSection() {
		return new DocumentElement(DocumentCategory.SECTION, null);
	}

	/**
	 * Creates a new section element with the given title.
	 *
	 * @param title
	 *            the title of the section.
	 * @return a <code>DocumentElement</code> representing the section.
	 */
	public DocumentElement createSection(String title) {
		return new DocumentElement(DocumentCategory.SECTION, title);
	}

	/**
	 * Creates a new section element with the given title and adds all of the
	 * given components in the list
	 *
	 * @param title
	 *            the title of this element.
	 * @param components
	 *            a <code>List</code> of <code>NLGElement</code>s that form the
	 *            components of this element.
	 * @return a <code>DocumentElement</code> representing the section.
	 */
	public DocumentElement createSection(String title, List<DocumentElement> components) {

		DocumentElement section = new DocumentElement(DocumentCategory.SECTION, title);
		if(components != null) {
			section.addComponents(components);
		}
		return section;
	}

	/**
	 * Creates a new section element with the given title and adds the given
	 * component.
	 *
	 * @param title
	 *            the title for this element.
	 * @param component
	 *            an <code>NLGElement</code> that becomes the first component of
	 *            this document element.
	 * @return a <code>DocumentElement</code> representing the section.
	 */
	public DocumentElement createSection(String title, NLGElement component) {
		DocumentElement section = new DocumentElement(DocumentCategory.SECTION, title);
		if(component != null) {
			section.addComponent(component);
		}
		return section;
	}

	/**
	 * Creates a new sentence element with no components.
	 *
	 * @return a <code>DocumentElement</code> representing this sentence
	 */
	public DocumentElement createSentence() {
		return new DocumentElement(DocumentCategory.SENTENCE, null);
	}

	/**
	 * Creates a new sentence element and adds all of the given components.
	 *
	 * @param components
	 *            a <code>List</code> of <code>NLGElement</code>s that form the
	 *            components of this element.
	 * @return a <code>DocumentElement</code> representing this sentence
	 */
	public DocumentElement createSentence(List<NLGElement> components) {
		DocumentElement sentence = new DocumentElement(DocumentCategory.SENTENCE, null);
		sentence.addComponents(components);
		return sentence;
	}

	/**
	 * Creates a new sentence element
	 *
	 * @param components
	 *            an <code>NLGElement</code> that becomes the first component of
	 *            this document element.
	 * @return a <code>DocumentElement</code> representing this sentence
	 */
	public DocumentElement createSentence(NLGElement components) {
		DocumentElement sentence = new DocumentElement(DocumentCategory.SENTENCE, null);
		sentence.addComponent(components);
		return sentence;
	}

	/**
	 * Creates a sentence with the given subject and verb. The phrase factory is
	 * used to construct a clause that then forms the components of the
	 * sentence.
	 *
	 * @param subject
	 *            the subject of the sentence.
	 * @param verb
	 *            the verb of the sentence.
	 * @return a <code>DocumentElement</code> representing this sentence
	 */
	public DocumentElement createSentence(Object subject, Object verb) {
		return createSentence(subject, verb, null);
	}

	/**
	 * Creates a sentence with the given subject, verb and direct object. The
	 * phrase factory is used to construct a clause that then forms the
	 * components of the sentence.
	 *
	 * @param subject
	 *            the subject of the sentence.
	 * @param verb
	 *            the verb of the sentence.
	 * @param complement
	 *            the object of the sentence.
	 * @return a <code>DocumentElement</code> representing this sentence
	 */
	public DocumentElement createSentence(Object subject, Object verb, Object complement) {

		DocumentElement sentence = new DocumentElement(DocumentCategory.SENTENCE, null);
		sentence.addComponent(createClause(subject, verb, complement));
		return sentence;
	}

	/**
	 * Creates a new sentence with the given canned text. The canned text is
	 * used to form a canned phrase (from the phrase factory) which is then
	 * added as the component to sentence element.
	 *
	 * @param cannedSentence
	 *            the canned text as a <code>String</code>.
	 * @return a <code>DocumentElement</code> representing this sentence
	 */
	public DocumentElement createSentence(String cannedSentence) {
		DocumentElement sentence = new DocumentElement(DocumentCategory.SENTENCE, null);

		if(cannedSentence != null) {
			sentence.addComponent(createStringElement(cannedSentence));
		}
		return sentence;
	}

}
