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

import java.util.ArrayList;
import java.util.List;

import simplenlgde.framework.*;
import simplenlgde.features.*;
import simplenlgde.syntax.SyntaxProcessor;



/**
 * <p>
 * This is the processor for handling morphology within the SimpleNLG. The
 * processor inflects words form the base form depending on the features applied
 * to the word. For example, <em>steigen</em> is inflected to <em>stieg</em> for
 * past tense, <em>Ertrag</em> is inflected to <em>Erträge</em> for
 * pluralisation.
 * </p>
 *
 * <p>
 * As a matter of course, the processor will first use any user-defined
 * inflection for the world. If no inflection is provided then the lexicon, if
 * it exists, will be examined for the correct inflection. Failing this a set of
 * very basic rules will be examined to inflect the word.
 * </p>
 *
 * <p>
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

public class MorphologyProcessor extends NLGModule {

	SyntaxProcessor syntaxHelper = new SyntaxProcessor();

	@Override
	public void initialise() {
		// Do nothing
	}

	@Override
	public NLGElement realise(NLGElement element) {
		NLGElement realisedElement = null;

		if (element instanceof InflectedWordElement) {
			realisedElement = doMorphology((InflectedWordElement) element);

		} else if (element instanceof StringElement) {
			realisedElement = element;

		} else if (element instanceof DocumentElement) {
			List<NLGElement> children = element.getChildren();
			((DocumentElement) element).setComponents(realise(children));
			realisedElement = element;

		} else if (element instanceof ListElement) {
			realisedElement = new ListElement();
			List<NLGElement> children = element.getChildren();
			if(children != null) {
                if (element.hasFeature(InternalFeature.CLAUSE_STATUS)) {
                    for (NLGElement child : children) {
                        child.setFeature(InternalFeature.CLAUSE_STATUS, element.getFeature(InternalFeature.CLAUSE_STATUS));
                    }
                }
            }
			((ListElement) realisedElement).addComponents(realise(children));
		} else if (element instanceof CoordinatedPhraseElement) {
			List<NLGElement> children = element.getChildren();
			((CoordinatedPhraseElement) element).clearCoordinates();

			if (children != null && children.size() > 0) {
				((CoordinatedPhraseElement) element).addCoordinate(realise(children.get(0)));

				for (int index = 1; index < children.size(); index++) {
					((CoordinatedPhraseElement) element).addCoordinate(realise(children.get(index)));
				}

				realisedElement = element;
			}

		} else if (element != null) {
			realisedElement = element;
		}
		if (element.getFeature(InternalFeature.INBETWEEN_VERB) != null) {
			realisedElement.setFeature(InternalFeature.INBETWEEN_VERB,
					element.getFeature(InternalFeature.INBETWEEN_VERB));
		}
		// If the sentence contains a separable verb, the word order changes.
		// This must be done after the verb is conjugated.
		if (element.hasFeature(LexicalFeature.SEPARABLE) && element.getFeatureAsBoolean(LexicalFeature.SEPARABLE)
				&& element.hasFeature(InternalFeature.DISCOURSE_FUNCTION)
				&& element.getFeature(InternalFeature.DISCOURSE_FUNCTION).equals(DiscourseFunction.VERB_PHRASE)) {
			List<NLGElement> verb = new ArrayList<NLGElement>();
			List<NLGElement> verbModifiers = new ArrayList<NLGElement>();
			List<NLGElement> subordinates = new ArrayList<NLGElement>();
			syntaxHelper.getSeparableVerbComponents(realisedElement, verb, verbModifiers);
			syntaxHelper.realiseSeparableVerbPhrase(realisedElement, verb, verbModifiers, subordinates);
		}
		return realisedElement;
	}

	@Override
	public List<NLGElement> realise(List<NLGElement> elements) {
		List<NLGElement> realisedElements = new ArrayList<NLGElement>();
		NLGElement currentElement = null;
		NLGElement determiner = null;
		NLGElement prevElement = null;

		if (elements != null) {
			for (NLGElement eachElement : elements) {
				if(prevElement != null && prevElement.hasFeature(InternalFeature.COMPONENTS)) {
					for (NLGElement el: prevElement.getFeatureAsElementList(InternalFeature.COMPONENTS)) {
						if(el.hasFeature("base_form") &&
								el.getFeatureAsString("base_form").equals("als")) {
							if(eachElement.hasFeature(InternalFeature.COMPONENTS)) {
								for (NLGElement eachEl: eachElement.getFeatureAsElementList(InternalFeature.COMPONENTS)) {
									eachEl.setFeature(Feature.ARTICLE_FORM, ArticleForm.DEFINITE);
								}
							}
						}
					}
				}
				else if(prevElement != null && prevElement.hasFeature("base_form")
						&& prevElement.getFeatureAsString("base_form").equals("als")) {
					if(eachElement.hasFeature(InternalFeature.COMPONENTS)) {
						for (NLGElement el: eachElement.getFeatureAsElementList(InternalFeature.COMPONENTS)) {
							el.setFeature(Feature.ARTICLE_FORM, ArticleForm.DEFINITE);
						}
					}
				}
				currentElement = realise(eachElement);
				if (currentElement != null) {
					// pass the discourse function and appositive features -- important for orth
					// processor
					currentElement.setFeature(Feature.APPOSITIVE, eachElement.getFeature(Feature.APPOSITIVE));
					Object function = eachElement.getFeature(InternalFeature.DISCOURSE_FUNCTION);

					if (eachElement.hasFeature(LexicalFeature.SEPARABLE)) {
						currentElement.setFeature(LexicalFeature.SEPARABLE,
								eachElement.getFeatureAsBoolean(LexicalFeature.SEPARABLE));
					}
					if (eachElement.hasFeature(InternalFeature.CASE)) {
						currentElement.setFeature(InternalFeature.CASE,
								eachElement.getFeature(InternalFeature.CASE));
					}
					if (eachElement.hasFeature(InternalFeature.CLAUSE_STATUS)) {
						currentElement.setFeature(InternalFeature.CLAUSE_STATUS,
								eachElement.getFeature(InternalFeature.CLAUSE_STATUS));
					}
					if (eachElement.hasFeature(Feature.INITIATED_SUBORD)) {
						currentElement.setFeature(Feature.INITIATED_SUBORD,
								eachElement.getFeature(Feature.INITIATED_SUBORD));
					}
					if (eachElement.hasFeature(InternalFeature.MERGED_ARTICLE)) {
						currentElement.setFeature(InternalFeature.MERGED_ARTICLE,
								eachElement.getFeature(InternalFeature.MERGED_ARTICLE));
					}
					if (eachElement.hasFeature(Feature.IS_SUPERLATIVE)) {
						currentElement.setFeature(Feature.IS_SUPERLATIVE,
								eachElement.getFeature(Feature.IS_SUPERLATIVE));
					}
					if (eachElement.hasFeature(Feature.IS_COMPARATIVE)) {
						currentElement.setFeature(Feature.IS_COMPARATIVE,
								eachElement.getFeature(Feature.IS_COMPARATIVE));
					}
					if (eachElement.hasFeature(Feature.ARTICLE_FORM)) {
						currentElement.setFeature(Feature.ARTICLE_FORM,
								eachElement.getFeature(Feature.ARTICLE_FORM));
					}
					if (eachElement.hasFeature(Feature.APPOSITIVE)) {
						currentElement.setFeature(Feature.APPOSITIVE,
								eachElement.getFeature(Feature.APPOSITIVE));
					}
					if (function != null) {
						currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, function);
					}
					realisedElements.add(currentElement);

					if (determiner == null && DiscourseFunction.SPECIFIER
							.equals(currentElement.getFeature(InternalFeature.DISCOURSE_FUNCTION))) {
						determiner = currentElement;
						determiner.setFeature(Feature.NUMBER, eachElement.getFeature(Feature.NUMBER));

					} else if (determiner != null) {
						determiner = null;
					}
				}
				prevElement = eachElement;
			}
		}

		return realisedElements;
	}

	/**
	 * This is the main method for performing the morphology. It effectively
	 * examines the lexical category of the element and calls the relevant set of
	 * rules from <code>MorphologyRules</em>.
	 *
	 * @param element the <code>InflectedWordElement</code>
	 * 
	 * @return an <code>NLGElement</code> reflecting the correct inflection for the
	 *         word.
	 */
	private NLGElement doMorphology(InflectedWordElement element) {
		NLGElement realisedElement = null;
		if (element.getFeatureAsBoolean(InternalFeature.NON_MORPH).booleanValue()) {
			realisedElement = new StringElement(element.getBaseForm());
			realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
					element.getFeature(InternalFeature.DISCOURSE_FUNCTION));

		} else {
			NLGElement baseWord = element.getFeatureAsElement(InternalFeature.BASE_WORD);

			if (baseWord == null && this.lexicon != null) {
				baseWord = this.lexicon.lookupWord(element.getBaseForm());
			}

			ElementCategory category = element.getCategory();
			
			if (category instanceof LexicalCategory) {
				switch ((LexicalCategory) category) {

				case NOUN:
					realisedElement = MorphologyRules.doNounMorphology(element, (WordElement) baseWord);
					break;

				case VERB:
				case MODAL:
					realisedElement = MorphologyRules.doVerbMorphology(element, (WordElement) baseWord);
					break;
					
				case ADVERB:
 					realisedElement = MorphologyRules.doAdjectiveCompSup(element, (WordElement) baseWord);
					break;

				case ADJECTIVE:
					if(element.hasFeature(InternalFeature.DISCOURSE_FUNCTION) && (
							element.getFeature(InternalFeature.DISCOURSE_FUNCTION).equals(DiscourseFunction.MODIFIER))) {
						realisedElement = MorphologyRules.doAdjectiveMorphology(element, (WordElement) baseWord);
					} else {
						realisedElement = MorphologyRules.doAdjectiveCompSup(element, (WordElement) baseWord);
					}
					realisedElement.setCategory(LexicalCategory.ADJECTIVE);
					if(element.hasFeature(InternalFeature.COMPOSITE)) {
						realisedElement.setFeature(InternalFeature.COMPOSITE, element.getFeatureAsBoolean(InternalFeature.COMPOSITE));
					} else {
						realisedElement.setFeature(InternalFeature.COMPOSITE, false);
					}
					break;

				case ARTICLE_INDEFINITE:
				case ARTICLE_DEFINITE:
					realisedElement = MorphologyRules.doArticleInflection(element, (WordElement) baseWord);
					break;

				case INDEFINITE_PRONOUN:
					realisedElement = MorphologyRules.doIndefPronounMorphology(element, (WordElement) baseWord);
					break;

				default:
			        String realised = element.getBaseForm();
		            if (element.getFeatureAsBoolean("composite")) {
		                // inflection for compound words, e.g. "die Russische Föderation"
		                realised = " " + realised.substring(0, 1).toUpperCase() + realised.substring(1);
		            }
					realisedElement = new StringElement(realised);
			        if(realised.equals("als")) {
			        	realisedElement.setFeature(Feature.ARTICLE_FORM, ArticleForm.DEFINITE);
			        }
					realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
							element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
					if (element.hasFeature(Feature.APPOSITIVE)) {
						realisedElement.setFeature(Feature.APPOSITIVE, element.getFeature(Feature.APPOSITIVE));
					}
				}
			}
		}
		return realisedElement;
	}

}