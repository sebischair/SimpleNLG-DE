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
 *
 *
 */

package simplenlgde.features;

/**
 * <p>
 * An enumeration representing the different types of number agreement. The
 * number agreement is recorded in the {@code Feature.NUMBER} feature and
 * applies to nouns and verbs, and their associated phrases.
 * </p>
 */

public enum NumberAgreement {
    /**
     * This represents words that have the same form regardless of whether they
     * are singular or plural. For example, <em>Maedchen</em>, <em>Fehler</em>.
     */
    BOTH,

    /**
     * This represents verbs and nouns that are written in the plural. For
     * example, <em>Hunde</em> as opposed to <em>Hund</em></em>, and
     * <em>John und Simon <b>suchen</b> Mary</em>.
     */
    PLURAL,

    /**
     * This represents verbs and nouns that are written in the singular. For
     * example, <em>Hund</em> as opposed to <em>Hunde</em>, and
     * <em>John <b>sucht</b> Mary</em>.
     */
    SINGULAR;
}
