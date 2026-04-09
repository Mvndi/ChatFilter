package a4.papers.chatfilter.chatfilter.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public final class Tokenizer {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[^\\p{Alnum}]+");

    public TokenizeResult tokenize(String normalizedInput) {
        if (normalizedInput == null || normalizedInput.trim().isEmpty()) {
            return TokenizeResult.empty();
        }

        List<String> tokens = new ArrayList<>();
        List<List<String>> tokenGroups = new ArrayList<>();

        for (String whitespaceToken : WHITESPACE_PATTERN.split(normalizedInput.trim())) {
            if (whitespaceToken.isEmpty()) {
                continue;
            }

            List<String> group = new ArrayList<>();
            for (String punctuationToken : PUNCTUATION_PATTERN.split(whitespaceToken)) {
                if (!punctuationToken.isEmpty()) {
                    tokens.add(punctuationToken);
                    group.add(punctuationToken);
                }
            }

            if (!group.isEmpty()) {
                tokenGroups.add(group);
            }
        }

        return new TokenizeResult(Collections.unmodifiableList(tokens), tokenGroups);
    }
}
