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
 * Contributor(s) German version: Kira Klimt, Technical University of Munich.
 *
 */

package simplenlgde.features;

/**
 * <p>
 * An enumeration representing the gender of the subject of a noun phrase, or
 * the object or subject of a verb phrase. The gender is recorded in the {@code Feature.GENDER}
 * feature and applies to nouns and pronouns.
 * </p>
 */

public enum Gender {

	/**
	 * A word or phrase pertaining to a male topic. For example, <em>er</em>,
	 * <em>ihm</em>, <em>sein</em>.
	 */
	MASCULINE,

	/**
	 * A word or phrase pertaining to a female topic. For example, <em>sie</em>,
	 * <em>ihr</em>.
	 */
	FEMININE,

	/**
	 * A word or phrase pertaining to a neutral or gender-less topic. For
	 * example, <em>es</em>, <em>sein</em>.
	 */
	NEUTER;
}
