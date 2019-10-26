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
 */

package simplenlgde.phrasespec;

import simplenlgde.framework.*;
import simplenlgde.features.*;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * <p>
 * This class defines a verb phrase. It is essentially
 * a wrapper around the <code>PhraseElement</code> class, with methods
 * for setting common constituents such as Objects.
 * For example, the <code>setVerb</code> method in this class sets
 * the head of the element to be the specified verb
 *
 * From an API perspective, this class is a simplified version of the SPhraseSpec
 * class in simplenlg V3.  It provides an alternative way for creating syntactic
 * structures, compared to directly manipulating a V4 <code>PhraseElement</code>.
 *
 * Methods are provided for setting and getting the following constituents:
 * <UL>
 * <LI>PreModifier		(eg, "reluctantly")
 * <LI>Verb				(eg, "gave")
 * <LI>IndirectObject	(eg, "Mary")
 * <LI>Object	        (eg, "an apple")
 * <LI>PostModifier     (eg, "before school")
 * </UL>
 *
 * NOTE: If there is a complex verb group, a preModifer set at the VP level appears before
 * the verb, while a preModifier set at the clause level appears before the verb group.  Eg
 *   "Mary unfortunately will eat the apple"  ("unfortunately" is clause preModifier)
 *   "Mary will happily eat the apple"  ("happily" is VP preModifier)
 *
 * NOTE: The setModifier method will attempt to automatically determine whether
 * a modifier should be expressed as a PreModifier or PostModifier
 *
 * Features (such as negated) must be accessed via the <code>setFeature</code> and
 * <code>getFeature</code> methods (inherited from <code>NLGElement</code>).
 * Features which are often set on VPPhraseSpec include
 * <UL>
 * <LI>Modal    (eg, "John eats an apple" vs "John can eat an apple")
 * <LI>Negated  (eg, "John eats an apple" vs "John does not eat an apple")
 * <LI>Passive  (eg, "John eats an apple" vs "An apple is eaten by John")
 * <LI>Perfect  (eg, "John ate an apple" vs "John has eaten an apple")
 * <LI>Progressive  (eg, "John eats an apple" vs "John is eating an apple")
 * <LI>Tense    (eg, "John ate" vs "John eats" vs "John will eat")
 * </UL>
 * Note that most VP features can be set on an SPhraseSpec, they will automatically
 * be propogated to the VP
 *
 * <code>VPPhraseSpec</code> are produced by the <code>createVerbPhrase</code>
 * method of a <code>PhraseFactory</code>
 * </p>
 *
 * @author E. Reiter, University of Aberdeen.
 * @version 4.1
 *
 */
public class VPPhraseSpec extends PhraseElement {
	
    /** The list of reflexive pronouns. */
	private static final List<String> REFLEXIVE_PRONOUNS = Arrays.asList("mich", "dich", "sich", "uns");
	
	/** create an empty clause
	 */
	public VPPhraseSpec(NLGFactory phraseFactory) {
		super(PhraseCategory.VERB_PHRASE);
		this.setFactory(phraseFactory);

		// set default feature values
		setFeature(Feature.TENSE, Tense.PRESENT);
		setFeature(Feature.PERSON, Person.THIRD);
		setFeature(Feature.FORM, Form.NORMAL);
		setPlural(false);
	}

	/** sets the verb (head) of a verb phrase.
	 * Extract particle from verb if necessary
	 * @param verb
	 */
	public void setVerb(Object verb) {
		NLGElement verbElement = null;
		NLGElement reflexivePronoun = null;

		if (verb instanceof String) { // if String given, check for particle
			int space = ((String) verb).indexOf(' ');
			if (space == -1) { // no space, so no particle
				verbElement = getFactory().createWord(verb, LexicalCategory.VERB);
			} else { // space, so break up into verb and particle
				if(REFLEXIVE_PRONOUNS.contains(((String) verb).substring(0, space))) {
	                verbElement = getFactory().createWord(((String) verb)
	                        .substring(space + 1, ((String) verb).length()), LexicalCategory.VERB);
	                reflexivePronoun = getFactory().createWord(((String) verb)
	                        .substring(0, space), LexicalCategory.PRONOUN);
	                setFeature(Feature.PARTICLE, reflexivePronoun);
				} else {
	                verbElement = getFactory().createWord(((String) verb)
	                        .substring(0, space), LexicalCategory.VERB);
	                setFeature(Feature.PARTICLE, ((String) verb)
	                        .substring(space + 1, ((String) verb).length()));
				}
            }
		} else { // Object is not a String
			verbElement = getFactory().createNLGElement(verb,LexicalCategory.VERB);
		}
		if(verbElement != null) {
			if((verbElement instanceof WordElement) && verbElement.hasFeature(LexicalFeature.SEPARABLE)) {
				this.setFeature(LexicalFeature.SEPARABLE, verbElement.getFeatureAsBoolean(LexicalFeature.SEPARABLE));
			} else {
				if(verbElement.hasFeature(InternalFeature.HEAD) && verbElement.getFeatureAsElement(InternalFeature.HEAD).hasFeature(LexicalFeature.SEPARABLE)) {
					this.setFeature(LexicalFeature.SEPARABLE, verbElement.getFeatureAsBoolean(LexicalFeature.SEPARABLE));
					verbElement.setFeature(LexicalFeature.SEPARABLE, verbElement.getFeatureAsBoolean(LexicalFeature.SEPARABLE));
				}
			}
		}
		setHead(verbElement);
	}


	/**
	 * @return verb (head) of verb phrase
	 */
	public NLGElement getVerb() {
		return getHead();
	}

	/** Sets the direct object of a clause  (assumes this is the only direct object)
	 *
	 * @param object
	 */
	public void setObject(Object object) {
		NLGElement objectPhrase;
		if (object instanceof PhraseElement || object instanceof CoordinatedPhraseElement) {
			objectPhrase = (NLGElement) object;
		} else
			objectPhrase = getFactory().createNounPhrase(object);

		objectPhrase.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);
		objectPhrase.setFeature(InternalFeature.INBETWEEN_VERB, true);
		objectPhrase.setFeature(InternalFeature.CASE, DiscourseFunction.OBJECT);
		setComplement(objectPhrase);
	}


	/** Returns the direct object of a clause (assumes there is only one)
	 *
	 * @return subject of clause (assume only one)
	 */
	public NLGElement getObject() {
		List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		for (NLGElement complement: complements)
			if (complement.getFeature(InternalFeature.DISCOURSE_FUNCTION) == DiscourseFunction.OBJECT)
				return complement;
		return null;
	}

	// note that addFrontModifier, addPostModifier, are inherited from PhraseElement
	// likewise getFrontModifiers, getPostModifiers, getPreModifiers


	/** Add a modifier to a verb phrase
	 * Use heuristics to decide where it goes
	 * @param modifier
	 */
	@Override
	public void addModifier(Object modifier) {
		// adverb is postModifier
		// string which is one lexicographic word is looked up in lexicon,
		// if it is an adverb than it becomes a preModifier
		// Everything else is postModifier

		if (modifier == null)
			return;

		// get modifier as NLGElement if possible
		NLGElement modifierElement = null;
		if (modifier instanceof NLGElement)
			modifierElement = (NLGElement) modifier;
		else if (modifier instanceof String) {
			String modifierString = (String)modifier;
			if (modifierString.length() > 0 && !modifierString.contains(" ") && !modifierString.matches("[0-9]+"))
				modifierElement = getFactory().createWord(modifier, LexicalCategory.ANY);
		}

		// if no modifier element, must be a complex string
		if (modifierElement == null) {
			addPostModifier((String)modifier);
			return;
		}

		// extract WordElement if modifier is a single word
		WordElement modifierWord = null;
		if (modifierElement != null && modifierElement instanceof WordElement)
			modifierWord = (WordElement) modifierElement;
		else if (modifierElement != null && modifierElement instanceof InflectedWordElement)
			modifierWord = ((InflectedWordElement) modifierElement).getBaseWord();
				
		if (modifierWord != null && modifierWord.getCategory() == LexicalCategory.ADVERB) {
			addModifier(modifierWord);
			return;
		}

		// default case
		if(modifierElement.getCategory().equals(LexicalCategory.ADJECTIVE)) {
			modifierElement.setCategory(LexicalCategory.ADVERB);
		}
		addModifier(modifierElement);
	}

	/** Set the indirect object of a clause (assumes this is the only direct indirect object)
	 *
	 * @param indirectObject
	 */
	public void setIndirectObject(Object indirectObject) {
		NLGElement indirectObjectPhrase;
		if (indirectObject instanceof PhraseElement || indirectObject instanceof CoordinatedPhraseElement)
			indirectObjectPhrase = (NLGElement) indirectObject;
		else
			indirectObjectPhrase = getFactory().createNounPhrase(indirectObject);

		indirectObjectPhrase.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.INDIRECT_OBJECT);
		indirectObjectPhrase.setFeature(InternalFeature.INBETWEEN_VERB, true);
		indirectObjectPhrase.setFeature(InternalFeature.CASE, DiscourseFunction.INDIRECT_OBJECT);
		setComplement(indirectObjectPhrase);
	}

	/** Returns the indirect object of a clause (assumes there is only one)
	 * 
	 * @return subject of clause (assume only one)
	 */
	public NLGElement getIndirectObject() {
		List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		for (NLGElement complement: complements)
			if (complement.getFeature(InternalFeature.DISCOURSE_FUNCTION) == DiscourseFunction.INDIRECT_OBJECT)
				return complement;
		return null;
	}
	
	/**
	 * Adds a new pre-modifier to the phrase element.
	 * 
	 * @param modifier
	 *            the new pre-modifier as a <code>String</code>. It is used to
	 *            create a <code>StringElement</code>.
	 */
	public void addPreModifier(Object modifier) {
		if (modifier == null)
			return;

		// get modifier as NLGElement if possible
		NLGElement modifierElement = null;
		if (modifier instanceof NLGElement)
			modifierElement = (NLGElement) modifier;
		else if (modifier instanceof String) {
			String modifierString = (String) modifier;
			if (modifierString.length() > 0 && !modifierString.contains(" ") && !modifierString.matches("[0-9]+"))
				modifierElement = getFactory().createWord(modifier,
						LexicalCategory.ANY);
			else
				// if no modifier element, must be a complex string, add as text
				modifierElement = new StringElement(modifierString);
		}
		if(modifierElement != null ) {
			if(this.hasFeature(LexicalFeature.GENDER)) {
				modifierElement.setFeature(LexicalFeature.GENDER, this.getFeature(LexicalFeature.GENDER));
			}
			if(this.hasFeature("number")) {
				modifierElement.setFeature("number", this.getFeature("number"));
			}
			if(this.hasFeature("specifier")) {
				ElementCategory spec = ((NLGElement) this.getFeature("specifier")).getCategory();
				modifierElement.setFeature("specifier", spec.toString());
			}
			if(this.hasFeature(InternalFeature.HEAD) && modifier.toString().equals("zu")) {
				this.getHead().setFeature("zu", true);
			}
		}

		// else extract WordElement if modifier is a single word
		WordElement modifierWord = null;
		if (modifierElement != null && modifierElement instanceof WordElement)
			modifierWord = (WordElement) modifierElement;
		else if (modifierElement != null
				&& modifierElement instanceof InflectedWordElement)
			modifierWord = ((InflectedWordElement) modifierElement)
					.getBaseWord();
		addPreModifier(modifierElement);
	}

	/**
	 * Adds a new post-modifier to the phrase element. Post-modifiers will be
	 * realised in the syntax after the complements.
	 * 
	 * @param newPostModifier
	 *            the new post-modifier as an <code>NLGElement</code>.
	 */
	public void addPostModifier(NLGElement newPostModifier) {
		List<NLGElement> postModifiers = getFeatureAsElementList(InternalFeature.POSTMODIFIERS);
		if (postModifiers == null) {
			postModifiers = new ArrayList<NLGElement>();
		}
		newPostModifier.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.POST_MODIFIER);
		if(newPostModifier.getCategory().equals(LexicalCategory.ADJECTIVE)) {
			newPostModifier.setCategory(LexicalCategory.ADVERB);
		}
		postModifiers.add(newPostModifier);
		setFeature(InternalFeature.POSTMODIFIERS, postModifiers);
	}
}
