package a4.papers.chatfilter.chatfilter.shared;

import java.util.Collections;
import java.util.List;

public final class TokenizeResult {
    private static final TokenizeResult EMPTY = new TokenizeResult(Collections.emptyList());

    private final List<String> normalTokens;

    public TokenizeResult(List<String> normalTokens) {
        this.normalTokens = normalTokens;
    }

    public static TokenizeResult empty() {
        return EMPTY;
    }

    public List<String> normalTokens() {
        return normalTokens;
    }
}
