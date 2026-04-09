package a4.papers.chatfilter.chatfilter.shared;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class BoundaryChecker {
    private static final Set<String> COMMON_PREFIXES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "anti", "de", "dis", "extra", "hyper", "inter", "mis",
            "non", "post", "pre", "pro", "pseudo", "re", "semi",
            "sub", "super", "trans", "ultra", "un"
    )));

    private static final Set<String> COMMON_SUFFIXES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "able", "ed", "er", "ers", "es", "ful", "hood", "ian",
            "ing", "ish", "ism", "ist", "ists", "less", "ly",
            "ness", "s", "y", "ies"
    )));

    public BoundaryResult check(String token, String bannedSubstring) {
        if (isBlank(token) || isBlank(bannedSubstring)) {
            return BoundaryResult.PASS;
        }

        int startIndex = token.indexOf(bannedSubstring);
        while (startIndex >= 0) {
            int endIndex = startIndex + bannedSubstring.length();
            String prefix = token.substring(0, startIndex);
            String suffix = token.substring(endIndex);

            if (startIndex == 0 || endIndex == token.length()) {
                return BoundaryResult.FLAG;
            }

            if (COMMON_PREFIXES.contains(prefix) || COMMON_SUFFIXES.contains(suffix)) {
                return BoundaryResult.FLAG;
            }

            if (prefix.length() <= 2 && suffix.length() <= 2) {
                return BoundaryResult.FLAG;
            }

            startIndex = token.indexOf(bannedSubstring, startIndex + 1);
        }

        return BoundaryResult.PASS;
    }

    public BoundaryResult checkAffixOnly(String token, String bannedSubstring) {
        if (isBlank(token) || isBlank(bannedSubstring)) {
            return BoundaryResult.PASS;
        }

        int startIndex = token.indexOf(bannedSubstring);
        while (startIndex >= 0) {
            int endIndex = startIndex + bannedSubstring.length();
            String prefix = token.substring(0, startIndex);
            String suffix = token.substring(endIndex);

            boolean prefixAffixMatch = endIndex == token.length()
                    && (prefix.isEmpty() || COMMON_PREFIXES.contains(prefix));
            boolean suffixAffixMatch = startIndex == 0
                    && (suffix.isEmpty() || COMMON_SUFFIXES.contains(suffix));

            if (prefixAffixMatch || suffixAffixMatch) {
                return BoundaryResult.FLAG;
            }

            startIndex = token.indexOf(bannedSubstring, startIndex + 1);
        }

        return BoundaryResult.PASS;
    }

    private boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }
}
