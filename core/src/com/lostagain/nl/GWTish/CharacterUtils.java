package com.lostagain.nl.GWTish;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
//TODO: TIDY and remove unnessery stuff. This was put in purely to support isPrintable
public class CharacterUtils {
	
	 /**
     * Instances of this class represent particular subsets of the Unicode
     * character set.  The only family of subsets defined in the
     * <code>Character</code> class is <code>{@link Character.UnicodeBlock
     * UnicodeBlock}</code>.  Other portions of the Java API may define other
     * subsets for their own purposes.
     *
     * @since 1.2
     */
    public static class Subset  {

        private String name;

        /**
         * Constructs a new <code>Subset</code> instance.
         *
         * @exception NullPointerException if name is <code>null</code>
         * @param  name  The name of this subset
         */
        protected Subset(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            this.name = name;
        }

        /**
         * Compares two <code>Subset</code> objects for equality.
         * This method returns <code>true</code> if and only if
         * <code>this</code> and the argument refer to the same
         * object; since this method is <code>final</code>, this
         * guarantee holds for all subclasses.
         */
        public final boolean equals(Object obj) {
            return (this == obj);
        }

        /**
         * Returns the standard hash code as defined by the
         * <code>{@link Object#hashCode}</code> method.  This method
         * is <code>final</code> in order to ensure that the
         * <code>equals</code> and <code>hashCode</code> methods will
         * be consistent in all subclasses.
         */
        public final int hashCode() {
            return super.hashCode();
        }

        /**
         * Returns the name of this subset.
         */
        public final String toString() {
            return name;
        }
    }

    /**
     * A family of character subsets representing the character blocks in the
     * Unicode specification. Character blocks generally define characters
     * used for a specific script or purpose. A character is contained by
     * at most one Unicode block.
     *
     * @since 1.2
     */
    public static final class UnicodeBlock extends Subset {

        private static Map map = new HashMap();

        /**
         * Create a UnicodeBlock with the given identifier name.
         * This name must be the same as the block identifier.
         */
        private UnicodeBlock(String idName) {
            super(idName);
            map.put(idName.toUpperCase(Locale.US), this);
        }

        /**
         * Create a UnicodeBlock with the given identifier name and
         * alias name.
         */
        private UnicodeBlock(String idName, String alias) {
            this(idName);
            map.put(alias.toUpperCase(Locale.US), this);
        }

        /**
         * Create a UnicodeBlock with the given identifier name and
         * alias names.
         */
        private UnicodeBlock(String idName, String[] aliasName) {
            this(idName);
            if (aliasName != null) {
                for(int x=0; x<aliasName.length; ++x) {
                    map.put(aliasName[x].toUpperCase(Locale.US), this);
                }
            }
        }

        /**
         * Constant for the "Basic Latin" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock  BASIC_LATIN =
            new UnicodeBlock("BASIC_LATIN", new String[] {"Basic Latin", "BasicLatin" });

        /**
         * Constant for the "Latin-1 Supplement" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock LATIN_1_SUPPLEMENT =
            new UnicodeBlock("LATIN_1_SUPPLEMENT", new String[]{ "Latin-1 Supplement", "Latin-1Supplement"});

        /**
         * Constant for the "Latin Extended-A" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock LATIN_EXTENDED_A =
            new UnicodeBlock("LATIN_EXTENDED_A", new String[]{ "Latin Extended-A", "LatinExtended-A"});

        /**
         * Constant for the "Latin Extended-B" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock LATIN_EXTENDED_B =
            new UnicodeBlock("LATIN_EXTENDED_B", new String[] {"Latin Extended-B", "LatinExtended-B"});

        /**
         * Constant for the "IPA Extensions" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock IPA_EXTENSIONS =
            new UnicodeBlock("IPA_EXTENSIONS", new String[] {"IPA Extensions", "IPAExtensions"});

        /**
         * Constant for the "Spacing Modifier Letters" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock SPACING_MODIFIER_LETTERS =
            new UnicodeBlock("SPACING_MODIFIER_LETTERS", new String[] { "Spacing Modifier Letters",
                                                                        "SpacingModifierLetters"});

        /**
         * Constant for the "Combining Diacritical Marks" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS =
            new UnicodeBlock("COMBINING_DIACRITICAL_MARKS", new String[] {"Combining Diacritical Marks",
                                                                          "CombiningDiacriticalMarks" });

        /**
         * Constant for the "Greek and Coptic" Unicode character block.
         * <p>
         * This block was previously known as the "Greek" block.
         *
         * @since 1.2
         */
        public static final UnicodeBlock GREEK
            = new UnicodeBlock("GREEK", new String[] {"Greek and Coptic", "GreekandCoptic"});

        /**
         * Constant for the "Cyrillic" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock CYRILLIC =
            new UnicodeBlock("CYRILLIC");

        /**
         * Constant for the "Armenian" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock ARMENIAN =
            new UnicodeBlock("ARMENIAN");

        /**
         * Constant for the "Hebrew" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock HEBREW =
            new UnicodeBlock("HEBREW");

        /**
         * Constant for the "Arabic" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock ARABIC =
            new UnicodeBlock("ARABIC");

        /**
         * Constant for the "Devanagari" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock DEVANAGARI =
            new UnicodeBlock("DEVANAGARI");

        /**
         * Constant for the "Bengali" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock BENGALI =
            new UnicodeBlock("BENGALI");

        /**
         * Constant for the "Gurmukhi" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock GURMUKHI =
            new UnicodeBlock("GURMUKHI");

        /**
         * Constant for the "Gujarati" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock GUJARATI =
            new UnicodeBlock("GUJARATI");

        /**
         * Constant for the "Oriya" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock ORIYA =
            new UnicodeBlock("ORIYA");

        /**
         * Constant for the "Tamil" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock TAMIL =
            new UnicodeBlock("TAMIL");

        /**
         * Constant for the "Telugu" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock TELUGU =
            new UnicodeBlock("TELUGU");

        /**
         * Constant for the "Kannada" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock KANNADA =
            new UnicodeBlock("KANNADA");

        /**
         * Constant for the "Malayalam" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock MALAYALAM =
            new UnicodeBlock("MALAYALAM");

        /**
         * Constant for the "Thai" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock THAI =
            new UnicodeBlock("THAI");

        /**
         * Constant for the "Lao" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock LAO =
            new UnicodeBlock("LAO");

        /**
         * Constant for the "Tibetan" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock TIBETAN =
            new UnicodeBlock("TIBETAN");

        /**
         * Constant for the "Georgian" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock GEORGIAN =
            new UnicodeBlock("GEORGIAN");

        /**
         * Constant for the "Hangul Jamo" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock HANGUL_JAMO =
            new UnicodeBlock("HANGUL_JAMO", new String[] {"Hangul Jamo", "HangulJamo"});

        /**
         * Constant for the "Latin Extended Additional" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock LATIN_EXTENDED_ADDITIONAL =
            new UnicodeBlock("LATIN_EXTENDED_ADDITIONAL", new String[] {"Latin Extended Additional",
                                                                        "LatinExtendedAdditional"});

        /**
         * Constant for the "Greek Extended" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock GREEK_EXTENDED =
            new UnicodeBlock("GREEK_EXTENDED", new String[] {"Greek Extended", "GreekExtended"});

        /**
         * Constant for the "General Punctuation" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock GENERAL_PUNCTUATION =
            new UnicodeBlock("GENERAL_PUNCTUATION", new String[] {"General Punctuation", "GeneralPunctuation"});

        /**
         * Constant for the "Superscripts and Subscripts" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS =
            new UnicodeBlock("SUPERSCRIPTS_AND_SUBSCRIPTS", new String[] {"Superscripts and Subscripts",
                                                                          "SuperscriptsandSubscripts" });

        /**
         * Constant for the "Currency Symbols" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock CURRENCY_SYMBOLS =
            new UnicodeBlock("CURRENCY_SYMBOLS", new String[] { "Currency Symbols", "CurrencySymbols"});

        /**
         * Constant for the "Combining Diacritical Marks for Symbols" Unicode character block.
         * <p>
         * This block was previously known as "Combining Marks for Symbols".
         * @since 1.2
         */
        public static final UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS =
            new UnicodeBlock("COMBINING_MARKS_FOR_SYMBOLS", new String[] {"Combining Diacritical Marks for Symbols",
                                                                                                                                                  "CombiningDiacriticalMarksforSymbols",
                                                                          "Combining Marks for Symbols",
                                                                          "CombiningMarksforSymbols" });

        /**
         * Constant for the "Letterlike Symbols" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock LETTERLIKE_SYMBOLS =
            new UnicodeBlock("LETTERLIKE_SYMBOLS", new String[] { "Letterlike Symbols", "LetterlikeSymbols"});

        /**
         * Constant for the "Number Forms" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock NUMBER_FORMS =
            new UnicodeBlock("NUMBER_FORMS", new String[] {"Number Forms", "NumberForms"});

        /**
         * Constant for the "Arrows" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock ARROWS =
            new UnicodeBlock("ARROWS");

        /**
         * Constant for the "Mathematical Operators" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock MATHEMATICAL_OPERATORS =
            new UnicodeBlock("MATHEMATICAL_OPERATORS", new String[] {"Mathematical Operators",
                                                                     "MathematicalOperators"});

        /**
         * Constant for the "Miscellaneous Technical" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock MISCELLANEOUS_TECHNICAL =
            new UnicodeBlock("MISCELLANEOUS_TECHNICAL", new String[] {"Miscellaneous Technical",
                                                                      "MiscellaneousTechnical"});

        /**
         * Constant for the "Control Pictures" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock CONTROL_PICTURES =
            new UnicodeBlock("CONTROL_PICTURES", new String[] {"Control Pictures", "ControlPictures"});

        /**
         * Constant for the "Optical Character Recognition" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock OPTICAL_CHARACTER_RECOGNITION =
            new UnicodeBlock("OPTICAL_CHARACTER_RECOGNITION", new String[] {"Optical Character Recognition",
                                                                            "OpticalCharacterRecognition"});

        /**
         * Constant for the "Enclosed Alphanumerics" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock ENCLOSED_ALPHANUMERICS =
            new UnicodeBlock("ENCLOSED_ALPHANUMERICS", new String[] {"Enclosed Alphanumerics",
                                                                     "EnclosedAlphanumerics"});

        /**
         * Constant for the "Box Drawing" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock BOX_DRAWING =
            new UnicodeBlock("BOX_DRAWING", new String[] {"Box Drawing", "BoxDrawing"});

        /**
         * Constant for the "Block Elements" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock BLOCK_ELEMENTS =
            new UnicodeBlock("BLOCK_ELEMENTS", new String[] {"Block Elements", "BlockElements"});

        /**
         * Constant for the "Geometric Shapes" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock GEOMETRIC_SHAPES =
            new UnicodeBlock("GEOMETRIC_SHAPES", new String[] {"Geometric Shapes", "GeometricShapes"});

        /**
         * Constant for the "Miscellaneous Symbols" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock MISCELLANEOUS_SYMBOLS =
            new UnicodeBlock("MISCELLANEOUS_SYMBOLS", new String[] {"Miscellaneous Symbols",
                                                                    "MiscellaneousSymbols"});

        /**
         * Constant for the "Dingbats" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock DINGBATS =
            new UnicodeBlock("DINGBATS");

        /**
         * Constant for the "CJK Symbols and Punctuation" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION =
            new UnicodeBlock("CJK_SYMBOLS_AND_PUNCTUATION", new String[] {"CJK Symbols and Punctuation",
                                                                          "CJKSymbolsandPunctuation"});

        /**
         * Constant for the "Hiragana" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock HIRAGANA =
            new UnicodeBlock("HIRAGANA");

        /**
         * Constant for the "Katakana" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock KATAKANA =
            new UnicodeBlock("KATAKANA");

        /**
         * Constant for the "Bopomofo" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock BOPOMOFO =
            new UnicodeBlock("BOPOMOFO");

        /**
         * Constant for the "Hangul Compatibility Jamo" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock HANGUL_COMPATIBILITY_JAMO =
            new UnicodeBlock("HANGUL_COMPATIBILITY_JAMO", new String[] {"Hangul Compatibility Jamo",
                                                                        "HangulCompatibilityJamo"});

        /**
         * Constant for the "Kanbun" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock KANBUN =
            new UnicodeBlock("KANBUN");

        /**
         * Constant for the "Enclosed CJK Letters and Months" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS =
            new UnicodeBlock("ENCLOSED_CJK_LETTERS_AND_MONTHS", new String[] {"Enclosed CJK Letters and Months",
                                                                              "EnclosedCJKLettersandMonths"});

        /**
         * Constant for the "CJK Compatibility" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock CJK_COMPATIBILITY =
            new UnicodeBlock("CJK_COMPATIBILITY", new String[] {"CJK Compatibility", "CJKCompatibility"});

        /**
         * Constant for the "CJK Unified Ideographs" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS =
            new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS", new String[] {"CJK Unified Ideographs",
                                                                     "CJKUnifiedIdeographs"});

        /**
         * Constant for the "Hangul Syllables" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock HANGUL_SYLLABLES =
            new UnicodeBlock("HANGUL_SYLLABLES", new String[] {"Hangul Syllables", "HangulSyllables"});

        /**
         * Constant for the "Private Use Area" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock PRIVATE_USE_AREA =
            new UnicodeBlock("PRIVATE_USE_AREA", new String[] {"Private Use Area", "PrivateUseArea"});

        /**
         * Constant for the "CJK Compatibility Ideographs" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS =
            new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS",
                             new String[] {"CJK Compatibility Ideographs",
                                           "CJKCompatibilityIdeographs"});

        /**
         * Constant for the "Alphabetic Presentation Forms" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock ALPHABETIC_PRESENTATION_FORMS =
            new UnicodeBlock("ALPHABETIC_PRESENTATION_FORMS", new String[] {"Alphabetic Presentation Forms",
                                                                            "AlphabeticPresentationForms"});

        /**
         * Constant for the "Arabic Presentation Forms-A" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_A =
            new UnicodeBlock("ARABIC_PRESENTATION_FORMS_A", new String[] {"Arabic Presentation Forms-A",
                                                                          "ArabicPresentationForms-A"});

        /**
         * Constant for the "Combining Half Marks" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock COMBINING_HALF_MARKS =
            new UnicodeBlock("COMBINING_HALF_MARKS", new String[] {"Combining Half Marks",
                                                                   "CombiningHalfMarks"});

        /**
         * Constant for the "CJK Compatibility Forms" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock CJK_COMPATIBILITY_FORMS =
            new UnicodeBlock("CJK_COMPATIBILITY_FORMS", new String[] {"CJK Compatibility Forms",
                                                                      "CJKCompatibilityForms"});

        /**
         * Constant for the "Small Form Variants" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock SMALL_FORM_VARIANTS =
            new UnicodeBlock("SMALL_FORM_VARIANTS", new String[] {"Small Form Variants",
                                                                  "SmallFormVariants"});

        /**
         * Constant for the "Arabic Presentation Forms-B" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_B =
            new UnicodeBlock("ARABIC_PRESENTATION_FORMS_B", new String[] {"Arabic Presentation Forms-B",
                                                                          "ArabicPresentationForms-B"});

        /**
         * Constant for the "Halfwidth and Fullwidth Forms" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS =
            new UnicodeBlock("HALFWIDTH_AND_FULLWIDTH_FORMS",
                             new String[] {"Halfwidth and Fullwidth Forms",
                                           "HalfwidthandFullwidthForms"});

        /**
         * Constant for the "Specials" Unicode character block.
         * @since 1.2
         */
        public static final UnicodeBlock SPECIALS =
            new UnicodeBlock("SPECIALS");

        /**
         * @deprecated As of J2SE 5, use {@link #HIGH_SURROGATES},
         *             {@link #HIGH_PRIVATE_USE_SURROGATES}, and
         *             {@link #LOW_SURROGATES}. These new constants match
         *             the block definitions of the Unicode Standard.
         *             The {@link #of(char)} and {@link #of(int)} methods
         *             return the new constants, not SURROGATES_AREA.
         */
        @Deprecated
        public static final UnicodeBlock SURROGATES_AREA =
            new UnicodeBlock("SURROGATES_AREA");

        /**
         * Constant for the "Syriac" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock SYRIAC =
            new UnicodeBlock("SYRIAC");

        /**
         * Constant for the "Thaana" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock THAANA =
            new UnicodeBlock("THAANA");

        /**
         * Constant for the "Sinhala" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock SINHALA =
            new UnicodeBlock("SINHALA");

        /**
         * Constant for the "Myanmar" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock MYANMAR =
            new UnicodeBlock("MYANMAR");

        /**
         * Constant for the "Ethiopic" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock ETHIOPIC =
            new UnicodeBlock("ETHIOPIC");

        /**
         * Constant for the "Cherokee" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock CHEROKEE =
            new UnicodeBlock("CHEROKEE");

        /**
         * Constant for the "Unified Canadian Aboriginal Syllabics" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS =
            new UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS",
                             new String[] {"Unified Canadian Aboriginal Syllabics",
                                           "UnifiedCanadianAboriginalSyllabics"});

        /**
         * Constant for the "Ogham" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock OGHAM =
                             new UnicodeBlock("OGHAM");

        /**
         * Constant for the "Runic" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock RUNIC =
                             new UnicodeBlock("RUNIC");

        /**
         * Constant for the "Khmer" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock KHMER =
                             new UnicodeBlock("KHMER");

        /**
         * Constant for the "Mongolian" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock MONGOLIAN =
                             new UnicodeBlock("MONGOLIAN");

        /**
         * Constant for the "Braille Patterns" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock BRAILLE_PATTERNS =
            new UnicodeBlock("BRAILLE_PATTERNS", new String[] {"Braille Patterns",
                                                               "BraillePatterns"});

        /**
         * Constant for the "CJK Radicals Supplement" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock CJK_RADICALS_SUPPLEMENT =
             new UnicodeBlock("CJK_RADICALS_SUPPLEMENT", new String[] {"CJK Radicals Supplement",
                                                                       "CJKRadicalsSupplement"});

        /**
         * Constant for the "Kangxi Radicals" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock KANGXI_RADICALS =
            new UnicodeBlock("KANGXI_RADICALS", new String[] {"Kangxi Radicals", "KangxiRadicals"});

        /**
         * Constant for the "Ideographic Description Characters" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS =
            new UnicodeBlock("IDEOGRAPHIC_DESCRIPTION_CHARACTERS", new String[] {"Ideographic Description Characters",
                                                                                 "IdeographicDescriptionCharacters"});

        /**
         * Constant for the "Bopomofo Extended" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock BOPOMOFO_EXTENDED =
            new UnicodeBlock("BOPOMOFO_EXTENDED", new String[] {"Bopomofo Extended",
                                                                "BopomofoExtended"});

        /**
         * Constant for the "CJK Unified Ideographs Extension A" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A =
            new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A", new String[] {"CJK Unified Ideographs Extension A",
                                                                                 "CJKUnifiedIdeographsExtensionA"});

        /**
         * Constant for the "Yi Syllables" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock YI_SYLLABLES =
            new UnicodeBlock("YI_SYLLABLES", new String[] {"Yi Syllables", "YiSyllables"});

        /**
         * Constant for the "Yi Radicals" Unicode character block.
         * @since 1.4
         */
        public static final UnicodeBlock YI_RADICALS =
            new UnicodeBlock("YI_RADICALS", new String[] {"Yi Radicals", "YiRadicals"});


        /**
         * Constant for the "Cyrillic Supplementary" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock CYRILLIC_SUPPLEMENTARY =
            new UnicodeBlock("CYRILLIC_SUPPLEMENTARY", new String[] {"Cyrillic Supplementary",
                                                                     "CyrillicSupplementary"});

        /**
         * Constant for the "Tagalog" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock TAGALOG =
            new UnicodeBlock("TAGALOG");

        /**
         * Constant for the "Hanunoo" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock HANUNOO =
            new UnicodeBlock("HANUNOO");

        /**
         * Constant for the "Buhid" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock BUHID =
            new UnicodeBlock("BUHID");

        /**
         * Constant for the "Tagbanwa" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock TAGBANWA =
            new UnicodeBlock("TAGBANWA");

        /**
         * Constant for the "Limbu" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock LIMBU =
            new UnicodeBlock("LIMBU");

        /**
         * Constant for the "Tai Le" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock TAI_LE =
            new UnicodeBlock("TAI_LE", new String[] {"Tai Le", "TaiLe"});

        /**
         * Constant for the "Khmer Symbols" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock KHMER_SYMBOLS =
            new UnicodeBlock("KHMER_SYMBOLS", new String[] {"Khmer Symbols", "KhmerSymbols"});

        /**
         * Constant for the "Phonetic Extensions" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock PHONETIC_EXTENSIONS =
            new UnicodeBlock("PHONETIC_EXTENSIONS", new String[] {"Phonetic Extensions", "PhoneticExtensions"});

        /**
         * Constant for the "Miscellaneous Mathematical Symbols-A" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A =
            new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A",
                             new String[]{"Miscellaneous Mathematical Symbols-A",
                                          "MiscellaneousMathematicalSymbols-A"});

        /**
         * Constant for the "Supplemental Arrows-A" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock SUPPLEMENTAL_ARROWS_A =
            new UnicodeBlock("SUPPLEMENTAL_ARROWS_A", new String[] {"Supplemental Arrows-A",
                                                                    "SupplementalArrows-A"});

        /**
         * Constant for the "Supplemental Arrows-B" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock SUPPLEMENTAL_ARROWS_B =
            new UnicodeBlock("SUPPLEMENTAL_ARROWS_B", new String[] {"Supplemental Arrows-B",
                                                                    "SupplementalArrows-B"});

        /**
         * Constant for the "Miscellaneous Mathematical Symbols-B" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B
                = new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B",
                                   new String[] {"Miscellaneous Mathematical Symbols-B",
                                                 "MiscellaneousMathematicalSymbols-B"});

        /**
         * Constant for the "Supplemental Mathematical Operators" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS =
            new UnicodeBlock("SUPPLEMENTAL_MATHEMATICAL_OPERATORS",
                             new String[]{"Supplemental Mathematical Operators",
                                          "SupplementalMathematicalOperators"} );

        /**
         * Constant for the "Miscellaneous Symbols and Arrows" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS =
            new UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_ARROWS", new String[] {"Miscellaneous Symbols and Arrows",
                                                                               "MiscellaneousSymbolsandArrows"});

        /**
         * Constant for the "Katakana Phonetic Extensions" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS =
            new UnicodeBlock("KATAKANA_PHONETIC_EXTENSIONS", new String[] {"Katakana Phonetic Extensions",
                                                                           "KatakanaPhoneticExtensions"});

        /**
         * Constant for the "Yijing Hexagram Symbols" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock YIJING_HEXAGRAM_SYMBOLS =
            new UnicodeBlock("YIJING_HEXAGRAM_SYMBOLS", new String[] {"Yijing Hexagram Symbols",
                                                                      "YijingHexagramSymbols"});

        /**
         * Constant for the "Variation Selectors" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock VARIATION_SELECTORS =
            new UnicodeBlock("VARIATION_SELECTORS", new String[] {"Variation Selectors", "VariationSelectors"});

        /**
         * Constant for the "Linear B Syllabary" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock LINEAR_B_SYLLABARY =
            new UnicodeBlock("LINEAR_B_SYLLABARY", new String[] {"Linear B Syllabary", "LinearBSyllabary"});

        /**
         * Constant for the "Linear B Ideograms" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock LINEAR_B_IDEOGRAMS =
            new UnicodeBlock("LINEAR_B_IDEOGRAMS", new String[] {"Linear B Ideograms", "LinearBIdeograms"});

        /**
         * Constant for the "Aegean Numbers" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock AEGEAN_NUMBERS =
            new UnicodeBlock("AEGEAN_NUMBERS", new String[] {"Aegean Numbers", "AegeanNumbers"});

        /**
         * Constant for the "Old Italic" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock OLD_ITALIC =
            new UnicodeBlock("OLD_ITALIC", new String[] {"Old Italic", "OldItalic"});

        /**
         * Constant for the "Gothic" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock GOTHIC = new UnicodeBlock("GOTHIC");

        /**
         * Constant for the "Ugaritic" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock UGARITIC = new UnicodeBlock("UGARITIC");

        /**
         * Constant for the "Deseret" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock DESERET = new UnicodeBlock("DESERET");

        /**
         * Constant for the "Shavian" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock SHAVIAN = new UnicodeBlock("SHAVIAN");

        /**
         * Constant for the "Osmanya" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock OSMANYA = new UnicodeBlock("OSMANYA");

        /**
         * Constant for the "Cypriot Syllabary" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock CYPRIOT_SYLLABARY =
            new UnicodeBlock("CYPRIOT_SYLLABARY", new String[] {"Cypriot Syllabary", "CypriotSyllabary"});

        /**
         * Constant for the "Byzantine Musical Symbols" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS =
            new UnicodeBlock("BYZANTINE_MUSICAL_SYMBOLS", new String[] {"Byzantine Musical Symbols",
                                                                        "ByzantineMusicalSymbols"});

        /**
         * Constant for the "Musical Symbols" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock MUSICAL_SYMBOLS =
            new UnicodeBlock("MUSICAL_SYMBOLS", new String[] {"Musical Symbols", "MusicalSymbols"});

        /**
         * Constant for the "Tai Xuan Jing Symbols" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock TAI_XUAN_JING_SYMBOLS =
            new UnicodeBlock("TAI_XUAN_JING_SYMBOLS", new String[] {"Tai Xuan Jing Symbols",
                                                                     "TaiXuanJingSymbols"});

        /**
         * Constant for the "Mathematical Alphanumeric Symbols" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS =
            new UnicodeBlock("MATHEMATICAL_ALPHANUMERIC_SYMBOLS",
                             new String[] {"Mathematical Alphanumeric Symbols", "MathematicalAlphanumericSymbols"});

        /**
         * Constant for the "CJK Unified Ideographs Extension B" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B =
            new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B",
                             new String[] {"CJK Unified Ideographs Extension B", "CJKUnifiedIdeographsExtensionB"});

        /**
         * Constant for the "CJK Compatibility Ideographs Supplement" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT =
            new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT",
                             new String[]{"CJK Compatibility Ideographs Supplement",
                                          "CJKCompatibilityIdeographsSupplement"});

        /**
         * Constant for the "Tags" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock TAGS = new UnicodeBlock("TAGS");

        /**
         * Constant for the "Variation Selectors Supplement" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT =
            new UnicodeBlock("VARIATION_SELECTORS_SUPPLEMENT", new String[] {"Variation Selectors Supplement",
                                                                             "VariationSelectorsSupplement"});

        /**
         * Constant for the "Supplementary Private Use Area-A" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A =
            new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_A",
                             new String[] {"Supplementary Private Use Area-A",
                                           "SupplementaryPrivateUseArea-A"});

        /**
         * Constant for the "Supplementary Private Use Area-B" Unicode character block.
         * @since 1.5
         */
        public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B =
            new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_B",
                             new String[] {"Supplementary Private Use Area-B",
                                           "SupplementaryPrivateUseArea-B"});

        /**
         * Constant for the "High Surrogates" Unicode character block.
         * This block represents codepoint values in the high surrogate
         * range: 0xD800 through 0xDB7F
         *
         * @since 1.5
         */
        public static final UnicodeBlock HIGH_SURROGATES =
            new UnicodeBlock("HIGH_SURROGATES", new String[] {"High Surrogates", "HighSurrogates"});

        /**
         * Constant for the "High Private Use Surrogates" Unicode character block.
         * This block represents codepoint values in the high surrogate
         * range: 0xDB80 through 0xDBFF
         *
         * @since 1.5
         */
        public static final UnicodeBlock HIGH_PRIVATE_USE_SURROGATES =
            new UnicodeBlock("HIGH_PRIVATE_USE_SURROGATES", new String[] { "High Private Use Surrogates",
                                                                           "HighPrivateUseSurrogates"});

        /**
         * Constant for the "Low Surrogates" Unicode character block.
         * This block represents codepoint values in the high surrogate
         * range: 0xDC00 through 0xDFFF
         *
         * @since 1.5
         */
        public static final UnicodeBlock LOW_SURROGATES =
            new UnicodeBlock("LOW_SURROGATES", new String[] {"Low Surrogates", "LowSurrogates"});

        private static final int blockStarts[] = {
            0x0000, // Basic Latin
            0x0080, // Latin-1 Supplement
            0x0100, // Latin Extended-A
            0x0180, // Latin Extended-B
            0x0250, // IPA Extensions
            0x02B0, // Spacing Modifier Letters
            0x0300, // Combining Diacritical Marks
            0x0370, // Greek and Coptic
            0x0400, // Cyrillic
            0x0500, // Cyrillic Supplementary
            0x0530, // Armenian
            0x0590, // Hebrew
            0x0600, // Arabic
            0x0700, // Syriac
            0x0750, // unassigned
            0x0780, // Thaana
            0x07C0, // unassigned
            0x0900, // Devanagari
            0x0980, // Bengali
            0x0A00, // Gurmukhi
            0x0A80, // Gujarati
            0x0B00, // Oriya
            0x0B80, // Tamil
            0x0C00, // Telugu
            0x0C80, // Kannada
            0x0D00, // Malayalam
            0x0D80, // Sinhala
            0x0E00, // Thai
            0x0E80, // Lao
            0x0F00, // Tibetan
            0x1000, // Myanmar
            0x10A0, // Georgian
            0x1100, // Hangul Jamo
            0x1200, // Ethiopic
            0x1380, // unassigned
            0x13A0, // Cherokee
            0x1400, // Unified Canadian Aboriginal Syllabics
            0x1680, // Ogham
            0x16A0, // Runic
            0x1700, // Tagalog
            0x1720, // Hanunoo
            0x1740, // Buhid
            0x1760, // Tagbanwa
            0x1780, // Khmer
            0x1800, // Mongolian
            0x18B0, // unassigned
            0x1900, // Limbu
            0x1950, // Tai Le
            0x1980, // unassigned
            0x19E0, // Khmer Symbols
            0x1A00, // unassigned
            0x1D00, // Phonetic Extensions
            0x1D80, // unassigned
            0x1E00, // Latin Extended Additional
            0x1F00, // Greek Extended
            0x2000, // General Punctuation
            0x2070, // Superscripts and Subscripts
            0x20A0, // Currency Symbols
            0x20D0, // Combining Diacritical Marks for Symbols
            0x2100, // Letterlike Symbols
            0x2150, // Number Forms
            0x2190, // Arrows
            0x2200, // Mathematical Operators
            0x2300, // Miscellaneous Technical
            0x2400, // Control Pictures
            0x2440, // Optical Character Recognition
            0x2460, // Enclosed Alphanumerics
            0x2500, // Box Drawing
            0x2580, // Block Elements
            0x25A0, // Geometric Shapes
            0x2600, // Miscellaneous Symbols
            0x2700, // Dingbats
            0x27C0, // Miscellaneous Mathematical Symbols-A
            0x27F0, // Supplemental Arrows-A
            0x2800, // Braille Patterns
            0x2900, // Supplemental Arrows-B
            0x2980, // Miscellaneous Mathematical Symbols-B
            0x2A00, // Supplemental Mathematical Operators
            0x2B00, // Miscellaneous Symbols and Arrows
            0x2C00, // unassigned
            0x2E80, // CJK Radicals Supplement
            0x2F00, // Kangxi Radicals
            0x2FE0, // unassigned
            0x2FF0, // Ideographic Description Characters
            0x3000, // CJK Symbols and Punctuation
            0x3040, // Hiragana
            0x30A0, // Katakana
            0x3100, // Bopomofo
            0x3130, // Hangul Compatibility Jamo
            0x3190, // Kanbun
            0x31A0, // Bopomofo Extended
            0x31C0, // unassigned
            0x31F0, // Katakana Phonetic Extensions
            0x3200, // Enclosed CJK Letters and Months
            0x3300, // CJK Compatibility
            0x3400, // CJK Unified Ideographs Extension A
            0x4DC0, // Yijing Hexagram Symbols
            0x4E00, // CJK Unified Ideographs
            0xA000, // Yi Syllables
            0xA490, // Yi Radicals
            0xA4D0, // unassigned
            0xAC00, // Hangul Syllables
            0xD7B0, // unassigned
            0xD800, // High Surrogates
            0xDB80, // High Private Use Surrogates
            0xDC00, // Low Surrogates
            0xE000, // Private Use
            0xF900, // CJK Compatibility Ideographs
            0xFB00, // Alphabetic Presentation Forms
            0xFB50, // Arabic Presentation Forms-A
            0xFE00, // Variation Selectors
            0xFE10, // unassigned
            0xFE20, // Combining Half Marks
            0xFE30, // CJK Compatibility Forms
            0xFE50, // Small Form Variants
            0xFE70, // Arabic Presentation Forms-B
            0xFF00, // Halfwidth and Fullwidth Forms
            0xFFF0, // Specials
            0x10000, // Linear B Syllabary
            0x10080, // Linear B Ideograms
            0x10100, // Aegean Numbers
            0x10140, // unassigned
            0x10300, // Old Italic
            0x10330, // Gothic
            0x10350, // unassigned
            0x10380, // Ugaritic
            0x103A0, // unassigned
            0x10400, // Deseret
            0x10450, // Shavian
            0x10480, // Osmanya
            0x104B0, // unassigned
            0x10800, // Cypriot Syllabary
            0x10840, // unassigned
            0x1D000, // Byzantine Musical Symbols
            0x1D100, // Musical Symbols
            0x1D200, // unassigned
            0x1D300, // Tai Xuan Jing Symbols
            0x1D360, // unassigned
            0x1D400, // Mathematical Alphanumeric Symbols
            0x1D800, // unassigned
            0x20000, // CJK Unified Ideographs Extension B
            0x2A6E0, // unassigned
            0x2F800, // CJK Compatibility Ideographs Supplement
            0x2FA20, // unassigned
            0xE0000, // Tags
            0xE0080, // unassigned
            0xE0100, // Variation Selectors Supplement
            0xE01F0, // unassigned
            0xF0000, // Supplementary Private Use Area-A
            0x100000, // Supplementary Private Use Area-B
        };

        private static final UnicodeBlock[] blocks = {
            BASIC_LATIN,
            LATIN_1_SUPPLEMENT,
            LATIN_EXTENDED_A,
            LATIN_EXTENDED_B,
            IPA_EXTENSIONS,
            SPACING_MODIFIER_LETTERS,
            COMBINING_DIACRITICAL_MARKS,
            GREEK,
            CYRILLIC,
            CYRILLIC_SUPPLEMENTARY,
            ARMENIAN,
            HEBREW,
            ARABIC,
            SYRIAC,
            null,
            THAANA,
            null,
            DEVANAGARI,
            BENGALI,
            GURMUKHI,
            GUJARATI,
            ORIYA,
            TAMIL,
            TELUGU,
            KANNADA,
            MALAYALAM,
            SINHALA,
            THAI,
            LAO,
            TIBETAN,
            MYANMAR,
            GEORGIAN,
            HANGUL_JAMO,
            ETHIOPIC,
            null,
            CHEROKEE,
            UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS,
            OGHAM,
            RUNIC,
            TAGALOG,
            HANUNOO,
            BUHID,
            TAGBANWA,
            KHMER,
            MONGOLIAN,
            null,
            LIMBU,
            TAI_LE,
            null,
            KHMER_SYMBOLS,
            null,
            PHONETIC_EXTENSIONS,
            null,
            LATIN_EXTENDED_ADDITIONAL,
            GREEK_EXTENDED,
            GENERAL_PUNCTUATION,
            SUPERSCRIPTS_AND_SUBSCRIPTS,
            CURRENCY_SYMBOLS,
            COMBINING_MARKS_FOR_SYMBOLS,
            LETTERLIKE_SYMBOLS,
            NUMBER_FORMS,
            ARROWS,
            MATHEMATICAL_OPERATORS,
            MISCELLANEOUS_TECHNICAL,
            CONTROL_PICTURES,
            OPTICAL_CHARACTER_RECOGNITION,
            ENCLOSED_ALPHANUMERICS,
            BOX_DRAWING,
            BLOCK_ELEMENTS,
            GEOMETRIC_SHAPES,
            MISCELLANEOUS_SYMBOLS,
            DINGBATS,
            MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A,
            SUPPLEMENTAL_ARROWS_A,
            BRAILLE_PATTERNS,
            SUPPLEMENTAL_ARROWS_B,
            MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B,
            SUPPLEMENTAL_MATHEMATICAL_OPERATORS,
            MISCELLANEOUS_SYMBOLS_AND_ARROWS,
            null,
            CJK_RADICALS_SUPPLEMENT,
            KANGXI_RADICALS,
            null,
            IDEOGRAPHIC_DESCRIPTION_CHARACTERS,
            CJK_SYMBOLS_AND_PUNCTUATION,
            HIRAGANA,
            KATAKANA,
            BOPOMOFO,
            HANGUL_COMPATIBILITY_JAMO,
            KANBUN,
            BOPOMOFO_EXTENDED,
            null,
            KATAKANA_PHONETIC_EXTENSIONS,
            ENCLOSED_CJK_LETTERS_AND_MONTHS,
            CJK_COMPATIBILITY,
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A,
            YIJING_HEXAGRAM_SYMBOLS,
            CJK_UNIFIED_IDEOGRAPHS,
            YI_SYLLABLES,
            YI_RADICALS,
            null,
            HANGUL_SYLLABLES,
            null,
            HIGH_SURROGATES,
            HIGH_PRIVATE_USE_SURROGATES,
            LOW_SURROGATES,
            PRIVATE_USE_AREA,
            CJK_COMPATIBILITY_IDEOGRAPHS,
            ALPHABETIC_PRESENTATION_FORMS,
            ARABIC_PRESENTATION_FORMS_A,
            VARIATION_SELECTORS,
            null,
            COMBINING_HALF_MARKS,
            CJK_COMPATIBILITY_FORMS,
            SMALL_FORM_VARIANTS,
            ARABIC_PRESENTATION_FORMS_B,
            HALFWIDTH_AND_FULLWIDTH_FORMS,
            SPECIALS,
            LINEAR_B_SYLLABARY,
            LINEAR_B_IDEOGRAMS,
            AEGEAN_NUMBERS,
            null,
            OLD_ITALIC,
            GOTHIC,
            null,
            UGARITIC,
            null,
            DESERET,
            SHAVIAN,
            OSMANYA,
            null,
            CYPRIOT_SYLLABARY,
            null,
            BYZANTINE_MUSICAL_SYMBOLS,
            MUSICAL_SYMBOLS,
            null,
            TAI_XUAN_JING_SYMBOLS,
            null,
            MATHEMATICAL_ALPHANUMERIC_SYMBOLS,
            null,
            CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B,
            null,
            CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT,
            null,
            TAGS,
            null,
            VARIATION_SELECTORS_SUPPLEMENT,
            null,
            SUPPLEMENTARY_PRIVATE_USE_AREA_A,
            SUPPLEMENTARY_PRIVATE_USE_AREA_B
        };


        /**
         * Returns the object representing the Unicode block containing the
         * given character, or <code>null</code> if the character is not a
         * member of a defined block.
         *
                 * <p><b>Note:</b> This method cannot handle <a
                 * href="Character.html#supplementary"> supplementary
                 * characters</a>. To support all Unicode characters,
                 * including supplementary characters, use the {@link
                 * #of(int)} method.
         *
         * @param   c  The character in question
         * @return  The <code>UnicodeBlock</code> instance representing the
         *          Unicode block of which this character is a member, or
         *          <code>null</code> if the character is not a member of any
         *          Unicode block
         */
        public static UnicodeBlock of(char c) {
            return of((int)c);
        }


        /**
         * Returns the object representing the Unicode block
         * containing the given character (Unicode code point), or
         * <code>null</code> if the character is not a member of a
         * defined block.
         *
                 * @param   codePoint the character (Unicode code point) in question.
         * @return  The <code>UnicodeBlock</code> instance representing the
         *          Unicode block of which this character is a member, or
         *          <code>null</code> if the character is not a member of any
         *          Unicode block
                 * @exception IllegalArgumentException if the specified
                 * <code>codePoint</code> is an invalid Unicode code point.
                 * @see Character#isValidCodePoint(int)
                 * @since   1.5
         */
        public static UnicodeBlock of(int codePoint) {
            if (!isValidCodePoint(codePoint)) {
                throw new IllegalArgumentException();
            }

            int top, bottom, current;
            bottom = 0;
            top = blockStarts.length;
            current = top/2;

            // invariant: top > current >= bottom && codePoint >= unicodeBlockStarts[bottom]
            while (top - bottom > 1) {
                if (codePoint >= blockStarts[current]) {
                    bottom = current;
                } else {
                    top = current;
                }
                current = (top + bottom) / 2;
            }
            return blocks[current];
        }

        /**
         * Returns the UnicodeBlock with the given name. Block
         * names are determined by The Unicode Standard. The file
         * Blocks-&lt;version&gt;.txt defines blocks for a particular
         * version of the standard. The {@link Character} class specifies
         * the version of the standard that it supports.
         * <p>
         * This method accepts block names in the following forms:
         * <ol>
         * <li> Canonical block names as defined by the Unicode Standard.
         * For example, the standard defines a "Basic Latin" block. Therefore, this
         * method accepts "Basic Latin" as a valid block name. The documentation of
         * each UnicodeBlock provides the canonical name.
         * <li>Canonical block names with all spaces removed. For example, "BasicLatin"
         * is a valid block name for the "Basic Latin" block.
         * <li>The text representation of each constant UnicodeBlock identifier.
         * For example, this method will return the {@link #BASIC_LATIN} block if
         * provided with the "BASIC_LATIN" name. This form replaces all spaces and
         *  hyphens in the canonical name with underscores.
         * </ol>
         * Finally, character case is ignored for all of the valid block name forms.
         * For example, "BASIC_LATIN" and "basic_latin" are both valid block names.
         * The en_US locale's case mapping rules are used to provide case-insensitive
         * string comparisons for block name validation.
         * <p>
         * If the Unicode Standard changes block names, both the previous and
         * current names will be accepted.
         *
         * @param blockName A <code>UnicodeBlock</code> name.
         * @return The <code>UnicodeBlock</code> instance identified
         *         by <code>blockName</code>
         * @throws IllegalArgumentException if <code>blockName</code> is an
         *         invalid name
         * @throws NullPointerException if <code>blockName</code> is null
         * @since 1.5
         */
        public static final UnicodeBlock forName(String blockName) {
            UnicodeBlock block = (UnicodeBlock)map.get(blockName.toUpperCase(Locale.US));
            if (block == null) {
                throw new IllegalArgumentException();
            }
            return block;
        }
    }
	
    
	public static boolean isPrintableChar( char c ) {
		CharacterUtils.UnicodeBlock block = CharacterUtils.UnicodeBlock.of( c );
        return (!isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != CharacterUtils.UnicodeBlock.SPECIALS;
    }
	
	   /**
     * Determines if the specified character is an ISO control
     * character.  A character is considered to be an ISO control
     * character if its code is in the range <code>'&#92;u0000'</code>
     * through <code>'&#92;u001F'</code> or in the range
     * <code>'&#92;u007F'</code> through <code>'&#92;u009F'</code>.
     *
     * <p><b>Note:</b> This method cannot handle <a
     * href="#supplementary"> supplementary characters</a>. To support
     * all Unicode characters, including supplementary characters, use
     * the {@link #isISOControl(int)} method.
     *
     * @param   ch      the character to be tested.
     * @return  <code>true</code> if the character is an ISO control character;
     *          <code>false</code> otherwise.
     *
     * @see     java.lang.Character#isSpaceChar(char)
     * @see     java.lang.Character#isWhitespace(char)
     * @since   1.1
     */
    public static boolean isISOControl(char ch) {
        return isISOControl((int)ch);
    }

    /**
     * Determines if the referenced character (Unicode code point) is an ISO control
     * character.  A character is considered to be an ISO control
     * character if its code is in the range <code>'&#92;u0000'</code>
     * through <code>'&#92;u001F'</code> or in the range
     * <code>'&#92;u007F'</code> through <code>'&#92;u009F'</code>.
     *
     * @param   codePoint the character (Unicode code point) to be tested.
     * @return  <code>true</code> if the character is an ISO control character;
     *          <code>false</code> otherwise.
     * @see     java.lang.Character#isSpaceChar(int)
     * @see     java.lang.Character#isWhitespace(int)
     * @since   1.5
     */
    public static boolean isISOControl(int codePoint) {
        return (codePoint >= 0x0000 && codePoint <= 0x001F) ||
            (codePoint >= 0x007F && codePoint <= 0x009F);
    }
    


    /**
     * The minimum value of a Unicode code point.
     *
     * @since 1.5
     */
    public static final int MIN_CODE_POINT = 0x000000;

    /**
     * The maximum value of a Unicode code point.
     *
     * @since 1.5
     */
    public static final int MAX_CODE_POINT = 0x10ffff;

    /**
     * Determines whether the specified code point is a valid Unicode
     * code point value in the range of <code>0x0000</code> to
     * <code>0x10FFFF</code> inclusive. This method is equivalent to
     * the expression:
     *
     * <blockquote><pre>
     * codePoint >= 0x0000 && codePoint <= 0x10FFFF
     * </pre></blockquote>
     *
     * @param  codePoint the Unicode code point to be tested
     * @return <code>true</code> if the specified code point value
     * is a valid code point value;
     * <code>false</code> otherwise.
     * @since  1.5
     */
    public static boolean isValidCodePoint(int codePoint) {
        return codePoint >= MIN_CODE_POINT && codePoint <= MAX_CODE_POINT;
    }

}
