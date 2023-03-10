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
import simplenlgde.framework.NLGFactory;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * This is a helper class containing the main methods for realising the syntax
 * of clauses. It is used exclusively by the <code>SyntaxProcessor</code>.
 * </p>
 *
 */
abstract class ClauseHelper {

	/**
	 * The main method for controlling the syntax realisation of clauses.
	 *
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that called this
	 *            method.
	 * @param phrase
	 *            the <code>PhraseElement</code> representation of the clause.
	 * @return the <code>NLGElement</code> representing the realised clause.
	 */
	static NLGElement realise(SyntaxProcessor parent, PhraseElement phrase) {
		ListElement realisedElement = null;
		NLGElement splitVerb = null;
		boolean interrogObj = false;
		NLGFactory nlgFactory = phrase.getFactory();

		if(phrase != null) {
			realisedElement = new ListElement();
			NLGElement verbElement = phrase.getFeatureAsElement(InternalFeature.VERB_PHRASE);
			if(verbElement == null) {
				verbElement = phrase.getHead();
			}

			checkSubjectNumberPerson(phrase, verbElement);
			checkDiscourseFunction(phrase);
			copyFrontModifiers(phrase, verbElement);
			addComplementiser(phrase, parent, realisedElement);
			addCuePhrase(phrase, parent, realisedElement);

			PhraseHelper.realiseList(parent,
						realisedElement,
						phrase.getFeatureAsElementList(InternalFeature.FRONT_MODIFIERS),
						DiscourseFunction.FRONT_MODIFIER);

			PhraseHelper.realiseList(parent,
						realisedElement,
						phrase.getFeatureAsElementList(InternalFeature.PREMODIFIERS),
						DiscourseFunction.PRE_MODIFIER);

			if(phrase.hasFeature(Feature.INTERROGATIVE_TYPE)) {
				//addSubjectsToFront(phrase, parent, realisedElement, splitVerb);

				realiseInterrogative(phrase, parent, realisedElement, nlgFactory, verbElement);
			}
			else if(phrase.getFeature(InternalFeature.FRONT_MODIFIERS) != null) {
				NLGElement passiveSplitVerb = addPassiveComplementsNumberPerson(phrase,
						parent,
						realisedElement,
						verbElement);
				if(passiveSplitVerb != null) {
					splitVerb = passiveSplitVerb;
				}
			  	realiseVerb(phrase, parent, realisedElement, splitVerb, verbElement, interrogObj);
			} else {
				addSubjectsToFront(phrase, parent, realisedElement, splitVerb);
				NLGElement passiveSplitVerb = addPassiveComplementsNumberPerson(phrase,
						parent,
						realisedElement,
						verbElement);
				if(passiveSplitVerb != null) {
					splitVerb = passiveSplitVerb;
				}
				realiseVerb(phrase, parent, realisedElement, splitVerb, verbElement, interrogObj);
			}
		}

		return realisedElement;
	}

	/**
	 * Checks the subjects of the phrase to determine if there is more than one
	 * subject. This ensures that the verb phrase is correctly set. Also set
	 * person correctly
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 */
	private static void checkSubjectNumberPerson(PhraseElement phrase, NLGElement verbElement) {
		NLGElement currentElement;
		List<NLGElement> subjects = phrase.getFeatureAsElementList(InternalFeature.SUBJECTS);
		boolean pluralSubjects = false;
		Person person = null;

		if(subjects != null) {
			switch(subjects.size()){
			case 0 :
				break;

			case 1 :
				currentElement = subjects.get(0);

				if(currentElement instanceof CoordinatedPhraseElement
						&& ((CoordinatedPhraseElement) currentElement).checkIfPlural()
				|| (currentElement.hasFeature(InternalFeature.HEAD) && currentElement.getFeatureAsElement(InternalFeature.HEAD).hasFeature("plural"))
						&& currentElement.getFeatureAsElement(InternalFeature.HEAD).getFeature("plural").equals("Prozente"))
					pluralSubjects = true;
				else if((currentElement.getFeature(Feature.NUMBER) == NumberAgreement.PLURAL)
						&& !(currentElement instanceof SPhraseSpec)) {
					// ER mod-clauses are singular as NPs, even if they are plural internally
					pluralSubjects = true;
					person = (Person) currentElement.getFeature(Feature.PERSON);
				}
				else if(currentElement.isA(PhraseCategory.NOUN_PHRASE)) {
					NLGElement currentHead = currentElement.getFeatureAsElement(InternalFeature.HEAD);
					person = (Person) currentElement.getFeature(Feature.PERSON);
					if(currentHead == null) {
						// subject is null and therefore is not gonna be plural
						pluralSubjects = false;
					} else if((currentHead.getFeature(Feature.NUMBER) == NumberAgreement.PLURAL))
						pluralSubjects = true;
					else if(currentHead instanceof ListElement) {
						pluralSubjects = true;
					}
				}
				break;

			default :
				pluralSubjects = true;
				break;
			}
		}
		if(verbElement != null) {
			verbElement.setFeature(Feature.NUMBER, pluralSubjects ? NumberAgreement.PLURAL
					: phrase.getFeature(Feature.NUMBER));
			if(person != null)
				verbElement.setFeature(Feature.PERSON, person);
		}
	}

	/**
	 * Adds the subjects to the beginning of the clause unless the clause is
	 * infinitive or imperative, contains front modifiers or the subjects split the verb.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param splitVerb
	 *            an <code>NLGElement</code> representing the subjects that
	 *            should split the verb
	 */
	private static void addSubjectsToFront(PhraseElement phrase,
			SyntaxProcessor parent,
			ListElement realisedElement,
			NLGElement splitVerb) {
		if(!Form.INFINITIVE.equals(phrase.getFeature(Feature.FORM))
				&& !Form.IMPERATIVE.equals(phrase.getFeature(Feature.FORM))
				&& splitVerb == null) {
			realisedElement.addComponents(realiseSubjects(phrase, parent).getChildren());
		}
	}

	/**
	 * Realises the subjects for the clause.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 */
	private static ListElement realiseSubjects(PhraseElement phrase, SyntaxProcessor parent) {
		NLGElement currentElement;
		ListElement realisedElement = new ListElement();

		for(NLGElement subject : phrase.getFeatureAsElementList(InternalFeature.SUBJECTS)) {

			subject.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.SUBJECT);
			currentElement = parent.realise(subject);
			if(currentElement != null) {
				realisedElement.addComponent(currentElement);
			}
		}
		return realisedElement;
	}

	/**
	 * Realises the verb part of the clause.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param splitVerb
	 *            an <code>NLGElement</code> representing the subjects that
	 *            should split the verb
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 * @param whObj
	 *            whether the VP is part of an object WH-interrogative
	 */
	private static void realiseVerb(PhraseElement phrase,
			SyntaxProcessor parent,
			ListElement realisedElement,
			NLGElement splitVerb,
			NLGElement verbElement,
			boolean whObj) {

		// information if verb is in initiated subordinate clause, needed for verb morphology
		if(phrase.hasFeature(InternalFeature.CLAUSE_STATUS) &&
				phrase.getFeature(InternalFeature.CLAUSE_STATUS).equals(ClauseStatus.SUBORDINATE)) {
			verbElement.setFeature(InternalFeature.CLAUSE_STATUS, ClauseStatus.SUBORDINATE);
			if(phrase.hasFeature(Feature.COMPLEMENTISER) && !phrase.getFeature(Feature.COMPLEMENTISER).equals("und")) {
				verbElement.setFeature(Feature.INITIATED_SUBORD, true);
			}
		}
		NLGElement currentElement = parent.realise(verbElement);

		if(currentElement != null) {
			if(splitVerb == null) {
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.VERB_PHRASE);
			} else {
				if(currentElement instanceof ListElement) {
					List<NLGElement> children = currentElement.getChildren();
					currentElement = children.get(0);
					currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.VERB_PHRASE);
					realisedElement.addComponent(currentElement);
					realisedElement.addComponent(splitVerb);

					for(int eachChild = 1; eachChild < children.size(); eachChild++ ) {
						currentElement = children.get(eachChild);
						currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.VERB_PHRASE);
						realisedElement.addComponent(currentElement);
					}
				} else {
					currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.VERB_PHRASE);

					if(whObj) {
						realisedElement.addComponent(currentElement);
						realisedElement.addComponent(splitVerb);
					} else {
						realisedElement.addComponent(splitVerb);
						realisedElement.addComponent(currentElement);
					}
				}
			}
		}

		// If phrase has front modifiers like objects, location or time,
		// the subject must be placed behind the finite verb
		if(phrase.getFeature(InternalFeature.FRONT_MODIFIERS)!=null) {
			ListElement subjects = realiseSubjects(phrase, parent);
			List<NLGElement> components = currentElement.getFeatureAsElementList(InternalFeature.COMPONENTS);
			List<NLGElement> verbPhrase = new ArrayList<NLGElement>();
			List<NLGElement> complements =  new ArrayList<NLGElement>();
			List<NLGElement> preModifiers =  new ArrayList<NLGElement>();
			List<NLGElement> modifiers =  new ArrayList<NLGElement>();
			List<NLGElement> objects =  new ArrayList<NLGElement>();
			List<NLGElement> indirectObjects =  new ArrayList<NLGElement>();
			List<NLGElement> subordinateClauses =  new ArrayList<NLGElement>();

			if(!components.isEmpty()) {
				for(NLGElement component: components) {
					addModifersComplements(component,verbPhrase, complements, modifiers, objects, subordinateClauses, preModifiers, indirectObjects);
				}
				//add mofified verb phrase to whole phrase again
				verbPhrase.addAll(0, preModifiers);
				verbPhrase.addAll(complements);
				verbPhrase.add(subjects);
				verbPhrase.addAll(modifiers);
				verbPhrase.addAll(indirectObjects);
				verbPhrase.addAll(objects);
				verbPhrase.addAll(subordinateClauses);
				currentElement.setFeature(InternalFeature.COMPONENTS, verbPhrase);
				realisedElement.addComponent(currentElement);
			} else {
				// verb has no modifiers
				realisedElement.addComponent(currentElement);
				realisedElement.addComponents(subjects.getChildren());
			}
		}
		else {
			realisedElement.addComponent(currentElement);
		}
		if(currentElement.hasFeature(Feature.CONTAINS_MODAL)) {
		    realisedElement.setFeature(Feature.CONTAINS_MODAL, currentElement.getFeatureAsBoolean(Feature.CONTAINS_MODAL));
        }
	}

	/**
	 * This is the main controlling method for handling interrogative clauses.
	 * The actual steps taken are dependent on the type of question being asked.
	 * The method also determines if there is a subject that will split the verb
	 * group of the clause. For example, the clause
	 * <em>the man <b>should give</b> the woman the flower</em> has the verb
	 * group indicated in <b>bold</b>. The phrase is rearranged as yes/no
	 * question as
	 * <em><b>should</b> the man <b>give</b> the woman the flower</em> with the
	 * subject <em>the man</em> splitting the verb group.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 * @return an <code>NLGElement</code> representing a subject that should
	 *         split the verb
	 */
	private static void realiseInterrogative(PhraseElement phrase,
												   SyntaxProcessor parent,
												   ListElement realisedElement,
												   NLGFactory phraseFactory,
												   NLGElement verbElement) {

		if(phrase.getParent() != null) {
			phrase.getParent().setFeature(InternalFeature.INTERROGATIVE, true);
		}

		Object type = phrase.getFeature(Feature.INTERROGATIVE_TYPE);

		if(type instanceof InterrogativeType) {
			switch ((InterrogativeType) type) {
				case YES_NO:
					realiseYesNo(phrase, parent, realisedElement, verbElement);
					break;
				case WHO_SUBJECT:
					realiseWhoWhatSubject(phrase, parent, realisedElement, phraseFactory, verbElement);
					break;
				case WHAT_SUBJECT:
					realiseWhoWhatSubject(phrase, parent, realisedElement, phraseFactory, verbElement);
					break;
				case WHAT_OBJECT:
					realiseWhoWhatObject(phrase, parent, realisedElement, phraseFactory, verbElement);
					break;
				case WHO_OBJECT:
					realiseWhoWhatObject(phrase, parent, realisedElement, phraseFactory, verbElement);
					break;
				case HOW:
					realiseHow(phrase, parent, realisedElement, phraseFactory, verbElement);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Performs the realisation for YES/NO types of questions. The phrase is rearranged as yes/no
	 * question.
	 *
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 * @return an <code>NLGElement</code> representing a subject that should
	 *         split the verb
	 */
	private static void realiseYesNo(PhraseElement phrase,
										   SyntaxProcessor parent,
										   ListElement realisedElement,
										   NLGElement verbElement) {
		NLGElement splitVerb = null;

		//change word order to generate question
		verbElement.setFeature(InternalFeature.POSTMODIFIERS, verbElement.getFeatureAsElementList(InternalFeature.COMPLEMENTS));
		verbElement.setFeature(InternalFeature.COMPLEMENTS, phrase.getFeatureAsElementList(InternalFeature.SUBJECTS));

		List<NLGElement> modifiers = verbElement.getFeatureAsElement(InternalFeature.HEAD).getFeatureAsElementList(InternalFeature.MODIFIERS);
		verbElement.getFeatureAsElement(InternalFeature.HEAD).setFeature(InternalFeature.MODIFIERS,null);
		for (NLGElement mod: modifiers) {
			phrase.addComplement(mod);

		}

		realiseVerb(phrase, parent, realisedElement, splitVerb, verbElement, false);
	}


	/**
	 * Performs the realisation for HOW types of questions. The word how is added to the front of the sentence and the word order is changed.
	 *
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 * 	          the phrase factory to be used.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 * @return an <code>NLGElement</code> representing a subject that should
	 *         split the verb
	 */
	private static void realiseHow(PhraseElement phrase,
									 SyntaxProcessor parent,
									 ListElement realisedElement,
									 NLGFactory phraseFactory,
									 NLGElement verbElement) {
		NLGElement splitVerb = null;

		//change word order
		verbElement.setFeature(InternalFeature.POSTMODIFIERS, verbElement.getFeatureAsElementList(InternalFeature.COMPLEMENTS));
		verbElement.setFeature(InternalFeature.COMPLEMENTS, phrase.getFeatureAsElementList(InternalFeature.SUBJECTS));

		//add word how
		NLGElement how = phraseFactory.createWord("wie",LexicalCategory.ADJECTIVE);
		verbElement.setFeature(InternalFeature.PREMODIFIERS, how);

		List<NLGElement> modifiers = verbElement.getFeatureAsElement(InternalFeature.HEAD).getFeatureAsElementList(InternalFeature.MODIFIERS);
		verbElement.getFeatureAsElement(InternalFeature.HEAD).setFeature(InternalFeature.MODIFIERS,null);
		for (NLGElement mod: modifiers) {
			phrase.addComplement(mod);

		}

		realiseVerb(phrase, parent, realisedElement, splitVerb, verbElement, false);
	}

	/**
	 * Performs the realisation for questions about the subject of a sentence.
	 * The original subject is replaced by a question word.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 * @return an <code>NLGElement</code> representing a subject that should
	 *         split the verb
	 */
	private static void realiseWhoWhatSubject(PhraseElement phrase,
									 SyntaxProcessor parent,
									 ListElement realisedElement,
									 NLGFactory phraseFactory,
									 NLGElement verbElement) {
		NLGElement splitVerb = null;

		NLGElement subject = null;
		if (phrase.getFeatureAsString(Feature.INTERROGATIVE_TYPE).equals("WHO_SUBJECT")){
			subject = phraseFactory.createWord("wer",LexicalCategory.PRONOUN);
		}
		else if(phrase.getFeatureAsString(Feature.INTERROGATIVE_TYPE).equals("WHAT_SUBJECT")){
			subject = phraseFactory.createWord("was",LexicalCategory.PRONOUN);
		}
		phrase.setFeature(InternalFeature.SUBJECTS, subject);

		addSubjectsToFront(phrase, parent, realisedElement, splitVerb);
		realiseVerb(phrase, parent, realisedElement, splitVerb, verbElement, false);
	}

	/**
	 * Performs the realisation for questions about the object of a sentence.
	 * The original subject is transformed into the object and the subject is replaced by a question word.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 * @return an <code>NLGElement</code> representing a subject that should
	 *         split the verb
	 */
	private static void realiseWhoWhatObject(PhraseElement phrase,
											  SyntaxProcessor parent,
											  ListElement realisedElement,
											  NLGFactory phraseFactory,
											  NLGElement verbElement) {
		NLGElement splitVerb = null;

		List<NLGElement> subjects = phrase.getFeatureAsElementList(InternalFeature.SUBJECTS);
		verbElement = replaceObject(verbElement, subjects);

		NLGElement subject = null;
		if (phrase.getFeatureAsString(Feature.INTERROGATIVE_TYPE).equals("WHO_OBJECT")){
			subject = phraseFactory.createWord("wen",LexicalCategory.PRONOUN);
		}
		else if(phrase.getFeatureAsString(Feature.INTERROGATIVE_TYPE).equals("WHAT_OBJECT")){
			subject = phraseFactory.createWord("was",LexicalCategory.PRONOUN);
		}
		phrase.setFeature(InternalFeature.SUBJECTS, subject);

		addSubjectsToFront(phrase, parent, realisedElement, splitVerb);

		realiseVerb(phrase, parent, realisedElement, splitVerb, verbElement, false);
	}

	protected static void addModifersComplements(NLGElement components, List<NLGElement> verbPhrase, List<NLGElement> complements, List<NLGElement> modifiers,
			List<NLGElement> objects, List<NLGElement> subordinateClauses, List<NLGElement> preModifiers, List<NLGElement> indirectObjects) {
		if(components.hasFeature(InternalFeature.CLAUSE_STATUS)) {
			if(components.getFeature(InternalFeature.CLAUSE_STATUS).equals(ClauseStatus.SUBORDINATE)) {
				subordinateClauses.add(components);
			}
		} else if(components.hasFeature(InternalFeature.CASE)) {
			if(components.getFeature(InternalFeature.CASE).equals(DiscourseFunction.INDIRECT_OBJECT)) {
				indirectObjects.add(components);
			} 
			else if(!components.getFeature(InternalFeature.CASE).equals(DiscourseFunction.OBJECT)
					&& !components.getFeature(InternalFeature.CASE).equals(DiscourseFunction.INDIRECT_OBJECT)
					&& !components.getFeature(InternalFeature.CASE).equals(DiscourseFunction.GENITIVE)
					&& (!components.hasFeature(InternalFeature.DISCOURSE_FUNCTION) || components.hasFeature(InternalFeature.DISCOURSE_FUNCTION) && !components.getFeature(InternalFeature.DISCOURSE_FUNCTION).equals(DiscourseFunction.COMPLEMENT))) {
				complements.add(components);
			} else {
				objects.add(components);
			}
		}
		else if(components instanceof ListElement) {
			if(components.hasFeature(InternalFeature.DISCOURSE_FUNCTION) && components.getFeature(InternalFeature.DISCOURSE_FUNCTION).equals(DiscourseFunction.PRE_MODIFIER)) {
				preModifiers.add(components);
			} else {
				for(NLGElement component: components.getChildren()) {
					addModifersComplements(component, verbPhrase, complements, modifiers, objects, subordinateClauses, preModifiers, indirectObjects);
				}
			}
		} else {
			if(components.getCategory()!= null && components.getCategory().equals(LexicalCategory.PRONOUN)) {
				verbPhrase.add(components);
			}
			else if(components.hasFeature(InternalFeature.BASE_WORD)
					&& components.getFeatureAsElement(InternalFeature.BASE_WORD).isA(LexicalCategory.VERB)) {
				verbPhrase.add(components);
			}
			else if(components.hasFeature(InternalFeature.CASE)) {
				if(!components.getFeature(InternalFeature.CASE).equals(DiscourseFunction.OBJECT)
						&& !components.getFeature(InternalFeature.CASE).equals(DiscourseFunction.INDIRECT_OBJECT)
						&& !components.getFeature(InternalFeature.CASE).equals(DiscourseFunction.GENITIVE)
				 && (!components.hasFeature(InternalFeature.DISCOURSE_FUNCTION) || components.hasFeature(InternalFeature.DISCOURSE_FUNCTION) && !components.getFeature(InternalFeature.DISCOURSE_FUNCTION).equals(DiscourseFunction.COMPLEMENT))) {
					complements.add(components);
				} else if(components.getFeature(InternalFeature.CASE).equals(DiscourseFunction.INDIRECT_OBJECT)) {
					indirectObjects.add(components);
				} else {
					objects.add(components);
				}
			} else if(components.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
				if(components.getFeature(InternalFeature.DISCOURSE_FUNCTION).equals(DiscourseFunction.COMPLEMENT)) {
					complements.add(components);
				} else if(components.getFeature(InternalFeature.DISCOURSE_FUNCTION).equals(DiscourseFunction.PRE_MODIFIER)) {
					preModifiers.add(components);
				} else {
					modifiers.add(components);
				}
			} else {
				modifiers.add(components);
			}
		}
	}

	/**
	 * Copies the front modifiers of the clause to the list of post-modifiers of
	 * the verb only if the phrase has infinitive form.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param verbElement
	 *            the <code>NLGElement</code> representing the verb phrase for
	 *            this clause.
	 */
	private static void copyFrontModifiers(PhraseElement phrase, NLGElement verbElement) {
		List<NLGElement> frontModifiers = phrase.getFeatureAsElementList(InternalFeature.FRONT_MODIFIERS);
		Object clauseForm = phrase.getFeature(Feature.FORM);
		if(verbElement != null) {
			List<NLGElement> phrasePostModifiers = phrase.getFeatureAsElementList(InternalFeature.POSTMODIFIERS);

			if(verbElement instanceof PhraseElement) {
				List<NLGElement> verbPostModifiers = verbElement.getFeatureAsElementList(InternalFeature.POSTMODIFIERS);

				for(NLGElement eachModifier : phrasePostModifiers) {
					if(!verbPostModifiers.contains(eachModifier)) {
						((PhraseElement) verbElement).addPostModifier(eachModifier);
					}
				}
			}
		}
		if(Form.INFINITIVE.equals(clauseForm)) {
			phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);

			for(NLGElement eachModifier : frontModifiers) {
				if(verbElement instanceof PhraseElement) {
					((PhraseElement) verbElement).addPostModifier(eachModifier);
				}
			}
			phrase.removeFeature(InternalFeature.FRONT_MODIFIERS);
			if(verbElement != null) {
				verbElement.setFeature(InternalFeature.NON_MORPH, true);
			}
		}
	}

	/**
	 * Checks to see if this clause is a subordinate clause. If it is then the
	 * complementiser is added as a component to the realised element
	 * <b>unless</b> the complementiser has been suppressed.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 */
	private static void addComplementiser(PhraseElement phrase, SyntaxProcessor parent, ListElement realisedElement) {

		NLGElement currentElement;

		if(ClauseStatus.SUBORDINATE.equals(phrase.getFeature(InternalFeature.CLAUSE_STATUS))
				&& !phrase.getFeatureAsBoolean(Feature.SUPRESSED_COMPLEMENTISER).booleanValue()) {

			currentElement = parent.realise(phrase.getFeatureAsElement(Feature.COMPLEMENTISER));

			if(currentElement != null) {
				realisedElement.addComponent(currentElement);
			}
		}
	}

	private static NLGElement addPassiveComplementsNumberPerson(PhraseElement phrase,
			SyntaxProcessor parent,
			ListElement realisedElement,
			NLGElement verbElement) {
		Object passiveNumber = null;
		Object passivePerson = null;
		NLGElement currentElement = null;
		NLGElement splitVerb = null;
		NLGElement verbPhrase = phrase.getFeatureAsElement(InternalFeature.VERB_PHRASE);

		// count complements to set plural feature if more than one
		int numComps = 0;
		boolean coordSubj = false;

		if(phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue() && verbPhrase != null
				&& !InterrogativeType.WHAT_OBJECT.equals(phrase.getFeature(Feature.INTERROGATIVE_TYPE))) {

			// complements of a clause are stored in the VPPhraseSpec
			for(NLGElement subject : verbPhrase.getFeatureAsElementList(InternalFeature.COMPLEMENTS)) {

				if(DiscourseFunction.OBJECT.equals(subject.getFeature(InternalFeature.DISCOURSE_FUNCTION))) {
					subject.setFeature(Feature.PASSIVE, true);
					numComps++ ;
					currentElement = parent.realise(subject);

					if(currentElement != null) {
						currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);

						if(phrase.hasFeature(Feature.INTERROGATIVE_TYPE)) {
							splitVerb = currentElement;
						} else {
							realisedElement.addComponent(currentElement);
						}
					}

					// flag if passive subject is coordinated with an "und"
					if(!coordSubj && subject instanceof CoordinatedPhraseElement) {
						String conj = ((CoordinatedPhraseElement) subject).getConjunction();
						coordSubj = (conj != null && conj.equals("und"));
					}

					if(passiveNumber == null) {
						passiveNumber = subject.getFeature(Feature.NUMBER);
					} else {
						passiveNumber = NumberAgreement.PLURAL;
					}

					if(Person.FIRST.equals(subject.getFeature(Feature.PERSON))) {
						passivePerson = Person.FIRST;
					} else if(Person.SECOND.equals(subject.getFeature(Feature.PERSON))
							&& !Person.FIRST.equals(passivePerson)) {
						passivePerson = Person.SECOND;
					} else if(passivePerson == null) {
						passivePerson = Person.THIRD;
					}

					if(Form.GERUND.equals(phrase.getFeature(Feature.FORM))
							&& !phrase.getFeatureAsBoolean(Feature.SUPPRESS_GENITIVE_IN_GERUND).booleanValue()) {
						subject.setFeature(Feature.POSSESSIVE, true);
					}
				}
			}
		}

		if(verbElement != null) {
			if(passivePerson != null) {
				verbElement.setFeature(Feature.PERSON, passivePerson);
			}

			if(numComps > 1 || coordSubj) {
				verbElement.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
			} else if(passiveNumber != null) {
				verbElement.setFeature(Feature.NUMBER, passiveNumber);
			}
		}
		return splitVerb;
	}

	/**
	 * Realises the subjects of a passive clause.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 * @param phraseFactory
	 *            the phrase factory to be used.
	 */
	private static void addPassiveSubjects(PhraseElement phrase,
			SyntaxProcessor parent,
			ListElement realisedElement,
			NLGFactory phraseFactory) {
		NLGElement currentElement = null;

		if(phrase.getFeatureAsBoolean(Feature.PASSIVE).booleanValue()) {
			List<NLGElement> allSubjects = phrase.getFeatureAsElementList(InternalFeature.SUBJECTS);

			/*			if(allSubjects.size() > 0 || phrase.hasFeature(Feature.INTERROGATIVE_TYPE)) {
				realisedElement.addComponent(parent.realise(phraseFactory.createPrepositionPhrase("von")));
			}*/

			for(NLGElement subject : allSubjects) {
				subject.setFeature(Feature.PASSIVE, true);
				if(subject.isA(PhraseCategory.NOUN_PHRASE) || subject instanceof CoordinatedPhraseElement) {
					currentElement = parent.realise(subject);
					if(currentElement != null) {
						currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.SUBJECT);
						realisedElement.addComponent(currentElement);
					}
				}
			}
		}
	}

	/**
	 * Realises the cue phrase for the clause if it exists.
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the clause.
	 */
	private static void addCuePhrase(PhraseElement phrase, SyntaxProcessor parent, ListElement realisedElement) {

		NLGElement currentElement = parent.realise(phrase.getFeatureAsElement(Feature.CUE_PHRASE));

		if(currentElement != null) {
			currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.CUE_PHRASE);
			realisedElement.addComponent(currentElement);
		}
	}

	/**
	 * Checks the discourse function of the clause and alters the form of the
	 * clause as necessary. The following algorithm is used: <br>
	 *
	 * <pre>
	 * If the clause represents a direct or indirect object then
	 *      If form is currently Imperative then
	 *           Set form to Infinitive
	 *           Suppress the complementiser
	 *      If form is currently Gerund and there are no subjects
	 *      	 Suppress the complementiser
	 * If the clause represents a subject then
	 *      Set the form to be Gerund
	 *      Suppress the complementiser
	 * </pre>
	 *
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this clause.
	 */
	private static void checkDiscourseFunction(PhraseElement phrase) {
		List<NLGElement> subjects = phrase.getFeatureAsElementList(InternalFeature.SUBJECTS);
		Object clauseForm = phrase.getFeature(Feature.FORM);
		Object discourseValue = phrase.getFeature(InternalFeature.DISCOURSE_FUNCTION);

		if(DiscourseFunction.OBJECT.equals(discourseValue) || DiscourseFunction.INDIRECT_OBJECT.equals(discourseValue)) {

			if(Form.IMPERATIVE.equals(clauseForm)) {
				phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
				phrase.setFeature(Feature.FORM, Form.INFINITIVE);
			} else if(Form.GERUND.equals(clauseForm) && subjects.size() == 0) {
				phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
			}
		} else if(DiscourseFunction.SUBJECT.equals(discourseValue)) {
			phrase.setFeature(Feature.FORM, Form.GERUND);
			phrase.setFeature(Feature.SUPRESSED_COMPLEMENTISER, true);
		}
	}

	private static NLGElement replaceObject(NLGElement original, List<NLGElement> replacement){
		List<NLGElement> complements = original.getFeatureAsElementList(InternalFeature.COMPLEMENTS);

		boolean inbetweenVerb = false;

		//remove old objects
		for (int i = 0; i < complements.size(); i++) {
			if(complements.get(i).getFeature(InternalFeature.DISCOURSE_FUNCTION) != null &&
					complements.get(i).getFeature(InternalFeature.DISCOURSE_FUNCTION).equals(DiscourseFunction.OBJECT)) {
				inbetweenVerb = complements.get(i).getFeatureAsBoolean(InternalFeature.INBETWEEN_VERB);
				complements.remove(i);
			}
		}

		//add new objects
		for (NLGElement e: replacement) {
			e.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);
			e.setFeature(InternalFeature.CASE, DiscourseFunction.OBJECT);
			e.setFeature(InternalFeature.INBETWEEN_VERB, true);
		}
		complements.addAll(replacement);

		original.setFeature(InternalFeature.COMPLEMENTS, complements);

		return original;
	}

}
