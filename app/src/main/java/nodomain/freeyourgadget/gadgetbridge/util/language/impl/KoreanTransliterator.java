/*  Copyright (C) 2020-2021 Ted Stein

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.util.language.impl;

import java.util.Optional;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import nodomain.freeyourgadget.gadgetbridge.util.language.Transliterator;

// Implements Revised Romanization of Korean as well as we can without understanding any grammar.
//
// https://en.wikipedia.org/wiki/Revised_Romanization_of_Korean
public class KoreanTransliterator implements Transliterator {
    // https://en.wikipedia.org/wiki/Hangul_Jamo_%28Unicode_block%29
    private static final char JAMO_BLOCK_START = 0x1100;
    private static final char JAMO_BLOCK_END = 0x11FF;
    // https://en.wikipedia.org/wiki/Hangul_Syllables
    private static final char SYLLABLES_BLOCK_START = 0xAC00;
    private static final char SYLLABLES_BLOCK_END = 0xD7A3;
    // https://en.wikipedia.org/wiki/Hangul_Compatibility_Jamo
    private static final char COMPAT_JAMO_BLOCK_START = 0x3131;
    private static final char COMPAT_JAMO_BLOCK_END = 0x318E;

    // Returns whether a char is in the given block. Both bounds are inclusive.
    private static boolean inRange(char c, char start, char end) {
        return c >= start && c <= end;
    }

    // User input consisting of isolated jamo is usually mapped to the KS X 1001 compatibility
    // block, but jamo resulting from decomposed syllables are mapped to the modern one. This
    // function maps compat jamo to modern ones where possible and returns all other characters
    // unmodified.
    //
    // https://en.wikipedia.org/wiki/Hangul_Compatibility_Jamo
    // https://en.wikipedia.org/wiki/Hangul_Jamo_%28Unicode_block%29
    private static char decompatJamo(char jamo) {
        // KS X 1001 Hangul filler, not used in modern Unicode. A useful landmark in the
        // compatibility jamo block.
        // https://en.wikipedia.org/wiki/KS_X_1001#Hangul_Filler
        final char HANGUL_FILLER = 0x3164;

        // Don't do anything to characters outside the compatibility jamo block.
        if (!inRange(jamo, COMPAT_JAMO_BLOCK_START, COMPAT_JAMO_BLOCK_END)) { return jamo; }

        // Vowels are contiguous, in the same order, and unambiguous, so it's a simple offset.
        if (jamo >= 0x314F && jamo < HANGUL_FILLER) {
            return (char)(jamo - 0x1FEE);
        }

        // Consonants are organized differently. No clean way to do this.
        //
        // The compatibility jamo block doesn't distinguish between Choseong (leading) and Jongseong
        // (final) positions, but the modern block does. We map to Choseong here.
        switch (jamo) {
            case 0x3131: return 0x1100;     // ???
            case 0x3132: return 0x1101;     // ???
            case 0x3134: return 0x1102;     // ???
            case 0x3137: return 0x1103;     // ???
            case 0x3138: return 0x1104;     // ???
            case 0x3139: return 0x1105;     // ???
            case 0x3141: return 0x1106;     // ???
            case 0x3142: return 0x1107;     // ???
            case 0x3143: return 0x1108;     // ???
            case 0x3145: return 0x1109;     // ???
            case 0x3146: return 0x110A;     // ???
            case 0x3147: return 0x110B;     // ???
            case 0x3148: return 0x110C;     // ???
            case 0x3149: return 0x110D;     // ???
            case 0x314A: return 0x110E;     // ???
            case 0x314B: return 0x110F;     // ???
            case 0x314C: return 0x1110;     // ???
            case 0x314D: return 0x1111;     // ???
            case 0x314E: return 0x1112;     // ???
        }

        // The rest of the compatibility block consists of archaic compounds that are unlikely to be
        // encountered in modern systems. Just leave them alone.
        return jamo;
    }

    // Transliterates jamo one at a time. Returns its input if it isn't in the modern jamo block.
    private static String transliterateSingleJamo(char jamo) {
        jamo = decompatJamo(jamo);

        switch (jamo) {
            // Choseong (leading position consonants)
            case 0x1100: return "g";    // ???
            case 0x1101: return "kk";   // ???
            case 0x1102: return "n";    // ???
            case 0x1103: return "d";    // ???
            case 0x1104: return "tt";   // ???
            case 0x1105: return "r";    // ???
            case 0x1106: return "m";    // ???
            case 0x1107: return "b";    // ???
            case 0x1108: return "pp";   // ???
            case 0x1109: return "s";    // ???
            case 0x110A: return "ss";   // ???
            case 0x110B: return "";     // ???
            case 0x110C: return "j";    // ???
            case 0x110D: return "jj";   // ???
            case 0x110E: return "ch";   // ???
            case 0x110F: return "k";    // ???
            case 0x1110: return "t";    // ???
            case 0x1111: return "p";    // ???
            case 0x1112: return "h";    // ???
            // Jungseong (vowels)
            case 0x1161: return "a";    // ???
            case 0x1162: return "ae";   // ???
            case 0x1163: return "ya";   // ???
            case 0x1164: return "yae";  // ???
            case 0x1165: return "eo";   // ???
            case 0x1166: return "e";    // ???
            case 0x1167: return "yeo";  // ???
            case 0x1168: return "ye";   // ???
            case 0x1169: return "o";    // ???
            case 0x116A: return "wa";   // ???
            case 0x116B: return "wae";  // ???
            case 0x116C: return "oe";   // ???
            case 0x116D: return "yo";   // ???
            case 0x116E: return "u";    // ???
            case 0x116F: return "wo";   // ???
            case 0x1170: return "we";   // ???
            case 0x1171: return "wi";   // ???
            case 0x1172: return "yu";   // ???
            case 0x1173: return "eu";   // ???
            case 0x1174: return "ui";   // ???
            case 0x1175: return "i";    // ???
            // Jongseong (final position consonants)
            case 0x11A8: return "k";    // ???
            case 0x11A9: return "k";    // ???
            case 0x11AB: return "n";    // ???
            case 0x11AE: return "t";    // ???
            case 0x11AF: return "l";    // ???
            case 0x11B7: return "m";    // ???
            case 0x11B8: return "p";    // ???
            case 0x11BA: return "t";    // ???
            case 0x11BB: return "t";    // ???
            case 0x11BC: return "ng";   // ???
            case 0x11BD: return "t";    // ???
            case 0x11BE: return "t";    // ???
            case 0x11BF: return "k";    // ???
            case 0x11C0: return "t";    // ???
            case 0x11C1: return "p";    // ???
            case 0x11C2: return "t";    // ???
        }

        // Input was not jamo.
        return String.valueOf(jamo);
    }

    // Some combinations of ending jamo in one syllable and initial jamo in the next are romanized
    // irregularly. These exceptions are called "special provisions". In cases where multiple
    // romanizations are permitted, we use the one that's least commonly used elsewhere.
    //
    // Returns no value if either character is not in the modern jamo block, or if there is no
    // special provision for that pair of jamo.
    public static Optional<String> transliterateSpecialProvisions(char previousEnding, char nextInitial) {
        // Special provisions only apply if both characters are in the modern jamo block.
        if (!inRange(previousEnding, JAMO_BLOCK_START, JAMO_BLOCK_END)) { return Optional.empty(); }
        if (!inRange(nextInitial, JAMO_BLOCK_START, JAMO_BLOCK_END)) { return Optional.empty(); }

        // Jongseong (final position) ??? has a number of special provisions.
        if (previousEnding == 0x11C2) { // ???
            switch (nextInitial) {
                case 0x110B: return Optional.of("h");       // ???
                case 0x1100: return Optional.of("k");       // ???
                case 0x1102: return Optional.of("nn");      // ???
                case 0x1103: return Optional.of("t");       // ???
                case 0x1105: return Optional.of("nn");      // ???
                case 0x1106: return Optional.of("nm");      // ???
                case 0x1107: return Optional.of("p");       // ???
                case 0x1109: return Optional.of("hs");      // ???
                case 0x110C: return Optional.of("ch");      // ???
                case 0x1112: return Optional.of("t");       // ???
                default: return Optional.empty();
            }
        }

        // Otherwise, special provisions are denser when grouped by the second jamo.
        switch (nextInitial) {
            case 0x1100: // ???
                switch (previousEnding) {
                    case 0x11AB: return Optional.of("n-g"); // ???
                    default: return Optional.empty();
                }
            case 0x1102: // ???
                switch (previousEnding) {
                    case 0x11A8: return Optional.of("ngn"); // ???
                    case 0x11AE:                            // ???
                    case 0x11BA:                            // ???
                    case 0x11BD:                            // ???
                    case 0x11BE:                            // ???
                    case 0x11C0:                            // ???
                        return Optional.of("nn");
                    case 0x11AF: return Optional.of("ll");  // ???
                    case 0x11B8: return Optional.of("mn");  // ???
                    default: return Optional.empty();
                }
            case 0x1105: // ???
                switch (previousEnding) {
                    case 0x11A8:                            // ???
                    case 0x11AB:                            // ???
                    case 0x11AF:                            // ???
                        return Optional.of("ll");
                    case 0x11AE:                            // ???
                    case 0x11BA:                            // ???
                    case 0x11BD:                            // ???
                    case 0x11BE:                            // ???
                    case 0x11C0:                            // ???
                        return Optional.of("nn");
                    case 0x11B7:                            // ???
                    case 0x11B8:                            // ???
                        return Optional.of("mn");
                    case 0x11BC: return Optional.of("ngn"); // ???
                    default: return Optional.empty();
                }
            case 0x1106: // ???
                switch (previousEnding) {
                    case 0x11A8: return Optional.of("ngm"); // ???
                    case 0x11AE:                            // ???
                    case 0x11BA:                            // ???
                    case 0x11BD:                            // ???
                    case 0x11BE:                            // ???
                    case 0x11C0:                            // ???
                        return Optional.of("nm");
                    case 0x11B8: return Optional.of("mm");  // ???
                    default: return Optional.empty();
                }
            case 0x110B: // ???
                switch (previousEnding) {
                    case 0x11A8: return Optional.of("g");   // ???
                    case 0x11AE: return Optional.of("d");   // ???
                    case 0x11AF: return Optional.of("r");   // ???
                    case 0x11B8: return Optional.of("b");   // ???
                    case 0x11BA: return Optional.of("s");   // ???
                    case 0x11BC: return Optional.of("ng-"); // ???
                    case 0x11BD: return Optional.of("j");   // ???
                    case 0x11BE: return Optional.of("ch");  // ???
                    default: return Optional.empty();
                }
            case 0x110F: // ???
                switch (previousEnding) {
                    case 0x11A8: return Optional.of("k-k"); // ???
                    default: return Optional.empty();
                }
            case 0x1110: // ???
                switch (previousEnding) {
                    case 0x11AE:                            // ???
                    case 0x11BA:                            // ???
                    case 0x11BD:                            // ???
                    case 0x11BE:                            // ???
                    case 0x11C0:                            // ???
                        return Optional.of("t-t");
                    default: return Optional.empty();
                }
            case 0x1111: // ???
                switch (previousEnding) {
                    case 0x11B8: return Optional.of("p-p"); // ???
                    default: return Optional.empty();
                }
            default: return Optional.empty();
        }
    }

    // Decompose a syllable into several jamo. Returns its input if that isn't possible.
    public static char[] decompose(char syllable) {
        String normalized = Normalizer.normalize(String.valueOf(syllable), Normalizer.Form.NFD);
        return normalized.toCharArray();
    }

    // Transliterate any Hangul in the given string. Leaves any non-Hangul characters unmodified.
    @Override
    public String transliterate(String txt) {
        if (txt == null || txt.isEmpty()) {
            return txt;
        }

        // Most of the bulk of these loops is for handling special provisions - situations where the
        // last jamo of one syllable and the first of the next need to be romanized as a pair in an
        // irregular way.
        StringBuilder builder = new StringBuilder();
        boolean nextInitialJamoConsumed = false;
        char[] syllables = txt.toCharArray();
        for (int i = 0; i < syllables.length; i++) {
            char thisSyllable = syllables[i];
            // If this isn't in any of the Hangul blocks we know about, emit it as-is.
            if (!inRange(thisSyllable, JAMO_BLOCK_START, JAMO_BLOCK_END)
                    && !inRange(thisSyllable, SYLLABLES_BLOCK_START, SYLLABLES_BLOCK_END)
                    && !inRange(thisSyllable, COMPAT_JAMO_BLOCK_START, COMPAT_JAMO_BLOCK_END)) {
                builder.append(thisSyllable);
                continue;
            }

            char[] theseJamo = decompose(thisSyllable);
            for (int j = 0; j < theseJamo.length; j++) {
                char thisJamo = theseJamo[j];

                // If we already transliterated the first jamo of this syllable as part of a special
                // provision, skip it. Otherwise, handle it in the unconditional else branch.
                if (j == 0 && nextInitialJamoConsumed) {
                    nextInitialJamoConsumed = false;
                    continue;
                }

                // If this is the last jamo of this syllable and not the last syllable of the
                // string, check for special provisions. If the next char is whitespace or not
                // Hangul, it's the responsibility of transliterateSpecialProvisions() to return no
                // value.
                if (j == theseJamo.length - 1 && i < syllables.length - 1) {
                    char nextSyllable = syllables[i + 1];
                    char nextJamo = decompose(nextSyllable)[0];
                    Optional<String> specialProvision = transliterateSpecialProvisions(thisJamo, nextJamo);
                    if (specialProvision.isPresent()) {
                        builder.append(specialProvision.get());
                        nextInitialJamoConsumed = true;
                    } else {
                        // No special provision applies. Transliterate in isolation.
                        builder.append(transliterateSingleJamo(thisJamo));
                    }
                    continue;
                }

                // Everything else is transliterated in isolation.
                builder.append(transliterateSingleJamo(thisJamo));
            }
        }

        return builder.toString();
    }
}
