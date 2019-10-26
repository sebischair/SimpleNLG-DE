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
import java.util.List;

import simplenlgde.framework.*;
import simplenlgde.orthograpgy.OrthographyProcessor;
import simplenlgde.features.*;


/**
 * <p>
 * This is the processor for handling syntax within the SimpleNLG. The processor
 * translates phrases into lists of words.
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

public class SyntaxProcessor extends NLGModule {

    OrthographyProcessor orthographyHelper = new OrthographyProcessor();

    @Override
    public void initialise() {
    }

    @Override
    public NLGElement realise(NLGElement element) {
        NLGElement realisedElement = null;

        if (element != null
                && !element.getFeatureAsBoolean(Feature.ELIDED).booleanValue()) {

            if (element instanceof DocumentElement) {
                List<NLGElement> children = element.getChildren();
                ((DocumentElement) element).setComponents(realise(children));
                realisedElement = element;

            } else if (element instanceof PhraseElement) {
                realisedElement = realisePhraseElement((PhraseElement) element);

            } else if (element instanceof ListElement) {
                realisedElement = new ListElement();
                ((ListElement) realisedElement).addComponents(realise(element
                        .getChildren()));

            } else if (element instanceof InflectedWordElement) {
                String baseForm = ((InflectedWordElement) element)
                        .getBaseForm();
                ElementCategory category = element.getCategory();

                if (this.lexicon != null && baseForm != null) {
                    WordElement word = ((InflectedWordElement) element)
                            .getBaseWord();

                    if (word == null) {
                        if (category instanceof LexicalCategory) {
                            word = this.lexicon.lookupWord(baseForm,
                                    (LexicalCategory) category);
                        } else {
                            word = this.lexicon.lookupWord(baseForm);
                        }
                    }

                    if (word != null) {
                        ((InflectedWordElement) element).setBaseWord(word);
                    }
                }

                realisedElement = element;

            } else if (element instanceof WordElement) {
                // need to check if it's a word element, in which case it
                // needs to be marked for inflection
                InflectedWordElement infl = new InflectedWordElement(
                        (WordElement) element);

                // the inflected word inherits all features from the base word
                for (String feature : element.getAllFeatureNames()) {
                    infl.setFeature(feature, element.getFeature(feature));
                }

                realisedElement = realise(infl);

            } else if (element instanceof CoordinatedPhraseElement) {
                realisedElement = CoordinatedPhraseHelper.realise(this,
                        (CoordinatedPhraseElement) element);

            } else {
                realisedElement = element;
            }
        }

        // Remove the spurious ListElements that have only one element.
        if (realisedElement instanceof ListElement) {
            if (((ListElement) realisedElement).size() == 1) {
                realisedElement = ((ListElement) realisedElement).getFirst();
            }
        }

        return realisedElement;
    }

    @Override
    public List<NLGElement> realise(List<NLGElement> elements) {
        List<NLGElement> realisedList = new ArrayList<NLGElement>();
        NLGElement childRealisation = null;

        if (elements != null) {
            for (NLGElement eachElement : elements) {
                if (eachElement != null) {
                    childRealisation = realise(eachElement);
                    if (childRealisation != null) {
                        if (childRealisation instanceof ListElement) {
                            realisedList
                                    .addAll(((ListElement) childRealisation)
                                            .getChildren());
                        } else {
                            realisedList.add(childRealisation);
                        }
                    }
                }
            }
        }
        return realisedList;
    }

    /**
     * Realises a phrase element.
     *
     * @param phrase
     *            the element to be realised
     * @return the realised element.
     */
    private NLGElement realisePhraseElement(PhraseElement phrase) {
        NLGElement realisedElement = null;

        if (phrase != null) {
            ElementCategory category = phrase.getCategory();

            if (category instanceof PhraseCategory) {
                switch ((PhraseCategory) category) {

                    case CLAUSE:
                        realisedElement = ClauseHelper.realise(this, phrase);
                        break;

                    case NOUN_PHRASE:
                        realisedElement = NounPhraseHelper.realise(this, phrase);
                        break;

                    case VERB_PHRASE:
                        realisedElement = VerbPhraseHelper.realise(this, phrase);
                        break;

                    case PREPOSITIONAL_PHRASE:
                    case ADJECTIVE_PHRASE:
                    case ADVERB_PHRASE:
                        realisedElement = PhraseHelper.realise(this, phrase);
                        break;

                    default:
                        realisedElement = phrase;
                        break;
                }
            }
        }

        if (realisedElement != null &&  realisedElement.hasFeature(Feature.CONTAINS_MODAL)) {
            copyModalFeature(realisedElement, realisedElement.getChildren());
        }

        return realisedElement;
    }

    /**
     * The method to copy the feature if the phrase has a modal verb
     * to the other verbs in this phrase. This is important for verb inflection.
     * @param element the parent <code>NLGElement</code> to take feature from
     * @param children the children <code>List<NLGElement></NLGElement></code> to copy the feature to
     */
    private void copyModalFeature(NLGElement element, List<NLGElement> children) {
        for (NLGElement child : children) {
            if(child instanceof ListElement) {
                copyModalFeature(element, child.getChildren());
            } else if (child.getCategory() != null && child.getCategory().equals(LexicalCategory.VERB)){
                child.setFeature(Feature.CONTAINS_MODAL, element.getFeature(Feature.CONTAINS_MODAL));
            }
        }
    }

    /**
     * A helper method for correcting the sentence structure if the verb phrase
     * contains a separable verb. For example, the object ("das Fahrrad") or
     * Modifiers ("schnell") has to be placed in between the separable verb
     * ("abschließen"), which would result in "Bob schließt schnell das Fahrrad ab".
     *
     * @param component the <code>NLGElement</code> to check for a separable verb
     * @param verb the <code>List<NLGElement></code> containing the separable verb
     * @param verbModifiers the <code>List<NLGElement></code> containing the verb's modifiers
     */
    public void getSeparableVerbComponents(NLGElement component, List<NLGElement> verb, List<NLGElement> verbModifiers) {
        if (component instanceof ListElement) {
            for (NLGElement childComponent : component.getChildren()) {
                if (childComponent.hasFeature(LexicalFeature.SEPARABLE) &&
                        childComponent.getFeatureAsBoolean(LexicalFeature.SEPARABLE) &&
                        childComponent.getRealisation().contains(" ")) {
                    verb.add(childComponent);
                } else {
                    copyParentFeatures(component, childComponent);
                    if (childComponent instanceof ListElement) {
                        for (NLGElement child : childComponent.getChildren()) {
                            copyParentFeatures(childComponent, child);
                            getSeparableVerbComponents(child, verb, verbModifiers);
                        }
                    } else {
                        verbModifiers.add(childComponent);
                    }
                }
            }
        } else if (component.hasFeature(LexicalFeature.SEPARABLE)) {
            if (component.getFeatureAsBoolean(LexicalFeature.SEPARABLE) && component.getRealisation().contains(" ")) {
                verb.add(component);
            } else {
                verbModifiers.add(component);
            }
        } else {
            verbModifiers.add(component);
        }
    }

    /**
     * The method to copy features important for inflection and word positioning from a parent element to its child
     * @param parent the parent <code>NLGElement</code> to take the features
     * @param child the child <code>NLGElement</code> to copy the features to
     */
    protected void copyParentFeatures(NLGElement parent, NLGElement child) {
        if (parent.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
            child.setFeature(InternalFeature.DISCOURSE_FUNCTION, parent.getFeature(InternalFeature.DISCOURSE_FUNCTION));
        }
        if (parent.getFeature(InternalFeature.INBETWEEN_VERB) != null) {
            child.setFeature(InternalFeature.INBETWEEN_VERB, parent.getFeature(InternalFeature.INBETWEEN_VERB));
        }
        if (parent.getFeature(InternalFeature.CASE) != null) {
            child.setFeature(InternalFeature.CASE, parent.getFeature(InternalFeature.CASE));
        }
        if (parent.hasFeature(InternalFeature.CLAUSE_STATUS)) {
            child.setFeature(InternalFeature.CLAUSE_STATUS, parent.getFeature(InternalFeature.CLAUSE_STATUS));
        }
        if (parent.hasFeature(LexicalFeature.SEPARABLE)) {
            child.setFeature(LexicalFeature.SEPARABLE, parent.getFeature(LexicalFeature.SEPARABLE));
        }
        if (parent.hasFeature(Feature.SEPARABLE_VERB)) {
            child.setFeature(Feature.SEPARABLE_VERB, parent.getFeatureAsBoolean(Feature.SEPARABLE_VERB));
        }
    }

    /**
     * The main method for correcting the sentence word order if the verb phrase
     * contains a separable verb. For example, the object ("das Fahrrad") or
     * Modifiers ("schnell") has to be placed in between the separable verb
     * ("abschließen"), which would result in "Bob schließt schnell das Fahrrad ab".
     *
     * @param child the <code>NLGElement</code> to check for a separable verb
     * @param verb the <code>List<NLGElement></code> containing the separable verb
     * @param verbModifiers the <code>List<NLGElement></code> containing the verb's modifiers
     * @param subordinates the <code>List<NLGElement></code> containing possible subordinate clause elements
     */
    public void realiseSeparableVerbPhrase(NLGElement child, List<NLGElement> verb, List<NLGElement> verbModifiers,
                                           List<NLGElement> subordinates) {
        String realisedModifiers = "";
        String realisedComplements = "";
        String realisedObjects = "";
        String realisedSubjects = "";
        StringElement realisedSubjectsElement = new StringElement("");
        StringElement realisedModifiersElement = new StringElement("");
        StringElement realisedComplementsElement = new StringElement("");
        StringElement realisedObjectsElement = new StringElement("");
        List<NLGElement> verbComplements = new ArrayList<NLGElement>();

        if (!verb.isEmpty() && verbModifiers != null) {
            String[] verbParts = verb.get(0).getRealisation().split(" ");
            String newVerbRealisation = verbParts[0];
            ListElement newVerbList = new ListElement();
            // first, add Modifiers of verb between separable verb parts
            for (NLGElement verbModifier : verbModifiers) {
                if(verb.size()>=1 && (verbModifier.hasFeature(InternalFeature.CLAUSE_STATUS)
                        && verbModifier.getFeature(InternalFeature.CLAUSE_STATUS).equals(ClauseStatus.SUBORDINATE))
                        && ((verb.get(0).hasFeature(InternalFeature.CLAUSE_STATUS)
                        && !verb.get(0).getFeature(InternalFeature.CLAUSE_STATUS).equals(ClauseStatus.SUBORDINATE))
                        || !verb.get(0).hasFeature(InternalFeature.CLAUSE_STATUS))) {
                    subordinates.add(verbModifier);
                } else if (verbModifier.getFeature(InternalFeature.DISCOURSE_FUNCTION) == null) {
                    realisedModifiers = addToSeparableVerb(realisedModifiers, verbModifier);
                } else if (verbModifier.getFeature(InternalFeature.DISCOURSE_FUNCTION) != null) {
                    if (verbModifier.hasFeature(InternalFeature.CASE)
                            && verbModifier.getFeature(InternalFeature.CASE).equals(DiscourseFunction.SUBJECT)) {
                        realisedSubjects = addToSeparableVerb(realisedSubjects, verbModifier);
                    } else if (verbModifier.hasFeature(InternalFeature.CASE)
                            && verbModifier.getFeature(InternalFeature.CASE).equals(DiscourseFunction.OBJECT)) {
                        realisedObjects = addToSeparableVerb(realisedObjects, verbModifier);
                    } else if (!verbModifier.getFeature(InternalFeature.DISCOURSE_FUNCTION)
                            .equals(DiscourseFunction.COMPLEMENT)) {
                        realisedModifiers = addToSeparableVerb(realisedModifiers, verbModifier);
                    } else {
                        verbComplements.add(verbModifier);
                    }
                }
            }
            for(NLGElement subordinate: subordinates) {
                verbModifiers.remove(subordinate);
            }
            // second, add complements of verb between separable verb parts
            for (NLGElement verbComplement : verbComplements) {
                if (verbComplement.getFeature(InternalFeature.INBETWEEN_VERB) != null) {
                    realisedObjects = addToSeparableVerb(realisedObjects, verbComplement);
                }
            }
            realisedSubjectsElement.setRealisation(realisedSubjects);
            realisedObjectsElement.setRealisation(realisedObjects);
            realisedModifiersElement.setRealisation(realisedModifiers);

            newVerbList.addComponent(new StringElement(newVerbRealisation));
            newVerbList.addComponent(realisedSubjectsElement);
            newVerbList.addComponent(realisedModifiersElement);
            newVerbList.addComponent(realisedObjectsElement);
            newVerbList.addComponent(new StringElement(verbParts[1]));

            if (!verbComplements.isEmpty()) {
                for (NLGElement verbComplement : verbComplements) {
                    if (verbComplement.getFeature(InternalFeature.INBETWEEN_VERB) == null) {
                        realisedComplements = addToSeparableVerb(realisedComplements, verbComplement);
                    }
                }
            }
            realisedComplementsElement.setRealisation(realisedComplements);
            newVerbList.addComponent(realisedComplementsElement);

            if(!subordinates.isEmpty()) {
                for(NLGElement subordinate: subordinates) {
                    newVerbList.addComponent(subordinate);
                }
            }
            if(verb.size()>1) {
                for(int i = 1; i<verb.size(); i++) {
                    newVerbList.addComponent(verb.get(i));
                }
            }
            for(NLGElement element: newVerbList.getChildren()) {
                if(element.getRealisation() == "") {
                    newVerbList.removeComponent(element);
                }
            }
            child.setFeature(InternalFeature.COMPONENTS, newVerbList);
        }
    }

    /**
     * Adds a component (e.g. a modifier or an object) to a separable verb
     * realisation.
     *
     * @param newVerbRealisation the <code>String</code> current verb realisation
     * @param verbModifier the <code>NLGElement</code> component to be added
     * @return a <code>String</code> reflecting the modified verb realisation
     *
     */
    protected String addToSeparableVerb(String newVerbRealisation, NLGElement verbModifier) {
        String realised;
        NLGElement currentElementRealised = orthographyHelper.realise(verbModifier);
        realised = newVerbRealisation + " " + currentElementRealised + " ";
        return realised;
    }
}
