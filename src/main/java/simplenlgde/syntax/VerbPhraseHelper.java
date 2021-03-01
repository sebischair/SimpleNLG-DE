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
import simplenlgde.phrasespec.*;

import java.util.List;
import java.util.Stack;

/**
 * <p>
 * This class contains static methods to help the syntax processor realise verb
 * phrases. It adds auxiliary verbs into the element tree as required.
 * </p>
 *
 */
abstract class VerbPhraseHelper {


	/**
     * The main method for realising verb phrases.
     *
     * @param parent
     *            the <code>SyntaxProcessor</code> that called this method.
     * @param phrase
     *            the <code>PhraseElement</code> to be realised.
     * @return the realised <code>NLGElement</code>.
     */
    static NLGElement realise(SyntaxProcessor parent, PhraseElement phrase) {
        ListElement realisedElement = null;
        Stack<NLGElement> vgComponents = null;
        Stack<NLGElement> mainVerbRealisation = new Stack<NLGElement>();
        Stack<NLGElement> auxiliaryRealisation = new Stack<NLGElement>();
        Boolean auxiliary = false;
        Boolean postModsRealised = false;
		Boolean auxRealised = false;

        if (phrase != null) {
        	if(phrase.hasFeature(Feature.PASSIVE) && phrase.getHead() != null && phrase.getHead() instanceof VPPhraseSpec) {
        		phrase.getHead().setFeature(Feature.PASSIVE, phrase.getFeature(Feature.PASSIVE));
        		phrase.removeFeature(Feature.PASSIVE);
			}
            vgComponents = createVerbGroup(parent, phrase);
            splitVerbGroup(vgComponents, mainVerbRealisation,
                    auxiliaryRealisation);
            if(auxiliaryRealisation.size() != 0) {
            	auxiliary = true;
			}
        	if(phrase.hasFeature(InternalFeature.HEAD) && phrase.getHead().getCategory() != null && phrase.getHead().getCategory().equals(PhraseCategory.VERB_PHRASE) 
        			&& phrase.hasFeature(Feature.TENSE) && (phrase.getFeature(Feature.TENSE).equals(Tense.PERFECT) || phrase.getFeature(Feature.TENSE).equals(Tense.FUTURE))) {
        		auxiliaryRealisation =  new Stack<NLGElement>();

				if (phrase.hasFeature(InternalFeature.COMPLEMENTS)) {
					phrase.getHead().setFeature(InternalFeature.COMPLEMENTS, phrase.getFeatureAsElementList(InternalFeature.COMPLEMENTS));
					phrase.removeFeature(InternalFeature.COMPLEMENTS);
				}
        	}

            realisedElement = new ListElement();

			if(phrase.hasFeature(Feature.FORM) && phrase.hasFeature(InternalFeature.POSTMODIFIERS)) {
				List<NLGElement> postMods = phrase.getFeatureAsElementList(InternalFeature.POSTMODIFIERS);
				for (NLGElement postMod: postMods) {
					if (postMod.getCategory() != null && postMod.getCategory().equals(PhraseCategory.VERB_PHRASE) && postMod.hasFeature(InternalFeature.HEAD)
							&& postMod.getFeatureAsElement(InternalFeature.HEAD).hasFeature("participle2")
							&& postMod.getFeatureAsElement(InternalFeature.HEAD).getFeature("participle2").equals("geworden")) {
						postMod.getFeatureAsElement(InternalFeature.HEAD).setFeature("participle2", "worden");
					}
				}
			}

			if(!mainVerbRealisation.empty()) {
				for (NLGElement mainVerb: mainVerbRealisation) {
					if(mainVerb.hasFeature(Feature.CONTAINS_MODAL)) {
						realisedElement.setFeature(Feature.CONTAINS_MODAL, mainVerb.getFeatureAsBoolean(Feature.CONTAINS_MODAL));
					}
				}
			}

            if (!phrase.hasFeature(InternalFeature.REALISE_AUXILIARY)
                    || phrase.getFeatureAsBoolean(
                    InternalFeature.REALISE_AUXILIARY).booleanValue()) {
				
                PhraseHelper.realiseList(parent, realisedElement, phrase
                        .getPreModifiers(), DiscourseFunction.PRE_MODIFIER);

                if((phrase.hasFeature(InternalFeature.CLAUSE_STATUS) && phrase.getFeature(InternalFeature.CLAUSE_STATUS).equals(ClauseStatus.SUBORDINATE))) {
					realiseComplements(parent, phrase, realisedElement);

					if((phrase.hasFeature(InternalFeature.CLAUSE_STATUS) && !phrase.getFeature(InternalFeature.CLAUSE_STATUS).equals(ClauseStatus.SUBORDINATE))
					|| !phrase.hasFeature(InternalFeature.CLAUSE_STATUS)) {
						realiseAuxiliaries(parent, realisedElement,
								auxiliaryRealisation);
						auxRealised = true;
					}

					PhraseHelper.realiseList(parent, realisedElement, phrase
							.getModifiers(), DiscourseFunction.MODIFIER);

					PhraseHelper.realiseList(parent, realisedElement, phrase
							.getPostModifiers(), DiscourseFunction.POST_MODIFIER);

					postModsRealised = true;

					realiseMainVerb(parent, phrase, mainVerbRealisation,
							realisedElement);

					if(!auxRealised) {
						realiseAuxiliaries(parent, realisedElement,
								auxiliaryRealisation);
					}

				} else {
                	if(auxiliary) {
                		// place main verb behind auxiliary verb AND all complements, e.g. "hat sie gemocht"
						realiseAuxiliaries(parent, realisedElement,
								auxiliaryRealisation);

						realiseComplements(parent, phrase, realisedElement);

						realiseMainVerb(parent, phrase, mainVerbRealisation,
								realisedElement);

					} else {
						realiseAuxiliaries(parent, realisedElement,
								auxiliaryRealisation);

						realiseMainVerb(parent, phrase, mainVerbRealisation,
								realisedElement);

						realiseComplements(parent, phrase, realisedElement);
					}

					PhraseHelper.realiseList(parent, realisedElement, phrase
							.getModifiers(), DiscourseFunction.MODIFIER);

					PhraseHelper.realiseList(parent, realisedElement, phrase
							.getPostModifiers(), DiscourseFunction.POST_MODIFIER);

					postModsRealised = true;

				}


            } else {
                PhraseHelper.realiseList(parent, realisedElement, phrase
                        .getPreModifiers(), DiscourseFunction.PRE_MODIFIER);
                PhraseHelper.realiseList(parent, realisedElement, phrase
                        .getModifiers(), DiscourseFunction.MODIFIER);
                
                realiseMainVerb(parent, phrase, mainVerbRealisation,
                        realisedElement);
            }

            if(!postModsRealised) {
				PhraseHelper.realiseList(parent, realisedElement, phrase
						.getPostModifiers(), DiscourseFunction.POST_MODIFIER);
			}

        }
        if (phrase.hasFeature(LexicalFeature.SEPARABLE)) {
        	realisedElement.setFeature(LexicalFeature.SEPARABLE, phrase.getFeatureAsBoolean(LexicalFeature.SEPARABLE));
        }
		if (phrase.hasFeature(Feature.SEPARABLE_VERB)) {
			realisedElement.setFeature(Feature.SEPARABLE_VERB, phrase.getFeatureAsBoolean(Feature.SEPARABLE_VERB));
		}
		if (realisedElement.hasFeature(Feature.CONTAINS_MODAL) && realisedElement.size() > 0) {
			for (NLGElement el: realisedElement.getChildren()) {
				el.setFeature(Feature.CONTAINS_MODAL, realisedElement.getFeatureAsBoolean(Feature.CONTAINS_MODAL));
			}
		}
        return realisedElement;
    }

    /**
     * Realises the main group of verbs in the phrase.
     *
     * @param parent
     *            the parent <code>SyntaxProcessor</code> that will do the
     *            realisation of the complementiser.
     * @param phrase
     *            the <code>PhraseElement</code> representing this noun phrase.
     * @param mainVerbRealisation
     *            the stack of the main verbs in the phrase.
     * @param realisedElement
     *            the current realisation of the noun phrase.
     */
    private static void realiseMainVerb(SyntaxProcessor parent,
                                        PhraseElement phrase, Stack<NLGElement> mainVerbRealisation,
                                        ListElement realisedElement) {

        NLGElement currentElement = null;
        NLGElement main = null;

        while (!mainVerbRealisation.isEmpty()) {
            main = mainVerbRealisation.pop();
            if(phrase != null && main != null) {
            	if(phrase.hasFeature(Feature.FORM) && !main.hasFeature(Feature.FORM)) {
                	main.setFeature(Feature.FORM, phrase.getFeature(Feature.FORM));            		
            	}
				if(phrase.hasFeature(LexicalFeature.SEPARABLE)) {
					main.setFeature(LexicalFeature.SEPARABLE, phrase.getFeature(LexicalFeature.SEPARABLE));
				}
				if (phrase.hasFeature(Feature.SEPARABLE_VERB)) {
					main.setFeature(Feature.SEPARABLE_VERB, phrase.getFeatureAsBoolean(Feature.SEPARABLE_VERB));
				}
				if(phrase.hasFeature(Feature.CONTAINS_MODAL)) {
					main.setFeature(Feature.CONTAINS_MODAL, phrase.getFeature(Feature.CONTAINS_MODAL));
				}
				if(phrase.hasFeature(InternalFeature.CLAUSE_STATUS)) {
					main.setFeature(InternalFeature.CLAUSE_STATUS, phrase.getFeature(InternalFeature.CLAUSE_STATUS));
				}
				if(phrase.hasFeature(Feature.INITIATED_SUBORD)) {
					main.setFeature(Feature.INITIATED_SUBORD, phrase.getFeature(Feature.INITIATED_SUBORD));
				}
            }
            currentElement = parent.realise(main);
            if (currentElement != null) {
                realisedElement.addComponent(currentElement);
            }
        }
    }
    
	/**
	 * Realises the auxiliary verbs in the verb group.
	 * 
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 * @param auxiliaryRealisation
	 *            the stack of auxiliary verbs.
	 */
	private static void realiseAuxiliaries(SyntaxProcessor parent,
			ListElement realisedElement, Stack<NLGElement> auxiliaryRealisation) {

		NLGElement aux = null;
		NLGElement currentElement = null;
		while (!auxiliaryRealisation.isEmpty()) {
			aux = auxiliaryRealisation.pop();
			currentElement = parent.realise(aux);
			if (currentElement != null) {
				realisedElement.addComponent(currentElement);
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.AUXILIARY);
			}
		}
	}

    /**
     * Creates a stack of verbs for the verb phrase. Additional auxiliary verbs
     * are added as required based on the features of the verb phrase.
     *
     * @param parent
     *            the parent <code>SyntaxProcessor</code> that will do the
     *            realisation of the complementiser.
     * @param phrase
     *            the <code>PhraseElement</code> representing this noun phrase.
     * @return the verb group as a <code>Stack</code> of <code>NLGElement</code>
     *         s.
     */
    static final private Stack<NLGElement> createVerbGroup(
            SyntaxProcessor parent, PhraseElement phrase) {

		String actualModal = null;
		Object formValue = phrase.getFeature(Feature.FORM);
		Tense tenseValue = (Tense) phrase.getFeature(Feature.TENSE);
		String modal = phrase.getFeatureAsString(Feature.MODAL);
		boolean modalPast = false;
		Stack<NLGElement> vgComponents = new Stack<NLGElement>();
		boolean interrogative = phrase.hasFeature(Feature.INTERROGATIVE_TYPE);
		Person personValue = (Person) phrase.getFeature(Feature.PERSON);
		NumberAgreement numberValue = (NumberAgreement) phrase.getFeature(Feature.NUMBER);


		if (Form.GERUND.equals(formValue) || Form.INFINITIVE.equals(formValue)) {
			tenseValue = Tense.PRESENT;
		}

		if (formValue == null || Form.NORMAL.equals(formValue)) {
			if (modal != null) {
				actualModal = modal;

				if (Tense.PAST.equals(tenseValue)) {
					modalPast = true;
				}
			}
		}

		pushParticles(phrase, parent, vgComponents);
		NLGElement frontVG = grabHeadVerb(phrase, tenseValue, modal != null);

		if(frontVG != null) {
			if(personValue != null ) {
				frontVG.setFeature(Feature.PERSON, personValue);
			}
			if(numberValue != null) {
				frontVG.setFeature(Feature.NUMBER, numberValue);
			}
		}

		checkImperativeInfinitive(formValue, frontVG);
		
		if(Tense.PERFECT.equals(tenseValue)) {
			if (phrase.getFeatureAsBoolean(Feature.PROGRESSIVE).booleanValue()) {
				frontVG = addHave(frontVG, vgComponents, modal, Tense.PRESENT);
				if(personValue != null) {
					frontVG.setFeature(Feature.PERSON, personValue);
				}
			} else {
				frontVG = addBe(frontVG, vgComponents, Form.NORMAL);
				if(personValue != null) {
					frontVG.setFeature(Feature.PERSON, personValue);
				}
			}
		}
		if(Tense.FUTURE.equals(tenseValue)) {
			frontVG = addWerden(frontVG, vgComponents, Form.NORMAL);
			if(personValue != null) {
				frontVG.setFeature(Feature.PERSON, personValue);
			}
		}
		if (phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue()) {
			if (phrase.getFeatureAsBoolean(Feature.PROGRESSIVE).booleanValue() || Tense.PAST.equals(tenseValue)) {
				frontVG = addWerden(frontVG, vgComponents, Form.PAST_PARTICIPLE);
			} else {
				frontVG = addBe(frontVG, vgComponents, Form.PAST_PARTICIPLE);
			}
		}


		frontVG = pushIfModal(actualModal != null, phrase, frontVG,
				vgComponents);
		frontVG = createNot(phrase, vgComponents, frontVG, modal != null);

		if (frontVG != null) {
			pushFrontVerb(phrase, vgComponents, frontVG, formValue,
					interrogative);
		}
		if(actualModal != null) {
			pushModal(actualModal, phrase, vgComponents);
		}
		return vgComponents;
    }
    
	/**
	 * Pushes the particles of the main verb onto the verb group stack.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 */
	private static void pushParticles(PhraseElement phrase,
			SyntaxProcessor parent, Stack<NLGElement> vgComponents) {
		Object particle = phrase.getFeature(Feature.PARTICLE);

		if (particle instanceof String) {
			vgComponents.push(new StringElement((String) particle));

		} else if (particle instanceof NLGElement) {
			vgComponents.push(parent.realise((NLGElement) particle));
		}
	}
    
	/**
	 * Checks to see if the phrase is in imperative, infinitive or bare
	 * infinitive form. If it is then no morphology is done on the main verb.
	 * 
	 * @param formValue
	 *            the <code>Form</code> of the phrase.
	 * @param frontVG
	 *            the first verb in the verb group.
	 */
	private static void checkImperativeInfinitive(Object formValue,
			NLGElement frontVG) {

		if ((Form.IMPERATIVE.equals(formValue)
				|| Form.INFINITIVE.equals(formValue) || Form.BARE_INFINITIVE
				.equals(formValue))
				&& frontVG != null) {
			frontVG.setFeature(InternalFeature.NON_MORPH, true);
		}
	}
    
	/**
	 * Pushes the modal onto the stack of verb components.
	 * 
	 * @param actualModal
	 *            the modal to be used.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 */
	private static void pushModal(String actualModal, PhraseElement phrase,
			Stack<NLGElement> vgComponents) {
		if (actualModal != null
				&& !phrase.getFeatureAsBoolean(InternalFeature.IGNORE_MODAL)
						.booleanValue()) {
			vgComponents.push(new InflectedWordElement(actualModal,
					LexicalCategory.MODAL));
		}
	}
    
	/**
	 * Adds <em>nicht</em> to the stack if the phrase is negated.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param hasModal
	 *            the phrase has a modal
	 * @return the new element for the front of the group.
	 */
	private static NLGElement createNot(PhraseElement phrase,
			Stack<NLGElement> vgComponents, NLGElement frontVG, boolean hasModal) {
		NLGElement newFront = frontVG;

		if (phrase.getFeatureAsBoolean(Feature.NEGATED).booleanValue()) {
			if (!vgComponents.empty() || frontVG != null && isCopular(frontVG)) {
				vgComponents.push(new InflectedWordElement(
						"nicht", LexicalCategory.ADVERB));
			} else {
				if (frontVG != null && !hasModal) {
					frontVG.setFeature(Feature.NEGATED, true);
					vgComponents.push(frontVG);
				}

				vgComponents.push(new InflectedWordElement(
						"nicht", LexicalCategory.ADVERB));
			}
		}

		return newFront;
	}
	
	/**
	 * Checks to see if the base form of the word is copular, i.e. <em>be</em>.
	 * 
	 * @param element
	 *            the element to be checked
	 * @return <code>true</code> if the element is copular.
	 */
	public static boolean isCopular(NLGElement element) {
		boolean copular = false;

		if (element instanceof InflectedWordElement) {
			copular = "sein".equalsIgnoreCase(((InflectedWordElement) element) //$NON-NLS-1$
					.getBaseForm());

		} else if (element instanceof WordElement) {
			copular = "sein".equalsIgnoreCase(((WordElement) element) //$NON-NLS-1$
					.getBaseForm());

		} else if (element instanceof PhraseElement) {
			// get the head and check if it's "sein"
			NLGElement head = element instanceof SPhraseSpec ? ((SPhraseSpec) element)
					.getVerb()
					: ((PhraseElement) element).getHead();

			if (head != null) {
				copular = (head instanceof WordElement && "sein"
						.equals(((WordElement) head).getBaseForm()));
			}
		}

		return copular;
	}
    
	/**
	 * Pushes the front verb on to the stack if the phrase has a modal.
	 * 
	 * @param hasModal
	 *            the phrase has a modal
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @return the new element for the front of the group.
	 */
	private static NLGElement pushIfModal(boolean hasModal,
			PhraseElement phrase, NLGElement frontVG,
			Stack<NLGElement> vgComponents) {

		NLGElement newFront = frontVG;
		if (hasModal
				&& !phrase.getFeatureAsBoolean(InternalFeature.IGNORE_MODAL)
						.booleanValue()) {
			if (frontVG != null) {
				frontVG.setFeature(InternalFeature.NON_MORPH, true);
				vgComponents.push(frontVG);
			}
			newFront = null;
		}
		return newFront;
	}
    
	/**
	 * Adds the <em>sein</em> verb to the front of the group.
	 * 
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param frontForm
	 *            the form the current front verb is to take.
	 * @return the new element for the front of the group.
	 */
	private static NLGElement addBe(NLGElement frontVG,
			Stack<NLGElement> vgComponents, Form frontForm) {

		if (frontVG != null) {
			frontVG.setFeature(Feature.FORM, frontForm);
			vgComponents.push(frontVG);
		}
		return new InflectedWordElement("sein", LexicalCategory.VERB);
	}

	/**
	 * Adds the <em>werden</em> verb to the front of the group.
	 *
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param frontForm
	 *            the form the current front verb is to take.
	 * @return the new element for the front of the group.
	 */
	private static NLGElement addWerden(NLGElement frontVG,
									Stack<NLGElement> vgComponents, Form frontForm) {

		if (frontVG != null) {
			frontVG.setFeature(Feature.FORM, frontForm);
			vgComponents.push(frontVG);
		}
		return new InflectedWordElement("werden", LexicalCategory.VERB);
	}
	
	/**
	 * Adds <em>haben</em> to the stack.
	 * 
	 * @param frontVG
	 *            the first verb in the verb group.
	 * @param vgComponents
	 *            the stack of verb components in the verb group.
	 * @param modal
	 *            the modal to be used.
	 * @param tenseValue
	 *            the <code>Tense</code> of the phrase.
	 * @return the new element for the front of the group.
	 */
	private static NLGElement addHave(NLGElement frontVG,
			Stack<NLGElement> vgComponents, String modal, Tense tenseValue) {
		NLGElement newFront = frontVG;

		if (frontVG != null) {
			//frontVG.setFeature(Feature.FORM, Form.PAST_PARTICIPLE);
			vgComponents.push(frontVG);
		}
		newFront = new InflectedWordElement("haben", LexicalCategory.VERB);
		newFront.setFeature(Feature.TENSE, tenseValue);
		if (modal != null) {
			newFront.setFeature(InternalFeature.NON_MORPH, true);
		}
		return newFront;
	}

    /**
     * Grabs the head verb of the verb phrase and sets it to future tense if the
     * phrase is future tense. It also turns off negation if the group has a
     * modal.
     *
     * @param phrase
     *            the <code>PhraseElement</code> representing this noun phrase.
     * @param tenseValue
     *            the <code>Tense</code> of the phrase.
     * @param hasModal
     *            <code>true</code> if the verb phrase has a modal.
     * @return the modified head element
     */
    private static NLGElement grabHeadVerb(PhraseElement phrase,
                                           Tense tenseValue, boolean hasModal) {
        NLGElement frontVG = phrase.getHead();

        if (frontVG != null) {
            if (frontVG instanceof WordElement) {
                frontVG = new InflectedWordElement((WordElement) frontVG);
            }

            if (tenseValue != null) {
                frontVG.setFeature(Feature.TENSE, tenseValue);
            }
        }

        return frontVG;
    }

    /**
     * Pushes the front verb onto the stack of verb components.
     *
     * @param phrase
     *            the <code>PhraseElement</code> representing this noun phrase.
     * @param vgComponents
     *            the stack of verb components in the verb group.
     * @param frontVG
     *            the first verb in the verb group.
     * @param formValue
     *            the <code>Form</code> of the phrase.
     * @param interrogative
     *            <code>true</code> if the phrase is interrogative.
     */
    private static void pushFrontVerb(PhraseElement phrase,
                                      Stack<NLGElement> vgComponents, NLGElement frontVG,
                                      Object formValue, boolean interrogative) {
        if ((!(formValue == null || Form.NORMAL.equals(formValue)) || interrogative)) {
            vgComponents.push(frontVG);

        } else {
            NumberAgreement numToUse = determineNumber(phrase.getParent(),
                    phrase);
            frontVG.setFeature(Feature.TENSE, phrase.getFeature(Feature.TENSE));
            frontVG.setFeature(Feature.PERSON, phrase
                    .getFeature(Feature.PERSON));
            frontVG.setFeature(Feature.NUMBER, numToUse);
            vgComponents.push(frontVG);
        }
    }

    /**
     * Determines the number agreement for the phrase ensuring that any number
     * agreement on the parent element is inherited by the phrase.
     *
     * @param parent
     *            the parent element of the phrase.
     * @param phrase
     *            the <code>PhraseElement</code> representing this noun phrase.
     * @return the <code>NumberAgreement</code> to be used for the phrase.
     */
    private static NumberAgreement determineNumber(NLGElement parent,
                                                   PhraseElement phrase) {
        Object numberValue = phrase.getFeature(Feature.NUMBER);
        NumberAgreement number = null;
        if (numberValue != null && numberValue instanceof NumberAgreement) {
            number = (NumberAgreement) numberValue;
        } else {
            number = NumberAgreement.SINGULAR;
        }
        return number;
    }

    /**
     * Splits the stack of verb components into two sections. One being the verb
     * associated with the main verb group, the other being associated with the
     * auxiliary verb group.
     *
     * @param vgComponents
     *            the stack of verb components in the verb group.
     * @param mainVerbRealisation
     *            the main group of verbs.
     * @param auxiliaryRealisation
     *            the auxiliary group of verbs.
     */
    private static void splitVerbGroup(Stack<NLGElement> vgComponents,
                                       Stack<NLGElement> mainVerbRealisation,
                                       Stack<NLGElement> auxiliaryRealisation) {

        boolean mainVerbSeen = false;

        for (NLGElement word : vgComponents) {
            if (!mainVerbSeen) {
                mainVerbRealisation.push(word);
                if (!word.equals("not") && !word.isA(LexicalCategory.PRONOUN)) {
                    mainVerbSeen = true;
                }
            } else {
                auxiliaryRealisation.push(word);
            }
        }
    }

    /**
     * Realises the complements of this phrase.
     *
     * @param parent
     *            the parent <code>SyntaxProcessor</code> that will do the
     *            realisation of the complementiser.
     * @param phrase
     *            the <code>PhraseElement</code> representing this noun phrase.
     * @param realisedElement
     *            the current realisation of the noun phrase.
     */
    private static void realiseComplements(SyntaxProcessor parent,
                                           PhraseElement phrase, ListElement realisedElement) {

        ListElement indirects = new ListElement();
        ListElement directs = new ListElement();
        ListElement unknowns = new ListElement();
        Object discourseValue = null;
        NLGElement currentElement = null;

        for (NLGElement complement : phrase
                .getFeatureAsElementList(InternalFeature.COMPLEMENTS)) {

            discourseValue = complement
                    .getFeature(InternalFeature.DISCOURSE_FUNCTION);
            currentElement = parent.realise(complement);
            if (currentElement != null) {
                currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
                        DiscourseFunction.COMPLEMENT);
                if(complement.hasFeature(InternalFeature.INBETWEEN_VERB)) {
                	currentElement.setFeature(InternalFeature.INBETWEEN_VERB, complement.getFeatureAsBoolean(InternalFeature.INBETWEEN_VERB));
                }
                if(complement.hasFeature(InternalFeature.CASE)) {
                	currentElement.setFeature(InternalFeature.CASE, complement.getFeature(InternalFeature.CASE));
                }
                if(complement.hasFeature(InternalFeature.CLAUSE_STATUS)) {
                	currentElement.setFeature(InternalFeature.CLAUSE_STATUS, complement.getFeature(InternalFeature.CLAUSE_STATUS));
                }
                if (DiscourseFunction.INDIRECT_OBJECT.equals(discourseValue)) {
                    indirects.addComponent(currentElement);
                } else if (DiscourseFunction.OBJECT.equals(discourseValue)) {
                    directs.addComponent(currentElement);
                } else {
                    unknowns.addComponent(currentElement);
                }
            }
        }
        realisedElement.addComponents(directs.getChildren());
        realisedElement.addComponents(indirects.getChildren());
        realisedElement.addComponents(unknowns.getChildren());
        
    }

}
