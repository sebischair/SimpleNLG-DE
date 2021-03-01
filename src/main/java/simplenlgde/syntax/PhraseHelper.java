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

import simplenlgde.framework.*;
import simplenlgde.features.*;

import java.util.List;

/**
 * <p>
 * This class contains static methods to help the syntax processor realise
 * phrases.
 * </p>
 */
abstract class PhraseHelper {

	/**
	 * The main method for realising phrases.
	 *
	 * @param parent the <code>SyntaxProcessor</code> that called this method.
	 * @param phrase the <code>PhraseElement</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	static NLGElement realise(SyntaxProcessor parent, PhraseElement phrase) {
		ListElement realisedElement = null;

		if (phrase != null) {
			realisedElement = new ListElement();

			realiseList(parent, realisedElement, phrase.getPreModifiers(),
					DiscourseFunction.PRE_MODIFIER);

			realiseHead(parent, phrase, realisedElement);

			realiseComplements(parent, phrase, realisedElement);

			PhraseHelper.realiseList(parent, realisedElement, phrase
					.getPostModifiers(), DiscourseFunction.POST_MODIFIER);
		}

		return realisedElement;
	}

	/**
	 * Realises the complements of the phrase adding <em>and</em> where
	 * appropriate.
	 *
	 * @param parent          the parent <code>SyntaxProcessor</code> that will do the
	 *                        realisation of the complementiser.
	 * @param phrase          the <code>PhraseElement</code> representing this noun phrase.
	 * @param realisedElement the current realisation of the noun phrase.
	 */
	private static void realiseComplements(SyntaxProcessor parent,
										   PhraseElement phrase, ListElement realisedElement) {

		boolean firstProcessed = false;
		NLGElement currentElement = null;

		for (NLGElement complement : phrase
				.getFeatureAsElementList(InternalFeature.COMPLEMENTS)) {
			if (phrase.hasFeature(InternalFeature.CASE) && !complement.hasFeature(InternalFeature.CASE)) {
				complement.setFeature(InternalFeature.CASE, phrase.getFeature(InternalFeature.CASE));
			}
			currentElement = parent.realise(complement);

			if (currentElement != null) {
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.COMPLEMENT);
				if (firstProcessed) {
					realisedElement.addComponent(new InflectedWordElement(
							"und", LexicalCategory.CONJUNCTION));
				} else {
					firstProcessed = true;
				}
				realisedElement.addComponent(currentElement);
			}
		}
	}

	/**
	 * Realises the head element of the phrase.
	 *
	 * @param parent          the parent <code>SyntaxProcessor</code> that will do the
	 *                        realisation of the complementiser.
	 * @param phrase          the <code>PhraseElement</code> representing this noun phrase.
	 * @param realisedElement the current realisation of the noun phrase.
	 */
	private static void realiseHead(SyntaxProcessor parent,
									PhraseElement phrase, ListElement realisedElement) {

		NLGElement head = phrase.getHead();
		if (head != null) {
			head.setFeature(LexicalFeature.GENDER, phrase
					.getFeature(LexicalFeature.GENDER));
			head.setFeature(Feature.NUMBER, phrase
					.getFeature(Feature.NUMBER));
			head.setFeature(InternalFeature.CASE, phrase.
					getFeature(InternalFeature.CASE));
			head.setFeature(Feature.ARTICLE_FORM, phrase.
					getFeature(Feature.ARTICLE_FORM));
			head.setFeature(Feature.IS_COMPARATIVE, phrase.
					getFeature(Feature.IS_COMPARATIVE));
			head.setFeature(Feature.IS_SUPERLATIVE, phrase.
					getFeature(Feature.IS_SUPERLATIVE));
			head = parent.realise(head);
			if (phrase.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
				head.setFeature(InternalFeature.DISCOURSE_FUNCTION, phrase.getFeature(InternalFeature.DISCOURSE_FUNCTION));
			} else {
				head.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.HEAD);
			}
			realisedElement.addComponent(head);
		}
	}

	/**
	 * Iterates through a <code>List</code> of <code>NLGElement</code>s
	 * realisation each element and adding it to the on-going realisation of
	 * this clause.
	 *
	 * @param parent          the parent <code>SyntaxProcessor</code> that will do the
	 *                        realisation of the complementiser.
	 * @param realisedElement the current realisation of the clause.
	 * @param elementList     the <code>List</code> of <code>NLGElement</code>s to be
	 *                        realised.
	 * @param function        the <code>DiscourseFunction</code> each element in the list is
	 *                        to take. If this is <code>null</code> then the function is not
	 *                        set and any existing discourse function is kept.
	 */
	static void realiseList(SyntaxProcessor parent,
							ListElement realisedElement, List<NLGElement> elementList,
							DiscourseFunction function) {

		ListElement realisedList = new ListElement();
		NLGElement currentElement;
		Boolean onePercent = false;

		for (NLGElement eachElement : elementList) {
			currentElement = parent.realise(eachElement);

			if (currentElement != null) {
				if (currentElement.equals("1")) {
					onePercent = true;
				}
				if (currentElement.hasFeature("base_word") && currentElement.getFeatureAsElement("base_word").hasFeature("plural")
						&& currentElement.getFeatureAsElement("base_word").getFeature("plural").equals("Prozente") && !onePercent) {
					// if noun = Prozent & >1 Prozent: Verb has to be in plural
					if (realisedElement != null && realisedElement.hasFeature(InternalFeature.COMPONENTS)) {
						List<NLGElement> components = realisedElement.getFeatureAsElementList(InternalFeature.COMPONENTS);
						for (NLGElement component : components) {
							component.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
						}
					}
				}
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						function);
				copyFeatures(currentElement, eachElement);
				realisedList.addComponent(currentElement);
			}
		}

		if (!realisedList.getChildren().isEmpty()) {
			realisedElement.addComponent(realisedList);
		}
	}

	/**
	 * Copies features from old to newly realised element
	 *
	 * @param currentElement the new <code>NLGElement</code> that receives the features
	 * @param eachElement    the old <code>NLGElement</code> from which the features are copied‚
	 */
	private static void copyFeatures(NLGElement currentElement, NLGElement eachElement) {
		if (eachElement.hasFeature(InternalFeature.MODIFIER_TYPE)) {
			currentElement.setFeature(InternalFeature.MODIFIER_TYPE, eachElement.getFeature(InternalFeature.MODIFIER_TYPE));
		}
		if (eachElement.hasFeature(Feature.IS_SUPERLATIVE)) {
			currentElement.setFeature(Feature.IS_SUPERLATIVE, eachElement.getFeature(Feature.IS_SUPERLATIVE));
		}
		if (eachElement.hasFeature(Feature.IS_COMPARATIVE)) {
			currentElement.setFeature(Feature.IS_COMPARATIVE, eachElement.getFeature(Feature.IS_COMPARATIVE));
		}
		if (eachElement.hasFeature(Feature.APPOSITIVE)) {
			currentElement.setFeature(Feature.APPOSITIVE, eachElement.getFeature(Feature.APPOSITIVE));
		}
	}

	/**
	 * Iterates through a <code>List</code> of <code>NLGElement</code>s
	 * realisation each element and adding it to the on-going realisation of
	 * this clause.
	 *
	 * @param parent          the parent <code>SyntaxProcessor</code> that will do the
	 *                        realisation of the complementiser.
	 * @param realisedElement the current realisation of the clause.
	 * @param elementList     the <code>List</code> of <code>NLGElement</code>s to be
	 *                        realised.
	 * @param function        the <code>DiscourseFunction</code> each element in the list is
	 *                        to take. If this is <code>null</code> then the function is not
	 *                        set and any existing discourse function is kept.
	 */
	static void realiseList(SyntaxProcessor parent,
							ListElement realisedElement, List<NLGElement> elementList,
							DiscourseFunction function, Object discourseFunctionParent) {
		ListElement realisedList = new ListElement();
		NLGElement currentElement;

		for (NLGElement eachElement : elementList) {
			currentElement = parent.realise(eachElement);

			if (currentElement != null) {
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						function);
				setParentCase(discourseFunctionParent, currentElement);
				copyFeatures(currentElement, eachElement);
				realisedList.addComponent(currentElement);
			}
		}

		if (!realisedList.getChildren().isEmpty()) {
			realisedElement.addComponent(realisedList);
		}
	}

	/**
	 * Sets the grammatical case of the parent element to the modifier or complement.
	 * E.g. sets "SUBJECT" to an adjective which is a modifier to a subject and needs to be inflected according to the case.
	 *
	 * @param discourseFunctionParent the <code>DiscourseFunction</code> (e.g. SUBJECT) of the parent element.
	 * @param currentElement          the <code>NLGElement</code> to which the parent Discourse Function should be set
	 */

	protected static void setParentCase(Object discourseFunctionParent, NLGElement currentElement) {
		if (discourseFunctionParent != null && currentElement != null) {
			if (currentElement instanceof ListElement) {
				for (NLGElement element : currentElement.getChildren()) {
					setParentCase(discourseFunctionParent, element);
				}
			} else {
				currentElement.setFeature(InternalFeature.CASE_PARENT, discourseFunctionParent);

			}
		}
	}
}
