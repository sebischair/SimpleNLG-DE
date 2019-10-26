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

package simplenlgde.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import simplenlgde.framework.*;
import simplenlgde.features.*;

/**
 * <p>
 * This class contains static methods to help the syntax processor realise noun
 * phrases.
 * </p>
 */
abstract class NounPhraseHelper {
	/** The qualitative position for ordering premodifiers. */
	private static final int QUALITATIVE_POSITION = 1;

	/** The colour position for ordering premodifiers. */
	private static final int COLOUR_POSITION = 2;

	/** The classifying position for ordering premodifiers. */
	private static final int CLASSIFYING_POSITION = 3;

	/** The noun position for ordering premodifiers. */
	private static final int NOUN_POSITION = 4;

	/** The list of words after which adjectives are conjugated equally to adjectives after a definite article. */
	private static final List<String> ADJ_CONJ_SPECIAL = Arrays.asList(
			"derselbe", "dieser", "jener", "jeder", "mancher", "solcher", "welcher", "alle");

	/**
	 * The main method for realising noun phrases.
	 * 
	 * @param parent
	 *            the <code>SyntaxProcessor</code> that called this method.
	 * @param phrase
	 *            the <code>PhraseElement</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	static NLGElement realise(SyntaxProcessor parent, PhraseElement phrase) {
		ListElement realisedElement = null;

		if (phrase != null
				&& !phrase.getFeatureAsBoolean(Feature.ELIDED).booleanValue()) {
			realisedElement = new ListElement();

			PhraseHelper.realiseList(parent,
					realisedElement,
					phrase.getFeatureAsElementList(InternalFeature.FRONT_MODIFIERS),
					DiscourseFunction.FRONT_MODIFIER);
			
			if(phrase.hasFeature(LexicalFeature.GENDER) && phrase.hasFeature(InternalFeature.HEAD)
					&& !phrase.getHead().hasFeature(LexicalFeature.GENDER)) {
				// for nouns where gender was manually set by user
				phrase.getHead().setFeature(LexicalFeature.GENDER, phrase
						.getFeature(LexicalFeature.GENDER));
			} 
			// if article = ein: set GENDER = MASCULINE
			if(!phrase.hasFeature(LexicalFeature.GENDER) && phrase.hasFeature(InternalFeature.HEAD) && !phrase.getHead().hasFeature(LexicalFeature.GENDER)
					&& phrase.hasFeature(InternalFeature.SPECIFIER)) {
				Object spec = phrase.getFeature(InternalFeature.SPECIFIER);
				if(spec instanceof WordElement) {
					if(((WordElement) spec).getBaseForm().equals("ein")) {
						phrase.getHead().setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
					}
				}
			}

			realisePreModifiers(phrase, parent, realisedElement);
			realiseSpecifier(phrase, parent, realisedElement);
			realiseModifiers(phrase, parent, realisedElement);
			realiseHeadNoun(phrase, parent, realisedElement);

			PhraseHelper.realiseList(parent, realisedElement, phrase
					.getFeatureAsElementList(InternalFeature.COMPLEMENTS),
					DiscourseFunction.COMPLEMENT, phrase.getFeature(InternalFeature.CASE));

			PhraseHelper.realiseList(parent, realisedElement, phrase
					.getPostModifiers(), DiscourseFunction.POST_MODIFIER, phrase.getFeature(InternalFeature.CASE));

			if(phrase.getFeature(InternalFeature.INBETWEEN_VERB)!=null){
				realisedElement.setFeature(InternalFeature.INBETWEEN_VERB,
						phrase.getFeature(InternalFeature.INBETWEEN_VERB));
			}
			if(phrase.getFeature(InternalFeature.CASE)!=null){
				realisedElement.setFeature(InternalFeature.CASE, phrase.getFeature(InternalFeature.CASE));
			}
		}

		return realisedElement;
	}

	/**
	 * Realises the head noun of the noun phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realiseHeadNoun(PhraseElement phrase,
			SyntaxProcessor parent, ListElement realisedElement) {
		NLGElement headElement = phrase.getHead();

		if (headElement != null) {
			headElement.setFeature(Feature.ELIDED, phrase
					.getFeature(Feature.ELIDED));
			headElement.setFeature(InternalFeature.ACRONYM, phrase
					.getFeature(InternalFeature.ACRONYM));
			headElement.setFeature(Feature.NUMBER, phrase
					.getFeature(Feature.NUMBER));
			headElement.setFeature(Feature.PERSON, phrase
					.getFeature(Feature.PERSON));
			headElement.setFeature(Feature.POSSESSIVE, phrase
					.getFeature(Feature.POSSESSIVE));
			headElement.setFeature(Feature.PASSIVE, phrase
					.getFeature(Feature.PASSIVE));
			headElement.setFeature(InternalFeature.CASE, phrase.
					getFeature(InternalFeature.CASE));
			headElement.setFeature(LexicalFeature.PROPER, phrase
					.getFeature(LexicalFeature.PROPER));
			NLGElement currentElement = parent.realise(headElement);
			currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
					DiscourseFunction.SUBJECT);
			realisedElement.addComponent(currentElement);
		}
	}

	/**
	 * Realises the pre-modifiers of the noun phrase. Before being realised,
	 * pre-modifiers undergo some basic sorting based on adjective ordering.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realisePreModifiers(PhraseElement phrase,
			SyntaxProcessor parent, ListElement realisedElement) {
		ArticleForm articleForm = ArticleForm.NONE;
		List<NLGElement> preModifiers = phrase.getPreModifiers();
		if (phrase.getFeatureAsBoolean(Feature.ADJECTIVE_ORDERING)
				.booleanValue()) {
			preModifiers = sortNPPreModifiers(preModifiers);
		}
		// these features are needed for correct adjective inflection
		NLGElement head = phrase.getFeatureAsElement(InternalFeature.HEAD);
		NLGElement specifierElement = phrase.getFeatureAsElement(InternalFeature.SPECIFIER);

		if (specifierElement != null) {
			if(specifierElement.isA(LexicalCategory.ARTICLE_DEFINITE) || specifierElement instanceof WordElement && ADJ_CONJ_SPECIAL.contains(((WordElement) specifierElement).getBaseForm())) {
				articleForm = ArticleForm.DEFINITE;
			} else if(specifierElement.isA(LexicalCategory.ARTICLE_INDEFINITE)) {
				articleForm = ArticleForm.INDEFINITE;
			}
		}		
		for (NLGElement eachPreModifier : preModifiers) {

			eachPreModifier.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));

			if((eachPreModifier.getCategory() != null && eachPreModifier.getCategory() != LexicalCategory.NOUN && eachPreModifier.getCategory() != PhraseCategory.NOUN_PHRASE)
			|| eachPreModifier.getCategory() == null) {
				// unless the modifier is a noun (and thus has its own gender), set gender of parent.
				// this is important for e.g. correct adjective inflection
				eachPreModifier.setFeature(LexicalFeature.GENDER, head.getFeature(LexicalFeature.GENDER));
			}
			if(!eachPreModifier.hasFeature(InternalFeature.CASE)) {
				eachPreModifier.setFeature(InternalFeature.CASE, phrase.getFeature(InternalFeature.CASE));
			}
			eachPreModifier.setFeature(Feature.ARTICLE_FORM, articleForm);
		}
		PhraseHelper.realiseList(parent, realisedElement, preModifiers,
				DiscourseFunction.PRE_MODIFIER);
	}

	/**
	 * Realises the modifiers of the noun phrase. Before being realised,
	 * modifiers undergo some basic sorting based on adjective ordering.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realiseModifiers(PhraseElement phrase,
			SyntaxProcessor parent, ListElement realisedElement) {
		ArticleForm articleForm = ArticleForm.NONE;
		List<NLGElement> modifiers = phrase.getModifiers();
		if (phrase.getFeatureAsBoolean(Feature.ADJECTIVE_ORDERING)
				.booleanValue()) {
			modifiers = sortNPPreModifiers(modifiers);
		}
		// these features are needed for correct adjective inflection
		NLGElement head = phrase.getFeatureAsElement(InternalFeature.HEAD);
		NLGElement specifierElement = phrase.getFeatureAsElement(InternalFeature.SPECIFIER);
		if (specifierElement != null) {
			if(specifierElement.isA(LexicalCategory.ARTICLE_DEFINITE) || specifierElement instanceof WordElement && ADJ_CONJ_SPECIAL.contains(((WordElement) specifierElement).getBaseForm())) {
				articleForm = ArticleForm.DEFINITE;
			} else if(specifierElement.isA(LexicalCategory.ARTICLE_INDEFINITE)) {
				articleForm = ArticleForm.INDEFINITE;
			}
		}	

		for (NLGElement eachModifier : modifiers) {
			eachModifier.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));
			eachModifier.setFeature(LexicalFeature.GENDER, head.getFeature(LexicalFeature.GENDER));
			if(!eachModifier.hasFeature(InternalFeature.CASE)) {
					eachModifier.setFeature(InternalFeature.CASE, phrase.getFeature(InternalFeature.CASE));
			}
			eachModifier.setFeature(Feature.ARTICLE_FORM, articleForm);
		}

		PhraseHelper.realiseList(parent, realisedElement, modifiers,
				DiscourseFunction.MODIFIER);
	}

	/**
	 * Realises the specifier of the noun phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realiseSpecifier(PhraseElement phrase,
			SyntaxProcessor parent, ListElement realisedElement) {
		NLGElement specifierElement = phrase
				.getFeatureAsElement(InternalFeature.SPECIFIER);
		NLGElement head = phrase.getFeatureAsElement(InternalFeature.HEAD);

		// get all other features of noun, e.g. from wiktionary
		if(head != null) {
			Set<String> features = head.getAllFeatureNames();

			// Some nouns are only avaliable in plural, e.g. "die USA"
			if(features.contains("dative_sin")) {
				String dative_sin = head.getFeatureAsString("dative_sin");
				if (dative_sin != null){
					if (dative_sin.equals("\u2014"))
						phrase.setFeature(Feature.NUMBER,NumberAgreement.PLURAL);
				}
			}

			if (specifierElement != null
					&& !phrase.getFeatureAsBoolean(InternalFeature.RAISED)
					.booleanValue() && !phrase.getFeatureAsBoolean(Feature.ELIDED).booleanValue()) {
				if (!specifierElement.isA(LexicalCategory.PRONOUN) && specifierElement.getCategory() != PhraseCategory.NOUN_PHRASE) {
					specifierElement.setFeature(Feature.NUMBER, phrase
							.getFeature(Feature.NUMBER));
				}
				if(specifierElement.isA(LexicalCategory.ARTICLE_DEFINITE)
						|| specifierElement.isA(LexicalCategory.ARTICLE_INDEFINITE)) {
					specifierElement.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));
					specifierElement.setFeature(LexicalFeature.GENDER, head.getFeature(LexicalFeature.GENDER));
					specifierElement.setFeature(InternalFeature.CASE, phrase.getFeature(InternalFeature.CASE));
				} 

				NLGElement currentElement = parent.realise(specifierElement);

				if (currentElement != null) {
					currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
							DiscourseFunction.SPECIFIER);
					realisedElement.addComponent(currentElement);
				}
			}
		}
	}

	/**
	 * Sort the list of modifier or premodifiers for this noun phrase using adjective
	 * ordering (ie, "big" comes before "red")
	 * 
	 * @param originalModifiers
	 *            the original listing of the modifiers or premodifiers.
	 * @return the sorted <code>List</code> of modifiers or premodifiers.
	 */
	private static List<NLGElement> sortNPPreModifiers(
			List<NLGElement> originalModifiers) {

		List<NLGElement> orderedModifiers = null;

		if (originalModifiers == null || originalModifiers.size() <= 1) {
			orderedModifiers = originalModifiers;
		} else {
			orderedModifiers = new ArrayList<NLGElement>(originalModifiers);
			boolean changesMade = false;
			do {
				changesMade = false;
				for (int i = 0; i < orderedModifiers.size() - 1; i++) {
					if (getMinPos(orderedModifiers.get(i)) > getMaxPos(orderedModifiers
							.get(i + 1))) {
						NLGElement temp = orderedModifiers.get(i);
						orderedModifiers.set(i, orderedModifiers.get(i + 1));
						orderedModifiers.set(i + 1, temp);
						changesMade = true;
					}
				}
			} while (changesMade == true);
		}
		return orderedModifiers;
	}

	/**
	 * Determines the minimim position at which this modifier can occur.
	 * 
	 * @param modifier
	 *            the modifier to be checked.
	 * @return the minimum position for this modifier.
	 */
	private static int getMinPos(NLGElement modifier) {
		int position = QUALITATIVE_POSITION;

		if (modifier.isA(LexicalCategory.NOUN)
				|| modifier.isA(PhraseCategory.NOUN_PHRASE)) {

			position = NOUN_POSITION;
		} else if (modifier.isA(LexicalCategory.ADJECTIVE)
				|| modifier.isA(PhraseCategory.ADJECTIVE_PHRASE)) {
			WordElement adjective = getHeadWordElement(modifier);

			if (adjective.getFeatureAsBoolean(LexicalFeature.QUALITATIVE)
					.booleanValue()) {
				position = QUALITATIVE_POSITION;
			} else if (adjective.getFeatureAsBoolean(LexicalFeature.COLOUR)
					.booleanValue()) {
				position = COLOUR_POSITION;
			} else if (adjective
					.getFeatureAsBoolean(LexicalFeature.CLASSIFYING)
					.booleanValue()) {
				position = CLASSIFYING_POSITION;
			}
		}
		return position;
	}

	/**
	 * Determines the maximim position at which this modifier can occur.
	 * 
	 * @param modifier
	 *            the modifier to be checked.
	 * @return the maximum position for this modifier.
	 */
	private static int getMaxPos(NLGElement modifier) {
		int position = NOUN_POSITION;

		if (modifier.isA(LexicalCategory.ADJECTIVE)
				|| modifier.isA(PhraseCategory.ADJECTIVE_PHRASE)) {
			WordElement adjective = getHeadWordElement(modifier);

			if (adjective.getFeatureAsBoolean(LexicalFeature.CLASSIFYING)
					.booleanValue()) {
				position = CLASSIFYING_POSITION;
			} else if (adjective.getFeatureAsBoolean(LexicalFeature.COLOUR)
					.booleanValue()) {
				position = COLOUR_POSITION;
			} else if (adjective
					.getFeatureAsBoolean(LexicalFeature.QUALITATIVE)
					.booleanValue()) {
				position = QUALITATIVE_POSITION;
			} else {
				position = CLASSIFYING_POSITION;
			}
		}
		return position;
	}

	/**
	 * Retrieves the correct representation of the word from the element. This
	 * method will find the <code>WordElement</code>, if it exists, for the
	 * given phrase or inflected word.
	 * 
	 * @param element
	 *            the <code>NLGElement</code> from which the head is required.
	 * @return the <code>WordElement</code>
	 */
	private static WordElement getHeadWordElement(NLGElement element) {
		WordElement head = null;

		if (element instanceof WordElement)
			head = (WordElement) element;
		else if (element instanceof InflectedWordElement) {
			head = (WordElement) element.getFeature(InternalFeature.BASE_WORD);
		} else if (element instanceof PhraseElement) {
			head = getHeadWordElement(((PhraseElement) element).getHead());
		}

		return head;
	}
}
