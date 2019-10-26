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

import java.util.List;

import simplenlgde.framework.*;
import simplenlgde.features.*;

/**
 * <p>
 * This class contains static methods to help the syntax processor realise
 * coordinated phrases.
 * </p>
 */
abstract class CoordinatedPhraseHelper {

    /**
     * The main method for realising coordinated phrases.
     *
     * @param parent
     *            the <code>SyntaxProcessor</code> that called this method.
     * @param phrase
     *            the <code>CoordinatedPhrase</code> to be realised.
     * @return the realised <code>NLGElement</code>.
     */
    static NLGElement realise(SyntaxProcessor parent,
                              CoordinatedPhraseElement phrase) {
        ListElement realisedElement = null;
        boolean phraseHasCase = phrase.hasFeature(InternalFeature.CASE);
        if(!phraseHasCase) {
        	phrase.setFeature(InternalFeature.CASE, DiscourseFunction.SUBJECT);
        }

        if (phrase != null) {
            realisedElement = new ListElement();
            PhraseHelper.realiseList(parent, realisedElement, phrase
                    .getPreModifiers(), DiscourseFunction.PRE_MODIFIER, phrase.getFeature(InternalFeature.CASE));

            CoordinatedPhraseElement coordinated = new CoordinatedPhraseElement();

            List<NLGElement> children = phrase.getChildren();
            String conjunction = phrase.getFeatureAsString(Feature.CONJUNCTION);
            coordinated.setFeature(Feature.CONJUNCTION, conjunction);
            coordinated.setFeature(Feature.CONJUNCTION_TYPE, phrase
                    .getFeature(Feature.CONJUNCTION_TYPE));
            if (phrase.hasFeature(InternalFeature.CASE)) {
            	coordinated.setFeature(InternalFeature.CASE, phrase
                        .getFeature(InternalFeature.CASE));
            }
            
            InflectedWordElement conjunctionElement = null;

            if (children != null && children.size() > 0) {

                NLGElement child = phrase.getLastCoordinate();

                child = children.get(0);

                setChildFeatures(phrase, child);

/*                if (children != null && children.size() > 1) {
                    if(phrase.getLastCoordinate().hasFeature("verb_phrase")) {
                        phrase.getLastCoordinate().getFeatureAsElement("verb_phrase").setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                    }
                }*/

                coordinated.addCoordinate(parent.realise(child));
                for (int index = 1; index < children.size(); index++) {
                    child = children.get(index);
                    setChildFeatures(phrase, child);

                    if (child.isA(PhraseCategory.CLAUSE)) {
                        child
                                .setFeature(
                                        Feature.SUPRESSED_COMPLEMENTISER,
                                        phrase
                                                .getFeature(Feature.SUPRESSED_COMPLEMENTISER));
                    }

                    //skip conjunction if it's null or empty string
                    if (conjunction != null && conjunction.length() > 0) {
                        conjunctionElement = new InflectedWordElement(
                                conjunction, LexicalCategory.CONJUNCTION);
                        conjunctionElement.setFeature(
                                InternalFeature.DISCOURSE_FUNCTION,
                                DiscourseFunction.CONJUNCTION);
                        coordinated.addCoordinate(conjunctionElement);
                    }

                    coordinated.addCoordinate(parent.realise(child));
                }
                realisedElement.addComponent(coordinated);
            }

            PhraseHelper.realiseList(parent, realisedElement, phrase
                    .getPostModifiers(), DiscourseFunction.POST_MODIFIER, phrase.getFeature(InternalFeature.CASE));
            PhraseHelper.realiseList(parent, realisedElement, phrase
                    .getComplements(), DiscourseFunction.COMPLEMENT, phrase.getFeature(InternalFeature.CASE));
        }
        return realisedElement;
    }

    /**
     * Sets the common features from the phrase to the child element.
     *
     * @param phrase
     *            the <code>CoordinatedPhraseElement</code>
     * @param child
     *            a single coordinated <code>NLGElement</code> within the
     *            coordination.
     */
    private static void setChildFeatures(CoordinatedPhraseElement phrase,
                                         NLGElement child) {
        if (phrase.hasFeature(InternalFeature.SPECIFIER)) {
            child.setFeature(InternalFeature.SPECIFIER, phrase
                    .getFeature(InternalFeature.SPECIFIER));
        }
        if (phrase.hasFeature(LexicalFeature.GENDER)) {
            child.setFeature(LexicalFeature.GENDER, phrase
                    .getFeature(LexicalFeature.GENDER));
        }
        if (phrase.hasFeature(Feature.NUMBER)) {
            child.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));
        }
        if (phrase.hasFeature(Feature.TENSE)) {
            child.setFeature(Feature.TENSE, phrase.getFeature(Feature.TENSE));
        }
        if (phrase.hasFeature(Feature.PERSON)) {
            child.setFeature(Feature.PERSON, phrase.getFeature(Feature.PERSON));
        }
        if (phrase.hasFeature(Feature.NEGATED)) {
            child.setFeature(Feature.NEGATED, phrase.getFeature(Feature.NEGATED));
        }
        if (phrase.hasFeature(Feature.MODAL)) {
            child.setFeature(Feature.MODAL, phrase.getFeature(Feature.MODAL));
        }
        if (phrase.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
            child.setFeature(InternalFeature.DISCOURSE_FUNCTION, phrase
                    .getFeature(InternalFeature.DISCOURSE_FUNCTION));
        }
        if (phrase.hasFeature(InternalFeature.CASE)) {
            child.setFeature(InternalFeature.CASE, phrase
                    .getFeature(InternalFeature.CASE));
        }
        if (phrase.hasFeature(Feature.FORM)) {
            child.setFeature(Feature.FORM, phrase.getFeature(Feature.FORM));
        }
        if (phrase.hasFeature(InternalFeature.CLAUSE_STATUS)) {
            child.setFeature(InternalFeature.CLAUSE_STATUS, phrase
                    .getFeature(InternalFeature.CLAUSE_STATUS));
        }
        if (phrase.hasFeature(Feature.INTERROGATIVE_TYPE)) {
            child.setFeature(InternalFeature.IGNORE_MODAL, true);
        }
    }

}
