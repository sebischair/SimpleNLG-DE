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
 * Contributor(s): Kira Klimt, Technical University of Munich.
 */

package simplenlgde.features;

/**
 * <p>
 * An enumeration representing the different forms an article can take. 
 * The form is recorded under the {@code Feature.ARTICLE_FORM} feature
 * and applies to nouns and noun phrases.
 * </p>
 * @author Kira Klimt, Technical University of Munich.
 * @version 4.0
 *
 */
public enum ArticleForm {
    /**
     * The definite article. For example, the verb <em>der</em>,
     * <em>die</em>, or <em>das</em>, <em>the</em> in English.
     */
    DEFINITE,
    
    /**
     * The indefinite article. For example, the verb <em>ein</em>,
     * <em>eine</em>, or <em>eines</em>, <em>a</em> in English.
     */
	INDEFINITE,

    /**
     * This indicates nouns specified without any articles.
     */
    NONE;
}
