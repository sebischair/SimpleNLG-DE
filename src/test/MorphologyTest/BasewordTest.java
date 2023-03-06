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
 * Contributor(s): Daniel Braun, Technical University of Munich.
 */

package MorphologyTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simplenlgde.framework.*;
import simplenlgde.lexicon.Lexicon;
import simplenlgde.realiser.Realiser;
import simplenlgde.features.*;
import simplenlgde.phrasespec.*;

import org.junit.jupiter.api.Assertions;

public class BasewordTest {
    private Lexicon lexicon;
    private NLGFactory nlgFactory;
    private Realiser realiser;

    @BeforeEach
    public void setup() {
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        realiser = new Realiser(lexicon);
    }

    @Test
    public void nounBasewordTest(){
        String[] base = {"Mensch", "Tier", "Tier"};
        String[] inflected = {"Menschen", "Tieres", "Tiere"};

        for (int i = 0; i < base.length; i++){
            NPPhraseSpec np1 = nlgFactory.createNounPhrase(base[i]);
            NPPhraseSpec np2 = nlgFactory.createNounPhrase(inflected[i]);

            Assertions.assertEquals(((WordElement) np1.getNoun()).getBaseForm(), ((WordElement) np2.getNoun()).getBaseForm());
        }
    }

    @Test
    public void adjectiveBasewordTest(){
        String[] base = {"gut", "gut", "gut", "gut", "gut"};
        String[] inflected = {"guter", "gute", "gutes", "gutem", "guten"};

        for (int i = 0; i < base.length; i++){
            VPPhraseSpec vp1 = nlgFactory.createVerbPhrase(base[i]);
            VPPhraseSpec vp2 = nlgFactory.createVerbPhrase(inflected[i]);

            Assertions.assertEquals(((WordElement) vp1.getVerb()).getBaseForm(), ((WordElement) vp2.getVerb()).getBaseForm());
        }
    }

    @Test
    public void verbBasewordTest(){
        String[] base = {"sein", "sein", "sein", "sein", "gehen", "gehen"};
        String[] inflected = {"bin", "bist", "ist", "sind", "ging", "gingen"};

        for (int i = 0; i < base.length; i++){
            VPPhraseSpec vp1 = nlgFactory.createVerbPhrase(base[i]);
            VPPhraseSpec vp2 = nlgFactory.createVerbPhrase(inflected[i]);

            Assertions.assertEquals(((WordElement) vp1.getVerb()).getBaseForm(), ((WordElement) vp2.getVerb()).getBaseForm());
        }
    }
}