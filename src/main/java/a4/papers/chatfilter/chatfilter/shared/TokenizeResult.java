package a4.papers.chatfilter.chatfilter.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TokenizeResult {
    private static final TokenizeResult EMPTY = new TokenizeResult(Collections.emptyList(), Collections.emptyList());

    private final List<String> normalTokens;
    private final List<List<String>> tokenGroups;

    public TokenizeResult(List<String> normalTokens, List<List<String>> tokenGroups) {
        this.normalTokens = normalTokens;
        this.tokenGroups = freezeGroups(tokenGroups);
    }

    public static TokenizeResult empty() {
        return EMPTY;
    }

    public List<String> normalTokens() {
        return normalTokens;
    }

    public List<List<String>> tokenGroups() {
        return tokenGroups;
    }

    private List<List<String>> freezeGroups(List<List<String>> groups) {
        if (groups == null || groups.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<String>> frozenGroups = new ArrayList<>(groups.size());
        for (List<String> group : groups) {
            frozenGroups.add(Collections.unmodifiableList(new ArrayList<>(group)));
        }
        return Collections.unmodifiableList(frozenGroups);
    }
}
