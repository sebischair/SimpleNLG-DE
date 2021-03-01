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

package simplenlgde.morphology;

import simplenlgde.framework.*;
import simplenlgde.features.*;

import java.util.Set;

/**
 * <p>
 * This abstract class contains a number of rules for doing simple inflection.
 * </p>
 *
 * <p>
 * As a matter of course, the processor will first use any user-defined
 * inflection for the word. If no inflection is provided then the lexicon, if
 * it exists, will be examined for the correct inflection. Failing this a set of
 * very basic rules will be examined to inflect the word.
 * </p>
 *
 * All processing modules perform realisation on a tree of
 * <code>NLGElement</code>s. The modules can alter the tree in whichever way
 * they wish. For example, the syntax processor replaces phrase elements with
 * list elements consisting of inflected words while the morphology processor
 * replaces inflected words with string elements.
 * </p>
 *
 * <p>
 * <b>N.B.</b> the use of <em>module</em>, <em>processing module</em> and
 * <em>processor</em> is interchangeable. They all mean an instance of this
 * class.
 * </p>
 */
public abstract class MorphologyRules extends NLGModule {

	/**
	 * This method is the main method to perform the morphology for nouns.
	 *
	 * @param element  the <code>InflectedWordElement</code>.
	 * @param baseWord the <code>WordElement</code> as created from the lexicon
	 *                 entry.
	 * @return a <code>StringElement</code> representing the word after
	 * inflection.
	 */
	protected static StringElement doNounMorphology(InflectedWordElement element, WordElement baseWord) {
		StringBuffer realised = new StringBuffer();
		String baseForm = getBaseForm(element, baseWord);
		String inflectedForm = baseForm;
		String genus = element.getFeatureAsString(LexicalFeature.GENDER);
		DiscourseFunction grammCase = DiscourseFunction.SUBJECT;

		// only if an element has no own grammatical case, use that of parent element
		if (element.hasFeature(InternalFeature.CASE_PARENT) &&
				element.getFeature(InternalFeature.CASE_PARENT) instanceof DiscourseFunction) {
			grammCase = (DiscourseFunction) element.getFeature(InternalFeature.CASE_PARENT);
		}
		if (element.hasFeature(InternalFeature.CASE) &&
				element.getFeature(InternalFeature.CASE) instanceof DiscourseFunction) {
			grammCase = (DiscourseFunction) element.getFeature(InternalFeature.CASE);
		}

		// get all other features of nouns from lexicon entry
		Set<String> features = baseWord.getAllFeatureNames();

		// special case "-fonds" declination
		if(baseForm.endsWith("fonds")) {
			realised.append(baseForm);
			StringElement realisedElement = new StringElement(realised.toString());
			realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
					element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
			return realisedElement;
		} 

		// do morphology
		if (!element.isPlural() && !element.getFeatureAsBoolean(LexicalFeature.PROPER).booleanValue()) {
			inflectedForm = doNounMorphologySingular(element, inflectedForm, baseForm, genus, grammCase, features);
		} else if (element.isPlural() && !element.getFeatureAsBoolean(LexicalFeature.PROPER).booleanValue()) {
			inflectedForm = doNounMorphologyPlural(element, baseWord, baseForm, genus, grammCase, features);
		}
		// if lexicon returned "-", e.g. for words which have no plural
		// keep the word in its base form
		if (inflectedForm.equals("-") || inflectedForm.length()<2) {
			inflectedForm = baseForm;
		}

		realised.append(inflectedForm);
		StringElement realisedElement = new StringElement(realised.toString());
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				element.getFeature(InternalFeature.DISCOURSE_FUNCTION));

		return realisedElement;
	}

	/**
	 * This method performs the morphology for nouns in plural.
	 *
	 * @param element  the <code>InflectedWordElement</code>.
	 * @param baseWord the <code>WordElement</code> as created from the lexicon
	 *                 entry.
	 * @param baseForm  the <code>String</code> representing the baseform as retrieved from the lexicon entry.
	 * @param genus  the <code>String</code> representing the gender of the noun.
	 * @param grammCase  the <code>DiscourseFunction</code> representing grammatical case.
	 * @param features  the <code>Set<String></code> list of features as retrieved from the lexicon entry.
	 * @return a <code>StringElement</code> representing the word after
	 * inflection.
	 */
	private static String doNounMorphologyPlural(InflectedWordElement element, WordElement baseWord,
												 String baseForm, String genus, DiscourseFunction grammCase,
												 Set<String> features) {
		String inflectedForm = null;
		if (element.hasFeature(LexicalFeature.PLURAL)) {
			inflectedForm = element.getFeatureAsString(LexicalFeature.PLURAL);
		}
		if (inflectedForm == null && baseWord != null) {
			String baseDefaultInfl = null;
			if (features.contains("dative_pl")) {
				baseDefaultInfl = baseWord.getFeatureAsString("dative_pl");
			}
			if (baseDefaultInfl != null && baseDefaultInfl.equals("\u2014"))
				inflectedForm = baseForm;
			else
				inflectedForm = baseWord.getFeatureAsString(LexicalFeature.PLURAL);
		}
		// If noun is not in lexicon: build plural from rules
		if (inflectedForm == null) {
			inflectedForm = buildRuleBasedPluralNoun(baseForm, genus);
		}
		// Do dative inflection from lexicon
		if (grammCase == DiscourseFunction.INDIRECT_OBJECT) {
			if (features.contains("dative_pl")) {
				String dative_pl = element.getFeatureAsString("dative_pl");
				if (!dative_pl.equals("\u2014")) {
					inflectedForm = dative_pl;
				}
			} else if (!inflectedForm.endsWith("n") && !inflectedForm.endsWith("s"))
				inflectedForm = inflectedForm + "n";
		}
		// Do genitive inflection from lexicon
		else if (grammCase == DiscourseFunction.GENITIVE && features.contains("genitive_pl")
				&& element.getFeatureAsString("genitive_pl") != "—") {
			inflectedForm = element.getFeatureAsString("genitive_pl");
		}
		// Do accusative inflection from lexicon
		else if (grammCase == DiscourseFunction.OBJECT && features.contains("akkusative_pl")
				&& element.getFeatureAsString("akkusative_pl") != "-") {
			inflectedForm = element.getFeatureAsString("akkusative_pl");
		}
		return inflectedForm;
	}

	/**
	 * This method performs the morphology for nouns in singular.
	 *
	 * @param element  the <code>InflectedWordElement</code>.
	 * @param inflectedForm the <code>String</code> representing the inflected form.
	 * @param baseForm  the <code>String</code> representing the base form as retrieved from the lexicon entry.
	 * @param genus  the <code>String</code> representing the gender of the noun.
	 * @param grammCase  the <code>DiscourseFunction</code> representing grammatical case.
	 * @param features  the <code>Set<String></code> list of features as retrieved from the lexicon entry.
	 * @return a <code>StringElement</code> representing the word after
	 * inflection.
	 */
	private static String doNounMorphologySingular(InflectedWordElement element, String inflectedForm, String baseForm,
												   String genus, DiscourseFunction grammCase, Set<String> features) {
		if (genus != null) {
			if (grammCase == DiscourseFunction.INDIRECT_OBJECT) {
				if (features.contains("dative_sin") && element.getFeatureAsString("dative_sin") != "-") {
					inflectedForm = element.getFeatureAsString("dative_sin");
				} else if (genus == "MASCULINE") {
					if (inflectedForm.endsWith("e"))
						inflectedForm = inflectedForm + "n";
					else if (inflectedForm.endsWith("ent"))
						inflectedForm = inflectedForm + "en";
				}
			} else if (grammCase == DiscourseFunction.OBJECT) {
				if (features.contains("akkusative_sin") && !element.getFeatureAsString("akkusative_sin").equals("\u2014")) {
					inflectedForm = element.getFeatureAsString("akkusative_sin");
				} else if (genus == "MASCULINE") {
					if (inflectedForm.endsWith("e"))
						inflectedForm = inflectedForm + "n";
					else if (inflectedForm.endsWith("ent"))
						inflectedForm = inflectedForm + "en";
				}
			} else if (grammCase == DiscourseFunction.GENITIVE) {
				// Check for special genitive forms in lexicon
				if (features.contains("genitive_sin") && element.getFeatureAsString("genitive_sin") != "-") {
					inflectedForm = element.getFeatureAsString("genitive_sin");
				} else if (genus == "NEUTER" || genus == "MASCULINE") {
					if (inflectedForm.endsWith("s") || inflectedForm.endsWith("x") || inflectedForm.endsWith("z"))
						inflectedForm = inflectedForm + "es";
					else if(inflectedForm.endsWith("ß")) {
						inflectedForm = baseForm.substring(0, baseForm.length() - 1) + "sses";
					}
					else if (inflectedForm.endsWith("e"))
						inflectedForm = inflectedForm + "n";
					else if (inflectedForm.endsWith("ent"))
						inflectedForm = inflectedForm + "en";
					else
						inflectedForm = inflectedForm + "s";
				}
			}
		}
		// if no gender is given
		else if (genus == null && grammCase == DiscourseFunction.GENITIVE) {
			if(baseForm.endsWith("ß")) {
				inflectedForm = baseForm.substring(0, baseForm.length() - 1) + "sses";
			} else {
				inflectedForm = baseForm + "s";
			}
		}
		return inflectedForm;
	}

	/**
	 * Builds a plural for nouns where a rule exist.
	 * The rules are performed in this order:
	 * <ul>
	 * <li>For nouns ending <em>-ung</em>,
	 * the ending becomes <em>-en</em>. For example, <em>Zahlung</em>
	 * becomes <em>Zahlungen</em>.</li>
	 * <li>For nouns ending <em>-e</em>,
	 * the ending becomes <em>-n</em>. For example, <em>Aufgabe</em>
	 * becomes <em>Aufgaben</em>.</li>
	 * </ul>
	 *
	 * @param baseForm the base form of the word.
	 * @param genus the gender of the word.
	 * @return the inflected word.
	 */
	private static String buildRuleBasedPluralNoun(String baseForm, String genus) {
		String plural = null;
		if (baseForm != null) {
			if (baseForm.endsWith("e")) {
				plural = baseForm + "n";
			} else if (((baseForm.endsWith("ent") || baseForm.endsWith("and") || baseForm.endsWith("ant") || baseForm.endsWith("ist") || baseForm.endsWith("or")) && genus == "MASCULINE")
					|| ((baseForm.endsWith("ion") || baseForm.endsWith("ik") || baseForm.endsWith("heit") || baseForm.endsWith("keit")
							|| baseForm.endsWith("schaft") || baseForm.endsWith("tät") || baseForm.endsWith("ung")) && genus == "FEMININE")) {
				plural = baseForm + "en";
			} else if (baseForm.endsWith("in") && genus == "FEMININE" && !baseForm.endsWith("ein")) {
				plural = baseForm + "nen";
			} else if (baseForm.endsWith("ma") || baseForm.endsWith("um") || baseForm.endsWith("us")) {
				plural = baseForm.substring(0, baseForm.length() - 1) + "en";
			} else if (baseForm.endsWith("a") || baseForm.endsWith("i") || baseForm.endsWith("o") || baseForm.endsWith("u") || baseForm.endsWith("y")) {
				plural = baseForm + "s";
			} else if (((baseForm.endsWith("el") || baseForm.endsWith("an") || baseForm.endsWith("er")) && genus == "MASCULINE")
					|| ((baseForm.endsWith("chen") || baseForm.endsWith("lein")) && genus == "FEMININE")) {
				plural = baseForm;
			} else if (getNumberOfSyllables(baseForm) == 1 && genus == "NEUTER") {
				if (baseForm.endsWith("a") || baseForm.endsWith("e") || baseForm.endsWith("i") || baseForm.endsWith("o") || baseForm.endsWith("u")) {
					plural = baseForm + "r";
				} else {
					plural = baseForm + "er";
				}
			} else if (baseForm.endsWith("en")) {
				plural = baseForm;
			} else {
				plural = baseForm + "e";
			}
		}
		return plural;
	}

	/**
	 * return the base form of a word
	 *
	 * @param element
	 * @param baseWord
	 * @return
	 */
	private static String getBaseForm(InflectedWordElement element, WordElement baseWord) {
		if (LexicalCategory.VERB == element.getCategory()) {
			if (baseWord != null && baseWord.getDefaultSpellingVariant() != null)
				return baseWord.getDefaultSpellingVariant();
			else
				return element.getBaseForm();
		} else {
			if (element.getBaseForm() != null)
				return element.getBaseForm();
			else if (baseWord == null)
				return null;
			else
				return baseWord.getDefaultSpellingVariant();
		}
	}

	/**
	 * This method performs the morphology for verbs.
	 *
	 * @param element  the <code>InflectedWordElement</code>.
	 * @param baseWord the <code>WordElement</code> as created from the lexicon
	 *                 entry.
	 * @return a <code>StringElement</code> representing the word after
	 * inflection.
	 */
	protected static NLGElement doVerbMorphology(InflectedWordElement element, WordElement baseWord) {
		String realised = null;
		Object numberValue = element.getFeature(Feature.NUMBER);
		Object personValue = element.getFeature(Feature.PERSON);
		Object form = element.getFeature(Feature.FORM);
		Object tense = element.getFeature(Feature.TENSE);
		Tense tenseValue;
		String eExtension = "";
		String eExtensionPattern = ".*(p|t|k|b|d|g|f|v|w|ch|s|sch|z)(m|n)$";
		String sDeletionPattern = ".*(s|ß|x|z)$";
		Boolean eDeletion = false;
		Boolean sDeletion = false;
		Boolean separable = false;
		Boolean separable_user = true;
		Boolean regular = false;
		String part2 = "";
		String preteriteStem = "";
		Boolean zu = false;
		Boolean modal = false;
		Boolean initiated_subord = false;

		// verbs in combination with modal verbs are kept in infinitive
		if (element.hasFeature(Feature.CONTAINS_MODAL)) {
			modal = element.getFeatureAsBoolean(Feature.CONTAINS_MODAL);
		}
		// verbs in initiated subordinate clauses are inflected differently
		if (element.hasFeature(Feature.INITIATED_SUBORD)) {
			initiated_subord = element.getFeatureAsBoolean(Feature.INITIATED_SUBORD);
		}
		// verbs in combination with "zu" ("to") are kept in infinitive
		if (baseWord.hasFeature("zu")) {
			zu = baseWord.getFeatureAsBoolean("zu");
		}

		if (tense instanceof Tense) {
			tenseValue = (Tense) tense;
		} else {
			tenseValue = Tense.PRESENT;
		}
		if (form instanceof Form) {
			form = (Form) form;
		} else {
			form = Form.NORMAL;
		}

		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);

		if (modal && !baseForm.matches("dürfen|können|mögen|müssen|sollen|wollen")
				&& tenseValue.equals(Tense.PRESENT)) {
			// if there is a modal verb in the phrase, following verbs are in infinitive
			StringElement realisedElement = new StringElement(baseForm);
			realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
			return realisedElement;
		}

		// inflect modal verb for future like its inflection in present tense
		if (tenseValue.equals(Tense.FUTURE) && baseForm.equals("werden")) {
			tenseValue = Tense.PRESENT;
		}

		// inflect modal verb for perfect like its inflection in present tense
		if (tenseValue.equals(Tense.PERFECT) && (baseForm.equals("sein") || baseForm.equals("haben"))) {
			tenseValue = Tense.PRESENT;
			form = Form.NORMAL;
		}

		// get all features of verb
		Set<String> features = baseWord.getAllFeatureNames();

		// if verb is separable (e.g. widerspiegeln -> spiegelt wider)
		if (features.contains(LexicalFeature.SEPARABLE)) {
			separable = Boolean.valueOf(baseWord.getFeatureAsString(LexicalFeature.SEPARABLE).toLowerCase());
		}
		// if user has set the separable feature, overwrite separable feature from lexicon
		if(element.hasFeature(Feature.SEPARABLE_VERB)) {
			separable_user = Boolean.valueOf(element.getFeatureAsString(Feature.SEPARABLE_VERB).toLowerCase());
		}
		if (features.contains("regular")) {
			regular = Boolean.valueOf(baseWord.getFeatureAsString("regular").toLowerCase());
		}
		if (features.contains("thirdPerPres")) {
			if (baseWord.getFeatureAsString("thirdPerPres").contains(" ")) {
				part2 = baseWord.getFeatureAsString("thirdPerPres").split(" ")[1];
				separable = true;
			}
		}

		String stem = getVerbStem(baseForm);

		// check if verb needs a e-extension
		if (stem.matches(eExtensionPattern) || stem.endsWith("t")
				|| stem.endsWith("d")) {
			eExtension = "e";
		} else if (stem.endsWith("er") || stem.endsWith("el")) {
			eDeletion = true;
		} else if (stem.matches(sDeletionPattern)) {
			sDeletion = true;
		}

		if (zu) {
			realised = baseForm;
		} else if (form.equals(Form.PAST_PARTICIPLE) || Tense.PERFECT.equals(tenseValue)) {
			if (features.contains("participle2")) {
				realised = baseWord.getFeatureAsString("participle2");
			} else {
				realised = "ge" + stem + "t";
			}
		} else {
			// check first if verb is regular, if not use lexicon.
			// Only relevant for singular, as irregular verbs in plural are usually conjugated regularly
			if (!regular) {
				if (tenseValue == null || Tense.PRESENT.equals(tenseValue)) {
					// Singular
					if ((numberValue == null || NumberAgreement.SINGULAR.equals(numberValue))) {
						if (Person.FIRST.equals(personValue) && features.contains("firstPerPres")) {
							realised = baseWord.getFeatureAsString("firstPerPres");
						} else if (Person.SECOND.equals(personValue) && features.contains("secPerPres")) {
							realised = baseWord.getFeatureAsString("secPerPres");
						} else if (Person.THIRD.equals(personValue) && features.contains("thirdPerPres")) {
							realised = baseWord.getFeatureAsString("thirdPerPres");
						} else {
							// default when no conjugated forms from dictionary available
							regular = true;
						}
					} else {
						// irregular verbs in plural are usually conjugated regularly
						regular = true;
					}

				} else if (Tense.PAST.equals(tenseValue)) {
					// get preterite stem of irregular verb in past, e.g. haben -> ich hatte -> new stem hat
					// no preterite stem available -> do regular inflection
					if (features.contains("preterite")) {
						preteriteStem = baseWord.getFeatureAsString("preterite");
					} else {
						regular = true;
					}
					if(separable) {
						if(preteriteStem.contains(" ")) {
							preteriteStem = preteriteStem.split(" ")[0];
							if(part2 == "") {
								part2 = preteriteStem.split(" ")[1];
							}
						}
					}
					if (preteriteStem != "") {
						if ((numberValue == null || NumberAgreement.SINGULAR.equals(numberValue))) {
							if (Person.FIRST.equals(personValue) || Person.THIRD.equals(personValue)) {
								realised = preteriteStem;
							} else if (Person.SECOND.equals(personValue)) {
								if (preteriteStem.matches(sDeletionPattern)) {
									realised = preteriteStem + "t";
								} else {
									realised = preteriteStem + "st";
								}
							}
						} else if (NumberAgreement.PLURAL.equals(numberValue)) {
							if (Person.FIRST.equals(personValue) || Person.THIRD.equals(personValue)) {
								if (preteriteStem.endsWith("e")) {
									realised = preteriteStem + "n";
								} else {
									realised = preteriteStem + "en";
								}
							} else if (Person.SECOND.equals(personValue)) {
								if (preteriteStem.matches(eExtensionPattern) || preteriteStem.endsWith("t")
										|| preteriteStem.endsWith("d")) {
									realised = preteriteStem + "et";
								} else {
									realised = preteriteStem + "t";
								}
							}
						}
					}
					if(separable) {
						if(initiated_subord) {
							realised = part2 + realised;
						} else {
							realised = realised + " " + part2;
						}
					}

				}
			}


			// Verb is regular
			if (regular) {
				// Present tense or no tense given
				if (tenseValue == null || Tense.PRESENT.equals(tenseValue)) {
					// Singular
					if ((numberValue == null || NumberAgreement.SINGULAR.equals(numberValue))) {
						if (Person.FIRST.equals(personValue)) {
							// normal regular case
							realised = stem + "e"; //$NON-NLS-1$
						} else if (Person.SECOND.equals(personValue)) {
							if (sDeletion) {
								realised = stem + "t";
							} else {
								// normal regular case or e-extension
								realised = stem + eExtension + "st";
							}
						} else if (Person.THIRD.equals(personValue)) {
							// normal regular case or e-extension
							realised = stem + eExtension + "t";
							// e-extension
						} else {
							realised = baseForm;
						}
						// Plural
					} else if (NumberAgreement.PLURAL.equals(numberValue)) {
						if (Person.FIRST.equals(personValue) || Person.THIRD.equals(personValue)) {
							// if plural conjugation is irregular
							if (features.contains("plFirstThirdPerPres")) {
								realised = baseWord.getFeatureAsString("plFirstThirdPerPres");
							} else if (eDeletion) {
								realised = baseForm;
							} else {
								// normal regular case
								realised = stem + "en";
							}
						} else if (Person.SECOND.equals(personValue)) {
							// if plural conjugation is irregular
							if (features.contains("plSecPerPres")) {
								realised = baseWord.getFeatureAsString("plSecPerPres");
							} else {
								// normal regular case or e-extension
								realised = stem + eExtension + "t";
							}
						}
					} else {
						realised = baseForm;
					}
				} else if (Tense.PAST.equals(tenseValue)) {
					// Singular
					if ((numberValue == null || NumberAgreement.SINGULAR.equals(numberValue))) {
						if (Person.FIRST.equals(personValue) || Person.THIRD.equals(personValue)) {
							// normal regular case
							realised = stem + eExtension + "te";
						} else if (Person.SECOND.equals(personValue)) {
							// normal regular case or e-extension
							realised = stem + eExtension + "test";
						}
						// Plural
					} else if (NumberAgreement.PLURAL.equals(numberValue)) {
						if (Person.FIRST.equals(personValue) || Person.THIRD.equals(personValue)) {
							// if plural conjugation is irregular
							if (features.contains("plFirstThirdPerPres")) {
								realised = baseWord.getFeatureAsString("plFirstThirdPerPres");
							} else {
								// normal regular case
								realised = stem + eExtension + "ten";
							}
						} else if (Person.SECOND.equals(personValue)) {
							// if plural conjugation is irregular
							if (features.contains("plSecPerPres")) {
								realised = baseWord.getFeatureAsString("plSecPerPres");
							}
							// normal regular case or e-extension
							realised = stem + eExtension + "tet";
						}
					} else {
						realised = baseForm;
					}
				}
				//if separable, get separable parts of verb
				if (separable && !form.equals(Form.PAST_PARTICIPLE)) {
					if (part2 == "") {
						part2 = baseWord.getFeatureAsString("part1");
					}
					if (part2 != null && realised != null) {
						if(initiated_subord) {
							String part1 = realised.split(part2)[1];
							realised = part2 + part1;
						} else {
							String part1 = realised.split(part2)[1];
							realised = part1 + " " + part2;
						}
					}
				}
			}
		}
		if (realised == null && baseForm != null) {
			realised = baseForm;
		}
		if(separable && !separable_user) {
			// if user set feature separable to false
			String[] split = realised.split(" ");
			if (split.length > 1) {
				realised = split[1] + split[0];
			}
		}
		StringElement realisedElement = new StringElement(realised);
		// as adverbs can be placed inside a separable verb, e.g. "schneidet gut ab",
		// the separable feature needs to be passed further to change word order
		if (separable) {
			realisedElement.setFeature(LexicalFeature.SEPARABLE, true);
		}
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * This method extracts the stem of a verb.
	 *
	 * @param baseForm the <code>base Form of a word</code>.
	 * @return a <code>String</code> representing the word after
	 * stemming.
	 */
	protected static String getVerbStem(String baseForm) {
		String stem = "";
		// extract verb stem
		if (baseForm.endsWith("en")) {
			stem = baseForm.substring(0, baseForm.length() - 2);
		} else if (baseForm.endsWith("n")) {
			stem = baseForm.substring(0, baseForm.length() - 1);
		} else {
			stem = baseForm;
		}
		return stem;
	}

	/**
	 * This method performs the morphology for adjectives regarding genus, case, number and the corresponding article
	 * (indefinite article: "Ein guter Schüler" but with definite article: "Der gute Schüler").
	 *
	 * @param element  the <code>InflectedWordElement</code>.
	 * @param baseWord the <code>WordElement</code> as created from the lexicon
	 *                 entry.
	 * @return a <code>StringElement</code> representing the word after
	 * inflection.
	 */

	protected static NLGElement doAdjectiveMorphology(InflectedWordElement element, WordElement baseWord) {
		String realised = getBaseForm(element, baseWord);
		Object numberValue = element.getFeature(Feature.NUMBER);
		String genus = element.getFeatureAsString(LexicalFeature.GENDER);
		Set<String> features = baseWord.getAllFeatureNames();

		Boolean is_comparative = element.hasFeature(Feature.IS_COMPARATIVE)
				&& element.getFeatureAsBoolean(Feature.IS_COMPARATIVE);
		Boolean is_superlative = element.hasFeature(Feature.IS_SUPERLATIVE)
				&& element.getFeatureAsBoolean(Feature.IS_SUPERLATIVE);

		ArticleForm articleForm = ArticleForm.NONE;	
		if(element.getFeature(Feature.ARTICLE_FORM) instanceof ArticleForm) {
			articleForm = (ArticleForm) element.getFeature(Feature.ARTICLE_FORM);
		}

		//default grammatical case
		DiscourseFunction grammCase = DiscourseFunction.SUBJECT;
		if (element.hasFeature(InternalFeature.CASE_PARENT) &&
				element.getFeature(InternalFeature.CASE_PARENT) instanceof DiscourseFunction) {
			grammCase = (DiscourseFunction) element.getFeature(InternalFeature.CASE_PARENT);
		}
		if (element.hasFeature(InternalFeature.CASE) &&
				element.getFeature(InternalFeature.CASE) instanceof DiscourseFunction) {
			grammCase = (DiscourseFunction) element.getFeature(InternalFeature.CASE);
		}
		// default genus: most nouns are feminine according to Duden
		if(genus == null) {
			genus = "FEMININE";
		}

		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);

		if (element.hasFeature(InternalFeature.MODIFIER_TYPE) &&
				element.getFeature(InternalFeature.MODIFIER_TYPE).equals("ADV")) {
			realised = baseForm;
		} else {
			// Handle special cases
			// If adjective ends with "e", no additional e needed (wrong: leiseer, correct: leiser)
			String addedE = "e";
			if (baseForm.endsWith("e") || is_superlative) {
				addedE = "";
			}

			// If adjective ends with "el", the "e" from the "el" is omitted (wrong: dunkeles, correct: dunkles)
			else if (baseForm.endsWith("el") && getNumberOfSyllables(baseForm) > 1) {
				baseForm = baseForm.substring(0, baseForm.length() - 2) + "l";
			}

			// If an adjective ends with "er" and "er" is following a vocal, the "e" is ommited (wrong: teueres, correct: teures")
			else if (baseForm.endsWith("er")) {
				String withoutEnding =  baseForm.substring(0, baseForm.length() - 2);
				if(withoutEnding.matches(".*[aeiouyäüö]$")) {
					baseForm = baseForm.substring(0, baseForm.length() - 2) + "r";
				}
			}
			// Adjective "hoch" is irregular "ein hohes Haus", not "ein hoches Haus"
			else if (baseForm.equalsIgnoreCase("hoch")) {
				baseForm = "hoh";
			}
			if(is_comparative || is_superlative) {
				baseForm = doAdjectiveCompSup(element, baseWord).getRealisation();

				if (is_comparative) {
					if (features.contains("comp")) {
						// comparative form is in Wiktionary
						baseForm = baseWord.getFeatureAsString("comp");
						if (!baseForm.endsWith("e")) {
							addedE = "e";
						}
					}
					realised = baseForm;
				}
				if(is_superlative) {
					realised = baseForm;
				}
			}

			if (grammCase == DiscourseFunction.SUBJECT) {
				if (articleForm.equals(ArticleForm.DEFINITE)) {
					if (NumberAgreement.PLURAL.equals(numberValue)) {
						realised = baseForm + addedE + "n";
					} else {
						realised = baseForm + addedE;
					}
				} else if (articleForm.equals(ArticleForm.INDEFINITE)) {
					if (NumberAgreement.PLURAL.equals(numberValue)) {
						realised = baseForm + addedE + "n";
					} else {
						if (genus == null) {
							realised = getBaseForm(element, baseWord) + "e";
						} else {
							switch (genus) {
							case "MASCULINE":
								realised = baseForm + addedE + "r";
								break;
							case "FEMININE":
								realised = baseForm + addedE;
								break;
							case "NEUTER":
								realised = baseForm + addedE + "s";
								break;
							default:
								realised = baseForm + "e";
							}
						}
					}
				} else if (articleForm.equals(ArticleForm.NONE)) {
					if (NumberAgreement.PLURAL.equals(numberValue)) {
						realised = baseForm + addedE;
					} else {
						if (genus == null) {
							realised = baseForm + "e";
						} else {
							switch (genus) {
							case "MASCULINE":
								realised = baseForm + addedE + "r";
								break;
							case "FEMININE":
								realised = baseForm + addedE;
								break;
							case "NEUTER":
								realised = baseForm + addedE + "s";
								break;
							default:
								realised = baseForm + "e";
							}
						}
					}
				}
			} else if (grammCase == DiscourseFunction.OBJECT) {
				if (articleForm.equals(ArticleForm.DEFINITE)) {
					if (NumberAgreement.PLURAL.equals(numberValue) || genus == "MASCULINE") {
						realised = baseForm + addedE + "n";
					} else {
						realised = baseForm + addedE;
					}
				} else if (articleForm.equals(ArticleForm.INDEFINITE)) {
					if (NumberAgreement.PLURAL.equals(numberValue)) {
						realised = baseForm + addedE + "n";
					} else {
						if (genus == null) {
							realised = getBaseForm(element, baseWord) + "e";
						} else {
							switch (genus) {
							case "MASCULINE":
								realised = baseForm + addedE + "n";
								break;
							case "FEMININE":
								realised = baseForm + addedE;
								break;
							case "NEUTER":
								realised = baseForm + addedE + "s";
								break;
							default:
								realised = baseForm + "e";
							}
						}
					}
				} else if (articleForm.equals(ArticleForm.NONE)) {
					if (NumberAgreement.PLURAL.equals(numberValue)) {
						realised = baseForm + addedE;
					} else {
						if (genus == null) {
							realised = baseForm + "e";
						} else {
							switch (genus) {
							case "MASCULINE":
								realised = baseForm + addedE + "n";
								break;
							case "FEMININE":
								realised = baseForm + addedE;
								break;
							case "NEUTER":
								realised = baseForm + addedE + "s";
								break;
							default:
								realised = baseForm + "e";
							}
						}
					}
				}
			} else if (grammCase == DiscourseFunction.INDIRECT_OBJECT) {
				if (articleForm.equals(ArticleForm.DEFINITE) || articleForm.equals(ArticleForm.INDEFINITE)) {
					realised = baseForm + addedE + "n";
				} else if (articleForm.equals(ArticleForm.NONE)) {
					if (NumberAgreement.PLURAL.equals(numberValue)) {
						realised = baseForm + addedE + "n";
					} else {
						if (genus == null) {
							realised = baseForm + "e";
						} else {
							switch (genus) {
							case "MASCULINE":
								realised = baseForm + addedE + "m";
								break;
							case "FEMININE":
								realised = baseForm + addedE + "r";
								break;
							case "NEUTER":
								realised = baseForm + addedE + "m";
								break;
							default:
								realised = baseForm + "e";
							}
						}
					}
				}
			} else if (grammCase == DiscourseFunction.GENITIVE) {
				if (articleForm.equals(ArticleForm.DEFINITE) || articleForm.equals(ArticleForm.INDEFINITE)) {
					realised = baseForm + addedE + "n";
				} else if (articleForm.equals(ArticleForm.NONE)) {
					if (NumberAgreement.PLURAL.equals(numberValue)) {
						realised = baseForm + addedE + "r";
					} else {
						if (genus == null) {
							realised = baseForm + "en";
						} else {
							switch (genus) {
							case "MASCULINE":
								realised = baseForm + addedE + "n";
								break;
							case "FEMININE":
								realised = baseForm + addedE + "r";
								break;
							case "NEUTER":
								realised = baseForm + addedE + "n";
								break;
							default:
								realised = baseForm + "en";
							}
						}
					}
				}
			} else {
				realised = baseForm;
			}
			if (element.getFeatureAsBoolean("composite")) {
				// inflection for compound words, e.g. "die Russische Föderation"
				realised = " " + realised.substring(0, 1).toUpperCase() + realised.substring(1);
			}
		}
		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * This method changes an adjective to its comparative or superlative
	 * form, depending on what is specified in its features.
	 *
	 * @param element  the <code>InflectedWordElement</code>.
	 * @param baseWord the <code>WordElement</code> as created from the lexicon
	 *                 entry.
	 * @return a <code>StringElement</code> representing the word after 
	 * changing its form to comparative or superlative
	 */
	protected static NLGElement doAdjectiveCompSup(InflectedWordElement element, WordElement baseWord) {
		Set<String> features = baseWord.getAllFeatureNames();
		Boolean is_comparative = element.hasFeature(Feature.IS_COMPARATIVE) && element.getFeatureAsBoolean(Feature.IS_COMPARATIVE);
		Boolean is_superlative = element.hasFeature(Feature.IS_SUPERLATIVE) && element.getFeatureAsBoolean(Feature.IS_SUPERLATIVE);
		
		ArticleForm articleForm = ArticleForm.NONE;	
		if(element.getFeature(Feature.ARTICLE_FORM) instanceof ArticleForm) {
			articleForm = (ArticleForm) element.getFeature(Feature.ARTICLE_FORM);
		}
		
		String baseForm = getBaseForm(element, baseWord);
		
		if (is_superlative) {
			if (features.contains("sup")) {
				// superlative form is in Wiktionary
				baseForm = baseWord.getFeatureAsString("sup");
				if ((articleForm.equals(ArticleForm.DEFINITE) || articleForm.equals(ArticleForm.INDEFINITE)) 
						&& (baseForm.endsWith("sten") || baseForm.endsWith("ßten"))) {
					baseForm = baseForm.substring(0, baseForm.length() - 1);
				}
			} else {
				// build superlative by rules
				if (baseForm.endsWith("d") || baseForm.endsWith("ß") || baseForm.endsWith("sch") || baseForm.endsWith("t")
						|| baseForm.endsWith("tz") || baseForm.endsWith("x") || baseForm.endsWith("z")) {
					baseForm = baseForm + "e";
				}
				if (articleForm.equals(ArticleForm.DEFINITE) || articleForm.equals(ArticleForm.INDEFINITE)) {
					baseForm = baseForm + "ste";
				} else {
					baseForm = baseForm + "sten";
				}
			}
		} else if (is_comparative) {
			if (features.contains("comp")) {
				// comparative form is in Wiktionary
				baseForm = baseWord.getFeatureAsString("comp");
			} else {
				// build comparative by rules
				if (baseForm.endsWith("e")) {
					baseForm = baseForm + "re";
				} else {
					baseForm = baseForm + "er";
				}
			}
		}

		StringElement realisedElement = new StringElement(baseForm);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * This method is the main method to perform the morphology for articles.
	 *
	 * @param element  the <code>InflectedWordElement</code>.
	 * @param baseWord the <code>WordElement</code> as created from the lexicon
	 *                 entry.
	 * @return a <code>StringElement</code> representing the word after
	 * inflection.
	 */
	protected static NLGElement doArticleInflection(InflectedWordElement element, WordElement baseWord) {
		String realised = getBaseForm(element, baseWord);
		Object numberValue = element.getFeature(Feature.NUMBER);
		String genus = element.getFeatureAsString(LexicalFeature.GENDER);

		//default grammatical case
		DiscourseFunction grammCase = DiscourseFunction.SUBJECT;
		if (element.hasFeature(InternalFeature.CASE) &&
				element.getFeature(InternalFeature.CASE) instanceof DiscourseFunction) {
			grammCase = (DiscourseFunction) element.getFeature(InternalFeature.CASE);
		} else if (element.hasFeature(InternalFeature.CASE_PARENT) &&
				element.getFeature(InternalFeature.CASE_PARENT) instanceof DiscourseFunction) {
			grammCase = (DiscourseFunction) element.getFeature(InternalFeature.CASE_PARENT);
		}

		String baseForm = getBaseForm(element, baseWord);

		if (genus == null) {
			if (baseForm.equalsIgnoreCase("das")) {
				genus = "NEUTER";
			} else if (baseForm.equalsIgnoreCase("der") || baseForm.equalsIgnoreCase("ein")) {
				genus = "MASCULINE";
			} else if (baseForm.equalsIgnoreCase("die") || baseForm.equalsIgnoreCase("eine")) {
				genus = "FEMININE";
			} else {
				genus = "MASCULINE";
			}
		}
		if (element.hasFeature(InternalFeature.MERGED_ARTICLE) && element.getFeatureAsBoolean(InternalFeature.MERGED_ARTICLE)) {
			realised = baseForm;
		} else {
			if (element.isA(LexicalCategory.ARTICLE_DEFINITE)) {
				if (grammCase == DiscourseFunction.INDIRECT_OBJECT) {
					// case dative
					if (NumberAgreement.SINGULAR.equals(numberValue)) {
						switch (genus) {
						case "MASCULINE":
							realised = "dem";
							break;
						case "FEMININE":
							realised = "der";
							break;
						case "NEUTER":
							realised = "dem";
							break;
						default:
							realised = baseForm;
						}
					} else {
						realised = "den";
					}
				} else if (grammCase == DiscourseFunction.OBJECT) {
					if (NumberAgreement.PLURAL.equals(numberValue)) {
						realised = "die";
					} else if (NumberAgreement.SINGULAR.equals(numberValue)) {
						switch (genus) {
						case "MASCULINE":
							realised = "den";
							break;
						case "FEMININE":
							realised = "die";
							break;
						case "NEUTER":
							realised = "das";
							break;
						default:
							realised = baseForm;
						}
					}
				} else if (grammCase == DiscourseFunction.SUBJECT) {
					if (NumberAgreement.PLURAL.equals(numberValue)) {
						realised = "die";
					} else {
						switch (genus) {
						case "MASCULINE":
							realised = "der";
							break;
						case "FEMININE":
							realised = "die";
							break;
						case "NEUTER":
							realised = "das";
							break;
						default:
							realised = baseForm;
						}
					}
				} else if (grammCase == DiscourseFunction.GENITIVE) {
					if (NumberAgreement.SINGULAR.equals(numberValue)) {
						switch (genus) {
						case "MASCULINE":
							realised = "des";
							break;
						case "FEMININE":
							realised = "der";
							break;
						case "NEUTER":
							realised = "des";
							break;
						default:
							realised = baseForm;
						}
					} else {
						realised = "der";
					}
				} else {
					// for modifier or complements
					realised = getBaseForm(element, baseWord);
				}
			}
			if (element.isA(LexicalCategory.ARTICLE_INDEFINITE)) {
				if (grammCase == DiscourseFunction.INDIRECT_OBJECT) {
					// case dative
					if (NumberAgreement.SINGULAR.equals(numberValue)) {
						switch (genus) {
						case "MASCULINE":
							realised = "einem";
							break;
						case "FEMININE":
							realised = "einer";
							break;
						case "NEUTER":
							realised = "einem";
							break;
						default:
							realised = baseForm;
						}
					} else {
						realised = getBaseForm(element, baseWord);
					}
				} else if (grammCase == DiscourseFunction.OBJECT) {
					if (NumberAgreement.SINGULAR.equals(numberValue)) {
						switch (genus) {
						case "MASCULINE":
							realised = "einen";
							break;
						case "FEMININE":
							realised = "eine";
							break;
						case "NEUTER":
							realised = "ein";
							break;
						default:
							realised = baseForm;
						}
					} else {
						realised = baseForm;
					}
				} else if (grammCase == DiscourseFunction.SUBJECT) {
					if (NumberAgreement.SINGULAR.equals(numberValue)) {
						switch (genus) {
						case "MASCULINE":
							realised = "ein";
							break;
						case "FEMININE":
							realised = "eine";
							break;
						case "NEUTER":
							realised = "ein";
							break;
						default:
							realised = baseForm;
						}
					} else {
						realised = getBaseForm(element, baseWord);
					}
				} else if (grammCase == DiscourseFunction.GENITIVE) {
					if (NumberAgreement.SINGULAR.equals(numberValue)) {
						switch (genus) {
						case "MASCULINE":
							realised = "eines";
							break;
						case "FEMININE":
							realised = "einer";
							break;
						case "NEUTER":
							realised = "eines";
							break;
						default:
							realised = baseForm;
						}
					} else {
						realised = baseForm;
					}
				} else {
					// for modifier or complements
					realised = baseForm;
				}
			}
		}
		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * This method is the main method to perform the morphology for indefinite pronouns.
	 *
	 * @param element  the <code>InflectedWordElement</code>.
	 * @param baseWord the <code>WordElement</code> as created from the lexicon
	 *                 entry.
	 * @return a <code>StringElement</code> representing the word after
	 * inflection.
	 */
	public static NLGElement doIndefPronounMorphology(InflectedWordElement element, WordElement baseWord) {
		String realised = getBaseForm(element, baseWord);
		Object numberValue = element.getFeature(Feature.NUMBER);
		String genus = element.getFeatureAsString(LexicalFeature.GENDER);

		ArticleForm articleForm = ArticleForm.NONE;	
		if(element.getFeature(Feature.ARTICLE_FORM) instanceof ArticleForm) {
			articleForm = (ArticleForm) element.getFeature(Feature.ARTICLE_FORM);
		}

		//default grammatical case
		DiscourseFunction grammCase = DiscourseFunction.SUBJECT;
		if (element.hasFeature(InternalFeature.CASE_PARENT) &&
				element.getFeature(InternalFeature.CASE_PARENT) instanceof DiscourseFunction) {
			grammCase = (DiscourseFunction) element.getFeature(InternalFeature.CASE_PARENT);
		} else if (element.hasFeature(InternalFeature.CASE) &&
				element.getFeature(InternalFeature.CASE) instanceof DiscourseFunction) {
			grammCase = (DiscourseFunction) element.getFeature(InternalFeature.CASE);
		}

		// base form from baseWord if it exists, otherwise from element
		String baseForm = getBaseForm(element, baseWord);

		if (baseForm != null) {
			switch (baseForm) {
			case "beide":
				if (numberValue.equals(NumberAgreement.SINGULAR)) {
					if (grammCase.equals(DiscourseFunction.SUBJECT)) {
						realised = "beides";
					} else if (grammCase.equals(DiscourseFunction.OBJECT)) {
						realised = "beides";
					} else if (grammCase.equals(DiscourseFunction.INDIRECT_OBJECT)) {
						realised = "beidem";
					}
				} else {
					if (articleForm == null) {
						if (grammCase.equals(DiscourseFunction.SUBJECT)) {
							realised = "beide";
						} else if (grammCase.equals(DiscourseFunction.GENITIVE)) {
							realised = "beider";
						} else if (grammCase.equals(DiscourseFunction.OBJECT)) {
							realised = "beiden";
						} else if (grammCase.equals(DiscourseFunction.INDIRECT_OBJECT)) {
							realised = "beide";
						}
					} else {
						realised = "beiden";
					}
				}
				break;
				//TODO: Add conjugation for further indefinite pronouns
			default:
				realised = baseForm;
			}
		}

		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * The method for getting the number of syllables for a given word
	 *
	 * @param s the given word
	 * @return the number of syllables
	 */
	public static int getNumberOfSyllables(String s) {
		s = s.trim();
		if (s.length() <= 3) {
			return 1;
		}
		s = s.toLowerCase();
		s = s.replaceAll("[aeiouyäüö]+", "a");
		s = "x" + s + "x";
		return s.split("a").length - 1;
	}
}
