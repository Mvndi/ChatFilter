package a4.papers.chatfilter.chatfilter.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class SwearFilterEngine {
    private static final int MAX_SPLIT_WINDOW_TOKENS = 3;
    private static final int MAX_SHORT_FRAGMENT_LENGTH = 2;

    private final Normalizer normalizer = new Normalizer();
    private final Tokenizer tokenizer = new Tokenizer();
    private final BoundaryChecker boundaryChecker = new BoundaryChecker();

    public List<String> findMatches(String normalizedInput, SwearWordRegistry swearWordRegistry) {
        if (swearWordRegistry == null || normalizedInput == null || normalizedInput.trim().isEmpty()) {
            return Collections.emptyList();
        }

        TokenizeResult tokenizeResult = tokenizer.tokenize(normalizedInput);
        LinkedHashSet<String> matches = new LinkedHashSet<>();

        addExactTokenMatches(matches, tokenizeResult.normalTokens(), swearWordRegistry);
        addControlledSplitExactMatches(matches, tokenizeResult.normalTokens(), swearWordRegistry);
        addControlledSplitMatches(matches, tokenizeResult.tokenGroups(), swearWordRegistry);
        addSingleTokenSubstringMatches(matches, tokenizeResult.normalTokens(), swearWordRegistry);

        return new ArrayList<>(matches);
    }

    private void addExactTokenMatches(Set<String> matches, List<String> tokens, SwearWordRegistry swearWordRegistry) {
        for (String token : tokens) {
            addExactComparableMatch(matches, token, swearWordRegistry);

            String squeezedToken = normalizer.squeezeRepeats(token);
            if (!squeezedToken.equals(token)) {
                addExactComparableMatch(matches, squeezedToken, swearWordRegistry);
            }
        }
    }

    private void addControlledSplitMatches(Set<String> matches, List<List<String>> tokenGroups, SwearWordRegistry swearWordRegistry) {
        if (tokenGroups == null || tokenGroups.isEmpty()) {
            return;
        }

        int maxLength = swearWordRegistry.getMaxComparableWordLength();
        if (maxLength <= 0) {
            return;
        }

        for (List<String> tokens : tokenGroups) {
            for (int start = 0; start < tokens.size(); start++) {
                StringBuilder builder = new StringBuilder();
                boolean hasShortFragment = false;

                for (int end = start; end < tokens.size() && end < start + MAX_SPLIT_WINDOW_TOKENS; end++) {
                    String token = tokens.get(end);
                    builder.append(token);
                    if (builder.length() > maxLength + 4) {
                        break;
                    }

                    if (token.length() <= MAX_SHORT_FRAGMENT_LENGTH) {
                        hasShortFragment = true;
                    }

                    if (end == start || !hasShortFragment) {
                        continue;
                    }

                    String candidate = builder.toString();
                    addExactComparableMatch(matches, candidate, swearWordRegistry);

                    String squeezedCandidate = normalizer.squeezeRepeats(candidate);
                    if (!squeezedCandidate.equals(candidate)) {
                        addExactComparableMatch(matches, squeezedCandidate, swearWordRegistry);
                    }

                    addBoundarySubstringMatch(matches, candidate, swearWordRegistry);
                    if (!squeezedCandidate.equals(candidate)) {
                        addBoundarySubstringMatch(matches, squeezedCandidate, swearWordRegistry);
                    }
                }
            }
        }
    }

    private void addControlledSplitExactMatches(Set<String> matches, List<String> tokens, SwearWordRegistry swearWordRegistry) {
        if (tokens == null || tokens.isEmpty()) {
            return;
        }

        int maxLength = swearWordRegistry.getMaxComparableWordLength();
        if (maxLength <= 0) {
            return;
        }

        for (int start = 0; start < tokens.size(); start++) {
            StringBuilder builder = new StringBuilder();
            boolean hasShortFragment = false;

            for (int end = start; end < tokens.size() && end < start + MAX_SPLIT_WINDOW_TOKENS; end++) {
                String token = tokens.get(end);
                builder.append(token);
                if (builder.length() > maxLength + 4) {
                    break;
                }

                if (token.length() <= MAX_SHORT_FRAGMENT_LENGTH) {
                    hasShortFragment = true;
                }

                if (end == start || !hasShortFragment) {
                    continue;
                }

                String candidate = builder.toString();
                addExactComparableMatch(matches, candidate, swearWordRegistry);
                addSplitAffixMatch(matches, candidate, swearWordRegistry);

                String squeezedCandidate = normalizer.squeezeRepeats(candidate);
                if (!squeezedCandidate.equals(candidate)) {
                    addExactComparableMatch(matches, squeezedCandidate, swearWordRegistry);
                    addSplitAffixMatch(matches, squeezedCandidate, swearWordRegistry);
                }
            }
        }
    }

    private void addSingleTokenSubstringMatches(Set<String> matches, List<String> tokens, SwearWordRegistry swearWordRegistry) {
        for (String token : tokens) {
            addBoundarySubstringMatch(matches, token, swearWordRegistry);

            String squeezedToken = normalizer.squeezeRepeats(token);
            if (!squeezedToken.equals(token)) {
                addBoundarySubstringMatch(matches, squeezedToken, swearWordRegistry);
            }
        }
    }

    private void addExactComparableMatch(Set<String> matches, String token, SwearWordRegistry swearWordRegistry) {
        String comparableToken = swearWordRegistry.toComparableToken(token);
        if (!comparableToken.isEmpty() && swearWordRegistry.contains(comparableToken)) {
            matches.add(comparableToken);
        }
    }

    private void addBoundarySubstringMatch(Set<String> matches, String token, SwearWordRegistry swearWordRegistry) {
        String comparableToken = swearWordRegistry.toComparableToken(token);
        if (comparableToken.isEmpty()) {
            return;
        }

        for (String bannedWord : swearWordRegistry.getExactMatchSet()) {
            if (comparableToken.length() <= bannedWord.length()) {
                continue;
            }
            if (!comparableToken.contains(bannedWord)) {
                continue;
            }
            if (boundaryChecker.check(comparableToken, bannedWord) == BoundaryResult.FLAG) {
                matches.add(bannedWord);
            }
        }
    }

    private void addSplitAffixMatch(Set<String> matches, String token, SwearWordRegistry swearWordRegistry) {
        String comparableToken = swearWordRegistry.toComparableToken(token);
        if (comparableToken.isEmpty()) {
            return;
        }

        for (String bannedWord : swearWordRegistry.getExactMatchSet()) {
            if (comparableToken.length() <= bannedWord.length()) {
                continue;
            }
            if (!comparableToken.contains(bannedWord)) {
                continue;
            }
            if (boundaryChecker.checkAffixOnly(comparableToken, bannedWord) == BoundaryResult.FLAG) {
                matches.add(bannedWord);
            }
        }
    }
}
