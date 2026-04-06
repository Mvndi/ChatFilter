package a4.papers.chatfilter.chatfilter.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class SwearWordRegistry {
    private final Normalizer normalizer = new Normalizer();
    private final Set<String> exactMatchSet = new HashSet<>();
    private final Map<String, FilterWrapper> filterByExactWord = new HashMap<>();
    private int maxComparableWordLength = 0;

    public void clear() {
        exactMatchSet.clear();
        filterByExactWord.clear();
        maxComparableWordLength = 0;
    }

    public void register(String word, FilterWrapper filterWrapper) {
        String comparableWord = toComparableToken(word);
        if (comparableWord.isEmpty()) {
            return;
        }

        exactMatchSet.add(comparableWord);
        if (!filterByExactWord.containsKey(comparableWord)) {
            filterByExactWord.put(comparableWord, filterWrapper);
        }
        maxComparableWordLength = Math.max(maxComparableWordLength, comparableWord.length());
    }

    public boolean contains(String comparableWord) {
        return exactMatchSet.contains(comparableWord);
    }

    public FilterWrapper getFilter(String comparableWord) {
        return filterByExactWord.get(comparableWord);
    }

    public int getMaxComparableWordLength() {
        return maxComparableWordLength;
    }

    public Set<String> getExactMatchSet() {
        return Collections.unmodifiableSet(exactMatchSet);
    }

    public String toComparableToken(String token) {
        if (token == null) {
            return "";
        }

        String normalizedToken = normalizer.normalize(token);
        if (normalizedToken.isEmpty()) {
            return "";
        }

        String comparableToken = stripNonAlphanumeric(normalizedToken);
        if (comparableToken.isEmpty()) {
            return "";
        }

        if (comparableToken.length() <= 4) {
            return normalizer.aggressiveNormalize(comparableToken);
        }

        return comparableToken;
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
}
