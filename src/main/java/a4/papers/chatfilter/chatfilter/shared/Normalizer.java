package a4.papers.chatfilter.chatfilter.shared;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class Normalizer {

    private static final Map<Character, Character> HOMOGLYPH_MAP = Homoglyphs.MAP;
    private static final Map<Character, Character> LEET_MAP = createLeetMap();
    private static final Set<Character> MINECRAFT_FORMATTING_CODES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f',
            'k', 'l', 'm', 'n', 'o', 'r',
            'x',
            'A', 'B', 'C', 'D', 'E', 'F',
            'K', 'L', 'M', 'N', 'O', 'R',
            'X'
    )));

    public String normalize(String input) {
        if (isBlank(input)) {
            return "";
        }

        String normalized = stripZeroWidthCharacters(input);
        normalized = stripFormattingCodes(normalized);
        normalized = mapCharacters(normalized, HOMOGLYPH_MAP);
        normalized = mapCharacters(normalized, LEET_MAP);
        normalized = collapseRepeats(normalized, 2);
        return normalized.toLowerCase(Locale.ROOT);
    }

    public String aggressiveNormalize(String token) {
        String normalized = normalize(token);
        if (isBlank(normalized)) {
            return normalized;
        }

        normalized = stripNonAlphanumeric(normalized);
        normalized = flattenVowels(normalized);
        return applyPhoneticMappings(normalized);
    }

    public String squeezeRepeats(String input) {
        if (isBlank(input)) {
            return "";
        }

        return collapseRepeats(input, 1);
    }

    private String stripZeroWidthCharacters(String input) {
        StringBuilder builder = new StringBuilder(input.length());

        for (int index = 0; index < input.length(); index++) {
            char current = input.charAt(index);
            if (Character.getType(current) == Character.FORMAT) {
                continue;
            }
            builder.append(current);
        }

        return builder.toString();
    }

    private String stripFormattingCodes(String input) {
        StringBuilder builder = new StringBuilder(input.length());

        for (int index = 0; index < input.length(); index++) {
            char current = input.charAt(index);
            if ((current == '&' || current == '§') && index + 1 < input.length()) {
                char next = input.charAt(index + 1);
                if (MINECRAFT_FORMATTING_CODES.contains(next)) {
                    index++;
                    continue;
                }
            }
            builder.append(current);
        }

        return builder.toString();
    }

    private String mapCharacters(String input, Map<Character, Character> replacementMap) {
        StringBuilder builder = new StringBuilder(input.length());

        for (int index = 0; index < input.length(); index++) {
            char current = input.charAt(index);
            builder.append(replacementMap.getOrDefault(current, current));
        }

        return builder.toString();
    }

    private String collapseRepeats(String input, int maxRunLength) {
        StringBuilder builder = new StringBuilder(input.length());
        char previous = 0;
        int runLength = 0;

        for (int index = 0; index < input.length(); index++) {
            char current = input.charAt(index);
            if (current == previous) {
                runLength++;
            } else {
                previous = current;
                runLength = 1;
            }

            if (runLength <= maxRunLength) {
                builder.append(current);
            }
        }

        return builder.toString();
    }

    private String stripNonAlphanumeric(String input) {
        StringBuilder builder = new StringBuilder(input.length());

        for (int index = 0; index < input.length(); index++) {
            char current = input.charAt(index);
            if (Character.isLetterOrDigit(current)) {
                builder.append(current);
            }
        }

        return builder.toString();
    }

    private String flattenVowels(String input) {
        String flattened = input
                .replace("ee", "e")
                .replace("oo", "o")
                .replace("ou", "u");

        if (!containsStandardVowel(flattened)) {
            flattened = flattened.replace('y', 'i');
        }

        return flattened;
    }

    private String applyPhoneticMappings(String input) {
        return input
                .replace("ck", "k")
                .replace("ph", "f")
                .replace("gh", "g")
                .replace("nn", "n")
                .replace("tt", "t")
                .replace("gg", "g")
                .replace("vv", "w")
                .replace("ii", "i")
                .replace("qu", "k");
    }

    private boolean containsStandardVowel(String input) {
        for (int index = 0; index < input.length(); index++) {
            char current = input.charAt(index);
            if (current == 'a' || current == 'e' || current == 'i' || current == 'o' || current == 'u') {
                return true;
            }
        }

        return false;
    }

    private boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }

    private static Map<Character, Character> createLeetMap() {
        Map<Character, Character> map = new HashMap<>();

        map.put('0', 'o');
        map.put('1', 'i');
        map.put('2', 'z');
        map.put('3', 'e');
        map.put('4', 'a');
        map.put('5', 's');
        map.put('6', 'g');
        map.put('7', 't');
        map.put('8', 'b');
        map.put('9', 'g');
        map.put('@', 'a');
        map.put('$', 's');
        map.put('!', 'i');
        map.put('+', 't');
        map.put('|', 'i');

        return Collections.unmodifiableMap(new HashMap<>(map));
    }
}
