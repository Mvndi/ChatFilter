package a4.papers.chatfilter.chatfilter.shared;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Homoglyphs {

    static final String[][] GROUPS = {
            {"A", "ΑАÀÁÂÃÄÅĀĂĄΔ"},
            {"a", "αаàáâãäåāăąδӑӓ"},
            {"B", "ΒВЬЪ"},
            {"b", "βьъ"},
            {"C", "СϹÇĆĈĊČ"},
            {"c", "сϲçćĉċč"},
            {"D", "ÐĎĐ"},
            {"d", "ðďđԁ"},
            {"E", "ΕЕЁÈÉÊËĒĔĖĘĚΞ"},
            {"e", "εеёèéêëēĕėęěϵӭ"},
            {"G", "ĜĞĠĢԌ"},
            {"g", "ĝğġģԍ"},
            {"H", "ΗНĤĦЊ"},
            {"h", "һĥħη"},
            {"I", "ΙІÌÍÎÏĨĪĬĮİӀ"},
            {"i", "ιіìíîïĩīĭįıӏ"},
            {"J", "ЈĴ"},
            {"j", "јϳĵ"},
            {"K", "ΚКĶ"},
            {"k", "κкķ"},
            {"L", "ĹĻĽĿŁ"},
            {"l", "ĺļľŀłӏ"},
            {"M", "ΜМ"},
            {"m", "μ"},
            {"N", "ΝŃŅŇ"},
            {"n", "ηñńņň"},
            {"O", "ΟОÒÓÔÕÖØŌŎŐϴ"},
            {"o", "οоòóôõöøōŏőӧӫ"},
            {"P", "ΡР"},
            {"p", "ρр"},
            {"Q", "Ԛ"},
            {"q", "ԛ"},
            {"R", "ŔŖŘ"},
            {"r", "ŕŗř"},
            {"S", "ŚŜŞŠ"},
            {"s", "śŝşš"},
            {"T", "ΤТŢŤŦ"},
            {"t", "τтţťŧ"},
            {"U", "ŨŪŬŮŰŲÙÚÛÜμ"},
            {"u", "υùúûüũūŭůűų"},
            {"V", "Ѵ"},
            {"v", "ν"},
            {"W", "ŴẀẂẄ"},
            {"w", "ŵẁẃẅ"},
            {"X", "ΧХ"},
            {"x", "χх"},
            {"Y", "ΥУÝŸŶΓҮӮӰ"},
            {"y", "уýÿŷγүӯӱ"},
            {"Z", "ΖŹŻŽ"},
            {"z", "ζźżž"},
            {"1", "١۱"},
            {"3", "Зз"},
            {"0", "Θθ"}
    };

    static final Map<Character, Character> MAP = buildMap();

    private Homoglyphs() {
    }

    private static Map<Character, Character> buildMap() {
        Map<Character, Character> map = new HashMap<>();
        for (String[] group : GROUPS) {
            char base = group[0].charAt(0);
            for (char c : group[1].toCharArray()) {
                map.put(c, base);
            }
        }
        return Collections.unmodifiableMap(map);
    }
}
